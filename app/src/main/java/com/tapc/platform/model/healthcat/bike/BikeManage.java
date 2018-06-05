package com.tapc.platform.model.healthcat.bike;

import com.tapc.platform.model.healthcat.BaseCommunicationManage;
import com.tapc.platform.model.healthcat.BaseDataPack;
import com.tapc.platform.model.healthcat.SID;
import com.tapc.platform.model.tcp.TcpClient;

/**
 * Created by Administrator on 2018/4/12.
 */

public class BikeManage extends BaseCommunicationManage {
    private BikeDataPack mDataPack;

    public BikeManage() {
        super();
    }

    @Override
    public BaseDataPack getDataPack() {
        if (mDataPack == null) {
            mDataPack = new BikeDataPack(SID.BIKE);
        }
        return mDataPack;
    }

    public BikeManage(TcpClient tcpClient, String deviceId) {
        super(tcpClient, deviceId);
    }

    public boolean sendHeartbeat(byte command, BikeData bikeData) {
        byte[] data = mDataPack.getHeartbeatData(command, bikeData);
        return sendData(data);
    }

    public boolean sendReadInfoData(BikeData bikeData) {
        byte[] data = mDataPack.getReadInfoData(bikeData);
        return sendData(data);
    }

}
