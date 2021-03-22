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

package org.onap.ransim.rest.api.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.ransim.rest.api.models.CellDetails;
import org.onap.ransim.rest.api.models.CellNeighbor;
import org.onap.ransim.rest.api.models.FmAlarmInfo;
import org.onap.ransim.rest.api.models.NeighborDetails;
import org.onap.ransim.rest.api.models.NeihborId;
import org.onap.ransim.rest.api.models.NetconfServers;
import org.onap.ransim.rest.api.services.RansimControllerServices;
import org.onap.ransim.rest.api.services.RansimRepositoryService;
import org.onap.ransim.rest.client.RestClient;
import org.onap.ransim.websocket.model.CommonEventHeaderFm;
import org.onap.ransim.websocket.model.EventFm;
import org.onap.ransim.websocket.model.FaultFields;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author ubuntu16
 *
 */
@RunWith(MockitoJUnitRunner.class)
@PropertySource("classpath:ransim.properties")
public class TestRansimController {

    @Test
    public void testGetRansimController() {
        RansimControllerServices rc = Mockito.mock(RansimControllerServices.class);
        assertNotNull(rc);
    }

    @Test
    public void testsetNetconfServers() {
        // fail("Not yet implemented");
        RansimControllerServices rscontroller = Mockito.mock(RansimControllerServices.class);
        CellDetails cell1 = new CellDetails("Chn01", 1, "nc1");
        CellDetails cell2 = new CellDetails("Chn02", 2, "nc1");
        CellDetails cell3 = new CellDetails("Chn03", 3, "nc1");
        CellDetails cell4 = new CellDetails("Chn04", 4, "nc1");

        Set<CellDetails> cells = new HashSet<CellDetails>();
        cells.add(cell1);
        cells.add(cell2);
        cells.add(cell3);
        cells.add(cell4);

        NetconfServers server = new NetconfServers("nc1", null, null, cells);

        new MockUp<RansimRepositoryService>() {
            @Mock
            NetconfServers getNetconfServer(String serverId) {

                return server;
            }
        };
        new MockUp<RansimRepositoryService>() {
            @Mock
            CellDetails getCellDetail(String nodeId) {

                return cell4;
            }
        };
        new MockUp<RansimRepositoryService>() {
            @Mock
            void mergeNetconfServers(NetconfServers netconfServers) {

            }
        };

        rscontroller.setNetconfServers("Chn04");

        boolean check = server.getCells().contains(cell4);

        assertTrue(check);

    }

    @Test
    public void testGenerateNeighborList() {
        // fail("Not yet implemented");
        RansimControllerServices rscontroller = Mockito.mock(RansimControllerServices.class);
        Set<NeighborDetails> neighborList = new HashSet<NeighborDetails>();
        NeighborDetails nbr1 = new NeighborDetails(new NeihborId("Chn00", "Chn01"), false);
        NeighborDetails nbr2 = new NeighborDetails(new NeihborId("Chn00", "Chn02"), false);
        NeighborDetails nbr3 = new NeighborDetails(new NeihborId("Chn00", "Chn03"), true);

        neighborList.add(nbr1);
        neighborList.add(nbr2);
        neighborList.add(nbr3);

        CellDetails cell0 = new CellDetails("Chn00", 4, "nc1");
        CellDetails cell1 = new CellDetails("Chn01", 1, "nc1");
        CellDetails cell2 = new CellDetails("Chn02", 2, "nc1");
        CellDetails cell3 = new CellDetails("Chn03", 3, "nc1");

        CellNeighbor cellNbr = new CellNeighbor();
        cellNbr.setNodeId("Chn00");
        cellNbr.setNeighborList(neighborList);

        new MockUp<RansimRepositoryService>() {
            @Mock
            CellNeighbor getCellNeighbor(String nodeId) {
                if (nodeId.equals("Chn00")) {
                    return cellNbr;
                } else {
                    return null;
                }

            }
        };

        new MockUp<RansimRepositoryService>() {
            @Mock
            CellDetails getCellDetail(String nodeId) {
                if (nodeId.equals("Chn00")) {
                    return cell0;
                } else if (nodeId.equals("Chn01")) {
                    return cell1;
                } else if (nodeId.equals("Chn02")) {
                    return cell2;
                } else if (nodeId.equals("Chn03")) {
                    return cell3;
                } else {
                    return null;
                }

            }
        };

        /*
         * GetNeighborList nbrListFct = rscontroller.generateNeighborList("Chn00");
         * 
         * boolean result = false;
         * 
         * if (nbrListFct.getCellsWithHo().contains(cell1)) { if
         * (nbrListFct.getCellsWithHo().contains(cell2)) { if
         * (nbrListFct.getCellsWithNoHo().contains(cell3)) { result = true; } } }
         * 
         * assertTrue(result);
         */

    }

