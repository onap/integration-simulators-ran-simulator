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
import javax.persistence.Table;

@Entity
@Table(name = "GNBCUUPFUNCTION")
public class GNBCUUPFunction implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "GNBCUUPID")
    private Integer gNBCUUPId;
    @Column(name = "GNBID")
    private Integer gNBId;
    @Column(name = "GNBIDLENGTH")
    private Integer gNBIdLength;
    // @Column(name="PLMNINFOLIST")
    // @OneToMany(mappedBy = "gNBCUUPFunction",
    // cascade={CascadeType.PERSIST,CascadeType.REMOVE})
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "PLMNINFO", joinColumns = @JoinColumn(name = "gnbcuupid"))
    private List<PLMNInfo> pLMNInfoList;
    @Column(name = "RESOURCETYPE")
    private String resourceType;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nearrtricid")
    private NearRTRIC nearRTRIC;

    public Integer getgNBCUUPId() {
        return gNBCUUPId;
    }

    public void setgNBCUUPId(Integer gNBCUUPId) {
        this.gNBCUUPId = gNBCUUPId;
    }

    public Integer getgNBId() {
        return gNBId;
    }

    public void setgNBId(Integer gNBId) {
        this.gNBId = gNBId;
    }

    public Integer getgNBIdLength() {
        return gNBIdLength;
    }

    public void setgNBIdLength(Integer gNBIdLength) {
        this.gNBIdLength = gNBIdLength;
    }

    public List<PLMNInfo> getpLMNInfoList() {
        return pLMNInfoList;
    }

    public void setpLMNInfoList(List<PLMNInfo> pLMNInfoList) {
        this.pLMNInfoList = pLMNInfoList;
    }

    public NearRTRIC getNearRTRIC() {
        return nearRTRIC;
    }

    public void setNearRTRIC(NearRTRIC nearRTRIC) {
        this.nearRTRIC = nearRTRIC;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public String toString() {
        return "GNBCUUPFunction [gNBCUUPId=" + gNBCUUPId + ", gNBId=" + gNBId + ", gNBIdLength=" + gNBIdLength
                + ", pLMNInfoList=" + pLMNInfoList + ", resourceType=" + resourceType + ", nearRTRIC=" + nearRTRIC
                + "]";
    }
}
