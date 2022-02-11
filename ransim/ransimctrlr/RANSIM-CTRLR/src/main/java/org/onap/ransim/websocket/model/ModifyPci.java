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

package org.onap.ransim.websocket.model;

import java.util.List;

public class ModifyPci {

    private String idGNBDUFunction;

    private long nRPCI;

    private String idNRCellDU;

    private List<Neighbor> neighborList;

    public long getNRPCI() {
        return nRPCI;
    }

    public void setNRPCI(long nRPCI) {
        this.nRPCI = nRPCI;
    }

    public String getIdNRCellDU() {
        return idNRCellDU;
    }

    public void setIdNRCellDU(String idNRCellDU) {
        this.idNRCellDU = idNRCellDU;
    }

    @Override
    public String toString() {
        return "ModifyPci [idGNBDUFunction = " + idGNBDUFunction + ", nRPCI = " + nRPCI + ", idNRCellDU = " + idNRCellDU + "neighborList:"
                + neighborList + "]";
    }

    public ModifyPci() {
    }

    /**
     * Modify Pci value from sdnr.
     *
     * @param idGNBDUFunction server id name
     * @param nRPCI pci number
     * @param idNRCellDU node id for the cell
     * @param neighborList neighbor list for the cell
     */
    public ModifyPci(String idGNBDUFunction, long nRPCI, String idNRCellDU, List<Neighbor> neighborList) {
        super();
        this.idGNBDUFunction = idGNBDUFunction;
        this.nRPCI = nRPCI;
        this.idNRCellDU = idNRCellDU;
        this.neighborList = neighborList;
    }

    public String getIdGNBDUFunction() {
        return idGNBDUFunction;
    }

    public void setIdGNBDUFunction(String idGNBDUFunction) {
        this.idGNBDUFunction = idGNBDUFunction;
    }

    public List<Neighbor> getNeighborList() {
        return neighborList;
    }

    public void setNeighborList(List<Neighbor> neighborList) {
        this.neighborList = neighborList;
    }

    /**
     * Checks if all the parameters are set correctly .
     *
     * @return returns true if the parameter are set correctly
     */
    public boolean isAllSet() {
        if (idGNBDUFunction == null || idGNBDUFunction.trim().equals("")) {
            return false;
        }
        if (idNRCellDU == null || idNRCellDU.trim().equals("")) {
            return false;
        }
        return true;
    }

}
