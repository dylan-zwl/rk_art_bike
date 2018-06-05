package com.tapc.platform.activity;

import java.io.InputStream;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.utils.SysUtils;

import android.os.Bundle;
import android.widget.TextView;

public class HelpActivity extends BaseActivity {
	@ViewInject(R.id.help_txt)
	private TextView helpText;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_help);
		ViewUtils.inject(this);

		String language = Config.LANGUAGE;
		InputStream inputStream = null;
		if (language.equals("0")) {
			inputStream = getResources().openRawResource(R.raw.help_en);
		} else if (language.equals("1")) {
		} else if (language.equals("2")) {
			inputStream = getResources().openRawResource(R.raw.help);
		}
		if (inputStream != null) {
			String txt = SysUtils.getString(inputStream);
			helpText.setText(txt);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
}
