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

public class NRCellCUModel {
    private Integer cellLocalId;
    private List<PLMNInfoModel> pLMNInfoList;
    private String resourceType;

    public Integer getCellLocalId() {
        return cellLocalId;
    }

    public void setCellLocalId(Integer cellLocalId) {
        this.cellLocalId = cellLocalId;
    }

    public List<PLMNInfoModel> getpLMNInfoList() {
        return pLMNInfoList;
    }

    public void setpLMNInfoList(List<PLMNInfoModel> pLMNInfoList) {
        this.pLMNInfoList = pLMNInfoList;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public String toString() {
        return "NRCellCUModel [cellLocalId=" + cellLocalId + ", pLMNInfoList=" + pLMNInfoList + ", resourceType="
                + resourceType + "]";
    }
}
