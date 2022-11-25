/*
 * ============LICENSE_START=======================================================
 * Ran Simulator Controller
 * ================================================================================
 * Copyright (C) 2020-2022 Wipro Limited.
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

import java.io.Serializable;
import org.apache.log4j.Logger;
import java.util.List;
import java.util.ArrayList;
import org.onap.ransim.rest.api.controller.RansimController;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "NRCELLCU")
public class NRCellCU implements Serializable, Comparable<NRCellCU> {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "CELLLOCALID")
    private Integer cellLocalId;
    @Column(name = "RESOURCETYPE")
    private String resourceType;
    @Column(name = "SCREENX")
    private float screenX;
    @Column(name = "SCREENY")
    private float screenY;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "PLMNINFO", joinColumns = @JoinColumn(name = "nrcellcu_celllocalid"))
    private List<PLMNInfo> pLMNInfoList;
    @Column(name = "NRCELLRELATIONLIST")
    @OneToMany(mappedBy = "cellLocalId", cascade = CascadeType.ALL)
    private List<NRCellRelation> nrCellRelationsList;
    @ManyToOne
    @JoinColumn(name = "gnbcuname")
    private GNBCUCPFunction gNBCUCPFunction;

    private boolean pciCollisionDetected;
    private boolean pciConfusionDetected;
    private String color;

    public boolean isPciCollisionDetected() {
      return pciCollisionDetected;
    }

    public void setPciCollisionDetected(boolean pciCollisionDetected) {
      this.pciCollisionDetected = pciCollisionDetected;
    }

    public boolean isPciConfusionDetected() {
      return pciConfusionDetected;
    }

    public void setPciConfusionDetected(boolean pciConfusionDetected) {
      this.pciConfusionDetected = pciConfusionDetected;
    }

    public String getColor() {
      return color;
    }

    public void setColor(String color) {
      this.color = color;
    }

    public Integer getCellLocalId() {
        return cellLocalId;
    }

    public void setCellLocalId(Integer cellLocalId) {
        this.cellLocalId = cellLocalId;
    }
    public List<NRCellRelation> getNrCellRelationsList() { return nrCellRelationsList; }
    
    public void setNrCellRelationsList(List<NRCellRelation> nrCellRelationsList) {
	this.nrCellRelationsList = nrCellRelationsList;
    }

    public List<PLMNInfo> getpLMNInfoList() {
        return pLMNInfoList;
    }

    public void setpLMNInfoList(List<PLMNInfo> pLMNInfoList) {
        this.pLMNInfoList = pLMNInfoList;
    }

    public GNBCUCPFunction getgNBCUCPFunction() {
        return gNBCUCPFunction;
    }

    public void setgNBCUCPFunction(GNBCUCPFunction gNBCUCPFunction) {
        this.gNBCUCPFunction = gNBCUCPFunction;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public void setScreenX(float screenX){ this.screenX = screenX; }
    public float getScreenX(){ return screenX; }

    public void setScreenY(float screenY){ this.screenY = screenY; }
    public float getScreenY(){ return screenY; }

    static Logger log = Logger.getLogger(RansimController.class.getName());

    public void display() {

        List<NRCellRelation> iterator = new ArrayList<>(nrCellRelationsList);
	for (int ii = 0; ii < iterator.size(); ii++) {
		log.info("neighbors NeighborList: " + iterator.get(ii).getCellLocalId()+ " "
				+ iterator.get(ii).getIdNRCellRelation() + " " + iterator.get(ii).getisHOAllowed());
	}
    }

    @Override
    public int compareTo(NRCellCU cd) {
        return this.getCellLocalId().compareTo(cd.getCellLocalId());
    }

}
