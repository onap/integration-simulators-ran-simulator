package org.onap.ransim.rest.api.models;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="SLICEPROFILE")
public class SliceProfile implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="SLICEPROFILEID")
	private String sliceProfileId;
	@Column(name="SNSSAI")
	private String sNSSAI;
	@Column(name="PLMNIDLIST")
	private String pLMNIdList;
	@Column(name="MAXNOOFUES")
	private Integer maxNumberofUEs;
	@Column(name="LATENCY")
	private Integer latency;
	@Column(name="DLTHPTPERSLICE")
	private Integer dLThptPerSlice;
	@Column(name="ULTHPTPERSLICE")
	private Integer uLThptPerSlice;
	@Column(name="MAXNUMBEROFCONNS")
	private Integer maxNumberofConns;
	public Integer getdLThptPerSlice() {
		return dLThptPerSlice;
	}
	public void setdLThptPerSlice(Integer dLThptPerSlice) {
		this.dLThptPerSlice = dLThptPerSlice;
	}
	public Integer getuLThptPerSlice() {
		return uLThptPerSlice;
	}
	public void setuLThptPerSlice(Integer uLThptPerSlice) {
		this.uLThptPerSlice = uLThptPerSlice;
	}
	public Integer getMaxNumberofConns() {
		return maxNumberofConns;
	}
	public void setMaxNumberofConns(Integer maxNumberofConns) {
		this.maxNumberofConns = maxNumberofConns;
	}
	public String getCoverageAreaList() {
		return coverageAreaList;
	}

	/*
	 * @Column(name="EXPDATARATEDL") private Integer expDataRateDL;
	 * 
	 * @Column(name="EXPDATARATEUL") private Integer expDataRateUL;
	 * 
	 * @Column(name="MAXNUMBEROFPDUSESSIONS") private Integer
	 * maxNumberofPDUSessions;
	 */
	@Column(name="UEMOBILITYLEVEL")
	private String uEMobilityLevel;
	@Column(name="RESOURCESHARINGLEVEL")
	private String resourceSharingLevel;
	//@ElementCollection
	//@CollectionTable(name="coveragearealist", joinColumns = @JoinColumn(name="sliceprofileid"))
	@Column(name="coveragearealist")
	private String coverageAreaList;
	@ManyToOne
	@JoinColumn(name = "rannfnssiid")
	private RANSliceInfo rANSliceInventory;
	public String getSliceProfileId() {
		return sliceProfileId;
	}
	public void setSliceProfileId(String sliceProfileId) {
		this.sliceProfileId = sliceProfileId;
	}
	public String getsNSSAI() {
		return sNSSAI;
	}
	public void setsNSSAI(String sNSSAI) {
		this.sNSSAI = sNSSAI;
	}
	public String getpLMNIdList() {
		return pLMNIdList;
	}
	public void setpLMNIdList(String pLMNIdList) {
		this.pLMNIdList = pLMNIdList;
	}
	public Integer getMaxNumberofUEs() {
		return maxNumberofUEs;
	}
	public void setMaxNumberofUEs(Integer maxNumberofUEs) {
		this.maxNumberofUEs = maxNumberofUEs;
	}
	public Integer getLatency() {
		return latency;
	}
	public void setLatency(Integer latency) {
		this.latency = latency;
	}
	public String getuEMobilityLevel() {
		return uEMobilityLevel;
	}
	public void setuEMobilityLevel(String uEMobilityLevel) {
		this.uEMobilityLevel = uEMobilityLevel;
	}
	public String getResourceSharingLevel() {
		return resourceSharingLevel;
	}
	public void setResourceSharingLevel(String resourceSharingLevel) {
		this.resourceSharingLevel = resourceSharingLevel;
	}
	public RANSliceInfo getrANSliceInventory() {
		return rANSliceInventory;
	}
	public void setrANSliceInventory(RANSliceInfo rANSliceInventory) {
		this.rANSliceInventory = rANSliceInventory;
	}
	
	public void setCoverageAreaList(String coverageAreaList) {
		this.coverageAreaList = coverageAreaList;
	}
	
}