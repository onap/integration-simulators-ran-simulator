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

package org.onap.ransim.rest.api.models;

import java.io.Serializable;
import java.util.List;

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
@Table(name = "NRCELLDU")
public class NRCellDU implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "CELLLOCALID")
    private Integer cellLocalId;
    @Column(name = "OPERATIONALSTATE")
    private String operationalState;
    @Column(name = "ADMINISTRATIVESTATE")
    private String administrativeState;
    @Column(name = "CELLSTATE")
    private String cellState;
    @Column(name = "LATITUDE")
    private Double latitude;
    @Column(name = "LONGITUDE")
    private Double longitude;
    @Column(name = "NETWORKID")
    private String networkId;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "PLMNINFO", joinColumns = @JoinColumn(name = "nrcelldu_celllocalid"))
    private List<PLMNInfo> pLMNInfoList;
    @Column(name = "NRPCI")
    private Integer nRPCI;
    @Column(name = "nRTAC")
    private Integer nRTAC;
    @Column(name = "RESOURCETYPE")
    private String resourceType;
    @ManyToOne
    @JoinColumn(name = "gnbduid")
    private GNBDUFunction gNBDUFunction;
    @Column(name = "prb")
    private Integer prbs;

    public Double getLatitude() {
	return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }    
    
    public Integer getCellLocalId() {
        return cellLocalId;
    }

    public void setCellLocalId(Integer cellLocalId) {
        this.cellLocalId = cellLocalId;
    }

    public List<PLMNInfo> getpLMNInfoList() {
        return pLMNInfoList;
    }

    public void setpLMNInfoList(List<PLMNInfo> pLMNInfoList) {
        this.pLMNInfoList = pLMNInfoList;
    }

    public String getOperationalState() {
        return operationalState;
    }

    public void setOperationalState(String operationalState) {
        this.operationalState = operationalState;
    }

    public String getAdministrativeState() {
        return administrativeState;
    }

    public void setAdministrativeState(String administrativeState) {
        this.administrativeState = administrativeState;
    }

    public String getCellState() {
        return cellState;
    }

    public void setCellState(String cellState) {
        this.cellState = cellState;
    }

    public Integer getnRPCI() {
        return nRPCI;
    }

    public void setnRPCI(Integer nRPCI) {
        this.nRPCI = nRPCI;
    }

    public Integer getnRTAC() {
        return nRTAC;
    }

    public void setnRTAC(Integer nRTAC) {
        this.nRTAC = nRTAC;
    }

    public GNBDUFunction getgNBDUFunction() {
        return gNBDUFunction;
    }

    public void setgNBDUFunction(GNBDUFunction gNBDUFunction) {
        this.gNBDUFunction = gNBDUFunction;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Integer getPrbs() {
        return prbs;
    }

    public void setPrbs(Integer prbs) {
        this.prbs = prbs;
    }
}
