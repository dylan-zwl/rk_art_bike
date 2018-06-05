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

import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.adpater.SimpleViewPagerAdapter;
import com.tapc.platform.dto.GroupUserDTO;
import com.tapc.platform.dto.GroupUserResponseDTO;
import com.tapc.platform.dto.net.Callback;
import com.tapc.platform.fragment.PublicGroupInfoFragment;
import com.tapc.platform.fragment.PublicGroupRankListFragment;

public class PublicGroupDetailActivity extends FragmentActivity implements OnClickListener {
	private RadioGroup rgGroup;
	private RadioButton rbGroupInfo;
	private RadioButton rbGroupRankList;
	private ViewPager viewPager;
	private static final int FRAGMENT_GROUP_INFO = 0;
	private List<GroupUserDTO> memberList = new ArrayList<GroupUserDTO>();
	private Map<String, GroupUserDTO> memberMap = new HashMap<String, GroupUserDTO>();
	private String groupId;
	private Fragment infoFragment;
	private Handler handler;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_group_detail);
		bindView();
		initData();
	}

	private void bindView() {
		rgGroup = (RadioGroup) findViewById(R.id.rg_group_type);
		rbGroupInfo = (RadioButton) findViewById(R.id.rb_group_info);
		rbGroupRankList = (RadioButton) findViewById(R.id.rb_group_rank_list);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		
	}

	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			groupId = intent.getStringExtra("groupId");
		}
		rbGroupRankList.setVisibility(View.GONE);
		List<Fragment> pagers = new ArrayList<Fragment>();
		infoFragment = new PublicGroupInfoFragment();
		pagers.add(infoFragment);

		SimpleViewPagerAdapter viewPagerAdapter = new SimpleViewPagerAdapter(getSupportFragmentManager(), pagers);
		viewPager.setAdapter(viewPagerAdapter);
		// 设置默认界面
		viewPager.setCurrentItem(FRAGMENT_GROUP_INFO);
		rgGroup.check(R.id.rb_group_info);
		viewPager.setOffscreenPageLimit(2);
		setListener();
		
		
		getGroupInfo();
	}

	private void setListener() {
		rbGroupInfo.setOnClickListener(this);

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}

			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case FRAGMENT_GROUP_INFO:
					rgGroup.check(R.id.rb_group_info);
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
		default:
			break;
		}
	}

	public void getGroupInfo() {
		TapcApp.getInstance().getHttpClient().getGroupInfo(groupId, new Callback() {

			@Override
			public void onSuccess(String result) {
			}

			@Override
			public void onSuccess(Object o) {
				GroupUserResponseDTO dto = (GroupUserResponseDTO) o;
				memberMap = dto.getResponse();
				memberList.clear();
				Iterator it = memberMap.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next().toString();
					memberList.add(memberMap.get(key));
				}
				handler.sendEmptyMessage(0);
			}

			@Override
			public void onStart() {
			}

			@Override
			public void onFailure(Object o) {
			}
		});
	}

	public List<GroupUserDTO> getMemberList() {
		return memberList;
	}

	public void setMemberList(List<GroupUserDTO> memberList) {
		this.memberList = memberList;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

}
