/*
 * Copyright (c) 2022 CAPGEMINI ENGINEERING.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.ransim.websocket.model;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"alias",
"CellConfig"
})
@Generated("jsonschema2pojo")
public class FAPService {

@JsonProperty("idNRCellCU")
private String idNRCellCU;
@JsonProperty("CellConfig")
private CellConfig cellConfig;



@JsonProperty("idNRCellCU")
public String getIdNRCellCU() {
	return idNRCellCU;
}

@JsonProperty("idNRCellCU")
public void setIdNRCellCU(String idNRCellCU) {
	this.idNRCellCU = idNRCellCU;
}

@JsonProperty("CellConfig")
public CellConfig getCellConfig() {
return cellConfig;
}

@JsonProperty("CellConfig")
public void setCellConfig(CellConfig cellConfig) {
this.cellConfig = cellConfig;
}


@Override
public String toString() {
	return "FAPService [alias=" + idNRCellCU + ", cellConfig=" + cellConfig + 
			 "]";
}

}