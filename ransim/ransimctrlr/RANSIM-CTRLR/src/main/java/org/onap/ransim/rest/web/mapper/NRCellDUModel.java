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

import javax.persistence.Column;

public class NRCellDUModel{
	private Integer cellLocalId;
	private String operationalState;
	private String administrativeState;
	private String cellState;	
	private List<PLMNInfoModel> pLMNInfoList;
	private Integer nRPCI;
	private Integer nRTAC;
	private String resourceType;
	private Integer prbs;
	
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
	
	@Override
	public String toString() {
		return "NRCellDUModel [cellLocalId=" + cellLocalId + ", operationalState=" + operationalState
				+ ", administrativeState=" + administrativeState + ", cellState=" + cellState + ", pLMNInfoList="
				+ pLMNInfoList + ", nRPCI=" + nRPCI + ", nRTAC=" + nRTAC + ", resourceType=" + resourceType + ", prbs="
				+ prbs + "]";
	}
	

	}
