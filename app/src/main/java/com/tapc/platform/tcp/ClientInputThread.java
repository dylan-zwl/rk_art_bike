package com.tapc.platform.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import android.os.SystemClock;
import android.util.Log;

public class ClientInputThread extends Thread {
	private Socket socket;
	private boolean isStart = true;
	private InputStream ois;
	private SocketListener listener;

	private boolean isCanRead = true;

	public ClientInputThread(Socket socket) {
		this.socket = socket;
		try {
			ois = socket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	public void setReadStatus(boolean vailable) {
		isCanRead = vailable;
	}

	public void setListener(SocketListener listener) {
		this.listener = listener;
	}

	@Override
	public void run() {
		try {
			while (isStart) {
				if (isCanRead) {
					int length = ois.available();
					if (length > 0) {
						byte[] dataBuffer = new byte[length];
						ois.read(dataBuffer);
						if (listener != null) {
							listener.onMessage(dataBuffer);
						}
					}
					SystemClock.sleep(5);
				}
			}
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("tcp net", "clientInputThread close");
	}
}