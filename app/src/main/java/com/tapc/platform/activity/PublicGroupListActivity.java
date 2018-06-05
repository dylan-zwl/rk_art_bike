package com.tapc.platform.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.adpater.CommonListAdapter;
import com.tapc.platform.adpater.ViewHolder;
import com.tapc.platform.dto.PublicGroupDto;
import com.tapc.platform.dto.ResponseDTO;
import com.tapc.platform.dto.net.Callback;

public class PublicGroupListActivity extends BaseActivity implements OnItemClickListener {
	@ViewInject(R.id.listview_groups)
	public ListView listView;

	private CommonListAdapter<PublicGroupDto> adapter;
	private List<PublicGroupDto> groupList = new ArrayList<PublicGroupDto>();
	private List<String> groupStringList = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_public_group_list);
		ViewUtils.inject(this);
		setData();
	}

	private void setData() {
		adapter = new CommonListAdapter<PublicGroupDto>(this, groupList, R.layout.item_pulic_group) {
			@Override
			public void convert(ViewHolder helper, final PublicGroupDto item, final int position, ViewGroup parent) {
				helper.setText(R.id.tv_public_number, String.valueOf(position + 1));
				helper.setText(R.id.tv_public_gourp_name, item.getGroupName());
				helper.setText(R.id.tv_public_gourp_number, String.valueOf((int)item.getMembersCount()));
				
				helper.getView(R.id.iv_group_public_add).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						 Intent intent = new Intent(PublicGroupListActivity.this, GroupDetailActivity.class);
						 intent.putExtra("isPrivate", false);
						 intent.putExtra("groupId", String.valueOf((long)item.getGroupId()));
							intent.putExtra("groupName", adapter.getItem(position).getGroupName());
						 startActivity(intent);
						 finish();
					}
				});
			}
		};

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		
		getPublicGroupsPost();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(PublicGroupListActivity.this, PublicGroupDetailActivity.class);
		intent.putExtra("groupId", String.valueOf((long)groupList.get(arg2).getGroupId()));
		intent.putExtra("groupName", adapter.getItem(arg2).getGroupName());
		startActivity(intent);
	}

	private void getPublicGroupsPost() {
		TapcApp.getInstance().getHttpClient().getPublicGroups(new Callback() {

			@Override
			public void onSuccess(String result) {
			}

			@Override
			public void onSuccess(Object o) {
				ResponseDTO<ArrayList<String>> responseDTO = (ResponseDTO<ArrayList<String>>) o;
				String json = new Gson().toJson(responseDTO.getResponse());
				List<PublicGroupDto> groupDtos= new Gson().fromJson(json, new TypeToken<ArrayList<PublicGroupDto>>() {}.getType());
				groupList.addAll(groupDtos);
				adapter.notifyDataSetChanged(groupList);
				listView.setFastScrollEnabled(true);
			}

			@Override
			public void onStart() {
			}

			@Override
			public void onFailure(Object o) {

			}
		});
	}

	private String getRandomId(List<String> idList) {
		Random rand = new Random();
		int listIndex = rand.nextInt(idList.size());
		String id = idList.get(listIndex);
		return id;
	}
	
}
