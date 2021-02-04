package org.onap.ransim.rest.api.models;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="NRCELLDU")
public class NRCellDU implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="CELLLOCALID")
	private Integer cellLocalId;
	@Column(name="OPERATIONALSTATE")
	private String operationalState;
	@Column(name="ADMINISTRATIVESTATE")
	private String administrativeState;
	@Column(name="CELLSTATE")
	private String cellState;
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="PLMNINFO",joinColumns = @JoinColumn(name="nrcelldu_celllocalid"))
	private List<PLMNInfo> pLMNInfoList;
	@Column(name="NRPCI")
	private Integer nRPCI;
	@Column(name="nRTAC")
	private Integer nRTAC;
	@Column(name="RESOURCETYPE")
	private String resourceType;
	@ManyToOne
	@JoinColumn(name = "gnbduid")
	private GNBDUFunction gNBDUFunction;
	
	public Integer getCellLocalId() {
		return cellLocalId;
	}
	public void setCellLocalId(Integer cellLocalId) {
		this.cellLocalId = cellLocalId;
	}
	public List<PLMNInfo> getpLMNInfoList() {
		return pLMNInfoList;
	}
	public void setpLMNInfoList(List<PLMNInfo> pLMNInfoList) {
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
	public GNBDUFunction getgNBDUFunction() {
		return gNBDUFunction;
	}
	public void setgNBDUFunction(GNBDUFunction gNBDUFunction) {
		this.gNBDUFunction = gNBDUFunction;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	
}
