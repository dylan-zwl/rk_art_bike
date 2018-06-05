package com.tapc.platform.witget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tapc.platform.R;

public class CountdownDialog extends RelativeLayout {
	@ViewInject(R.id.countdown_text)
	private TextView mNum_text;

	public CountdownDialog(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.countdown_dialog, this, true);
		ViewUtils.inject(this);
	}

	public void setNumShow(String num) {
		mNum_text.setText(num);
		mNum_text.setVisibility(View.VISIBLE);
	}
}
