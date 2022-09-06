/*
 * ============LICENSE_START=======================================================
 * Ran Simulator Controller
 * ================================================================================
 * Copyright (C) 2020-2021 Wipro Limited.
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

package org.onap.ransim.netconf;

import com.tailf.jnc.Device;
import com.tailf.jnc.DeviceUser;
import com.tailf.jnc.Element;
import com.tailf.jnc.JNCException;
import com.tailf.jnc.NetconfSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.io.IOException;
import java.net.ConnectException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.apache.log4j.Logger;
import org.onap.ransim.rest.api.models.PLMNInfoModel;
import org.onap.ransim.rest.api.models.GNBCUCPFunction;
import org.onap.ransim.rest.web.mapper.NRRelationData;
import org.onap.ransim.rest.web.mapper.GNBCUCPModel;
import org.onap.ransim.rest.web.mapper.GNBCUUPModel;
import org.onap.ransim.rest.web.mapper.GNBDUModel;
import org.onap.ransim.rest.web.mapper.NRCellCUModel;
import org.onap.ransim.rest.web.mapper.NRCellDUModel;
import org.onap.ransim.rest.web.mapper.NRCellRelationModel;
import org.onap.ransim.rest.web.mapper.NearRTRICModel;
import org.onap.ransim.websocket.model.ConfigData;
import org.onap.ransim.rest.api.services.RANSliceConfigService;
import org.onap.ransim.websocket.model.LTECell;
import org.onap.ransim.websocket.model.PayloadOutput;
import org.springframework.beans.factory.annotation.Autowired;

public class NetconfClient {

    static Logger log = Logger.getLogger(NetconfClient.class.getName());
    static List<NRCellRelationModel> relationModelList = Collections.synchronizedList(new ArrayList<>());

    private String emsUserName;
    private Device device;

    @Autowired
    RANSliceConfigService ranSliceConfigService;

    public NetconfClient(Device device) {
        this.device = device;
    }

    public NetconfClient(String emsUserName, String username, String password, String serverName, String ip, int port) {
        DeviceUser deviceUser = new DeviceUser(emsUserName, username, password);
        this.emsUserName = emsUserName;
        this.device = new Device(serverName, deviceUser, ip, port);
    }

    public Element getInitialNodeSet(NearRTRICModel rtRicModel, String serverId) {
        try {

            Element ranNetworkElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/ran-network");
            Element nearRTRICElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/NearRTRIC");
            Element idNearRTRICElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/idNearRTRIC");
            idNearRTRICElement.setValue(rtRicModel.getNearRTRICId());
            Element sapElement = Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/sAP");
            Element hostElement = Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/host");
            hostElement.setValue("localhost");
            Element portElement = Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/port");
            portElement.setValue("8080");
            sapElement.addChild(hostElement);
            sapElement.addChild(portElement);
            NearRTRICModel nRICmodel = new NearRTRICModel();
	    org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();
	    try (FileReader reader = new FileReader("/tmp/ransim-install/config/ransimdata.json")) {
		    // Read JSON file
		    Object obj = jsonParser.parse(reader);
		    JSONArray List = (JSONArray) obj;
		    List<GNBCUCPModel> savedGNBModelList = new ArrayList<GNBCUCPModel>();
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
                                    NRCellCUModel nrCellCUModel = new NRCellCUModel();
				    nrCellCUModel.setCellLocalId(cellLocalId.intValue());
				    nrCellCUModel.setNRCellRelation(nrCellRelationModelList);
				    nRCellCUModelList.add(nrCellCUModel);
				    log.info(" saved nrcellcu in netconfclent is : " + nrCellCUModel);
			    }
		            GNBCUCPModel SavedgNBCUCPModel = new GNBCUCPModel();	    
			    SavedgNBCUCPModel.setgNBCUName(gNBCUName);
			    SavedgNBCUCPModel.setgNBId(gNBId.intValue());
			    SavedgNBCUCPModel.setgNBIdLength(gNBIdLength.intValue());
			    SavedgNBCUCPModel.setpLMNId(pLMNId);
			    SavedgNBCUCPModel.setnFType(nFType);
			    SavedgNBCUCPModel.setNearRTRICId(nearRTRICId.intValue());
			    SavedgNBCUCPModel.setCellCUList(nRCellCUModelList);
			    savedGNBModelList.add(SavedgNBCUCPModel);
			    log.info(" saved gnbcucp in netconfclent is : " + SavedgNBCUCPModel);
		    }
		    nRICmodel.setgNBCUCPList(savedGNBModelList);
		    nRICmodel.setNearRTRICId(Integer.parseInt(serverId));
	    }
	    catch (Exception e) {
		    log.error("Properties file error", e);
	    }
            for (GNBCUUPModel gNBCUUPModel : rtRicModel.getgNBCUUPList()) {
                if (gNBCUUPModel.getgNBCUUPId().toString().equals(serverId)
                        || rtRicModel.getNearRTRICId().toString().equals(serverId)) {
                    Element gNBCUUPFunctionElement =
                            Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/GNBCUUPFunction");
                    Element idGNBCUUPFunctionElement =
                            Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/idGNBCUUPFunction");
                    idGNBCUUPFunctionElement.setValue(gNBCUUPModel.getgNBCUUPId().toString());
                    Element attributesElement =
                            Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/attributes");
                    Element gnBIdElement =
                            Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/gNBId");
                    gnBIdElement.setValue(gNBCUUPModel.getgNBId());
                    attributesElement.addChild(gnBIdElement);
                    attributesElement.addChild(sapElement);
                    gNBCUUPFunctionElement.addChild(idGNBCUUPFunctionElement);
                    gNBCUUPFunctionElement.addChild(attributesElement);
                    nearRTRICElement.addChild(gNBCUUPFunctionElement);
                }
            }
            for (GNBCUCPModel gNBCUCPModel : nRICmodel.getgNBCUCPList()) {
                if (gNBCUCPModel.getgNBCUName().equals(serverId)
                        || rtRicModel.getNearRTRICId().toString().equals(serverId)) {
                    Element gNBCUCPFunctionElement =
                            Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/GNBCUCPFunction");
                    Element idGNBCUCPFunctionElement =
                            Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/idGNBCUCPFunction");
                    idGNBCUCPFunctionElement.setValue(gNBCUCPModel.getgNBCUName());
                    Element attributesElement =
                            Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/attributes");
                    Element gNBCUNameElement =
                            Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/gNBCUName");
                    gNBCUNameElement.setValue(gNBCUCPModel.getgNBCUName());
                    Element gnBIdElement =
                            Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/gNBId");
                    gnBIdElement.setValue(gNBCUCPModel.getgNBId());
                    Element gNBIdLengthElement =
                            Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/gNBIdLength");
                    gNBIdLengthElement.setValue(gNBCUCPModel.getgNBIdLength());
                    attributesElement.addChild(gNBCUNameElement);
                    attributesElement.addChild(gnBIdElement);
                    attributesElement.addChild(gNBIdLengthElement);
                    attributesElement.addChild(sapElement);
                    for (NRCellCUModel nRCellCUModel : gNBCUCPModel.getCellCUList()) {
			Element nRCellCUElement =
				Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/NRCellCU");
			Element idNRCellCUElement =
				Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/idNRCellCU");
			idNRCellCUElement.setValue(nRCellCUModel.getCellLocalId());
			Element nRCellattributesElement =
				Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/attributes");
			Element cellLocalIdElement =
				Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/cellLocalId");
			cellLocalIdElement.setValue(nRCellCUModel.getCellLocalId());
			nRCellattributesElement.addChild(cellLocalIdElement);
			nRCellattributesElement.addChild(sapElement);
			nRCellCUElement.addChild(idNRCellCUElement);
			nRCellCUElement.addChild(nRCellattributesElement);
			for (NRCellRelationModel nRCellRelationModel : nRCellCUModel.getNRCellRelationList()) {
				Element nRCellRelationElement =
					Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/NRCellRelation");
				Element idNRCellRelationElement =
					Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/idNRCellRelation");
				idNRCellRelationElement.setValue(nRCellRelationModel.getIdNRCellRelation());
				Element nRCellRelationattributesElement =
					Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/attributes");
				Element nRTCIElement =
					Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/nRTCI");
				nRTCIElement.setValue(nRCellRelationModel.getNRRelationData().getNRTCI());
				Element isHOAllowedElement =
					Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/isHOAllowed");
				isHOAllowedElement.setValue(nRCellRelationModel.getNRRelationData().getIsHOAllowed());
				nRCellRelationattributesElement.addChild(nRTCIElement);
				nRCellRelationattributesElement.addChild(isHOAllowedElement);
				nRCellRelationattributesElement.addChild(sapElement);
				nRCellRelationElement.addChild(idNRCellRelationElement);
				nRCellRelationElement.addChild(nRCellRelationattributesElement);
				nRCellCUElement.addChild(nRCellRelationElement);
			}      
                        gNBCUCPFunctionElement.addChild(nRCellCUElement);
                    }
                    gNBCUCPFunctionElement.addChild(idGNBCUCPFunctionElement);
                    gNBCUCPFunctionElement.addChild(attributesElement);
                    nearRTRICElement.addChild(gNBCUCPFunctionElement);
                }
            }
            for (GNBDUModel gNBDUModel : rtRicModel.getgNBDUList()) {
                if (gNBDUModel.getgNBDUId().toString().equals(serverId)
                        || rtRicModel.getNearRTRICId().toString().equals(serverId)) {
                    Element gNBDUFunctionElement =
                            Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/GNBDUFunction");
                    Element idGNBDUFunctionElement =
                            Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/idGNBDUFunction");
                    idGNBDUFunctionElement.setValue(gNBDUModel.getgNBDUId());
                    Element attributesElement =
                            Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/attributes");
                    Element gNBDUNameElement =
                            Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/gNBDUName");
                    gNBDUNameElement.setValue(gNBDUModel.getgNBDUName());
                    Element gNBIdLengthElement =
                            Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/gNBIdLength");
                    gNBIdLengthElement.setValue(gNBDUModel.getgNBIdLength());
                    Element gNBDUIdElement =
                            Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/gNBDUId");
                    gNBDUIdElement.setValue(gNBDUModel.getgNBDUId());
                    attributesElement.addChild(gNBIdLengthElement);
                    attributesElement.addChild(gNBDUNameElement);
                    attributesElement.addChild(gNBDUIdElement);
                    attributesElement.addChild(sapElement);
                    for (NRCellDUModel nRCellDUModel : gNBDUModel.getCellDUList()) {
                        Element nRCellDUElement =
                                Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/NRCellDU");
                        Element idNRCellDUElement =
                                Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/idNRCellDU");
                        idNRCellDUElement.setValue(nRCellDUModel.getCellLocalId());
                        Element nRCellattributesElement =
                                Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/attributes");
                        Element cellLocalIdElement =
                                Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/cellLocalId");
                        cellLocalIdElement.setValue(nRCellDUModel.getCellLocalId());
                        Element nRSectorCarrierRefElement = Element
                                .create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/nRSectorCarrierRef");
                        nRSectorCarrierRefElement.setValue("OU=Sales");
                        Element administrativeStateElement = Element
                                .create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/administrativeState");
                        administrativeStateElement.setValue(nRCellDUModel.getAdministrativeState());
                        Element nRPCIElement =
                                Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/nRPCI");
                        nRPCIElement.setValue(nRCellDUModel.getnRPCI());
                        Element nRTACElement =
                                Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/nRTAC");
                        nRTACElement.setValue(nRCellDUModel.getnRTAC());
                        nRCellattributesElement.addChild(cellLocalIdElement);
                        nRCellattributesElement.addChild(sapElement);
                        nRCellattributesElement.addChild(nRSectorCarrierRefElement);
                        nRCellattributesElement.addChild(administrativeStateElement);
                        nRCellattributesElement.addChild(nRPCIElement);
                        nRCellattributesElement.addChild(nRTACElement);
                        nRCellDUElement.addChild(idNRCellDUElement);
                        nRCellDUElement.addChild(nRCellattributesElement);
                        gNBDUFunctionElement.addChild(nRCellDUElement);
                    }
                    gNBDUFunctionElement.addChild(idGNBDUFunctionElement);
                    gNBDUFunctionElement.addChild(attributesElement);
                    nearRTRICElement.addChild(gNBDUFunctionElement);
                }
            }

            nearRTRICElement.addChild(idNearRTRICElement);
            ranNetworkElement.addChild(nearRTRICElement);

            return ranNetworkElement;

        } catch (JNCException e) {
            log.error("Exception occured during NodeSet creation {}", e);
            return null;
        }

    }

    public Element sendUpdatedPLMNInfoForClosedLoop(PLMNInfoModel pLMNInfoModel, String serverId) {
        try {
            log.info("sending PLMNInfo data to netconf server");
            Element ranNetworkElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/ran-network");
            Element nearRTRICElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/NearRTRIC");
            Element idNearRTRICElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/idNearRTRIC");
            idNearRTRICElement.setValue(pLMNInfoModel.getNearrtricid());

            Element gNBDUFunctionElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/GNBDUFunction");
            Element idGNBDUFunctionElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/idGNBDUFunction");
            idGNBDUFunctionElement.setValue(pLMNInfoModel.getGnbId());

            Element nRCellDUElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/NRCellDU");
            Element idNRCellDUElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/idNRCellDU");
            idNRCellDUElement.setValue(pLMNInfoModel.getNrCellId());
            Element nRCellattributesElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/attributes");
            Element pLMNInfoListElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/pLMNInfoList");
            Element mccElement = Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/mcc");
            Element mncElement = Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/mnc");
            String[] plmn = pLMNInfoModel.getpLMNId().split("-");
            mccElement.setValue(plmn[0]);
            mncElement.setValue(plmn[1]);

            Element sNSSAIListElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/sNSSAIList");
            Element sNssaiElement = Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/sNssai");
            sNssaiElement.setValue(pLMNInfoModel.getSnssai());

            List<ConfigData> configDataList = pLMNInfoModel.getConfigData();
            for (ConfigData c : configDataList) {
                Element configDataElement =
                        Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/configData");
                Element configParameterElement =
                        Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/configParameter");
                configParameterElement.setValue(c.getConfigParameter());
                Element configValueElement =
                        Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/configValue");
                configValueElement.setValue(c.getConfigValue());
                configDataElement.markMerge();
                configDataElement.addChild(configParameterElement);
                configDataElement.addChild(configValueElement);
                sNSSAIListElement.addChild(configDataElement);
            }

            sNSSAIListElement.markMerge();
            sNSSAIListElement.addChild(sNssaiElement);

            pLMNInfoListElement.addChild(mccElement);
            pLMNInfoListElement.addChild(mncElement);
            pLMNInfoListElement.addChild(sNSSAIListElement);

            nRCellattributesElement.addChild(pLMNInfoListElement);
            nRCellDUElement.addChild(idNRCellDUElement);
            nRCellDUElement.addChild(nRCellattributesElement);

            gNBDUFunctionElement.addChild(idGNBDUFunctionElement);
            gNBDUFunctionElement.addChild(nRCellDUElement);

            nearRTRICElement.addChild(idNearRTRICElement);
            nearRTRICElement.addChild(gNBDUFunctionElement);

            ranNetworkElement.addChild(nearRTRICElement);
            ranNetworkElement.markMerge();
            return ranNetworkElement;

        } catch (JNCException e) {
            log.error("Exception occured during NodeSet creation {}", e);
            return null;
        }

    }

    public Element sendUpdatedPLMNInfoForIntelligentSlicing(PLMNInfoModel pLMNInfoModel, String serverId) {
        try {
            log.info("sending PLMNInfo data to netconf server");
            Element ranNetworkElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/ran-network");
            Element nearRTRICElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/NearRTRIC");
            Element idNearRTRICElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/idNearRTRIC");
            idNearRTRICElement.setValue(pLMNInfoModel.getNearrtricid());

            Element gNBCUCPFunctionElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/GNBCUCPFunction");
            Element idGNBCUCPFunctionElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/idGNBCUCPFunction");
            idGNBCUCPFunctionElement.setValue(pLMNInfoModel.getGnbId());

            Element nRCellCUElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/NRCellCU");
            Element idNRCellCUElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/idNRCellCU");
            idNRCellCUElement.setValue(pLMNInfoModel.getNrCellId());
            Element nRCellattributesElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/attributes");
            Element pLMNInfoListElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/pLMNInfoList");
            Element mccElement = Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/mcc");
            Element mncElement = Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/mnc");
            String[] plmn = pLMNInfoModel.getpLMNId().split("-");
            mccElement.setValue(plmn[0]);
            mncElement.setValue(plmn[1]);

            Element sNSSAIListElement =
                    Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/sNSSAIList");
            Element sNssaiElement = Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/sNssai");
            sNssaiElement.setValue(pLMNInfoModel.getSnssai());

            List<ConfigData> configDataList = pLMNInfoModel.getConfigData();
            for (ConfigData c : configDataList) {
                Element configDataElement =
                        Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/configData");
                Element configParameterElement =
                        Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/configParameter");
                configParameterElement.setValue(c.getConfigParameter());
                Element configValueElement =
                        Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/configValue");
                configValueElement.setValue(c.getConfigValue());
                configDataElement.addChild(configParameterElement);
                configDataElement.addChild(configValueElement);
                sNSSAIListElement.addChild(configDataElement);
            }

            sNSSAIListElement.markReplace();
            sNSSAIListElement.addChild(sNssaiElement);

            pLMNInfoListElement.addChild(mccElement);
            pLMNInfoListElement.addChild(mncElement);
            pLMNInfoListElement.addChild(sNSSAIListElement);

            nRCellattributesElement.addChild(pLMNInfoListElement);
            nRCellCUElement.addChild(idNRCellCUElement);
            nRCellCUElement.addChild(nRCellattributesElement);

            gNBCUCPFunctionElement.addChild(idGNBCUCPFunctionElement);
            gNBCUCPFunctionElement.addChild(nRCellCUElement);

            nearRTRICElement.addChild(idNearRTRICElement);
            nearRTRICElement.addChild(gNBCUCPFunctionElement);

            ranNetworkElement.addChild(nearRTRICElement);
            ranNetworkElement.markMerge();
            return ranNetworkElement;

        } catch (JNCException e) {
            log.error("Exception occured during NodeSet creation {}", e);
            return null;
        }

    }

    public void editConfig(Element initialConfig) {
        try {
            Boolean connected = false;
            while (!connected) {
                try {
                    Thread.sleep(6000);
                    device.connect(emsUserName);
                    connected = true;
                } catch (ConnectException e) {
                    log.error("Device is not up yet... retrying after 3s");
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

            }

            device.newSession("cfg");
            log.info(initialConfig.toXMLString());
            device.getSession("cfg").editConfig(NetconfSession.CANDIDATE, initialConfig);
            device.getSession("cfg").commit();
            log.info("Initial configuration set");
        } catch (IOException | JNCException e) {
            log.error("Exception occured during edit config {}", e);
        }
    }



	private Element updateNeighbourListCUCP(PayloadOutput neighbourListinUse, String serverId) {
		// TODO Auto-generated method stub
		
		
		try {
		log.info("update Neighbourlist data to netconf server");
        Element ranNetworkElement =
                Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/ran-network");
        Element nearRTRICElement =
                Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/NearRTRIC");
        Element idNearRTRICElement =
                Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/idNearRTRIC");
        idNearRTRICElement.setValue(neighbourListinUse.getConfigurations().get(0).getData().getRicId());
        Element gNBCUCPFunctionElement =
                Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/GNBCUCPFunction");
        Element idGNBCUCPFunctionElement =
                Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/idGNBCUCPFunction");
        idGNBCUCPFunctionElement.setValue(neighbourListinUse.getConfigurations().get(0).getData().getFAPService().getIdNRCellCU());

        Element sapElement = Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/sAP");
        Element hostElement = Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/host");
        hostElement.setValue("localhost");
        Element portElement = Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/port");
        portElement.setValue("8080");
        sapElement.addChild(hostElement);
        sapElement.addChild(portElement);
        
        Element attributesElement =
                Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/attributes");
        Element gNBCUNameElement =
                Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/gNBCUName");
        gNBCUNameElement.setValue(neighbourListinUse.getConfigurations().get(0).getData().getFAPService().getIdNRCellCU());
        Element gnBIdElement =
                Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/gNBId");
        gnBIdElement.setValue(32);
        Element gNBIdLengthElement =
                Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/gNBIdLength");
        gNBIdLengthElement.setValue(98763);
        attributesElement.addChild(gNBCUNameElement);
        attributesElement.addChild(gnBIdElement);
        attributesElement.addChild(gNBIdLengthElement);
        attributesElement.addChild(sapElement);
        
        
        Element nRCellCUElement =
                Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/NRCellCU");
        Element idNRCellCUElement =
                Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/idNRCellCU");
        
        String localcellID=neighbourListinUse.getConfigurations().get(0).getData().getFAPService().getCellConfig().getLte().getRan().getCommon().getCellIdentity();
        idNRCellCUElement.setValue(localcellID);
        Element nRCellattributesElement =
                Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/attributes");
        Element cellLocalIdElement =
				Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/cellLocalId");
			cellLocalIdElement.setValue(localcellID);
			nRCellattributesElement.addChild(cellLocalIdElement);
			nRCellattributesElement.addChild(sapElement);
        nRCellCUElement.addChild(idNRCellCUElement);
        nRCellCUElement.addChild(nRCellattributesElement);
        List<LTECell> LTECellList=neighbourListinUse.getConfigurations().get(0).getData().getFAPService().getCellConfig().getLte().getRan().getNeighborListInUse().getLTECell();
        for (LTECell nRCellRelationModel : LTECellList) {
			Element nRCellRelationElement =
				Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/NRCellRelation");
			Element idNRCellRelationElement =
				Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/idNRCellRelation");
			idNRCellRelationElement.setValue(nRCellRelationModel.getIdNRCellRelation());
			Element nRCellRelationattributesElement =
				Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/attributes");
			Element nRTCIElement =
				Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/nRTCI");
			nRTCIElement.setValue(nRCellRelationModel.getIdNRCellRelation());
			Element isHOAllowedElement =
				Element.create("org:onap:ccsdk:features:sdnr:northbound:ran-network", "/isHOAllowed");
			isHOAllowedElement.setValue(nRCellRelationModel.getIsHOAllowed());
			nRCellRelationattributesElement.addChild(nRTCIElement);
			nRCellRelationattributesElement.addChild(isHOAllowedElement);
			nRCellRelationattributesElement.addChild(sapElement);
			nRCellRelationElement.addChild(idNRCellRelationElement);
			nRCellRelationElement.addChild(nRCellRelationattributesElement);
			nRCellCUElement.addChild(nRCellRelationElement);
		} 
        
        
        gNBCUCPFunctionElement.addChild(idGNBCUCPFunctionElement);
        gNBCUCPFunctionElement.addChild(nRCellCUElement);

        nearRTRICElement.addChild(idNearRTRICElement);
        nearRTRICElement.addChild(gNBCUCPFunctionElement);

        ranNetworkElement.addChild(nearRTRICElement);
        ranNetworkElement.markMerge();
        return ranNetworkElement;

    } catch (JNCException e) {
        log.error("Exception occured during NodeSet creation {}", e);
        return null;
    }
	}
}
