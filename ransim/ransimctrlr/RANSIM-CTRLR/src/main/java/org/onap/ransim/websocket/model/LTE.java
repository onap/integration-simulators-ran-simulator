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
"RAN"
})
@Generated("jsonschema2pojo")
public class LTE {

@JsonProperty("RAN")
private RAN ran;


@JsonProperty("RAN")
public RAN getRan() {
return ran;
}

@JsonProperty("RAN")
public void setRan(RAN ran) {
this.ran = ran;
}



@Override
public String toString() {
	return "LTE [ran=" + ran +   "]";
}

}