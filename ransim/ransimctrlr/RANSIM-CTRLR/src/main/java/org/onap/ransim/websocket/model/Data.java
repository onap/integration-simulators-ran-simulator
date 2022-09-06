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