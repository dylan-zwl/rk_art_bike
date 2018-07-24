package com.tapc.platform.witget.scancode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnTouch;
import com.tapc.android.uart.Commands;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.LoginSettingActivity;
import com.tapc.platform.model.common.ClickModel;
import com.tapc.platform.model.common.ConfigModel;
import com.tapc.platform.model.common.UserManageModel;
import com.tapc.platform.model.healthcat.BaseCtlModel;
import com.tapc.platform.model.healthcat.DeviceRunStatus;
import com.tapc.platform.model.healthcat.bike.BikeData;
import com.tapc.platform.model.healthcat.power.PowerData;
import com.tapc.platform.rfid.RfidModel;
import com.tapc.platform.usb.UsbCtl;
import com.tapc.platform.utils.SysUtils;
import com.tapc.platform.utils.WindowManagerUtils;
import com.tapc.platform.workout.MessageType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

public class ScanCodeDialog extends BaseSystemView implements ScanCodeContract.View {
    @ViewInject(R.id.qr_codes)
    ImageView mQrCode;
    @ViewInject(R.id.dialog_rl)
    RelativeLayout mDialogRl;
    @ViewInject(R.id.scancode_login_rl)
    RelativeLayout mScancodeLoginRl;
    @ViewInject(R.id.set_device_id_rl)
    RelativeLayout mSetDeviceIdRl;
    @ViewInject(R.id.show_text)
    TextView mShowMsg;
    @ViewInject(R.id.net_msg_rl)
    RelativeLayout mNetMsgRl;
    @ViewInject(R.id.net_msg)
    TextView mNetMsg;
    @ViewInject(R.id.rfid_msg_rl)
    RelativeLayout mRfidMsgRl;
    @ViewInject(R.id.rfid_reconnect)
    Button mRfidReconnectBtn;
    @ViewInject(R.id.rfid_msg)
    TextView mRfidMsg;
    @ViewInject(R.id.dialog_qr_codes_fl)
    RelativeLayout mQrCodeRl;

    @ViewInject(R.id.scan_code_time)
    TextView mScanCodeTime;
    @ViewInject(R.id.show_text2)
    TextView mShowPhoneNumber;
    @ViewInject(R.id.wifi_status)
    ImageView mWifiStatus;

    private Handler mHandler;
    private ScanCodeContract.Presenter mPresenter;
    private int mConnectFailCout = 0;
    private String mOldQrCodeStr;
    private ClickModel mClickModel;
    private DeviceType mDeviceType;
    private Object mData;
    private String mUrl;

    private boolean isDevLogin = false;

    public ScanCodeDialog(Context context, DeviceType deviceType) {
        super(context);
        mDeviceType = deviceType;
        initDeviceType(deviceType);
        init();
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.scan_code_healthcat_dialog;
    }

