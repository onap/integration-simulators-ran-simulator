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
"LTE"
})
@Generated("jsonschema2pojo")
public class CellConfig {

@JsonProperty("LTE")
private LTE lte;


@JsonProperty("LTE")
public LTE getLte() {
return lte;
}

@JsonProperty("LTE")
public void setLte(LTE lte) {
this.lte = lte;
}


@Override
public String toString() {
	return "CellConfig [lte=" + lte +   "]";
}

}