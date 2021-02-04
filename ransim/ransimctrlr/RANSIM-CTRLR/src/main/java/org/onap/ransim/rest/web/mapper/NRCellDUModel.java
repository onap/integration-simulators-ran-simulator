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
