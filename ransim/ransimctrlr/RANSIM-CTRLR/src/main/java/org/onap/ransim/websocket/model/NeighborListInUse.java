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
import java.util.List;
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
"LTECell",
"LTECellNumberOfEntries"
})
@Generated("jsonschema2pojo")
public class NeighborListInUse {

@JsonProperty("LTECell")
private List<LTECell> lTECell = null;
@JsonProperty("LTECellNumberOfEntries")
private String lTECellNumberOfEntries;


@JsonProperty("LTECell")
public List<LTECell> getLTECell() {
return lTECell;
}

@JsonProperty("LTECell")
public void setLTECell(List<LTECell> lTECell) {
this.lTECell = lTECell;
}

@JsonProperty("LTECellNumberOfEntries")
public String getLTECellNumberOfEntries() {
return lTECellNumberOfEntries;
}

@JsonProperty("LTECellNumberOfEntries")
public void setLTECellNumberOfEntries(String lTECellNumberOfEntries) {
this.lTECellNumberOfEntries = lTECellNumberOfEntries;
}



@Override
public String toString() {
	return "NeighborListInUse [lTECell=" + lTECell + ", lTECellNumberOfEntries=" + lTECellNumberOfEntries
			+   "]";
}

}