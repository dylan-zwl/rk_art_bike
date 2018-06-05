package com.tapc.platform.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.android.data.Enum.ProgramType;
import com.tapc.android.data.SystemSettingsHelper;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.adpater.ChallengeListAdapter;
import com.tapc.platform.dto.net.Callback;
import com.tapc.platform.dto.response.GetChallengeRankResponse;
import com.tapc.platform.entity.ChallengeRank;
import com.tapc.platform.entity.ChallengeTpye;
import com.tapc.platform.entity.FitnessSetAllEntity;
import com.tapc.platform.entity.RankingInfo;
import com.tapc.platform.workout.MessageType;

public class ChooseChallengeModeActivity extends Activity {
	private static final String MODE = "mode";
	private static final int Reflesh = 0;

	@ViewInject(R.id.challenge_rank_listview)
	private ListView mChallengeRanklv;

	@ViewInject(R.id.challenge_mode_5)
	private Button mChallengeMode5;
	@ViewInject(R.id.challenge_mode_10)
	private Button mChallengeMode10;
	@ViewInject(R.id.challenge_mode_time_20)
	private Button mChallengeModeTime20;

	@ViewInject(R.id.challenge_people_count)
	private TextView mPeopleCount;
	@ViewInject(R.id.challenge_success_percent)
	private TextView mSuccessPercent;
	@ViewInject(R.id.challenge_failed_percent)
	private TextView mFailedPercent;

	private List<RankingInfo> mList;
	private ChallengeListAdapter mAdapter;
	private FitnessSetAllEntity mAllEntity;
	private Handler mHandler;

	private int mChallengeMode;
	private String mNumberOfpeopleStr;
	private String mSuccessPercentStr;
	private String mFailedPercentStr;

