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

@Entity(name = "RANINVENTORY")
@Table(name = "RANINVENTORY")
public class RANSliceInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "RANNFNSSIID")
	private String ranNFNSSIId;
	@ElementCollection
	@CollectionTable(name = "rannssi", joinColumns = @JoinColumn(name = "rannfnssiid"))
	private List<String> ranNSSIList;
	@ElementCollection
	@CollectionTable(name = "nssai", joinColumns = @JoinColumn(name = "rannfnssiid"))
	private List<String> nSSAIList;
	@OneToMany(mappedBy = "rANSliceInventory", cascade = CascadeType.ALL)
	private List<SliceProfile> sliceProfilesList;
	@Column(name = "TALIST")
	private String trackingAreaList;
	@Column(name = "SUBNETSTATUS")
	private String subnetStatus;
	@Column(name = "NSSTID")
	private String nsstId;
	@Column(name = "SLICETYPE")
	private String sliceType;
	@Column(name = "ISSHAREABLE")
	private String isShareable;

	public String getRanNFNSSIId() {
		return ranNFNSSIId;
	}

	public void setRanNFNSSIId(String ranNFNSSIId) {
		this.ranNFNSSIId = ranNFNSSIId;
	}

	public List<String> getRanNSSIList() {
		return ranNSSIList;
	}

	public void setRanNSSIList(List<String> ranNSSIList) {
		this.ranNSSIList = ranNSSIList;
	}

	public List<SliceProfile> getSliceProfilesList() {
		return sliceProfilesList;
	}

	public void setSliceProfilesList(List<SliceProfile> sliceProfilesList) {
		this.sliceProfilesList = sliceProfilesList;
	}

	public String getSubnetStatus() {
		return subnetStatus;
	}

	public void setSubnetStatus(String subnetStatus) {
		this.subnetStatus = subnetStatus;
	}

	public String getNsstId() {
		return nsstId;
	}

	public void setNsstId(String nsstId) {
		this.nsstId = nsstId;
	}

	public String getSliceType() {
		return sliceType;
	}

	public void setSliceType(String sliceType) {
		this.sliceType = sliceType;
	}

	public String getIsShareable() {
		return isShareable;
	}

	public void setIsShareable(String isShareable) {
		this.isShareable = isShareable;
	}

	public List<String> getnSSAIList() {
		return nSSAIList;
	}

	public void setnSSAIList(List<String> nSSAIList) {
		this.nSSAIList = nSSAIList;
	}

	public String getTrackingAreaList() {
		return trackingAreaList;
	}

	public void setTrackingAreaList(String trackingAreaList) {
		this.trackingAreaList = trackingAreaList;
	}

	@Override
	public String toString() {
		return "RANSliceInfo [ranNFNSSIId=" + ranNFNSSIId + ", ranNSSIList=" + ranNSSIList + ", nSSAIList=" + nSSAIList
				+ ", sliceProfilesList=" + sliceProfilesList + ", trackingAreaList=" + trackingAreaList
				+ ", subnetStatus=" + subnetStatus + ", nsstId=" + nsstId + ", sliceType=" + sliceType
				+ ", isShareable=" + isShareable + "]";
	}
}
