package org.onap.ransim.rest.api.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Table;
import java.sql.Timestamp;

@Table(name="NSSAICONFIG")
@Embeddable
public class NSSAIConfig implements Serializable{
	private static final long serialVersionUID = 1L;
	@Column(name="DLTHPTPERSLICE")
	private Integer dLThptPerSlice;
	@Column(name="ULTHPTPERSLICE")
	private Integer uLThptPerSlice;
	@Column(name="MAXNUMBEROFCONNS")
	private Integer maxNumberOfConns;
	@Column(name="LASTUPDATEDTS")
	private Timestamp lastUpdatedTS;
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
	public Integer getMaxNumberOfConns() {
		return maxNumberOfConns;
	}
	public void setMaxNumberOfConns(Integer maxNumberOfConns) {
		this.maxNumberOfConns = maxNumberOfConns;
	}
	public Timestamp getLastUpdatedTS() {
		return lastUpdatedTS;
	}
	public void setLastUpdatedTS(Timestamp lastUpdatedTS) {
		this.lastUpdatedTS = lastUpdatedTS;
	}
}