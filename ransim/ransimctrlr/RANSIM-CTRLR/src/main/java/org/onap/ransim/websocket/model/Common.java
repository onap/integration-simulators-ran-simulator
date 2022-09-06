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
"CellIdentity"
})
@Generated("jsonschema2pojo")
public class Common {

@JsonProperty("CellIdentity")
private String cellIdentity;


@JsonProperty("CellIdentity")
public String getCellIdentity() {
return cellIdentity;
}

@JsonProperty("CellIdentity")
public void setCellIdentity(String cellIdentity) {
this.cellIdentity = cellIdentity;
}



@Override
public String toString() {
	return "Common [cellIdentity=" + cellIdentity +   "]";
}

}