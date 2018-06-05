package com.tapc.platform.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.android.controller.MachineController;
import com.tapc.android.data.WorkoutInfo;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.model.common.UserManageModel;
import com.tapc.platform.model.healthcat.DeviceRunStatus;
import com.tapc.platform.model.healthcat.bike.BikeData;
import com.tapc.platform.sportsrunctrl.SportsEnginImpl;
import com.tapc.platform.sql.SportRecordItem;

import java.util.Timer;
import java.util.TimerTask;

public class SportResultActivity extends BaseActivity {
    @ViewInject(R.id.result_distance)
    private TextView mTextDistance;
    @ViewInject(R.id.result_time)
    private TextView mTextTime;
    @ViewInject(R.id.result_speed)
    private TextView mTextSpeed;
    @ViewInject(R.id.result_calorie)
    private TextView mTextCalorie;
    private Timer mTimer;
    private Handler mHandler;
    private SportsEnginImpl mEnginImpl;
    private WorkoutInfo mWorkoutInfo;
    private String mShowType;
    private SportRecordItem mSportRecordItem;
    private double mSteps = 0;
    private Activity mActivity;

    public static void launch(Context c, String type) {
        Intent i = new Intent(c, SportResultActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("type", type);
        c.startActivity(i);
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_sportresult);
        ViewUtils.inject(this);
        mActivity = this;

        mShowType = this.getIntent().getExtras().getString("type");
        if (mShowType == null) {
            mShowType = "";
        }

        mEnginImpl = (SportsEnginImpl) TapcApp.getInstance().getSportsEngin();
        if (mEnginImpl.mWorkouting != null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    mWorkoutInfo = mEnginImpl.mWorkouting.getWorkoutInfo();
                    if (mWorkoutInfo != null) {
                        mTextDistance.setText(String.format("%.2f", mWorkoutInfo.getDistance()));
                        mTextTime.setText(getLongNumFormat(mWorkoutInfo.getTime()));
                        mTextSpeed.setText(String.format("%.1f", mWorkoutInfo.getDistance() / mWorkoutInfo.getTime()
                                * 3600));
                        mTextCalorie.setText(String.format("%.2f", mWorkoutInfo.getCalorie()));
                        mSteps = mWorkoutInfo.getTotalPace();
                        if (mShowType.equals("stoprun_show")) {
                            updateScanCodeStatus(mWorkoutInfo);
                        }
                    } else {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(), 500);
                    }
                }
            };
            mHandler.sendMessageDelayed(mHandler.obtainMessage(), 500);
        } else {
            mTextDistance.setText("0");
            mTextTime.setText("0");
            mTextSpeed.setText("0");
            mTextCalorie.setText("0");
        }
        TimerTask task = new TimerTask() {
            public void run() {
                finish();
            }
        };
        mTimer = new Timer();
        mTimer.schedule(task, 10 * 1000);
    }

    public static String getLongNumFormat(long mLong) {
        String str = String.format("%02d:%02d:%02d",
                mLong / 3600,
                (mLong - mLong / 3600 * 60) / 60,
                mLong % 60);
        return str;
    }

    private void updateScanCodeStatus(WorkoutInfo workoutInfo) {
        BikeData data = TapcApp.getInstance().scanCodeData;
        data.setRunTime((int) workoutInfo.getTime());
        data.setDistance(workoutInfo.getResistance() * 1000);
        data.setCalorie((float) workoutInfo.getCalorie());
        data.setResistance(workoutInfo.getResistance());
        data.setRounds(MachineController.getInstance().getRounds());

        TapcApp.getInstance().setDeviceRunStatus(DeviceRunStatus.STOPED);
        UserManageModel.getInstance().logout();
    }


    @OnClick(R.id.sports_record)
    protected void recordOnClick(View v) {
        Intent intent = new Intent();
        intent.setClass(SportResultActivity.this, RecordActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
