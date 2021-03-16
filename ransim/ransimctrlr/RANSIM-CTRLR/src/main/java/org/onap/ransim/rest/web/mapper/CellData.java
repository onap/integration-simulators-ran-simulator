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

public class CellData {
	private String networkId;
	private String nodeId;
	private Long physicalCellId;
	private String pnfId;
	private String sectorNumber;
	private String latitude;
	private String longitude;
	private String notes;

	public CellData() {

	}

	public CellData(String networkId, String nodeId, Long physicalCellId, String pnfId, String sectorNumber,
			String latitude, String longitude, String notes) {
		super();
		this.networkId = networkId;
		this.nodeId = nodeId;
		this.physicalCellId = physicalCellId;
		this.pnfId = pnfId;
		this.sectorNumber = sectorNumber;
		this.latitude = latitude;
		this.longitude = longitude;
		this.notes = notes;
	}

	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public Long getPhysicalCellId() {
		return physicalCellId;
	}

	public void setPhysicalCellId(Long physicalCellId) {
		this.physicalCellId = physicalCellId;
	}

	public String getPnfId() {
		return pnfId;
	}

	public void setPnfId(String pnfId) {
		this.pnfId = pnfId;
	}

	public String getSectorNumber() {
		return sectorNumber;
	}

	public void setSectorNumber(String sectorNumber) {
		this.sectorNumber = sectorNumber;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

}
