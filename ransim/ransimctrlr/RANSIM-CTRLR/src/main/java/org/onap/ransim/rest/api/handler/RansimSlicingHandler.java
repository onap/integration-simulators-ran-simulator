/*-
 }* ============LICENSE_START=======================================================
 * Ran Simulator Controller
 * ================================================================================
 * Copyright (C) 2020 Wipro Limited.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.ransim.rest.api.handler;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.websocket.Session;

import org.apache.log4j.Logger;
import org.onap.ransim.rest.api.services.RANSliceConfigService;
import org.onap.ransim.rest.api.services.RansimControllerServices;
import org.onap.ransim.rest.api.services.RansimRepositoryService;
import org.onap.ransim.rest.web.mapper.GNBCUCPModel;
import org.onap.ransim.rest.web.mapper.NRCellCUModel;
import org.onap.ransim.rest.web.mapper.PLMNInfoModel;
import org.onap.ransim.rest.xml.models.*;
import org.onap.ransim.websocket.server.RansimWebSocketServer;
import org.onap.ransim.websocket.model.SlicingPmMessage;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class RansimSlicingHandler {
	
	static Logger log = Logger.getLogger(RansimSlicingHandler.class
			.getName());
			
	@Autowired
	RansimRepositoryService ransimRepo ;
	
	@Autowired
	RansimControllerServices rscServices;
	
	@Autowired
	RANSliceConfigService ranSliceConfigService;


   public List<String> generateIntelligentSlicingPmData(long startTime, HashMap<String,List<String>> taCells) {
		  List<String> result = new ArrayList<String>();
		  try {
		    String requestUrl = "http://" + "localhost" + ":" + "8081" + "/ransim/api/ransim-db/v4/cucp-list";
		    List<GNBCUCPModel> gnbcucpModelList = sendGetRequestToConfigDb(requestUrl).getBody();
		    long peakEndTime = System.currentTimeMillis() + 360000;
		    for (int i = 0; i < gnbcucpModelList.size(); i++) {
		                                String gNBName = gnbcucpModelList.get(i).getgNBCUName();                              
		                                LocalDateTime beginTime = LocalDateTime.now();
		                                String beginTimeString = beginTime.toString();
						SlicingPmMessage pmMessage = new SlicingPmMessage();
                                                pmMessage.setStartEpochMicrosec(System.currentTimeMillis() * 1000);
                                                pmMessage.setSourceName(gNBName);
		                                MeasCollec measCollec = new MeasCollec(beginTimeString);
		                                FileSender fileSender = new FileSender(gNBName);
		                                FileHeader fileHeader = new FileHeader("Prefix", "Acme Ltd", "32.435 V10.0", measCollec, fileSender);
		                                Random r = new Random();
		                                int jobId = r.nextInt((9999 - 1000) + 1) + 1000;
		                                Job job = new Job(String.valueOf(jobId));
		                                ReportingPeriod reportingPeriod = new ReportingPeriod("PT900S");
		                                List<MeasType> measTypeList = new ArrayList<MeasType>();
		                                int numberOfNssaiType = 1;
		                                HashMap<String, Integer> nssaiInfoMap = new HashMap<String, Integer>();
		                                for (NRCellCUModel nRCellCUModel : gnbcucpModelList.get(i).getCellCUList()) {
		                                    for (PLMNInfoModel pLMNInfoModel : nRCellCUModel.getpLMNInfoList()) {
		                                        if (pLMNInfoModel.getsNSSAI().getStatus().equalsIgnoreCase("active")) {
		                                                        nssaiInfoMap.put(pLMNInfoModel.getsNSSAI().getsNSSAI(),
		                                                        pLMNInfoModel.getsNSSAI().getConfigData().getMaxNumberOfConns());
		                                                }
		                                    }
		                                }
		                                HashMap<String, Integer> nSSAINo = new HashMap<String, Integer>();
		                                for (java.util.Map.Entry<String, Integer> map : nssaiInfoMap.entrySet()) {   
		                                        String nssai = map.getKey();
		                                        MeasType mesType1 = new MeasType("SM.PDUSessionSetupReq." + nssai, numberOfNssaiType++);
		                                        MeasType mesType2 = new MeasType("SM.PDUSessionSetupSucc." + nssai, numberOfNssaiType++);
		                                         measTypeList.add(mesType1);
		                                        measTypeList.add(mesType2);
		                                        if (numberOfNssaiType == 3) {
		                                                MeasType mesTypeFail = new MeasType("SM.PDUSessionSetupFail." + "0", 3);
		                                                measTypeList.add(mesTypeFail);
		                                                numberOfNssaiType++;
		                                        }
		                                        nSSAINo.put(nssai,mesType1.getP());
		                                }
		                                
		                                List<MeasValue> measValueList = new ArrayList<MeasValue>();
		                                
		                                for (NRCellCUModel nRCellCUModel : gnbcucpModelList.get(i).getCellCUList()) {
		                                        List<Result> resultList = new ArrayList<Result>();
		                                        int noOfRequestedSessions = 0;
		                                        int successfulSessions = 0;
		                                        int failedSessions = 0;
		                                        int configData = 0;
		                                        double rNumForTotalReq = 0;
                                                int numberOfNssaiValue = 1; //change
		                                        for (PLMNInfoModel pLMNInfoModel : nRCellCUModel.getpLMNInfoList()) {
		                                         for (java.util.Map.Entry<String, Integer> nssaiMap :  nSSAINo.entrySet()) {
		                                                if(nssaiMap.getKey().equals(pLMNInfoModel.getsNSSAI().getsNSSAI())) {
		                                                configData = pLMNInfoModel.getsNSSAI().getConfigData().getMaxNumberOfConns();
		                                                noOfRequestedSessions = 0;
		                                                successfulSessions = 0;
		                                                for (java.util.Map.Entry<String, List<String>> taCellMap : taCells.entrySet()) {
		                                                      for(String cell : taCellMap.getValue()) {
                                                                        if(Integer.parseInt(cell) == nRCellCUModel.getCellLocalId().intValue()) {
                                                                           long end = (System.currentTimeMillis());
		                                                                    if(peakEndTime>end) {
		                                                                       if(taCellMap.getKey().equalsIgnoreCase("TA1") || taCellMap.getKey().equalsIgnoreCase("TA3")) {
		                                                                             rNumForTotalReq = 1.4 + (new Random().nextDouble() * (1.0 - 1.4));
		                                                                            } else  {
		                                                                            	rNumForTotalReq = 0.6 + (new Random().nextDouble() * (0.3 - 0.6));
		                                                                            	}    
		                                                                       }
		                                                                    else {
		                                                                    	rNumForTotalReq = 0.7 + (new Random().nextDouble() * (0.3 - 0.7));
		                                                                    	}
		                                                                    }
                                                                        }
		                                                      }

		                                                double rNumForFs = 0.10 + (new Random().nextDouble() * (0.05 - 0.10));
		                                                double rNumForSs = 0.75 + (new Random().nextDouble()* (0.75 - 0.90));
		                                                noOfRequestedSessions = (int) (configData * rNumForTotalReq);
		                                                successfulSessions = (int) (noOfRequestedSessions * rNumForSs);
		                                                failedSessions += noOfRequestedSessions - successfulSessions;
		                                                numberOfNssaiValue = nssaiMap.getValue();
		                                                Result result1 = new Result(numberOfNssaiValue++, noOfRequestedSessions);
		                                                Result result2 = new Result(numberOfNssaiValue++, successfulSessions);
		                                                resultList.add(result1);
		                                                resultList.add(result2);
		                                                if (numberOfNssaiValue == 3) {
	                                                        numberOfNssaiValue++;
	                                                    }
		                                                if(nRCellCUModel.getpLMNInfoList().indexOf(pLMNInfoModel)+1 == nRCellCUModel.getpLMNInfoList().size())
		                                                {                                       
		                                                failedSessions -= failedSessions * rNumForFs;
		                                                Result result3 = new Result(3, failedSessions);
		                                                resultList.add(result3);  
		                                                }		                                               
		                                        }
		                                    }
		                                        
		                                 }

		                                        MeasValue measValue = new MeasValue(nRCellCUModel.getCellLocalId(),
		                                                        resultList, false);
		                                        measValueList.add(measValue);
		                                }
		                           
		                                ManagedElement managedElement = new ManagedElement("r0.1", gNBName);
		                                LocalDateTime grabularityEndTime = LocalDateTime.now();
		                                String grabularityEndTimeString = grabularityEndTime.toString();
		                                GranularityPeriod granularityPeriod = new GranularityPeriod(grabularityEndTimeString, "PT900S");
		                                MeasInfo measInfo = new MeasInfo("measInfoIsVal", job, granularityPeriod, reportingPeriod, measTypeList,
		                                                measValueList);
		                                List<MeasInfo> measInfoList = new ArrayList<MeasInfo>();
		                                measInfoList.add(measInfo);
		                                MeasData measData = new MeasData(managedElement, measInfoList);
		                                List<MeasData> measDataList = new ArrayList<MeasData>();
		                                measDataList.add(measData);
		                                LocalDateTime endTime = LocalDateTime.now();
		                                String endTimeString = endTime.toString();
	                                        pmMessage.setLastEpochMicrosec(System.currentTimeMillis() * 1000);
		                                MeasCollecEnd measCollecEnd = new MeasCollecEnd(endTimeString);
		                                FileFooter fileFooter = new FileFooter(measCollecEnd);
		                                MeasCollecFile measCollecFile = new MeasCollecFile(fileHeader, measDataList, fileFooter,
		                                                "http://www.3gpp.org/ftp/specs/archive/32_series/32.435#measCollec");		                             
		                                String startDate = beginTimeString.replace(':', '-');
		                                String endDate = endTimeString.replace(':', '-');
		                                String fileName = "A" + startDate + "-"+ endDate + "-" + String.valueOf(jobId) + "-" + gNBName+ ".xml";
		                                pmMessage.setFileName(fileName);
		                                Gson gson = new Gson();
		                                String pmData = gson.toJson(measCollecFile);
		                                pmMessage.setPmData(pmData);
		                                sendIntelligentSlicingPmData(pmMessage);
		                                }
		         }
		         catch (Exception e) {
		        	 System.out.println("Exception: " + e);
		         }
		     return result;
}

public static <T> ResponseEntity<List<GNBCUCPModel>> sendGetRequestToConfigDb(String requestUrl) {
    
          HttpHeaders headers = new HttpHeaders();
          log.info("sending...");
          headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
          HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
         try {
             RestTemplate restTemplate = new RestTemplate();
             return restTemplate.exchange(requestUrl, HttpMethod.GET, requestEntity,
             new ParameterizedTypeReference<List<GNBCUCPModel>>() {
           });
       }
       catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
}


public void sendIntelligentSlicingPmData( SlicingPmMessage pmMessage) {

		log.info("inside sendIntelligentSlicingPmData");
		
		Gson gson = new Gson();
		String jsonStr = gson.toJson(pmMessage);

		log.info("IntelligentSlicingPmData " + jsonStr);

		String ipPort = RansimControllerServices.serverIdIpPortMapping.get(pmMessage.getSourceName());

		if (ipPort != null && !ipPort.trim().equals("")) {

			log.info("Connection estabilished with ip: " + ipPort);
			if (ipPort != null && !ipPort.trim().equals("")) {
				Session clSess = RansimControllerServices.webSocketSessions.get(ipPort);
				if (clSess != null) {
					log.info("PM Data message sent.");
					RansimWebSocketServer.sendIntelligentSlicingPmData(jsonStr, clSess);
				} else {
					log.info("No client session for " + ipPort);
				}
			} else {
				log.info("No client for this serverId");
			}
		} else {
			log.info("No client for ");
		}
     
	}
}

