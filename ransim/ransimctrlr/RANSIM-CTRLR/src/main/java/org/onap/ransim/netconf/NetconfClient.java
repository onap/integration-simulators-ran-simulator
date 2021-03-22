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

import java.io.IOException;
import java.net.ConnectException;

import org.apache.log4j.Logger;
import org.onap.ransim.rest.web.mapper.GNBCUCPModel;
import org.onap.ransim.rest.web.mapper.GNBCUUPModel;
import org.onap.ransim.rest.web.mapper.GNBDUModel;
import org.onap.ransim.rest.web.mapper.NRCellCUModel;
import org.onap.ransim.rest.web.mapper.NRCellDUModel;
import org.onap.ransim.rest.web.mapper.NearRTRICModel;

public class NetconfClient {

    static Logger log = Logger.getLogger(NetconfClient.class.getName());

    private String emsUserName;
    private Device device;

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
            for (GNBCUCPModel gNBCUCPModel : rtRicModel.getgNBCUCPList()) {
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
}
