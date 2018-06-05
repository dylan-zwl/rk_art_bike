package com.tapc.platform.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.adpater.SimpleViewPagerAdapter;
import com.tapc.platform.dto.GroupUserDTO;
import com.tapc.platform.dto.GroupUserResponseDTO;
import com.tapc.platform.dto.ResponseDTO;
import com.tapc.platform.dto.net.Callback;
import com.tapc.platform.fragment.GroupInfoFragment;
import com.tapc.platform.fragment.PrivateGroupRankListFragment;

public class GroupDetailActivity extends FragmentActivity implements OnClickListener {
	private RadioGroup rgGroup;
	private RadioButton rbGroupInfo;
	private RadioButton rbRankList;
	private ViewPager viewPager;
	private String groupId = "";
	private List<GroupUserDTO> memberList = new ArrayList<GroupUserDTO>();
	private List<GroupUserDTO> oldMemberList = new ArrayList<GroupUserDTO>();
	private Map<String, GroupUserDTO> memberMap = new HashMap<String, GroupUserDTO>();

	private static final int FRAGMENT_GROUP_INFO = 0;
	private static final int FRAGMENT_GROUP_RANK_LIST = 1;
	private Handler handler;
	private Handler handlerRank;
	public static GroupUserDTO groupUserDTO;
	private boolean isFirstTime = true;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_group_detail);
		bindView();
	}

	@Override
	protected void onDestroy() {
		groupUserDTO = null;
		super.onDestroy();
	}

	private void bindView() {
		rgGroup = (RadioGroup) findViewById(R.id.rg_group_type);
		rbGroupInfo = (RadioButton) findViewById(R.id.rb_group_info);
		rbRankList = (RadioButton) findViewById(R.id.rb_group_rank_list);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		initData();
	}

	private void initData() {
		List<Fragment> pagers = new ArrayList<Fragment>();
		Fragment infoFragment = new GroupInfoFragment();
		Fragment rankListFragment = new PrivateGroupRankListFragment();
		pagers.add(infoFragment);
		pagers.add(rankListFragment);

		SimpleViewPagerAdapter viewPagerAdapter = new SimpleViewPagerAdapter(getSupportFragmentManager(), pagers);
		viewPager.setAdapter(viewPagerAdapter);
		// 设置默认界面
		viewPager.setCurrentItem(FRAGMENT_GROUP_INFO);
		rgGroup.check(R.id.rb_group_info);
		viewPager.setOffscreenPageLimit(2);
		setListener();

		Config.LEAVE_GROUP = false;
	}

	private void setListener() {
		rbGroupInfo.setOnClickListener(this);
		rbRankList.setOnClickListener(this);

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case FRAGMENT_GROUP_INFO:
					rgGroup.check(R.id.rb_group_info);
					break;

				case FRAGMENT_GROUP_RANK_LIST:
					rgGroup.check(R.id.rb_group_rank_list);
					break;

				default:
					break;
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rb_group_info:
			viewPager.setCurrentItem(FRAGMENT_GROUP_INFO, false);
			break;
		case R.id.rb_group_rank_list:
			viewPager.setCurrentItem(FRAGMENT_GROUP_RANK_LIST, false);
			break;
		default:
			break;
		}
	}

	public List<GroupUserDTO> getMemberList() {
		return memberList;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void setHandlerRank(Handler handlerRank) {
		this.handlerRank = handlerRank;
	}

	public Handler getHandlerRank() {
		return handlerRank;
	}

	public void groupInfoPost() {
		GroupUserDTO groupUserDTOs = TapcApp.getInstance().groupUserDTO;
		if (Config.LEAVE_GROUP) {
			leaveGroup();
			return;
		}

		Intent intent = getIntent();
		if (intent != null) {
			groupId = intent.getStringExtra("groupId");
		}

		String username = TapcApp.getInstance().userInfor.getUsername();
		if (username.isEmpty()) {
			username = Config.DEVICE_ID;
		}

		if (groupUserDTO != null) {
			groupUserDTO.setDuration(groupUserDTOs.getDuration());
			groupUserDTO.setDistance(groupUserDTOs.getDistance());
			groupUserDTO.setCalories(groupUserDTOs.getCalories());
			groupUserDTO.setSpeed(groupUserDTOs.getSpeed());
		} else {
			groupUserDTO = new GroupUserDTO(username, Config.DEVICE_ID, Config.CURRENT_LOCATON, Config.TOTAL_DISTANCE,
					Config.CURRENT_SPEED, Config.TOTAL_CALORIE, Config.TOTAL_TIME);
			TapcApp.getInstance().groupUserDTO = groupUserDTO;
		}
		String member = new Gson().toJson(groupUserDTO);
		TapcApp.getInstance().getHttpClient().setAndGetGroupData(groupId, member, new Callback() {

			@Override
			public void onSuccess(String result) {
			}

			@Override
			public void onSuccess(Object o) {
				try {
					GroupUserResponseDTO dto = (GroupUserResponseDTO) o;
					memberMap = dto.getResponse();
					Iterator it = memberMap.keySet().iterator();
					if (memberMap.isEmpty()) {
						return;
					}
					memberList.clear();
					while (it.hasNext()) {
						String key = it.next().toString();
						memberList.add(memberMap.get(key));
					}
					// 查询出退出的用户
					StringBuilder existUsernameSb = new StringBuilder();
					for (GroupUserDTO groupUser : oldMemberList) {
						boolean isExist = false;
						for (GroupUserDTO member : memberList) {
							if (groupUser.getRunner().equals(member.getRunner())) {
								isExist = true;
								break;
							}
						}
						if (!isExist) {
							existUsernameSb.append(groupUser.getRunner()).append(",");
							oldMemberList.remove(groupUser);
						}
					}

					if (!existUsernameSb.toString().isEmpty()) {
						Toast.makeText(
								GroupDetailActivity.this,
								existUsernameSb.toString().substring(0, existUsernameSb.toString().lastIndexOf(","))
										+ " " + getString(R.string.user_exit), Toast.LENGTH_LONG).show();
					}

					// 查询出新增的用户
					StringBuilder newUsernameSb = new StringBuilder();
					for (GroupUserDTO member : memberList) {
						boolean isExist = false;
						for (GroupUserDTO groupUserDTO : oldMemberList) {
							if (groupUserDTO.getRunner().equals(member.getRunner())) {
								isExist = true;
								break;
							}
						}
						if (!isExist) {
							newUsernameSb.append(member.getRunner()).append(",");
							oldMemberList.add(member);
						} else {
						}
					}
					if (!newUsernameSb.toString().isEmpty()) {
						isFirstTime = false;
						Toast.makeText(
								GroupDetailActivity.this,
								newUsernameSb.toString().substring(0, newUsernameSb.toString().lastIndexOf(",")) + " "
										+ getString(R.string.join_us), Toast.LENGTH_LONG).show();
					}

					handler.sendEmptyMessage(0);
					handlerRank.sendEmptyMessage(1);

				} catch (Exception e) {
					e.printStackTrace();
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

	@Override
	public void onBackPressed() {
		TapcApp.getInstance().menuBar.backHome();
	}

	private void leaveGroup() {
		String username = TapcApp.getInstance().userInfor.getUsername();
		if (username.isEmpty()) {
			username = Config.DEVICE_ID;
		}
		TapcApp.getInstance().getHttpClient().leaveGroup(groupId, username, new Callback() {

			@Override
			public void onSuccess(String result) {
			}

			@Override
			public void onSuccess(Object o) {
				ResponseDTO<String> dto = (ResponseDTO<String>) o;
				// 退出返回
				finish();
			}

			@Override
			public void onStart() {
			}

			@Override
			public void onFailure(Object o) {
				Toast.makeText(GroupDetailActivity.this, getResources().getString(R.string.exit_fail),
						Toast.LENGTH_SHORT).show();
				finish();
			}
		});

	}

}