    @Override
    protected void initView() {
        super.initView();
        mHandler = new Handler();
        EventBus.getDefault().register(this);

        mDialogRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void init() {
        mData = TapcApp.getInstance().scanCodeData;

        if (Config.isConnected) {
            TapcApp.getInstance().initDeviceId(mContext);
        }

        mPresenter = new ScanCodePresenter(mContext, this, mDeviceType);
        if (Config.HAS_SCAN_QR) {
            hideConnectNet();
            initConfig();
            mPresenter.start();
        }
        if (Config.HAS_RFID) {
            connectRfid();
        }
    }

    private void initConfig() {
        String deviceId = ConfigModel.getHealthCatDeviceId(mContext, "");
        if (TextUtils.isEmpty(deviceId)) {
            String defDeviceId = "DTAT0001";
            switch (mDeviceType) {
                case BIKE:
                    defDeviceId = "DTAT0001";
                    break;
                case POWER:
                    defDeviceId = "STAT0001";
                    break;
            }
//            ConfigModel.setHealthCatDeviceId(mContext, defDeviceId);
            deviceId = defDeviceId;
        }

        mUrl = ConfigModel.readString(mContext, ConfigModel.SETTING_CONFIG, ConfigModel.SERVER_URL, ConfigModel
                .DEF_SERVER_URL);
        String phoneNumber = ConfigModel.readString(mContext, ConfigModel.SETTING_CONFIG, ConfigModel
                .PHONE_NUMBER, ConfigModel.DEF_PHONE_NUMBER);
        BaseCtlModel.SERVER_URL = ConfigModel.readString(mContext, ConfigModel.SETTING_CONFIG, ConfigModel
                .SERVER_DOMAIN_NAME, BaseCtlModel.SERVER_URL);
        BaseCtlModel.SERVER_PORT = ConfigModel.readInt(mContext, ConfigModel.SETTING_CONFIG, ConfigModel
                .SERVER_PORT, BaseCtlModel.SERVER_PORT);

        mDeviceIdEt.setText(deviceId);
        mShowPhoneNumber.setText("广州大象运动科技有限公司\n联系客服：" + phoneNumber);

        setDeviceId(deviceId);
    }

    private void initDeviceType(DeviceType deviceType) {
        switch (deviceType) {
            case TREADMILL:
                mDialogRl.setBackgroundResource(R.drawable.scan_qrcode_healthcat_bg1);
                break;
            case BIKE:
                mDialogRl.setBackgroundResource(R.drawable.scan_qrcode_healthcat_bg1);
                break;
            case POWER:
                mDialogRl.setBackgroundResource(R.drawable.scan_qrcode_healthcat_bg2);
                break;
        }
    }

    @Override
    public WindowManager.LayoutParams getLayoutParams() {
        return WindowManagerUtils.getLayoutParams(0, 0, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, Gravity.TOP);
    }

    /**
     * 扫码界面设置显示或隐藏
     */
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100, sticky = true)
    public void onDialogEvent(ScanCodeEvent event) {
        setVisibility(event.isVisibility());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND, priority = 100, sticky = true)
    public void onBikeEvent(BikeData bikeData) {
        mPresenter.sendHeartbeat();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND, priority = 100, sticky = true)
    public void onPowerEvent(PowerData powerData) {
        mPresenter.sendHeartbeat();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100, sticky = true)
    public void onUpdateTime(ShowTimeEvent showTimeEvent) {
        mScanCodeTime.setText(showTimeEvent.getTime());
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100, sticky = true)
    public void onUpdateTime(WifiStatusEvent wifiStatusEvent) {
        if (wifiStatusEvent.isConnect()) {
            if (wifiStatusEvent.getType() == ConnectivityManager.TYPE_ETHERNET) {
                mWifiStatus.setImageResource(R.drawable.ethernet_connect);
            } else {
                mWifiStatus.setImageResource(R.drawable.wifi_connect);
            }
        } else {
            if (wifiStatusEvent.getType() == ConnectivityManager.TYPE_ETHERNET) {
                mWifiStatus.setImageResource(R.drawable.ethernet_unconnect);
            } else {
                mWifiStatus.setImageResource(R.drawable.wifi_unconnect);
            }
        }
        mWifiStatus.setVisibility(View.VISIBLE);
    }

    private void initShowDialog() {
        setVisibility(View.VISIBLE);
    }

