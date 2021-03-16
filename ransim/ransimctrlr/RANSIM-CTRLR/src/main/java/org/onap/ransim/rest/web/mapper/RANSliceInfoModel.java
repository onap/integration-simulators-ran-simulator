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

import java.io.Serializable;
import java.util.List;

public class RANSliceInfoModel implements Serializable{
	private static final long serialVersionUID = 1L;
	private String ranNFNSSIId;
	private List<String> ranNSSIList;
	private List<String> nSSAIList;
	private List<SliceProfileModel> sliceProfilesList;
	private String trackingAreaList;
	private String subnetStatus;
	private String nsstId;
	private String sliceType;
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
	
	public String getTrackingAreaList() {
		return trackingAreaList;
	}
	public void setTrackingAreaList(String trackingAreaList) {
		this.trackingAreaList = trackingAreaList;
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
	public List<String> getnSSAIList() {
		return nSSAIList;
	}
	public void setnSSAIList(List<String> nSSAIList) {
		this.nSSAIList = nSSAIList;
	}
	public List<SliceProfileModel> getSliceProfilesList() {
		return sliceProfilesList;
	}
	public void setSliceProfilesList(List<SliceProfileModel> sliceProfilesList) {
		this.sliceProfilesList = sliceProfilesList;
	}
	public void setIsShareable(String isShareable) {
		this.isShareable = isShareable;
	}
	@Override
	public String toString() {
		return "RANSliceInfoModel [ranNFNSSIId=" + ranNFNSSIId + ", ranNSSIList=" + ranNSSIList + ", nSSAIList=" + nSSAIList + ", sliceProfilesList=" + sliceProfilesList + ", trackingAreaList="
				+ trackingAreaList + ", subnetStatus=" + subnetStatus + ", nsstId=" + nsstId + ", sliceType="
				+ sliceType + ", isShareable=" + isShareable + "]";
	}
	
}
