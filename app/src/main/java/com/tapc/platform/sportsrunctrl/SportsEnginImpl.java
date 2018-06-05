package com.tapc.platform.sportsrunctrl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.tapc.android.data.Enum.ProgramType;
import com.tapc.android.data.ProgramSetting;
import com.tapc.android.data.SystemSettingsHelper;
import com.tapc.android.data.Workout;
import com.tapc.android.workouting.Workouting;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.MainActivity.CTRLType;
import com.tapc.platform.activity.MainActivity.FinishReceiver;
import com.tapc.platform.entity.FitnessSetAllEntity;
import com.tapc.platform.media.PlayEntity;
import com.tapc.platform.workout.WorkoutListener;

public class SportsEnginImpl implements SportsEngin {
    public Workouting mWorkouting = null;
    public PlayEntity playEntity;
    public ProgramSetting mProgramSetting;
    public FitnessSetAllEntity longEntity;

    private WorkoutListener mWorkoutListener;
    private Handler mFitnessHandle = new Handler();
    private boolean isRunning = false;
    private boolean isPause = false;
    private Context mFinishContext;

    @Override
    public void start() {
        startRunning();
    }

    @Override
    public void stop() {
        stopRunning();
    }

    @Override
    public void pause() {
        PauseRunning();
    }

    @Override
    public void restart() {
        RestartRunning();
    }

    @Override
    public void openScene(PlayEntity entity) {
        playEntity = entity;
    }

    @Override
    public PlayEntity getScene() {
        return playEntity;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    private void startRunning() {
        isRunning = true;
        ProgramSetting programSetting = new ProgramSetting();
        if (longEntity != null) {
            ProgramType programType = longEntity.getProgramType();
            switch (programType) {
                case DISTANCE: {
                    programSetting.setProgramType(programType);
                    programSetting.SetDistance(longEntity.getDistance());
                    programSetting.setWeight(longEntity.getWeight());
                    programSetting.setSpeed(longEntity.getSpeed());
                    programSetting.setIncline(longEntity.getIncline());
                }
                break;
                case TIME: {
                    programSetting.setProgramType(programType);
                    programSetting.setMinutes(longEntity.getMinutes());
                    programSetting.setWeight(longEntity.getWeight());
                    programSetting.setSpeed(longEntity.getSpeed());
                    programSetting.setIncline(longEntity.getIncline());
                }
                break;
                case CALORIE: {
                    programSetting.setProgramType(programType);
                    programSetting.setWeight(longEntity.getWeight());
                    programSetting.SetCalorie(longEntity.getCalorie());
                    programSetting.setSpeed(longEntity.getSpeed());
                    programSetting.setIncline(longEntity.getIncline());
                }
                break;
                case TAPC_PROG: {
                    programSetting.setProgramType(programType);
                    programSetting.setMinutes(longEntity.getProgramTime());
                    programSetting.setWeight(longEntity.getWeight());
                    programSetting.setLevel(longEntity.getLevel());
                    programSetting.setSpeed(longEntity.getSpeed());
                    programSetting.setIncline(longEntity.getIncline());
                }
                break;
                case MANUAL: {
                    programSetting.setProgramType(programType);
                    if (TapcApp.getInstance().isTestOpen) {
                        programSetting.setMinutes(100000);
                    } else {
                        programSetting.setMinutes(120);
                    }
                    programSetting.setWeight(longEntity.getWeight());
                    programSetting.setLevel(longEntity.getLevel());
                    programSetting.setSpeed(longEntity.getSpeed());
                    programSetting.setIncline(longEntity.getIncline());
                }
                break;
                default:
                    break;
            }
        } else {
            programSetting.setProgramType(ProgramType.MANUAL);
            if (TapcApp.getInstance().isTestOpen) {
                programSetting.setMinutes(100000);
            } else {
                programSetting.setMinutes(120);
            }
            programSetting.setWeight(SystemSettingsHelper.WEIGHT);
            programSetting.setSpeed(SystemSettingsHelper.DEFAULT_SPEED);
            programSetting.setIncline(SystemSettingsHelper.DEFAULT_INCLINE);
        }
        if (mWorkouting != null) {
            mWorkouting = null;
        }
        mWorkouting = new Workouting(programSetting);
        mWorkouting.setBroadcast(mFinishContext);
        mWorkouting.onStart();
        mFitnessHandle.removeCallbacks(mFitnessRunnable);
        mFitnessHandle.post(mFitnessRunnable);
    }

    private void stopRunning() {
        isRunning = false;
        if (mWorkouting != null) {
            mWorkouting.onStop();
        }
        longEntity = null;
        mFitnessHandle.removeCallbacks(mFitnessRunnable);
    }

    private void PauseRunning() {
        setPause(true);
    }

    private void RestartRunning() {
        setPause(false);
    }

    private Runnable mFitnessRunnable = new Runnable() {

        @Override
        public void run() {
            if (mWorkouting != null && mWorkoutListener != null) {
                Workout workout = mWorkouting.getWorkout();
                mWorkoutListener.update(workout);
                TapcApp.getInstance().setWorkout(workout);
            }
            mFitnessHandle.postDelayed(mFitnessRunnable, 500);
        }
    };

    @Override
    public double subSpeed() {
        if (mWorkouting != null) {
            return mWorkouting.onRightKeypadSub();
        }
        return 0;
    }

    @Override
    public double addIncline() {
        if (mWorkouting != null) {
            return mWorkouting.onLeftKeypadAdd();
        }
        return 0;
    }

    @Override
    public double subIncline() {
        if (mWorkouting != null) {
            return mWorkouting.onLeftKeypadSub();
        }
        return 0;
    }

    @Override
    public double addSpeed() {
        if (mWorkouting != null) {
            return mWorkouting.onRightKeypadAdd();
        }
        return 0;
    }

    public double setValue(CTRLType type, double value) {
        if (type == CTRLType.INCLINE) {
            return setIncline(value);
        }
        if (type == CTRLType.SPEED) {
            return setSpeed(value);
        }
        return 0;
    }

    @Override
    public double setSpeed(double speed) {
        if (mWorkouting != null) {
            return mWorkouting.onRightKeypadEvent(speed);
        }
        return 0;
    }

    @Override
    public double setIncline(double inlcine) {
        if (mWorkouting != null) {
            return mWorkouting.onLeftKeypadEvent(inlcine);
        }
        return 0;
    }

    @Override
    public void setWorkoutListener(WorkoutListener listener) {
        mWorkoutListener = listener;
    }

    public void setBroadcast(Context context, FinishReceiver finishReceiver) {
        mFinishContext = context;
    }

    public class WorkoutFinishReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("WorkoutFinishReceiver", "onReceive");
            // mFinishContext.sendBroadcast(intent);
        }

    }

    @Override
    public boolean isPause() {
        return isPause;
    }

    @Override
    public void setPause(boolean flag) {
        if (mWorkouting != null) {
            isPause = flag;
            if (isPause) {
                mWorkouting.onPause();
            } else {
                mWorkouting.onResume();
            }
        }
    }

    @Override
    public void openFitness(FitnessSetAllEntity entity) {
        longEntity = entity;
    }

    @Override
    public FitnessSetAllEntity getFitness() {
        return longEntity;
    }

    @Override
    public void setCooldown() {
        if (mWorkouting != null) {
            mWorkouting.onCooldown();
        }
    }
}
