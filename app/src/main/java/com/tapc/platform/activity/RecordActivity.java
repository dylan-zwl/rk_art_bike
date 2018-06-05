package com.tapc.platform.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.adpater.UserRecordAdapter;
import com.tapc.platform.dto.net.Callback;
import com.tapc.platform.dto.response.GetUserFitnessResponse;
import com.tapc.platform.dto.response.UserRecordResponse;
import com.tapc.platform.sql.SportRecordItem;
import com.tapc.platform.utils.SysUtils;

public class RecordActivity extends BaseActivity {
	@ViewInject(R.id.record_listview)
	private ListView mRecordList;

	@ViewInject(R.id.statistics_text)
	private TextView mStatistics;
	@ViewInject(R.id.all_time)
	private TextView mAllTime;
	@ViewInject(R.id.all_distance)
	private TextView mAllDistance;
	@ViewInject(R.id.all_calorie)
	private TextView mAllCalorie;

	private int mAlltimes;
	private double mAllDistances;
	private double mAllCalories;

	private List<SportRecordItem> mList;
	private UserRecordAdapter mAdapter;
	private int mIndex = 1;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_record);
		ViewUtils.inject(this);
		initView();
	}

	private void initView() {
		String userName = TapcApp.getInstance().userInfor.getUsername();
		if (Config.SERVICE_ID == 0) {
			mList = TapcApp.getInstance().sportsDataDao.getRecord(userName);
			if (mList != null) {
				mAdapter = new UserRecordAdapter(RecordActivity.this, mList);
				mRecordList.setAdapter(mAdapter);
				for (SportRecordItem item : mList) {
					mAlltimes = mAlltimes + Integer.valueOf(item.getSportsData().getRuntime());
					mAllDistances = mAllDistances + Double.valueOf(item.getSportsData().getDistance());
					mAllCalories = mAllCalories + Double.valueOf(item.getSportsData().getCalories());
				}
				if (mAlltimes < 60) {
					mAllTime.setText(Integer.toString(mAlltimes) + " Sec");
				} else {
					mAllTime.setText(Integer.toString(mAlltimes / 60) + " Min");
				}
				mAllDistance.setText(String.format("%.2f", mAllDistances) + " Km");
				mAllCalorie.setText(String.format("%.2f", mAllCalories) + " Kcal");
				mStatistics.setText(getString(R.string.record_statistics));
			}
		} else if (Config.SERVICE_ID == 1) {
			getUserFitnessData(1, 10);
		}
	}

	private Handler mShowRecord = new Handler() {
		public void handleMessage(Message msg) {
			if (mList != null && mList.size() > 0) {
				for (SportRecordItem item : mList) {
					String timeStr = item.getSportsData().getRuntime();
					int min = Integer.valueOf(timeStr.substring(0, timeStr.indexOf(":"))) * 60;
					int sec = Integer.valueOf(timeStr.substring(timeStr.indexOf(":") + 1, timeStr.length()));
					mAlltimes = mAlltimes + min + sec;
					mAllDistances = mAllDistances + Double.valueOf(item.getSportsData().getDistance());
					mAllCalories = mAllCalories + Double.valueOf(item.getSportsData().getCalories());
				}
				if (mAlltimes % 60 < 9) {
					mAllTime.setText(mAlltimes / 60 + ":0" + mAlltimes % 60 + " Min");
				} else {
					mAllTime.setText(mAlltimes / 60 + ":" + mAlltimes % 60 + " Min");
				}
				mAllDistance.setText(String.format("%.2f", mAllDistances) + " Km");
				mAllCalorie.setText(String.format("%.2f", mAllCalories) + " Kcal");
				mStatistics.setText(getString(R.string.record_statistics));
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
			}
		};
	};

	/**
	 * 获取用户数据
	 */
	private void getUserFitnessData(int start, int end) {
		String token = TapcApp.getInstance().userInfor.getToken();
		if (token.isEmpty()) {
			return;
		}
		TapcApp.getInstance().getHttpClient().getUserFitnessData(token, start, end, new Callback() {

			@Override
			public void onSuccess(Object o) {
				GetUserFitnessResponse response = (GetUserFitnessResponse) o;
				if (response.getStatus() == 200) {
					if (mList == null) {
						mList = new ArrayList<SportRecordItem>();
						initList();
					}
					ArrayList<UserRecordResponse> infoEntries = response.getResponse();
					if (infoEntries != null && infoEntries.size() > 0) {
						for (UserRecordResponse userFitnessInfoEntry : infoEntries) {
							SportRecordItem item = new SportRecordItem();
							String dateTime = SysUtils.getDataTimeStr("yyyy-MM-dd",
									Long.parseLong(userFitnessInfoEntry.getRunDate()));
							item.setDateTime(dateTime);
							item.getSportsData().setRuntime(userFitnessInfoEntry.getMinute());
							item.getSportsData().setDistance(userFitnessInfoEntry.getDuration());
							item.getSportsData().setCalories(userFitnessInfoEntry.getCalorie());
							mList.add(item);
						}
						mShowRecord.sendEmptyMessage(0);
					}
				} else if (response.getStatus() == 301) {

				} else if (response.getStatus() == 501) {
					Toast.makeText(RecordActivity.this, getString(R.string.toast_connect_server_failure),
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onStart() {
			}

			@Override
			public void onFailure(Object o) {
			}

			@Override
			public void onSuccess(String result) {
			}
		});
	}

	private void initList() {
		mAdapter = new UserRecordAdapter(RecordActivity.this, mList);
		mRecordList.setAdapter(mAdapter);
		mRecordList.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				// 当不滚动时
				case OnScrollListener.SCROLL_STATE_IDLE:
					// 判断滚动到底部
					if (mRecordList.getLastVisiblePosition() == (mRecordList.getCount() - 1)) {
						mIndex = mIndex + 1;
						getUserFitnessData((mIndex * 10 + 1), 10);
					}
					break;
				}
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
}
