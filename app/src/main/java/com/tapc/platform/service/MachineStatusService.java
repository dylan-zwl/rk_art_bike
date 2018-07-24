package com.tapc.platform.service;

/**
 * StatusReceiver.java[v 1.0.0]
 * classes:com.jht.tapc.platform.activity.StatusReceiver
 * fch Create of at 2015骞�鏈�0鏃�涓嬪�?:44:32
 */

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.tapc.android.controller.HardwareStatusController;
import com.tapc.android.controller.KeyboardController;
import com.tapc.android.controller.MachieStatusController;
import com.tapc.android.controller.MachineController;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.sportsrunctrl.SportsEngin;
import com.tapc.platform.utils.SysUtils;
import com.tapc.platform.witget.CountdownDialog;
import com.tapc.platform.witget.DeviceSelDialog;
import com.tapc.platform.witget.FullScreenDialog;
import com.tapc.platform.witget.MachineStatusDialog;
import com.tapc.platform.witget.MsgPromptDialog;
import com.tapc.platform.witget.scancode.DeviceType;
import com.tapc.platform.witget.scancode.ScanCodeDialog;
import com.tapc.platform.workout.MessageType;

public class MachineStatusService extends Service {
    public static final String COUNTDOWN_NAME = "com.tapc.countdown";
    private static final int NO_ERROR = 0xf00000;
    private static final int SAFE_KEY_SHOW = 0xf10000;
    private static final int LIFTER_ERROR = 0xf20000;
    private static final int SAFE_KEY_HIDE = 0xf30000;
    public static final int DELAY_TIME = 5000;
    public static final int NULL_NUM = -1;
    private int mPressnumber = NULL_NUM;
    private boolean hasError = false;
    private int oldErrorStaus = -1;

    private WindowManager.LayoutParams mKeyBoardParams;
    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;
    private MsgPromptDialog mKeyBoardDialog;
    private MachineStatusDialog mMachineStatusDialog;
    private CountdownDialog mCountdownDialog;
    private ScanCodeDialog mScanCodeDialog;
    private DeviceSelDialog mDeviceSelDialog;
    private FullScreenDialog mFullScreenDialog;

