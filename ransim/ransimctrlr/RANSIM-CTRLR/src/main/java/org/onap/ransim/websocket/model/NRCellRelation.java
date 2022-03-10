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

 @JsonInclude(JsonInclude.Include.NON_NULL)
 public class NRCellRelation {

      private Integer idNRCellRelation;
      private AttributesNRRelation attributes;


      public NRCellRelation() {
      }

      public Integer getIdNRCellRelation(){ return idNRCellRelation; }

      public void setIdNRCellRelation(Integer idNRCellRelation){ this.idNRCellRelation = idNRCellRelation; }

      public AttributesNRRelation getAttributes() {
	      return attributes;
      }

      public void setAttributes(AttributesNRRelation attributes) {
	      this.attributes = attributes;
      }

      public NRCellRelation(AttributesNRRelation attributes) {
	      super();
	      this.attributes = attributes;
      }
 }

