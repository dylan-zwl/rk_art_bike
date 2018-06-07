package com.tapc.platform.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnTouch;
import com.tapc.android.controller.MachineController;
import com.tapc.android.data.Enum.WorkoutStage;
import com.tapc.android.data.MessageDefine;
import com.tapc.android.data.Workout;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.dto.GroupUserDTO;
import com.tapc.platform.entity.AppInfoEntity;
import com.tapc.platform.entity.BikeCtlType;
import com.tapc.platform.entity.ChallengeTpye;
import com.tapc.platform.entity.FixedAppEntity;
import com.tapc.platform.entity.UserInfor;
import com.tapc.platform.model.common.ConfigModel;
import com.tapc.platform.model.common.UserManageModel;
import com.tapc.platform.model.healthcat.DeviceRunStatus;
import com.tapc.platform.model.healthcat.bike.BikeData;
import com.tapc.platform.service.MachineStatusService;
import com.tapc.platform.stardandctrl.WorkoutCtrl;
import com.tapc.platform.stardandctrl.WorkoutGoal;
import com.tapc.platform.stardandctrl.WorkoutText;
import com.tapc.platform.utils.AppType;
import com.tapc.platform.utils.AppUtils;
import com.tapc.platform.utils.PreferenceHelper;
import com.tapc.platform.utils.SysUtils;
import com.tapc.platform.witget.DeviceSelDialog;
import com.tapc.platform.witget.scancode.ScanCodeEvent;
import com.tapc.platform.workout.MessageType;
import com.tapc.platform.workout.WorkoutListener;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

public class MainActivity extends BaseActivity {
    public static final int SD_FREE_SIZE = 200;
    public static final int INTERNAL_MEMORY_FREE_SIZE = 60;

    public enum CTRLType {
        INCLINE, SPEED
    }

    @ViewInject(R.id.screen1)
    public LinearLayout mMain1View1;
    @ViewInject(R.id.screen2)
    public LinearLayout mMain1View2;
    @ViewInject(R.id.main_LinearLayout_line_workout1)
    private LinearLayout mLayout_stop;
    @ViewInject(R.id.main_LinearLayout_line_workout2)
    private LinearLayout mLayout_start;
    @ViewInject(R.id.inclineCtrl)
    private WorkoutCtrl mInclineCtrl;
    @ViewInject(R.id.speedCtrl)
    private WorkoutCtrl mSpeedCtrl;
    @ViewInject(R.id.workout_goal)
    private WorkoutGoal mWorkoutGoal;
    @ViewInject(R.id.totalTime)
    private TextView mGoalRun;
    @ViewInject(R.id.scan_code_time)
    private WorkoutText mWorkoutTextTime;
    @ViewInject(R.id.distance)
    private WorkoutText mWorkoutTextDistance;
    @ViewInject(R.id.calorie)
    private WorkoutText mWorkoutTextCalorie;
    @ViewInject(R.id.heart)
    private WorkoutText mWorkoutTextHeart;
    @ViewInject(R.id.altitude)
    private WorkoutText mWorkoutTextPace;
    @ViewInject(R.id.pauseButton)
    private Button mPause;
    @ViewInject(R.id.restartButton)
    private Button mRestart;
    @ViewInject(R.id.loginButton)
    private Button mLoginShowText;
    @ViewInject(R.id.login_name_edit)
    private EditText login_name;
    @ViewInject(R.id.login_password_edit)
    private EditText login_password;

    @ViewInject(R.id.wifiButton)
    private Button mWifiBtn;
    @ViewInject(R.id.datetimeButton)
    private Button mDateTimeBtn;
    private FinishReceiver mFinishReceiver;
    private UIHandler mUIHandler = new UIHandler();

    /**
     * 单击弹出设置的次数
     */
    public static final int SETTING_COUNT = 10;
    private int mClickCount = 1;
    private long mClickTime;

