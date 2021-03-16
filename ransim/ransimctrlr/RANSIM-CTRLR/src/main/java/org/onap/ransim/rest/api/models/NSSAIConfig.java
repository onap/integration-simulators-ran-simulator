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
import javax.persistence.Embeddable;
import javax.persistence.Table;
import java.sql.Timestamp;

@Table(name = "NSSAICONFIG")
@Embeddable
public class NSSAIConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(name = "DLTHPTPERSLICE")
	private Integer dLThptPerSlice;
	@Column(name = "ULTHPTPERSLICE")
	private Integer uLThptPerSlice;
	@Column(name = "MAXNUMBEROFCONNS")
	private Integer maxNumberOfConns;
	@Column(name = "LASTUPDATEDTS")
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
