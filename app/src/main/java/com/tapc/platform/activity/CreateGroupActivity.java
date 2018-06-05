package com.tapc.platform.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.dto.GroupUserDTO;
import com.tapc.platform.dto.ResponseDTO;
import com.tapc.platform.dto.net.Callback;
import com.tapc.platform.utils.SysUtils;
import com.tapc.platform.witget.ProgressDialog;

public class CreateGroupActivity extends BaseActivity {
	@ViewInject(R.id.edit_private_group_name)
	private EditText editText;

	@ViewInject(R.id.cb_is_private)
	private CheckBox isPrivateCb;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_create_groups);
		ViewUtils.inject(this);
	}

	@OnClick(R.id.btn_crate_group)
	protected void groupsButtonOnclick(View v) {
		String group = editText.getText().toString().trim();
		if ("".equals(group)) {
			Toast.makeText(this, getResources().getString(R.string.please_input_group_name), Toast.LENGTH_SHORT).show();
			return;
		}

		createGroupPost(isPrivateCb.isChecked());
	}

	private void createGroupPost(boolean isPrivate) {
		final ProgressDialog dialog = new ProgressDialog(CreateGroupActivity.this);
		GroupUserDTO groupUserDTO = TapcApp.getInstance().groupUserDTO;
		String member = new Gson().toJson(groupUserDTO);

		if (isPrivate) {
			TapcApp.getInstance().getHttpClient()
					.createPrivateGroup(editText.getText().toString().trim(), member, new Callback() {

						@Override
						public void onSuccess(String result) {
						}

						@Override
						public void onSuccess(Object o) {
							ResponseDTO<String> dto = (ResponseDTO<String>) o;
							Intent intent = new Intent(CreateGroupActivity.this,
									CreatePrivateGroupSuccessActivity.class);
							intent.putExtra("groupName", editText.getText().toString().trim());
							intent.putExtra("invitationCode", dto.getResponse());
							startActivity(intent);
							dialog.dismiss();
							finish();
						}

						@Override
						public void onStart() {
							dialog.setMessage(getResources().getString(R.string.creatting));
							dialog.show();
						}

						@Override
						public void onFailure(Object o) {
							dialog.dismiss();
							Toast.makeText(CreateGroupActivity.this, getResources().getString(R.string.creat_fail),
									Toast.LENGTH_SHORT).show();
						}
					});
		} else {

			TapcApp.getInstance().getHttpClient()
					.createPublicGroup(editText.getText().toString().trim(), member, new Callback() {

						@Override
						public void onSuccess(String result) {

						}

						@Override
						public void onSuccess(Object o) {
							ResponseDTO dto = (ResponseDTO) o;
							Intent intent = new Intent(CreateGroupActivity.this, GroupDetailActivity.class);
							intent.putExtra("groupId",
									String.valueOf((int) Double.parseDouble(dto.getResponse().toString())));
							intent.putExtra("invitationCode", "");
							intent.putExtra("isPrivate", false);
							intent.putExtra("groupName", editText.getText().toString().trim());
							startActivity(intent);
							finish();
						}

						@Override
						public void onStart() {
							dialog.setMessage(getResources().getString(R.string.creatting));
							dialog.show();
						}

						@Override
						public void onFailure(Object o) {
							dialog.dismiss();
							Toast.makeText(CreateGroupActivity.this, getResources().getString(R.string.creat_fail),
									Toast.LENGTH_SHORT).show();
						}
					});
		}

	}

}
