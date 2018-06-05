package com.tapc.platform.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.platform.R;
import com.tapc.platform.activity.base.BaseActivity;

public class AddGroupActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_add_groups);
		ViewUtils.inject(this);
	}

	@OnClick(R.id.btn_add_private_group)
	protected void createButtonOnclick(View v) {
		Intent intent = new Intent(this, AddPrivateGroupActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.iv_add_private_group)
	protected void createImageViewOnclick(View v) {
		Intent intent = new Intent(this, AddPrivateGroupActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.btn_add_pbulic_group)
	protected void addButtonOnclick(View v) {
		Intent intent = new Intent(this, PublicGroupListActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.iv_add_pbulic_group)
	protected void addImageViewOnclick(View v) {
		Intent intent = new Intent(this, PublicGroupListActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
}
