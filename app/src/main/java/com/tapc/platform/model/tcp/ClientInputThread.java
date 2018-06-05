package com.tapc.platform.model.tcp;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientInputThread extends Thread {
    private boolean isStart = true;
    private InputStream mInputStream;
    private SocketListener mListener;

    private boolean isCanRead = true;

    public ClientInputThread(Socket socket) {
        try {
            mInputStream = socket.getInputStream();
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
        this.mListener = listener;
    }

    @Override
    public void run() {
        try {
            while (isStart) {
                if (isCanRead) {
                    byte[] data = new byte[1];

                    List<Byte> dataBuffer = new ArrayList<>();
                    while (true) {
                        mInputStream.read(data);
                        dataBuffer.add(data[0]);
                        if (data[0] == 0x16 && dataBuffer.size() >= 7) {
                            int length = dataBuffer.get(4);
                            if (dataBuffer.size() == (length + 7)) {
                                break;
                            } else if (dataBuffer.size() > (length + 7)) {
                                dataBuffer.clear();
                            }
                        } else if (dataBuffer.size() > 100) {
                            dataBuffer.clear();
                        }
                    }
                    byte[] dataSend = new byte[dataBuffer.size()];
                    for (int i = 0; i < dataBuffer.size(); i++) {
                        dataSend[i] = dataBuffer.get(i);
                    }
                    dataBuffer.clear();
                    if (mListener != null) {
                        mListener.onMessage(dataSend);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (mInputStream != null) {
                try {
                    mInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("tcp net", "clientInputThread close");
    }
}