package com.tapc.platform.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.dto.ResponseDTO;
import com.tapc.platform.dto.net.Callback;
import com.tapc.platform.witget.ProgressDialog;

public class CreatePrivateGroupSuccessActivity extends BaseActivity {

	@ViewInject(R.id.tv_group_name)
	private TextView tvName;
	@ViewInject(R.id.tv_group_id)
	private TextView tvCode;

	private String groupName = "";
	private String invitationCode = "";
	private String groupId = "";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_create_private_group_success);
		ViewUtils.inject(this);
		initData();
	}

	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			groupName = intent.getStringExtra("groupName");
			invitationCode = intent.getStringExtra("invitationCode");
			tvName.setText(getResources().getString(R.string.private_group) + " " + groupName);
			tvCode.setText(getResources().getString(R.string.invitation_code) + " " + invitationCode);
		}

		joinPrivateGroupPost();
	}

	@OnClick(R.id.btn_into_group)
	protected void groupsButtonOnclick(View v) {
		Intent intent = new Intent(this, GroupDetailActivity.class);
		intent.putExtra("groupId", groupId);
		intent.putExtra("invitationCode", invitationCode);
		intent.putExtra("isPrivate", true);
		intent.putExtra("groupName", groupName);
		startActivity(intent);
		finish();
	}

	private void joinPrivateGroupPost() {
		final ProgressDialog dialog = new ProgressDialog(CreatePrivateGroupSuccessActivity.this);
		TapcApp.getInstance().getHttpClient().joinPrivateGroup(invitationCode, new Callback() {

			@Override
			public void onSuccess(String result) {
			}

			@Override
			public void onSuccess(Object o) {
				ResponseDTO<Long> dto = (ResponseDTO<Long>) o;
				String id = String.valueOf(dto.getResponse());
				groupId = id.substring(0, id.indexOf("."));
				Log.v("CreateGroupSuccessActivity-ResponseDTO", dto.toString());
				dialog.dismiss();
			}

			@Override
			public void onStart() {
				dialog.setMessage(getResources().getString(R.string.loading));
				dialog.show();
			}

			@Override
			public void onFailure(Object o) {
				dialog.dismiss();
				Toast.makeText(CreatePrivateGroupSuccessActivity.this, getResources().getString(R.string.creat_fail),
						Toast.LENGTH_SHORT).show();
			}
		});
	}
}
