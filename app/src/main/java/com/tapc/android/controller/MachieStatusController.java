package com.tapc.android.controller;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tapc.android.helper.SpeedRatioParam;
import com.tapc.android.uart.Commands;
import com.tapc.android.uart.GenericMessageHandler;
import com.tapc.android.uart.ReceivePacket;
import com.tapc.android.uart.TransferPacket;
import com.tapc.android.uart.Utility;
import com.tapc.platform.Config;
import com.tapc.platform.entity.BikeCtlType;

public class MachieStatusController extends GenericMessageHandler {
    public final static int STATUS_BIT_INVERTER_ERR_MASK_VALUE = 0xEE0000;
    private TransferPacket mMachineError;
    private TransferPacket mStartCommand;
    private TransferPacket mStopCommand;
	private TransferPacket mLoginQuitCommand;
    private TransferPacket mRegisterPreStart;
    private TransferPacket mPauseCommand;
    private TransferPacket mFancontrol;
    private TransferPacket mEnterERP;
    private TransferPacket mGetMachineVersion;
    private TransferPacket mSetMachineParam;
    private TransferPacket mGetMachineParam;
    private Handler mGetParamHandler;
    private SpeedRatioParam mSpeedRatioParam;
    private int mFanSpeedLeverl;
    private String mMachineVersion;

    private int mBikeWatt;
    private int mBikeLoad;
    private int mBikeRpmSpeed;
    private int mBikePwm;
    private int mBikeRounds;
    private int mDeviceId;

    public MachieStatusController(Handler uihandler) {
        super(uihandler);

        mStartCommand = new TransferPacket(Commands.SET_MCHINE_START);
        mStopCommand = new TransferPacket(Commands.SET_MACHINE_STOP);
        mRegisterPreStart = new TransferPacket(Commands.REGISTER_PRE_START);
        mPauseCommand = new TransferPacket(Commands.SET_MACHINE_PAUSE);
        mMachineError = new TransferPacket(Commands.GET_MACHIE_ERROR);
        mEnterERP = new TransferPacket(Commands.ENTER_ERP);
        mFancontrol = new TransferPacket(Commands.SET_FAN_CNTRL);
		mLoginQuitCommand = new TransferPacket(Commands.MACHINE_LOGIN_QUIT);
    }

    @Override
    public boolean shouldHandleCommand(Commands cmd) {
        return cmd == Commands.GET_MACHIE_ERROR || cmd == Commands.ENTER_ERP || cmd == Commands.GET_MCB_VERSION
                || cmd == Commands.GET_MACHINE_PARAM || cmd == Commands.GET_BIKE_DATA
                || cmd == Commands.SET_DEVICE_TYPE;
    }

    @Override
    public void handlePacket(ReceivePacket packet, Message msg) {
        if (packet.getCommand() == Commands.GET_MACHIE_ERROR) {
            int hardwareStatus = Utility.getIntegerFromByteArray(packet.getData()) & 0xFFFF
                    | MachieStatusController.STATUS_BIT_INVERTER_ERR_MASK_VALUE;
            Bundle bndl = new Bundle();
            bndl.putInt(HardwareStatusController.KEY_WORKOUT_STATUS, hardwareStatus);
            msg.setData(bndl);
        } else if (packet.getCommand() == Commands.ENTER_ERP) {
            Bundle b = new Bundle();
            b.putString("ERP", "Enter ERP started");
            msg.setData(b);
        } else if (packet.getCommand() == Commands.GET_MCB_VERSION) {
            byte[] data = packet.getData();
            if (data != null && data.length > 0) {
                String version = "";
                for (int i = 0; i < data.length; i++) {
                    if (i == (data.length - 1)) {
                        version = version + (data[i] & 0xff);
                    } else {
                        version = version + (data[i] & 0xff) + ".";
                    }
                }
                mMachineVersion = version;
            }
        } else if (packet.getCommand() == Commands.GET_MACHINE_PARAM) {
            if (mGetParamHandler != null) {
                if (mSpeedRatioParam == null) {
                    mSpeedRatioParam = new SpeedRatioParam();
                }
                if (packet.getData().length == Commands.GET_MACHINE_PARAM.getReceivePacketDataSize()) {
                    mSpeedRatioParam.setAllParam(packet.getData());
                    mGetParamHandler.sendEmptyMessage(SpeedRatioParam.RECV_DATA_SUCCESS);
                }
            }
        } else if (packet.getCommand() == Commands.GET_BIKE_DATA) {
            if (packet.getData().length == Commands.GET_BIKE_DATA.getReceivePacketDataSize()) {
                setBikeData(packet.getData());
            }
        } else if (packet.getCommand() == Commands.SET_DEVICE_TYPE) {
            mDeviceId = Utility.getIntegerFromByteArray(packet.getData()) & 0xff;
        }
    }

