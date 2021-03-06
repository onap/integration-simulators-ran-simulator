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

public class GNBCUCPModel {
    private String gNBCUName;
    private Integer gNBId;
    private Integer gNBIdLength;
    private String pLMNId;
    private String nFType;
    private List<NRCellCUModel> cellCUList;
    private Integer nearRTRICId;

    public String getgNBCUName() {
        return gNBCUName;
    }

    public void setgNBCUName(String gNBCUName) {
        this.gNBCUName = gNBCUName;
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

    public String getpLMNId() {
        return pLMNId;
    }

    public void setpLMNId(String pLMNId) {
        this.pLMNId = pLMNId;
    }

    public List<NRCellCUModel> getCellCUList() {
        return cellCUList;
    }

    public void setCellCUList(List<NRCellCUModel> cellCUList) {
        this.cellCUList = cellCUList;
    }

    public Integer getNearRTRICId() {
        return nearRTRICId;
    }

    public void setNearRTRICId(Integer nearRTRICId) {
        this.nearRTRICId = nearRTRICId;
    }

    public String getnFType() {
        return nFType;
    }

    public void setnFType(String nFType) {
        this.nFType = nFType;
    }

    @Override
    public String toString() {
        return "GNBCUCPModel [gNBCUName=" + gNBCUName + ", gNBId=" + gNBId + ", gNBIdLength=" + gNBIdLength
                + ", pLMNId=" + pLMNId + ", nFType=" + nFType + ", cellCUList=" + cellCUList + ", nearRTRICId="
                + nearRTRICId + "]";
    }

}
