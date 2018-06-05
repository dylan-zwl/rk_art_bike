package com.tapc.platform.witget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tapc.platform.R;

public class MsgPromptDialog extends RelativeLayout {
	@ViewInject(R.id.messages)
	private TextView mMessage;
	private Context mContext;

	public MsgPromptDialog(Context context) {
		super(context);
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.msg_prompt, this, true);
		ViewUtils.inject(this);
	}
	public void setMsgShow(String msg) {
		mMessage.setText(msg);
		if (mMessage.isShown() == false) {
			mMessage.setVisibility(VISIBLE);
		}
	}

	public void setMsgVisivility(boolean flag) {
		if (flag) {
			this.setVisibility(VISIBLE);
		} else {
			this.setVisibility(GONE);
			mMessage.setText("");
		}
	}
}
