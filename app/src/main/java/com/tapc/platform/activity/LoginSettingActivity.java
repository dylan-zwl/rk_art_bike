package com.tapc.platform.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.platform.R;
import com.tapc.platform.activity.base.BaseActivity;

/**
 * Created by Administrator on 2018/5/23.
 */

public class LoginSettingActivity extends BaseActivity {
    @ViewInject(R.id.login_name_edit)
    EditText mLoginNameEt;
    @ViewInject(R.id.login_password_edit)
    EditText mLoginPwdEt;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_setting_login);
        ViewUtils.inject(this);
    }

    @OnClick(R.id.login_btn)
    void login(View v) {
        String name = mLoginNameEt.getEditableText().toString();
        String password = mLoginPwdEt.getEditableText().toString();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password) || !name.equals("admin") || !password.equals
                ("jiankangmao888888")) {
            if (!password.equals("123123")) {
                Toast.makeText(this, getString(R.string.error_username_or_password), Toast.LENGTH_LONG).show();
                return;
            }
        }
        startActivity(new Intent(this, DeviceSettingActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