    private int bikeSpeedType = 1;
    private int showSpeedTime = 0;
    private double mBikeSpeed;

    private Activity mActivity;
    private BikeData mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);

        mData = TapcApp.getInstance().scanCodeData;
        TapcApp.getInstance().setBrightness(this, 255);
        TapcApp.getInstance().mainActivity = this;
        mActivity = this;
        initView();
        clearSDData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TapcApp.getInstance().isAppToBackground = false;
        if (TapcApp.getInstance().menuBar != null) {
            if (!TapcApp.getInstance().isNextPage()) {
                TapcApp.getInstance().menuBar.showOSD(false);
            }
        }
        if (!UserManageModel.getInstance().isLogined()) {
            int device = PreferenceHelper.readInt(mActivity, Config.SETTING_CONFIG, "device", 0);
            if (device == 0) {
                mUIHandler.sendEmptyMessageDelayed(MessageType.MSG_UI_SHOW_DEVICE_DIALOG, 500);
            }
            mUIHandler.sendEmptyMessageDelayed(MessageType.MSG_UI_SHOW_QR_DIALOG, 500);
        }
    }

    @Override
    protected void onStop() {
        TapcApp.getInstance().isAppToBackground = true;
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @OnClick(R.id.nextButton)
    protected void onNext(View v) {
        TapcApp.getInstance().setNextPage(true);
        mMain1View1.setVisibility(View.INVISIBLE);
        mMain1View2.setVisibility(View.VISIBLE);
        if (TapcApp.getInstance().getSportsEngin().isRunning()) {
            TapcApp.getInstance().menuBar.showOSD(true);
        }
    }

    @OnClick(R.id.preButton)
    protected void onPre(View v) {
        TapcApp.getInstance().setNextPage(false);
        mMain1View1.setVisibility(View.VISIBLE);
        mMain1View2.setVisibility(View.INVISIBLE);
        if (TapcApp.getInstance().getSportsEngin().isRunning()) {
            TapcApp.getInstance().menuBar.showOSD(false);
        }
    }

    private void startCountdown() {
        if (Config.HAS_SCAN_QR || Config.HAS_RFID) {
            if (!UserManageModel.getInstance().isLogined()) {
                setQrCodeDialogShow(mActivity, true);
                TapcApp.getInstance().setStart(false);
                return;
            }
        }
        MachineController.getInstance().registerPreStart();
        TapcApp.getInstance().setStart(true);
        Intent intent = new Intent();
        intent.setAction(MachineStatusService.COUNTDOWN_NAME);
        sendBroadcast(intent);

        TapcApp.getInstance().setDeviceRunStatus(DeviceRunStatus.STARTED);
    }

    public void setLoadMode() {
        Config.sBikeCtlType = BikeCtlType.LOAD;
        mSpeedCtrl.setValueCtlVisibility(View.INVISIBLE);
        mInclineCtrl.setValueCtlVisibility(View.VISIBLE);
    }

    public void setWattMode() {
        Config.sBikeCtlType = BikeCtlType.WATT;
        mSpeedCtrl.setValueCtlVisibility(View.VISIBLE);
        mInclineCtrl.setValueCtlVisibility(View.INVISIBLE);
    }

    public void startRun() {
        if (TapcApp.getInstance().isTestOpen) {
            SysUtils.getLoadMap(MainActivity.this);
        }

        TapcApp.getInstance().setDeviceRunStatus(DeviceRunStatus.RUNNING);

        TapcApp.getInstance().setStart(true);
        mLayout_stop.setVisibility(View.INVISIBLE);
        mLayout_start.setVisibility(View.VISIBLE);
        mPause.setVisibility(View.VISIBLE);
        mRestart.setVisibility(View.INVISIBLE);
        if (TapcApp.getInstance().isNextPage()) {
            TapcApp.getInstance().menuBar.showOSD(true);
        } else {
            TapcApp.getInstance().menuBar.showOSD(false);
        }
        if (TapcApp.getInstance().getSportsEngin().isPause()) {
            TapcApp.getInstance().getSportsEngin().setPause(false);
        }
        mFinishReceiver = new FinishReceiver();
        IntentFilter filter = new IntentFilter(MessageDefine.MSG_WORKOUT_FINISH);
        TapcApp.getInstance().registerReceiver(mFinishReceiver, filter);
        TapcApp.getInstance().setBroadcast(this, mFinishReceiver);
        TapcApp.getInstance().getSportsEngin().start();
        TapcApp.getInstance().noPersonTimeCount = 0;
        initMachineCtl();
        TapcApp.getInstance().vaRecordPosition.setVaRecordPosition(false, 0, -1);
        Config.LEAVE_GROUP = false;
    }

    @OnClick(R.id.startButton)
    protected void onStart(View v) {
        v.setClickable(false);
        if (!TapcApp.getInstance().isStart()) {
            startCountdown();
        }
        v.setClickable(true);
    }

    private void initMachineCtl() {
        mInclineCtrl.SetRange(TapcApp.MIN_INCLINE, TapcApp.MAX_INCLINE, TapcApp.STEP_INCLINE, CTRLType.INCLINE);
        mSpeedCtrl.SetRange(TapcApp.MIN_SPEED, TapcApp.MAX_SPEED, TapcApp.STEP_SPEED, CTRLType.SPEED);
    }

    public void initView() {
        mLayout_start.setVisibility(View.INVISIBLE);
        mLayout_stop.setVisibility(View.VISIBLE);
        mRestart.setVisibility(View.INVISIBLE);
        onPre(null);
        TapcApp.getInstance().setUIHandler(mUIHandler);
        // String nowLoginUser = TapcApp.getInstance().userInfor.getUsername();
        // if (nowLoginUser != null && !nowLoginUser.isEmpty()) {
        // setLoginUserName(nowLoginUser);
        // }
        setLoadMode();
//        TapcApp.getInstance().controller.stopMachine(0);
    }

    public void stopRun() {
        SysUtils.playBeep(MainActivity.this, R.raw.beeps);
        mLayout_start.setVisibility(View.INVISIBLE);
        mLayout_stop.setVisibility(View.VISIBLE);
        TapcApp.getInstance().menuBar.showOSD(false);
        mInclineCtrl.stop();
        mSpeedCtrl.stop();
        TapcApp.getInstance().getSportsEngin().stop();
        TapcApp.getInstance().getSportsEngin().setWorkoutListener(null);
        TapcApp.getInstance().unregisterReceiver(mFinishReceiver);
        mFinishReceiver = null;
        TapcApp.getInstance().setStart(false);
//        TapcApp.getInstance().getSportsEngin().setPause(false);
        TapcApp.getInstance().vaRecordPosition.setVaRecordPosition(false, 0, -1);

        if (TapcApp.getInstance().sportsType == ChallengeTpye.NOMAL) {
            SportResultActivity.launch(this, "stoprun_show");
        } else {
            ChallengeResultActivity.launch(this, "");
        }
        Config.LEAVE_GROUP = true;
        setLoadMode();
    }

    @OnClick(R.id.stopButton)
    protected void onStop(View v) {
        stopRun();
    }

    public void pauseRun() {
        TapcApp.getInstance().getSportsEngin().pause();
        mRestart.setVisibility(View.VISIBLE);
        mPause.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.pauseButton)
    protected void onPause(View v) {
        pauseRun();
    }

    public void restartRun() {
        TapcApp.getInstance().getSportsEngin().restart();
        mRestart.setVisibility(View.INVISIBLE);
        mPause.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.restartButton)
    public void onRestart(View v) {
        restartRun();
    }

    public WorkoutCtrl getSpeedCtrl() {
        return mSpeedCtrl;
    }

    public WorkoutCtrl getInclineCtrl() {
        return mInclineCtrl;
    }

    @OnClick(R.id.goalDistanceButton)
    public void onGoalDistance(View v) {
        GoalActivity.launch(this, 1);
    }

    @OnClick(R.id.goalTimeButton)
    public void onGoalTime(View v) {
        GoalActivity.launch(this, 2);
    }

    @OnClick(R.id.goalCalorieButton)
    public void onGoalCalorie(View v) {
        GoalActivity.launch(this, 3);
    }

    public void checkPerson(Workout workout) {
        if (TapcApp.getInstance().hasDetectPerson) {
            int paceRate = workout.getPaceFlag();
            if (paceRate == 0 && !TapcApp.getInstance().getSportsEngin().isPause()) {
                TapcApp.getInstance().noPersonTimeCount++;
                if (TapcApp.getInstance().noPersonTimeCount >= Config.NOPERSON_DELAYTIME * 2) {
                    TapcApp.getInstance().noPersonTimeCount = 0;
                    stopRun();
                }
            } else {
                TapcApp.getInstance().noPersonTimeCount = 0;
            }
        }
    }

    public WorkoutListener mWorkoutListener = new WorkoutListener() {
        @Override
        public void update(Workout workout) {
            // TODO Auto-generated method stub
            // time
            long time = workout.getTotalTime();
            mWorkoutTextTime.setValue(String.format("%02d:%02d", time / 60, time % 60));

            // distance
            double distance = workout.getTotalDistance();
            mWorkoutTextDistance.setValue(String.format("%.2f", distance));

            // calorie
            double calorie = workout.getTotalCalorie();
            mWorkoutTextCalorie.setValue(String.format("%.0f", calorie));

            // heartRate
            if (TapcApp.getInstance().isTestOpen) {
                mWorkoutTextHeart.setUnit("(pwm)");
                String pwm = String.valueOf(TapcApp.getInstance().controller.getPwm());
                mWorkoutTextHeart.setValue(pwm);
            } else {
                mWorkoutTextHeart.setValue(String.valueOf(workout.getHeartRate()));
            }

            // Altitude
            // double altitude = workout.getTotalAltitude();
            // mWorkoutTextPace.setValue(String.format("%.2f", altitude));
            int speedType = getBikeSpeedType();
            if (speedType == 0) {
                mWorkoutTextPace.setTitle(getResources().getString(R.string.bike_speed));
                mWorkoutTextPace.setUnit("km/h");
                double bikeSpeed = workout.getBikeSpeed();
                mWorkoutTextPace.setValue(String.format("%.1f", bikeSpeed));
            } else {
                mWorkoutTextPace.setTitle(getResources().getString(R.string.bike_rpm));
                mWorkoutTextPace.setUnit("rpm");
                double bikeSpeedRpm = workout.getBikeRpmSpeed();
                mWorkoutTextPace.setValue(String.format("%.0f", bikeSpeedRpm));
            }
            setBikeSpeed(workout.getBikeSpeed());

            if (workout.getSpeed() != mSpeedCtrl.getCtlValue()) {
                mSpeedCtrl.setShowValue(workout.getSpeed(), TapcApp.getInstance().isSimulation);
            }
            if (workout.getIncline() != mInclineCtrl.getCtlValue()) {
                mInclineCtrl.setShowValue(workout.getIncline(), TapcApp.getInstance().isSimulation);
            }

            if (Config.sBikeCtlType == BikeCtlType.WATT) {
                mInclineCtrl.setShowValue(TapcApp.getInstance().controller.getIncline());
            }

            com.tapc.android.data.Enum.WorkoutGoal goal = workout.getWorkoutGoal();
            double cur = workout.getGoal();
            if (goal == goal.TIME) {
                if (workout.getWorkoutStage() == WorkoutStage.WARMUP) {
                    // mWorkoutGoal.setTitleName(getString(R.string.warmUp_time),
                    // "min");
                } else if (workout.getWorkoutStage() == WorkoutStage.COOLDOWN) {
                    mWorkoutGoal.setTitleName(getString(R.string.relax_time), "min");
                } else {
                    mWorkoutGoal.setTitleName(getString(R.string.goal_sports), "min");
                }
                int goaltime = (int) workout.getTargetTime();
                mGoalRun.setText(String.format("%d", goaltime / 60));
                if (goaltime != 0) {
                    mWorkoutGoal.SetRange(0, (float) goaltime);
                    mWorkoutGoal.setPos((float) cur);
                }
            } else if (goal == goal.DISTANCE) {
                mWorkoutGoal.setTitleName(getString(R.string.goal_sports), "km");
                double goaldistance = workout.getTargetDistance();
                mGoalRun.setText(String.format("%.0f", goaldistance));
                if (distance != 0) {
                    mWorkoutGoal.SetRange(0, (float) (goaldistance * 100));
                    mWorkoutGoal.setPos((float) (cur * 100));
                }
            } else if (goal == goal.CALORIE) {
                mWorkoutGoal.setTitleName(getString(R.string.goal_sports), "kcal");
                double goalcalorie = workout.getTargetCalorie();
                mGoalRun.setText(String.format("%.0f", goalcalorie));
                if (calorie != 0) {
                    mWorkoutGoal.SetRange(0, (float) (goalcalorie * 100));
                    mWorkoutGoal.setPos((float) (cur * 100));
                }
            }
            checkPerson(workout);
            TapcApp.getInstance().playIntervalSound();

            updateGroupUserDTO(time, distance, calorie, getBikeSpeed());

            mData.setRunTime((int) time);
            mData.setDistance((int) (distance * 1000));
            mData.setCalorie((float) calorie);
            mData.setResistance((int) workout.getIncline());
            mData.setSpeed((float) getBikeSpeed());
            mData.setHeart((int) workout.getHeart());
            mData.setRounds(MachineController.getInstance().getRounds());
        }
    };

    public int getBikeSpeedType() {
        showSpeedTime++;
        if (showSpeedTime >= 20) {
            showSpeedTime = 0;
            if (bikeSpeedType == 0) {
                bikeSpeedType = 1;
            } else {
                bikeSpeedType = 0;
            }
        }
        return bikeSpeedType;
    }

    public void updateGroupUserDTO(long time, double distance, double calorie, double speed) {
        GroupUserDTO groupUserDTO = TapcApp.getInstance().groupUserDTO;
        if (groupUserDTO != null) {
            groupUserDTO.setDuration(time);
            groupUserDTO.setDistance((int) (distance * GroupUserDTO.VALUE));
            groupUserDTO.setCalories((int) (calorie * GroupUserDTO.VALUE));
            groupUserDTO.setSpeed((int) (speed * GroupUserDTO.VALUE));
        }
    }

    @OnClick(R.id.loginButton)
    protected void loginButtonOnclick(View v) {
        if (TapcApp.getInstance().getSportsEngin().isRunning()) {
            Toast.makeText(this, R.string.cannot_operate, Toast.LENGTH_SHORT).show();
        } else {
            startTapcActivity(this, LoginActivity.class);
        }
    }

    public void setLoginUserName(String name) {
        mLoginShowText.setText(name);
    }

    @OnClick(R.id.registerButton)
    protected void registerOnclick(View v) {
        if (TapcApp.getInstance().getSportsEngin().isRunning()) {
            Toast.makeText(this, R.string.cannot_operate, Toast.LENGTH_SHORT).show();
        } else {
            startTapcActivity(this, RegisterActivity.class);
        }
    }

    @OnClick(R.id.groupsButton)
    protected void groupsButtonOnclick(View v) {
        if (Config.isConnected) {
            startTapcActivity(this, GroupsActivity.class);
        } else {
            Toast.makeText(this, R.string.net_unconnected, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.vaButton)
    public void openVA(View v) {
        if (TapcApp.getInstance().getSportsEngin().isRunning()
                && TapcApp.getInstance().vaRecordPosition.isNeedToResume()) {
            startTapcActivity(this, ScenePlayActivity.class);
        } else {
            startTapcActivity(this, SceneRunActivity.class);
        }
    }

    @OnClick(R.id.internetButton)
    public void openInternet(View v) {
        startTapcActivity(FixedAppEntity.INTERNET.packageName, FixedAppEntity.INTERNET.className);
    }

    @OnClick(R.id.weatherButton)
    public void openWeather(View v) {
        startTapcActivity(FixedAppEntity.WEATHER.packageName, FixedAppEntity.WEATHER.className);
    }

    @OnClick(R.id.gameButton)
    public void openGame(View v) {
        startTapcActivity(FixedAppEntity.GAME.packageName, FixedAppEntity.GAME.className);
    }

    @OnClick(R.id.storeButton)
    public void openStore(View v) {
        startTapcActivity(FixedAppEntity.STORE.packageName, FixedAppEntity.STORE.className);
    }

    @OnClick(R.id.videoButton)
    public void openVideo(View v) {
        startTapcActivity(FixedAppEntity.VIDEO.packageName, FixedAppEntity.VIDEO.className);
    }

    @OnClick(R.id.musicButton)
    public void openMusic(View v) {
        startTapcActivity(FixedAppEntity.MUSIC.packageName, FixedAppEntity.MUSIC.className);
    }

    @OnClick(R.id.newsButton)
    public void openBlog(View v) {
        startTapcActivity(FixedAppEntity.NEWS.packageName, FixedAppEntity.NEWS.className);
    }

    @OnClick(R.id.bluetoothButton)
    public void bluetoothOnclick(View v) {
        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
    }

    @OnClick(R.id.wifiButton)
    public void wifiOnclick(View v) {
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    @OnClick(R.id.datetimeButton)
    public void datetimeOnclick(View v) {
        startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));
    }

    @OnClick(R.id.app)
    public void appOnclick(View v) {
        ApplicationActivity.launch(MainActivity.this, AppType.NO_TYPE);
    }

    @OnClick(R.id.helpButton)
    public void onHelp(View v) {
        startTapcActivity(this, WattConstanAcitvity.class);
    }

    @OnClick(R.id.challenges)
    public void challengeOnclik(View v) {
        UserInfor userInfor = TapcApp.getInstance().userInfor;
        if (userInfor == null || userInfor.getUsername().isEmpty()) {
            Toast.makeText(this, R.string.need_loggin_user, Toast.LENGTH_SHORT).show();
        } else if (TapcApp.getInstance().getSportsEngin().isRunning()) {
            Toast.makeText(this, R.string.cannot_operate, Toast.LENGTH_SHORT).show();
        } else {
            startTapcActivity(this, ChooseChallengeModeActivity.class);
        }
    }

    @OnClick(R.id.reportButton)
    public void onReport(View v) {
        if (TapcApp.getInstance().getSportsEngin().isRunning()) {
            Toast.makeText(this, R.string.cannot_operate, Toast.LENGTH_SHORT).show();
        } else {
            SportResultActivity.launch(this, "click_show");
        }
    }

    @OnClick(R.id.exerciseButton)
    public void onExercise(View v) {
        if (TapcApp.getInstance().getSportsEngin().isRunning()) {
            Toast.makeText(this, R.string.cannot_operate, Toast.LENGTH_SHORT).show();
        } else {
            startTapcActivity(this, ProgramAcitvity.class);
        }
    }

    @OnClick(R.id.languageButton)
    public void onLanguage(View v) {
        startTapcActivity(this, LanguageAcivity.class);
    }

    public void startTapcActivity(Context context, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        startActivity(intent);
    }

    private void startTapcActivity(String packageName, String className) {
        if (isAppInstalled(this, packageName)) {
            ApplicationActivity.launch(MainActivity.this, AppType.START_APP, packageName, className);
        }
    }

    /*
     * check the app is installed
     */
    private boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    public class FinishReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            TapcApp.getInstance().sendUIMessage(MessageType.MSG_UI_MAIN_STOP);
        }
    }

    @SuppressLint("HandlerLeak")
    class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MessageType.MSG_UI_MAIN_PAGE_PRE:
                    onPre(null);
                    break;
                case MessageType.MSG_UI_MAIN_START:
                    if (!TapcApp.isApplicationBroughtToBackground(MainActivity.this)) {
                        ActivityManager am = (ActivityManager) MainActivity.this.getSystemService(Context
                                .ACTIVITY_SERVICE);
                        ComponentName app = am.getRunningTasks(1).get(0).topActivity;
                        if (!app.getClassName().equals("com.tapc.platform.activity.ScenePlayActivity")) {
                            TapcApp.getInstance().menuBar.backHome();
                            onPre(null);
                        }
                    }
                    startCountdown();
                    break;
                case MessageType.MSG_UI_MAIN_STOP:
                    if (!TapcApp.isApplicationBroughtToBackground(MainActivity.this)) {
                        // TapcApp.getInstance().menuBar.backHome();
                        onPre(null);
                    }
                    stopRun();
                    break;
                case MessageType.MSG_UI_BASE:
                    break;
                case MessageType.MSG_UI_MAIN_LANGUAGE:
                    reload();
                    break;
                case MessageType.MSG_UI_SHOW_QR_DIALOG:
                    int device = PreferenceHelper.readInt(mActivity, Config.SETTING_CONFIG, "device", 0);
                    if (device != 0) {
                        setQrCodeDialogShow(mActivity, true);
                    } else {
                        mUIHandler.sendEmptyMessageDelayed(MessageType.MSG_UI_SHOW_QR_DIALOG, 500);
                    }
                    break;
                case MessageType.MSG_UI_SHOW_DEVICE_DIALOG:
                    setDeviceDialogShow(mActivity);
                    break;
                default:
                    break;
            }
        }
    }

    public void reload() {
        TapcApp.getInstance().stopService();
        finish();
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void startTestMachine(Intent intent) {
        TapcApp.getInstance().stopService();
        startActivity(intent);
        System.exit(0);
    }

    private void clearSDData() {
        if (avaiableMedia()) {
            if (getSDFreeSize() < SD_FREE_SIZE || getInternalMemorySize() < INTERNAL_MEMORY_FREE_SIZE) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(android.R.drawable.ic_menu_info_details);
                builder.setTitle(getString(R.string.clear_app_data));
                builder.setMessage(getString(R.string.not_enough_storage));
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        List<AppInfoEntity> mlistAppInfo = TapcApp.getInstance().listAppInfo;
                        TapcApp.getInstance().clearAppExit(mlistAppInfo);
                        AppUtils.clearAppUserData(MainActivity.this);
                        SysUtils.RecursionDeleteFile(new File(Config.IN_SD_FILE_PATH));
                        Toast.makeText(MainActivity.this, R.string.app_clear_completed, Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    /* 判断SD卡是否存在 返回true表示存在 */
    private boolean avaiableMedia() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressLint("NewApi")
    private long getFreeSizeMB(String path) {
        StatFs sf = new StatFs(path);
        long blockSize = sf.getBlockSizeLong();
        long freeBlocks = sf.getAvailableBlocksLong();
        long freeSizeMB = (freeBlocks * blockSize) / 1024 / 1024;
        return freeSizeMB;// 单位MB
    }

    /* 获取SD卡可用空间 */
    private long getSDFreeSize() {
        File path = Environment.getExternalStorageDirectory();
        return getFreeSizeMB(path.getPath());
    }

    /* 内部剩余存储空间 */
    private long getInternalMemorySize() {
        File path = Environment.getDataDirectory();
        return getFreeSizeMB(path.getPath());
    }

    public void setQrCodeDialogShow(Context context, boolean visibility) {
        if (Config.HAS_SCAN_QR || Config.HAS_RFID) {
            if (!UserManageModel.getInstance().isLogined()) {
                showScanCodeDialog(visibility, 500);
            }
        }
    }

    /**
     * 功能描述 : 扫码界面显示设置
     */
    private void showScanCodeDialog(final boolean visibility, int delayTime) {
        if (!ConfigModel.getScanCode(this)) {
            return;
        }
        mUIHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().postSticky(new ScanCodeEvent(visibility));
            }
        }, delayTime);
    }

    public void setDeviceDialogShow(Context context) {
        Intent intent = new Intent();
        intent.setAction(DeviceSelDialog.ACTION_SHOW_DEVICE);
        Bundle bundle = new Bundle();
        bundle.putBoolean("visibility", true);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("key Down", "" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                Log.d("key Down", "home");
                return true;
            case KeyEvent.KEYCODE_BACK:
                if (!TapcApp.getInstance().isNextPage() && !TapcApp.getInstance().isStart()
                        && !TapcApp.getInstance().getSportsEngin().isRunning()
                        && UserManageModel.getInstance().isLogined()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setIcon(android.R.drawable.ic_menu_info_details).setTitle(getString(R.string.user_logout))
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    backLogin();
                                }
                            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    onPre(null);
                }
                Log.d("key Down", "back");
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnTouch(R.id.system_settings)
    private boolean clickLogo(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (System.currentTimeMillis() - mClickTime > 3000) {
                mClickCount = 0;
            }
            mClickTime = System.currentTimeMillis();

            if (++mClickCount >= SETTING_COUNT) {
                mClickCount = 0;
                if (!TapcApp.getInstance().isStart && !Config.HAS_SCAN_QR) {
                    startTapcActivity(this, SettingActivity.class);
                }
            }
        }
        return true;
    }

    public boolean isInGoalActivity() {
        if (!TapcApp.isApplicationBroughtToBackground(MainActivity.this)) {
            ActivityManager am = (ActivityManager) MainActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName app = am.getRunningTasks(1).get(0).topActivity;
            if (app.getClassName().equals("com.tapc.platform.activity.GoalActivity")) {
                return true;
            }
        }
        return false;
    }

    public double getBikeSpeed() {
        return mBikeSpeed;
    }

    public void setBikeSpeed(double bikeSpeed) {
        this.mBikeSpeed = bikeSpeed;
    }

    public void checkBikeSpeed() {
        if (TapcApp.getInstance().getSportsEngin().isRunning()) {
            int rpmSpeed = TapcApp.getInstance().controller.getRpmSpeed();
            if (getBikeSpeed() <= 0) {
                if (rpmSpeed <= 0) {
                    if (!TapcApp.getInstance().getSportsEngin().isPause()) {
                        pauseRun();
                    }
                } else {
                    if (TapcApp.getInstance().getSportsEngin().isPause()) {
                        restartRun();
                    }
                }
            }
        }
    }

    public void setWifiBtnVisibility(final boolean visibility) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (visibility) {
                    mWifiBtn.setVisibility(View.VISIBLE);
//                    mDateTimeBtn.setVisibility(View.VISIBLE);
                } else {
                    mWifiBtn.setVisibility(View.GONE);
//                    mDateTimeBtn.setVisibility(View.GONE);
                }
            }
        });
    }

    public void backLogin() {
        UserManageModel.getInstance().logout();
        TapcApp.getInstance().setDeviceRunStatus(DeviceRunStatus.STOPED);
        mUIHandler.sendEmptyMessageDelayed(MessageType.MSG_UI_SHOW_QR_DIALOG, 0);
    }
}
