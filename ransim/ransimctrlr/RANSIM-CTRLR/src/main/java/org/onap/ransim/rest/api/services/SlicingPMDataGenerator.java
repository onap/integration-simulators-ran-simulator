/*-
 * ============LICENSE_START=======================================================
 * Ran Simulator Controller
 * ================================================================================
 * Copyright (C) 2021 Wipro Limited.
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

package org.onap.ransim.rest.api.services;
import java.util.Collection;
import java.util.Set;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.Session;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.onap.ransim.rest.api.exceptions.RansimException;
import org.onap.ransim.rest.api.models.NSSAIConfig;
import org.onap.ransim.rest.web.mapper.GNBDUModel;
import org.onap.ransim.rest.web.mapper.NRCellDUModel;
import org.onap.ransim.rest.web.mapper.NSSAIData;
import org.onap.ransim.rest.web.mapper.PLMNInfoModel;
import org.onap.ransim.rest.xml.models.FileFooter;
import org.onap.ransim.rest.xml.models.FileHeader;
import org.onap.ransim.rest.xml.models.FileSender;
import org.onap.ransim.rest.xml.models.GranularityPeriod;
import org.onap.ransim.rest.xml.models.Job;
import org.onap.ransim.rest.xml.models.ManagedElement;
import org.onap.ransim.rest.xml.models.MeasCollec;
import org.onap.ransim.rest.xml.models.MeasCollecEnd;
import org.onap.ransim.rest.xml.models.MeasCollecFile;
import org.onap.ransim.rest.xml.models.MeasData;
import org.onap.ransim.rest.xml.models.MeasInfo;
import org.onap.ransim.rest.xml.models.MeasType;
import org.onap.ransim.rest.xml.models.MeasValue;
import org.onap.ransim.rest.xml.models.ReportingPeriod;
import org.onap.ransim.rest.xml.models.Result;
import org.onap.ransim.websocket.model.SlicingPmMessage;
import org.onap.ransim.websocket.server.RansimWebSocketServer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

@Service
public class SlicingPMDataGenerator {

	static Logger logger = Logger.getLogger(SlicingPMDataGenerator.class.getName());

	@Autowired
	RANSliceConfigService ranSliceConfigService;

	double ricId11MaxVariation = 0.8;
	double ricId22MaxVariation = 0.2;

	/**
	 * Generates closed loop PM data for all DU functions Generates PRBs for DUs in
	 * the ratio of 80:20 for the same nssai in different RICs
	 * 
	 * @throws RansimException
	 */
	public void generateClosedLoopPmData(long startTime) {
		try {
			String requestUrl = "http://" + "localhost" + ":" + "8081" + "/ransim/api/ransim-db/v4/du-list";
			List<GNBDUModel> duList = sendGetRequestToransimDb(requestUrl).getBody();
			for (GNBDUModel du : duList) {
				logger.info("Generating PM data for DU :  " + du.getgNBDUName() + " Id :" + du.getgNBDUId());
				Map<String, NSSAIConfig> activeNssaiDetails = new HashMap<String, NSSAIConfig>();
				List<NRCellDUModel> duCellList = du.getCellDUList();
				int ricId = du.getNearRTRICId();
				List<PLMNInfoModel> plmnInfoList = new ArrayList<>();
				duCellList.forEach(cell -> plmnInfoList.addAll(cell.getpLMNInfoList()));
				List<NSSAIData> nssaiData = new ArrayList<>();
				plmnInfoList.forEach(plmnInfo -> nssaiData.add(plmnInfo.getsNSSAI()));
				nssaiData.stream().filter(nssai -> nssai.getStatus().equalsIgnoreCase("active"))
						.forEach(x -> activeNssaiDetails.put(x.getsNSSAI(), x.getConfigData()));
				produceMeasurementCollectionFile(du, activeNssaiDetails, duCellList, ricId);
				logger.info("PM data generated for DU : " + du.getgNBDUName() + " Id: " + du.getgNBDUId());
			}
		} catch (RansimException e) {
			logger.debug("ERROR in closed lopp PM data generation : ");
			logger.debug(e);
		} catch (Exception exp) {
			logger.debug(exp);
		}
	}

	private void produceMeasurementCollectionFile(GNBDUModel du, Map<String, NSSAIConfig> activeNssaiDetails,
			List<NRCellDUModel> duCellList, int ricId) throws RansimException {
		logger.debug("produceing MeasurementCollectionFile ");
		SlicingPmMessage pmMessage = new SlicingPmMessage();
		pmMessage.setStartEpochMicrosec(System.currentTimeMillis() * 1000);
		pmMessage.setSourceName(du.getgNBDUName());
		LocalDateTime beginTime = LocalDateTime.now();
		String beginTimeString = beginTime.toString();
		MeasCollec measCollec = new MeasCollec(beginTimeString);
		FileSender fileSender = new FileSender(du.getgNBDUName());
		FileHeader fileHeader = new FileHeader("Prefix", "Acme Ltd", "32.435 V10.0", measCollec, fileSender);
		Random r = new Random();
		int jobId = r.nextInt((9999 - 1000) + 1) + 1000;
		Job job = new Job(String.valueOf(jobId));
		ReportingPeriod reportingPeriod = new ReportingPeriod("PT900S");

		List<MeasType> measTypeList = setMeasurementTypes(activeNssaiDetails);
		List<MeasValue> measValueList = setMeasurementValues(duCellList, getMaxVariation(ricId), measTypeList);

		ManagedElement managedElement = new ManagedElement("r0.1", du.getgNBDUName());
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

		try {
			String startDate = beginTimeString.replace(':', '-');
			String endDate = endTimeString.replace(':', '-');
			String fileName = "A" + startDate + "-" + endDate + "-" + String.valueOf(jobId) + "-"
					+ du.getgNBDUName() + ".xml";
			pmMessage.setFileName(fileName);
			Gson gson = new Gson();
			String pmData = gson.toJson(measCollecFile);
			pmMessage.setPmData(pmData);
			closedLoopPmData(pmMessage);
		} catch (Exception exp) {
			logger.debug(exp);
		}

	}

	private List<MeasValue> setMeasurementValues(List<NRCellDUModel> duCellList, double ricIdVariation,
			List<MeasType> measTypeList) {
		logger.debug("setting MeasurementValues");
		List<MeasValue> measValueList = new ArrayList<MeasValue>();
		duCellList.forEach(cell -> {
			AtomicInteger pValue = new AtomicInteger(1);
			List<Result> resultList = new ArrayList<>();
			int prbs = cell.getPrbs();
			measTypeList.forEach(meas -> {
				int prbsUsed = (int) (Math.random() * ricIdVariation * prbs);
				Result result = new Result(pValue.getAndIncrement(), prbsUsed);
				resultList.add(result);
			});
			MeasValue measValue = new MeasValue(cell.getCellLocalId(), resultList, false);
			measValueList.add(measValue);

		});
		return measValueList;
	}

	private double getMaxVariation(int ricId) throws RansimException {
		switch (ricId) {
		case 11:
			// ricId11MaxVariation =
			// Double.parseDouble(System.getProperty("RIC_11_VARIATION"));
			return ricId11MaxVariation;
		case 22:
			return ricId22MaxVariation;
		default:
			throw new RansimException("Invalid RIC ID : valid Ids : [11,22] ");
		}
	}

	private List<MeasType> setMeasurementTypes(Map<String, NSSAIConfig> activeNssaiDetails) {
		logger.debug("setting MeasurementTypes");
		List<MeasType> measTypeList = new ArrayList<MeasType>();
		AtomicInteger pValue = new AtomicInteger(1);
		activeNssaiDetails.forEach((nssai, configData) -> {
			MeasType mesType1 = new MeasType("SM.PrbUsedDl." + nssai, pValue.getAndIncrement());
			MeasType mesType2 = new MeasType("SM.PrbUsedUl." + nssai, pValue.getAndIncrement());
			measTypeList.add(mesType1);
			measTypeList.add(mesType2);
		});
		return measTypeList;
	}

	public static <T> ResponseEntity<List<GNBDUModel>> sendGetRequestToransimDb(String requestUrl) {
		HttpHeaders headers = new HttpHeaders();
		logger.info("sending request to ransimdb : " + requestUrl);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
		try {
			RestTemplate restTemplate = new RestTemplate();
			return restTemplate.exchange(requestUrl, HttpMethod.GET, requestEntity,
					new ParameterizedTypeReference<List<GNBDUModel>>() {
					});
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	public void closedLoopPmData(SlicingPmMessage pmMessage) {
		Gson gson = new Gson();
		String jsonStr = gson.toJson(pmMessage);
		logger.info("ClosedPmData " + jsonStr);
		String ipPort = RansimControllerServices.serverIdIpPortMapping.get(pmMessage.getSourceName());
		if (ipPort != null && !ipPort.trim().equals("")) {
			logger.info("Connection estabilished with ip: " + ipPort);
			if (ipPort != null && !ipPort.trim().equals("")) {
				Session clSess = RansimControllerServices.webSocketSessions.get(ipPort);
				if (clSess != null) {
					logger.info("PM Data message sent.");
					RansimWebSocketServer.sendIntelligentSlicingPmData(jsonStr, clSess);
				} else {
					logger.info("No client session for " + ipPort);
				}
			} else {
				logger.info("No client for this serverId");
			}
		} else {
			logger.info("No client for ");
		}
	}
}

