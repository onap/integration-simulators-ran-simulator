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
