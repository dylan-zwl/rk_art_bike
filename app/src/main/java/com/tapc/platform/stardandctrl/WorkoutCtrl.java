package com.tapc.platform.stardandctrl;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnLongClick;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.LoadCtlSetAcitvity;
import com.tapc.platform.activity.MainActivity.CTRLType;
import com.tapc.platform.activity.WorkoutCtlSetActivity;
import com.tapc.platform.utils.SysUtils;

import java.util.Timer;
import java.util.TimerTask;

public class WorkoutCtrl extends LinearLayout implements View.OnTouchListener {
    private static final int ADD_TOUCH = 0;
    private static final int SUB_TOUCH = 1;
    private static final int SIMULATION = 2;

    private CTRLType mType = CTRLType.INCLINE;

    private float mStep = 1.0f;
    @ViewInject(R.id.Sub)
    private Button mSub;

    @ViewInject(R.id.Add)
    private Button mAdd;

    @ViewInject(R.id.seekbar)
    private WorkoutSeekBar mSeekBar;

    @ViewInject(R.id.min_max)
    private WorkoutValue mValue;

    @ViewInject(R.id.show_value_text)
    private TextView mValueText;

    private Context mContext;
    private boolean isLongTouch = true;
    private int delayTime = 150;
    private float mMinValue = 0;
    private float mMaxValue = 0;
    private double mNowValue = 0;
    private double mSimulation = 0;
    private Timer mTimer;
    private int mChangeDelayTime = 0;

    public WorkoutCtrl(Context context) {
        this(context, null);
    }

    public WorkoutCtrl(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.workout_ctrl, this);
        ViewUtils.inject(view);

