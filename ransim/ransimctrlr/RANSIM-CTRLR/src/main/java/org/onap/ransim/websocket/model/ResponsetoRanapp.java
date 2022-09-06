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

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponsetoRanapp {

	@Override
	public String toString() {
		return "{ \"response-code\":" + response_code + ", \"error-info\":" + error_info + "}";
	}

	@JsonProperty("response-code")
	private int response_code;
	
	
	@JsonProperty("error-info")
	private String error_info;

	@JsonProperty("response-code")
	public int getResponse_code() {
		return response_code;
	}

	@JsonProperty("response-code")
	public void setResponse_code(int response_code) {
		this.response_code = response_code;
	}

	@JsonProperty("error-info")
	public String getError_info() {
		return error_info;
	}

	@JsonProperty("error-info")
	public void setError_info(String error_info) {
		this.error_info = error_info;
	}
}
