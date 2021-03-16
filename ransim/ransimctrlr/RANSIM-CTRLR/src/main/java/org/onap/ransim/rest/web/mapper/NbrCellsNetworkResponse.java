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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NbrCellsNetworkResponse {
	
	private String networkId;
	//private List<NbrCellsNetwork> NbrCellsNetworkObjList;
	@JsonProperty("cellsNbrList")
	private List<NbrListResponse> cellsNbrList;
	public String getNetworkId() {
		return networkId;
	}
	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}
	public List<NbrListResponse> getCellsNbrList() {
		return cellsNbrList;
	}
	public void setCellsNbrList(List<NbrListResponse> cellsNbrList) {
		this.cellsNbrList = cellsNbrList;
	}
	
	
	
	/*public List<NbrCellsNetwork> getNbrCellsNetworkObjList() {
		return NbrCellsNetworkObjList;
	}
	public void setNbrCellsNetworkObjList(List<NbrCellsNetwork> nbrCellsNetworkObjList) {
		NbrCellsNetworkObjList = nbrCellsNetworkObjList;
	}*/
	
	

}