        mAdd.setOnTouchListener(this);
        mSub.setOnTouchListener(this);
    }

    public void SetRange(float min, float max, float step, CTRLType type) {
        mType = type;
        mStep = step;
        mMinValue = min;
        mMaxValue = max;
        setShowValue(0.0);
        if (mType == CTRLType.INCLINE) {
            mChangeDelayTime = 400;
        } else {
            mChangeDelayTime = 80;
        }
    }

    private void subValue() {
        if (mSub.isClickable()) {
            if (mTimer != null) {
                mNowValue = mSimulation;
            }
            cancelTimer();
            double value = mNowValue;
            value -= mStep;
            value = SysUtils.formatDouble(value);
            if (value > mMinValue) {
                if (mType == CTRLType.INCLINE) {
                    mValueText.setText(String.format("%.0f", value));
                } else {
                    mValueText.setText(String.format("%.1f", value));
                }
                TapcApp.getInstance().setValue(mType, value);
            } else {
                if (mType == CTRLType.INCLINE) {
                    mValueText.setText(String.format("%.0f", mMinValue));
                } else {
                    mValueText.setText(String.format("%.1f", mMinValue));
                }
                TapcApp.getInstance().setValue(mType, mMinValue);
                value = SysUtils.formatDouble(mMinValue);
            }
            mNowValue = value;
            mSimulation = value;
        }
    }

    private void addValue() {
        if (mAdd.isClickable()) {
            if (mTimer != null) {
                mNowValue = mSimulation;
            }
            cancelTimer();
            double value = mNowValue;
            value += mStep;
            value = SysUtils.formatDouble(value);
            if (value < mMaxValue) {
                if (mType == CTRLType.INCLINE) {
                    mValueText.setText(String.format("%.0f", value));
                } else {
                    mValueText.setText(String.format("%.1f", value));
                }
                TapcApp.getInstance().setValue(mType, value);
            } else {
                if (mType == CTRLType.INCLINE) {
                    mValueText.setText(String.format("%.0f", mMaxValue));
                } else {
                    mValueText.setText(String.format("%.1f", mMaxValue));
                }
                TapcApp.getInstance().setValue(mType, mMaxValue);
                value = SysUtils.formatDouble(mMaxValue);
            }
            mNowValue = value;
            mSimulation = value;
        }
    }

    public double getCtlValue() {
        return mNowValue;
    }

    public double getSimulationValue() {
        return mSimulation;
    }

    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void startTimer() {
        cancelTimer();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(SIMULATION);
            }
        }, mChangeDelayTime, mChangeDelayTime);
    }

    public void stop() {
        mSimulation = mNowValue = 0;
        TapcApp.getInstance().setValue(mType, mNowValue);
    }

    public void setCtlValue(double value) {
        if (TapcApp.getInstance().getSportsEngin().isRunning()) {
            value = SysUtils.formatDouble(value);
            if (value >= mMinValue && value <= mMaxValue) {
                if (TapcApp.getInstance().isSimulation) {
                    if (mNowValue != value) {
                        if (mTimer == null) {
                            mSimulation = mNowValue;
                        }
                    }
                    startTimer();
                    TapcApp.getInstance().setValue(mType, value);
                    mNowValue = value;
                } else {
                    if (mType == CTRLType.INCLINE) {
                        mValueText.setText(String.format("%.0f", value));
                    } else {
                        mValueText.setText(String.format("%.1f", value));
                    }
                    TapcApp.getInstance().setValue(mType, value);
                    mNowValue = value;
                }
            }
        } else {
            mSimulation = mNowValue;
            if (mType == CTRLType.INCLINE) {
                mValueText.setText(String.format("%.0f", mNowValue));
            } else {
                mValueText.setText(String.format("%.1f", mNowValue));
            }
        }
    }

    @OnClick(R.id.show_value_text)
    protected void valueClick(View v) {
        if (mType == CTRLType.INCLINE) {
            if (TapcApp.getInstance().isTestOpen) {
                LoadCtlSetAcitvity.launch(mContext, WorkoutCtlSetActivity.INCLINE, mNowValue);
            } else {
                WorkoutCtlSetActivity.launch(mContext, WorkoutCtlSetActivity.INCLINE, mNowValue);
            }
        } else {
            WorkoutCtlSetActivity.launch(mContext, WorkoutCtlSetActivity.SPEED, mNowValue);
        }
    }

    @OnClick(R.id.Sub)
    public void subClick(View v) {
        subValue();
    }

    @OnClick(R.id.Add)
    public void addClick(View v) {
        addValue();
    }

    @OnLongClick(R.id.Sub)
    protected boolean subLongClick(View v) {
        isLongTouch = true;
        mHandler.sendMessageDelayed(mHandler.obtainMessage(SUB_TOUCH), delayTime);
        return true;
    }

    @OnLongClick(R.id.Add)
    protected boolean addLongClick(View v) {
        isLongTouch = true;
        mHandler.sendMessageDelayed(mHandler.obtainMessage(ADD_TOUCH), delayTime);
        return true;
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            isLongTouch = false;
        }
        return false;
    }

    public void setButtonClickable(boolean clickable) {
        mAdd.setClickable(clickable);
        mSub.setClickable(clickable);
    }

    public void setShowValue(double value) {
        value = SysUtils.formatDouble(value);
        if (mType == CTRLType.INCLINE) {
            mValueText.setText(String.format("%.0f", value));
        } else {
            mValueText.setText(String.format("%.1f", value));
        }
        mNowValue = value;
    }

    public void setShowValue(double value, boolean flag) {
        value = SysUtils.formatDouble(value);
        if (flag) {
            if (mNowValue != value) {
                mSimulation = mNowValue;
            }
            startTimer();
            mNowValue = value;
        } else {
            if (mType == CTRLType.INCLINE) {
                mValueText.setText(String.format("%.0f", value));
            } else {
                mValueText.setText(String.format("%.1f", value));
            }
            mNowValue = value;
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADD_TOUCH:
                    if (isLongTouch) {
                        addValue();
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(ADD_TOUCH), delayTime);
                    }
                    break;
                case SUB_TOUCH:
                    if (isLongTouch) {
                        subValue();
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(SUB_TOUCH), delayTime);
                    }
                    break;
                case SIMULATION:
                    if (mSimulation > mNowValue) {
                        mSimulation = mSimulation - mStep;
                        if (mSimulation < mNowValue) {
                            mSimulation = mNowValue;
                        }
                    } else if (mSimulation < mNowValue) {
                        mSimulation = mSimulation + mStep;
                        if (mSimulation > mNowValue) {
                            mSimulation = mNowValue;
                        }
                    } else {
                        mSimulation = mNowValue;
                        cancelTimer();
                    }
                    mSimulation = SysUtils.formatDouble(mSimulation);
                    if (mType == CTRLType.INCLINE) {
                        mValueText.setText(String.format("%.0f", mSimulation));
                    } else {
                        mValueText.setText(String.format("%.1f", mSimulation));
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void setValueCtlVisibility(int visibility) {
        mAdd.setVisibility(visibility);
        mSub.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            mValueText.setClickable(true);
        } else {
            mValueText.setClickable(false);
        }
    }
}
