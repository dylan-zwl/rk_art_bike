package com.tapc.platform.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.GroupDetailActivity;
import com.tapc.platform.activity.GroupUserDetailActivity;
import com.tapc.platform.adpater.CommonListAdapter;
import com.tapc.platform.adpater.ViewHolder;
import com.tapc.platform.dto.GroupUserDTO;
import com.tapc.platform.witget.CircleImageView;

public class PrivateGroupRankListFragment extends Fragment implements OnItemClickListener {
	private View rootView;
	private ListView listView;
	private CommonListAdapter<GroupUserDTO> adapter;
	private RadioGroup rgRank;
	private RadioButton rbDistance;
	private RadioButton rbTime;
	private RadioButton rbSpeed;
	private RadioButton rbCalorie;
	private ImageView ivDirectionFrist;
	private ImageView ivDirectionSecond;
	private ImageView ivDirectionThrid;
	private ImageView ivDirectionFour;
	private String groupId = "";
	private Map<String, GroupUserDTO> memberMap = new HashMap<String, GroupUserDTO>();
	private List<GroupUserDTO> newMemberList = new ArrayList<GroupUserDTO>();
	private Map<String, Integer> oldRankMap = new HashMap<String, Integer>();

	private TextView myRankTv;
	private TextView myUserNameTv;
	private TextView myDistanceTv;
	private TextView myTimeTv;
	private TextView mySpeedTv;
	private TextView myCalorieTv;
	private CircleImageView myCiv_avatar;
	private RelativeLayout layoutHeader;

	private int positions = 0;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				List<GroupUserDTO> memberList = ((GroupDetailActivity) getActivity()).getMemberList();
				if (memberList != null && memberList.size() > 0) {
					newMemberList.clear();
					newMemberList.addAll(memberList);
				}
				// listView.setSelection(positions);
				chooseRank(rgRank.getCheckedRadioButtonId());
				adapter.notifyDataSetChanged(newMemberList);