    @SuppressLint("NewApi")
    public void setVisibility(boolean visibility) {
//        NoActionModel.getInstance().cleanNoActionCount();
        if (visibility) {
            TapcApp.getInstance().controller.LoginQuitMachine(0);
            if (mDialogRl.isShown()) {
                return;
            }
            mDialogRl.setClickable(false);

            initShowDialog();
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.up_to_down);
            mDialogRl.startAnimation(animation);
            animation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    mDialogRl.setClickable(true);
                    if (Config.HAS_SCAN_QR == false) {
                        mQrCodeRl.setVisibility(View.INVISIBLE);
                    }
                    TapcApp.getInstance().scanCodeData.clearData();
                    TapcApp.getInstance().setDeviceRunStatus(DeviceRunStatus.LOCK_SCREEN);
                }
            });
            UserManageModel.getInstance().logout();
            exitApp();
        } else {
            mDialogRl.setClickable(false);
            TapcApp.getInstance().controller.LoginQuitMachine(1);
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.down_to_up);
            mDialogRl.startAnimation(animation);
            animation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    setVisibility(View.GONE);
                    mDialogRl.setClickable(true);
                    TapcApp.getInstance().setDeviceRunStatus(DeviceRunStatus.UNSTART);
                }
            });
        }

        if (UserManageModel.getInstance().isLogined()) {
            TapcApp.getInstance().mainActivity.setWifiBtnVisibility(false);
        } else {
            TapcApp.getInstance().mainActivity.setWifiBtnVisibility(true);
        }
    }

    /**
     * 无网络提示
     */
    private void showConnectNet(final String text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mNetMsg.setText(text);
                mNetMsgRl.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideConnectNet() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mConnectFailCout = 0;
                mNetMsg.setText("");
                mNetMsgRl.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 二维码现显示
     */
    private void setQrcodeVisibility(final int visibility) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mQrCode.setVisibility(visibility);
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    public void showQrcode(final String qrcodeStr) {
        if (qrcodeStr == null || qrcodeStr.isEmpty()) {
            setQrcodeVisibility(View.INVISIBLE);
            return;
        }
        if (mOldQrCodeStr != null && mOldQrCodeStr.equals(qrcodeStr)) {
            setQrcodeVisibility(View.VISIBLE);
            return;
        }
        Thread thread = new Thread() {
            @Override
            public void run() {
                synchronized (this) {
                    String showQrcodeStr = qrcodeStr;
                    if (!TextUtils.isEmpty(mUrl)) {
//                        showQrcodeStr = mUrl + "?id=" + qrcodeStr + "&mac=" + Config.DEVICE_ID;
                        showQrcodeStr = mUrl + "?id=" + qrcodeStr;
                    }
                    final Bitmap bitmap = SysUtils.createImage(showQrcodeStr, 100, 100, 5);
                    if (bitmap != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mOldQrCodeStr = qrcodeStr;
                                mQrCode.setBackground(new BitmapDrawable(bitmap));
                                mQrCode.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            }
        };
        thread.start();
    }

    @Override
    public void loginStatus(boolean isSuccess) {
        isDevLogin = isSuccess;
    }

    private Runnable mLoginRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isDevLogin) {
                mConnectFailCout++;
                if (mConnectFailCout > 10) {
                    mConnectFailCout = 0;
                    showConnectNet(mContext.getString(R.string.login_fail));
                }
                login();
            } else {
                mConnectFailCout = 0;
                hideConnectNet();
            }
        }
    };

    private void login() {
        mPresenter.login(Config.DEVICE_ID);
        mHandler.postDelayed(mLoginRunnable, 2000);
    }

    /**
     * 启动和停止设备。
     */
    @Override
    public boolean setRunStatus(boolean isStart) {
//        if (!mDialogRl.isShown()) {
        if (isStart) {
            if (!TapcApp.getInstance().isStart()) {
                TapcApp.getInstance().sendUIMessage(MessageType.MSG_UI_MAIN_START);
            }
        } else {
            if (TapcApp.getInstance().getSportsEngin().isRunning()) {
                TapcApp.getInstance().sendUIMessage(MessageType.MSG_UI_MAIN_STOP);
            } else if (TapcApp.getInstance().isStart()) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TapcApp.getInstance().sendUIMessage(MessageType.MSG_UI_MAIN_STOP);
                    }
                }, 3000);
            }
        }
