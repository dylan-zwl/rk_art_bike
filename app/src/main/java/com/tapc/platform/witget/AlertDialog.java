package com.tapc.platform.witget;

import com.tapc.platform.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class AlertDialog extends Dialog implements OnClickListener {

	public interface OnClickListener {
		public void onClick();
	}

	private Context context;
	private TextView tv_title, tv_message;
	private Button btn_cancel, btn_ok;
	private String mTitle, mMessage;

	private OnClickListener mOkListener;
	private OnClickListener mCancelListener;


	public AlertDialog(Context context) {
		super(context, R.style.AlertDialogStyle);
		this.context = context;
		this.mMessage = "";
		this.mTitle = "";
	}

	public AlertDialog(Context context, String message,
			OnClickListener okListener, OnClickListener cancelListener) {
		this(context, "提示", message, okListener, cancelListener);
	}

	public AlertDialog(Context context, String title, String message) {
		this(context, title, message, null, null);
	}

	public AlertDialog(Context context, String title, String message,
			OnClickListener okListener, OnClickListener cancelListener) {
		super(context, R.style.AlertDialogStyle);
		this.context = context;
		this.mTitle = title;
		this.mMessage = message;
		this.mOkListener = okListener;
		this.mCancelListener = cancelListener;
	}

	@Override
	public void onClick(View v) {
		dismiss();
		switch (v.getId()) {
		case R.id.btn_ok:
			if (mOkListener == null)
				return;
			mOkListener.onClick();
			break;
		case R.id.btn_cancel:
			if (mCancelListener == null)
				return;
			mCancelListener.onClick();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_alert);

		setCanceledOnTouchOutside(false);// Touch Other Dismiss

		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_message = (TextView) findViewById(R.id.tv_message);
		if (TextUtils.isEmpty(mTitle))
			tv_title.setVisibility(View.GONE);
		if (TextUtils.isEmpty(mMessage))
			tv_message.setVisibility(View.GONE);
		tv_title.setText(mTitle);
		tv_message.setText(mMessage);

		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_ok.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		if (mCancelListener == null) {
			btn_cancel.setVisibility(View.GONE);
		}
		if (mOkListener == null) {
			btn_ok.setVisibility(View.GONE);
		}

		// Set Dialog Gravity
		Window dialogWindow = getWindow();
		dialogWindow.setGravity(Gravity.CENTER);

		// Set Dialog Width
		WindowManager.LayoutParams p = dialogWindow.getAttributes();
		// p.width = WindowManager.LayoutParams.MATCH_PARENT;

		dialogWindow.setAttributes(p);
	}

	public AlertDialog setTitle(String title) {
		this.mTitle = title;
		return this;
	}

	public AlertDialog setMessage(String message) {
		this.mMessage = message;
		return this;
	}

	public AlertDialog setOnOkClickListener(OnClickListener listener) {
		this.mOkListener = listener;
		return this;
	}

	public void setOnCancelListener(OnClickListener listener) {
		this.mCancelListener = listener;
	}

}
