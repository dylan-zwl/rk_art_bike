package com.tapc.platform.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.tapc.platform.R;
import com.tapc.platform.activity.GroupDetailActivity;
import com.tapc.platform.activity.PublicGroupDetailActivity;
import com.tapc.platform.adpater.CommonListAdapter;
import com.tapc.platform.adpater.ViewHolder;
import com.tapc.platform.dto.GroupUserDTO;

public class PublicGroupRankListFragment extends Fragment  {
	private View rootView;
	private ListView listView;
	private CommonListAdapter<GroupUserDTO> adapter;
	private RadioGroup rgRank;
	private RadioButton rbDistance;
	private RadioButton rbTime;
	private RadioButton rbSpeed;
	private RadioButton rbCalorie;
	protected boolean isVisible;
	private int rbCheckId;
	private Map<String, GroupUserDTO> memberMap = new HashMap<String, GroupUserDTO>();
	private List<GroupUserDTO> memberList = new ArrayList<GroupUserDTO>();
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				memberList = ((PublicGroupDetailActivity) getActivity()).getMemberList();
				chooseRank();
				adapter.notifyDataSetChanged(memberList);
				break;

			default:
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_public_groups_rank, null);
		bindView();
		initData();
		return rootView;
	}

	private void bindView() {
		listView = (ListView) rootView.findViewById(R.id.listview_group);
		rgRank = (RadioGroup) rootView.findViewById(R.id.rg_group_type);
		rbDistance = (RadioButton) rootView.findViewById(R.id.rb_public_rank_distance);
		rbTime = (RadioButton) rootView.findViewById(R.id.rb_public_rank_time);
		rbSpeed = (RadioButton) rootView.findViewById(R.id.rb_public_rank_speed);
		rbCalorie = (RadioButton) rootView.findViewById(R.id.rb_public_rank_calorie);
		rbDistance.setChecked(true);
		rgRank.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				rbCheckId = checkedId;
				chooseRank();
			}
		});
	}

	private void initData() {
		adapter = new CommonListAdapter<GroupUserDTO>(getActivity(), memberList, R.layout.item_public_group_rank) {

			@Override
			public void convert(ViewHolder helper, GroupUserDTO item, int position, ViewGroup parent) {
				 helper.setText(R.id.tv_public_rank, (position+1)+"");
				 helper.setText(R.id.tv_public_name, item.getRunner());
				 helper.setText(R.id.tv_public_data, item.getDistance() + "KM");
					switch (rbCheckId) {
					case R.id.rb_public_rank_distance:
						helper.setText(R.id.tv_public_data, item.getDistance() + "KM");
						break;
					case R.id.rb_public_rank_time:
						helper.setText(R.id.tv_public_data, (item.getDuration()/1000/3600) + "h");
						break;
					case R.id.rb_public_rank_speed:
						helper.setText(R.id.tv_public_data, item.getSpeed() + "km/h");
						break;
					case R.id.rb_public_rank_calorie:
						helper.setText(R.id.tv_public_data, item.getCalories() + "kcal");
						break;
					default:
						break;
					}
				
			}
		};

		listView.setAdapter(adapter);
		memberList = ((PublicGroupDetailActivity) getActivity()).getMemberList();
		adapter.notifyDataSetChanged(memberList);
	}

	
	private void chooseRank(){
		switch (rbCheckId) {
		case R.id.rb_public_rank_distance:
			rbDistance.setChecked(true);
			rbTime.setChecked(false);
			rbSpeed.setChecked(false);
			rbCalorie.setChecked(false);
			ComparatorListDistance comparator = new ComparatorListDistance();
			Collections.sort(memberList, comparator);
			adapter.notifyDataSetChanged(memberList);
			break;
		case R.id.rb_public_rank_time:
			rbDistance.setChecked(false);
			rbTime.setChecked(true);
			rbSpeed.setChecked(false);
			rbCalorie.setChecked(false);
			ComparatorListTime comparatorListTime = new ComparatorListTime();
			Collections.sort(memberList, comparatorListTime);
			adapter.notifyDataSetChanged(memberList);
			break;
		case R.id.rb_public_rank_speed:
			rbDistance.setChecked(false);
			rbTime.setChecked(false);
			rbSpeed.setChecked(true);
			rbCalorie.setChecked(false);
			ComparatorListSpeed comparatorListSpeed = new ComparatorListSpeed();
			Collections.sort(memberList, comparatorListSpeed);
			adapter.notifyDataSetChanged(memberList);
			break;
		case R.id.rb_public_rank_calorie:
			rbDistance.setChecked(false);
			rbTime.setChecked(false);
			rbSpeed.setChecked(false);
			rbCalorie.setChecked(true);
			ComparatorListCalorie comparatorListCalorie = new ComparatorListCalorie();
			Collections.sort(memberList, comparatorListCalorie);
			adapter.notifyDataSetChanged(memberList);
			break;

		default:
			break;
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
}
