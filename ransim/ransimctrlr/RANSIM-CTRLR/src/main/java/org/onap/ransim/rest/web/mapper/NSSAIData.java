package org.onap.ransim.rest.web.mapper;


import org.onap.ransim.rest.api.models.NSSAIConfig;

public class NSSAIData{
	private String sNSSAI;
	private String status;
	private String globalSubscriberId;
	private String subscriptionServiceType;
	//private Map<String, Integer> configData;
	private NSSAIConfig configData;
	public String getsNSSAI() {
		return sNSSAI;
	}
	public void setsNSSAI(String sNSSAI) {
		this.sNSSAI = sNSSAI;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getGlobalSubscriberId() {
		return globalSubscriberId;
	}
	public void setGlobalSubscriberId(String globalSubscriberId) {
		this.globalSubscriberId = globalSubscriberId;
	}
	public String getSubscriptionServiceType() {
		return subscriptionServiceType;
	}
	public void setSubscriptionServiceType(String subscriptionServiceType) {
		this.subscriptionServiceType = subscriptionServiceType;
	}
	public NSSAIConfig getConfigData() {
		return configData;
	}
	public void setConfigData(NSSAIConfig configData) {
		this.configData = configData;
	}
}