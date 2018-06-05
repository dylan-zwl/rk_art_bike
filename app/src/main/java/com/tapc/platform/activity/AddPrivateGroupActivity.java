package com.tapc.platform.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.tapc.platform.dto.GroupUserResponseDTO;
import com.tapc.platform.dto.ResponseDTO;
import com.tapc.platform.dto.net.Callback;
import com.tapc.platform.utils.SysUtils;
import com.tapc.platform.witget.ProgressDialog;

public class AddPrivateGroupActivity extends BaseActivity {
	@ViewInject(R.id.edit_group_id)
	private EditText editId;

	private String groupId = "";
	private String invitationCode = "";
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_add_private_groups);
		ViewUtils.inject(this);
		dialog = new ProgressDialog(this);
	}

	@OnClick(R.id.btn_add_group)
	protected void addButtonOnclick(View v) {
		invitationCode = editId.getText().toString().trim();
		if ("".equals(invitationCode)) {
			Toast.makeText(AddPrivateGroupActivity.this, getResources().getString(R.string.please_input_group_name),
					Toast.LENGTH_SHORT).show();
			return;
		}

		getGroupIdPost();
	}

	private void getGroupIdPost() {
		TapcApp.getInstance().getHttpClient().joinPrivateGroup(invitationCode, new Callback() {

			@Override
			public void onSuccess(String result) {
			}

			@Override
			public void onSuccess(Object o) {
				ResponseDTO<Long> dto = (ResponseDTO<Long>) o;
				switch (dto.getStatus()) {
				case 200:
					String id = String.valueOf(dto.getResponse());
					groupId = id.substring(0, id.indexOf("."));
					addPrivateGroupPost();
					break;

				case 404:
					dialog.dismiss();
					Toast.makeText(AddPrivateGroupActivity.this,
							getResources().getString(R.string.error_invitation_code), Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}

			}

			@Override
			public void onStart() {
				dialog.setMessage(getResources().getString(R.string.joining));
				dialog.show();
			}

			@Override
			public void onFailure(Object o) {
				dialog.dismiss();
				Toast.makeText(AddPrivateGroupActivity.this, getResources().getString(R.string.join_fail),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void addPrivateGroupPost() {
		GroupUserDTO groupUserDTO = TapcApp.getInstance().groupUserDTO;
		String member = new Gson().toJson(groupUserDTO);
		TapcApp.getInstance().getHttpClient().setAndGetGroupData(groupId, member, new Callback() {

			@Override
			public void onSuccess(String result) {
			}

			@Override
			public void onSuccess(Object o) {
				dialog.dismiss();
				Intent intent = new Intent(AddPrivateGroupActivity.this, GroupDetailActivity.class);
				intent.putExtra("groupId", groupId);
				intent.putExtra("invitationCode", invitationCode);
				intent.putExtra("isPrivate", true);
				startActivity(intent);
				finish();
			}

			@Override
			public void onStart() {
			}

			@Override
			public void onFailure(Object o) {
				dialog.dismiss();
				Toast.makeText(AddPrivateGroupActivity.this, getResources().getString(R.string.join_fail),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

}
