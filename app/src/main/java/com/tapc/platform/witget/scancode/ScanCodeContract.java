package com.tapc.platform.witget.scancode;

import android.support.annotation.Nullable;

public interface ScanCodeContract {

    interface View {
        void connectServerResult(boolean isSuccess);

        void showQrcode(String qrcode);

        void loginStatus(boolean isSuccess);

        boolean serverSetLock(boolean lock);

        Object getDeviceInfo();

        void setParameter(byte parameter);

        boolean setRunStatus(boolean isStart);

        void icLoginStatus(byte status);
    }

    interface Presenter {
        void start();

        void stop();

        void sendHeartbeat();

        boolean login();

        void setDeviceId(@Nullable String deviceId);

        boolean sendIcStart(String icId);
    }
}
