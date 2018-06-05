package com.tapc.platform.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.dto.GroupUserDTO;
import com.tapc.platform.dto.taobaoDataDTO;
import com.tapc.platform.dto.taobaoLocation;
import com.tapc.platform.dto.net.Callback;
import com.tapc.platform.utils.SysUtils;

public class GroupsActivity extends BaseActivity {
	@ViewInject(R.id.btn_create_group)
	public Button btnCreateGroup;
	@ViewInject(R.id.btn_add_group)
	public Button btnAddGroup;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_groups);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		if (Config.CURRENT_LOCATON.isEmpty()) {
			getLocation();
		}
		if (Config.DEVICE_ID.isEmpty()) {
			Config.DEVICE_ID = SysUtils.getLocalMacAddress(this);
		}
		String username = TapcApp.getInstance().userInfor.getUsername();
		if (username.isEmpty()) {
			username = Config.DEVICE_ID;
		}
		GroupUserDTO groupUserDTO = new GroupUserDTO(username, Config.DEVICE_ID, Config.CURRENT_LOCATON,
				Config.TOTAL_DISTANCE, Config.CURRENT_SPEED, Config.TOTAL_CALORIE, Config.TOTAL_TIME);
		TapcApp.getInstance().groupUserDTO = groupUserDTO;
	}

	private void getLocation() {
		TapcApp.getInstance().getHttpClient().getLocation("myip", new Callback() {

			@Override
			public void onSuccess(String result) {

			}

			@Override
			public void onSuccess(Object o) {
				taobaoLocation dto = (taobaoLocation) o;
				if (dto != null) {
					taobaoDataDTO data = dto.getData();
					if (data == null) {
						return;
					}
					String country = data.getCountry();
					String region = data.getRegion();
					String city = data.getCity();
					String county = data.getCounty();
					StringBuilder location = new StringBuilder();
					if (country != null) {
						location.append(country);
					}
					if (region != null) {
						location.append(" ");
						location.append(region);
					}
					if (city != null) {
						location.append(" ");
						location.append(city);
					}
					if (county != null) {
						location.append(" ");
						location.append(county);
					}
					String locationStr = location.toString();
					if (locationStr != null) {
						Config.CURRENT_LOCATON = locationStr;
					}
				}
			}

			@Override
			public void onStart() {

			}

			@Override
			public void onFailure(Object o) {

			}
		});
	}

	@OnClick(R.id.btn_create_group)
	protected void createButtonOnclick(View v) {
		Intent intent = new Intent(this, CreateGroupActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.iv_create_group)
	protected void createIamgeVeiwOnclick(View v) {
		Intent intent = new Intent(this, CreateGroupActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.btn_add_group)
	protected void addButtonOnclick(View v) {
		Intent intent = new Intent(this, AddGroupActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.iv_add_group)
	protected void addIamgeVeiwOnclick(View v) {
		Intent intent = new Intent(this, AddGroupActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
}