//        }
        return true;
    }

    /**
     * 加锁，解锁设备
     */
    @Override
    public boolean serverSetLock(final boolean lock) {
        if (!lock) {
            UserManageModel.getInstance().login();
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(lock);
            }
        });
        return true;
    }

    @Override
    public Object getDeviceInfo() {
        return mData;
    }

    @Override
    public void setParameter(byte parameter) {
        int data = parameter & 0xff;
        ((BikeData) mData).setResistance(data);
    }

    private void setDeviceId(String saveId) {
        mPresenter.setDeviceId(saveId);
    }

    /**
     * 连接服务器结果
     */
    @Override
    public void connectServerResult(boolean isSuccess) {
        if (!isSuccess) {
            mConnectFailCout++;
            if (mConnectFailCout > 5) {
                mConnectFailCout = 0;
                showConnectNet(mContext.getString(R.string.connect_server_failure));
            }
            isDevLogin = false;
        } else {
            mConnectFailCout = 0;
            hideConnectNet();
            if (!isDevLogin) {
                mHandler.removeCallbacks(mLoginRunnable);
                login();
            }
        }
    }

    /**
     * 进入网络设置按键
     */
    @OnClick(R.id.connect_service_yes)
    protected void connectServer(View v) {
        hideConnectNet();
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(Settings.ACTION_WIFI_SETTINGS);
        mContext.startActivity(intent);
        setVisibility(false);
    }

    @OnClick(R.id.connect_service_no)
    protected void unConnectServer(View v) {
        hideConnectNet();
    }

    /**
     * 手动进入主页面
     */
    @OnTouch(R.id.system_settings)
    boolean clickLogo(View v, MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_UP) {
//            if (mClickModel == null) {
//                mClickModel = new ClickModel.Builder().onceClickTimeout(1000).maxClickNumbers(5).create();
//            }
//            mClickModel.setListener(mClickLogoListener);
//            mClickModel.click();
//        }
        return true;
    }

    private ClickModel.Listener mClickLogoListener = new ClickModel.Listener() {
        @Override
        public void onClickCompleted() {
            setVisibility(false);
        }
    };

    /**
     * 设置device id
     */
    @ViewInject(R.id.device_id_et)
    TextView mDeviceIdEt;

    @OnClick(R.id.device_id_save)
    protected void save(View v) {
        String text = mDeviceIdEt.getEditableText().toString();
        if (TextUtils.isEmpty(text)) {
            return;
        }
        text = text.toUpperCase();
        boolean result = ConfigModel.setHealthCatDeviceId(mContext, text);
        if (result) {
            mPresenter.stop();
            setDeviceId(text);
            mPresenter.start();
        }
        mScancodeLoginRl.setVisibility(View.VISIBLE);
        mSetDeviceIdRl.setVisibility(View.GONE);
        setSoftInputVisibility(false);
    }

    @OnTouch(R.id.set_device_id)
    boolean setDeviceIdOnClick(View v, MotionEvent event) {
//        mPresenter.sendIcStart("87686670");
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mClickModel == null) {
                mClickModel = new ClickModel.Builder().onceClickTimeout(1000).maxClickNumbers(5).create();
            }
            mClickModel.setListener(mSetDeviceIdListener);
            mClickModel.click();
        }
        return true;
    }


    private ClickModel.Listener mSetDeviceIdListener = new ClickModel.Listener() {
        @Override
        public void onClickCompleted() {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setClass(mContext, LoginSettingActivity.class);
            mContext.startActivity(intent);
            setVisibility(false);
        }
    };

    /**
     * 退出第三方应用
     */
    private void exitApp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TapcApp.getInstance().clearAppExit(TapcApp.getInstance().listAppInfo);
            }
        }).start();
    }

    /**
     * 功能描述 : 是否显示输入法键盘
     *
     * @param : visibility  = false 隐藏显示
     */
    private void setSoftInputVisibility(boolean visibility) {
        setSoftInputVisibility(mContext, mDeviceIdEt, visibility);
    }

    /**
     * 功能描述 : 是否显示输入法键盘
     *
     * @param : visibility  = false 隐藏显示
     */
    public static void setSoftInputVisibility(Context context, View view, boolean visibility) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (visibility) {
            imm.showSoftInput(view, 0);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
/*********************************************************************************************************************/

    /**
     * rfid模块功能
     *
     * @param :
     */
    private RfidModel mRfidModel;
    private Timer mTimer;
    private int mRfidConnectCount = 0;

    /**
     * 手环登录状态
     */
    @Override
    public void icLoginStatus(byte status) {
        String errorText = "";
        switch (status) {
            case (byte) 0xE0:
                serverSetLock(false);
                break;
            case (byte) 0xE1:
                errorText = "手环编号不存在";
                break;
            case (byte) 0xE2:
                errorText = "服务器开小差了，\n请稍后再试";
                break;
            case (byte) 0xE3:
                errorText = "手环未绑定猫号，\n请到健康猫APP-我的运动馆\n-我的手环中完成绑定";
                break;
            case (byte) 0xE4:
                errorText = "手环编号格式不正确";
                break;
            case (byte) 0xE5:
                errorText = "设备编号格式不正确";
                break;
            case (byte) 0xE6:
                errorText = "设备号不存在";
                break;
            case (byte) 0xE7:
                errorText = "上传的数据格式有误";
                break;
            case (byte) 0xE8:
                errorText = "设备已离线";
                break;
            case (byte) 0xE9:
                errorText = "设备正在使用中";
                break;
            case (byte) 0xEA:
                errorText = "设备正在维护中";
                break;
            case (byte) 0xEB:
                errorText = "设备已经被预约";
                break;
        }
        if (!TextUtils.isEmpty(errorText)) {
            setRfidMsg(errorText, 4000);
        }
    }

    private void connectRfid() {
        mRfidModel = null;
        mRfidModel = new RfidModel();
        boolean isConnected = mRfidModel.connectUsb(TapcApp.getInstance().mainActivity);
        if (isConnected) {
            //startDetect();
            mRfidModel.connectRfid(new RfidModel.DetectListener() {

                @Override
                public void connectStatus(boolean isConnected) {
                    if (!isConnected) {
                        setRfidMsg(mContext.getString(R.string.rfid_device_connect_failed));
                    }
                }

                @Override
                public void Uid(byte[] uid) {
                    if (mDialogRl.isShown()) {
                        TapcApp.getInstance().controller.sendCommands(Commands.SET_BUZZER_CNTRL, null);
                        SysUtils.playBeep(mContext, R.raw.rfid);
                        String uidStr = UsbCtl.bytesToHexString(uid);
                        mPresenter.sendIcStart(uidStr);
                    }
                }
            });
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRfidConnectCount++;
                    if (mRfidConnectCount >= 5) {
                        mRfidConnectCount = 0;
                        setRfidMsg(mContext.getString(R.string.rfid_device_malfunction));
                    } else {
                        connectRfid();
                    }
                }
            }, 1000);
        }
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void startDetect() {
        stopTimer();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mRfidModel.activationCard();
            }
        }, 1000, 1000);
    }

    @OnClick(R.id.rfid_reconnect)
    protected void reconnect(View v) {
        mRfidMsgRl.setVisibility(GONE);
        connectRfid();
    }

    private void setRfidMsg(final String text) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mRfidMsgRl.setVisibility(VISIBLE);
                mRfidMsg.setText(text);
            }
        });
    }

    private void setRfidMsg(final String text, int hideDelayTime) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mRfidMsgRl.setVisibility(VISIBLE);
                mRfidReconnectBtn.setVisibility(GONE);
                mRfidMsg.setText(text);
            }
        });
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRfidMsgRl.setVisibility(GONE);
                mRfidMsg.setText("");
            }
        }, hideDelayTime);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mPresenter != null) {
            mPresenter.stop();
        }
    }
}
