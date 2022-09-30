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
"FAPService"
})
@Generated("jsonschema2pojo")
public class Data {


@JsonProperty("ric_id")
private String ricId;
	
@JsonProperty("FAPService")
private FAPService fAPService;

@JsonProperty("ric_id")
public String getRicId() {
	return ricId;
}

@JsonProperty("ric_id")
public void setRicId(String ricId) {
	this.ricId = ricId;
}

@JsonProperty("FAPService")
public FAPService getFAPService() {
return fAPService;
}

@JsonProperty("FAPService")
public void setFAPService(FAPService fAPService) {
this.fAPService = fAPService;
}



@Override
public String toString() {
	return "Data [ricId=" + ricId + ", fAPService=" + fAPService + "]";
}

}