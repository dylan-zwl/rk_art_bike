package com.tapc.platform.activity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.platform.R;
import com.tapc.platform.Config;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.utils.PreferenceHelper;
import com.tapc.platform.workout.MessageType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class LanguageAcivity extends BaseActivity {
	@ViewInject(R.id.chinese)
	RadioButton mButtonChinese;
	@ViewInject(R.id.english)
	RadioButton mButtonEnglish;
	@ViewInject(R.id.language_select)
	RadioGroup mRadioGroupLanguage;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_language);
		ViewUtils.inject(this);
		if (Config.LANGUAGE.equals("0")) {
			mButtonEnglish.setChecked(true);
			mButtonChinese.setChecked(false);
		} else if (Config.LANGUAGE.equals("2")) {
			mButtonEnglish.setChecked(false);
			mButtonChinese.setChecked(true);
		}

		mRadioGroupLanguage
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (checkedId == mButtonChinese.getId()) {
							TapcApp.getInstance().switchLanguage("2");
							Config.LANGUAGE = "2";
						} else if (checkedId == mButtonEnglish.getId()) {
							TapcApp.getInstance().switchLanguage("0");
							Config.LANGUAGE = "0";
						}
						PreferenceHelper.write(LanguageAcivity.this,
								Config.SETTING_CONFIG, "admin_language",
								Config.LANGUAGE);
						TapcApp.getInstance().sendUIMessage(
								MessageType.MSG_UI_MAIN_LANGUAGE);
					}
				});
	}
	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
}
