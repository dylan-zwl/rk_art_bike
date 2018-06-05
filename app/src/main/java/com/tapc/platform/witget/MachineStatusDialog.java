package com.tapc.platform.witget;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;

public class MachineStatusDialog extends RelativeLayout {
	@ViewInject(R.id.errormessages)
	private TextView mErrorMessage;
	@ViewInject(R.id.safekey)
	private ImageView safekey;
	@ViewInject(R.id.error_lifter_rl)
	private RelativeLayout mErrorLifterRl;

	private Context mContext;
	private Timer mTimer;

	private boolean mLeftBtnClick = false;
	boolean mManuallyCancel = false;

	public MachineStatusDialog(Context context) {
		super(context);
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.machine_status_dialog, this, true);
		ViewUtils.inject(this);
	}

	public void setErrorLifterRlShow() {
		setManuallyStatus(false);
		mErrorLifterRl.setVisibility(View.VISIBLE);
	}

	public void setErrorLifterRlHide() {
		mErrorLifterRl.setVisibility(View.GONE);
	}

	public void setErrorTextShow(int resId) {
		if (safekey.isShown()) {
			safekey.setVisibility(View.GONE);
		}
		mManuallyCancel = false;
		mErrorMessage.setVisibility(View.VISIBLE);
		mErrorMessage.setText(mContext.getString(resId));
	}

	public void setErrorTextShow(String str) {
		if (safekey.isShown()) {
			safekey.setVisibility(View.GONE);
		}
		mManuallyCancel = false;
		mErrorMessage.setVisibility(View.VISIBLE);
		mErrorMessage.setText(str);
	}

	public void setErrorTextHide() {
		mErrorMessage.setText("");
		TapcApp.getInstance().setStart(false);
		mErrorMessage.setVisibility(View.GONE);
	}

	public void setSafeKeyVisibility(boolean flag) {
		if (flag) {
			if (!safekey.isShown()) {
				safekey.setVisibility(View.VISIBLE);
			}
		} else {
			if (safekey.isShown()) {
				safekey.setVisibility(View.GONE);
			}
		}
	}

	public void setManuallyStatus(boolean flag) {
		mManuallyCancel = flag;
	}

	public boolean getManuallyStatus() {
		return mManuallyCancel;
	}

	@OnClick(R.id.error_cancel_show)
	protected void errorCancelShow(View v) {
		if (TapcApp.getInstance().noShowNoCtlError) {
			mErrorLifterRl.setVisibility(View.GONE);
			manuallyCancelShow();
		}
	}

	@OnClick(R.id.noShowError_l)
	protected void lBtnOnClick(View v) {
		if (!mLeftBtnClick) {
			mLeftBtnClick = true;
			if (mTimer == null) {
				mTimer = new Timer();
				mTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						if (mLeftBtnClick) {
							mLeftBtnClick = false;
							cencelTimer();
						}
					}
				}, 1500);
			}
		}
	}

	@OnClick(R.id.noShowError_r)
	protected void rBtnOnClick(View v) {
		if (mLeftBtnClick) {
			mLeftBtnClick = false;
			cencelTimer();
			if (TapcApp.getInstance().noShowNoCtlError) {
				manuallyCancelShow();
			}
		}
	}

	private void manuallyCancelShow() {
		TapcApp.getInstance().setStart(false);
		mManuallyCancel = true;
		this.setVisibility(View.GONE);
		if (TapcApp.getInstance().getSafeKeyStatus()) {
			this.setVisibility(View.VISIBLE);
			setSafeKeyVisibility(true);
			setErrorTextHide();
		}
	}

	private void cencelTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}
}
