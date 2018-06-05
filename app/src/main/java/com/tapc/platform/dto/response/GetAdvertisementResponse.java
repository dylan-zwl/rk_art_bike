package com.tapc.platform.dto.response;

import com.tapc.platform.entity.AdvertisementEntity;

import java.util.ArrayList;


public class GetAdvertisementResponse extends Response {

	private static final long serialVersionUID = -7376688326292642692L;
	private ArrayList<AdvertisementEntity> response;

	public ArrayList<AdvertisementEntity> getResponse() {
		return response;
	}

	public void setResponse(ArrayList<AdvertisementEntity> response) {
		this.response = response;
	}

}