				showLocalUserInfo();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_private_groups_rank_list, null);
		bindView();
		initData();
		return rootView;
	}

	private void bindView() {
		((GroupDetailActivity) getActivity()).setHandlerRank(handler);
		listView = (ListView) rootView.findViewById(R.id.listview_group);
		rgRank = (RadioGroup) rootView.findViewById(R.id.rg_group_type);
		rbDistance = (RadioButton) rootView.findViewById(R.id.rb_rank_distance);
		rbTime = (RadioButton) rootView.findViewById(R.id.rb_rank_time);
		rbSpeed = (RadioButton) rootView.findViewById(R.id.rb_rank_speed);
		rbCalorie = (RadioButton) rootView.findViewById(R.id.rb_rank_calorie);
		ivDirectionFrist = (ImageView) rootView.findViewById(R.id.group_rank_direction_first);
		ivDirectionSecond = (ImageView) rootView.findViewById(R.id.group_rank_direction_second);
		ivDirectionThrid = (ImageView) rootView.findViewById(R.id.group_rank_direction_thrid);
		ivDirectionFour = (ImageView) rootView.findViewById(R.id.group_rank_direction_four);

		myRankTv = (TextView) rootView.findViewById(R.id.tv_rank);
		myUserNameTv = (TextView) rootView.findViewById(R.id.tv_name);
		myDistanceTv = (TextView) rootView.findViewById(R.id.tv_runner_distance);
		myTimeTv = (TextView) rootView.findViewById(R.id.tv_runner_time);
		mySpeedTv = (TextView) rootView.findViewById(R.id.tv_runner_speed);
		myCalorieTv = (TextView) rootView.findViewById(R.id.tv_runner_calorie);
		myCiv_avatar = (CircleImageView) rootView.findViewById(R.id.civ_avatar);
		layoutHeader = (RelativeLayout) rootView.findViewById(R.id.layout_header);

		rbDistance.setChecked(true);
		rgRank.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				handler.sendEmptyMessage(1);
			}
		});
		initScrollBar();

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

	private String getCalorieStr(GroupUserDTO item) {
		return item.getCaloriesStr() + " kcal";
	}

	private String getSpeedStr(GroupUserDTO item) {
		return item.getSpeedStr() + " Km/h";
	}

	private String getDistanceStr(GroupUserDTO item) {
		return item.getDistanceStr() + " Km";
	}

	private String getTimeStr(GroupUserDTO item) {
		return item.getTimeTampStr();
	}

	private void showLocalUserInfo() {
		String username = TapcApp.getInstance().userInfor.getUsername();
		if (username.isEmpty()) {
			username = Config.DEVICE_ID;
		}
		int position = 0;
		for (GroupUserDTO item : newMemberList) {
			if (item.getRunner().equals(username)) {
				String time = getTimeStr(item);
				String distance = getDistanceStr(item);
				String speed = getSpeedStr(item);
				String calorie = getCalorieStr(item);
				layoutHeader.setVisibility(View.VISIBLE);
				myRankTv.setText(String.valueOf(position + 1));
				myUserNameTv.setText(item.getRunner());
				myDistanceTv.setText(distance);
				myTimeTv.setText(time);
				mySpeedTv.setText(speed);
				myCalorieTv.setText(calorie);

				int icId = ViewHolder.getUserIcId(username);
				myCiv_avatar.setImageResource(icId);
			}
			position++;
		}
		if (positions > newMemberList.size()) {
			positions = newMemberList.size();
		}
	}

	private void initData() {
		adapter = new CommonListAdapter<GroupUserDTO>(getActivity(), newMemberList, R.layout.item_group_rank) {

			@Override
			public void convert(ViewHolder helper, GroupUserDTO item, int position, ViewGroup parent) {
				ImageView rankIamgeIv = helper.getView(R.id.iv_rank_iamge_up);
				if (oldRankMap != null && oldRankMap.size() > 0) {
					try {
						int oldRank = oldRankMap.get(item.getRunner());
						if (oldRank > position) {
							rankIamgeIv.setImageResource(R.drawable.group_rank_up);
						} else if (oldRank < position) {
							rankIamgeIv.setImageResource(R.drawable.group_rank_down);
						} else {
							rankIamgeIv.setImageResource(android.R.color.transparent);
						}
					} catch (Exception e) {
						rankIamgeIv.setImageResource(android.R.color.transparent);
					}

				}

				String time = getTimeStr(item);
				String distance = getDistanceStr(item);
				String speed = getSpeedStr(item);
				String calorie = getCalorieStr(item);

				String name = item.getRunner();
				helper.setText(R.id.tv_name, name);
				helper.setText(R.id.tv_runner_distance, distance);
				helper.setText(R.id.tv_runner_time, time);
				helper.setText(R.id.tv_runner_speed, speed);
				helper.setText(R.id.tv_runner_calorie, calorie);
				helper.setText(R.id.tv_rank, (position + 1) + "");

				int icId = helper.getUserIcId(name);
				helper.setImageResource(R.id.civ_avatar, icId);

				switch (position) {
				case 0:
					helper.setImageResource(R.id.iv_rank_iamge_icon, R.drawable.group_rank_first);
					helper.getView(R.id.rl_item).setBackgroundResource(R.drawable.group_item_rank_shape_first);
					helper.getView(R.id.iv_rank_iamge_icon).setVisibility(View.VISIBLE);
					helper.getView(R.id.view_item_left).setVisibility(View.VISIBLE);
					break;
				case 1:
					helper.setImageResource(R.id.iv_rank_iamge_icon, R.drawable.group_rank_second);
					helper.getView(R.id.rl_item).setBackgroundResource(R.drawable.group_item_rank_shape_second);
					helper.getView(R.id.iv_rank_iamge_icon).setVisibility(View.VISIBLE);
					helper.getView(R.id.view_item_left).setVisibility(View.VISIBLE);
					break;
				case 2:
					helper.setImageResource(R.id.iv_rank_iamge_icon, R.drawable.group_rank_third);
					helper.getView(R.id.rl_item).setBackgroundResource(R.drawable.group_item_rank_shape_thrid);
					helper.getView(R.id.iv_rank_iamge_icon).setVisibility(View.VISIBLE);
					helper.getView(R.id.view_item_left).setVisibility(View.VISIBLE);
					break;
				default:
					helper.setImageResource(R.id.iv_rank_iamge_icon, android.R.color.transparent);
					helper.getView(R.id.rl_item).setBackgroundResource(R.drawable.group_item_rank_shape);
					helper.getView(R.id.iv_rank_iamge_icon).setVisibility(View.VISIBLE);
					helper.getView(R.id.view_item_left).setVisibility(View.INVISIBLE);
					break;
				}

				if (position == newMemberList.size() - 1) {
					setNewRankMap();
				}

			}
		};

		positions = 0;
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		newMemberList.clear();
		newMemberList.addAll(((GroupDetailActivity) getActivity()).getMemberList());
		adapter.notifyDataSetChanged(newMemberList);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View poss, int position, long arg3) {
				positions = position;
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(getActivity(), GroupUserDetailActivity.class);
		intent.putExtra("groupUserDTO", newMemberList.get(arg2));
		startActivity(intent);
	}

	private void chooseRank(int rbId) {
		switch (rbId) {
		case R.id.rb_rank_distance:
			rbDistance.setChecked(true);
			rbTime.setChecked(false);
			rbSpeed.setChecked(false);
			rbCalorie.setChecked(false);
			ivDirectionFrist.setVisibility(View.VISIBLE);
			ivDirectionSecond.setVisibility(View.INVISIBLE);
			ivDirectionThrid.setVisibility(View.INVISIBLE);
			ivDirectionFour.setVisibility(View.INVISIBLE);
			ComparatorListDistance comparator = new ComparatorListDistance();
			Collections.sort(newMemberList, comparator);
			break;
		case R.id.rb_rank_time:
			rbDistance.setChecked(false);
			rbTime.setChecked(true);
			rbSpeed.setChecked(false);
			rbCalorie.setChecked(false);
			ivDirectionFrist.setVisibility(View.INVISIBLE);
			ivDirectionSecond.setVisibility(View.VISIBLE);
			ivDirectionThrid.setVisibility(View.INVISIBLE);
			ivDirectionFour.setVisibility(View.INVISIBLE);
			ComparatorListTime comparatorListTime = new ComparatorListTime();
			Collections.sort(newMemberList, comparatorListTime);
			break;
		case R.id.rb_rank_speed:
			rbDistance.setChecked(false);
			rbTime.setChecked(false);
			rbSpeed.setChecked(true);
			rbCalorie.setChecked(false);
			ivDirectionFrist.setVisibility(View.INVISIBLE);
			ivDirectionSecond.setVisibility(View.INVISIBLE);
			ivDirectionThrid.setVisibility(View.VISIBLE);
			ivDirectionFour.setVisibility(View.INVISIBLE);
			ComparatorListSpeed comparatorListSpeed = new ComparatorListSpeed();
			Collections.sort(newMemberList, comparatorListSpeed);
			break;
		case R.id.rb_rank_calorie:
			rbDistance.setChecked(false);
			rbTime.setChecked(false);
			rbSpeed.setChecked(false);
			rbCalorie.setChecked(true);
			ivDirectionFrist.setVisibility(View.INVISIBLE);
			ivDirectionSecond.setVisibility(View.INVISIBLE);
			ivDirectionThrid.setVisibility(View.INVISIBLE);
			ivDirectionFour.setVisibility(View.VISIBLE);
			ComparatorListCalorie comparatorListCalorie = new ComparatorListCalorie();
			Collections.sort(newMemberList, comparatorListCalorie);
			break;

		default:
			break;
		}
	}

	private void setNewRankMap() {
		oldRankMap.clear();
		for (int i = 0; i < newMemberList.size(); i++) {
			oldRankMap.put(newMemberList.get(i).getRunner(), i);
		}
	}

}

