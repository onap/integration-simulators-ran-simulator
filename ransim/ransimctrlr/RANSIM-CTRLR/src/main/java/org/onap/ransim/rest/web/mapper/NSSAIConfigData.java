package org.onap.ransim.rest.web.mapper;

public class NSSAIConfigData{
	private Integer dLThptPerSlice;
	private Integer uLThptPerSlice;
	private Integer maxNumberOfConns;
	private String lastUpdatedTS;
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
	public String getLastUpdatedTS() {
		return lastUpdatedTS;
	}
	public void setLastUpdatedTS(String lastUpdatedTS) {
		this.lastUpdatedTS = lastUpdatedTS;
    }	
	@Override
	public String toString() {
		return "NSSAIConfigData [dLThptPerSlice=" + dLThptPerSlice + ", uLThptPerSlice=" + uLThptPerSlice
				+ ", maxNumberOfConns=" + maxNumberOfConns + ", lastUpdatedTS=" + lastUpdatedTS + "]";
	}
}