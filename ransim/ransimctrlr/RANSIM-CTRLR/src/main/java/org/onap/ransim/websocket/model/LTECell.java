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
"idGNBCUCPFunction",
"PLMNID",
"idNRCellRelation",
"nRTCI",
"isHOAllowed"
})
@Generated("jsonschema2pojo")
public class LTECell {

@JsonProperty("idGNBCUCPFunction")
private String idGNBCUCPFunction;
@JsonProperty("PLMNID")
private String plmnid;
@JsonProperty("idNRCellRelation")
private String idNRCellRelation;
@JsonProperty("nRTCI")
private Integer nRTCI;
@JsonProperty("isHOAllowed")
private String isHOAllowed;
public String getIdGNBCUCPFunction() {
	return idGNBCUCPFunction;
}
public void setIdGNBCUCPFunction(String idGNBCUCPFunction) {
	this.idGNBCUCPFunction = idGNBCUCPFunction;
}
public String getPlmnid() {
	return plmnid;
}
public void setPlmnid(String plmnid) {
	this.plmnid = plmnid;
}
public String getIdNRCellRelation() {
	return idNRCellRelation;
}
public void setIdNRCellRelation(String idNRCellRelation) {
	this.idNRCellRelation = idNRCellRelation;
}
public Integer getnRTCI() {
	return nRTCI;
}
public void setnRTCI(Integer nRTCI) {
	this.nRTCI = nRTCI;
}
public String getIsHOAllowed() {
	return isHOAllowed;
}
public void setIsHOAllowed(String isHOAllowed) {
	this.isHOAllowed = isHOAllowed;
}
@Override
public String toString() {
	return "LTECell [idGNBCUCPFunction=" + idGNBCUCPFunction + ", plmnid=" + plmnid + ", idNRCellRelation="
			+ idNRCellRelation + ", nRTCI=" + nRTCI + ", isHOAllowed=" + isHOAllowed + "]";
}




}