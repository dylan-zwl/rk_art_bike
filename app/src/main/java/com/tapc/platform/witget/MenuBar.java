package com.tapc.platform.witget;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.android.controller.MachineController;
import com.tapc.android.data.Workout;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.entity.BikeCtlType;
import com.tapc.platform.model.common.UserManageModel;
import com.tapc.platform.model.healthcat.DeviceRunStatus;
import com.tapc.platform.model.healthcat.bike.BikeData;
import com.tapc.platform.stardandctrl.WorkoutCtrl;
import com.tapc.platform.stardandctrl.WorkoutOSD;
import com.tapc.platform.utils.SysUtils;
import com.tapc.platform.witget.scancode.ShowTimeEvent;
import com.tapc.platform.witget.scancode.WifiStatusEvent;
import com.tapc.platform.workout.WorkoutListener;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MenuBar extends LinearLayout {
    @ViewInject(R.id.fan)
    private ImageButton mFan;
    @ViewInject(R.id.volume)
    private ImageButton mVolume;
    @ViewInject(R.id.home)
    private ImageButton mHome;
    @ViewInject(R.id.back)
    private ImageButton mBack;
    @ViewInject(R.id.osdIncline)
    private WorkoutOSD mIncline;
    @ViewInject(R.id.osdSpeed)
    private WorkoutOSD mSpeed;
    @ViewInject(R.id.osdTime)
    private WorkoutOSD mTime;
    @ViewInject(R.id.osdHeart)
    private WorkoutOSD mHeart;
    @ViewInject(R.id.osdDateTime)
    private WorkoutOSD mDateTime;
    @ViewInject(R.id.osdDistance)
    private WorkoutOSD mDistance;
    @ViewInject(R.id.osdBikeSpeed)
    private WorkoutOSD mBikeSpeed;
    @ViewInject(R.id.osdGoal)
    private TextView goalText;
    @ViewInject(R.id.osdProgress)
    private ProgressBar mTarget;
    @ViewInject(R.id.workoutOsd)
    private LinearLayout mLinearLayoutOSD;
    @ViewInject(R.id.connect_status_LinearLayout)
    private LinearLayout mConnectStatuslny;
    @ViewInject(R.id.wifi_status)
    private ImageView mWifiStatus;
    @ViewInject(R.id.bluetooth_status)
    private ImageView mBluetoothStatus;

    private Handler mHandler;
    private Context mContext;

    private boolean isShowConnectStaus = true;
    private boolean mOSD = false;
    private int mFanLevel = 0;
    private int mScanCodeTimeNums = 0;
    private ShowTimeEvent mShowTimeEvent;
    private BikeData mData;

    public MenuBar(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.menu_bar, this, true);
        ViewUtils.inject(this);
        this.mContext = context;
        initView();
        mData = TapcApp.getInstance().scanCodeData;
    }

    public WorkoutListener mWorkoutListener = new WorkoutListener() {

        @Override
        public void update(Workout workout) {
            if (TapcApp.getInstance().isSimulation) {
                mIncline.setBottomString(String.format("%.0f", TapcApp.getInstance().mainActivity.getInclineCtrl()
                        .getSimulationValue()));
                mSpeed.setBottomString(String.format("%.1f", TapcApp.getInstance().mainActivity.getSpeedCtrl()
                        .getSimulationValue()));
            } else {
                // incline
                double incline = workout.getIncline();
                mIncline.setBottomString(String.format("%.0f", incline));
                // speed
                double speed = workout.getSpeed();
                mSpeed.setBottomString(String.format("%.1f", speed));
            }
            // time
            long time = workout.getTotalTime();
            mTime.setBottomString(String.format("%02d:%02d", time / 60, time % 60));

            // heart
            int heart = (int) workout.getHeartRate();
            mHeart.setBottomString(String.valueOf(heart));

            // distance
            double distance = workout.getTotalDistance();
//            mDistance.setBottomString(String.format("%.2f", distance));
            mDistance.setBottomString("" + MachineController.getInstance().getRounds());


            WorkoutCtrl speedCtrl = TapcApp.getInstance().mainActivity.getSpeedCtrl();
            WorkoutCtrl inclineCtrl = TapcApp.getInstance().mainActivity.getInclineCtrl();
            if (workout.getSpeed() != speedCtrl.getCtlValue()) {
                speedCtrl.setShowValue(workout.getSpeed(), TapcApp.getInstance().isSimulation);
            }
            if (workout.getIncline() != inclineCtrl.getCtlValue()) {
                inclineCtrl.setShowValue(workout.getIncline(), TapcApp.getInstance().isSimulation);
            }

            if (Config.sBikeCtlType == BikeCtlType.WATT) {
                TapcApp.getInstance().mainActivity.getInclineCtrl().setShowValue(
                        TapcApp.getInstance().controller.getIncline());
            }

            int speedType = TapcApp.getInstance().mainActivity.getBikeSpeedType();
            if (speedType == 0) {
                mBikeSpeed.setTopString(getResources().getString(R.string.bike_speed));
                double bikeSpeed = workout.getBikeSpeed();
                mBikeSpeed.setBottomString(String.format("%.1f", bikeSpeed));
            } else {
                mBikeSpeed.setTopString(getResources().getString(R.string.bike_rpm));
                double bikeSpeedRpm = workout.getBikeRpmSpeed();
                mBikeSpeed.setBottomString(String.format("%.0f", bikeSpeedRpm));
            }
            TapcApp.getInstance().mainActivity.setBikeSpeed(workout.getBikeSpeed());

            if (Config.sBikeCtlType == BikeCtlType.LOAD
                    && workout.getSpeed() != TapcApp.getInstance().mainActivity.getSpeedCtrl().getCtlValue()) {
                TapcApp.getInstance().mainActivity.getSpeedCtrl().setShowValue(workout.getSpeed(),
                        TapcApp.getInstance().isSimulation);
            }
            if (Config.sBikeCtlType == BikeCtlType.WATT
                    && workout.getIncline() != TapcApp.getInstance().mainActivity.getInclineCtrl().getCtlValue()) {
                TapcApp.getInstance().mainActivity.getInclineCtrl().setShowValue(workout.getIncline(),
                        TapcApp.getInstance().isSimulation);
            }

            // com.tapc.android.data.Enum.WorkoutGoal goal =
            // workout.getWorkoutGoal();
            // double cur = workout.getGoal();
            // if (goal == goal.TIME) {
            // if (workout.getWorkoutStage() == WorkoutStage.WARMUP) {
            // // goalText.setText(R.string.warmUp_time);
            // } else if (workout.getWorkoutStage() == WorkoutStage.COOLDOWN) {
            // goalText.setText(R.string.relax_time);
            // } else {
            // goalText.setText(R.string.goal_sports);
            // }
            // int goaltime = (int) workout.getTargetTime();
            // if (goaltime != 0) {
            // mTarget.setMax(goaltime);
            // mTarget.setProgress((int) cur);
            // }
            // } else if (goal == goal.DISTANCE) {
            // goalText.setText(R.string.goal_sports);
            // double goaldistance = workout.getTargetDistance();
            // if (goaldistance != 0) {
            // mTarget.setMax((int) (goaldistance * 100));
            // mTarget.setProgress((int) (cur * 100));
            // }
            // } else if (goal == goal.CALORIE) {
            // goalText.setText(R.string.goal_sports);
            // double goalcalorie = workout.getTargetCalorie();
            // if (goalcalorie != 0) {
            // mTarget.setMax((int) (goalcalorie * 100));
            // mTarget.setProgress((int) (cur * 100));
            // }
            // }

            mData.setRunTime((int) time);
            mData.setDistance((int) (distance * 1000));
            mData.setCalorie((float) workout.getCalorie());
            mData.setResistance((int) workout.getIncline());
            mData.setSpeed((float) workout.getBikeSpeed());
            mData.setHeart((int) workout.getHeartRate());
            mData.setRounds(MachineController.getInstance().getRounds());

            TapcApp.getInstance().mainActivity.checkPerson(workout);
            TapcApp.getInstance().playIntervalSound();

            TapcApp.getInstance().mainActivity.updateGroupUserDTO(time, workout.getTotalDistance(),
                    workout.getTotalCalorie(), workout.getSpeed());
        }
    };

    @SuppressLint("HandlerLeak")
    private void initView() {
        mShowTimeEvent = new ShowTimeEvent("");
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HandlerType.UPDATE_TIME:
                        ContentResolver c = mContext.getContentResolver();
                        String strTimeFormat = android.provider.Settings.System.getString(c,
                                android.provider.Settings.System.TIME_12_24);
                        CharSequence sysTimeStr;
                        long sysTime = System.currentTimeMillis();
                        if (strTimeFormat != null) {
                            if (strTimeFormat.equals("24")) {
                                sysTimeStr = SysUtils.getDataTimeStr("HH:mm", sysTime);
                            } else {
                                sysTimeStr = SysUtils.getDataTimeStr("hh:mm", sysTime);
                            }
                        } else {
                            sysTimeStr = SysUtils.getDataTimeStr("hh:mm", sysTime);
                        }
                        String sysDateStr = SysUtils.getDataTimeStr("yyyy/MM/dd", sysTime);
                        mDateTime.setTopString(sysTimeStr.toString());
                        mDateTime.setBottomString(sysDateStr.toString());

                        mScanCodeTimeNums++;
                        if (mScanCodeTimeNums >= 5) {
                            mScanCodeTimeNums = 0;
                            Date date = new Date(sysTime);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 E  HH:mm", Locale.CHINA);
                            String time = format.format(date);
                            if (!TextUtils.isEmpty(time)) {
                                mShowTimeEvent.setTime(time);
                                EventBus.getDefault().post(mShowTimeEvent);
                            }
                        }

                        TapcApp.getInstance().mainActivity.checkBikeSpeed();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                noAction();
                mHandler.sendEmptyMessage(HandlerType.UPDATE_TIME);
            }
        };
        timer.schedule(timerTask, 1000, 1000);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showOSD(false);
            }
        }, 1000);
        if (Config.isConnected) {
            setWifiConnect(true);
        } else {
            setWifiConnect(false);
        }
    }

    public void setWifiConnect(boolean isConncet) {
        if (isConncet) {
            if (getConnectType() == ConnectivityManager.TYPE_ETHERNET) {
                mWifiStatus.setImageResource(R.drawable.ethernet_connect);
            } else {
                mWifiStatus.setImageResource(R.drawable.wifi_connect);
            }
            mWifiStatus.setVisibility(View.VISIBLE);
        } else {
            if (getConnectType() == ConnectivityManager.TYPE_ETHERNET) {
                mWifiStatus.setImageResource(R.drawable.ethernet_unconnect);
            } else {
                mWifiStatus.setImageResource(R.drawable.wifi_unconnect);
            }
            mWifiStatus.setVisibility(View.INVISIBLE);
        }
        EventBus.getDefault().postSticky(new WifiStatusEvent(isConncet, getConnectType()));
    }

    public int getConnectType() {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        if (activeInfo != null) {
            return activeInfo.getType();
        }
        return ConnectivityManager.TYPE_WIFI;
    }

    public void setBlueConnect(boolean isConncet) {
        if (isConncet) {
            mBluetoothStatus.setImageResource(R.drawable.blue_connect);
            mBluetoothStatus.setVisibility(View.VISIBLE);
        } else {
            mBluetoothStatus.setImageResource(R.drawable.blue_unconnect);
            mBluetoothStatus.setVisibility(View.INVISIBLE);
        }
    }

    public void setTimeDisplay(boolean isVisible) {
        if (isVisible) {
            mDateTime.setVisibility(View.VISIBLE);
        } else {
            mDateTime.setVisibility(View.GONE);
        }
    }

    private class HandlerType {
        public static final int UPDATE_TIME = 0;
    }

    public void setWorkoutListener(WorkoutListener listener) {
        mWorkoutListener = listener;
    }

    public void showOSD(boolean bShow) {
        mOSD = bShow;
        if (bShow) {
            TapcApp.getInstance().getSportsEngin().setWorkoutListener(mWorkoutListener);
            mBikeSpeed.setVisibility(View.VISIBLE);
            setTimeDisplay(false);
            mLinearLayoutOSD.setVisibility(View.VISIBLE);
            if (isShowConnectStaus) {
                mConnectStatuslny.setVisibility(View.INVISIBLE);
            }
        } else {
            TapcApp.getInstance().getSportsEngin()
                    .setWorkoutListener(TapcApp.getInstance().mainActivity.mWorkoutListener);
            mBikeSpeed.setVisibility(View.GONE);
            setTimeDisplay(true);
            mLinearLayoutOSD.setVisibility(View.INVISIBLE);
            if (isShowConnectStaus) {
                mConnectStatuslny.setVisibility(View.VISIBLE);
            }
        }
    }

    public boolean getOSD() {
        return mOSD;
    }

    private void noAction() {
        if (TapcApp.getInstance().hasErp) {
            if (!TapcApp.getInstance().isStart() && !TapcApp.getInstance().getSportsEngin().isPause()) {
                if (TapcApp.getInstance().isAppToBackground) {
                    if (TapcApp.isApplicationBroughtToBackground(mContext)) {
                        return;
                    }
                }
                if (!TapcApp.getInstance().isScreenOn) {
                    return;
                }
                TapcApp.getInstance().noNoActionCount++;
                // Log.d("noNoActionCount", "" +
                // TapcApp.getInstance().noNoActionCount);
                if (TapcApp.getInstance().noNoActionCount >= Config.NOACTION_DELAYTIME) {
                    TapcApp.getInstance().noNoActionCount = 0;
                    TapcApp.getInstance().enterERP();
                }
            }
        }

        if (UserManageModel.getInstance().isLogined() && !TapcApp.getInstance().isStart()) {
            if (TapcApp.getInstance().hasErp == false) {
                TapcApp.getInstance().noNoActionCount++;
            }
            if (TapcApp.getInstance().noNoActionCount >= (3 * 60)) {
                TapcApp.getInstance().noNoActionCount = 0;

                TapcApp.getInstance().setDeviceRunStatus(DeviceRunStatus.TIMEOUT);
                TapcApp.getInstance().menuBar.backHome();
                TapcApp.getInstance().mainActivity.backLogin();
//                TapcApp.getInstance().mainActivity.setUser(null);
            }
        }
    }

    @OnClick(R.id.volume)
    public void voiceOnClick(View v) {
        TapcApp.getInstance().openVolume();
    }

    @OnClick(R.id.fan)
    public void fanOnClick(View v) {
        if (mFanLevel == 0) {
            mFanLevel = 3;
            mFan.setBackgroundResource(R.drawable.fan4);
        } else if (mFanLevel > 0) {
            mFanLevel = 0;
            mFan.setBackgroundResource(R.drawable.fan1);
        }
        TapcApp.getInstance().controller.setFanLevel(mFanLevel);
    }

    @OnClick(R.id.back)
    public void backOnClick(View v) {
        TapcApp.getInstance().clearSportsEngin();
        if (TapcApp.getInstance().mainActivity.isInGoalActivity()) {
            backHome();
        } else {
            TapcApp.getInstance().keyboardEvent.backEvent();
        }
    }

    public void backHome() {
        // TapcApp.getInstance().keyboardEvent.homeEvent();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);// 注意
        intent.addCategory(Intent.CATEGORY_HOME);
        mContext.startActivity(intent);
    }

    @OnClick(R.id.home)
    public void homeOnClick(View v) {
        TapcApp.getInstance().clearSportsEngin();
        backHome();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        TapcApp.getInstance().noNoActionCount = 0;
        return super.onTouchEvent(event);
    }
}