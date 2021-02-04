package org.onap.ransim.rest.api.services;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import javax.xml.bind.annotation.XmlRootElement;
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
	public void generateClosedLoopPmData(long startTime)  {
	try{
		String requestUrl = "http://" + "localhost" + ":" + "8081" + "/ransim/api/ransim-db/v4/du-list";
		List<GNBDUModel> duList = sendGetRequestToransimDb(requestUrl).getBody();;
NSSAIConfig  nSSAIConfig  = new NSSAIConfig();
nSSAIConfig.setdLThptPerSlice(27);
nSSAIConfig.setuLThptPerSlice(30);
nSSAIConfig.setMaxNumberOfConns(3000);

NSSAIData nSSAIData = new NSSAIData();
nSSAIData.setsNSSAI("001-010000");
nSSAIData.setStatus("ACTIVE");
nSSAIData.setGlobalSubscriberId("Customer-001");
nSSAIData.setSubscriptionServiceType("Premium");
nSSAIData.setConfigData(nSSAIConfig);
NSSAIData nSSAIData2 = new NSSAIData();
nSSAIData2.setsNSSAI("001-010001");
nSSAIData2.setStatus("ACTIVE");
nSSAIData2.setGlobalSubscriberId("Customer-001");
nSSAIData2.setSubscriptionServiceType("Premium");
nSSAIData2.setConfigData(nSSAIConfig);

PLMNInfoModel pLMNInfoModel1 = new PLMNInfoModel();
pLMNInfoModel1.setpLMNId("310-410 ");
pLMNInfoModel1.setsNSSAI(nSSAIData);
PLMNInfoModel pLMNInfoModel2 = new PLMNInfoModel();
pLMNInfoModel2.setpLMNId("310-411 ");
pLMNInfoModel2.setsNSSAI(nSSAIData2);

List<PLMNInfoModel> myPLMNInfoModelList = new ArrayList<PLMNInfoModel>();
myPLMNInfoModelList.add(pLMNInfoModel1);
myPLMNInfoModelList.add(pLMNInfoModel2);


 for (GNBDUModel du : duList) {
    List<NRCellDUModel> duCellList = du.getCellDUList();
    for (NRCellDUModel cell : duCellList ) {
    cell.setpLMNInfoList(myPLMNInfoModelList);
}
}

duList.forEach(x->logger.debug("DU : " + x));
		for (GNBDUModel du : duList) {
			logger.info("Generating PM data for DU :  " + du.getgNBDUName()+ " Id :" + du.getgNBDUId());

   Map<String, Integer > myactiveNssai = new HashMap<>();			
Map<String, NSSAIConfig> activeNssaiDetails = new HashMap<String, NSSAIConfig>();
			List<NRCellDUModel> duCellList = du.getCellDUList();
logger.debug("duCellList.size : " + duCellList.size());
			int ricId = du.getNearRTRICId();
logger.debug("RIC ID : " + ricId);
			List<PLMNInfoModel> plmnInfoList = new ArrayList<>();
			duCellList.forEach(cell -> plmnInfoList.addAll(cell.getpLMNInfoList()));
			List<NSSAIData> nssaiData = new ArrayList<>();
			plmnInfoList.forEach(plmnInfo -> nssaiData.add(plmnInfo.getsNSSAI()));
			nssaiData.stream().filter(nssai -> nssai.getStatus().equalsIgnoreCase("active"))
					.forEach(x -> activeNssaiDetails.put(x.getsNSSAI(), x.getConfigData()));
logger.debug("Goin to produceMeasurementCollectionFile");

                         for (NRCellDUModel cell : duCellList ) {
                        nssaiData.stream().filter(nssai -> nssai.getStatus().equalsIgnoreCase("active"))
                                        .forEach(x -> myactiveNssai.put(x.getsNSSAI(), cell.getCellLocalId()));
                    }   
logger.debug("myactiveNssai.size : " + myactiveNssai.size());
			produceMeasurementCollectionFile(du, activeNssaiDetails, duCellList, ricId);
			logger.info("PM data generated for DU : "+ du.getgNBDUName() + " Id: " + du.getgNBDUId());
		}
}catch(RansimException e){
	logger.debug("ERROR in closed lopp PM data generation : ");
logger.debug(e);
 } catch(Exception er){
logger.debug(er);
	}
}
	private void produceMeasurementCollectionFile(GNBDUModel du, Map<String, NSSAIConfig> activeNssaiDetails,
			List<NRCellDUModel> duCellList, int ricId) throws RansimException {
logger.debug("produceMeasurementCollectionFile");
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
		List<MeasValue> measValueList = setMeasurementValues(duCellList,getMaxVariation(ricId));
		
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
		MeasCollecEnd measCollecEnd = new MeasCollecEnd(endTimeString);
		FileFooter fileFooter = new FileFooter(measCollecEnd);
		MeasCollecFile measCollecFile = new MeasCollecFile(fileHeader, measDataList, fileFooter,
				"http://www.3gpp.org/ftp/specs/archive/32_series/32.435#measCollec");

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(MeasCollecFile.class);
			Marshaller marshaller = jaxbContext.createMarshaller();

                        marshaller.setProperty("jaxb.encoding", "UTF-8");   
                     marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
    XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(
        baos, (String) marshaller.getProperty(Marshaller.JAXB_ENCODING));
    xmlStreamWriter.writeStartDocument(
        (String) marshaller.getProperty(Marshaller.JAXB_ENCODING), "1.0");
   
   
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
             marshaller.setProperty("com.sun.xml.bind.xmlHeaders",
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); 
			String startDate = beginTimeString.replace(':', '-');
			String endDate = endTimeString.replace(':', '-');
			String fileName = "./" + "A" + startDate + "-" + endDate + "-" + String.valueOf(jobId) + "-" + du.getgNBDUName()
					+ ".xml";
logger.debug("Craeting file");
			marshaller.marshal(measCollecFile, new File("/tmp/ransim-install/ClosedLoopData/" + fileName));
			marshaller.marshal(measCollecFile, System.out);
                         marshaller.marshal(measCollecFile, xmlStreamWriter);
		} catch (JAXBException e) {
			throw new RansimException(e);
		}
catch(Exception er){
logger.debug(er);
        }

		logger.info("measCollec: " + measCollecFile.toString());
	}

	private List<MeasValue> setMeasurementValues(List<NRCellDUModel> duCellList, double ricIdVariation) {
logger.debug("setMeasurementValues");
		List<MeasValue> measValueList = new ArrayList<MeasValue>();
		duCellList.forEach(cell -> {
			int pvalue = 1;
			int prbs = cell.getPrbs();
			int prbsUsedDl = (int) (Math.random() * ricIdVariation * prbs);
			int prbsUsedUl = (int) (Math.random() * ricIdVariation * prbs);
                        int prbsUsedDl1 = (int) (Math.random() * ricIdVariation * prbs);
                        int prbsUsedUl2 = (int) (Math.random() * ricIdVariation * prbs);
			List<Result> resultList = new ArrayList<>();
			Result result1 = new Result(pvalue++, prbsUsedDl);
			Result result2 = new Result(pvalue++, prbsUsedUl);
                        Result result3 = new Result(pvalue++, prbsUsedDl1);
                        Result result4 = new Result(pvalue++, prbsUsedUl2);

                        resultList.add(result1);
                        resultList.add(result2);
                        resultList.add(result3);
                        resultList.add(result4);
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
}

