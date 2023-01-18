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

package org.onap.ransim.rest.api.models;

import static org.junit.Assert.*;

import org.junit.Test;
import org.onap.ransim.rest.api.models.CellDetails;
import org.onap.ransim.rest.api.models.ModifyACellReq;
import org.onap.ransim.rest.api.models.NetconfServers;

public class TestApiModels {

    @Test
    public void testsetNewPhysicalCellId() {
        ModifyACellReq mcell = new ModifyACellReq();
        mcell.setNewPhysicalCellId(001);
        assertTrue(mcell.getNewPhysicalCellId() == 001);
    }

    @Test
    public void testsetNewPhysicalCellId1() {
        ModifyACellReq mcell = new ModifyACellReq();
        mcell.setNewPhysicalCellId(000);
        assertFalse(mcell.getNewPhysicalCellId() == 001);
    }

    @Test
    public void testsetNetworkId() {
        CellDetails cd = new CellDetails();
        cd.setNetworkId("Ns001");
        assertTrue(cd.getNetworkId() == "Ns001");
    }

    @Test
    public void testsetServerId() {
        NetconfServers ns = new NetconfServers();
        ns.setServerId("ns0031");
        assertTrue(ns.getServerId() == "ns0031");
    }

}
