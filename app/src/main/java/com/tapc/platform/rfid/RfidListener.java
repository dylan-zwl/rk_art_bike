package com.tapc.platform.rfid;

public interface RfidListener {
	public void detectCard(byte[] uid);
}
