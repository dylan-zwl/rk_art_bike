package com.tapc.platform.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.activity.GroupDetailActivity;
import com.tapc.platform.activity.GroupUserDetailActivity;
import com.tapc.platform.adpater.CommonListAdapter;
import com.tapc.platform.adpater.ViewHolder;
import com.tapc.platform.dto.GroupUserDTO;
import com.tapc.platform.witget.AlertDialog;

public class GroupInfoFragment extends Fragment implements OnItemClickListener {
	private Activity activity;
	private View rootView;
	private ListView listView;
	private ImageButton leaveGroup;
	private CommonListAdapter<GroupUserDTO> adapter;
	private String groupId = "";
	private String invitationCode = "";
	private String groupName = "";
	private Map<String, GroupUserDTO> memberMap = new HashMap<String, GroupUserDTO>();
	private List<GroupUserDTO> memberList = new ArrayList<GroupUserDTO>();
	private TextView tvInvitationCode;
	private TextView tvGroupName;
	private TextView tvTotal;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				memberList = ((GroupDetailActivity) getActivity()).getMemberList();
				tvTotal.setText(" " + String.valueOf(memberList.size()));
				adapter.notifyDataSetChanged(memberList);
				break;
			default:
				break;
			}
		}
	};
	private Handler mhandler = new Handler();

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			((GroupDetailActivity) getActivity()).groupInfoPost();
			mhandler.postDelayed(runnable, 2000);
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_groups_info, null);
		bindView();
		initData();
		return rootView;
	}

	private void bindView() {
		((GroupDetailActivity) getActivity()).setHandler(handler);
		listView = (ListView) rootView.findViewById(R.id.listview_group_info);
		tvInvitationCode = (TextView) rootView.findViewById(R.id.tv_private_gourp_id);
		tvGroupName = (TextView) rootView.findViewById(R.id.tv_group_detail_name);
		tvTotal = (TextView) rootView.findViewById(R.id.tv_group_total_num);
		leaveGroup = (ImageButton) rootView.findViewById(R.id.leave_group);
		leaveGroup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final AlertDialog dialog = new AlertDialog(activity, getResources().getString(R.string.exit_group),
						null);
				dialog.setOnCancelListener(new com.tapc.platform.witget.AlertDialog.OnClickListener() {
					@Override
					public void onClick() {
						dialog.dismiss();
					}
				});
				dialog.setOnOkClickListener(new com.tapc.platform.witget.AlertDialog.OnClickListener() {
					@Override
					public void onClick() {
						Config.LEAVE_GROUP = true;
					}
				});
				dialog.show();
			}
		});
		if (memberList != null && memberList.size() > 0) {
			tvTotal.setText(" " + String.valueOf(memberList.size()));
		}
		initScrollBar();
	}

	private void initData() {
		Intent intent = getActivity().getIntent();
		if (intent != null) {
			groupId = intent.getStringExtra("groupId");
			invitationCode = intent.getStringExtra("invitationCode");
			groupName = intent.getStringExtra("groupName");
			boolean isPrivate = intent.getBooleanExtra("isPrivate", false);
			if (isPrivate) {
				tvInvitationCode.setVisibility(View.VISIBLE);
				tvInvitationCode.setText(getResources().getString(R.string.invitation_code) + " " + invitationCode);
			}
			tvGroupName.setText(groupName);
		}
		adapter = new CommonListAdapter<GroupUserDTO>(getActivity(), memberList, R.layout.item_group_member) {

			@Override
			public void convert(ViewHolder helper, GroupUserDTO item, int position, ViewGroup parent) {
				String username = item.getRunner();
				helper.setText(R.id.tv_group_name, username);
				helper.setText(R.id.tv_location, item.getLocation());
				int icId = helper.getUserIcId(username);
				helper.setImageResource(R.id.civ_avatar, icId);
			}
		};

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		memberList = ((GroupDetailActivity) getActivity()).getMemberList();
		adapter.notifyDataSetChanged(memberList);
		mhandler.postDelayed(runnable, 0);
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(getActivity(), GroupUserDetailActivity.class);
		intent.putExtra("groupUserDTO", memberList.get(arg2));
		startActivity(intent);
	}

	@Override
	public void onDestroy() {
		mhandler.removeCallbacks(runnable);
		super.onDestroy();
	}

}