class ComparatorListDistance implements Comparator {

	public int compare(Object arg0, Object arg1) {

		GroupUserDTO dto1 = (GroupUserDTO) arg0;
		GroupUserDTO dto2 = (GroupUserDTO) arg1;

		// 首先比较出现次数，如果相同，则比较名字
		Integer num = dto1.getDistance();
		Integer num2 = dto2.getDistance();
		int flag = num2.compareTo(num);
		if (flag == 0) {
			return (dto1.getRunner().toString()).compareTo(dto2.getRunner().toString());
		} else {
			return flag;
		}
	}
}

class ComparatorListTime implements Comparator {

	public int compare(Object arg0, Object arg1) {

		GroupUserDTO dto1 = (GroupUserDTO) arg0;
		GroupUserDTO dto2 = (GroupUserDTO) arg1;

		Long num = dto1.getDuration();
		Long num2 = dto2.getDuration();
		long flag = num2.compareTo(num);
		if (flag == 0) {
			return (dto1.getRunner().toString()).compareTo(dto2.getRunner().toString());
		} else {
			return (int) flag;
		}
	}
}

class ComparatorListSpeed implements Comparator {

	public int compare(Object arg0, Object arg1) {

		GroupUserDTO dto1 = (GroupUserDTO) arg0;
		GroupUserDTO dto2 = (GroupUserDTO) arg1;

		Integer num = dto1.getSpeed();
		Integer num2 = dto2.getSpeed();
		int flag = num2.compareTo(num);
		if (flag == 0) {
			return (dto1.getRunner().toString()).compareTo(dto2.getRunner().toString());
		} else {
			return flag;
		}
	}
}

class ComparatorListCalorie implements Comparator {

	public int compare(Object arg0, Object arg1) {

		GroupUserDTO dto1 = (GroupUserDTO) arg0;
		GroupUserDTO dto2 = (GroupUserDTO) arg1;

		Integer num = dto1.getCalories();
		Integer num2 = dto2.getCalories();
		int flag = num2.compareTo(num);
		if (flag == 0) {
			return (dto1.getRunner().toString()).compareTo(dto2.getRunner().toString());
		} else {
			return flag;
		}
	}
}
