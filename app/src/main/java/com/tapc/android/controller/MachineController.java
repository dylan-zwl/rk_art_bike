package com.tapc.android.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.tapc.android.data.MessageDefine;
import com.tapc.android.helper.SpeedRatioParam;
import com.tapc.android.uart.Commands;
import com.tapc.platform.TapcApp;
import com.tapc.platform.utils.SysUtils;

import java.util.Map;

@SuppressLint("HandlerLeak")
public final class MachineController {
    public static final String MSG_WORKOUT_STATUS = "tapc.status.action";
    public static final String MSG_KEY_BOARD = "tapc.key.action";
    private Context mcontext;
    private int mKeyCode;
    private int mSpeed;
    private int mPaceRate;
    private int mHeartRate;
    private int mIncline;
    private int mHardwareStatus;
    private int mInclineCalStatus;
    private Handler messageHandler;
    private static MachineController mMachineController;
    private MachieStatusController mMachineStatusController;
    private KeyboardController mKeyboardController;
    private HeartController mHeartController;
    private SpeedController mSpeedController;
    private InclineController mInclineController;
    private IOUpdateController mIOUpdateController;
    private FootRateController mFootRateController;
    private HardwareStatusController mHardwareStatuscontroller;
    private Handler mUIGetSpeed;
    private Handler mUIGetIncline;

    public MachineController() {

    }

    public static MachineController getInstance() {
        if (null == mMachineController) {
            mMachineController = new MachineController();
        }
        return mMachineController;
    }

