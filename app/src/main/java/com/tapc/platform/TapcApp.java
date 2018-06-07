package com.tapc.platform;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityManagerNative;
import android.app.Application;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.SoundEffectConstants;

import com.google.gson.Gson;
import com.jht.tapc.jni.KeyEvent;
import com.lidroid.xutils.util.LogUtils;
import com.tapc.android.controller.MachineController;
import com.tapc.android.data.SystemSettingsHelper;
import com.tapc.android.data.Workout;
import com.tapc.platform.activity.MainActivity;
import com.tapc.platform.activity.MainActivity.CTRLType;
import com.tapc.platform.activity.MainActivity.FinishReceiver;
import com.tapc.platform.dto.GroupUserDTO;
import com.tapc.platform.dto.net.Callback;
import com.tapc.platform.dto.net.MyHttpCilent;
import com.tapc.platform.dto.net.MyHttpCilentImpl;
import com.tapc.platform.dto.response.DeviceDataResponse;
import com.tapc.platform.entity.AppInfoEntity;
import com.tapc.platform.entity.ChallengeTpye;
import com.tapc.platform.entity.DeviceData;
import com.tapc.platform.entity.UserInfor;
import com.tapc.platform.media.VaRecordPosition;
import com.tapc.platform.model.common.ConfigModel;
import com.tapc.platform.model.healthcat.bike.BikeData;
import com.tapc.platform.service.MachineStatusService;
import com.tapc.platform.service.MenuService;
import com.tapc.platform.service.VoiceInputService;
import com.tapc.platform.sportsrunctrl.SportsEngin;
import com.tapc.platform.sportsrunctrl.SportsEnginImpl;
import com.tapc.platform.sql.SportRecordItem;
import com.tapc.platform.sql.SportsDataDao;
import com.tapc.platform.sql.UserDataDao;
import com.tapc.platform.utils.AppUtils;
import com.tapc.platform.utils.NetUtils;
import com.tapc.platform.utils.PreferenceHelper;
import com.tapc.platform.utils.SysUtils;
import com.tapc.platform.witget.MenuBar;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

public class TapcApp extends Application {
    public static float MIN_INCLINE;
    public static float MAX_INCLINE;
    public static float STEP_INCLINE;
    public static float DEFAULT_SPEED;
    public static float MIN_SPEED;
    public static float MAX_SPEED;
    public static float STEP_SPEED;
    public static float DEFAULT_INCLINE;

    public MenuBar menuBar;
    public MainActivity mainActivity;

    private static TapcApp instance;
    private SportsEnginImpl mSportsEngin;
    private MyHttpCilent mHttp;

    public KeyEvent keyboardEvent;
    public Workout mWorkout;

    public boolean isNextPage = false;
    public boolean isStart = false;
    private Handler mUIHandler;
    private AudioManager mAudioManager;
    public int nowVolume;
    public int maxVolume;
    public MachineController controller;

    public UserInfor userInfor;
    public SportsDataDao sportsDataDao;
    public UserDataDao userDataDao;

    public VaRecordPosition vaRecordPosition;

    public boolean isSimulation = true;
    public int noPersonTimeCount;
    public int noNoActionCount;
    public boolean isScreenOn = true;
    public boolean isAppToBackground = false;
    public boolean hasDetectPerson = false;
    public boolean hasErp = false;
    public boolean isTestOpen = false;
    private boolean hasIntervalSound = true;
    public boolean noShowNoCtlError = false;
    public int sportsType = ChallengeTpye.NOMAL;
    private DeviceData mDeviceData;

    public List<AppInfoEntity> listAppInfo;
    public BikeData scanCodeData;
    /**
     * SafeKey ״̬
     */
    private boolean mSafeKeyStatus = false;

    public GroupUserDTO groupUserDTO;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//		Thread.setDefaultUncaughtExceptionHandler(new AppExceptionHandler());

