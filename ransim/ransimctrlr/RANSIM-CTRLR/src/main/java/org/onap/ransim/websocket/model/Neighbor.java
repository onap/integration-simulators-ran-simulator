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

public class Neighbor {

    private String plmnId;
    private String idNRCellRelation;
    private long physicalCellId;
    private String serverId;
    private String idGNBCUCPFunction;
    private boolean isHOAllowed;

    public String getPlmnId() {
        return plmnId;
    }

    public void setPlmnId(String plmnId) {
        this.plmnId = plmnId;
    }

    public String getIdNRCellRelation() {
        return idNRCellRelation;
    }

    public void setIdNRCellRelation(String idNRCellRelation) {
        this.idNRCellRelation = idNRCellRelation;
    }

    public long getPhysicalCellId() {
        return physicalCellId;
    }

    public void setPhysicalCellId(long physicalCellId) {
        this.physicalCellId = physicalCellId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getIdGNBCUCPFunction() {
        return idGNBCUCPFunction;
    }

    public void setIdGNBCUCPFunction(String idGNBCUCPFunction) {
        this.idGNBCUCPFunction = idGNBCUCPFunction;
    }

    public Neighbor() {
        // TODO Auto-generated constructor stub
    }

    public boolean getIsHOAllowed() {
        return isHOAllowed;
    }

    public void setIsHOAllowed(boolean blacklisted) {
        this.isHOAllowed = isHOAllowed;
    }

    @Override
    public String toString() {
        return "Neighbor [idNRCellRelation=" + idNRCellRelation + ", physicalCellId=" + physicalCellId + ", serverId=" + serverId
                + ", idGNBCUCPFunction=" + idGNBCUCPFunction + "]";
    }
}
