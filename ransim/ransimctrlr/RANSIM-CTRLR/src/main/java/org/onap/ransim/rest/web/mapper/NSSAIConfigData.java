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
