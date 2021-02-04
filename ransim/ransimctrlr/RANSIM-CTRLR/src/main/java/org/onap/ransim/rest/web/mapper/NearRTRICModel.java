package org.onap.ransim.rest.web.mapper;

import java.util.List;

public class NearRTRICModel{
	private Integer nearRTRICId;
	private Integer gNBId;
	private List<String> trackingArea;
	private String resourceType;
	private List<GNBCUCPModel> gNBCUCPList;
	private List<GNBCUUPModel> gNBCUUPList;
	private List<GNBDUModel> gNBDUList;
	private List<String> ranNFNSSIList;
	private List<PLMNInfoModel> pLMNInfoList;
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
	
	public List<String> getRanNFNSSIList() {
		return ranNFNSSIList;
	}
	public void setRanNFNSSIList(List<String> ranNFNSSIList) {
		this.ranNFNSSIList = ranNFNSSIList;
	}
	public List<GNBCUCPModel> getgNBCUCPList() {
		return gNBCUCPList;
	}
	public void setgNBCUCPList(List<GNBCUCPModel> gNBCUCPList) {
		this.gNBCUCPList = gNBCUCPList;
	}
	public List<GNBCUUPModel> getgNBCUUPList() {
		return gNBCUUPList;
	}
	public void setgNBCUUPList(List<GNBCUUPModel> gNBCUUPList) {
		this.gNBCUUPList = gNBCUUPList;
	}
	public List<GNBDUModel> getgNBDUList() {
		return gNBDUList;
	}
	public void setgNBDUList(List<GNBDUModel> gNBDUList) {
		this.gNBDUList = gNBDUList;
	}
	public List<PLMNInfoModel> getpLMNInfoList() {
		return pLMNInfoList;
	}
	public void setpLMNInfoList(List<PLMNInfoModel> pLMNInfoList) {
		this.pLMNInfoList = pLMNInfoList;
	}
	@Override
	public String toString() {
		return "NearRTRICModel [nearRTRICId=" + nearRTRICId + ", gNBId=" + gNBId + ", trackingArea=" + trackingArea
				+ ", resourceType=" + resourceType + ", gNBCUCPList=" + gNBCUCPList + ", gNBCUUPList=" + gNBCUUPList
				+ ", gNBDUList=" + gNBDUList + ", ranNFNSSIList=" + ranNFNSSIList + ", pLMNInfoList=" + pLMNInfoList
				+ "]";
	}
}