	private ChallengeRank mModeD5Rank;
	private ChallengeRank mModeD10Rank;
	private ChallengeRank mModeT20Rank;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_challenge);
		ViewUtils.inject(this);
		initView();
	}

	private void initView() {
		TapcApp.getInstance().sportsDataDao.query();

		challengeModeOnclick(mChallengeMode5);

		mList = new ArrayList<RankingInfo>();
		mAdapter = new ChallengeListAdapter(this, mList);
		mAdapter.setMode(mChallengeMode);
		mChallengeRanklv.setAdapter(mAdapter);

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case Reflesh:
					if (mNumberOfpeopleStr != null) {
						mPeopleCount.setText(mNumberOfpeopleStr);
					}
					if (mSuccessPercentStr != null) {
						mSuccessPercent.setText(mSuccessPercentStr + " %");
					}
					if (mFailedPercentStr != null) {
						mFailedPercent.setText(mFailedPercentStr + " %");
					}
					mAdapter.setMode(mChallengeMode);
					mAdapter.notifyDataSetChanged(mList);
					break;
				default:
					break;
				}
			}
		};
	}

	@OnClick({ R.id.challenge_mode_5, R.id.challenge_mode_10, R.id.challenge_mode_time_20 })
	protected void challengeModeOnclick(View view) {
		mChallengeMode5.setBackgroundResource(R.drawable.challenge_mode_5_d);
		mChallengeMode10.setBackgroundResource(R.drawable.challenge_mode_10_d);
		mChallengeModeTime20.setBackgroundResource(R.drawable.challenge_mode_time_20_d);
		switch (view.getId()) {
		case R.id.challenge_mode_5:
			mChallengeMode = ChallengeTpye.DISTANCE_5;
			view.setBackgroundResource(R.drawable.challenge_mode_5_u);
			break;
		case R.id.challenge_mode_10:
			mChallengeMode = ChallengeTpye.DISTANCE_10;
			view.setBackgroundResource(R.drawable.challenge_mode_10_u);
			break;
		case R.id.challenge_mode_time_20:
			mChallengeMode = ChallengeTpye.TIME_20;
			view.setBackgroundResource(R.drawable.challenge_mode_time_20_u);
			break;
		default:
			break;
		}
		getRankList(mChallengeMode);
	}

	@OnClick(R.id.challenge_starts)
	protected void startChallengeOnclick(View v) {
		mAllEntity = new FitnessSetAllEntity();
		if (mAllEntity != null) {
			switch (mChallengeMode) {
			case ChallengeTpye.DISTANCE_5:
				mAllEntity.setDistanceEntity(ProgramType.DISTANCE, 5, SystemSettingsHelper.WEIGHT,
						SystemSettingsHelper.DEFAULT_INCLINE, SystemSettingsHelper.DEFAULT_SPEED);
				break;
			case ChallengeTpye.DISTANCE_10:
				mAllEntity.setDistanceEntity(ProgramType.DISTANCE, 10, SystemSettingsHelper.WEIGHT,
						SystemSettingsHelper.DEFAULT_INCLINE, SystemSettingsHelper.DEFAULT_SPEED);
				break;
			case ChallengeTpye.TIME_20:
				mAllEntity.setMinutesEntity(ProgramType.TIME, 20, SystemSettingsHelper.WEIGHT,
						SystemSettingsHelper.DEFAULT_INCLINE, SystemSettingsHelper.DEFAULT_SPEED);
				break;
			default:
				break;
			}
			// SystemSettingsHelper.COOLDOWN_TIME = 0;
			TapcApp.getInstance().getSportsEngin().openFitness(mAllEntity);
			TapcApp.getInstance().sportsType = mChallengeMode;
			TapcApp.getInstance().sendUIMessage(MessageType.MSG_UI_MAIN_START);
			Intent intent = new Intent();
			intent.setClass(this, MainActivity.class);
			startActivity(intent);
		}
	}

	private void getRankList(final int mode) {
		ChallengeRank challengeRank = null;
		switch (mode) {
		case ChallengeTpye.DISTANCE_5:
			if (mModeD5Rank != null) {
				challengeRank = mModeD5Rank;
			}
			break;
		case ChallengeTpye.DISTANCE_10:
			if (mModeD10Rank != null) {
				challengeRank = mModeD10Rank;
			}
			break;
		case ChallengeTpye.TIME_20:
			if (mModeT20Rank != null) {
				challengeRank = mModeT20Rank;
			}
			break;
		default:
			break;
		}

		String userName = TapcApp.getInstance().userInfor.getToken();
		if (userName != null && !userName.isEmpty()) {
			if (challengeRank == null) {
				TapcApp.getInstance().getHttpClient().getChallengeData(userName, mode, 1, 100, new Callback() {
					@Override
					public void onStart() {
					}

					@Override
					public void onSuccess(Object o) {
						GetChallengeRankResponse response = (GetChallengeRankResponse) o;
						if (response != null && response.getStatus() == 200) {
							ChallengeRank challengeRank = response.getResponse();
							setRankShow(challengeRank);
							switch (mode) {
							case ChallengeTpye.DISTANCE_5:
								if (mModeD5Rank == null) {
									mModeD5Rank = challengeRank;
								}
								break;
							case ChallengeTpye.DISTANCE_10:
								if (mModeD10Rank == null) {
									mModeD10Rank = challengeRank;
								}
								break;
							case ChallengeTpye.TIME_20:
								if (mModeT20Rank == null) {
									mModeT20Rank = challengeRank;
								}
								break;
							default:
								break;
							}
						}
						if (response.getStatus() == 301) {
						}
						if (response.getStatus() == 401) {
						}
						if (response.getStatus() == 501) {
						}
					}

					@Override
					public void onSuccess(String result) {
					}

					@Override
					public void onFailure(Object o) {
					}
				});
			} else {
				setRankShow(challengeRank);
			}
		}
	}

	private void setRankShow(ChallengeRank challengeRank) {
		mNumberOfpeopleStr = challengeRank.getPeople();
		setChallengePeople(mNumberOfpeopleStr);

		mSuccessPercentStr = challengeRank.getSuccesspercent();
		setSuccessPercent(mSuccessPercentStr);
		setFailedPercent(mSuccessPercentStr);

		mList = challengeRank.getRankresponse();
		if (mList != null) {
			mHandler.sendEmptyMessage(Reflesh);
		}
	}

	private void setChallengePeople(String numberOfpeople) {
		mNumberOfpeopleStr = numberOfpeople;
	}

	private void setSuccessPercent(String successPercent) {
		int successPercentInt = (int) (Float.valueOf(successPercent) * 100);
		mSuccessPercentStr = mFailedPercentStr = String.valueOf(successPercentInt);
	}

	private void setFailedPercent(String successPercent) {
		int successPercentInt = (int) (Float.valueOf(successPercent) * 100);
		int failPercent = 100 - successPercentInt / 100;
		mFailedPercentStr = String.valueOf(failPercent);
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
}
