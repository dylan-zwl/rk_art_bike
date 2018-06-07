package com.tapc.platform.witget.scancode;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tapc.platform.model.healthcat.BaseCtlModel;
import com.tapc.platform.model.healthcat.bike.BikeCtlModel;
import com.tapc.platform.model.healthcat.bike.BikeData;
import com.tapc.platform.model.healthcat.power.PowerCtlModel;
import com.tapc.platform.model.healthcat.power.PowerData;


public class ScanCodePresenter implements ScanCodeContract.Presenter {
    private ScanCodeContract.View mView;
    private BaseCtlModel mModel;

    public ScanCodePresenter(Context context, @NonNull final ScanCodeContract.View view, DeviceType deviceType) {
        mView = view;
        switch (deviceType) {
            case BIKE:
                mModel = new BikeCtlModel(context.getApplicationContext());
                initBike((BikeCtlModel) mModel, view);
                break;
            case POWER:
                mModel = new PowerCtlModel(context.getApplicationContext());
                initPower((PowerCtlModel) mModel, view);
                break;
        }
    }

    @Override
    public void start() {
        mModel.connectServer();
    }

    @Override
    public void stop() {
        mModel.disConnectServer();
    }

    @Override
    public void sendHeartbeat() {
        mModel.sendHeartbeat();
    }

    @Override
    public boolean login(String mac) {
        return mModel.login(mac);
    }

    @Override
    public void setDeviceId(@Nullable String deviceId) {
        mModel.setDeviceId(deviceId);
    }

    @Override
    public boolean sendIcStart(String icId) {
        return mModel.sendIcStart(icId);
    }

    private void initBike(final BikeCtlModel ctlModel, @NonNull final ScanCodeContract.View view) {
        //健身车
        ctlModel.setCtlLister(new BikeCtlModel.BikeCtlLister() {
            @Override
            public BikeData serverReadInfo() {
                return (BikeData) view.getDeviceInfo();
            }

            @Override
            public void serverSetResistance(byte resistance) {
                view.setParameter(resistance);
            }

            @Override
            public boolean serverSetRunStatus(boolean isStart) {
                return view.setRunStatus(isStart);
            }

            @Override
            public boolean serverSetLock(boolean lock) {
                return view.serverSetLock(lock);
            }

            @Override
            public void icLoginStatus(byte status) {
                view.icLoginStatus(status);
            }

            @Override
            public void showQrcode(String qrcodeStr) {
                view.showQrcode(qrcodeStr);
            }

            @Override
            public void connectServerResult(boolean isSuccess) {
                view.connectServerResult(isSuccess);
            }

            @Override
            public void login(boolean isSuccess) {
                if (isSuccess) {
                    view.showQrcode(mModel.getDeviceId());
                }
                mView.loginStatus(isSuccess);
            }
        });
    }

    private void initPower(final PowerCtlModel ctlModel, @NonNull final ScanCodeContract.View view) {
        //重训
        ctlModel.setCtlLister(new PowerCtlModel.PowerCtlLister() {
            @Override
            public PowerData serverReadInfo() {
                return (PowerData) view.getDeviceInfo();
            }

            @Override
            public boolean serverSetRunStatus(boolean isStart) {
                return view.setRunStatus(isStart);
            }

            @Override
            public boolean serverSetLock(boolean lock) {
                return view.serverSetLock(lock);
            }

            @Override
            public void icLoginStatus(byte status) {
                view.icLoginStatus(status);
            }

            @Override
            public void showQrcode(String qrcodeStr) {
                view.showQrcode(qrcodeStr);
            }

            @Override
            public void connectServerResult(boolean isSuccess) {
                view.connectServerResult(isSuccess);
            }

            @Override
            public void login(boolean isSuccess) {
                if (isSuccess) {
                    view.showQrcode(ctlModel.getDeviceId());
                }
                mView.loginStatus(isSuccess);
            }
        });
    }
}
