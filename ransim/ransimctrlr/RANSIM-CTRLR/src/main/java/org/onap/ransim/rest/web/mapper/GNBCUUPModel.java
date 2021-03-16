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

import org.onap.ransim.rest.api.models.NearRTRIC;
import org.onap.ransim.rest.api.models.PLMNInfo;

public class GNBCUUPModel{
	private Integer gNBCUUPId;
	private Integer gNBId;
	private Integer gNBIdLength;
	private List<PLMNInfoModel> pLMNInfoList;
	private String resourceType;
	private String metricKey;
	private Integer metricValue;
	private Integer nearRTRICId;
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
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public String getMetricKey() {
		return metricKey;
	}
	public void setMetricKey(String metricKey) {
		this.metricKey = metricKey;
	}
	public Integer getMetricValue() {
		return metricValue;
	}
	public void setMetricValue(Integer metricValue) {
		this.metricValue = metricValue;
	}
	public List<PLMNInfoModel> getpLMNInfoList() {
		return pLMNInfoList;
	}
	public void setpLMNInfoList(List<PLMNInfoModel> pLMNInfoList) {
		this.pLMNInfoList = pLMNInfoList;
	}
	public Integer getNearRTRICId() {
		return nearRTRICId;
	}
	public void setNearRTRICId(Integer nearRTRICId) {
		this.nearRTRICId = nearRTRICId;
	}
	@Override
	public String toString() {
		return "GNBCUUPModel [gNBCUUPId=" + gNBCUUPId + ", gNBId=" + gNBId + ", gNBIdLength=" + gNBIdLength
				+ ", pLMNInfoList=" + pLMNInfoList + ", resourceType=" + resourceType + ", metricKey=" + metricKey
				+ ", metricValue=" + metricValue + ", nearRTRICId=" + nearRTRICId + "]";
	}
	
}
