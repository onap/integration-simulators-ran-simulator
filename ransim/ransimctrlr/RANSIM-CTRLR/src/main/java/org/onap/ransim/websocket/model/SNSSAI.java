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

package org.onap.ransim.websocket.model;

import java.util.*;

public class SNSSAI {

    private String sNssai;

    private List<ConfigData> configData;

    public String getSNssai() {
        return sNssai;
    }

    public void setSNssai(String sNssai) {
        this.sNssai = sNssai;
    }

    public List<ConfigData> getConfigData() {
        return configData;
    }

    public void setConfigData(List<ConfigData> configData) {
        this.configData = configData;
    }

    public SNSSAI() {

    }

}
