package org.onap.ransim.websocket.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NearRTRIC {
    
     private String idNearRTRIC;
     
     private Attributes attributes;

     @JsonProperty("GNBDUFunction")
     private List<GNBDUFunction> gNBDUFunction;

     @JsonProperty("GNBCUUPFunction")
     private List<GNBCUUPFunction> gNBCUUPFunction;

     public String getIdNearRTRIC () {
       return idNearRTRIC;
     }
     public void setIdNearRTRIC(String idNearRTRIC) {
       this.idNearRTRIC = idNearRTRIC;
     }
     public Attributes getAttributes() {
       return attributes;
     }
     public void setAttributes(Attributes attributes) {
       this.attributes = attributes;
     }
     public List<GNBDUFunction> getgNBDUFunction() {
       return gNBDUFunction;
     }
     public void setgNBDUFunction(List<GNBDUFunction> gNBDUFunction) {
       this.gNBDUFunction = gNBDUFunction;
     }
     public List<GNBCUUPFunction> getgNBCUUPFunction() {
       return gNBCUUPFunction;
     }
     public void setgNBCUUPFunction(List<GNBCUUPFunction> gNBCUUPFunction) {
       this.gNBCUUPFunction = gNBCUUPFunction;
     }
     public NearRTRIC()
     {
   
     }
}