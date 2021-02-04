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
@Table(name="NRCELLCU")
public class NRCellCU implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="CELLLOCALID")
	private Integer cellLocalId;
	@Column(name="RESOURCETYPE")
	private String resourceType;
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="PLMNINFO",joinColumns = @JoinColumn(name="nrcellcu_celllocalid"))
	private List<PLMNInfo> pLMNInfoList;
	@ManyToOne
	@JoinColumn(name = "gnbcuname")
	private GNBCUCPFunction gNBCUCPFunction;
	
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
	public GNBCUCPFunction getgNBCUCPFunction() {
		return gNBCUCPFunction;
	}
	public void setgNBCUCPFunction(GNBCUCPFunction gNBCUCPFunction) {
		this.gNBCUCPFunction = gNBCUCPFunction;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	
}
