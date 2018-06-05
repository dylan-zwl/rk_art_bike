package com.tapc.platform.model.common;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/10/26.
 */

public class ClickModel {
    private Timer mTimer;
    private int mMaxClickNumbers;
    private int mCount;
    private int mOnceClickTimeOut;
    private Listener mListener;

    public ClickModel() {
        mCount = 0;
        mMaxClickNumbers = 5;
        mOnceClickTimeOut = 1000;
    }

    public interface Listener {
        void onClickCompleted();
    }

    public void setOnceClickTimeout(int timeMs) {
        mOnceClickTimeOut = timeMs;
    }

    public void setMaxClickNumbers(int times) {
        mMaxClickNumbers = times;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void click() {
        cancel();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mCount = 0;
            }
        }, mOnceClickTimeOut);
        mCount++;
        if (mCount >= mMaxClickNumbers) {
            reset();
            if (mListener != null) {
                mListener.onClickCompleted();
            }
        }
    }

    public void reset() {
        cancel();
        mCount = 0;
    }

    public void cancel() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public static class Builder {
        private ClickModel mClickModel;

        public Builder() {
            mClickModel = new ClickModel();
        }

        public Builder onceClickTimeout(int timeMs) {
            mClickModel.mOnceClickTimeOut = timeMs;
            return this;
        }

        public Builder maxClickNumbers(int times) {
            mClickModel.mMaxClickNumbers = times;
            return this;
        }

        public Builder listener(Listener listener) {
            mClickModel.mListener = listener;
            return this;
        }

        public ClickModel create() {
            return mClickModel;
        }
    }
}