    public void initController() {
        if (null != mMachineController) {
            messageHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.getData().containsKey(HardwareStatusController.KEY_WORKOUT_STATUS)) {
                        mHardwareStatus = (Integer) msg.getData().get(HardwareStatusController.KEY_WORKOUT_STATUS);
                        Intent intent = new Intent();
                        intent.setAction(MessageDefine.MSG_WORKOUT_STATUS);
                        intent.putExtra(HardwareStatusController.KEY_WORKOUT_STATUS, mHardwareStatus);
                        mcontext.sendBroadcast(intent);
                    } else if (msg.getData().containsKey("HEART_RATE")) {
                        mHeartRate = (Integer) msg.getData().get("HEART_RATE");
                    } else if (msg.getData().containsKey("FOOT_RATE")) {
                        mPaceRate = (Integer) msg.getData().get("FOOT_RATE");
                    } else if (msg.getData().containsKey("SPEED_VALUE")) {
                        mSpeed = (Integer) msg.getData().get("SPEED_VALUE");
                        if (mUIGetSpeed != null) {
                            setUIIntMessage(mUIGetSpeed, "SPEED_VALUE", mSpeed);
                        }
                    } else if (msg.getData().containsKey("INCLINE_VALUE")) {
                        mIncline = (Integer) msg.getData().get("INCLINE_VALUE");
                        if (mUIGetIncline != null) {
                            setUIIntMessage(mUIGetIncline, "INCLINE_VALUE", mIncline);
                        }
                    } else if (msg.getData().containsKey(KeyboardController.KEY_CODE)) {
                        mKeyCode = (Integer) msg.getData().get(KeyboardController.KEY_CODE);
                        Intent intent = new Intent();
                        intent.setAction(MSG_KEY_BOARD);
                        intent.putExtra(KeyboardController.KEY_CODE, mKeyCode);
                        mcontext.sendBroadcast(intent);
                    } else if (msg.getData().containsKey("INCLNE_CAL_FINISH")) {
                        mInclineCalStatus = (Integer) msg.getData().get("INCLNE_CAL_FINISH");
                    }
                }
            };
            mMachineStatusController = new MachieStatusController(messageHandler);
            mHeartController = new HeartController(messageHandler);
            mSpeedController = new SpeedController(messageHandler);
            mInclineController = new InclineController(messageHandler);
            mFootRateController = new FootRateController(messageHandler);
            mHardwareStatuscontroller = new HardwareStatusController(messageHandler);
            mKeyboardController = new KeyboardController(messageHandler);
        }
    }

    public void start() {
        mHeartController.start();
        mSpeedController.start();
        mInclineController.start();
        mFootRateController.start();
        mHardwareStatuscontroller.start();
        mMachineStatusController.start();
        mKeyboardController.start();
    }

    public void stop() {
        mHeartController.stop();
        mSpeedController.stop();
        mInclineController.stop();
        mFootRateController.stop();
        mHardwareStatuscontroller.stop();
        mMachineStatusController.stop();
        mKeyboardController.stop();
    }

    public void registerPreStart() {
        mMachineStatusController.registerPreStart();
    }

    public void updateMCU(String filePath, Handler IOUpdateMsg) {
        mIOUpdateController = new IOUpdateController(IOUpdateMsg);
        mIOUpdateController.start();
        mIOUpdateController.updateIO(filePath);
    }

    public void stopIOUpdateController() {
        if (mIOUpdateController != null) {
            mIOUpdateController.stop();
            mIOUpdateController = null;
        }
    }

    public void enterErpStatus(int delayTime) {
        mMachineStatusController.enterERP(delayTime);
    }

    public void setReceiveBroadcast(Context context) {
        mcontext = context;
    }

    public void setSpeed(int speed) {
        mSpeedController.setSpeed(speed);
    }

    public void setIncline(int incline) {
        if (TapcApp.getInstance().isTestOpen) {
            Map<String, Integer> loadMap = SysUtils.getLoadMap(mcontext);
            int pwm = loadMap.get("" + incline).intValue();
            mInclineController.setIncline(incline, pwm);
        } else {
            mInclineController.setIncline(incline, 0);
        }
    }

    public void startInclinecal() {
        mInclineCalStatus = -1;
        mInclineController.startInclinecal();
    }

    public void stopInclinecal() {
        mInclineController.stopInclinecal();
    }

    public void setFanLevel(int spdlvl) {
        mMachineStatusController.setFanSpeedLevel(spdlvl);
    }

	public void LoginQuitMachine(int status) {
		mMachineStatusController.LoginQuitMachine(status);
	}
	
    public void startMachine(int speed, int incline) {
        mMachineStatusController.startMachine(speed, incline);
    }

    public void stopMachine(int incline) {
        mMachineStatusController.stopMachine(incline);
    }

    public void pauseMachine() {
        mMachineStatusController.pauseMachine();
    }

    public void sendMachineErrorCmd() {
        mMachineStatusController.sendMachineErrorCmd();
    }

    public void sendCtlVersionCmd() {
        mMachineStatusController.sendCtlVersionCmd();
    }

    public String getCtlVersionValue() {
        return mMachineStatusController.getCtlVersionValue();
    }

    public void setMachinePram(byte[] databyte) {
        mMachineStatusController.setMachinePram(databyte);
    }

    public void getMachinePram(Handler getParamHandler) {
        mMachineStatusController.getMachineParam(getParamHandler);
    }

    public SpeedRatioParam getMachinePramData() {
        return mMachineStatusController.getMachinePramData();
    }

    public int getFanLevel() {
        return mMachineStatusController.getFanSpeedLevel();
    }

    public int getInclinecalStatus() {
        return mInclineCalStatus;
    }

    public int getDeviceId() {
        return mMachineStatusController.getDeviceId();
    }

    public void getIncline(Handler handler) {
        mUIGetIncline = handler;
        mInclineController.getIncline();
    }

    public void getSpeed(Handler handler) {
        mUIGetSpeed = handler;
        mSpeedController.getSpeed();
    }

    public int getIncline() {
        return mMachineStatusController.getBikeLoad();
    }

    public int getSpeed() {
        return mMachineStatusController.getBikeWatt();
    }

    public int getRpmSpeed() {
        return mMachineStatusController.getBikeRpmSpeed();
    }

    public int getPwm() {
        return mMachineStatusController.getBikePwm();
    }

    public int getRounds() {
        return mMachineStatusController.getRounds();
    }

    public int getPaceRate() {
        return mPaceRate;
    }

    public int getHeartRate() {
        return mHeartRate;
    }

    public int getHardwareStatus() {
        return mHardwareStatus;
    }

    public void sendCommands(Commands commands, byte[] data) {
        mMachineStatusController.sendCommands(commands, data);
    }

    public void setUIIntMessage(Handler handler, String key, int value) {
        Bundle bndl = new Bundle();
        bndl.putInt(key, value);
        Message msg = new Message();
        msg.setData(bndl);
        handler.sendMessage(msg);
    }
}