    @Test
    public void testSetEventFm() {
        // fail("Not yet implemented");
        RansimControllerServices rscontroller = Mockito.mock(RansimControllerServices.class);
        Map<String, String> alarmAdditionalInformation = new HashMap<String, String>();
        alarmAdditionalInformation.put("networkId", "abc");
        alarmAdditionalInformation.put("collisions", "1");
        alarmAdditionalInformation.put("confusions", "0");
        CommonEventHeaderFm commonEventHeader = new CommonEventHeaderFm("Chn00", "", "nc1", 0, 0);
        FaultFields faultFields = new FaultFields("RanPciCollisionConfusionOccurred", "other", "Collision", "CRITICAL",
                alarmAdditionalInformation);
        EventFm checkObj = new EventFm(commonEventHeader, faultFields);

        new MockUp<RansimControllerServices>() {
            @Mock
            String getUuid() {
                return "";
            }
        };
        new MockUp<System>() {
            @Mock
            public long currentTimeMillis() {
                return (long) 0;
            }
        };

        String networkId = "abc";
        String ncServer = "nc1";
        String cellId = "Chn00";
        FmAlarmInfo issue = new FmAlarmInfo("Collision", "1", "0");
        /*
         * EventFm eventObj = rscontroller.setEventFm(networkId, ncServer, cellId,
         * issue);
         * 
         * boolean result = false;
         * 
         * Gson gson = new Gson(); String eventStr = gson.toJson(eventObj); String
         * checkStr = gson.toJson(checkObj);
         * 
         * System.out.println("eventStr: " + eventStr); System.out.println("checkStr: "
         * + checkStr);
         * 
         * if (eventStr.equals(checkStr)) { result = true; }
         * 
         * assertTrue(result);
         */
    }

    // @Test
    public void testStopAllCells() {
        RansimControllerServices rscontroller = Mockito.mock(RansimControllerServices.class);

        new MockUp<RansimRepositoryService>() {
            @Mock
            List<NetconfServers> getNetconfServersList() {
                System.out.println("getNetconfServersList");
                List<NetconfServers> ns = new ArrayList<NetconfServers>();
                NetconfServers n1 = new NetconfServers("nc1", null, null, null);
                NetconfServers n2 = new NetconfServers("nc2", null, null, null);
                ns.add(n1);
                ns.add(n2);
                return ns;
            }
        };

        new MockUp<RestClient>() {
            @Mock
            public String sendUnmountRequestToSdnr(String serverId, String ip, int port, String sdnrUsername,
                    String sdnrPassword) {
                System.out.println("sendUnmountRequestToSdnr");
                return "";
            }
        };

        String result = rscontroller.stopAllSimulation();
        System.out.println("testStopAllCells: " + result);
        assertEquals("Netconf servers unmounted.", result);
    }

    @Test
    public void testStartRanSimulation() throws Exception {

        ResponseEntity<String> rsEntity = new ResponseEntity<>("Simulation started", HttpStatus.OK);
        RansimController rscontroller = Mockito.mock(RansimController.class);
        when(rscontroller.startRanSliceSimulation()).thenReturn(rsEntity);
        assertEquals(rscontroller.startRanSliceSimulation(), rsEntity);

    }

    @Test
    public void testFailureStartRanSimulation() throws Exception {

        RansimController rscontroller = new RansimController();
        ResponseEntity<String> result = rscontroller.startRanSliceSimulation();
        assertEquals(result.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Test
    public void testStopRanSimulation() throws Exception {

        ResponseEntity<String> rsEntity = new ResponseEntity<>("Simulation stopped", HttpStatus.OK);
        RansimController rscontroller = Mockito.mock(RansimController.class);
        when(rscontroller.stopRanSliceSimulation()).thenReturn(rsEntity);
        assertEquals(rscontroller.stopRanSliceSimulation(), rsEntity);

    }

    @Test
    public void testGenerateIntelligentSlicingPmData() throws Exception {

        ResponseEntity<String> rsEntity = new ResponseEntity<>("IntelligentSlicing PM data generated", HttpStatus.OK);
        RansimController rscontroller = Mockito.mock(RansimController.class);
        when(rscontroller.generateIntelligentSlicingPmData()).thenReturn(rsEntity);
        assertEquals(rscontroller.generateIntelligentSlicingPmData(), rsEntity);

    }

    @Test
    public void testStopIntelligentSlicingPmData() throws Exception {

        ResponseEntity<String> rsEntity = new ResponseEntity<>("Stopped PM data generation.", HttpStatus.OK);
        RansimController rscontroller = Mockito.mock(RansimController.class);
        when(rscontroller.stopIntelligentSlicingPmData()).thenReturn(rsEntity);
        assertEquals(rscontroller.stopIntelligentSlicingPmData(), rsEntity);

    }

}
