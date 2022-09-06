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
"Configurations"
})
@Generated("jsonschema2pojo")
public class PayloadOutput {

@JsonProperty("Configurations")
private List<Configuration> configurations = null;


@JsonProperty("Configurations")
public List<Configuration> getConfigurations() {
return configurations;
}

@JsonProperty("Configurations")
public void setConfigurations(List<Configuration> configurations) {
this.configurations = configurations;
}

@Override
public String toString() {
	return "PayloadOutput [configurations=" + configurations +   "]";
}

}