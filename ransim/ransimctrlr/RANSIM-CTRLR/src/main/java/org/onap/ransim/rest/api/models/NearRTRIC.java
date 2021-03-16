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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "NEARRTRIC")
public class NearRTRIC implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "NEARRTRICID")
	private Integer nearRTRICId;
	@Column(name = "GNBID")
	private Integer gNBId;
	@ElementCollection(targetClass = String.class)
	@CollectionTable(name = "TRACKINGAREA", joinColumns = @JoinColumn(name = "nearrtricid"))
	private List<String> trackingArea;
	@Column(name = "RESOURCETYPE")
	private String resourceType;
	@OneToMany(mappedBy = "nearRTRIC", cascade = CascadeType.ALL)
	private List<GNBCUCPFunction> gNBCUCPList;
	@OneToMany(mappedBy = "nearRTRIC", cascade = CascadeType.ALL)
	private List<GNBCUUPFunction> gNBCUUPList;
	@OneToMany(mappedBy = "nearRTRIC", cascade = CascadeType.ALL)
	private List<GNBDUFunction> gNBDUList;
	@ElementCollection(targetClass = String.class)
	@CollectionTable(name = "RANNFNSSI", joinColumns = @JoinColumn(name = "nearrtricid"))
	private List<String> ranNFNSSIList;
	@ElementCollection
	@CollectionTable(name = "PLMNINFO", joinColumns = @JoinColumn(name = "nearrtricid"))
	private List<PLMNInfo> pLMNInfoList;

	public Integer getNearRTRICId() {
		return nearRTRICId;
	}

	public void setNearRTRICId(Integer nearRTRICId) {
		this.nearRTRICId = nearRTRICId;
	}

	public Integer getgNBId() {
		return gNBId;
	}

	public void setgNBId(Integer gNBId) {
		this.gNBId = gNBId;
	}

	public List<String> getTrackingArea() {
		return trackingArea;
	}

	public void setTrackingArea(List<String> trackingArea) {
		this.trackingArea = trackingArea;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public List<GNBCUCPFunction> getgNBCUCPList() {
		return gNBCUCPList;
	}

	public void setgNBCUCPList(List<GNBCUCPFunction> gNBCUCPList) {
		this.gNBCUCPList = gNBCUCPList;
	}

	public List<GNBCUUPFunction> getgNBCUUPList() {
		return gNBCUUPList;
	}

	public void setgNBCUUPList(List<GNBCUUPFunction> gNBCUUPList) {
		this.gNBCUUPList = gNBCUUPList;
	}

	public List<GNBDUFunction> getgNBDUList() {
		return gNBDUList;
	}

	public void setgNBDUList(List<GNBDUFunction> gNBDUList) {
		this.gNBDUList = gNBDUList;
	}

	public List<PLMNInfo> getpLMNInfoList() {
		return pLMNInfoList;
	}

	public void setpLMNInfoList(List<PLMNInfo> pLMNInfoList) {
		this.pLMNInfoList = pLMNInfoList;
	}

	public List<String> getRanNFNSSIList() {
		return ranNFNSSIList;
	}

	public void setRanNFNSSIList(List<String> ranNFNSSIList) {
		this.ranNFNSSIList = ranNFNSSIList;
	}

}