        if (!Config.DEFAUL_LANGUAGE.isEmpty()) {
            String language = PreferenceHelper.readString(this, Config.SETTING_CONFIG, "admin_language",
                    Config.DEFAUL_LANGUAGE);
            LogUtils.e(language);
            switchLanguage(language);
        }

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume / 2, AudioManager.FLAG_PLAY_SOUND);
        nowVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        listAppInfo = AppUtils.getAllAppInfo(this);
        clearAppExit(listAppInfo);

        stopService();
        startService();

        keyboardEvent = new KeyEvent(null, 200);
        keyboardEvent.openUinput();
        keyboardEvent.initCom();

        loadLib();

        hasDetectPerson = PreferenceHelper.readBoolean(this, Config.SETTING_CONFIG, "detectperson", hasDetectPerson);
        hasErp = PreferenceHelper.readBoolean(this, Config.SETTING_CONFIG, "erp", hasErp);
        isTestOpen = PreferenceHelper.readBoolean(this, Config.SETTING_CONFIG, "test", isTestOpen);
        Config.HAS_SCAN_QR = PreferenceHelper.readBoolean(this, Config.SETTING_CONFIG, "scan_qr", Config.HAS_SCAN_QR);
        Config.HAS_RFID = PreferenceHelper.readBoolean(this, Config.SETTING_CONFIG, "rfid", Config.HAS_RFID);
        getDeviceData();

        String userInforStr = PreferenceHelper.readString(this, Config.SETTING_CONFIG, "user_infor");
        if (userInforStr != null && Config.SERVICE_ID == 0) {
            userInfor = UserInfor.getUserInfor(userInforStr);
        } else {
            userInfor = new UserInfor();
        }

        sportsDataDao = new SportsDataDao(this);
        userDataDao = new UserDataDao(this);
        clearSportsData();

        setDefaulBantAppInstall();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mScreenReceiver, filter);

        vaRecordPosition = new VaRecordPosition();

        mHttp = new MyHttpCilentImpl(this);
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        if (activeInfo != null) {
            Config.isConnected = true;
        } else {
            Config.isConnected = false;
        }

        autoUploadSportsData();
        clearSomeUserData();

        scanCodeData = new BikeData();
    }

    public DeviceData getDeviceData() {
        if (mDeviceData == null) {
            String data = PreferenceHelper.readString(this, Config.MACHINE_STATUS, "device_data", null);
            if (data != null && !data.isEmpty()) {
                mDeviceData = new Gson().fromJson(data, DeviceData.class);
            }
            if (mDeviceData == null) {
                mDeviceData = new DeviceData();
            }
        }
        return mDeviceData;
    }

    public void uploadDeviceData(final Context context, String deviceId, final DeviceData deviceData) {
        if (deviceData == null || deviceId == null || deviceId.isEmpty()) {
            return;
        }
        TapcApp.getInstance().getHttpClient()
                .uploadDevicData(deviceId, new Gson().toJson(deviceData).toString(), new Callback() {

                    @Override
                    public void onSuccess(String result) {
                    }

                    @Override
                    public void onSuccess(Object o) {
                        // TODO Auto-generated method stub
                        DeviceDataResponse responseDTO = (DeviceDataResponse) o;
                        if (responseDTO != null && responseDTO.getStatus() == 200) {
                            if (responseDTO.getResponse() != null) {
                                DeviceData deviceData = responseDTO.getResponse();
                                if (deviceData != null) {
                                    DeviceData saveDeviceData = new DeviceData();
                                    saveDeviceData.setOlddistance(deviceData.getDistance());
                                    saveDeviceData.setOldtime(deviceData.getTime());
                                    saveDeviceData.setDistance("0");
                                    saveDeviceData.setTime("0");
                                    String data = new Gson().toJson(saveDeviceData);
                                    if (data != null && !data.isEmpty()) {
                                        mDeviceData = saveDeviceData;
                                        PreferenceHelper.write(instance, Config.MACHINE_STATUS, "device_data", data);
                                        Log.d("DeviceData", "set successful");
                                    }
                                }
                            }
                        } else {
                            saveLocalDeviceData(deviceData);
                        }
                        Log.d("DeviceData ", String.valueOf(responseDTO.getStatus()) + " " + responseDTO.getMessage());
                    }

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFailure(Object o) {
                        saveLocalDeviceData(deviceData);
                    }
                });
    }

    public void saveLocalDeviceData(DeviceData deviceData) {
        if (deviceData != null) {
            int distance = Integer.valueOf(deviceData.getOlddistance()).intValue()
                    + Integer.valueOf(deviceData.getDistance()).intValue();
            int time = Integer.valueOf(deviceData.getOldtime()).intValue()
                    + Integer.valueOf(deviceData.getTime()).intValue();
            deviceData.setOlddistance(String.valueOf(distance));
            deviceData.setOldtime(String.valueOf(time));
            deviceData.setDistance("0");
            deviceData.setTime("0");
            String data = new Gson().toJson(deviceData);
            if (data != null && !data.isEmpty()) {
                mDeviceData = deviceData;
                PreferenceHelper.write(instance, Config.MACHINE_STATUS, "device_data", data);
                Log.d("DeviceData", "set successful");
            }
        }
    }

    private long getDeleteTime() {
        long time = 1;
        time = time * 31 * 24 * 60 * 60 * 1000;
        return time;
    }

    private void clearSportsData() {
        long recordTime = PreferenceHelper.readLong(this, Config.SETTING_CONFIG, "datetime", 0);
        long nowTime = System.currentTimeMillis();
        if (recordTime == 0) {
            PreferenceHelper.write(this, Config.SETTING_CONFIG, "datetime", nowTime);
        } else {
            if (((nowTime - recordTime) >= getDeleteTime()) || (recordTime - nowTime) >= getDeleteTime()) {
                sportsDataDao.deleteAllItem();
                PreferenceHelper.write(this, Config.SETTING_CONFIG, "datetime", nowTime);
                LogUtils.e("clear sport all data");
            }
        }
    }

    public void sendStartServiceIntent(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startService(intent);
    }

    public void sendStopServiceIntent(Class<?> cls) {
        if (isServiceRunning(cls.getName())) {
            Intent intent = new Intent(this, cls);
            stopService(intent);
        }
    }

    public void startService() {
        sendStartServiceIntent(MenuService.class);
        sendStartServiceIntent(MachineStatusService.class);
        if (Config.hasInputVoicePlay) {
            sendStartServiceIntent(VoiceInputService.class);
        }
    }

    public void stopService() {
        sendStopServiceIntent(MenuService.class);
        sendStopServiceIntent(MachineStatusService.class);
        sendStopServiceIntent(VoiceInputService.class);
    }

    private boolean isServiceRunning(String seviceName) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().endsWith(seviceName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void setMachineParameter() {
        MIN_INCLINE = SystemSettingsHelper.MIN_INCLINE;
        MAX_INCLINE = SystemSettingsHelper.MAX_INCLINE;
        STEP_INCLINE = SystemSettingsHelper.STEP_INCLINE;
        DEFAULT_INCLINE = SystemSettingsHelper.DEFAULT_INCLINE;
        MIN_SPEED = SystemSettingsHelper.MIN_SPEED;
        MAX_SPEED = SystemSettingsHelper.MAX_SPEED;
        STEP_SPEED = SystemSettingsHelper.STEP_SPEED;
        DEFAULT_SPEED = SystemSettingsHelper.DEFAULT_SPEED;
        SystemSettingsHelper.setFloat(SystemSettingsHelper.KEY_ORIGIN_INCLINE, 0.0f);
    }

    private void loadLib() {
        SystemSettingsHelper.Init(this);
        setMachineParameter();
        controller = MachineController.getInstance();
        controller.setReceiveBroadcast(TapcApp.getInstance());
        controller.initController();
        controller.start();
    }

    public SportsEngin getSportsEngin() {
        if (mSportsEngin == null) {
            mSportsEngin = new SportsEnginImpl();
        }
        return mSportsEngin;
    }

    public static TapcApp getInstance() {
        return instance;
    }

    /**
     * @param type
     * @param value
     * @return
     */
    public double setValue(CTRLType type, double value) {
        if (mSportsEngin != null) {
            return mSportsEngin.setValue(type, value);
        }
        return 0;
    }

    public void setBroadcast(Context context, FinishReceiver finishReceiver) {
        mSportsEngin.setBroadcast(context, finishReceiver);
    }

    public boolean isNextPage() {
        return isNextPage;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setNextPage(boolean nextPage) {
        isNextPage = nextPage;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public Boolean sendUIMessage(int msgID) {
        if (null == mUIHandler) {
            return false;
        }

        Message msg = mUIHandler.obtainMessage(msgID);
        return mUIHandler.sendMessage(msg);
    }

    public void setUIHandler(Handler handler) {
        this.mUIHandler = handler;
    }

    public void setWorkout(Workout workout) {
        mWorkout = workout;
    }

    public Workout getWorkout() {
        return mWorkout;
    }

    public void setSafeKeyStatus(boolean flag) {
        mSafeKeyStatus = flag;
    }

    public boolean getSafeKeyStatus() {
        return mSafeKeyStatus;
    }

    // ��������
    public MyHttpCilent getHttpClient() {
        return mHttp;
    }

    public void openVolume() {
        mAudioManager
                .adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
    }

    public void setVolume() {
        if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, nowVolume, AudioManager.FLAG_SHOW_UI);
        } else {
            nowVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, AudioManager.FLAG_SHOW_UI);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, AudioManager.FLAG_SHOW_UI);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, AudioManager.FLAG_SHOW_UI);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI);
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }
        openVolume();
    }

    public void addVolume() {
        if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, nowVolume, AudioManager.FLAG_SHOW_UI);
        }
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_SHOW_UI);
        nowVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public void subVolume() {
        if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, nowVolume, AudioManager.FLAG_SHOW_UI);
        }
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_SHOW_UI);
        nowVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public void clickSound() {
        mAudioManager.playSoundEffect(SoundEffectConstants.CLICK);
    }

    public void switchLanguage(String language) {
        boolean selctSystem = true;
        if (selctSystem) {
            try {
                IActivityManager amn = null;
                Configuration config = null;
                amn = ActivityManagerNative.getDefault();
                config = amn.getConfiguration();
                config.userSetLocale = true;
                Config.LANGUAGE = language;
                if (language.equals("0")) {
                    config.locale = Locale.ENGLISH;
                } else if (language.equals("1")) {
                    config.locale = Locale.TAIWAN;
                } else if (language.equals("2")) {
                    config.locale = Locale.SIMPLIFIED_CHINESE;
                }
                amn.updateConfiguration(config);
            } catch (Exception e) {
            }
        }
    }

    @SuppressLint("NewApi")
    private void setDefaulBantAppInstall() {
        try {
            boolean appPermisson = (Settings.Global.getInt(this.getContentResolver(),
                    Settings.Global.INSTALL_NON_MARKET_APPS, 0) > 0 ? true : false);
            if (appPermisson) {
                PreferenceHelper.write(this, Config.SETTING_CONFIG, "app_permisson", false);
                Settings.Global.putInt(this.getContentResolver(), Settings.Global.INSTALL_NON_MARKET_APPS, 0);
                LogUtils.e("set default app permisson : ban");
            }
        } catch (Exception e) {
            LogUtils.e("set default app permisson fail");
        }
    }

    public void startMachine() {
        controller.startMachine(((int) SystemSettingsHelper.MAX_INCLINE), ((int) SystemSettingsHelper.MAX_INCLINE));
    }

    public void stopMachine() {
        controller.stopMachine(0);
    }

    public void clearSportsEngin() {
        if (!isStart && instance.getSportsEngin().getFitness() != null) {
            TapcApp.getInstance().mainActivity.setLoadMode();
            TapcApp.getInstance().getSportsEngin().openFitness(null);
        }
    }

    public void playIntervalSound() {
        if (hasIntervalSound && SystemSettingsHelper.getIntervalSound()) {
            SysUtils.playBeep(this, R.raw.start);
            SystemSettingsHelper.setIntervalSound(false);
        }
    }

    public void enterERP() {
        controller.enterErpStatus(0);
    }

    public void clearAppExit(List<AppInfoEntity> listAppInfo) {
        if (listAppInfo == null) {
            return;
        }
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(Integer.MAX_VALUE);
        for (ActivityManager.RunningTaskInfo amTask : runningTasks) {
            for (AppInfoEntity appInfoEntity : listAppInfo) {
                if (appInfoEntity.getPkgName().equals(amTask.baseActivity.getPackageName())) {
                    Method method = null;
                    try {
                        method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage",
                                String.class);
                        method.invoke(manager, amTask.baseActivity.getPackageName());
                    } catch (Exception e) {
                        LogUtils.d("--app--" + appInfoEntity.getPkgName() + "--exit--fail");
                        continue;
                    }
                    LogUtils.d("--app--" + appInfoEntity.getPkgName() + "--exit--success");
                }
            }
        }
    }

    public void clearAppUserData() {
        new Runnable() {
            public void run() {
                AppUtils.clearAppUserData(TapcApp.this);
            }
        }.run();
    }

    private void clearSomeUserData() {
        new Runnable() {
            public void run() {
                AppUtils.clearAppUserData(TapcApp.this, "com.tencent.qqpinyin.pad");
            }
        }.run();
    }

    public void autoUploadSportsData() {
        if (Config.SERVICE_ID == 1 || !Config.HAS_SERVICE) {
            return;
        }
        if (!userInfor.getUsername().isEmpty() && !userInfor.getUid().isEmpty()) {
            List<SportRecordItem> uploadDataList = sportsDataDao.getNotUploadRecord(userInfor.getUsername());
            if (uploadDataList != null && !uploadDataList.isEmpty()) {
                for (final SportRecordItem uploadItem : uploadDataList) {
                    if (Double.parseDouble(uploadItem.sportsData.getDistance()) < Config.UPLOAD_MINDISTANCE) {
                        continue;
                    }
                    TapcApp.getInstance()
                            .getHttpClient()
                            .uploadSportsData(uploadItem.userInfor.getUid(), uploadItem.userInfor.getUsername(),
                                    uploadItem.getDateTime(), uploadItem.sportsData, new Callback() {
                                        @Override
                                        public void onStart() {
                                        }

                                        @Override
                                        public void onSuccess(Object o) {
                                        }

                                        @Override
                                        public void onSuccess(String result) {
                                            JSONObject jsonObj;
                                            try {
                                                jsonObj = new JSONObject(result);
                                                if (jsonObj != null) {
                                                    if (jsonObj.has("rid")) {
                                                        Log.d("Sports data Upload successful", jsonObj.getString
                                                                ("rid"));
                                                        uploadItem.setUploadStatus(1);
                                                        sportsDataDao.updateUploadStatus(uploadItem.id,
                                                                uploadItem.getUploadStatus());
                                                    } else if (jsonObj.has("err")) {
                                                        Log.d("Sports data Upload failed", jsonObj.getString("err"));
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                // TODO Auto-generated catch
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Object o) {
                                            Log.d("Sports data Upload failed", "cannot connect service");
                                        }
                                    });

                }
            }
        }
    }

    private final BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                isScreenOn = true;
                LogUtils.d("-----------------screen is on...");
                TapcApp.getInstance().noNoActionCount = 0;
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                isScreenOn = false;
                LogUtils.d("-----------------screen is off...");
                TapcApp.getInstance().noNoActionCount = 0;
            }
        }
    };

    /**
     * 判断当前应用程序处于前台还是后台
     */
    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public void setBrightness(Activity activity, int value) {
        try {
            android.provider.Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                    value);
        } catch (Exception e) {
        }
    }

    public void setDeviceRunStatus(byte status) {
        scanCodeData.setStatus(status);
        EventBus.getDefault().post(scanCodeData);
    }

    public void initDeviceId(Context context) {
        String id = ConfigModel.getDeviceId(context, null);
        if (TextUtils.isEmpty(id)) {
            id = NetUtils.getDeviceId(context);
            if (!TextUtils.isEmpty(id)) {
                ConfigModel.setDeviceId(context, id);
            }
        }
        Config.DEVICE_ID = id;
    }
}
