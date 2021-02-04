package org.onap.ransim.rest.web.mapper;

import java.util.List;

public class RRMPolicyRatioModel{	
	private Integer rrmPolicyID;
	private String resourceID;
	private String resourceType;
	private String sliceType;
	private List<RRMPolicyMemberModel> rRMPolicyMemberList;
	private String quotaType;
	private Integer rRMPolicyMaxRatio;
	private Integer rRMPolicyMinRatio;
	private Integer rRMPolicyDedicatedRatio;
	public Integer getRrmPolicyID() {
		return rrmPolicyID;
	}
	public void setRrmPolicyID(Integer rrmPolicyID) {
		this.rrmPolicyID = rrmPolicyID;
	}
	public String getResourceID() {
		return resourceID;
	}
	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public String getSliceType() {
		return sliceType;
	}
	public void setSliceType(String sliceType) {
		this.sliceType = sliceType;
	}
	public List<RRMPolicyMemberModel> getrRMPolicyMemberList() {
		return rRMPolicyMemberList;
	}
	public void setrRMPolicyMemberList(List<RRMPolicyMemberModel> rRMPolicyMemberList) {
		this.rRMPolicyMemberList = rRMPolicyMemberList;
	}
	public String getQuotaType() {
		return quotaType;
	}
	public void setQuotaType(String quotaType) {
		this.quotaType = quotaType;
	}
	public Integer getrRMPolicyMaxRatio() {
		return rRMPolicyMaxRatio;
	}
	public void setrRMPolicyMaxRatio(Integer rRMPolicyMaxRatio) {
		this.rRMPolicyMaxRatio = rRMPolicyMaxRatio;
	}
	public Integer getrRMPolicyMinRatio() {
		return rRMPolicyMinRatio;
	}
	public void setrRMPolicyMinRatio(Integer rRMPolicyMinRatio) {
		this.rRMPolicyMinRatio = rRMPolicyMinRatio;
	}
	public Integer getrRMPolicyDedicatedRatio() {
		return rRMPolicyDedicatedRatio;
	}
	public void setrRMPolicyDedicatedRatio(Integer rRMPolicyDedicatedRatio) {
		this.rRMPolicyDedicatedRatio = rRMPolicyDedicatedRatio;
	}
	@Override
	public String toString() {
		return "RRMPolicyRatioModel [rrmPolicyID=" + rrmPolicyID + ", resourceID=" + resourceID + ", resourceType="
				+ resourceType + ", sliceType=" + sliceType + ", rRMPolicyMemberList=" + rRMPolicyMemberList
				+ ", quotaType=" + quotaType + ", rRMPolicyMaxRatio=" + rRMPolicyMaxRatio + ", rRMPolicyMinRatio="
				+ rRMPolicyMinRatio + ", rRMPolicyDedicatedRatio=" + rRMPolicyDedicatedRatio + "]";
	}
	
}
