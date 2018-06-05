package com.tapc.platform.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RecoverySystem;
import android.os.SystemClock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnRadioGroupCheckedChange;
import com.tapc.android.controller.MachineController;
import com.tapc.android.uart.Commands;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.dto.RemoteDTO;
import com.tapc.platform.dto.net.Callback;
import com.tapc.platform.dto.response.RemotionResponse;
import com.tapc.platform.entity.DeviceData;
import com.tapc.platform.utils.AppUtils;
import com.tapc.platform.utils.AutoUpdate;
import com.tapc.platform.utils.PreferenceHelper;
import com.tapc.platform.utils.SysUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class AdminSystemFragment extends Fragment {
    private Context mContext;
    private ContentResolver mResolver;
    @ViewInject(R.id.backlight)
    private SeekBar backlight;
    @ViewInject(R.id.calibration_progress)
    private ProgressBar mCalibrationProgress;
    @ViewInject(R.id.calibration_msg)
    private TextView mCalibration_msg;
    @ViewInject(R.id.version_text)
    private TextView mVersionText;
    @ViewInject(R.id.app_install_ban)
    private RadioButton app_install_ban;
    @ViewInject(R.id.app_install_allow)
    private RadioButton app_install_allow;
    @ViewInject(R.id.erp_ban)
    private RadioButton erp_ban;
    @ViewInject(R.id.erp_allow)
    private RadioButton erp_allow;
    @ViewInject(R.id.detectperson_ban)
    private RadioButton detectperson_ban;
    @ViewInject(R.id.detectperson_allow)
    private RadioButton detectperson_allow;
    @ViewInject(R.id.test_ban)
    private RadioButton test_ban;
    @ViewInject(R.id.test_allow)
    private RadioButton test_allow;
    @ViewInject(R.id.scan_qr_ban)
    private RadioButton scan_qr_ban;
    @ViewInject(R.id.scan_qr_allow)
    private RadioButton scan_qr_allow;
    @ViewInject(R.id.rfid_ban)
    private RadioButton rfid_ban;
    @ViewInject(R.id.rfid_allow)
    private RadioButton rfid_allow;
    @ViewInject(R.id.start_calibration)
    private Button mInclineCalBtn;

    @ViewInject(R.id.service_ip)
    private EditText mIpEt;
    @ViewInject(R.id.service_port)
    private EditText mPortEt;

    @ViewInject(R.id.device_id)
    private EditText mDeviceIdEt;

    @ViewInject(R.id.device_run_infor)
    private TextView mDeviceRunInfor;

    @ViewInject(R.id.setting_bike_b8900et)
    private Button b8900et;
    @ViewInject(R.id.setting_bike_b8900rt)
    private Button b8900rt;
    @ViewInject(R.id.setting_bike_b8900ut)
    private Button b8900ut;

    private ProgressDialog mProgressDialog;
    private AlertDialog mAlert;
    private Handler MachineMaintenanceHandler;
    MachineController mController = MachineController.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_system_setting, container, false);
        ViewUtils.inject(this, view);
        initUI();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MachineMaintenanceHandler.removeCallbacksAndMessages(null);
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
    }

    /**
     *
     */
    @SuppressLint({"HandlerLeak", "NewApi"})
    private void initUI() {
        try {
            mResolver = mContext.getContentResolver();
            initShowDeviceInfor();
            int nowBrightnessValue;
            nowBrightnessValue = android.provider.Settings.System.getInt(mResolver, Settings.System.SCREEN_BRIGHTNESS);
            backlight.setMax(255);
            backlight.setProgress(nowBrightnessValue);
            backlight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int value, boolean arg2) {
                    try {
                        android.provider.Settings.System.putInt(mResolver, Settings.System.SCREEN_BRIGHTNESS, value);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            });
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        MachineMaintenanceHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        if (!mCalibrationProgress.isShown()) {
                            mCalibrationProgress.setVisibility(View.VISIBLE);
                            mCalibration_msg.setText(getString(R.string.calibrationing_msg));
                        }
                        if (mController.getInclinecalStatus() == 0xff) {
                            MachineMaintenanceHandler.sendEmptyMessage(1);
                        } else {
                            MachineMaintenanceHandler.sendMessageDelayed(MachineMaintenanceHandler.obtainMessage(0),
                                    1000);
                        }
                        break;
                    case 1:
                        mCalibrationProgress.setVisibility(View.INVISIBLE);
                        mCalibration_msg.setText(getString(R.string.calibrationed_msg));
                        mInclineCalBtn.setClickable(true);
                        break;
                    case 3:
                        String appVersion = "APP : V" + SysUtils.getLocalVersionCode(mContext);
                        String mcuVersion = "  MCU : V" + mController.getCtlVersionValue();
                        String deviceId = "";
                        if (Config.DEVICE_ID != null && !Config.DEVICE_ID.isEmpty()) {
                            deviceId = "  ID : " + Config.DEVICE_ID;
                        }
                        mVersionText.setText(appVersion + mcuVersion + deviceId);

                        break;
                    default:
                        break;
                }
            }
        };
        mCalibrationProgress.setVisibility(View.INVISIBLE);

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.hide();

        mController.sendCtlVersionCmd();
        MachineMaintenanceHandler.sendMessageDelayed(MachineMaintenanceHandler.obtainMessage(3), 1000);

        if (TapcApp.getInstance().hasDetectPerson) {
            detectperson_allow.setChecked(true);
        } else {
            detectperson_ban.setChecked(true);
        }

        if (TapcApp.getInstance().hasErp) {
            erp_allow.setChecked(true);
        } else {
            erp_ban.setChecked(true);
        }

        if (TapcApp.getInstance().isTestOpen) {
            test_allow.setChecked(true);
        } else {
            test_ban.setChecked(true);
        }

        if (Config.HAS_SCAN_QR) {
            scan_qr_allow.setChecked(true);
        } else {
            scan_qr_ban.setChecked(true);
        }

        if (Config.HAS_RFID) {
            rfid_allow.setChecked(true);
        } else {
            rfid_ban.setChecked(true);
        }

        try {
            boolean appPermisson = (Settings.Global.getInt(mContext.getContentResolver(),
                    Settings.Global.INSTALL_NON_MARKET_APPS, 0) > 0 ? true : false);
            if (appPermisson) {
                app_install_allow.setChecked(true);
            } else {
                app_install_ban.setChecked(true);
            }
        } catch (Exception e) {
        }
        setDeviceTypeShow();
