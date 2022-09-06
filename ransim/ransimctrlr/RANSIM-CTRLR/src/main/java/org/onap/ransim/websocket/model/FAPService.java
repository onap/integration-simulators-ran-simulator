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
"alias",
"CellConfig"
})
@Generated("jsonschema2pojo")
public class FAPService {

@JsonProperty("idNRCellCU")
private String idNRCellCU;
@JsonProperty("CellConfig")
private CellConfig cellConfig;



@JsonProperty("idNRCellCU")
public String getIdNRCellCU() {
	return idNRCellCU;
}

@JsonProperty("idNRCellCU")
public void setIdNRCellCU(String idNRCellCU) {
	this.idNRCellCU = idNRCellCU;
}

@JsonProperty("CellConfig")
public CellConfig getCellConfig() {
return cellConfig;
}

@JsonProperty("CellConfig")
public void setCellConfig(CellConfig cellConfig) {
this.cellConfig = cellConfig;
}


@Override
public String toString() {
	return "FAPService [alias=" + idNRCellCU + ", cellConfig=" + cellConfig + 
			 "]";
}

}