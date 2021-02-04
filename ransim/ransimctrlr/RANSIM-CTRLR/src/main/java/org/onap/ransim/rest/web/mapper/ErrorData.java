package org.onap.ransim.rest.web.mapper;

public enum ErrorData {
	NO_DATA_FOUND("404", "No Data Found"),
	DATA_NOT_STORED("500","Data not Saved"),
	UNEXPECTED_ERROR("500","Exception Occured during the Operation");
	
	private String errorCode;
	private String errorMessage;
	ErrorData(String errorCode, String errorMessage) {
		this.errorCode=errorCode;
		this.errorMessage = errorMessage;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	@Override
	public String toString() {
		return "ErrorData [errorCode=" + errorCode + ", errorMessage=" + errorMessage + "]";
	}
}
