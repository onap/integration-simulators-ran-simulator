package org.onap.ransim.rest.api.models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="GNBCUCPFUNCTION")
public class GNBCUCPFunction implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="GNBCUNAME")
	private String gNBCUName;
	@Column(name="GNBID")
	private Integer gNBId;
	@Column(name="GNBIDLENGTH")
	private Integer gNBIdLength;
	@Column(name="PLMNID")
	private String pLMNId;
	@Column(name="NFTYPE")
	private String nFType;
	@Column(name="CELLCULIST")
	@OneToMany(mappedBy = "gNBCUCPFunction", cascade=CascadeType.ALL)
	private List<NRCellCU> cellCUList;
	@ManyToOne//(cascade=CascadeType.ALL)
	@JoinColumn(name = "nearrtricid")
	private NearRTRIC nearRTRIC;
	public String getgNBCUName() {
		return gNBCUName;
	}
	public void setgNBCUName(String gNBCUName) {
		this.gNBCUName = gNBCUName;
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
	public String getpLMNId() {
		return pLMNId;
	}
	public void setpLMNId(String pLMNId) {
		this.pLMNId = pLMNId;
	}
	public List<NRCellCU> getCellCUList() {
		return cellCUList;
	}
	public void setCellCUList(List<NRCellCU> cellCUList) {
		this.cellCUList = cellCUList;
	}
	public NearRTRIC getNearRTRIC() {
		return nearRTRIC;
	}
	public void setNearRTRIC(NearRTRIC nearRTRIC) {
		this.nearRTRIC = nearRTRIC;
	}
	public String getnFType() {
		return nFType;
	}
	public void setnFType(String nFType) {
		this.nFType = nFType;
	}
	
}