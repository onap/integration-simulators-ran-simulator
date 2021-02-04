package org.onap.ransim.rest.api.models;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="RRMPOLICYRATIO")
public class RRMPolicyRatio implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="RRMPOLICYID")
	private Integer rrmPolicyID;
	@Column(name="RESOURCEID")
	private String resourceID;
	@Column(name="RESOURCETYPE")
	private String resourceType;
	@Column(name="SLICETYPE")
	private String sliceType;
//	@OneToMany(mappedBy = "rrmPolicy",  cascade={CascadeType.PERSIST,CascadeType.REMOVE})
        @OneToMany(mappedBy = "rrmPolicy",  cascade = CascadeType.ALL)
	private List<RRMPolicyMember> rRMPolicyMemberList;
	@Column(name="QUOTATYPE")
	private String quotaType;
	@Column(name="RRMPOLICYMAXRATIO")
	private Integer rRMPolicyMaxRatio;	
	@Column(name="RRMPOLICYMINRATIO")
	private Integer rRMPolicyMinRatio;
	@Column(name="RRMPOLICYDEDICATEDRATIO")
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
	public List<RRMPolicyMember> getrRMPolicyMemberList() {
		return rRMPolicyMemberList;
	}
	public void setrRMPolicyMemberList(List<RRMPolicyMember> rRMPolicyMemberList) {
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
}
