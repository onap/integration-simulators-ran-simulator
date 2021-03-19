/*
 * ============LICENSE_START=======================================================
 * Ran Simulator Controller
 * ================================================================================
 * Copyright (C) 2020-2021 Wipro Limited.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.ransim.rest.api.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "RRMPOLICYMEMBER")
public class RRMPolicyMember implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "PLMNID")
	private String pLMNId;
	@Column(name = "SNSSAI")
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
