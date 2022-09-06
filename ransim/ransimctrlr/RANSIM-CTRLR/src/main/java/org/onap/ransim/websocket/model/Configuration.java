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
"data"
})
@Generated("jsonschema2pojo")
public class Configuration {

@JsonProperty("data")
private Data data;

@JsonProperty("data")
public Data getData() {
return data;
}

@JsonProperty("data")
public void setData(Data data) {
this.data = data;
}

@Override
public String toString() {
	return "Configuration [data=" + data +   "]";
}

}