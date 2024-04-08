package com.tobias.des.dto.responses;

public class PhotoResponse {
	private String base64Data;

	public PhotoResponse(String base64Data) {
		this.base64Data = base64Data;
	}

	public String getBase64Data() {
		return base64Data;
	}

	public void setBase64Data(String base64Data) {
		this.base64Data = base64Data;
	}
}
