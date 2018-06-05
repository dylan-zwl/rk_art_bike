package com.tapc.platform.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.dto.net.Callback;
import com.tapc.platform.dto.response.LoginResponse;
import com.tapc.platform.entity.UploadUserConfig;
import com.tapc.platform.entity.UserInfor;
import com.tapc.platform.utils.PreferenceHelper;

public class LoginActivity extends BaseActivity {
	@ViewInject(R.id.login_name_edit)
	private EditText login_name;
	@ViewInject(R.id.login_password_edit)
	private EditText login_password;

	private Activity mActivity;
	private String username = "";
	private String pwd = "";
	private UserInfor mUserInfor;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_login);
		ViewUtils.inject(this);
		mActivity = this;
		mUserInfor = TapcApp.getInstance().userInfor;
		login_name.setText(mUserInfor.getUsername());
		login_password.setText(mUserInfor.getPassword());
	}

	private void localUserLogin(UserInfor userInfor) {
		if (userInfor.getPassword().equals(pwd)) {
			Toast.makeText(mActivity, username + getString(R.string.user_loggin_success), Toast.LENGTH_SHORT).show();
			TapcApp.getInstance().userInfor = userInfor;
			PreferenceHelper
					.write(mActivity, Config.SETTING_CONFIG, "user_infor", UserInfor.toGsonUserInfor(userInfor));
			TapcApp.getInstance().mainActivity.setLoginUserName(userInfor.getUsername());
			returnMainUi();
		} else {
			Toast.makeText(mActivity, getString(R.string.error_username_or_password), Toast.LENGTH_SHORT).show();
		}
	}

	@OnClick(R.id.login_btn)
	protected void loginSureOnclick(View v) {
		username = login_name.getText().toString();
		pwd = login_password.getText().toString();
		if (Config.isConnected && Config.HAS_SERVICE) {
			if (verifyTheInput()) {
				doPostLogin();
			}
		} else {
			UserInfor userInfor = TapcApp.getInstance().userDataDao.getUserRecord(username);
			if (userInfor != null) {
				localUserLogin(userInfor);
			} else {
				Toast.makeText(mActivity, getString(R.string.error_no_user), Toast.LENGTH_SHORT).show();
			}
		}
	}

	@OnClick(R.id.logout_btn)
	protected void logoutOnclick(View v) {
		TapcApp.getInstance().mainActivity.setLoginUserName(getString(R.string.login));
		PreferenceHelper.remove(mActivity, Config.SETTING_CONFIG, "user_infor");
		TapcApp.getInstance().userInfor = new UserInfor();
		finish();
	}

	/**
	 * @author sean ����˵������֤
	 */
	private boolean verifyTheInput() {
		if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, getText(R.string.error_empty_username), Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, getText(R.string.error_empty_password), Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void saveUserData() {
		username = mUserInfor.getUsername();
		Toast.makeText(mActivity, username + getString(R.string.user_loggin_success), Toast.LENGTH_SHORT).show();
		if (Config.SERVICE_ID == 0) {
			PreferenceHelper.write(mActivity, Config.SETTING_CONFIG, "user_infor",
					UserInfor.toGsonUserInfor(mUserInfor));
			UserInfor userInfor = TapcApp.getInstance().userDataDao.getUserRecord(username);
			if (userInfor == null) {
				TapcApp.getInstance().userDataDao.insertItem(mUserInfor);
			}
		}
		TapcApp.getInstance().mainActivity.setLoginUserName(username);
		returnMainUi();
	}

	private void doPostLogin() {
		TapcApp.getInstance().getHttpClient().login(username, pwd, new Callback() {
			@Override
			public void onSuccess(Object o) {
				if (o != null) {
					LoginResponse login = (LoginResponse) o;
					if (login.getStatus() == 200) {
						UploadUserConfig config = login.getResponse();
						mUserInfor.setUid("0");
						mUserInfor.setUsername(username);
						mUserInfor.setNickname(username);
						mUserInfor.setPassword(pwd);
						mUserInfor.setToken(config.getToken());
						saveUserData();
						Config.LEAVE_GROUP = true;
					} else if (login.getStatus() == 201) {
						Toast.makeText(mActivity, getString(R.string.error_empty_username_or_paswword),
								Toast.LENGTH_SHORT).show();
					} else if (login.getStatus() == 401) {
						Toast.makeText(mActivity, getString(R.string.error_username_or_password), Toast.LENGTH_SHORT)
								.show();
					} else if (login.getStatus() == 501) {
						Toast.makeText(mActivity, getString(R.string.toast_connect_server_failure), Toast.LENGTH_SHORT)
								.show();
					}
				}
			}

			@Override
			public void onStart() {
			}

			@Override
			public void onFailure(Object o) {
				UserInfor userInfor = TapcApp.getInstance().userDataDao.getUserRecord(username);
				if (userInfor != null) {
					localUserLogin(userInfor);
				} else {
					Toast.makeText(LoginActivity.this, getString(R.string.toast_connect_server_failure),
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onSuccess(String result) {
				if (mUserInfor.loginResult(result)) {
					saveUserData();
				} else {
					Toast.makeText(mActivity, getString(R.string.error_username_or_password), Toast.LENGTH_SHORT)
							.show();
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
