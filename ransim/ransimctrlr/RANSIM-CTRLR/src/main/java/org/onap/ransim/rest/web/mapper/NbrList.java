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

public class NbrList {
    // param

    private String targetCellId;
    private long pciValue;
    private boolean ho;

    public String getTargetCellId() {
        return targetCellId;
    }

    public void setTargetCellId(String targetCellId) {
        this.targetCellId = targetCellId;
    }

    public long getPciValue() {
        return pciValue;
    }

    public void setPciValue(long pciValue) {
        this.pciValue = pciValue;
    }

    public boolean isHo() {
        return ho;
    }

    public void setHo(boolean ho) {
        this.ho = ho;
    }

}
