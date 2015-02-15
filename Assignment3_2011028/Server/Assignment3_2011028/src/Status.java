public class Status {
	private String responseCode;
	private String responseData;
	
	public Status(String responseCode, String responseData) {
		setResponseCode(responseCode);
		setResponseData(responseData);
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseData() {
		return responseData;
	}

	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}
}
