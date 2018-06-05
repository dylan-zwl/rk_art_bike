package com.tapc.platform.model.healthcat;

import com.tapc.platform.model.tcp.TcpClient;

/**
 * Created by Administrator on 2018/1/10.
 */
public abstract class BaseCommunicationManage {
    protected TcpClient mTcpClient;
    protected String mDeviceId;
    private BaseDataPack mBaseDataPack;

    public BaseCommunicationManage() {
        mBaseDataPack = getDataPack();
    }

    public abstract BaseDataPack getDataPack();

    public BaseCommunicationManage(TcpClient tcpClient, String deviceId) {
        mBaseDataPack = getDataPack();
        this.mTcpClient = tcpClient;
        this.mDeviceId = deviceId;
    }

    public boolean sendData(byte[] dataBuffer) {
        if (dataBuffer != null && mTcpClient != null) {
            mTcpClient.sendData(dataBuffer);
            return true;
        }
        return false;
    }

    public String getDeviceId() {
        return mDeviceId;
    }

    public void setDeviceId(String deviceId) {
        this.mDeviceId = deviceId;
    }

    public TcpClient getTcpClient() {
        return mTcpClient;
    }

    public void setTcpClient(TcpClient tcpClient) {
        this.mTcpClient = tcpClient;
    }


    public boolean login() {
        byte[] data = mBaseDataPack.login(mDeviceId + "000000000000");
        return sendData(data);
    }

    public boolean ackStatus(byte command, byte status) {
        byte[] data = mBaseDataPack.ackStatus(command, status);
        return sendData(data);
    }

    public boolean sendIcStart(byte command, String icId) {
        String sendStr = mDeviceId + "IC" + icId;
        byte[] data = mBaseDataPack.getDataStream(command, sendStr.getBytes());
        return sendData(data);
    }
}
