/*
 * =============LICENSE_START=======================================================
 * Ran Simulator Controller
 ** ================================================================================
 ** Copyright (C) 2022 Wipro Limited.
 ** ================================================================================
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **      http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 ** ============LICENSE_END=========================================================
 **/

 package org.onap.ransim.websocket.model;

 import com.fasterxml.jackson.annotation.JsonProperty;
 import com.fasterxml.jackson.annotation.JsonInclude;

 import java.util.List;

 @JsonInclude(JsonInclude.Include.NON_NULL)
 public class GNBCUCPFunction {
      
	 private String idGNBCUCPFunction;
	 private Attributes attributes;

	 @JsonProperty("NRCellCU")
	 public List<NRCellCU> nRCellCU;


	 public String getIdGNBCUCPFunction() {
		 return idGNBCUCPFunction;
	 }


	 public void setIdGNBCUCPFunction(String idGNBCUCPFunction) {
		 this.idGNBCUCPFunction = idGNBCUCPFunction;
	 }


	 public Attributes getAttributes() {
		 return attributes;
	 }


	 public void setAttributes(Attributes attributes) {
		 this.attributes = attributes;
	 }


	 public List<NRCellCU> getnRCellCU() {
		 return nRCellCU;
	 }


	 public void setnRCellCU(List<NRCellCU> nRCellCU) {
		 this.nRCellCU = nRCellCU;
	 }


	 public GNBCUCPFunction() {
	 } 
 }

