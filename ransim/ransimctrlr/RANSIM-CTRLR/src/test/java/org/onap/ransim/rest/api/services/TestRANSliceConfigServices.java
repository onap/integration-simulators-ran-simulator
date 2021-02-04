package org.onap.ransim.rest.api.services;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.ransim.rest.api.models.NSSAIConfig;
import org.onap.ransim.rest.api.services.RANSliceConfigService;
import org.onap.ransim.rest.web.mapper.GNBCUCPModel;
import org.onap.ransim.rest.web.mapper.GNBCUUPModel;
import org.onap.ransim.rest.web.mapper.GNBDUModel;
import org.onap.ransim.rest.web.mapper.NRCellCUModel;
import org.onap.ransim.rest.web.mapper.NRCellDUModel;
import org.onap.ransim.rest.web.mapper.NearRTRICModel;
import org.onap.ransim.rest.web.mapper.RANSliceInfoModel;
import org.springframework.context.annotation.PropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
@PropertySource("classpath:ransim.properties")
public class TestRANSliceConfigServices {

	@Mock
	RANSliceConfigService rANSliceConfigService;
	
    ObjectMapper objectMapper = new ObjectMapper();

	@Test
    public void testSaveGNBCUCP() {
            String input = "{\"gNBCUName\":\"cucpserver1\",\"gNBId\":98763,\"gNBIdLength\":5,\"pLMNId\":\"310-410\",\"nFType\":\"CUCP\",\"nearRTRICId\":11,\"cellCUList\":[{\"cellLocalId\":103594000,\"pLMNInfoList\":[{\"pLMNId\":\"310-410\",\"sNSSAI\":{\"sNSSAI\":\"001-093\",\"status\":\"ACTIVE\",\"configData\":{\"maxNumberOfConns\":3000}}}]}]}";
            GNBCUCPModel gNBCUCPModel = new GNBCUCPModel();
            try {
                    gNBCUCPModel = objectMapper.readValue(input, GNBCUCPModel.class);
            }catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }

