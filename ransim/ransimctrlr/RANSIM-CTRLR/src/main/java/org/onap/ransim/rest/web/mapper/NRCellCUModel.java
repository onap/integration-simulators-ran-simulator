package org.onap.ransim.rest.web.mapper;

import java.util.List;

public class NRCellCUModel{	
	private Integer cellLocalId;
	private List<PLMNInfoModel> pLMNInfoList;
	private String resourceType;
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
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	@Override
	public String toString() {
		return "NRCellCUModel [cellLocalId=" + cellLocalId + ", pLMNInfoList=" + pLMNInfoList + ", resourceType="
				+ resourceType + "]";
	}
}
