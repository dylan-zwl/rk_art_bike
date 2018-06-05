package com.tapc.platform.model.healthcat.bike;

import android.content.Context;

import com.tapc.platform.model.healthcat.AckStatus;
import com.tapc.platform.model.healthcat.BaseCommunicationManage;
import com.tapc.platform.model.healthcat.BaseCtlModel;
import com.tapc.platform.model.healthcat.Command;

/**
 * Created by Administrator on 2018/4/12.
 */

public class BikeCtlModel extends BaseCtlModel {
    private BikeManage mManage;
    private BikeCtlLister mCtlLister;

    public BikeCtlModel(Context context) {
        super(context);
    }

    @Override
    public void sendHeartbeat() {
        mManage.sendHeartbeat(Command.Bike.D_UPLOAD_INFO, (BikeData) mCtlLister.serverReadInfo());
    }

    @Override
    public BaseCommunicationManage getManage() {
        mManage = new BikeManage();
        return mManage;
    }

    public interface BikeCtlLister extends BaseCtlModel.BaseListener {

        void serverSetResistance(byte resistance);
    }

    public void setCtlLister(BikeCtlLister listener) {
        setBaseListener(listener);
        this.mCtlLister = listener;
    }

    @Override
    public boolean login() {
        return mManage.login();
    }

    @Override
    public boolean sendIcStart(String icId) {
        return mManage.sendIcStart(Command.Bike.D_IC_START, icId);
    }

    @Override
    protected void recvMessage(byte[] dataBuffer) {
        byte cmd = mManage.getDataPack().getComman(dataBuffer);
        byte[] data = null;
        if (cmd != 0) {
            data = mManage.getDataPack().getCommanDataPack(dataBuffer);
        }
        switch (cmd) {
            case Command.S_LOGIN:
                byte loginStatus = mManage.getDataPack().getCommanData(data, 0);
                if (loginStatus != -1) {
                    switch (loginStatus) {
                        case AckStatus.SUCCESS:
                            mCtlLister.login(true);
                            break;
                        case AckStatus.FAIL:
                            mCtlLister.login(false);
                            break;
                    }
                }
                break;
            case Command.Bike.S_READ_INFO:
                mManage.sendReadInfoData((BikeData) mCtlLister.serverReadInfo());
                break;
            case Command.Bike.S_READ_STATUS:
                mManage.sendHeartbeat(Command.Bike.D_READ_STATUS, (BikeData) mCtlLister.serverReadInfo());
                break;
            case Command.Bike.S_SET_RESISTANCE:
                byte resistance = mManage.getDataPack().getCommanData(data, 0);
                if (resistance != -1) {
                    mCtlLister.serverSetResistance(resistance);
                }
                mManage.ackStatus(Command.Bike.D_SET_RESISTANCE, AckStatus.SUCCESS);
                break;
            case Command.Bike.S_START_STOP:
                setRunStatus(Command.Bike.D_START_STOP, data);
                break;
            case Command.Bike.S_LOCK:
                setLock(Command.Bike.D_LOCK, data);
                break;
            case Command.Bike.S_UPLOAD_INFO:
                mHeartBeat.resetCount();
                break;
            case Command.Bike.S_IC_START:
                if (data.length >= 1) {
                    mCtlLister.icLoginStatus(data[0]);
                }
                break;
        }
    }
}
