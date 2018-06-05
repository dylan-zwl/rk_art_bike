package com.tapc.platform.model.healthcat.power;

import com.tapc.platform.model.healthcat.BaseCommunicationManage;
import com.tapc.platform.model.healthcat.BaseDataPack;
import com.tapc.platform.model.healthcat.SID;
import com.tapc.platform.model.tcp.TcpClient;

/**
 * Created by Administrator on 2018/4/12.
 */

public class PowerManage extends BaseCommunicationManage {
    private PowerDataPack mDataPack;

    public PowerManage() {
        super();
    }

    @Override
    public BaseDataPack getDataPack() {
        if (mDataPack == null) {
            mDataPack = new PowerDataPack(SID.POWER);
        }
        return mDataPack;
    }

    public PowerManage(TcpClient tcpClient, String deviceId) {
        super(tcpClient, deviceId);
    }

    public boolean sendHeartbeat(byte command, PowerData powerData) {
        byte[] data = mDataPack.getHeartbeatData(command, powerData);
        return sendData(data);
    }
}
