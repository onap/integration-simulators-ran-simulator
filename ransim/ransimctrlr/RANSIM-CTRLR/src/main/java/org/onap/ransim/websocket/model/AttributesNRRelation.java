/*
 * ============LICENSE_START=======================================================
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

 import com.fasterxml.jackson.annotation.JsonInclude;
 import com.fasterxml.jackson.annotation.JsonProperty;

 @JsonInclude(JsonInclude.Include.NON_NULL)
 public class AttributesNRRelation {
	
	 private Integer nRTCI;
         private boolean isHOAllowed;

	 @JsonProperty("nRTCI")
	 public Integer getNRTCI() {
		 return nRTCI;
	 }

	 public void setNRTCI(Integer nRTCI) {
		 this.nRTCI = nRTCI;
	 }

	 @JsonProperty("isHOAllowed")
	 public boolean getIsHOAllowed() {
		 return isHOAllowed;
	 }

	 public void setIsHoAllowed(boolean isHOAllowed) {
		 this.isHOAllowed = isHOAllowed;
	 }
 }