    public void startMachine(int speed, int incline) {
        byte[] speedDatabyte = Utility.getByteArrayFromInteger(speed, 2);
        byte[] inclineDatabyte = Utility.getByteArrayFromInteger(incline, 2);
        byte[] databyte = new byte[5];
        System.arraycopy(speedDatabyte, 0, databyte, 0, 2);
        System.arraycopy(inclineDatabyte, 0, databyte, 2, 2);
        byte bikeMode = 0x00;
        if (Config.sBikeCtlType == BikeCtlType.WATT) {
            bikeMode = 0x01;
        }
        databyte[4] = bikeMode;
        mStartCommand.setData(databyte);
        send(mStartCommand);
    }
	public void LoginQuitMachine(int status) {
		mLoginQuitCommand.setData(Utility.getByteArrayFromInteger(status, Commands.MACHINE_LOGIN_QUIT.getSendPacketDataSize()));
		send(mLoginQuitCommand);
	}

    public void registerPreStart() {
        send(mRegisterPreStart);
    }

    public void stopMachine(int incline) {
        mStopCommand
                .setData(Utility.getByteArrayFromInteger(incline, Commands.SET_MACHINE_STOP.getSendPacketDataSize()));
        send(mStopCommand);
    }

    public void pauseMachine() {
        send(mPauseCommand);
    }

    public void enterERP(int time) {
        mEnterERP.setData(Utility.getByteArrayFromInteger(time, Commands.ENTER_ERP.getSendPacketDataSize()));
        send(mEnterERP);
    }

    public void setFanSpeedLevel(int spdlvl) {
        mFanSpeedLeverl = spdlvl;
        mFancontrol.setData(Utility.getByteArrayFromInteger(mFanSpeedLeverl,
                Commands.SET_FAN_CNTRL.getSendPacketDataSize()));
        send(mFancontrol);
    }

    public int getFanSpeedLevel() {
        return mFanSpeedLeverl;
    }

    public void sendMachineErrorCmd() {
        send(mMachineError);
    }

    public void sendCtlVersionCmd() {
        if (mGetMachineVersion == null) {
            mGetMachineVersion = new TransferPacket(Commands.GET_MCB_VERSION);
        }
        mMachineVersion = "";
        send(mGetMachineVersion);
    }

    public String getCtlVersionValue() {
        return mMachineVersion;
    }

    public void setMachinePram(byte[] databyte) {
        if (mSetMachineParam == null) {
            mSetMachineParam = new TransferPacket(Commands.SET_MACHINE_PARAM);
        }
        mSetMachineParam.setData(databyte);
        send(mSetMachineParam);
    }

    public void getMachineParam(Handler getParamHandler) {
        if (mGetMachineParam == null) {
            mGetMachineParam = new TransferPacket(Commands.GET_MACHINE_PARAM);
        }
        if (getParamHandler != null) {
            mGetParamHandler = getParamHandler;
        }
        send(mGetMachineParam);
    }

    public SpeedRatioParam getMachinePramData() {
        return mSpeedRatioParam;
    }

    public int getBikeWatt() {
        return mBikeWatt;
    }

    public int getBikeLoad() {
        return mBikeLoad;
    }

    public int getBikeRpmSpeed() {
        return mBikeRpmSpeed;
    }

    public int getBikePwm() {
        return mBikePwm;
    }

    public int getDeviceId() {
        return mDeviceId;
    }

    public int getRounds() {
        return mBikeRounds;
    }

    public void setBikeData(byte[] data) {
        byte[] loads = new byte[1];
        byte[] rpmSpeed = new byte[1];
        byte[] watts = new byte[2];
        byte[] pwm = new byte[2];
        byte[] rounds = new byte[2];

        System.arraycopy(data, 0, loads, 0, 1);
        System.arraycopy(data, 1, rpmSpeed, 0, 1);
        System.arraycopy(data, 2, watts, 0, 2);
        System.arraycopy(data, 4, pwm, 0, 2);
        System.arraycopy(data, 6, rounds, 0, 2);


        mBikeLoad = Utility.getIntegerFromByteArray(loads);
        mBikeRpmSpeed = Utility.getIntegerFromByteArray(rpmSpeed);
        mBikeWatt = Utility.getIntegerFromByteArray(watts);
        mBikePwm = Utility.getIntegerFromByteArray(pwm);
        mBikeRounds = Utility.getIntegerFromByteArray(rounds);

        Log.d("bike data", " " + mBikeLoad + " " + mBikeRpmSpeed + " " + mBikeWatt + " " + mBikePwm + " " +
                mBikeRounds);
    }

    public void sendCommands(Commands commands, byte[] data) {
        TransferPacket command = new TransferPacket(commands);
        if (data != null) {
            command.setData(data);
        }
        send(command);
    }

}
