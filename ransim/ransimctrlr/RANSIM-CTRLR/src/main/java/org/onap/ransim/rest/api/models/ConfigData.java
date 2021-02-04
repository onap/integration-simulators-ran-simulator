package org.onap.ransim.rest.api.models;

public class ConfigData {

private int maxNumberOfConns;

public ConfigData() {
}
public ConfigData(int maxNumberOfConns) {
super();
this.maxNumberOfConns = maxNumberOfConns;
}

public int getMaxNumberOfConns() {
return maxNumberOfConns;
}

public void setMaxNumberOfConns(int maxNumberOfConns) {
this.maxNumberOfConns = maxNumberOfConns;
}
}
