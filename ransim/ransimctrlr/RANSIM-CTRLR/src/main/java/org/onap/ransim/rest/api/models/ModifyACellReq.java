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

public class ModifyACellReq {

    private String nodeId;
    private Integer newPhysicalCellId;
    private String newNbrs;

    /**
     * A constructor for ModifyACellReq.
     *
     * @param nodeId node Id of the cell which is to be modified
     * @param newPhysicalCellId new PCI number for the cell
     * @param newNbrs new neighbor list for the cell
     */
    public ModifyACellReq(String nodeId, Integer newPhysicalCellId, String newNbrs) {
        super();
        this.nodeId = nodeId;
        this.newPhysicalCellId = newPhysicalCellId;
        this.newNbrs = newNbrs;

    }

    public ModifyACellReq() {

        // Default constructor for ModifyCell
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getNewPhysicalCellId() {
        return newPhysicalCellId;
    }

    public void setNewPhysicalCellId(Integer newPhysicalCellId) {
        this.newPhysicalCellId = newPhysicalCellId;
    }

    public String getNewNbrs() {
        return newNbrs;
    }

    public void setNewNbrs(String newNbrs) {
        this.newNbrs = newNbrs;
    }

}
