package com.tapc.platform.dto.response;

import java.util.ArrayList;



public class GetUserFitnessResponse extends Response {

	private static final long serialVersionUID = -3225743986137721673L;
	private ArrayList<UserRecordResponse> response;
	public ArrayList<UserRecordResponse> getResponse() {
		return response;
	}
	public void setResponse(ArrayList<UserRecordResponse> response) {
		this.response = response;
	}


}
