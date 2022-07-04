/*-
 * ============LICENSE_START=======================================================
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

package org.onap.ransim.rest.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.websocket.Session;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.onap.ransim.netconf.NetconfClient;
import org.onap.ransim.rest.api.handler.RansimPciHandler;
import org.onap.ransim.rest.api.models.CellData;
import org.onap.ransim.rest.api.models.CellDetails;
import org.onap.ransim.rest.api.models.CellNeighbor;
import org.onap.ransim.rest.api.models.NSSAIConfig;
import org.onap.ransim.rest.api.models.NbrDump;
import org.onap.ransim.rest.api.models.NeighborDetails;
import org.onap.ransim.rest.api.models.NeihborId;
import org.onap.ransim.rest.api.models.NetconfServers;
import org.onap.ransim.rest.api.models.NRCellCU;
import org.onap.ransim.rest.api.models.NRCellRelation;
import org.onap.ransim.rest.api.models.PLMNInfo;
import org.onap.ransim.rest.api.models.RRMPolicyMember;
import org.onap.ransim.rest.api.models.RRMPolicyRatio;
import org.onap.ransim.rest.api.models.SliceProfile;
import org.onap.ransim.rest.api.models.TopologyDump;
import org.onap.ransim.rest.api.repository.GNBCUUPRepository;
import org.onap.ransim.rest.api.repository.NRCellCURepository;
import org.onap.ransim.rest.api.repository.NRCellDURepository;
import org.onap.ransim.rest.api.repository.NetconfServersRepo;
import org.onap.ransim.rest.api.repository.RRMPolicyRepository;
import org.onap.ransim.rest.api.repository.SliceProfileRepository;
import org.onap.ransim.rest.client.RestClient;
import org.onap.ransim.rest.web.mapper.GNBCUCPModel;
import org.onap.ransim.rest.web.mapper.GNBCUUPModel;
import org.onap.ransim.rest.web.mapper.GNBDUModel;
import org.onap.ransim.rest.web.mapper.NRRelationData;
import org.onap.ransim.rest.web.mapper.NRCellRelationModel;
import org.onap.ransim.rest.web.mapper.NRCellCUModel;
import org.onap.ransim.rest.web.mapper.NRCellDUModel;
import org.onap.ransim.rest.web.mapper.NSSAIData;
import org.onap.ransim.rest.web.mapper.NearRTRICModel;
import org.onap.ransim.rest.web.mapper.PLMNInfoModel;
import org.onap.ransim.rest.web.mapper.RRMPolicyRatioModel;
import org.onap.ransim.utilities.RansimUtilities;
import org.onap.ransim.websocket.model.*;
import org.onap.ransim.websocket.model.ConfigData;
import org.onap.ransim.websocket.model.ConfigPLMNInfo;
import org.onap.ransim.websocket.model.GNBCUCPFunction;
import org.onap.ransim.websocket.model.ModifyNeighbor;
import org.onap.ransim.websocket.model.Neighbor;
import org.onap.ransim.websocket.model.SNSSAI;
import org.onap.ransim.websocket.model.SetConfigTopology;
import org.onap.ransim.websocket.model.Topology;
import org.onap.ransim.websocket.model.UpdateCell;
import org.onap.ransim.websocket.server.RansimWebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RansimControllerServices {

    static Logger log = Logger.getLogger(RansimControllerServices.class.getName());

    Properties netconfConstants = new Properties();
    public int gridSize = 10;
    boolean collision = false;
    String serverIdPrefix = "";
    public static String useCaseType = "";
    static int numberOfCellsPerNcServer = 15;
    int numberOfMachines = 1;
    int numberOfProcessPerMc = 5;
    boolean strictValidateRansimAgentsAvailability = false;
    static public Map<String, Session> webSocketSessions = new ConcurrentHashMap<String, Session>();
    public static Map<String, String> serverIdIpPortMapping = new ConcurrentHashMap<String, String>();

    static Map<String, String> globalNcServerUuidMap = new ConcurrentHashMap<String, String>();
    static List<String> unassignedServerIds = Collections.synchronizedList(new ArrayList<String>());
    static Map<String, List<String>> serverIdIpNodeMapping = new ConcurrentHashMap<String, List<String>>();
    static Map<String, List<String>> ricIdFunctionMapping = new ConcurrentHashMap<String, List<String>>();
    static List<String> unassignedgNBIds = Collections.synchronizedList(new ArrayList<String>());
    static List<String> unassignedrtRicIds = Collections.synchronizedList(new ArrayList<String>());
    static List<NearRTRICModel> rtricModelList = Collections.synchronizedList(new ArrayList<>());
    int nextServerIdNumber = 1001;
    String sdnrServerIp = "";
    int sdnrServerPort = 0;
    static String sdnrServerUserid = "";
    static String sdnrServerPassword = "";
    private static final String fileBasePath = "/tmp/ransim-install/config/";
    public static String dumpFileName = "";
    public static long maxPciValueAllowed = 503;

    @Autowired
    RansimPciHandler rsPciHdlr;

    @Autowired
    RansimRepositoryService ransimRepo;

    @Autowired
    RANSliceConfigService ranSliceConfigService;

    @Autowired
    RRMPolicyRepository rrmPolicyRepository;

    @Autowired
    NRCellDURepository nRCellDURepository;

    @Autowired
    NRCellCURepository nRCellCURepository;

    @Autowired
    GNBCUUPRepository gNBCUUPRepository;

    @Autowired
    NetconfServersRepo netconfServersRepo;
    /*
     * @Autowired
     * PLMNInfoRepo pLMNInfoRepo;
     * 
     */ @Autowired
    SliceProfileRepository sliceProfileRepository;

    @PostConstruct
    private void startWSTheread() {
        new KeepWebsockAliveThread(this).start();
    }

    private String checkIpPortAlreadyExists(String ipPort, Map<String, String> serverIdIpPortMapping) {
        String serverId = null;
        for (String key : serverIdIpPortMapping.keySet()) {
            String value = serverIdIpPortMapping.get(key);
            if (value.equals(ipPort)) {
                serverId = key;
                break;
            }
        }
        return serverId;
    }

    /**
     * Add web socket sessions.
     * * @param ipPort ip address for the session
     * 
     * @param wsSession session details
     */
    public synchronized String addWebSocketSessions(String ipPort, Session wsSession) {
        loadProperties();
        if (webSocketSessions.containsKey(ipPort)) {
            log.info("addWebSocketSessions: Client session " + wsSession.getId() + " for " + ipPort
                    + " already exist. Removing old session.");
            webSocketSessions.remove(ipPort);
        }

        log.info("addWebSocketSessions: Adding Client session " + wsSession.getId() + " for " + ipPort);
        webSocketSessions.put(ipPort, wsSession);
        String serverId = null;
        if (!serverIdIpPortMapping.containsValue(ipPort)) {
            if (unassignedServerIds.size() > 0) {
                log.info("addWebSocketSessions: No serverIds pending to assign for " + ipPort);
                serverId = checkIpPortAlreadyExists(ipPort, serverIdIpPortMapping);
                if (serverId == null) {
                    serverId = unassignedServerIds.remove(0);
                } else {
                    if (unassignedServerIds.contains(serverId)) {
                        unassignedServerIds.remove(serverId);
                    }
                }
                log.info("RanSim Controller - Available unassigned ServerIds :" + unassignedServerIds);
                log.info("RanSim Controller - addWebSocketSessions: Adding serverId " + serverId + " for " + ipPort);
                serverIdIpPortMapping.put(serverId, ipPort);
                log.debug("RanSim Controller - serverIdIpPortMapping >>>> :" + serverIdIpPortMapping);
                mapServerIdToNodes(serverId);
                try {

                    NetconfServers server = ransimRepo.getNetconfServer(serverId);
                    if (server != null) {
                        server.setIp(ipPort.split(":")[0]);
                        server.setNetconfPort(ipPort.split(":")[1]);
                        ransimRepo.mergeNetconfServers(server);
                    }

                } catch (Exception e1) {
                    log.error("Exception mapServerIdToNodes :", e1);
                }
            } else {
                log.error("addWebSocketSessions: No serverIds pending to assign for " + ipPort);
            }
        } else {
            for (String key : serverIdIpPortMapping.keySet()) {
                if (serverIdIpPortMapping.get(key).equals(ipPort)) {
                    log.info("addWebSocketSessions: ServerId " + key + " for " + ipPort + " is exist already");
                    serverId = key;
                    break;
                }
            }
        }
        return serverId;
    }

    public synchronized String addRanWebSocketSessions(String ipPort, Session wsSession) {
        if (webSocketSessions.containsKey(ipPort)) {
            log.info("addWebSocketSessions: Client session " + wsSession.getId() + " for " + ipPort
                    + " already exist. Removing old session.");
            webSocketSessions.remove(ipPort);
        }

        log.info("addWebSocketSessions: Adding Client session " + wsSession.getId() + " for " + ipPort);
        webSocketSessions.put(ipPort, wsSession);
        String serverId = null;
        if (!serverIdIpPortMapping.containsValue(ipPort)) {
            if (!ricIdFunctionMapping.isEmpty()) {
                if (unassignedgNBIds.size() > 0) {
                    log.info("addWebSocketSessions: No serverIds pending to assign for " + ipPort);
                    serverId = checkIpPortAlreadyExists(ipPort, serverIdIpPortMapping);
                    if (serverId == null) {
                        serverId = unassignedgNBIds.remove(0);
                    } else {
                        if (unassignedgNBIds.contains(serverId)) {
                            unassignedgNBIds.remove(serverId);
                        }
                    }
                    log.info("RanSim Controller - Available unassigned ServerIds :" + unassignedgNBIds);
                    log.info(
                            "RanSim Controller - addWebSocketSessions: Adding serverId " + serverId + " for " + ipPort);
                    serverIdIpPortMapping.put(serverId, ipPort);
                    log.debug("RanSim Controller - serverIdIpPortMapping >>>> :" + serverIdIpPortMapping);
                    try {
                        NetconfServers server = ransimRepo.getNetconfServer(serverId);
                        if (server != null) {
                            server.setIp(ipPort.split(":")[0]);
                            server.setNetconfPort(ipPort.split(":")[1]);
                            ransimRepo.mergeNetconfServers(server);
                        }

                    } catch (Exception e1) {
                        log.error("Exception mapServerIdToNodes :", e1);
                    }
                } else {
                    if (unassignedrtRicIds.size() > 0) {
                        unassignedgNBIds.addAll(ricIdFunctionMapping.get(unassignedrtRicIds.get(0)));
                        log.info("addWebSocketSessions: No serverIds pending to assign for " + ipPort);
                        serverId = checkIpPortAlreadyExists(ipPort, serverIdIpPortMapping);
                        if (serverId == null) {
                            serverId = unassignedrtRicIds.remove(0);
                        } else {
                            if (unassignedrtRicIds.contains(serverId)) {
                                unassignedrtRicIds.remove(serverId);
                            }
                        }
                        log.info("RanSim Controller - Available unassigned ServerIds :" + unassignedrtRicIds);
                        log.info("RanSim Controller - addWebSocketSessions: Adding serverId " + serverId + " for "
                                + ipPort);
                        serverIdIpPortMapping.put(serverId, ipPort);
                        log.debug("RanSim Controller - serverIdIpPortMapping >>>> :" + serverIdIpPortMapping);
                        try {
                            NetconfServers server = ransimRepo.getNetconfServer(serverId);
                            if (server != null) {
                                server.setIp(ipPort.split(":")[0]);
                                server.setNetconfPort(ipPort.split(":")[1]);
                                ransimRepo.mergeNetconfServers(server);
                            }

                        } catch (Exception e1) {
                            log.error("Exception mapServerIdToNodes :", e1);
                        }
                    } else {
                        log.info("addWebSocketSessions: No ric serverIds pending to assign for " + ipPort);
                    }
                }
            } else {
                log.error("addWebSocketSessions: No serverIds pending to assign for " + ipPort);
            }
        } else {
            for (String key : serverIdIpPortMapping.keySet()) {
                if (serverIdIpPortMapping.get(key).equals(ipPort)) {
                    log.info("addWebSocketSessions: ServerId " + key + " for " + ipPort + " is exist already");
                    serverId = key;
                    break;
                }
            }
        }
        return serverId;
    }

    /**
     * Map server ID to the cells
     *
     * @param serverId Server ID
     */
    private void mapServerIdToNodes(String serverId) {
        dumpSessionDetails();
        // already mapped.RansimController Do nothing.
        if (!serverIdIpNodeMapping.containsKey(serverId)) {
            List<String> nodeIds = new ArrayList<String>();
            try {
                List<CellDetails> nodes = ransimRepo.getCellsWithNoServerIds();
                for (CellDetails cell : nodes) {
                    cell.setServerId(serverId);
                    nodeIds.add(cell.getNodeId());
                    ransimRepo.mergeCellDetails(cell);
                }
                serverIdIpNodeMapping.put(serverId, nodeIds);
            } catch (Exception e1) {
                log.info("Exception mapServerIdToNodes :", e1);

            }
        }
    }

    /**
     * It removes the web socket sessions.
     *
     * @param ipPort ip address of the netconf server
     */
    public synchronized void removeWebSocketSessions(String ipPort) {
        log.info("remove websocket session request received for: " + ipPort);
        try {
            if (webSocketSessions.containsKey(ipPort)) {
                String removedServerId = null;
                for (String serverId : serverIdIpPortMapping.keySet()) {
                    String ipPortVal = serverIdIpPortMapping.get(serverId);
                    if (ipPortVal.equals(ipPort)) {
                        if (!unassignedServerIds.contains(serverId)) {
                            unassignedServerIds.add(serverId);
                            log.info(serverId + "added in unassignedServerIds");
                        }
                        NetconfServers ns = ransimRepo.getNetconfServer(serverId);
                        ns.setIp(null);
                        ns.setNetconfPort(null);
                        log.info(serverId + " ip and Port set as null ");
                        ransimRepo.mergeNetconfServers(ns);
                        removedServerId = serverId;
                        break;
                    }
                }
                serverIdIpPortMapping.remove(removedServerId);

                Session wsSession = webSocketSessions.remove(ipPort);
                log.info("removeWebSocketSessions: Client session " + wsSession.getId() + " for " + ipPort
                        + " is removed. Server Id : " + removedServerId);
            } else {
                log.info("addWebSocketSessions: Client session for " + ipPort + " not exist");
            }
        } catch (Exception e) {
            log.error("Exception in removeWebSocketSessions. e: " + e);
        }

    }

    /**
     * Checks the number of ransim agents running.
     *
     * @param cellsToBeSimulated number of cells to be simulated
     * @return returns true if there are enough ransim agents running
     */
    public boolean hasEnoughRansimAgentsRunning(int cellsToBeSimulated) {

        log.info("hasEnoughRansimAgentsRunning: numberOfCellsPerNCServer " + numberOfCellsPerNcServer
                + " , webSocketSessions.size:" + webSocketSessions.size() + " , cellsToBeSimulated:"
                + cellsToBeSimulated);
        log.info(strictValidateRansimAgentsAvailability);

        if (strictValidateRansimAgentsAvailability) {
            if (numberOfCellsPerNcServer * webSocketSessions.size() < cellsToBeSimulated) {
                return false;
            }
        }
        return true;
    }

    /**
     * It updates the constant values in the properties file.
     */
    public void loadProperties() {
        InputStream input = null;
        try {

            input = new FileInputStream("/tmp/ransim-install/config/ransim.properties");
            netconfConstants.load(input);
            serverIdPrefix = netconfConstants.getProperty("serverIdPrefix");
            numberOfCellsPerNcServer = Integer.parseInt(netconfConstants.getProperty("numberOfCellsPerNCServer"));
            numberOfMachines = Integer.parseInt(netconfConstants.getProperty("numberOfMachines"));
            numberOfProcessPerMc = Integer.parseInt(netconfConstants.getProperty("numberOfProcessPerMc"));
            strictValidateRansimAgentsAvailability =
                    Boolean.parseBoolean(netconfConstants.getProperty("strictValidateRansimAgentsAvailability"));
            maxPciValueAllowed = Long.parseLong(netconfConstants.getProperty("maxPciValueAllowed"));
            sdnrServerIp = System.getenv("SDNR_IP");
            sdnrServerPort = Integer.parseInt(System.getenv("SDNR_PORT"));
            sdnrServerUserid = System.getenv("SDNR_USER");
            sdnrServerPassword = System.getenv("SDNR_PASSWORD");
            useCaseType = "sonUsecase";

        } catch (Exception e) {
            log.error("Properties file error", e);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception ex) {
                log.error("Properties file error", ex);
            }
        }
    }

    public void loadGNBFunctionProperties() {
	try {
            
	    sdnrServerIp = System.getenv("SDNR_IP");
            sdnrServerPort = Integer.parseInt(System.getenv("SDNR_PORT"));
            sdnrServerUserid = System.getenv("SDNR_USER");
            sdnrServerPassword = System.getenv("SDNR_PASSWORD");
            useCaseType = "ranSlicingUsecase";
            rtricModelList = ranSliceConfigService.findAllNearRTRIC();
            for (NearRTRICModel rtricModel : rtricModelList) {
                List<String> gNBList = new ArrayList<>();
                for (GNBCUCPModel gNBCUCPModel : rtricModel.getgNBCUCPList()) {
                    gNBList.add(gNBCUCPModel.getgNBCUName());
		    setRanNetconfServers(gNBCUCPModel.getgNBCUName());
                    for (NRCellCUModel nRCellCUModel : gNBCUCPModel.getCellCUList()) {
		        if (nRCellCUModel.getpLMNInfoList().isEmpty()) {
                            org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();
                            try (FileReader reader = new FileReader("/tmp/ransim-install/config/ransimdata.json")) {
                                // Read JSON file
                                Object obj = jsonParser.parse(reader);
                                JSONArray List = (JSONArray) obj;
                                for (int i = 0; i < List.size(); i++) {
                                    JSONObject gNB = (JSONObject) List.get(i);
                                    String gNBCUName = (String) gNB.get("gNBCUName");
                                    Long gNBId = (Long) gNB.get("gNBId");
                                    Long gNBIdLength = (Long) gNB.get("gNBIdLength");
                                    String pLMNId = (String) gNB.get("pLMNId");
                                    String nFType = (String) gNB.get("nFType");
                                    Long nearRTRICId = (Long) gNB.get("nearRTRICId");
                                    String cellListVar = (String) gNB.get("cellCUList").toString();
                                    Object cellListObj = jsonParser.parse(cellListVar);
                                    JSONArray cellList = (JSONArray) cellListObj;
                                    List<NRCellCUModel> nRCellCUModelList = new ArrayList<NRCellCUModel>();
                                    for (int j = 0; j < cellList.size(); j++) {
                                        JSONObject cell = (JSONObject) cellList.get(j);
                                        Long cellLocalId = (Long) cell.get("cellLocalId");
					String nRCellRelVar = (String) cell.get("nRCellRelationList").toString();
					Object nRCellVarObj = jsonParser.parse(nRCellRelVar);
					JSONArray nRCellRelList = (JSONArray) nRCellVarObj;
					List<NRCellRelationModel> nrCellRelationModelList = new ArrayList<NRCellRelationModel>();
					for (int k = 0; k < nRCellRelList.size(); k++) {
						JSONObject nrcellrel = (JSONObject) nRCellRelList.get(k);
						Long idNRCellRelation = (Long) nrcellrel.get("idNRCellRelation");
						JSONObject attributes = (JSONObject) nrcellrel.get("attributes");
						Long nRTCI = (Long) attributes.get("nRTCI");
						String isHOAllowed = (String) attributes.get("isHOAllowed");
						NRRelationData nrRelationDataObj = new NRRelationData();
						nrRelationDataObj.setNRTCI(nRTCI.intValue());
						nrRelationDataObj.setIsHOAllowed(Boolean.valueOf(isHOAllowed));
						NRCellRelationModel nrCellRelationModel = new NRCellRelationModel();
						nrCellRelationModel.setIdNRCellRelation(idNRCellRelation.intValue());
						nrCellRelationModel.setNRRelationData(nrRelationDataObj);
						nrCellRelationModelList.add(nrCellRelationModel);
                                        }
                                        String plmVar = (String) cell.get("pLMNInfoList").toString();
                                        Object pmlVarObj = jsonParser.parse(plmVar);
                                        JSONArray pmlList = (JSONArray) pmlVarObj;
                                        List<PLMNInfoModel> plmnInfoModelList = new ArrayList<PLMNInfoModel>();
                                        for (int k = 0; k < pmlList.size(); k++) {
                                            JSONObject pml = (JSONObject) pmlList.get(k);
                                            String pLMNid = (String) pml.get("pLMNId");
                                            JSONObject sNSSAI = (JSONObject) pml.get("sNSSAI");
                                            String sNssai = (String) sNSSAI.get("sNSSAI");
                                            String status = (String) sNSSAI.get("status");
                                            JSONObject configData = (JSONObject) sNSSAI.get("configData");
                                            Long maxNumberOfConns = (Long) configData.get("maxNumberOfConns");
                                            NSSAIConfig configDataObj = new NSSAIConfig();
                                            configDataObj.setMaxNumberOfConns(maxNumberOfConns.intValue());
                                            NSSAIData nssaiData = new NSSAIData();
                                            nssaiData.setsNSSAI(sNssai);
                                            nssaiData.setStatus(status);
                                            nssaiData.setConfigData(configDataObj);
                                            PLMNInfoModel plmnInfoModel = new PLMNInfoModel();
                                            plmnInfoModel.setpLMNId(pLMNid);
                                            plmnInfoModel.setsNSSAI(nssaiData);
                                            plmnInfoModelList.add(plmnInfoModel);
                                        }
                                        NRCellCUModel nrCellCUModel = new NRCellCUModel();
                                        nrCellCUModel.setCellLocalId(cellLocalId.intValue());
					nrCellCUModel.setNRCellRelation(nrCellRelationModelList);
                                        nrCellCUModel.setpLMNInfoList(plmnInfoModelList);
					List<String> attachedNoeds;
					if (serverIdIpNodeMapping.isEmpty()) {
						attachedNoeds = new ArrayList<String>();
						attachedNoeds.add(Integer.toString(nrCellCUModel.getCellLocalId()));
						serverIdIpNodeMapping.put(gNBCUCPModel.getgNBCUName(), attachedNoeds);
						if (attachedNoeds.size() > numberOfCellsPerNcServer) {
							log.info("Attaching Cell:");
						}
					} else {
						attachedNoeds = serverIdIpNodeMapping.get(gNBCUCPModel.getgNBCUName());
						attachedNoeds.add(Integer.toString(nrCellCUModel.getCellLocalId()));
						serverIdIpNodeMapping.put(gNBCUCPModel.getgNBCUName(), attachedNoeds);
						if (attachedNoeds.size() > numberOfCellsPerNcServer) {
							log.info("Attaching Cell:");
						}
					}
					log.info("Attaching Cell:" + nrCellCUModel.getCellLocalId() + " to "
							+ gNBCUCPModel.getgNBCUName());
					setRanCUCPNetconfServers(nrCellCUModel.getCellLocalId());
					dumpSessionDetails();
                                        nRCellCUModelList.add(nrCellCUModel);
					log.info("NRCELLCU LIST is : " + nRCellCUModelList);
                                    }
                                    GNBCUCPModel gNBModel = new GNBCUCPModel();
                                    gNBModel.setgNBCUName(gNBCUName);
                                    gNBModel.setgNBId(gNBId.intValue());
                                    gNBModel.setgNBIdLength(gNBIdLength.intValue());
                                    gNBModel.setpLMNId(pLMNId);
                                    gNBModel.setnFType(nFType);
                                    gNBModel.setNearRTRICId(nearRTRICId.intValue());
                                    gNBModel.setCellCUList(nRCellCUModelList);
                                    ranSliceConfigService.saveGNBCUCP(gNBModel);
				    log.info("gnBCUCP model is: " + gNBModel);
                                }
                            } catch (Exception e) {
                                log.error("Properties file error", e);
                            }
                        }
                    }
                }
                for (GNBCUUPModel gNBCUUPModel : rtricModel.getgNBCUUPList()) {
                    gNBList.add(gNBCUUPModel.getgNBCUUPId().toString());
                    setRanNetconfServers(gNBCUUPModel.getgNBCUUPId().toString());
                }
                for (GNBDUModel gNBDUModel : rtricModel.getgNBDUList()) {
                    gNBList.add(gNBDUModel.getgNBDUId().toString());
                    setRanNetconfServers(gNBDUModel.getgNBDUId().toString());
                }
		log.info(" gNBList in loadGNB is : " + gNBList);
                unassignedrtRicIds.add(rtricModel.getNearRTRICId().toString());
                ricIdFunctionMapping.put(rtricModel.getNearRTRICId().toString(), gNBList);
                setRanNetconfServers(rtricModel.getNearRTRICId().toString());
            
	    }
	} catch (Exception e) {
	      log.error("Properties file error", e);
	}
    }

    public void setRanNetconfServers(String serverId) {
	    try {		    
		    NetconfServers server = ransimRepo.getNetconfServer(serverId);
		    if (server == null) {
			    server = new NetconfServers();
			    server.setServerId(serverId);
		    }
		    ransimRepo.mergeNetconfServers(server);
	
	    } catch (Exception eu) {
		    log.error("setRanNetconfServers Function Error", eu);

	
	    }

    }

    
    public void setRanCUCPNetconfServers(Integer cellLocalId) {
	
	    NRCellCU currentCell = ransimRepo.getNRCellCUDetail(cellLocalId);
	    Set<NRCellCU> newList = new HashSet<NRCellCU>();

	    try {
		    if (currentCell != null) {
			    NetconfServers server = ransimRepo.getNetconfServer(currentCell.getgNBCUCPFunction().getgNBCUName());
			    if (server == null) {
				    server = new NetconfServers();
				    server.setServerId(currentCell.getgNBCUCPFunction().getgNBCUName());
			    } else {
				    newList.addAll(server.getCellList());

			    }
			    newList.add(currentCell);
			    server.setCellList(newList);
			    log.info("setNetconfServer CUCP: cellLocalId: " + cellLocalId + ",  ip: " + server.getIp() + ", portNum: " + server.getNetconfPort()
					    + ", serverId:" + currentCell.getgNBCUCPFunction().getgNBCUName());

			    ransimRepo.mergeNetconfServers(server);
		    }
	    } catch (Exception eu) {
		    log.error("setRanCUCPNetconfServers Function Error", eu);
	    }
    }

    /**
     * The function adds the cell(with nodeId passed as an argument) to its netconf
     * server list if the netconf server already exists. Else it will create a new
     * netconf server in the NetconfServers Table and the cell into its list.
     *
     * @param nodeId node Id of the cell
     */
    public void setNetconfServers(String nodeId) {

        CellDetails currentCell = ransimRepo.getCellDetail(nodeId);

        Set<CellDetails> newList = new HashSet<CellDetails>();
        try {
            if (currentCell != null) {
                NetconfServers server = ransimRepo.getNetconfServer(currentCell.getServerId());

                if (server == null) {

                    server = new NetconfServers();
                    server.setServerId(currentCell.getServerId());
                } else {
                    newList.addAll(server.getCells());
                }

                newList.add(currentCell);
                server.setCells(newList);
                log.info("setNetconfServers: nodeId: " + nodeId + ", X:" + currentCell.getGridX() + ", Y:"
                        + currentCell.getGridY() + ", ip: " + server.getIp() + ", portNum: " + server.getNetconfPort()
                        + ", serverId:" + currentCell.getServerId());

                ransimRepo.mergeNetconfServers(server);

            }

        } catch (Exception eu) {
            log.error("setNetconfServers Function Error", eu);

        }
    }

    /**
     * generateClusterFromFile()
     *
     * @throws IOException
     */
    public void generateClusterFromFile() throws IOException {

        log.debug("Inside generateClusterFromFile");
        File dumpFile = null;
        String cellDetailsString = "";

        dumpFile = new File(fileBasePath + dumpFileName);

        BufferedReader br = null;
        try {
            log.debug("Reading dump file");
            br = new BufferedReader(new FileReader(dumpFile));

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            cellDetailsString = sb.toString();

            TopologyDump dumpTopo = new Gson().fromJson(cellDetailsString, TopologyDump.class);
            CellDetails cellsDb = new CellDetails();

            log.info("dumpTopo.getCellList().size():" + dumpTopo.getCellList().size());
            for (int i = 0; i < dumpTopo.getCellList().size(); i++) {
                Gson g = new Gson();
                String pnt = g.toJson(dumpTopo.getCellList().get(i));
                log.info("Creating Cell:" + pnt);
                log.info("Creating Cell:" + dumpTopo.getCellList().get(i).getCell().getNodeId());

                cellsDb = new CellDetails();
                cellsDb.setNodeId(dumpTopo.getCellList().get(i).getCell().getNodeId());
                cellsDb.setPhysicalCellId(dumpTopo.getCellList().get(i).getCell().getPhysicalCellId());
                cellsDb.setLongitude(dumpTopo.getCellList().get(i).getCell().getLongitude());
                cellsDb.setLatitude(dumpTopo.getCellList().get(i).getCell().getLatitude());
                cellsDb.setServerId(dumpTopo.getCellList().get(i).getCell().getPnfName());
                if (!unassignedServerIds.contains(cellsDb.getServerId())) {
                    unassignedServerIds.add(cellsDb.getServerId());
                }
                cellsDb.setNetworkId(dumpTopo.getCellList().get(i).getCell().getNetworkId());

                double lon = Float.valueOf(dumpTopo.getCellList().get(i).getCell().getLongitude());
                double lat = Float.valueOf(dumpTopo.getCellList().get(i).getCell().getLatitude());

                double xx = (lon - 0) * RansimUtilities.metersDeglon(0);
                double yy = (lat - 0) * RansimUtilities.metersDeglat(0);

                double rad = Math.sqrt(xx * xx + yy * yy);

                if (rad > 0) {
                    double ct = xx / rad;
                    double st = yy / rad;
                    xx = rad * ((ct * Math.cos(0)) + (st * Math.sin(0)));
                    yy = rad * ((st * Math.cos(0)) - (ct * Math.sin(0)));
                }

                cellsDb.setScreenX((float) (xx));
                cellsDb.setScreenY((float) (yy));

                List<String> attachedNoeds = serverIdIpNodeMapping.get(cellsDb.getServerId());
                log.info("Attaching Cell:" + dumpTopo.getCellList().get(i).getCell().getNodeId() + " to "
                        + cellsDb.getServerId());
                if (attachedNoeds == null) {
                    attachedNoeds = new ArrayList<String>();
                }
                attachedNoeds.add(cellsDb.getNodeId());
                serverIdIpNodeMapping.put(cellsDb.getServerId(), attachedNoeds);
                if (attachedNoeds.size() > numberOfCellsPerNcServer) {
                    log.warn("Attaching Cell:" + dumpTopo.getCellList().get(i).getCell().getNodeId() + " to "
                            + cellsDb.getServerId() + ", But it is exceeding numberOfCellsPerNcServer "
                            + numberOfCellsPerNcServer);
                }
                ransimRepo.mergeCellDetails(cellsDb);
                setNetconfServers(cellsDb.getNodeId());
            }

            dumpSessionDetails();

            try {

                for (int i = 0; i < dumpTopo.getCellList().size(); i++) {

                    String cellNodeId = dumpTopo.getCellList().get(i).getCell().getNodeId();

                    // neighbor list with the corresponding node id
                    CellNeighbor neighborList = ransimRepo.getCellNeighbor(cellNodeId);
                    // cell with the corresponding nodeId
                    CellDetails currentCell = ransimRepo.getCellDetail(cellNodeId);

                    Set<NeighborDetails> newCell = new HashSet<NeighborDetails>();

                    if (currentCell != null) {
                        if (neighborList == null) {
                            neighborList = new CellNeighbor();
                            neighborList.setNodeId(cellNodeId);
                        }
                        List<NbrDump> neighboursFromFile = dumpTopo.getCellList().get(i).getNeighbor();
                        log.info("Creating Neighbor for Cell :" + cellNodeId);
                        for (NbrDump a : neighboursFromFile) {
                            String id = a.getNodeId().trim();
                            boolean noHo = Boolean.parseBoolean(a.getBlacklisted().trim());
                            CellDetails neighborCell = ransimRepo.getCellDetail(id);
                            NeighborDetails neighborDetails = new NeighborDetails(
                                    new NeihborId(currentCell.getNodeId(), neighborCell.getNodeId()), noHo);

                            newCell.add(neighborDetails);
                        }

                        neighborList.setNeighborList(newCell);
                        ransimRepo.mergeCellNeighbor(neighborList);
                        rsPciHdlr.setCollisionConfusionFromFile(cellNodeId);

                    }

                }

            } catch (Exception e1) {
                log.error("Exception generateClusterFromFile :", e1);
            }

            try {

                long startTimeSectorNumber = System.currentTimeMillis();
                for (int i = 0; i < dumpTopo.getCellList().size(); i++) {

                    CellData icellData = dumpTopo.getCellList().get(i);
                    CellDetails icell = ransimRepo.getCellDetail(icellData.getCell().getNodeId());
                    int icount = icell.getSectorNumber();
                    if (icount == 0) {
                        log.info("Setting sectorNumber for Cell(i) :" + icell.getNodeId());
                        int jcount = 0;
                        for (int j = (i + 1); j < dumpTopo.getCellList().size(); j++) {

                            CellData jcellData = dumpTopo.getCellList().get(j);
                            if (icellData.getCell().getLatitude().equals(jcellData.getCell().getLatitude())) {
                                if (icellData.getCell().getLongitude().equals(jcellData.getCell().getLongitude())) {

                                    if (icount == 0) {
                                        icount++;
                                        jcount = icount + 1;
                                    }

                                    CellDetails jcell = ransimRepo
                                            .getCellDetail(dumpTopo.getCellList().get(j).getCell().getNodeId());

                                    jcell.setSectorNumber(jcount);
                                    log.info("Setting sectorNumber for Cell(j) :" + jcell.getNodeId() + " icell: "
                                            + icell.getNodeId() + " Sector number: " + jcount);
                                    ransimRepo.mergeCellDetails(jcell);
                                    jcount++;
                                    if (jcount > 3) {
                                        break;
                                    }
                                }
                            }
                        }
                        icell.setSectorNumber(icount);
                        ransimRepo.mergeCellDetails(icell);
                    }

                }

                long endTimeSectorNumber = System.currentTimeMillis();
                log.info("Time taken for setting sector number: " + (endTimeSectorNumber - startTimeSectorNumber));

            } catch (Exception e3) {
                log.error("Exception generateClusterFromFile :", e3);
            }

        } catch (Exception e) {
            log.error("Exception generateClusterFromFile :", e);

        } finally {
            br.close();
        }
    }

    /**
     * The function deletes the cell from the database with the nodeId passed in the
     * arguments. It removes the cell from its neighbor's neighbor list and the
     * netconf server list.
     *
     * @param nodeId node Id of the cell to be deleted.
     * @return returns success or failure message
     */
    public String deleteCellFunction(String nodeId) {
        String result = "failure node dosent exist";
        log.info("deleteCellFunction called with nodeId :" + nodeId);

        try {
            CellDetails deleteCelldetail = ransimRepo.getCellDetail(nodeId);

            CellNeighbor deleteCellNeighbor = ransimRepo.getCellNeighbor(nodeId);

            if (deleteCelldetail != null) {
                if (deleteCellNeighbor != null) {
                    List<CellNeighbor> cellNeighborList = ransimRepo.getCellNeighborList();
                    for (CellNeighbor cellNeighbors : cellNeighborList) {
                        Set<NeighborDetails> currentCellNeighbors =
                                new HashSet<NeighborDetails>(cellNeighbors.getNeighborList());

                        NeihborId deleteNeighborDetail =
                                new NeihborId(cellNeighbors.getNodeId(), deleteCelldetail.getNodeId());

                        if (currentCellNeighbors.contains(deleteNeighborDetail)) {
                            log.info("Deleted Cell is Neighbor of NodeId : " + cellNeighbors.getNodeId());
                            currentCellNeighbors.remove(deleteNeighborDetail);
                            cellNeighbors.setNeighborList(currentCellNeighbors);
                            ransimRepo.mergeCellNeighbor(cellNeighbors);
                        }
                    }

                    deleteCellNeighbor.getNeighborList().clear();
                    ransimRepo.deleteCellNeighbor(deleteCellNeighbor);
                }

                ransimRepo.deleteCellDetails(deleteCelldetail);
                result = "cell has been deleted from the database";
            } else {
                log.info("cell id does not exist");
                result = "failure nodeId dosent exist";
                return result;
            }
        } catch (Exception eu) {
            log.error("Exception deleteCellFunction :", eu);
            result = "Exception in function";
        }
        return result;
    }

    /**
     * Send configuration details to all the netconf server.
     */
    public void sendInitialConfigAll() {
        try {
            dumpSessionDetails();
            List<NetconfServers> ncServers = ransimRepo.getNetconfServersList();
            for (NetconfServers netconfServers : ncServers) {
                String ipPortKey = serverIdIpPortMapping.get(netconfServers.getServerId());
                if (ipPortKey == null || ipPortKey.trim().equals("")) {
                    log.info("No client for " + netconfServers.getServerId());
                    for (String ipPortKeyStr : webSocketSessions.keySet()) {
                        if (!serverIdIpPortMapping.containsValue(ipPortKeyStr)) {
                            serverIdIpPortMapping.put(netconfServers.getServerId(), ipPortKeyStr);
                            ipPortKey = ipPortKeyStr;
                            break;
                        }
                    }
                }
                if (ipPortKey != null && !ipPortKey.trim().equals("")) {
                    Session clSess = webSocketSessions.get(ipPortKey);
                    if (clSess != null) {
                        sendInitialConfig(netconfServers.getServerId());
                        try {
                            String[] agentDetails = ipPortKey.split(":");
                            new RestClient().sendMountRequestToSdnr(netconfServers.getServerId(), sdnrServerIp,
                                    sdnrServerPort, agentDetails[0], agentDetails[1], sdnrServerUserid,
                                    sdnrServerPassword);
                        } catch (Exception ex1) {
                            log.info("Ignoring exception", ex1);
                        }

                    } else {
                        log.info("No session for " + ipPortKey);
                    }
                }
            }
        } catch (Exception eu) {
            log.error("Exception:", eu);
        }
    }

    public void sendRanInitialConfigAll() {
        try {
            List<NearRTRICModel> ncServers = ranSliceConfigService.findAllNearRTRIC();
            for (NearRTRICModel netconfServers : ncServers) {
                String ipPortKey = serverIdIpPortMapping.get(netconfServers.getNearRTRICId());
                if (ipPortKey == null || ipPortKey.trim().equals("")) {
                    log.info("No client for " + netconfServers.getNearRTRICId());
                    for (String ipPortKeyStr : webSocketSessions.keySet()) {
                        if (!serverIdIpPortMapping.containsValue(ipPortKeyStr)) {
                            serverIdIpPortMapping.put(netconfServers.getNearRTRICId().toString(), ipPortKeyStr);
                            ipPortKey = ipPortKeyStr;
                            break;
                        }
                    }
                }
                if (ipPortKey != null && !ipPortKey.trim().equals("")) {
                    Session clSess = webSocketSessions.get(ipPortKey);
                    if (clSess != null) {
                        sendRanInitialConfig(netconfServers.getNearRTRICId().toString());
                        try {
                            String[] agentDetails = ipPortKey.split(":");
                            new RestClient().sendMountRequestToSdnr(netconfServers.getNearRTRICId().toString(),
                                    sdnrServerIp, sdnrServerPort, agentDetails[0], agentDetails[1], sdnrServerUserid,
                                    sdnrServerPassword);
                        } catch (Exception ex1) {
                            log.info("Ignoring exception", ex1);
                        }

                    } else {
                        log.info("No session for " + ipPortKey);
                    }
                }
            }
        } catch (Exception eu) {
            log.error("Exception:", eu);
        }
    }

    /**
     * Sends initial configuration details of the cells for a new netconf server
     * that has been started.
     *
     * @param ipPortKey ip address details of the netconf server
     */
    public void sendInitialConfigForNewAgent(String ipPortKey, String serverId) {
        try {
            dumpSessionDetails();
            if (ipPortKey != null && !ipPortKey.trim().equals("")) {
                if (serverId != null && !serverId.trim().equals("")) {
                    Session clSess = webSocketSessions.get(ipPortKey);
                    if (clSess != null) {
                        String[] agentDetails = ipPortKey.split(":");
                        sendInitialConfig(serverId);
                        new RestClient().sendMountRequestToSdnr(serverId, sdnrServerIp, sdnrServerPort, agentDetails[0],
                                agentDetails[1], sdnrServerUserid, sdnrServerPassword);

                    } else {
                        log.info("No session for " + ipPortKey);
                    }
                } else {
                    log.info("No serverid for " + ipPortKey);
                }
            } else {
                log.info("Invalid ipPortKey " + ipPortKey);
            }
        } catch (Exception eu) {
            log.info("Exception:", eu);
        }
    }

    public void sendRanInitialConfigForNewAgent(String ipPortKey, String serverId) {
        try {
            if (ipPortKey != null && !ipPortKey.trim().equals("")) {
                if (serverId != null && !serverId.trim().equals("")) {
                    Session clSess = webSocketSessions.get(ipPortKey);
                    if (clSess != null) {
                        String[] agentDetails = ipPortKey.split(":");
                        sendRanInitialConfig(serverId);
                        new RestClient().sendMountRequestToSdnr(serverId, sdnrServerIp, sdnrServerPort, agentDetails[0],
                                agentDetails[1], sdnrServerUserid, sdnrServerPassword);
                    } else {
                        log.info("No session for " + ipPortKey);
                    }
                } else {
                    log.info("No serverid for " + ipPortKey);
                }
            } else {
                log.info("Invalid ipPortKey " + ipPortKey);
            }
        } catch (Exception eu) {
            log.info("Exception:", eu);
        }
    }

    public void sendRanInitialConfig(String serverId) {

        try {
            NetconfServers server = ransimRepo.getNetconfServer(serverId);
            log.info("sendInitialConfig: serverId:" + serverId + ", server:" + server);
            if (server == null) {
                return;
            }

            for (NearRTRICModel rtRicModel : rtricModelList) {
		log.info(" rtric model in sendRanInintialConfig is : " + rtricModelList);    
                if (rtRicModel.getNearRTRICId().toString().equals(serverId)) {
                    getInitalConfigTree(rtRicModel, serverId);
                    NetconfClient netconfClient = new NetconfClient("ransim", "admin", "admin", server.getServerId(),
                            server.getIp(), Integer.parseInt(server.getNetconfPort()));

                    netconfClient.editConfig(netconfClient.getInitialNodeSet(rtRicModel, serverId));
                } else {
                    for (Map.Entry<String, List<String>> entry : ricIdFunctionMapping.entrySet()) {
                        for (String value : entry.getValue()) {
                            if (value.equals(serverId)
                                    && rtRicModel.getNearRTRICId().toString().equals(entry.getKey())) {
                                getInitalConfigTree(rtRicModel, serverId);
                                NetconfClient netconfClient =
                                        new NetconfClient("ransim", "admin", "admin", server.getServerId(),
                                                server.getIp(), Integer.parseInt(server.getNetconfPort()));

                                netconfClient.editConfig(netconfClient.getInitialNodeSet(rtRicModel, serverId));
                            }
                        }
                    }
                }
            }

        } catch (Exception eu) {
            log.info("Exception:", eu);
        }

    }

    /**
     * Gets the initial config tree from the database.
     */
    private void getInitalConfigTree(NearRTRICModel rtRicModel, String serverId) {
        RanNetwork ranNetwork = new RanNetwork();
        List<NearRTRIC> nearRTRICList = new ArrayList<>();

        NearRTRIC nearRTRIC = new NearRTRIC();
        nearRTRIC.setIdNearRTRIC(rtRicModel.getNearRTRICId().toString());
        Attributes attributes = new Attributes();
        attributes.setLocationName("Palmdale");
        attributes.setgNBId(rtRicModel.getgNBId().toString());
        nearRTRIC.setAttributes(attributes);
        log.info("RTRIC model is : " + rtRicModel);

        List<GNBCUUPFunction> gNBCUUPFunctionList = new ArrayList<>();
        for (GNBCUUPModel gNBCUUPModel : rtRicModel.getgNBCUUPList()) {
            if (gNBCUUPModel.getgNBCUUPId().toString().equals(serverId)
                    || rtRicModel.getNearRTRICId().toString().equals(serverId)) {
                GNBCUUPFunction gNBCUUPFunction = new GNBCUUPFunction();
                Attributes cUUPattributes = new Attributes();
                cUUPattributes.setgNBCUUPId(gNBCUUPModel.getgNBCUUPId().toString());
                gNBCUUPFunction.setAttributes(cUUPattributes);
                gNBCUUPFunction.setIdGNBCUUPFunction(gNBCUUPModel.getgNBCUUPId().toString());
                gNBCUUPFunctionList.add(gNBCUUPFunction);
            }
        }
        List<GNBDUFunction> gNBDUFunctionList = new ArrayList<>();
        for (GNBDUModel gNBDUModel : rtRicModel.getgNBDUList()) {
            if (gNBDUModel.getgNBDUId().toString().equals(serverId)
                    || rtRicModel.getNearRTRICId().toString().equals(serverId)) {
                GNBDUFunction gNBDUFunction = new GNBDUFunction();
                Attributes dUattributes = new Attributes();
                dUattributes.setgNBId(gNBDUModel.getgNBId().toString());
                gNBDUFunction.setAttributes(dUattributes);
                gNBDUFunction.setIdGNBDUFunction(gNBDUModel.getgNBDUId().toString());
                List<NRCellDU> nRCellDUList = new ArrayList<>();
                for (NRCellDUModel nRCellDUModel : gNBDUModel.getCellDUList()) {
                    NRCellDU nRCellDU = new NRCellDU();
                    DUAttributes nRCellDUattributes = new DUAttributes();
                    nRCellDUattributes.setOperationalState(nRCellDUModel.getOperationalState());
                    nRCellDUattributes.setCellState(nRCellDUModel.getCellState());
                    nRCellDU.setAttributes(nRCellDUattributes);
                    nRCellDU.setIdNRCellDU(nRCellDUModel.getCellLocalId().toString());
                    nRCellDUList.add(nRCellDU);
                }
                gNBDUFunction.setnRCellDU(nRCellDUList);
                gNBDUFunctionList.add(gNBDUFunction);
            }
        }
	/* List<GNBCUCPFunction> gNBCUCPFunctionList = new ArrayList<>();
	for (GNBCUCPModel gnbcucpModel : rtRicModel.getgNBCUCPList()){
	    if (gnbcucpModel.getgNBId().toString().equals(serverId)
		    || rtRicModel.getNearRTRICId().toString().equals(serverId)) {
        GNBCUCPFunction gNBCUCPFunction = new GNBCUCPFunction();
	Attributes cUCPattributes = new Attributes();
	cUCPattributes.setgNBId(gnbcucpModel.getgNBId().toString());
	gNBCUCPFunction.setAttributes(cUCPattributes);
	gNBCUCPFunction.setIdGNBCUCPFunction(gnbcucpModel.getgNBCUName());
	List<NRCellCU> nRCellCUList = new ArrayList<>();
	for (NRCellCUModel nRCellCUModel : gnbcucpModel.getCellCUList()) {
		NRCellCU nRCellCU = new NRCellCU();
		 log.info("gnbcucpModel from DB:"+gnbcucpModel);
		nRCellCU.setIdNRCellCU(nRCellCUModel.getCellLocalId().toString());
		List<NRCellRelation> nRCellRelationList = new ArrayList<NRCellRelation>();
		for(NRCellRelationModel nrCellRelationModel : nRCellCUModel.getNRCellRelationList())
		{
			NRCellRelation nRCellRelation = new NRCellRelation();
			nRCellRelation.setIdNRCellRelation(nrCellRelationModel.getIdNRCellRelation());
			AttributesNRRelation attributesNRRelation = new AttributesNRRelation();
			attributesNRRelation.setNRTCI(nrCellRelationModel.getNRRelationData().getNRTCI());
			attributesNRRelation.setIsHoAllowed(nrCellRelationModel.getNRRelationData().getIsHOAllowed());
			nRCellRelation.setAttributes(attributesNRRelation);
			nRCellRelationList.add(nRCellRelation);
			nRCellCU.setNRCellRelation(nRCellRelationList);
		}
		nRCellCUList.add(nRCellCU);
              }
	      gNBCUCPFunction.setnRCellCU(nRCellCUList);
	      gNBCUCPFunctionList.add(gNBCUCPFunction);
	   }
	} 
	nearRTRIC.setgNBCUCPFunction(gNBCUCPFunctionList); */
        nearRTRIC.setgNBDUFunction(gNBDUFunctionList);
        nearRTRIC.setgNBCUUPFunction(gNBCUUPFunctionList);
        nearRTRICList.add(nearRTRIC);
        ranNetwork.setNearRTRIC(nearRTRICList);

        String ipPortKey = serverIdIpPortMapping.get(serverId);
        log.info("sendInitialConfig: ipPortKey:" + ipPortKey);

        Gson gson = new Gson();
        String jsonStr = gson.toJson(ranNetwork);
        log.info("ConfigTopologyMessage: " + jsonStr);
        Session session = webSocketSessions.get(ipPortKey);
        RansimWebSocketServer.sendSetConfigTopologyMessage(jsonStr, session);
    }

    /**
     * To send the initial configration to the netconf server.
     *
     * @param serverId ip address details of the netconf server
     */
    public void sendInitialConfig(String serverId) {

        try {
            NetconfServers server = ransimRepo.getNetconfServer(serverId);
            log.info("sendInitialConfig: serverId:" + serverId + ", server:" + server);
            if (server == null) {
                return;
            }

            String ipPortKey = serverIdIpPortMapping.get(serverId);

            log.info("sendInitialConfig: ipPortKey:" + ipPortKey);

            List<CellDetails> cellList = new ArrayList<CellDetails>(server.getCells());

            List<Topology> config = new ArrayList<Topology>();

            for (int i = 0; i < server.getCells().size(); i++) {
                Topology cell = new Topology();
                CellDetails currentCell = ransimRepo.getCellDetail(cellList.get(i).getNodeId());
                CellNeighbor neighbor = ransimRepo.getCellNeighbor(cellList.get(i).getNodeId());

                cell.setCellId("" + currentCell.getNodeId());
                cell.setPciId(currentCell.getPhysicalCellId());
                cell.setPnfName(serverId);

                List<Neighbor> nbrList = new ArrayList<Neighbor>();
                Set<NeighborDetails> nbrsDet = neighbor.getNeighborList();
                for (NeighborDetails cellDetails : nbrsDet) {
                    Neighbor nbr = new Neighbor();
                    CellDetails nbrCell = ransimRepo.getCellDetail(cellDetails.getNeigbor().getNeighborCell());
                    nbr.setIdNRCellRelation(nbrCell.getNodeId());
                    nbr.setNRTCI(nbrCell.getPhysicalCellId());
                    nbr.setIdGNBCUCPFunction(nbrCell.getServerId());
		    nbr.setServerId(nbrCell.getServerId());
                    nbr.setPlmnId(nbrCell.getNetworkId());
		    nbr.setIsHOAllowed(cellDetails.isBlacklisted());
                    nbrList.add(nbr);
                }
                cell.setNeighborList(nbrList);
                config.add(i, cell);
            }

            SetConfigTopology topo = new SetConfigTopology();

            topo.setServerId(server.getServerId());
            String uuid = globalNcServerUuidMap.get(server.getServerId());
            if (uuid == null) {
                uuid = getUuid();
                globalNcServerUuidMap.put(server.getServerId(), uuid);
            }
            topo.setUuid(uuid);

            topo.setTopology(config);

            Gson gson = new Gson();
            String jsonStr = gson.toJson(topo);
            log.info("ConfigTopologyMessage: " + jsonStr);
            Session clSess = webSocketSessions.get(ipPortKey);
            Thread.sleep(10000);
            RansimWebSocketServer.sendSetConfigTopologyMessage(jsonStr, clSess);

        } catch (Exception eu) {
            log.info("Exception:", eu);
        }

    }

    private static String getUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * The function alters the database information based on the modifications made
     * in the SDNR.
     *
     * @param message message received from the SDNR
     * @param session sends the session details
     * @param ipPort ip address of the netconf server
     */
    public void handleModifyPciFromSdnr(String message, Session session, String ipPort) {
        log.info("handleModifyPciFromSDNR: message:" + message + " session:" + session + " ipPort:" + ipPort);
        NRCellDU modifyPci = new Gson().fromJson(message, NRCellDU.class);
        log.info("handleModifyPciFromSDNR: modifyPci:" + modifyPci.getIdNRCellDU() + "; pci: " + modifyPci.getAttributes().getNRPCI());
	String source = "Netconf";
	int cellLocalId = Integer.parseInt(modifyPci.getIdNRCellDU());
	org.onap.ransim.rest.api.models.NRCellDU nrCellDU = ransimRepo.getNRCellDUDetail(cellLocalId);
	//CellDetails cd = ransimRepo.getCellDetail(modifyPci.getIdNRCellDU());
	log.info("NRCellDU: " + nrCellDU);
	int nRPCI = nrCellDU.getnRPCI();
	nrCellDU.setnRPCI(modifyPci.getAttributes().getNRPCI());
	ransimRepo.mergeNRCellDU(nrCellDU);
	long pci = Long.valueOf(nRPCI);
        //long pci = cd.getPhysicalCellId();
        //cd.setPhysicalCellId(modifyPci.getAttributes().getNRPCI());
        //ransimRepo.mergeCellDetails(cd);
	rsPciHdlr.updatePciOperationsTable(modifyPci.getIdNRCellDU(), source, pci, modifyPci.getAttributes().getNRPCI());
    }

    public void handleRTRICConfigFromSdnr(String message, Session session, String ipPort) {
        org.onap.ransim.rest.api.models.PLMNInfoModel plmnInfoModel =
                new Gson().fromJson(message, org.onap.ransim.rest.api.models.PLMNInfoModel.class);
        if (!(plmnInfoModel.getConfigData().get(0).getConfigParameter().equalsIgnoreCase("maxNumberOfConns"))
                && !(plmnInfoModel.getConfigData().get(0).getConfigParameter().equalsIgnoreCase("dLThptPerSlice"))
                && !(plmnInfoModel.getConfigData().get(0).getConfigParameter().equalsIgnoreCase("uLThptPerSlice"))) {
            handleIntelligentSlicingDataFromSdnr(message, session, ipPort);
        } else {

            log.info(
                    "handleReconfigureRTRICFromSDNR: message:" + message + " session:" + session + " ipPort:" + ipPort);

            List<GNBDUModel> gNBDUModelList = new ArrayList<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            try {
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<List<GNBDUModel>> response = restTemplate.exchange(
                        "http://" + "localhost" + ":" + "8081" + "/ransim/api/ransim-db/v4/du-list/"
                                + plmnInfoModel.getSnssai(),
                        HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<GNBDUModel>>() {});
                gNBDUModelList = response.getBody();
            } catch (Exception e) {
                log.error("Exception:", e);
            }
            for (GNBDUModel gNBDUModel : gNBDUModelList) {
                plmnInfoModel = new Gson().fromJson(message, org.onap.ransim.rest.api.models.PLMNInfoModel.class);
                String serverId = gNBDUModel.getgNBDUId().toString();
                int duCellCount = gNBDUModel.getCellDUList().size();
                for (ConfigData configData : plmnInfoModel.getConfigData()) {
                    configData.setConfigValue((configData.getConfigValue()) / duCellCount);
                }
                for (org.onap.ransim.rest.web.mapper.NRCellDUModel nrcelldu : gNBDUModel.getCellDUList()) {
                    plmnInfoModel.setNrCellId(nrcelldu.getCellLocalId());
                    plmnInfoModel.setGnbType("gnbdu");
                    plmnInfoModel.setGnbId(gNBDUModel.getgNBDUId().toString());
                    try {
                        log.info("serverId" + serverId);
                        String ipPortKey = serverIdIpPortMapping.get(serverId);
                        String[] ipPortlist = ipPortKey.split(":");
                        ObjectMapper obj = new ObjectMapper();
                        String plmnString = obj.writeValueAsString(plmnInfoModel);
                        handlePLMNInfoUpdateFromSdnr(plmnString, session, ipPort);
                        NetconfClient netconfClient = new NetconfClient("ransim", "admin", "admin", serverId,
                                ipPortlist[0], Integer.parseInt(ipPortlist[1]));
                        netconfClient
                                .editConfig(netconfClient.sendUpdatedPLMNInfoForClosedLoop(plmnInfoModel, serverId));
                    } catch (Exception e) {
                        log.error("Exception occured:", e);
                    }
                }
            }
        }
    }

    public void handleRRMPolicyRatioUpdateFromSdnr(String message, Session session, String ipPort) {
        log.info("handle RRMPolicy update: " + message);
        RRMPolicyRatio rRMPolicyRatio = new Gson().fromJson(message, RRMPolicyRatio.class);
        rrmPolicyRepository.save(rRMPolicyRatio);
    }

    public void handleRRMPolicyRatioDeleteFromSdnr(String message, Session session, String ipPort) {
        log.info("handle RRMPolicyRatio Delete: " + message);
        RRMPolicyRatio rRMPolicyRatio = new Gson().fromJson(message, RRMPolicyRatio.class);
        rrmPolicyRepository.delete(rRMPolicyRatio);
    }

    public void handlePLMNInfoUpdateFromSdnr(String message, Session session, String ipPort) {
        log.info("handle PLMNInfo update: " + message);
        List<PLMNInfo> pLMNInfoList = null;
        org.onap.ransim.rest.api.models.PLMNInfoModel plmnInfoModel =
                new Gson().fromJson(message, org.onap.ransim.rest.api.models.PLMNInfoModel.class);
        if (plmnInfoModel.getConfigData().size() > 0
                && !(plmnInfoModel.getConfigData().get(0).getConfigParameter().equalsIgnoreCase("maxNumberOfConns"))
                && !(plmnInfoModel.getConfigData().get(0).getConfigParameter().equalsIgnoreCase("dLThptPerSlice"))
                && !(plmnInfoModel.getConfigData().get(0).getConfigParameter().equalsIgnoreCase("uLThptPerSlice"))
                && (plmnInfoModel.getGnbType().equalsIgnoreCase("gnbcucp"))) {
            handleIntelligentSlicingDataFromSdnr(message, session, ipPort);
        } else {
            PLMNInfo plmnInfo = new PLMNInfo();
            NSSAIConfig nSSAIConfig = new NSSAIConfig();
            List<ConfigData> configDataList = plmnInfoModel.getConfigData();
            for (ConfigData c : configDataList) {
                if (c.getConfigParameter().equalsIgnoreCase("maxNumberOfConns")) {
                    nSSAIConfig.setMaxNumberOfConns(c.getConfigValue());
                }
                if (c.getConfigParameter().equalsIgnoreCase("dLThptPerSlice")) {
                    nSSAIConfig.setdLThptPerSlice(c.getConfigValue());
                }
                if (c.getConfigParameter().equalsIgnoreCase("uLThptPerSlice")) {
                    nSSAIConfig.setuLThptPerSlice(c.getConfigValue());
                }
            }
            org.onap.ransim.rest.api.models.SNSSAI sNSSAI = new org.onap.ransim.rest.api.models.SNSSAI();
            sNSSAI.setsNSSAI(plmnInfoModel.getSnssai());
            sNSSAI.setStatus(plmnInfoModel.getStatus());
            sNSSAI.setConfigData(nSSAIConfig);
            plmnInfo.setpLMNId(plmnInfoModel.getpLMNId());
            plmnInfo.setsNSSAI(sNSSAI);
            try {
                if (plmnInfoModel.getGnbType().equalsIgnoreCase("gnbdu")) {
                    org.onap.ransim.rest.api.models.NRCellDU nrCellDu =
                            nRCellDURepository.findById(plmnInfoModel.getNrCellId()).get();
                    boolean isAdded = false;
                    if (!(Objects.isNull(nrCellDu.getpLMNInfoList()))) {
                        pLMNInfoList = nrCellDu.getpLMNInfoList();
                        for (org.onap.ransim.rest.api.models.PLMNInfo plmninfo : pLMNInfoList) {
                            if (plmninfo.getsNSSAI().getsNSSAI().equalsIgnoreCase(plmnInfoModel.getSnssai())) {
                                if (Objects.nonNull(plmnInfoModel.getStatus())) {
                                    plmninfo.getsNSSAI().setStatus(plmnInfoModel.getStatus());
                                }
                                if (Objects.nonNull(nSSAIConfig.getdLThptPerSlice())) {
                                    plmninfo.getsNSSAI().getConfigData()
                                            .setdLThptPerSlice(nSSAIConfig.getdLThptPerSlice());
                                }
                                if (Objects.nonNull(nSSAIConfig.getuLThptPerSlice())) {
                                    plmninfo.getsNSSAI().getConfigData()
                                            .setuLThptPerSlice(nSSAIConfig.getuLThptPerSlice());
                                }
                                if (Objects.nonNull(nSSAIConfig.getMaxNumberOfConns())) {
                                    plmninfo.getsNSSAI().getConfigData()
                                            .setMaxNumberOfConns(nSSAIConfig.getMaxNumberOfConns());
                                }
                                isAdded = true;
                            }
                        }
                    } else {
                        pLMNInfoList = new ArrayList<PLMNInfo>();
                    }
                    if (!(isAdded)) {
                        pLMNInfoList.add(plmnInfo);
                    }

                    nrCellDu.setpLMNInfoList(pLMNInfoList);
                    nRCellDURepository.save(nrCellDu);
                } else if (plmnInfoModel.getGnbType().equalsIgnoreCase("gnbcucp")) {
                    org.onap.ransim.rest.api.models.NRCellCU nrCellCu =
                            nRCellCURepository.findById(plmnInfoModel.getNrCellId()).get();
                    boolean isAdded = false;

                    if (!(Objects.isNull(nrCellCu.getpLMNInfoList()))) {
                        pLMNInfoList = nrCellCu.getpLMNInfoList();
                        for (org.onap.ransim.rest.api.models.PLMNInfo plmninfo : pLMNInfoList) {
                            if (plmninfo.getsNSSAI().getsNSSAI().equalsIgnoreCase(plmnInfoModel.getSnssai())) {
                                if (Objects.nonNull(plmnInfoModel.getStatus())) {
                                    plmninfo.getsNSSAI().setStatus(plmnInfoModel.getStatus());
                                }
                                if (Objects.nonNull(nSSAIConfig.getdLThptPerSlice())) {
                                    plmninfo.getsNSSAI().getConfigData()
                                            .setdLThptPerSlice(nSSAIConfig.getdLThptPerSlice());
                                }
                                if (Objects.nonNull(nSSAIConfig.getuLThptPerSlice())) {
                                    plmninfo.getsNSSAI().getConfigData()
                                            .setuLThptPerSlice(nSSAIConfig.getuLThptPerSlice());
                                }
                                if (Objects.nonNull(nSSAIConfig.getMaxNumberOfConns())) {
                                    plmninfo.getsNSSAI().getConfigData()
                                            .setMaxNumberOfConns(nSSAIConfig.getMaxNumberOfConns());
                                }
                                isAdded = true;
                                log.info("data updated");
                            }
                        }

                    } else {
                        pLMNInfoList = new ArrayList<PLMNInfo>();
                    }
                    if (!(isAdded)) {
                        pLMNInfoList.add(plmnInfo);
                    }
                    nrCellCu.setpLMNInfoList(pLMNInfoList);
                    nRCellCURepository.save(nrCellCu);
                } else {
                    org.onap.ransim.rest.api.models.GNBCUUPFunction gNBCUUPFunction =
                            gNBCUUPRepository.findBygNBCUUPId(plmnInfoModel.getGnbId()).get();
                    boolean isAdded = false;
                    if (!(Objects.isNull(gNBCUUPFunction.getpLMNInfoList()))) {
                        pLMNInfoList = gNBCUUPFunction.getpLMNInfoList();
                        for (org.onap.ransim.rest.api.models.PLMNInfo plmninfo : pLMNInfoList) {
                            if (plmninfo.getsNSSAI().getsNSSAI().equalsIgnoreCase(plmnInfoModel.getSnssai())) {
                                if (Objects.nonNull(plmnInfoModel.getStatus())) {
                                    plmninfo.getsNSSAI().setStatus(plmnInfoModel.getStatus());
                                }
                                if (Objects.nonNull(nSSAIConfig.getdLThptPerSlice())) {
                                    plmninfo.getsNSSAI().getConfigData()
                                            .setdLThptPerSlice(nSSAIConfig.getdLThptPerSlice());
                                }
                                if (Objects.nonNull(nSSAIConfig.getuLThptPerSlice())) {
                                    plmninfo.getsNSSAI().getConfigData()
                                            .setuLThptPerSlice(nSSAIConfig.getuLThptPerSlice());
                                }
                                if (Objects.nonNull(nSSAIConfig.getMaxNumberOfConns())) {
                                    plmninfo.getsNSSAI().getConfigData()
                                            .setMaxNumberOfConns(nSSAIConfig.getMaxNumberOfConns());
                                }
                                log.info("data updated");
                                isAdded = true;
                            }
                        }

                    } else {
                        pLMNInfoList = new ArrayList<PLMNInfo>();
                    }
                    if (!(isAdded)) {
                        pLMNInfoList.add(plmnInfo);
                    }
                    gNBCUUPFunction.setpLMNInfoList(pLMNInfoList);
                    gNBCUUPRepository.save(gNBCUUPFunction);
                }
            } catch (NullPointerException nullPointerException) {
                log.error("Record does not exist");
            } catch (Exception e) {
                log.error("Unexpected error while fetching data from database: " + e);
            }
        }
    }

    public void handlePLMNInfoDeleteFromSdnr(String message, Session session, String ipPort) {
        log.info("handle PLMNInfo Delete: " + message);
        List<PLMNInfo> pLMNInfoList = null;
        org.onap.ransim.rest.api.models.PLMNInfoModel plmnInfoModel =
                new Gson().fromJson(message, org.onap.ransim.rest.api.models.PLMNInfoModel.class);
        PLMNInfo plmnInfo = new PLMNInfo();
        NSSAIConfig nSSAIConfig = new NSSAIConfig();
        org.onap.ransim.rest.api.models.SNSSAI sNSSAI = new org.onap.ransim.rest.api.models.SNSSAI();
        sNSSAI.setsNSSAI(plmnInfoModel.getSnssai());
        sNSSAI.setStatus(plmnInfoModel.getStatus());
        sNSSAI.setConfigData(nSSAIConfig);
        plmnInfo.setpLMNId(plmnInfoModel.getpLMNId());
        plmnInfo.setsNSSAI(sNSSAI);
        try {
            if (plmnInfoModel.getGnbType().equalsIgnoreCase("gnbdu")) {
                org.onap.ransim.rest.api.models.NRCellDU nrCellDu =
                        nRCellDURepository.findById(plmnInfoModel.getNrCellId()).get();
                if (!(Objects.isNull(nrCellDu.getpLMNInfoList()))) {
                    pLMNInfoList = nrCellDu.getpLMNInfoList();
                    for (org.onap.ransim.rest.api.models.PLMNInfo plmninfo : pLMNInfoList) {
                        if (plmninfo.getsNSSAI().getsNSSAI().equalsIgnoreCase(plmnInfoModel.getSnssai())) {
                            pLMNInfoList.remove(plmninfo);
                            nrCellDu.setpLMNInfoList(pLMNInfoList);
                            nRCellDURepository.save(nrCellDu);
                        }
                    }
                }

            } else if (plmnInfoModel.getGnbType().equalsIgnoreCase("gnbcucp")) {
                org.onap.ransim.rest.api.models.NRCellCU nrCellCu =
                        nRCellCURepository.findById(plmnInfoModel.getNrCellId()).get();
                if (!(Objects.isNull(nrCellCu.getpLMNInfoList()))) {
                    pLMNInfoList = nrCellCu.getpLMNInfoList();
                    for (org.onap.ransim.rest.api.models.PLMNInfo plmninfo : pLMNInfoList) {
                        if (plmninfo.getsNSSAI().getsNSSAI().equalsIgnoreCase(plmnInfoModel.getSnssai())) {
                            pLMNInfoList.remove(plmninfo);
                            nrCellCu.setpLMNInfoList(pLMNInfoList);
                            nRCellCURepository.save(nrCellCu);
                        }
                    }

                }

            } else {
                org.onap.ransim.rest.api.models.GNBCUUPFunction gNBCUUPFunction =
                        gNBCUUPRepository.findBygNBCUUPId(plmnInfoModel.getGnbId()).get();
                if (!(Objects.isNull(gNBCUUPFunction.getpLMNInfoList()))) {
                    pLMNInfoList = gNBCUUPFunction.getpLMNInfoList();
                    for (org.onap.ransim.rest.api.models.PLMNInfo plmninfo : pLMNInfoList) {
                        if (plmninfo.getsNSSAI().getsNSSAI().equalsIgnoreCase(plmnInfoModel.getSnssai())) {
                            pLMNInfoList.remove(plmninfo);
                            gNBCUUPFunction.setpLMNInfoList(pLMNInfoList);
                            gNBCUUPRepository.save(gNBCUUPFunction);
                        }
                    }

                }
            }
        } catch (NullPointerException nullPointerException) {
            log.error("Record does not exist");
        } catch (Exception e) {
            log.error("Unexpected error while fetching data from database: " + e);
        }
    }

    public void handleSliceProfileUpdateFromSdnr(String message, Session session, String ipPort) {
        log.info("handle SliceProfile update: " + message);
        SliceProfile sliceProfile = new Gson().fromJson(message, SliceProfile.class);
        sliceProfileRepository.save(sliceProfile);
    }

    public void handleSliceProfileDeleteFromSdnr(String message, Session session, String ipPort) {
        log.info("handle SliceProfile delete: " + message);
        SliceProfile sliceProfile = new Gson().fromJson(message, SliceProfile.class);
        sliceProfileRepository.delete(sliceProfile);
    }

    public void handleIntelligentSlicingDataFromSdnr(String message, Session session, String ipPort) {

        log.info("handle Intelligent Slicing data From Sdnr : " + message);
        org.onap.ransim.rest.api.models.PLMNInfoModel plmnInfoModel =
                new Gson().fromJson(message, org.onap.ransim.rest.api.models.PLMNInfoModel.class);
        plmnInfoModel.getConfigData().get(0).setConfigParameter("maxNumberOfConns");
        log.info("plmnInfoModel: " + plmnInfoModel.toString());
        List<GNBCUCPModel> gNBCUCPModelList = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        try {
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<List<GNBCUCPModel>> response = restTemplate.exchange(
                    "http://" + "localhost" + ":" + "8081" + "/ransim/api/ransim-db/v4/cucp-list/", HttpMethod.GET,
                    requestEntity, new ParameterizedTypeReference<List<GNBCUCPModel>>() {});
            gNBCUCPModelList = response.getBody();
            gNBCUCPModelList.forEach(gnb -> {
                GNBCUCPModel gNBCUCPModel = gnb;
                log.info("gNBCUCPModel: " + gNBCUCPModel.toString());
                List<NRCellCUModel> nRCellCUModelList = gnb.getCellCUList();
                for (NRCellCUModel nrcell : nRCellCUModelList) {
                    if (nrcell.getCellLocalId().equals(plmnInfoModel.getNrCellId())) {
                        plmnInfoModel.setGnbId(gnb.getgNBCUName());
                        List<PLMNInfoModel> pLMNInfoModelList = nrcell.getpLMNInfoList();
                        pLMNInfoModelList.forEach(plmn -> {
                            if (plmn.getpLMNId().equalsIgnoreCase(plmnInfoModel.getpLMNId())) {
                                if (plmn.getsNSSAI().getsNSSAI().equalsIgnoreCase(plmnInfoModel.getSnssai())) {
                                    if (Objects.isNull(plmnInfoModel.getStatus())) {
                                        plmnInfoModel.setStatus(plmn.getsNSSAI().getStatus());
                                        log.info("plmn status set");
                                    }
                                }
                            }
                        });
                        String serverId = plmnInfoModel.getGnbId();
                        String ipPortKey = serverIdIpPortMapping.get(serverId);
                        String[] ipPortlist = ipPortKey.split(":");
                        NetconfClient netconfClient = new NetconfClient("ransim", "admin", "admin", serverId,
                                ipPortlist[0], Integer.parseInt(ipPortlist[1]));

                        netconfClient.editConfig(
                                netconfClient.sendUpdatedPLMNInfoForIntelligentSlicing(plmnInfoModel, serverId));
                        log.info("Intelligent Slicing Data sent successfully : ");
                        try {
                            ObjectMapper obj = new ObjectMapper();
                            String plmnString = obj.writeValueAsString(plmnInfoModel);
                            handlePLMNInfoUpdateFromSdnr(plmnString, session, ipPort);
                        } catch (Exception e) {
                            log.info("Exception while parsing:", e);
                        }

                        break;
                    }
                }
            });
        } catch (Exception e) {
            log.info("Exception:", e);
        }
    }

    /**
     * The function alters the database information based on the modifications made
     * in the SDNR.
     *
     * @param message message received from the SDNR
     * @param session sends the session details
     * @param ipPort ip address of the netconf server
     */
    public void handleModifyNeighborFromSdnr(String message, Session session, String ipPort) {
        log.info("handleModifyAnrFromSDNR: message:" + message + " session:" + session + " ipPort:" + ipPort);
        ModifyNeighbor modifyNeighbor = new Gson().fromJson(message, ModifyNeighbor.class);
	log.info("handleModifyAnrFromSDNR: modifyNeighbor:" + modifyNeighbor.getIdNRCellCU());
	Integer cellLocalId = Integer.parseInt(modifyNeighbor.getIdNRCellCU());
	Integer idNRCellRel = Integer.parseInt(modifyNeighbor.getIdNRCellRelation());
	org.onap.ransim.rest.api.models.NRCellCU nrCellCU = ransimRepo.getNRCellCUDetail(cellLocalId);
	NRCellRelation nRCellRel = new NRCellRelation();
	nRCellRel.setIdNRCellRelation(idNRCellRel);
	nRCellRel.setnRTCI(modifyNeighbor.getAttributes().getNRTCI());
	nRCellRel.setisHOAllowed(modifyNeighbor.getAttributes().getIsHOAllowed());
	nRCellRel.setCellLocalId(nrCellCU);
        List<String> cellList = new ArrayList<String>();
        cellList.add(modifyNeighbor.getIdNRCellCU());
	cellList.add(modifyNeighbor.getIdNRCellRelation());
        String nbrsAdd = "";
        String nbrsDel = "";
        String source = "Netconf";

	NRCellRelation nRCellRel1 = ransimRepo.getNRCellRelation(idNRCellRel,nrCellCU);
	if(nRCellRel1!=null){
		nRCellRel1.setisHOAllowed(modifyNeighbor.getAttributes().getIsHOAllowed());
		ransimRepo.mergeNRCellRel(nRCellRel1);
		if(!modifyNeighbor.getAttributes().getIsHOAllowed()) {
			nbrsDel = modifyNeighbor.getIdNRCellRelation();
		}
	} else {
		nbrsAdd = modifyNeighbor.getIdNRCellRelation();
		ransimRepo.mergeNRCellRel(nRCellRel);
	}

        for (String cl : cellList) {
            rsPciHdlr.setCollisionConfusionFromFile(cl);
        }

	rsPciHdlr.updateNbrsOperationsTable(modifyNeighbor.getIdNRCellCU(), source, nbrsAdd, nbrsDel);
    }

    /**
     * The function sends the modification made in the GUI to the netconf server.
     *
     * @param cellId node Id of the cell which was modified
     * @param pciId PCI number of the cell which was modified
     */
    public void handleModifyPciFromGui(String cellId, long pciId) {
        log.info("handleModifyPciFromGUI: cellId:" + cellId + " pciId:" + pciId);

        try {
            CellDetails currentCell = ransimRepo.getCellDetail(cellId);
            CellNeighbor neighborList = ransimRepo.getCellNeighbor(cellId);
            List<Neighbor> nbrList = new ArrayList<Neighbor>();
            Iterator<NeighborDetails> iter = neighborList.getNeighborList().iterator();
            while (iter.hasNext()) {
                NeighborDetails nbCell = iter.next();
                Neighbor nbr = new Neighbor();
                CellDetails nbrCell = ransimRepo.getCellDetail(nbCell.getNeigbor().getNeighborCell());

		nbr.setIdNRCellRelation(nbrCell.getNodeId());
                nbr.setNRTCI(nbrCell.getPhysicalCellId());
                nbr.setIdGNBCUCPFunction(nbrCell.getNodeName());
                nbr.setServerId(nbrCell.getServerId());
                nbr.setPlmnId(nbrCell.getNetworkId());
                nbrList.add(nbr);
            }

            String pnfName = currentCell.getServerId();
            String ipPort = serverIdIpPortMapping.get(pnfName);
            log.info("handleModifyPciFromGui:ipPort >>>>>>> " + ipPort);

            if (ipPort != null && !ipPort.trim().equals("")) {

                String[] ipPortArr = ipPort.split(":");
                Topology oneCell = new Topology(pnfName, pciId, cellId, nbrList);
                UpdateCell updatedPci = new UpdateCell(currentCell.getServerId(), ipPortArr[0], ipPortArr[1], oneCell);
                Gson gson = new Gson();
                String jsonStr = gson.toJson(updatedPci);
                if (ipPort != null && !ipPort.trim().equals("")) {
                    Session clSess = webSocketSessions.get(ipPort);
                    if (clSess != null) {
                        RansimWebSocketServer.sendUpdateCellMessage(jsonStr, clSess);
                        log.info("handleModifyPciFromGui, message: " + jsonStr);
                    } else {
                        log.info("No client session for " + ipPort);
                    }
                } else {
                    log.info("No client for " + currentCell.getServerId());
                }
            }

        } catch (Exception eu) {

            log.error("Exception:", eu);
        }
    }

    /**
     * The function unmounts the connection with SDNR.
     *
     * @return returns null value
     */
    public String stopAllSimulation() {
        try {
            List<NetconfServers> ncServers = ransimRepo.getNetconfServersList();
            for (NetconfServers netconfServers : ncServers) {
                try {
                    log.info("Unmount " + netconfServers.getServerId());
                    new RestClient().sendUnmountRequestToSdnr(netconfServers.getServerId(), sdnrServerIp,
                            sdnrServerPort, sdnrServerUserid, sdnrServerPassword);
                } catch (Exception e) {
                    log.error("Ignore Exception:", e);
                }
                serverIdIpNodeMapping.clear();
            }
            return "Netconf servers unmounted.";
        } catch (Exception eu) {

            log.error("Exception:", eu);
            return "Error";
        }
    }

    /**
     * Used to dump session details.
     */
    synchronized public static void dumpSessionDetails() {

        try {

            log.info("serverIdIpPortMapping.size:" + serverIdIpPortMapping.size() + "webSocketSessions.size"
                    + webSocketSessions.size());
            for (String key : serverIdIpPortMapping.keySet()) {
                String val = serverIdIpPortMapping.get(key);
                Session sess = webSocketSessions.get(val);
                log.info("ServerId:" + key + " IpPort:" + val + " Session:" + sess);
            }
            for (String serverId : unassignedServerIds) {
                log.info("Unassigned ServerId:" + serverId);
            }
            for (String serverId : serverIdIpPortMapping.keySet()) {
                List<String> attachedNoeds = serverIdIpNodeMapping.get(serverId);
                if (attachedNoeds != null) {
                    log.info("ServerId:" + serverId + " attachedNoeds.size:" + attachedNoeds.size() + " nodes:"
                            + attachedNoeds.toArray());
                } else {
                    log.info("ServerId:" + serverId + " attachedNoeds:" + null);
                }
            }
        } catch (Exception e) {
            log.info("Exception:", e);
        }
    }

}


class KeepWebsockAliveThread extends Thread {
    static Logger log = Logger.getLogger(KeepWebsockAliveThread.class.getName());
    RansimControllerServices rsCtrlrServices = null;

    KeepWebsockAliveThread(RansimControllerServices ctrlr) {
        rsCtrlrServices = ctrlr;
    }

    @Override
    public void run() {
        log.info("Inside KeepWebsockAliveThread run method");
        while (true) {
            for (String ipPort : RansimControllerServices.webSocketSessions.keySet()) {
                try {
                    Session sess = RansimControllerServices.webSocketSessions.get(ipPort);
                    RansimWebSocketServer.sendPingMessage(sess);
                    log.debug("Sent ping message to Client ipPort:" + ipPort);
                } catch (Exception ex1) {
                }
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                log.error("Thread interrupted");
            }
        }
    }
}
