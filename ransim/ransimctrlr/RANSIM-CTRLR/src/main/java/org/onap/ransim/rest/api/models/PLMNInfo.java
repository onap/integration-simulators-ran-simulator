package org.onap.ransim.rest.api.models;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Embeddable
@Table(name = "PLMNINFO")
public class PLMNInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "PLMNID")
	private String pLMNId;

	@Embedded
	private SNSSAI sNSSAI;
	public String getpLMNId() {
		return pLMNId;
	}

	public void setpLMNId(String pLMNId) {
		this.pLMNId = pLMNId;
	}


	public SNSSAI getsNSSAI() {
		return sNSSAI;
	}

	public void setsNSSAI(SNSSAI sNSSAI) {
		this.sNSSAI = sNSSAI;
	}

}