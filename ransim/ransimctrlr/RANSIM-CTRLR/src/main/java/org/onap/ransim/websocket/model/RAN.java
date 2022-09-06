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
"Common",
"NeighborListInUse"
})
@Generated("jsonschema2pojo")
public class RAN {

@JsonProperty("Common")
private Common common;

@JsonProperty("Common")
public Common getCommon() {
return common;
}

@JsonProperty("Common")
public void setCommon(Common common) {
this.common = common;
}
@JsonProperty("NeighborListInUse")
private NeighborListInUse neighborListInUse;




@JsonProperty("NeighborListInUse")
public NeighborListInUse getNeighborListInUse() {
return neighborListInUse;
}

@JsonProperty("NeighborListInUse")
public void setNeighborListInUse(NeighborListInUse neighborListInUse) {
this.neighborListInUse = neighborListInUse;
}



@Override
public String toString() {
	return "RAN [common=" + common + ", neighborListInUse=" + neighborListInUse + 
			 "]";
}

}