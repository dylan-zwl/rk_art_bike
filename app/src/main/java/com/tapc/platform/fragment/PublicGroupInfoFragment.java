package com.tapc.platform.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.activity.GroupDetailActivity;
import com.tapc.platform.activity.PublicGroupDetailActivity;
import com.tapc.platform.adpater.CommonListAdapter;
import com.tapc.platform.adpater.ViewHolder;
import com.tapc.platform.dto.GroupUserDTO;

public class PublicGroupInfoFragment extends Fragment {
	private View rootView;
	private ListView listView;
	private TextView groupNameTv;
	private TextView publicFroupTotalNumTv;
	public CommonListAdapter<GroupUserDTO> adapter;
	private String groupId;
	private String groupName;
	private Button btnAddNow;
	private Map<String, GroupUserDTO> memberMap = new HashMap<String, GroupUserDTO>();
	private List<GroupUserDTO> memberList = new ArrayList<GroupUserDTO>();

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				memberList = ((PublicGroupDetailActivity) getActivity()).getMemberList();
				publicFroupTotalNumTv.setText(String.valueOf(memberList.size()));
				adapter.notifyDataSetChanged(memberList);
				break;

			default:
				break;
			}
		}
	};

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			((PublicGroupDetailActivity) getActivity()).getGroupInfo();
			handler.postDelayed(runnable, 3000);
			handler.removeCallbacks(runnable);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_public_groups_info, null);
		bindView();
		initData();
		return rootView;
	}

	private void bindView() {
		((PublicGroupDetailActivity) getActivity()).setHandler(handler);
		listView = (ListView) rootView.findViewById(R.id.listview_group);
		groupNameTv = (TextView) rootView.findViewById(R.id.tv_groupname);
		publicFroupTotalNumTv = (TextView) rootView.findViewById(R.id.tv_public_group_total_num);
		btnAddNow = (Button) rootView.findViewById(R.id.btn_join_public_group);
		btnAddNow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), GroupDetailActivity.class);
				intent.putExtra("isPrivate", false);
				intent.putExtra("groupId", groupId);
				intent.putExtra("groupName", groupName);
				startActivity(intent);
				getActivity().finish();
			}
		});

		initScrollBar();
	}

	private void initData() {
		Intent intent = getActivity().getIntent();
		if (intent != null) {
			groupId = intent.getStringExtra("groupId");
			groupName = intent.getStringExtra("groupName");

		}
		adapter = new CommonListAdapter<GroupUserDTO>(getActivity(), memberList, R.layout.item_public_group_member) {
			@Override
			public void convert(ViewHolder helper, GroupUserDTO item, int position, ViewGroup parent) {
				helper.setText(R.id.tv_name, item.getRunner());
				String location = item.getLocation();
				if (location != null && !location.isEmpty()) {
					helper.setText(R.id.tv_location, location);
				}
			}
		};
		groupNameTv.setText(groupName);

		listView.setAdapter(adapter);
		memberList = ((PublicGroupDetailActivity) getActivity()).getMemberList();
		adapter.notifyDataSetChanged(memberList);
		handler.postDelayed(runnable, 0);
	}

	private void initScrollBar() {
		try {
			Field f = AbsListView.class.getDeclaredField("mFastScroller");
			f.setAccessible(true);
			Object o = f.get(listView);
			f = f.getType().getDeclaredField("mThumbDrawable");
			f.setAccessible(true);
			Drawable drawable = (Drawable) f.get(o);
			drawable = getResources().getDrawable(R.drawable.group_listview_bscroll_dark);
			f.set(o, drawable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
