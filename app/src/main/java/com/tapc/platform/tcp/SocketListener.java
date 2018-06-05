package com.tapc.platform.tcp;

public interface SocketListener {
	public void onMessage(byte[] dataBuffer);

	public void onOpen(boolean isConnect);

	public void onClose();

	public void onError(String error);
}
