package org.onap.ransim.rest.api.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="RRMPOLICYMEMBER")
public class RRMPolicyMember implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "PLMNID")
	private String pLMNId;
	@Column(name="SNSSAI")
	private String sNSSAI;
	@ManyToOne
	private RRMPolicyRatio rrmPolicy;

	public String getpLMNId() {
		return pLMNId;
	}
	public void setpLMNId(String pLMNId) {
		this.pLMNId = pLMNId;
	}
	
	public String getsNSSAI() {
		return sNSSAI;
	}
	public void setsNSSAI(String sNSSAI) {
		this.sNSSAI = sNSSAI;
	}
	public RRMPolicyRatio getRrmPolicy() {
		return rrmPolicy;
	}
	public void setRrmPolicy(RRMPolicyRatio rrmPolicy) {
		this.rrmPolicy = rrmPolicy;
	}
}