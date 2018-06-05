package com.tapc.android.controller;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tapc.android.uart.Commands;
import com.tapc.android.uart.GenericMessageHandler;
import com.tapc.android.uart.ReceivePacket;
import com.tapc.android.uart.TransferPacket;
import com.tapc.android.uart.Utility;

public class InclineController extends GenericMessageHandler {
	static private String INCLINECAL_FINISH = "inclinecalfinish";
	private int mTargetIncline = 0;

	private TransferPacket mSetInclinePacket;
	private TransferPacket mSetInclinecalPacket;
	private TransferPacket mGetInclineCalFinish;
	private TransferPacket mGetInclinePacket;

	public InclineController(Handler uihandler) {
		super(uihandler);

		mGetInclinePacket = new TransferPacket(Commands.GET_ADC_CURRENT);
		mSetInclinePacket = new TransferPacket(Commands.SET_ADC_TARGET);
	}

	@Override
	public boolean shouldHandleCommand(Commands cmd) {
		return cmd == Commands.GET_ADC_CURRENT || cmd == Commands.GET_INCLNE_CAL_FINISH;
	}

	@Override
	public void handlePacket(ReceivePacket packet, Message msg) {
		if (packet.getCommand() == Commands.GET_ADC_CURRENT) {
			Bundle bndl = new Bundle();
			bndl.putInt("INCLINE_VALUE", Utility.getIntegerFromByteArray(packet.getData()));
			msg.setData(bndl);
		} else if (packet.getCommand() == Commands.GET_INCLNE_CAL_FINISH) {
			Bundle bndl = new Bundle();
			bndl.putInt("INCLNE_CAL_FINISH", Utility.getIntegerFromByteArray(packet.getData()));
			msg.setData(bndl);
		}
	}

	public void startInclinecal() {
		if (mSetInclinecalPacket == null) {
			mSetInclinecalPacket = new TransferPacket(Commands.SET_INCLNE_CAL);
			mGetInclineCalFinish = new TransferPacket(Commands.GET_INCLNE_CAL_FINISH);
		}
		getPeriodicCommander().addCommandtoList(INCLINECAL_FINISH, mGetInclineCalFinish);
		send(mSetInclinecalPacket);
	}

	public void stopInclinecal() {
		getPeriodicCommander().removeCommandFromList(INCLINECAL_FINISH);
	}

	public void setIncline(int incline, int pwm) {
		mTargetIncline = incline;
		Log.d("set incline", "" + incline);
		byte[] data = new byte[4];
		byte[] inclineBytes = new byte[2];
		byte[] inclinePwm = new byte[2];
		inclineBytes = Utility.getByteArrayFromInteger(mTargetIncline, 2);
		inclinePwm = Utility.getByteArrayFromInteger(pwm, 2);
		System.arraycopy(inclineBytes, 0, data, 0, 2);
		System.arraycopy(inclinePwm, 0, data, 2, 2);
		mSetInclinePacket.setData(data);
		send(mSetInclinePacket);
	}

	public void getIncline() {
		send(mGetInclinePacket);
	}
}