//        setDeviceId();
    }

    private void setDeviceId() {
        String deviceId = "AT0001";
        try {
            FileInputStream fis = new FileInputStream(mContext.getCacheDir().getPath() + "/device_id");
            InputStreamReader isr = new InputStreamReader(fis, "utf-8");
            char input[] = new char[fis.available()];
            isr.read(input);
            isr.close();
            fis.close();
            deviceId = new String(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDeviceIdEt.setText(deviceId);
    }

    @OnClick(R.id.device_id_save)
    protected void saveDeviceId(View v) {
        try {
            String path = mContext.getCacheDir().getPath() + "/device_id";
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(path, false);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
            osw.write(mDeviceIdEt.getText().toString().toUpperCase());
            osw.flush();
            fos.flush();
            osw.close();
            fos.close();
            Toast.makeText(mContext, "保存成功", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "保存失败", Toast.LENGTH_LONG).show();
        }
    }


    private void initShowDeviceInfor() {
        DeviceData data = TapcApp.getInstance().getDeviceData();
        if (data != null) {
            float time = Integer.valueOf(data.getOldtime()).intValue();
            String showTime = String.format("%.2f", time / 3600);
            float distance = Integer.valueOf(data.getOlddistance()).intValue();
            distance = distance / 1000;
            String showDistance = String.format("%.2f", distance);
            mDeviceRunInfor.setText(getString(R.string.device_run_time) + " " + showTime + " h    "
                    + getString(R.string.device_run_distance) + " " + showDistance + " km");
        }
    }

    @OnClick(R.id.update_os)
    protected void updateOS(View v) {
        updatePrompt(1);
    }

    @OnClick(R.id.update_mcu)
    protected void updateMCU(View v) {
        updatePrompt(2);
    }

    @OnClick(R.id.update_app_u)
    protected void updateAppU(View v) {
        updatePrompt(3);
    }

    @OnClick(R.id.update_app_online)
    protected void updateAppOnline(View v) {
        getRemoteVersion();
    }

    public void updatePrompt(final int updateFlag) {
        String promptMsg = "Are you sure update ";
        switch (updateFlag) {
            case 1:
                promptMsg = promptMsg + "OS ?";
                break;
            case 2:
                promptMsg = promptMsg + "MCU ?";
                break;
            case 3:
                promptMsg = promptMsg + "APK from udisk ?";
                break;
            default:
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(promptMsg).setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switch (updateFlag) {
                            case 1:
                                osUpdate();
                                break;
                            case 2:
                                mcuUpdate();
                                break;
                            case 3:
                                apkuUpdate();
                                break;
                            default:
                                break;
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * OS升级
     *
     * @param v
     */
    private void osUpdate() {
        try {
            File packageFile = new File("/mnt/external_sd/update.zip");
            RecoverySystem.installPackage(mContext, packageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * MCU升级
     *
     * @param v
     */
    private void mcuUpdate() {
        File rombin = new File(Config.USB_FILE_PATH + "ROM.bin");
        // File rombin = new File("/mnt/external_sd/ROM.bin");
        if (rombin.exists()) {
            mProgressDialog.setTitle("Update MCU");
            mProgressDialog.setProgress(0);
            mProgressDialog.show();
            Handler IOUpdateMsg = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.getData().containsKey("ERROR")) {
                        String errorStr = (String) msg.getData().get("ERROR");
                        Log.d("update MCU error", "error info: " + errorStr);
                        uiMessage("Upgrade Fails");
                        mProgressDialog.hide();
                    } else if (msg.getData().containsKey("INFO")) {
                        String infoStr = (String) msg.getData().get("INFO");
                        Log.d("update MCU info", "info: " + infoStr);
                        if (infoStr.contains("Update Completed Successfully")) {
                            PreferenceHelper.write(mContext, Config.SETTING_CONFIG, "device", 0);
                            PreferenceHelper.write(mContext, Config.SETTING_CONFIG, "is_upload_device_infor", false);
                            mProgressDialog.hide();
                            uiMessage("Upgrade Success");
                        }
                    } else if (msg.getData().containsKey("PROGRESS")) {
                        String progressStr = (String) msg.getData().get("PROGRESS");
                        if (progressStr != null) {
                            int nowProgress = Integer.valueOf(progressStr).intValue();
                            if (nowProgress >= 0 && nowProgress <= 100) {
                                mProgressDialog.setProgress(nowProgress);
                            }
                        }
                    }
                }
            };
            MachineController mController = MachineController.getInstance();
            mController.updateMCU(rombin.getAbsolutePath(), IOUpdateMsg);
        } else {
            uiMessage("Can not found update file");
        }
    }

    /**
     * apku升级
     *
     * @param v
     */
    private void apkuUpdate() {
        File file = new File(Config.USB_FILE_PATH, Config.UPDATE_FILE_NAME);
        if (file.exists()) {
            AppUtils.clearAppUserData(mContext, mContext.getPackageName());
            SysUtils.installApk(mContext, file);
        } else {
            uiMessage(getString(R.string.update_file_not_exit));
        }
    }

    private void getRemoteVersion() {
        TapcApp.getInstance().getHttpClient().getRemotion(Config.VERSION_NAME, new Callback() {

            @Override
            public void onSuccess(Object o) {
                RemoteDTO remoteData = ((RemotionResponse) o).getResponse();
                String remoteVersion = remoteData.getVersionCode();
                Log.i("update", "结果remoteVersion=" + remoteVersion);
                new AutoUpdate(mContext, remoteData.getVersionName(), remoteData.getVersionCode(), remoteData
                        .getDescription(), remoteData.getDownload(), true);
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onFailure(Object o) {
            }

            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                RemoteDTO remoteData = gson.fromJson(result, RemotionResponse.class).getResponse();
                String remoteVersion = remoteData.getVersionCode();
                Log.i("update", "结果remoteVersion=" + remoteVersion);
                new AutoUpdate(mContext, remoteData.getVersionName(), remoteData.getVersionCode(), remoteData
                        .getDescription(), remoteData.getDownload(), true);
            }
        });

    }

    @OnClick(R.id.start_calibration)
    protected void startCalibration(View v) {
        mInclineCalBtn.setClickable(false);
        mController.startInclinecal();
        MachineMaintenanceHandler.sendMessageDelayed(MachineMaintenanceHandler.obtainMessage(0), 1000);
    }

    private boolean setDeviceType(int id) {
        byte[] ids = new byte[1];
        ids[0] = (byte) id;
        TapcApp.getInstance().controller.sendCommands(Commands.SET_DEVICE_TYPE, ids);
        SystemClock.sleep(500);
        int getMcuDeviceId = TapcApp.getInstance().controller.getDeviceId();
        if (getMcuDeviceId == id) {
            PreferenceHelper.write(mContext, Config.SETTING_CONFIG, "device", id);
            PreferenceHelper.write(mContext, Config.SETTING_CONFIG, "is_upload_device_infor", false);
            setDeviceTypeShow();
            Toast.makeText(mContext, R.string.admin_machine_set_success, 1000).show();
            return true;
        } else {
            Toast.makeText(mContext, R.string.device_sel_failed, 3000).show();
            return false;
        }
    }

    @OnClick(R.id.setting_bike_b8900ut)
    protected void bikeB8900ut(View v) {
        setDeviceType(4);
    }

    @OnClick(R.id.setting_bike_b8900et)
    protected void bikeB8900et(View v) {
        setDeviceType(2);
    }

    @OnClick(R.id.setting_bike_b8900rt)
    protected void bikeB8900rt(View v) {
        setDeviceType(3);
    }

    private void setDeviceTypeShow() {
        int device = PreferenceHelper.readInt(mContext, Config.SETTING_CONFIG, "device", 0);
        b8900et.setEnabled(true);
        b8900rt.setEnabled(true);
        b8900ut.setEnabled(true);
        switch (device) {
            case 2:
                b8900et.setEnabled(false);
                break;
            case 3:
                b8900rt.setEnabled(false);
                break;
            case 4:
                b8900ut.setEnabled(false);
                break;
            default:
                break;
        }
    }

    @SuppressLint("NewApi")
    @OnRadioGroupCheckedChange(R.id.app_install_group)
    protected void onCheckedChanged(RadioGroup group, int checkedId) {
        try {
            switch (checkedId) {
                case R.id.app_install_allow:
                    PreferenceHelper.write(mContext, Config.SETTING_CONFIG, "app_permisson", true);
                    Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.INSTALL_NON_MARKET_APPS, 1);
                    break;
                case R.id.app_install_ban:
                    PreferenceHelper.write(mContext, Config.SETTING_CONFIG, "app_permisson", false);
                    Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.INSTALL_NON_MARKET_APPS, 0);
                    break;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @SuppressLint("NewApi")
    @OnRadioGroupCheckedChange(R.id.erp_group)
    protected void onCheckedChangedErp(RadioGroup group, int checkedId) {
        try {
            switch (checkedId) {
                case R.id.erp_allow:
                    PreferenceHelper.write(mContext, Config.SETTING_CONFIG, "erp", true);
                    TapcApp.getInstance().hasErp = true;
                    break;
                case R.id.erp_ban:
                    PreferenceHelper.write(mContext, Config.SETTING_CONFIG, "erp", false);
                    TapcApp.getInstance().hasErp = false;
                    break;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @SuppressLint("NewApi")
    @OnRadioGroupCheckedChange(R.id.detectperson_group)
    protected void onCheckedChangedDetectperson(RadioGroup group, int checkedId) {
        try {
            switch (checkedId) {
                case R.id.detectperson_allow:
                    PreferenceHelper.write(mContext, Config.SETTING_CONFIG, "detectperson", true);
                    TapcApp.getInstance().hasDetectPerson = true;
                    break;
                case R.id.detectperson_ban:
                    PreferenceHelper.write(mContext, Config.SETTING_CONFIG, "detectperson", false);
                    TapcApp.getInstance().hasDetectPerson = false;
                    break;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @SuppressLint("NewApi")
    @OnRadioGroupCheckedChange(R.id.test_group)
    protected void onCheckedChangedTest(RadioGroup group, int checkedId) {
        try {
            switch (checkedId) {
                case R.id.test_allow:
                    PreferenceHelper.write(mContext, Config.SETTING_CONFIG, "test", true);
                    TapcApp.getInstance().isTestOpen = true;
                    break;
                case R.id.test_ban:
                    PreferenceHelper.write(mContext, Config.SETTING_CONFIG, "test", false);
                    TapcApp.getInstance().isTestOpen = false;
                    break;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @SuppressLint("NewApi")
    @OnRadioGroupCheckedChange(R.id.scan_qr_group)
    protected void onCheckedChangedScan(RadioGroup group, int checkedId) {
        try {
            switch (checkedId) {
                case R.id.scan_qr_allow:
                    PreferenceHelper.write(mContext, Config.SETTING_CONFIG, "scan_qr", true);
                    Config.HAS_SCAN_QR = true;
                    break;
                case R.id.scan_qr_ban:
                    PreferenceHelper.write(mContext, Config.SETTING_CONFIG, "scan_qr", false);
                    Config.HAS_SCAN_QR = false;
                    break;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @SuppressLint("NewApi")
    @OnRadioGroupCheckedChange(R.id.rfid_group)
    protected void onCheckedChangedRfid(RadioGroup group, int checkedId) {
        try {
            switch (checkedId) {
                case R.id.rfid_allow:
                    PreferenceHelper.write(mContext, Config.SETTING_CONFIG, "rfid", true);
                    Config.HAS_RFID = true;
                    break;
                case R.id.rfid_ban:
                    PreferenceHelper.write(mContext, Config.SETTING_CONFIG, "rfid", false);
                    Config.HAS_RFID = false;
                    break;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @OnClick(R.id.enter_reset)
    protected void enterReset(View v) {
        startActivity(new Intent(Settings.ACTION_PRIVACY_SETTINGS));
    }

    private void uiMessage(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(text);
        if (mAlert != null && mAlert.isShowing()) {
            mAlert.dismiss();
        }
        mAlert = builder.create();
        mAlert.show();
    }

    @OnClick(R.id.service_ip_save)
    protected void saveServiceIp(View v) {
//        String ip = mIpEt.getText().toString();
//        String port = mPortEt.getText().toString();
//        if ((ip != null && !ip.isEmpty()) && (port != null && !port.isEmpty())) {
//            TcpClient.IP = ip;
//            TcpClient.PORT = Integer.valueOf(port).intValue();
//            PreferenceHelper.write(mContext, Config.SETTING_CONFIG, "service_ip", ip);
//            PreferenceHelper.write(mContext, Config.SETTING_CONFIG, "service_port", port);
//            uiMessage(getString(R.string.admin_machine_set_success));
//        } else {
//            uiMessage(getString(R.string.admin_machine_set_fault));
//        }
    }
}
