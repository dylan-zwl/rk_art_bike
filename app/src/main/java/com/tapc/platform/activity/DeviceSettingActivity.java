package com.tapc.platform.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.model.common.ConfigModel;
import com.tapc.platform.model.healthcat.BaseCtlModel;

/**
 * Created by Administrator on 2018/5/23.
 */

public class DeviceSettingActivity extends BaseActivity {
    @ViewInject(R.id.set_device_id_et)
    EditText mDeviceIdEt;
    @ViewInject(R.id.set_server_url_et)
    EditText mServerUrlEt;
    @ViewInject(R.id.set_domain_name_et)
    EditText mDomainNameEt;
    @ViewInject(R.id.set_server_port_et)
    EditText mPortEt;
    @ViewInject(R.id.set_phone_number_et)
    EditText mPhoneNumberEt;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_setting_device);
        ViewUtils.inject(this);
        initView();
    }

    private void initView() {
        String deviceId = ConfigModel.getHealthCatDeviceId(this, "");
        String url = ConfigModel.readString(this, ConfigModel.SETTING_CONFIG, ConfigModel.SERVER_URL, ConfigModel
                .DEF_SERVER_URL);
        String domain = ConfigModel.readString(this, ConfigModel.SETTING_CONFIG, ConfigModel.SERVER_DOMAIN_NAME,
                BaseCtlModel.SERVER_URL);
        int port = ConfigModel.readInt(this, ConfigModel.SETTING_CONFIG, ConfigModel.SERVER_PORT, BaseCtlModel
                .SERVER_PORT);
        String phoneNumber = ConfigModel.readString(this, ConfigModel.SETTING_CONFIG, ConfigModel.PHONE_NUMBER,
                ConfigModel.DEF_PHONE_NUMBER);

        mDeviceIdEt.setText(deviceId);
        mServerUrlEt.setText(url);
        mDomainNameEt.setText(domain);
        mPortEt.setText(String.valueOf(port));
        mPhoneNumberEt.setText(phoneNumber);
    }

    @OnClick(R.id.save_btn)
    void saveOnClick(View v) {
        String deviceId = mDeviceIdEt.getEditableText().toString();
        String url = mServerUrlEt.getEditableText().toString();
        String phoneNumber = mPhoneNumberEt.getEditableText().toString();
        String domain = mDomainNameEt.getEditableText().toString();
        String port = mPortEt.getEditableText().toString();

        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(port) || TextUtils.isEmpty(domain)) {
            Toast.makeText(this, "输入值不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (url == null) {
            url = "";
        }
        if (phoneNumber == null) {
            phoneNumber = "";
        }
        int portInt = Integer.valueOf(port).intValue();

        ConfigModel.setHealthCatDeviceId(this, deviceId);
        ConfigModel.write(this, ConfigModel.SETTING_CONFIG, ConfigModel.SERVER_URL, url);
        ConfigModel.write(this, ConfigModel.SETTING_CONFIG, ConfigModel.PHONE_NUMBER, phoneNumber);
        ConfigModel.write(this, ConfigModel.SETTING_CONFIG, ConfigModel.SERVER_DOMAIN_NAME, domain);
        ConfigModel.write(this, ConfigModel.SETTING_CONFIG, ConfigModel.SERVER_PORT, portInt);
        Toast.makeText(DeviceSettingActivity.this, getString(R.string.save_reboot), Toast
                .LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TapcApp.getInstance().mainActivity.reload();
                System.exit(0);
            }
        }, 5000);
    }

    @OnClick(R.id.setting_btn)
    void setttingOnClick(View v) {
        startActivity(new Intent(this, SettingActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
