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

package org.onap.ransim.websocket.server;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;
import org.onap.ransim.rest.api.services.RansimControllerServices;
import org.onap.ransim.websocket.model.DeviceData;
import org.onap.ransim.websocket.model.DeviceDataDecoder;
import org.onap.ransim.websocket.model.DeviceDataEncoder;
import org.onap.ransim.websocket.model.MessageTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@ServerEndpoint(
        value = "/RansimAgent/{IpPort}",
        encoders = {DeviceDataEncoder.class},
        decoders = {DeviceDataDecoder.class})
public class RansimWebSocketServer {

    static Logger log = Logger.getLogger(RansimWebSocketServer.class.getName());

    private static RansimControllerServices rscServices;

    @Autowired
    public void setRscServices(RansimControllerServices service) {
        this.rscServices = service;
    }

    /**
     * Set of actions to be done when connection is opened.
     *
     * @param session Session details
     * @param ipPort ip address of the agent
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("IpPort") String ipPort) {
        try {
            String useCaseType = RansimControllerServices.useCaseType;
            switch (useCaseType) {
                case "sonUsecase":
                    log.info("RansimWebSocketServer : Assign serverId wrt SlicingUsecase");
                    String serverId = rscServices.addWebSocketSessions(ipPort, session);
                    if (serverId != null) {
                        log.info("New websocket session added for " + serverId);
                        rscServices.sendInitialConfigForNewAgent(ipPort, serverId);
                    } else {
                        log.info(
                                "RansimWebSocketServer: No assigned ServerId found - No intial configuration sent to New Agent "
                                        + ipPort);
                    }
                    break;
                case "ranSlicingUsecase":
                    log.info("RansimWebSocketServer : Assign serverId wrt RANSlicingUsecase");
                    String ranServerId = rscServices.addRanWebSocketSessions(ipPort, session);
                    if (ranServerId != null) {
                        log.info("New websocket session added for " + ranServerId);
                        rscServices.sendRanInitialConfigForNewAgent(ipPort, ranServerId);
                    } else {
                        log.info(
                                "RansimWebSocketServer: No assigned ServerId found - No intial configuration sent to New Agent "
                                        + ipPort);
                    }
                    break;
                default:
                    log.info("RansimWebSocketServer: No assigned ServerId found");
            }
        } catch (Exception e) {
            log.info("Exception in onOpen:", e);
        }
    }

    /**
     * Handles the message sent from the agent.
     *
     * @param message message sent from the agent
     * @param session session details
     * @param ipPort ip address public void onMessage(DeviceData message, Session
     *        session, @PathParam("IpPort") String ipPort) {
     */
    @OnMessage
    public void onMessage(final DeviceData message, final Session session, @PathParam("IpPort") String ipPort)
            throws IOException, EncodeException {
        log.info("WSS Obj Message received from client(" + ipPort + ") with id " + session.getId());
        try {
            if (message != null) {
                if (message.getMessage() == null || message.getMessage().trim().equals("")) {
                    log.debug("Periodic ping message.... ignore");
                    return;
                } else {

                    if (message.getType().equals(MessageTypes.HC_TO_RC_MODPCI)) {
                        log.info("Modify pci message received");
                        rscServices.handleModifyPciFromSdnr(message.getMessage(), session, ipPort);
                    } else if (message.getType().equals(MessageTypes.HC_TO_RC_MODANR)) {
                        log.info("Modify anr message received");
                        rscServices.handleModifyNeighborFromSdnr(message.getMessage(), session, ipPort);
                    } else if (message.getType().equals(MessageTypes.HC_TO_RC_RTRIC)) {
                        log.info("Distribute RTRIC Config message received");
                        rscServices.handleRTRICConfigFromSdnr(message.getMessage(), session, ipPort);
                    } else if (message.getType().equals(MessageTypes.HC_TO_RC_RRM_POLICY)) {
                        log.info("Add RRMPolicyRatio message received");
                        rscServices.handleRRMPolicyRatioUpdateFromSdnr(message.getMessage(), session, ipPort);
                    } else if (message.getType().equals(MessageTypes.HC_TO_RC_PLMN)) {
                        log.info("Add PLMNInfo message received");
                        rscServices.handlePLMNInfoUpdateFromSdnr(message.getMessage(), session, ipPort);
                    } else if (message.getType().equals(MessageTypes.HC_TO_RC_SLICE_PROFILE)) {
                        log.info("Add SliceProfile message received");
                        rscServices.handleSliceProfileUpdateFromSdnr(message.getMessage(), session, ipPort);
                    } else if (message.getType().equals(MessageTypes.HC_TO_RC_RRM_POLICY_DEL)) {
                        log.info("Delete RRMPolicyRatio message received");
                        rscServices.handleRRMPolicyRatioDeleteFromSdnr(message.getMessage(), session, ipPort);
                    } else if (message.getType().equals(MessageTypes.HC_TO_RC_PLMN_DEL)) {
                        log.info("Delete PLMNInfo message received");
                        rscServices.handlePLMNInfoDeleteFromSdnr(message.getMessage(), session, ipPort);
                    } else if (message.getType().equals(MessageTypes.HC_TO_RC_SLICE_PROFILE_DEL)) {
                        log.info("Delete SliceProfile message received");
                        rscServices.handleSliceProfileDeleteFromSdnr(message.getMessage(), session, ipPort);
                    }

                }
            }
        } catch (Exception e) {
            log.info("Exception in onMessage:", e);
        }
    }

