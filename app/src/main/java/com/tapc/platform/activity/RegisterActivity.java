package com.tapc.platform.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.dto.net.Callback;
import com.tapc.platform.dto.response.RegisterResponse;
import com.tapc.platform.entity.UserInfor;
import com.tapc.platform.utils.PreferenceHelper;

@SuppressWarnings("unused")
public class RegisterActivity extends BaseActivity {
	@ViewInject(R.id.register_viewpage)
	private ViewPager mRegister_viewpage;
	@ViewInject(R.id.user_name_edit)
	private EditText mUserName;
	@ViewInject(R.id.user_password_edit)
	private EditText mPassword;
	@ViewInject(R.id.user_sure_password_edit)
	private EditText mRePassword;
	@ViewInject(R.id.user_weight_edit)
	private EditText mWeight;
	@ViewInject(R.id.user_nick_edit)
	private EditText mNick;
	@ViewInject(R.id.select_man)
	private Button mManSex;
	@ViewInject(R.id.select_woman)
	private Button mWoman;
	@ViewInject(R.id.user_nick_tx)
	private TextView mNickTx;

	private Activity mActivity;
	private ArrayList<View> mListViews;
	private UserInfor mUserInfor;

	private String username = "";
	private String sex = "";
	private String nickname = "";
	private String weight = "";
	private String pwd = "";
	private String repwd = "";
	private String email = "";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.register_viewpage);
		mListViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		mListViews.add(mInflater.inflate(R.layout.register_page1, null));
		mListViews.add(mInflater.inflate(R.layout.register_page2, null));

		mActivity = this;
		ViewUtils.inject(this);
		ViewUtils.inject(this, mListViews.get(0));
		ViewUtils.inject(this, mListViews.get(1));

		if (Config.SERVICE_ID == 1) {
			mNickTx.setText(getString(R.string.user_email));
		}
		// ���ViewPager�����������
		PagerAdapter mPagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return mListViews.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(mListViews.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(mListViews.get(position));
				return mListViews.get(position);
			}
		};

		mRegister_viewpage.setAdapter(mPagerAdapter);
		mRegister_viewpage.setCurrentItem(0);
		mRegister_viewpage.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});
		select_sex("man");
		mUserInfor = TapcApp.getInstance().userInfor;
	}

	@OnClick(R.id.register_nextbutton)
	protected void nextbuttonOnClick(View v) {
		mRegister_viewpage.setCurrentItem(1);
	}

	@OnClick(R.id.register_prebutton)
	protected void prebuttonOnClick(View v) {
		mRegister_viewpage.setCurrentItem(0);
	}

	private void select_sex(String sex) {
		if (sex.equals("man")) {
			mManSex.setBackgroundResource(R.drawable.sex_down);
			mWoman.setBackgroundResource(R.drawable.sex_up);
			sex = getString(R.string.user_sex_man);
		} else {
			mManSex.setBackgroundResource(R.drawable.sex_up);
			mWoman.setBackgroundResource(R.drawable.sex_down);
			sex = getString(R.string.user_sex_woman);
		}
	}

	@OnClick(R.id.select_man)
	protected void select_manOnClick(View v) {
		select_sex("man");
	}

	@OnClick(R.id.select_woman)
	protected void select_womanOnClick(View v) {
		select_sex("woman");
	}

	@OnClick(R.id.sure_register)
	protected void sure_registerOnClick(View v) {
		if (getVerify()) {
			if (Config.HAS_SERVICE) {
				postRegister(username, nickname, sex, weight, "180", email, pwd, repwd);
			} else {
				mUserInfor.setUid("0");
				mUserInfor.setUsername(username);
				mUserInfor.setNickname(nickname);
				mUserInfor.setPassword(pwd);
				saveUserData();
			}
		}
	}

	private boolean getVerify() {
		username = mUserName.getText().toString();
		pwd = mPassword.getText().toString();
		repwd = mRePassword.getText().toString();
		if (Config.SERVICE_ID == 1) {
			nickname = "";
			email = mNick.getText().toString();
		} else {
			nickname = mNick.getText().toString();
		}
		weight = mWeight.getText().toString();
		if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, getText(R.string.error_empty_user_id), Toast.LENGTH_SHORT).show();
			return false;
		} else if (username.length() < 2) {
			Toast.makeText(this, getText(R.string.error_user_size), Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, getString(R.string.error_empty_password), Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(repwd)) {
			Toast.makeText(this, getString(R.string.error_empty_sure_password), Toast.LENGTH_SHORT).show();
			return false;
		}

		if (Config.SERVICE_ID == 1) {
			if (TextUtils.isEmpty(email)) {
				Toast.makeText(this, getString(R.string.error_empty_email), Toast.LENGTH_SHORT).show();
				return false;
			} else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
				Toast.makeText(this, getString(R.string.error_format_email), Toast.LENGTH_SHORT).show();
				return false;
			}
			if (TextUtils.isEmpty(weight)) {
				Toast.makeText(this, getString(R.string.error_empty_weight), Toast.LENGTH_SHORT).show();
				return false;
			}
			if (Integer.valueOf(weight) < 0 || Integer.valueOf(weight) > 220) {
				Toast.makeText(this, getString(R.string.error_fifty_two_weight), Toast.LENGTH_SHORT).show();
				return false;
			}
		}

		if (TextUtils.isEmpty(nickname)) {
			nickname = username;
		}

		if (!pwd.equals(repwd)) {
			Toast.makeText(this, getString(R.string.error_not_equal_password), Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void saveUserData() {
		String username = mUserInfor.getUsername();
		Toast.makeText(mActivity, username + getString(R.string.toast_register_success), Toast.LENGTH_SHORT).show();
		if (Config.SERVICE_ID == 0 || !Config.HAS_SERVICE) {
			PreferenceHelper.write(mActivity, Config.SETTING_CONFIG, "user_infor",
					UserInfor.toGsonUserInfor(mUserInfor));
			TapcApp.getInstance().mainActivity.setLoginUserName(username);
			UserInfor userInfor = TapcApp.getInstance().userDataDao.getUserRecord(username);
			if (userInfor == null) {
				TapcApp.getInstance().userDataDao.insertItem(mUserInfor);
			}
		}
		returnMainUi();
	}

	/**
	 * 2015-4-1����9:32:38 Jason.liu Email 1946711081@qq.com TODO ע��
	 * 
	 * @param usernames
	 * @param nicknames
	 * @param genders
	 * @param heights
	 * @param ages
	 * @param emails
	 * @param pwds
	 * @param rePwds
	 */
	private void postRegister(String usernames, String nicknames, String genders, String heights, String ages,
			String emails, String pwds, String rePwds) {

		TapcApp.getInstance().getHttpClient()
				.register(usernames, nicknames, genders, heights, ages, emails, pwds, rePwds, new Callback() {

					@Override
					public void onSuccess(Object o) {
						if (o != null) {
							RegisterResponse register = (RegisterResponse) o;
							if (register.getStatus() == 200) {
								mUserInfor = new UserInfor();
								saveUserData();
							} else if (register.getStatus() == 201) {
								Toast.makeText(mActivity, getString(R.string.erro_the_input_data_is_wrong),
										Toast.LENGTH_SHORT).show();
							} else if (register.getStatus() == 401) {
								Toast.makeText(mActivity, getString(R.string.toast_has_been_register),
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(mActivity, getString(R.string.toast_connect_server_failure),
										Toast.LENGTH_SHORT).show();
							}
						}
					}

					@Override
					public void onStart() {
					}

					@Override
					public void onFailure(Object o) {
						Toast.makeText(mActivity, getString(R.string.toast_connect_server_failure), Toast.LENGTH_SHORT)
								.show();
					}

					@Override
					public void onSuccess(String result) {
						if (mUserInfor.loginResult(result)) {
							saveUserData();
						} else {
							int err = mUserInfor.getErrResultCode(result);
							if (err == 2 || err == 3) {
								Toast.makeText(mActivity, getString(R.string.error_username_or_password),
										Toast.LENGTH_SHORT).show();
							} else if (err == 4) {
								Toast.makeText(mActivity, getString(R.string.toast_has_been_register),
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
	}

	private void returnMainUi() {
		finish();
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
}
