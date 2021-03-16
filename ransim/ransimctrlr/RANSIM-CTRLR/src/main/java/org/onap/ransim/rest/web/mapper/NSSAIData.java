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


import org.onap.ransim.rest.api.models.NSSAIConfig;

public class NSSAIData{
	private String sNSSAI;
	private String status;
	private String globalSubscriberId;
	private String subscriptionServiceType;
	//private Map<String, Integer> configData;
	private NSSAIConfig configData;
	public String getsNSSAI() {
		return sNSSAI;
	}
	public void setsNSSAI(String sNSSAI) {
		this.sNSSAI = sNSSAI;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getGlobalSubscriberId() {
		return globalSubscriberId;
	}
	public void setGlobalSubscriberId(String globalSubscriberId) {
		this.globalSubscriberId = globalSubscriberId;
	}
	public String getSubscriptionServiceType() {
		return subscriptionServiceType;
	}
	public void setSubscriptionServiceType(String subscriptionServiceType) {
		this.subscriptionServiceType = subscriptionServiceType;
	}
	public NSSAIConfig getConfigData() {
		return configData;
	}
	public void setConfigData(NSSAIConfig configData) {
		this.configData = configData;
	}
}