    /**
     * Set of actions to be done when connection is closed.
     *
     * @param reason reason the session was closed
     * @param session session details
     * @param ipPort ip address
     */
    @OnClose
    public void onClose(CloseReason reason, Session session, @PathParam("IpPort") String ipPort) {
        try {
            log.info("WSS Closing client(" + ipPort + ") cxn with id " + session.getId() + "due to "
                    + reason.getReasonPhrase());
            rscServices.removeWebSocketSessions(ipPort);
        } catch (Exception e) {
            log.info("Exception in onClose:", e);
        }
    }

    public static void sendUpdateCellMessage(String str, Session session) {
        DeviceData data = new DeviceData();
        data.setType(MessageTypes.RC_TO_HC_UPDCELL);
        data.setMessage(str);
        try {
            sendMessage(data, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendPmMessage(String str, Session session) {
        DeviceData data = new DeviceData();
        data.setType(MessageTypes.RC_TO_HC_PMDATA);
        data.setMessage(str);
        log.info("data.setMessage: " + data.getMessage());
        try {
            sendMessage(data, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendIntelligentSlicingPmData(String str, Session session) {
        DeviceData data = new DeviceData();
        data.setType(MessageTypes.RC_TO_HC_PMFILEDATA);
        data.setMessage(str);
        log.info("data.setMessage: " + data.getMessage());
        try {
            sendMessage(data, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendFmMessage(String str, Session session) {
        DeviceData data = new DeviceData();
        data.setType(MessageTypes.RC_TO_HC_FMDATA);
        data.setMessage(str);
        try {
            sendMessage(data, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendSetConfigTopologyMessage(String str, Session session) {
        DeviceData data = new DeviceData();
        data.setType(MessageTypes.RC_TO_HC_SETCONFIGTOPO);
        data.setMessage(str);
        try {
            sendMessage(data, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendSetReconfigureMessage(String str, Session session) {
        DeviceData data = new DeviceData();
        data.setType(MessageTypes.HC_TO_RC_RTRIC);
        data.setMessage(str);
        try {
            sendMessage(data, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendPingMessage(Session session) {
        DeviceData data = new DeviceData();
        data.setType(MessageTypes.RC_TO_HC_PING);
        data.setMessage("");
        try {
            sendMessage(data, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage(DeviceData data, Session session) {
        try {
            session.getBasicRemote().sendObject(data);
        } catch (Exception e) {
            log.info("Exception in sendMessage:", e);
        }
    }
}
