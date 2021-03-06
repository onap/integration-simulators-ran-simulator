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

package org.onap.ransim.rest.web.mapper;

import java.util.List;

public class SliceProfileModel {
    private String sliceProfileId;
    private String sNSSAI;
    private String pLMNIdList;
    private Integer maxNumberofUEs;
    private Integer latency;
    private Integer dLThptPerSlice;
    private Integer uLThptPerSlice;
    private Integer maxNumberofConns;
    private String uEMobilityLevel;
    private String resourceSharingLevel;
    private List<String> coverageAreaList;

    public String getSliceProfileId() {
        return sliceProfileId;
    }

    public void setSliceProfileId(String sliceProfileId) {
        this.sliceProfileId = sliceProfileId;
    }

    public String getsNSSAI() {
        return sNSSAI;
    }

    public void setsNSSAI(String sNSSAI) {
        this.sNSSAI = sNSSAI;
    }

    public String getpLMNIdList() {
        return pLMNIdList;
    }

    public void setpLMNIdList(String pLMNIdList) {
        this.pLMNIdList = pLMNIdList;
    }

    public Integer getMaxNumberofUEs() {
        return maxNumberofUEs;
    }

    public void setMaxNumberofUEs(Integer maxNumberofUEs) {
        this.maxNumberofUEs = maxNumberofUEs;
    }

    public Integer getLatency() {
        return latency;
    }

    public void setLatency(Integer latency) {
        this.latency = latency;
    }

    public Integer getdLThptPerSlice() {
        return dLThptPerSlice;
    }

    public void setdLThptPerSlice(Integer dLThptPerSlice) {
        this.dLThptPerSlice = dLThptPerSlice;
    }

    public Integer getuLThptPerSlice() {
        return uLThptPerSlice;
    }

    public void setuLThptPerSlice(Integer uLThptPerSlice) {
        this.uLThptPerSlice = uLThptPerSlice;
    }

    public Integer getMaxNumberofConns() {
        return maxNumberofConns;
    }

    public void setMaxNumberofConns(Integer maxNumberofConns) {
        this.maxNumberofConns = maxNumberofConns;
    }

    public String getuEMobilityLevel() {
        return uEMobilityLevel;
    }

    public void setuEMobilityLevel(String uEMobilityLevel) {
        this.uEMobilityLevel = uEMobilityLevel;
    }

    public String getResourceSharingLevel() {
        return resourceSharingLevel;
    }

    public void setResourceSharingLevel(String resourceSharingLevel) {
        this.resourceSharingLevel = resourceSharingLevel;
    }

    public List<String> getCoverageAreaList() {
        return coverageAreaList;
    }

    public void setCoverageAreaList(List<String> coverageAreaList) {
        this.coverageAreaList = coverageAreaList;
    }

    @Override
    public String toString() {
        return "SliceProfileModel [sliceProfileId=" + sliceProfileId + ", sNSSAI=" + sNSSAI + ", pLMNIdList="
                + pLMNIdList + ", maxNumberofUEs=" + maxNumberofUEs + ", latency=" + latency + ", dLThptPerSlice="
                + dLThptPerSlice + ", uLThptPerSlice=" + uLThptPerSlice + ", maxNumberofConns=" + maxNumberofConns
                + ", uEMobilityLevel=" + uEMobilityLevel + ", resourceSharingLevel=" + resourceSharingLevel
                + ", coverageAreaList=" + coverageAreaList + "]";
    }
}