            Mockito.doReturn(gNBCUCPModel).when(rANSliceConfigService).saveGNBCUCP(gNBCUCPModel);
            assertEquals(gNBCUCPModel, rANSliceConfigService.saveGNBCUCP(gNBCUCPModel));
    }

	 @Test
     public void testFetchGNBCUCPData() {
             String cuCPName = "cucpserver1";
             GNBCUCPModel gNBCUCPModel = new GNBCUCPModel();
             Mockito.doReturn(gNBCUCPModel).when(rANSliceConfigService).fetchGNBCUCPData(cuCPName);
             assertEquals(gNBCUCPModel, rANSliceConfigService.fetchGNBCUCPData(cuCPName));
     }

	 @Test
	 public void testSaveGNBDU() {
         String input="{\"gNBDUId\":1,\"gNBId\":98763,\"gNBIdLength\":5,\"pLMNId\":\"310-410\",\"gNBDUName\":\"gnduserver1\",\"nFType\":\"DU\",\"nearRTRICId\":11,\"cellDUList\":[{\"cellLocalId\":103593999,\"operationalState\":\"ENABLED\",\"administrativeState\":\"UNLOCKED\",\"cellState\":\"ACTIVE\",\"nRPCI\":12,\"nRTAC\":310,\"resourceType\":\"PRB\",\"pLMNInfoList\":[{\"pLMNId\":\"310-410\",\"sNSSAI\":{\"sNSSAI\":\"001-003\",\"status\":\"ACTIVE\",\"configData\":{\"dLThptPerSlice\":50,\"uLThptPerSlice\":40}}}]}]}";
         GNBDUModel gNBDUModel = new GNBDUModel();
         try {
                 gNBDUModel = objectMapper.readValue(input, GNBDUModel.class);
         } catch (Exception e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
         }
         Mockito.doReturn(gNBDUModel).when(rANSliceConfigService).saveGNBDU(gNBDUModel);
         assertEquals(gNBDUModel, rANSliceConfigService.saveGNBDU(gNBDUModel));
         }
	 
	 @Test
     public void testFetchGNBDUData() {
             int gNBDUId=1;
             GNBDUModel gNBDUModel = new GNBDUModel();
             Mockito.doReturn(gNBDUModel).when(rANSliceConfigService).fetchGNBDUData(gNBDUId);
             assertEquals(gNBDUModel, rANSliceConfigService.fetchGNBDUData(gNBDUId));
     }

	 @Test
     public void testSaveGNBCUUP() {
             String input = "{\"gNBCUUPId\":1111,\"gNBId\":98763,\"gNBIdLength\":2,\"resourceType\":\"DRB\",\"nearRTRICId\":11,\"pLMNInfoList\":[{\"pLMNId\":\"310-410\",\"sNSSAI\":{\"sNSSAI\":\"001-003\",\"status\":\"ACTIVE\",\"configData\":{\"maxNumberOfConns\":3000}}}]}";
             GNBCUUPModel gNBCUUPModel = new GNBCUUPModel();
             try {
                     gNBCUUPModel=objectMapper.readValue(input, GNBCUUPModel.class);
             } catch (Exception e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
             }
             Mockito.doReturn(gNBCUUPModel).when(rANSliceConfigService).saveGNBCUUP(gNBCUUPModel);
             assertEquals(gNBCUUPModel, rANSliceConfigService.saveGNBCUUP(gNBCUUPModel));
     }
	 
	 @Test
     public void testFetchGNBCUUPData() {
             Integer gNBCUUPId = 1111;
             String response = "{\"gNBCUUPId\":1111,\"gNBId\":98763,\"gNBIdLength\":2,\"resourceType\":\"DRB\",\"nearRTRICId\":11,\"pLMNInfoList\":[{\"pLMNId\":\"310-410\",\"sNSSAI\":{\"sNSSAI\":\"001-003\",\"status\":\"ACTIVE\",\"configData\":{\"maxNumberOfConns\":3000}}}]}";
             GNBCUUPModel gNBCUUPModel = new GNBCUUPModel();
             try {
                     gNBCUUPModel=objectMapper.readValue(response, GNBCUUPModel.class);
             } catch (Exception e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
             }
             Mockito.doReturn(gNBCUUPModel).when(rANSliceConfigService).fetchGNBCUUPData(gNBCUUPId);
             assertEquals(gNBCUUPModel, rANSliceConfigService.fetchGNBCUUPData(gNBCUUPId));

     }

	 @Test
     public void testSaveNearRTRIC() {
             NearRTRICModel nearRTRIC = new NearRTRICModel();
             String input =  "{\"nearRTRICId\":11,\"gNBId\":98763,\"trackingArea\":[\"Kingston\"],\"resourceType\":\"NearRTRIC\",\"ranNFNSSIList\":[\"11\",\"22\"],\"pLMNInfoList\":[{\"pLMNId\":\"310-410\",\"sNSSAI\":{\"sNSSAI\":\"001-003\",\"status\":\"ACTIVE\",\"configData\":{\"dLThptPerSlice\":55,\"uLThptPerSlice\":40}}}]}";
             try {
                     nearRTRIC=objectMapper.readValue(input, NearRTRICModel.class);
             } catch (Exception e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
             }
             Mockito.doReturn(nearRTRIC).when(rANSliceConfigService).saveNearRTRIC(nearRTRIC);
             assertEquals(nearRTRIC, rANSliceConfigService.saveNearRTRIC(nearRTRIC));
     }
	 
	 @Test
     public void testFetchNearRTRICData() {
             int nearRTRICId = 11;
             String response =       "{\"nearRTRICId\":11,\"gNBId\":98763,\"trackingArea\":[\"Kingston\"],\"resourceType\":\"NearRTRIC\",\"ranNFNSSIList\":[\"11\",\"22\"],\"pLMNInfoList\":[{\"pLMNId\":\"310-410\",\"sNSSAI\":{\"sNSSAI\":\"001-003\",\"status\":\"ACTIVE\",\"configData\":{\"dLThptPerSlice\":55,\"uLThptPerSlice\":40}}}]}";
             NearRTRICModel nearRTRIC = new NearRTRICModel();
             try {
                     nearRTRIC=objectMapper.readValue(response, NearRTRICModel.class);
             } catch (Exception e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
             }
             Mockito.doReturn(nearRTRIC).when(rANSliceConfigService).fetchNearRTRICData(nearRTRICId);
             assertEquals(nearRTRIC, rANSliceConfigService.fetchNearRTRICData(nearRTRICId));
     }
	 
	 @Test
     public void testFindRICsInTA() {
             String nSSAI="01-000100";
             List<NearRTRICModel> ricsList = new ArrayList<>();
             String ricModel =       "{\"nearRTRICId\":11,\"gNBId\":98763,\"trackingArea\":[\"Kingston\"],\"resourceType\":\"NearRTRIC\",\"ranNFNSSIList\":[\"11\",\"22\"],\"pLMNInfoList\":[{\"pLMNId\":\"310-410\",\"sNSSAI\":{\"sNSSAI\":\"001-003\",\"status\":\"ACTIVE\",\"configData\":{\"dLThptPerSlice\":55,\"uLThptPerSlice\":40}}}]}";
             NearRTRICModel nearRTRIC = new NearRTRICModel();
             try {
                     nearRTRIC=objectMapper.readValue(ricModel, NearRTRICModel.class);
             } catch (Exception e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
             }
             ricsList.add(nearRTRIC);
             Mockito.doReturn(ricsList).when(rANSliceConfigService).findRICsInTA(Mockito.anyString());
             assertEquals(ricsList, rANSliceConfigService.findRICsInTA(nSSAI));
     }
	 
	 @Test
     public void testfindNearRTRICofCells() {
             List<Integer> cellIds=new ArrayList<Integer>();cellIds.add(23456);cellIds.add(45785);
             List<NearRTRICModel> ricsList = new ArrayList<>();
             String ricModel =       "{\"nearRTRICId\":11,\"gNBId\":98763,\"trackingArea\":[\"Kingston\"],\"resourceType\":\"NearRTRIC\",\"ranNFNSSIList\":[\"11\",\"22\"],\"pLMNInfoList\":[{\"pLMNId\":\"310-410\",\"sNSSAI\":{\"sNSSAI\":\"001-003\",\"status\":\"ACTIVE\",\"configData\":{\"dLThptPerSlice\":55,\"uLThptPerSlice\":40}}}]}";
             NearRTRICModel nearRTRIC = new NearRTRICModel();
             try {
                     nearRTRIC=objectMapper.readValue(ricModel, NearRTRICModel.class);
             } catch (Exception e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
             }
             ricsList.add(nearRTRIC);
             Mockito.doReturn(ricsList).when(rANSliceConfigService).findNearRTRICofCells(Mockito.anyList());
             assertEquals(ricsList, rANSliceConfigService.findNearRTRICofCells(cellIds));
     }

     @Test
     public void testUpdateRANInventory() {
             String input = "{\"ranNFNSSIId\":\"11\",\"ranNSSIList\":[\"ran3\"],\"nSSAIList\":[\"001-003\",\"001-001\"],\"sliceProfilesList\":[{\"sliceProfileId\":12,\"dLThptPerSlice\":1,\"uLThptPerSlice\":2,\"maxNumberofConns\":3,\"sNSSAI\":\"001-003\"}],\"trackingAreaList\":\"Chennai\",\"subnetStatus\":\"ACTIVE\",\"nsstId\":\"NSSTID1\",\"sliceType\":\"eMBB\",\"isShareable\":\"true\"}";
             RANSliceInfoModel inventorynfo = new RANSliceInfoModel();
             try {
                     inventorynfo=objectMapper.readValue(input, RANSliceInfoModel.class);
             } catch (Exception e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
             }
             Mockito.doReturn(inventorynfo).when(rANSliceConfigService).updateRANInventory(Mockito.anyObject());
             assertEquals(inventorynfo, rANSliceConfigService.updateRANInventory(inventorynfo));
     }
     
     @Test
     public void testFetchRANSlice() {
             String ranNFNSSIId="11";
             String input = "{\"ranNFNSSIId\":\"11\",\"ranNSSIList\":[\"ran3\"],\"nSSAIList\":[\"001-003\",\"001-001\"],\"sliceProfilesList\":[{\"sliceProfileId\":12,\"dLThptPerSlice\":1,\"uLThptPerSlice\":2,\"maxNumberofConns\":3,\"sNSSAI\":\"001-003\"}],\"trackingAreaList\":\"Chennai\",\"subnetStatus\":\"ACTIVE\",\"nsstId\":\"NSSTID1\",\"sliceType\":\"eMBB\",\"isShareable\":\"true\"}";
             RANSliceInfoModel inventorynfo = new RANSliceInfoModel();
             try {
                     inventorynfo=objectMapper.readValue(input, RANSliceInfoModel.class);
             } catch (Exception e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
             }
             Mockito.doReturn(inventorynfo).when(rANSliceConfigService).fetchRANSlice(ranNFNSSIId);
             assertEquals(inventorynfo, rANSliceConfigService.fetchRANSlice(ranNFNSSIId));
     }

     @Test
     public void testFetchCellsofTA() {
             String trackingArea = "Kingston";
             List<String> cellIds=new ArrayList<String>();cellIds.add("23456");cellIds.add("45785");
             Mockito.doReturn(cellIds).when(rANSliceConfigService).fetchCellsofTA(Mockito.anyString());
             assertEquals(cellIds, rANSliceConfigService.fetchCellsofTA(trackingArea));
     }

     @Test
     public void testFetchCUCellsofRIC() {
             Integer nearRTRICId=1;
             List<NRCellCUModel> cellCUList= new ArrayList<>();
             Mockito.doReturn(cellCUList).when(rANSliceConfigService).fetchCUCellsofRIC(Mockito.anyInt());
             assertEquals(cellCUList, rANSliceConfigService.fetchCUCellsofRIC(nearRTRICId));;
     }

     @Test
     public void testFetchDUCellsofRIC() {
             String sNSSAI = "001-00001";
             Map<Integer, List<NRCellDUModel>> cellsMap = new HashMap<Integer, List<NRCellDUModel>>();
             Mockito.doReturn(cellsMap).when(rANSliceConfigService).fetchDUCellsofRIC(Mockito.anyString());
             assertEquals(cellsMap, rANSliceConfigService.fetchDUCellsofRIC(sNSSAI));;
     }
     
     @Test
     public void testFindNearRTRICByNSSI() {
             String ranNFNSSIID="11";
             List<NearRTRICModel> ricsList = new ArrayList<>();
             String ricModel =       "{\"nearRTRICId\":11,\"gNBId\":98763,\"trackingArea\":[\"Kingston\"],\"resourceType\":\"NearRTRIC\",\"ranNFNSSIList\":[\"11\",\"22\"],\"pLMNInfoList\":[{\"pLMNId\":\"310-410\",\"sNSSAI\":{\"sNSSAI\":\"001-003\",\"status\":\"ACTIVE\",\"configData\":{\"dLThptPerSlice\":55,\"uLThptPerSlice\":40}}}]}";
             NearRTRICModel nearRTRIC = new NearRTRICModel();
             try {
                     nearRTRIC=objectMapper.readValue(ricModel, NearRTRICModel.class);
             } catch (Exception e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
             }
             ricsList.add(nearRTRIC);
             Mockito.doReturn(ricsList).when(rANSliceConfigService).findNearRTRICByNSSI(Mockito.anyString());
             assertEquals(ricsList, rANSliceConfigService.findNearRTRICByNSSI(ranNFNSSIID));
     }

     @Test
     public void testFindRICsByNSSAI() {
             String sNSSAI = "001-00001";
             List<NearRTRICModel> ricsList = new ArrayList<>();
             String ricModel =       "{\"nearRTRICId\":11,\"gNBId\":98763,\"trackingArea\":[\"Kingston\"],\"resourceType\":\"NearRTRIC\",\"ranNFNSSIList\":[\"11\",\"22\"],\"pLMNInfoList\":[{\"pLMNId\":\"310-410\",\"sNSSAI\":{\"sNSSAI\":\"001-003\",\"status\":\"ACTIVE\",\"configData\":{\"dLThptPerSlice\":55,\"uLThptPerSlice\":40}}}]}";
             NearRTRICModel nearRTRIC = new NearRTRICModel();
             try {
                     nearRTRIC=objectMapper.readValue(ricModel, NearRTRICModel.class);
             } catch (Exception e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
             }
             ricsList.add(nearRTRIC);
             Mockito.doReturn(ricsList).when(rANSliceConfigService).findRICsByNSSAI(Mockito.anyString());
             assertEquals(ricsList, rANSliceConfigService.findRICsByNSSAI(sNSSAI));
     }

     @Test
     public void testFindSliceProfileconfig() {
             String sNSSAI = "001-00001";
             Map<String,Integer> configDetails = new HashMap<String, Integer>();
             configDetails.put("dLThptPerSlice",40);
             configDetails.put("uLThptPerSlice",50);
             Mockito.doReturn(configDetails).when(rANSliceConfigService).findSliceProfileconfig(Mockito.anyString());
             assertEquals(configDetails, rANSliceConfigService.findSliceProfileconfig(sNSSAI));
     }
     @Test
     public void testFindSliceeconfig() {
             String sNSSAI = "001-00001";Map<Integer, NSSAIConfig> configMap = new HashMap<Integer, NSSAIConfig>();
             Mockito.doReturn(configMap).when(rANSliceConfigService).findSliceConfig(Mockito.anyString());
             assertEquals(configMap, rANSliceConfigService.findSliceConfig(sNSSAI));
     }

     @Test
     public void testFindDUsofSNssai() {
             String sNSSAI = "001-00001";
             List<GNBDUModel> duModels = new ArrayList<GNBDUModel>();
             Mockito.doReturn(duModels).when(rANSliceConfigService).findDUsofSNssai(Mockito.anyString());
             assertEquals(duModels, rANSliceConfigService.findDUsofSNssai(sNSSAI));
     }

     @Test
     public void testFindCUsofSNssai() {
             String sNSSAI = "001-00001";
             List<GNBCUCPModel> cuModels = new ArrayList<GNBCUCPModel>();
             Mockito.doReturn(cuModels).when(rANSliceConfigService).findCUsofSNssai(Mockito.anyString());
             assertEquals(cuModels, rANSliceConfigService.findCUsofSNssai(sNSSAI));
     }
     
     @Test
     public void testGetSubscriberDetails() {
             String sNSSAI = "001-00001";
             Map<String, String> details = new HashMap<String, String>();
             Mockito.doReturn(details).when(rANSliceConfigService).getSubscriberDetails(Mockito.anyString());
             assertEquals(details, rANSliceConfigService.getSubscriberDetails(sNSSAI));
     }

     @Test
     public void testFindAllCUCPFunctions() {
             List<GNBCUCPModel> cucpModels = new ArrayList<GNBCUCPModel>();
             Mockito.doReturn(cucpModels).when(rANSliceConfigService).findAllCUCPFunctions();
             assertEquals(cucpModels, rANSliceConfigService.findAllCUCPFunctions());
     }

     @Test
     public void testFindAllDUFunctions() {
             List<GNBDUModel> duModels = new ArrayList<GNBDUModel>();
             Mockito.doReturn(duModels).when(rANSliceConfigService).findAllDUFunctions();
             assertEquals(duModels, rANSliceConfigService.findAllDUFunctions());
     }

}