    private Handler mKeyHandler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate();
    }

    @SuppressLint("HandlerLeak")
    public void onStart(Intent intent, int startId) {
        mLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
                        | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH, PixelFormat.TRANSPARENT);
        mKeyBoardParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
                        | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH, PixelFormat.TRANSPARENT);

        LayoutParams deviceLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
                        | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH, PixelFormat.TRANSPARENT);

        LayoutParams fullScreenLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
                        | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
                PixelFormat.TRANSPARENT);
        fullScreenLayoutParams.gravity = Gravity.TOP;
        fullScreenLayoutParams.x = 960;
        fullScreenLayoutParams.y = 440;

        mWindowManager = (WindowManager) getSystemService("window");

        mKeyBoardDialog = new MsgPromptDialog(this);
        mKeyBoardDialog.setVisibility(View.GONE);
        mWindowManager.addView(mKeyBoardDialog, mKeyBoardParams);

        mFullScreenDialog = new FullScreenDialog(this);
        mWindowManager.addView(mFullScreenDialog, fullScreenLayoutParams);

        mCountdownDialog = new CountdownDialog(this);
        mCountdownDialog.setVisibility(View.GONE);
        mWindowManager.addView(mCountdownDialog, mLayoutParams);

        initScanCodeDialog();

        mMachineStatusDialog = new MachineStatusDialog(this);
        mMachineStatusDialog.setVisibility(View.GONE);
        mWindowManager.addView(mMachineStatusDialog, mLayoutParams);

        mDeviceSelDialog = new DeviceSelDialog(this);
        mDeviceSelDialog.setVisibility(View.GONE);
        mWindowManager.addView(mDeviceSelDialog, deviceLayoutParams);

        CountdownStatusReceiver countdown = new CountdownStatusReceiver();
        IntentFilter countdownFilter = new IntentFilter(COUNTDOWN_NAME);
        registerReceiver(countdown, countdownFilter);

        StatusReceiver mStatusReceiver = new StatusReceiver();
        IntentFilter statusFilter = new IntentFilter(MachineController.MSG_WORKOUT_STATUS);
        registerReceiver(mStatusReceiver, statusFilter);

        KeyboardReceiver mKeyboardReceiver = new KeyboardReceiver();
        IntentFilter keyFilter = new IntentFilter(MachineController.MSG_KEY_BOARD);
        registerReceiver(mKeyboardReceiver, keyFilter);

        DeviceSelReceiver mDeviceSelReceiver = new DeviceSelReceiver();
        IntentFilter deviceFilter = new IntentFilter(DeviceSelDialog.ACTION_SHOW_DEVICE);
        registerReceiver(mDeviceSelReceiver, deviceFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("stop sevice", "MachineStatusService");
        mWindowManager.removeView(mCountdownDialog);
        mWindowManager.removeView(mMachineStatusDialog);
        mWindowManager.removeView(mKeyBoardDialog);
        mWindowManager.removeView(mScanCodeDialog);
        mWindowManager.removeView(mDeviceSelDialog);
        mWindowManager.removeView(mFullScreenDialog);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public class CountdownStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mCountdownHandler.sendMessageDelayed(mCountdownHandler.obtainMessage(3), 0);
        }
    }

    private void initScanCodeDialog() {
        mScanCodeDialog = new ScanCodeDialog(this, DeviceType.BIKE);
        mScanCodeDialog.addViewToWindow();
        mScanCodeDialog.hide();
    }

    public class DeviceSelReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean visibility = intent.getExtras().getBoolean("visibility");
            if (visibility) {
                mDeviceSelDialog.setVisibility(View.VISIBLE);
            } else {
                mDeviceSelDialog.setVisibility(View.GONE);
            }
        }
    }

    private void countdownVoice() {
        SystemClock.sleep(200);
        SysUtils.playBeep(MachineStatusService.this, R.raw.start);
    }

    @SuppressLint("HandlerLeak")
    private Handler mCountdownHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    countdownVoice();
                    mCountdownDialog.setNumShow("3");
                    mCountdownDialog.setVisibility(View.VISIBLE);
                    mCountdownHandler.sendMessageDelayed(mCountdownHandler.obtainMessage(2), 1000);
                    break;
                case 2:
                    // countdownVoice();
                    mCountdownDialog.setNumShow("2");
                    mCountdownHandler.sendMessageDelayed(mCountdownHandler.obtainMessage(1), 1000);
                    break;
                case 1:
                    // countdownVoice();
                    mCountdownDialog.setNumShow("1");
                    mCountdownHandler.sendMessageDelayed(mCountdownHandler.obtainMessage(0), 1000);
                    break;
                case 0:
                    mCountdownDialog.setVisibility(View.GONE);
                    mCountdownDialog.setNumShow("");
                    if (!TapcApp.getInstance().getSafeKeyStatus()) {
                        TapcApp.getInstance().mainActivity.startRun();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public class StatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(HardwareStatusController.KEY_WORKOUT_STATUS, 0);
            Log.e("StatusReceiver", "onReceive " + status);
            if ((status & MachieStatusController.STATUS_BIT_INVERTER_ERR_MASK_VALUE) == MachieStatusController
                    .STATUS_BIT_INVERTER_ERR_MASK_VALUE) {
                status = status & 0xffff;
                if (!TapcApp.getInstance().noShowNoCtlError) {
                    TapcApp.getInstance().noShowNoCtlError = true;
                } else if (oldErrorStaus == status) {
                    return;
                }
                if (status == 0) {
                    hasError = false;
                    TapcApp.getInstance().noShowNoCtlError = false;
                    TapcApp.getInstance().setStart(false);
                    mErrorHandler.sendEmptyMessage(NO_ERROR);
                    TapcApp.getInstance().scanCodeData.restoreStatus();
                } else {
                    hasError = true;
                    TapcApp.getInstance().setStart(false);
                    if (TapcApp.getInstance().getSportsEngin().isRunning()) {
                        TapcApp.getInstance().sendUIMessage(MessageType.MSG_UI_MAIN_STOP);
                    }
                    mCountdownDialog.setVisibility(View.GONE);
                    mCountdownHandler.removeCallbacksAndMessages(null);
                    mErrorHandler.sendEmptyMessage(status);

//                    TapcApp.getInstance().scanCodeData.saveStatus();
//                    TapcApp.getInstance().setDeviceRunStatus(DeviceRunStatus.ERROR);

                    if (status == 0x40) {
                        // mErrorHandler.sendEmptyMessage(LIFTER_ERROR);
                    }
                }
                oldErrorStaus = status;
            } else {
                if ((status & HardwareStatusController.SAFEKEY_MASK_VALUE) == HardwareStatusController
                        .SAFEKEY_MASK_VALUE) {
                    TapcApp.getInstance().setSafeKeyStatus(true);
                    if (TapcApp.getInstance().getSportsEngin().isRunning()) {
                        TapcApp.getInstance().sendUIMessage(MessageType.MSG_UI_MAIN_STOP);
                    }
                    mCountdownDialog.setVisibility(View.GONE);
                    mCountdownHandler.removeCallbacksAndMessages(null);
                    mErrorHandler.sendEmptyMessage(SAFE_KEY_SHOW);
                }
                if ((status & HardwareStatusController.SAFEKEY_MASK_VALUE) == 0) {
                    TapcApp.getInstance().setSafeKeyStatus(false);
                    TapcApp.getInstance().setStart(false);
                    mErrorHandler.sendEmptyMessage(SAFE_KEY_HIDE);
                }
            }
        }
    }

    private void clickSound() {
        TapcApp.getInstance().clickSound();
    }

    public class KeyboardReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int keycode = intent.getIntExtra(KeyboardController.KEY_CODE, 0);
            Log.e("keyboard", "keycode : " + keycode);
            TapcApp.getInstance().noNoActionCount = 0;
            if (mMachineStatusDialog.isShown() || mDeviceSelDialog.isShown() || mScanCodeDialog.isShown()) {
                SysUtils.playBeep(MachineStatusService.this, R.raw.notify);
                return;
            }
            SportsEngin sportsEngin = TapcApp.getInstance().getSportsEngin();
            switch (keycode) {
                case KeyCode.START:
                    if (!TapcApp.getInstance().isStart()) {
                        if (TapcApp.getInstance().mainActivity.isInGoalActivity()) {
                            Intent goalIntent = new Intent("Physical buttons");
                            MachineStatusService.this.sendBroadcast(goalIntent);
                            clickSound();
                            break;
                        }
                        TapcApp.getInstance().setStart(true);
                        TapcApp.getInstance().sendUIMessage(MessageType.MSG_UI_MAIN_START);
                        clickSound();
                    } else {
                        SysUtils.playBeep(MachineStatusService.this, R.raw.notify);
                    }
                    break;
                case KeyCode.STOP:
                    if (sportsEngin.isRunning()) {
                        mKeyHandler.postDelayed(mRunnable, 0);
                        TapcApp.getInstance().sendUIMessage(MessageType.MSG_UI_MAIN_STOP);
                        clickSound();
                    } else {
                        SysUtils.playBeep(MachineStatusService.this, R.raw.notify);
                    }
                    break;
                case KeyCode.START_PAUSE:
                    if (!TapcApp.getInstance().isStart()) {
                        if (TapcApp.getInstance().mainActivity.isInGoalActivity()) {
                            Intent goalIntent = new Intent("Physical buttons");
                            MachineStatusService.this.sendBroadcast(goalIntent);
                            clickSound();
                            break;
                        }
                        TapcApp.getInstance().setStart(true);
                        TapcApp.getInstance().sendUIMessage(MessageType.MSG_UI_MAIN_START);
                    } else {
                        if (sportsEngin.isPause()) {
                            TapcApp.getInstance().mainActivity.restartRun();
                        } else {
                            TapcApp.getInstance().mainActivity.pauseRun();
                        }
                    }
                    clickSound();
                    break;
                case KeyCode.START_STOP:
                    if (!TapcApp.getInstance().isStart()) {
                        TapcApp.getInstance().setStart(true);
                        TapcApp.getInstance().sendUIMessage(MessageType.MSG_UI_MAIN_START);
                    } else {
                        mKeyHandler.postDelayed(mRunnable, 0);
                        TapcApp.getInstance().sendUIMessage(MessageType.MSG_UI_MAIN_STOP);
                    }
                    clickSound();
                    break;
                case KeyCode.SPEED_INC_KEY:
                    if (sportsEngin.isRunning()) {
                        if (TapcApp.getInstance().mainActivity.getSpeedCtrl().getCtlValue() < TapcApp.MAX_SPEED) {
                            TapcApp.getInstance().mainActivity.getSpeedCtrl().addClick(null);
                            clickSound();
                        }
                    } else {
                        SysUtils.playBeep(MachineStatusService.this, R.raw.notify);
                    }
                    break;
                case KeyCode.SPEED_DEC_KEY:
                    if (sportsEngin.isRunning()) {
                        if (TapcApp.getInstance().mainActivity.getSpeedCtrl().getCtlValue() > TapcApp.MIN_SPEED) {
                            TapcApp.getInstance().mainActivity.getSpeedCtrl().subClick(null);
                            clickSound();
                        }
                    } else {
                        SysUtils.playBeep(MachineStatusService.this, R.raw.notify);
                    }
                    break;
                case KeyCode.INCLINE_INC:
                    if (sportsEngin.isRunning()) {
                        if (TapcApp.getInstance().mainActivity.getInclineCtrl().getCtlValue() < TapcApp.MAX_INCLINE) {
                            TapcApp.getInstance().mainActivity.getInclineCtrl().addClick(null);
                            clickSound();
                        }
                    } else {
                        SysUtils.playBeep(MachineStatusService.this, R.raw.notify);
                    }
                    break;
                case KeyCode.INCLINE_DEC:
                    if (sportsEngin.isRunning()) {
                        if (TapcApp.getInstance().mainActivity.getInclineCtrl().getCtlValue() > TapcApp.MIN_INCLINE) {
                            TapcApp.getInstance().mainActivity.getInclineCtrl().subClick(null);
                            clickSound();
                        }
                    } else {
                        SysUtils.playBeep(MachineStatusService.this, R.raw.notify);
                    }
                    break;
                case KeyCode.SET_SPEED:
                    if (sportsEngin.isRunning() && mPressnumber != NULL_NUM) {
                        if (mPressnumber >= TapcApp.MIN_SPEED && mPressnumber <= TapcApp.MAX_SPEED) {
                            TapcApp.getInstance().mainActivity.getSpeedCtrl().setCtlValue(mPressnumber);
                            mKeyBoardDialog.setMsgVisivility(false);
                        } else {
                            mKeyBoardDialog.setMsgShow(getString(R.string.keyvalue_error));
                            SysUtils.playBeep(MachineStatusService.this, R.raw.notify);
                        }
                        mPressnumber = NULL_NUM;
                        clickSound();
                    } else {
                        SysUtils.playBeep(MachineStatusService.this, R.raw.notify);
                    }
                    break;
                case KeyCode.SET_INCLINE:
                    if (sportsEngin.isRunning() && mPressnumber != NULL_NUM) {
                        if (mPressnumber >= TapcApp.MIN_INCLINE && mPressnumber <= TapcApp.MAX_INCLINE) {
                            TapcApp.getInstance().mainActivity.getInclineCtrl().setCtlValue(mPressnumber);
                            mKeyBoardDialog.setMsgVisivility(false);
                        } else {
                            mKeyBoardDialog.setMsgShow(getString(R.string.keyvalue_error));
                            SysUtils.playBeep(MachineStatusService.this, R.raw.notify);
                        }
                        mPressnumber = NULL_NUM;
                        clickSound();
                    } else {
                        SysUtils.playBeep(MachineStatusService.this, R.raw.notify);
                    }
                    break;
                case KeyCode.VOLUME_EN_DIS:
                    TapcApp.getInstance().setVolume();
                    clickSound();
                    break;
                case KeyCode.VOLUME_EN:
                    TapcApp.getInstance().openVolume();
                    clickSound();
                    break;
                case KeyCode.VOLUME_INC:
                    if (TapcApp.getInstance().nowVolume < TapcApp.getInstance().maxVolume) {
                        clickSound();
                    }
                    TapcApp.getInstance().addVolume();
                    break;
                case KeyCode.VOLUME_DEC:
                    clickSound();
                    TapcApp.getInstance().subVolume();
                    break;
                case KeyCode.HOME:
                    TapcApp.getInstance().menuBar.homeOnClick(null);
                    clickSound();
                    break;
                case KeyCode.BACK:
                    TapcApp.getInstance().menuBar.backOnClick(null);
                    clickSound();
                    break;
                case KeyCode.FAN:
                    TapcApp.getInstance().menuBar.fanOnClick(null);
                    clickSound();
                    break;
                case KeyCode.PROGRAM_SET:
                    if (!TapcApp.getInstance().isStart()) {
                        clickSound();
                        TapcApp.getInstance().mainActivity.onExercise(null);
                    } else {
                        SysUtils.playBeep(MachineStatusService.this, R.raw.notify);
                    }
                    break;
                case KeyCode.RELAX:
                    if (TapcApp.getInstance().getSportsEngin().isRunning()) {
                        clickSound();
                        TapcApp.getInstance().getSportsEngin().setCooldown();
                    } else {
                        SysUtils.playBeep(MachineStatusService.this, R.raw.notify);
                    }
                    break;
                case KeyCode.MEDIA_PREVIOUS:
                    SysUtils.sendKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                    clickSound();
                    break;
                case KeyCode.MEDIA_NEXT:
                    SysUtils.sendKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT);
                    clickSound();
                    break;
                default:
                    if (keycode >= KeyCode.NUM_MIN && keycode <= KeyCode.NUM_MAX) {
                        mPressnumber = getPressNum(keycode - KeyCode.NUM_MIN);
                    }
                    if (keycode >= KeyCode.SPEED_MIN && keycode <= KeyCode.SPEED_MAX) {
                        setSpeed(keycode - KeyCode.SPEED_MIN);
                    }
                    if (keycode >= KeyCode.INCLINE_MIN && keycode <= KeyCode.INCLINE_MAX) {
                        setIncline(keycode - KeyCode.INCLINE_MIN);
                    }
                    break;
            }
        }
    }

    private void setSpeed(double speed) {
        if (TapcApp.getInstance().getSportsEngin().isRunning()) {
            TapcApp.getInstance().mainActivity.getSpeedCtrl().setCtlValue(speed);
            clickSound();
        } else {
            SysUtils.playBeep(MachineStatusService.this, R.raw.notify);
        }
    }

    private void setIncline(double incline) {
        if (TapcApp.getInstance().getSportsEngin().isRunning()) {
            TapcApp.getInstance().mainActivity.getInclineCtrl().setCtlValue(incline);
            clickSound();
        } else {
            SysUtils.playBeep(MachineStatusService.this, R.raw.notify);
        }
    }

    private int getPressNum(int num) {
        if (TapcApp.getInstance().getSportsEngin().isRunning()) {
            if (mPressnumber == NULL_NUM) {
                mPressnumber = num;
            } else {
                mPressnumber = (mPressnumber * 10 + num) % 100;
            }
            mKeyBoardDialog.setMsgShow(getString(R.string.keyboard_input_str) + mPressnumber);
            if (mKeyBoardDialog.isShown() == false) {
                mKeyBoardDialog.setMsgVisivility(true);
            }
            mKeyHandler.removeCallbacks(mRunnable);
            mKeyHandler.postDelayed(mRunnable, DELAY_TIME);
            clickSound();
        } else {
            SysUtils.playBeep(MachineStatusService.this, R.raw.notify);
        }
        return mPressnumber;
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mPressnumber = NULL_NUM;
            mKeyBoardDialog.setMsgVisivility(false);
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mErrorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NO_ERROR:
                    if (TapcApp.getInstance().getSafeKeyStatus()) {
                        mMachineStatusDialog.setVisibility(View.VISIBLE);
                        mMachineStatusDialog.setSafeKeyVisibility(true);
                    } else {
                        mMachineStatusDialog.setVisibility(View.GONE);
                        mMachineStatusDialog.setSafeKeyVisibility(false);
                    }
                    mMachineStatusDialog.setErrorTextHide();
                    mMachineStatusDialog.setErrorLifterRlHide();
                    break;
                case SAFE_KEY_SHOW:
                    if (!hasError || mMachineStatusDialog.getManuallyStatus()) {
                        mMachineStatusDialog.setVisibility(View.VISIBLE);
                        mMachineStatusDialog.setSafeKeyVisibility(true);
                        mMachineStatusDialog.setErrorTextHide();
                    }
                    break;
                case SAFE_KEY_HIDE:
                    if (!hasError || mMachineStatusDialog.getManuallyStatus()) {
                        mMachineStatusDialog.setVisibility(View.GONE);
                        mMachineStatusDialog.setSafeKeyVisibility(false);
                        mMachineStatusDialog.setErrorTextHide();
                        mMachineStatusDialog.setErrorLifterRlHide();
                    }
                    break;
                case LIFTER_ERROR:
                    mMachineStatusDialog.setErrorLifterRlShow();
                    break;
                default:
                    String errorStr = Integer.toHexString(msg.what);
                    if (errorStr == null) {
                        errorStr = "null";
                    }
                    mMachineStatusDialog.setVisibility(View.VISIBLE);
                    mMachineStatusDialog.setErrorTextShow("ERROR CODE : " + errorStr + " H");
                    break;
            }
        }
    };

    public class KeyCode {
        public static final int START = 0x01;
        public static final int STOP = 0x02;
        public static final int RELAX = 0x03;
        public static final int PAUSE = 0x04;
        public static final int START_PAUSE = 0x16;
        public static final int START_STOP = 0x17;

        public static final int SET_SPEED = 0x05;
        public static final int SPEED_INC_KEY = 0x06;
        public static final int SPEED_DEC_KEY = 0x07;

        public static final int SET_INCLINE = 0x08;
        public static final int INCLINE_INC = 0x09;
        public static final int INCLINE_DEC = 0x0a;

        public static final int VOLUME_EN_DIS = 0x18;
        public static final int VOLUME_EN = 0x0b;
        public static final int VOLUME_DIS = 0x0c;
        public static final int VOLUME_INC = 0x0d;
        public static final int VOLUME_DEC = 0x0e;

        public static final int PROGRAM_SET = 0x10;
        public static final int PROGRAM_INC = 0x11;
        public static final int PROGRAM_DEC = 0x12;

        public static final int NUM_MIN = 0x30;
        public static final int NUM_MAX = 0x39;

        public static final int SPEED_MIN = 0x40;
        public static final int SPEED_MAX = 0x59;

        public static final int INCLINE_MIN = 0x80;
        public static final int INCLINE_MAX = 0x99;

        public static final int MENU = 0x0f;
        public static final int HOME = 0X13;
        public static final int BACK = 0X14;
        public static final int FAN = 0X15;

        public static final int MEDIA_PREVIOUS = 0x19;
        public static final int MEDIA_NEXT = 0x20;
    }
}
