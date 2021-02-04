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
@Table(name="GNBDUFUNCTION")
public class GNBDUFunction implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="GNBDUID")
	private Integer gNBDUId;
	@Column(name="GNBID")
	private Integer gNBId;
	@Column(name="GNBIDLENGTH")
	private Integer gNBIdLength;
	@Column(name="GNBDUNAME")
	private String gNBDUName;
	@Column(name="PLMNID")
	private String pLMNId;
	@Column(name="NFTYPE")
	private String nFType;
	@Column(name="CELLDULIST")
	@OneToMany(mappedBy = "gNBDUFunction",cascade=CascadeType.ALL)
	private List<NRCellDU> cellDUList;
	@ManyToOne
	@JoinColumn(name = "nearrtricid")
	private NearRTRIC nearRTRIC;
	public Integer getgNBDUId() {
		return gNBDUId;
	}
	public void setgNBDUId(Integer gNBDUId) {
		this.gNBDUId = gNBDUId;
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
	public String getgNBDUName() {
		return gNBDUName;
	}
	public void setgNBDUName(String gNBDUName) {
		this.gNBDUName = gNBDUName;
	}
	public String getpLMNId() {
		return pLMNId;
	}
	public void setpLMNId(String pLMNId) {
		this.pLMNId = pLMNId;
	}
	public List<NRCellDU> getCellDUList() {
		return cellDUList;
	}
	public void setCellDUList(List<NRCellDU> cellDUList) {
		this.cellDUList = cellDUList;
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