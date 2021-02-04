package org.onap.ransim.rest.web.mapper;

public class PLMNInfoModel{
	private String pLMNId;
	private NSSAIData sNSSAI;

	public String getpLMNId() {
		return pLMNId;
	}
	public void setpLMNId(String pLMNId) {
		this.pLMNId = pLMNId;
	}
	public NSSAIData getsNSSAI() {
		return sNSSAI;
	}
	public void setsNSSAI(NSSAIData sNSSAI) {
		this.sNSSAI = sNSSAI;
	}
	@Override
	public String toString() {
		return "PLMNInfoModel [pLMNId=" + pLMNId + ", sNSSAI=" + sNSSAI + "]";
	}
	
}
