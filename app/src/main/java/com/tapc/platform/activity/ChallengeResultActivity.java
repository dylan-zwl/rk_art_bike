package com.tapc.platform.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.android.data.WorkoutInfo;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.dto.net.Callback;
import com.tapc.platform.dto.response.GetChallengeResultResponse;
import com.tapc.platform.entity.ChallengeResult;
import com.tapc.platform.entity.ChallengeTpye;
import com.tapc.platform.entity.UserInfor;
import com.tapc.platform.sportsrunctrl.SportsEnginImpl;

public class ChallengeResultActivity extends Activity {
	public static final int SPORT_RESULT = 0;
	public static final int CHALLENGE_RESULT = 1;
	@ViewInject(R.id.challenge_result_distance)
	private TextView mTextDistance;
	@ViewInject(R.id.challenge_result_time)
	private TextView mTextTime;
	@ViewInject(R.id.challenge_result_calorie)
	private TextView mTextCalorie;
	@ViewInject(R.id.challenge_result_rank)
	private TextView mRinkingTx;
	@ViewInject(R.id.challenge_result_win_user)
	private TextView mBeatPersonTx;

	@ViewInject(R.id.challenge_success)
	private RelativeLayout mChallengeSuccess;
	@ViewInject(R.id.challenge_failed)
	private RelativeLayout mChallengeFailed;

	private Activity mActivity;
	private Handler mHandler;
	private SportsEnginImpl mEnginImpl;
	private WorkoutInfo mWorkoutInfo;
	private int mSportsType;

	private String mRinking;
	private String mBeatPerson;

	private boolean isSuccess = false;

	public static void launch(Context c, String type) {
		Intent i = new Intent(c, ChallengeResultActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("type", type);
		c.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_challenge_result);
		ViewUtils.inject(this);

		mSportsType = TapcApp.getInstance().sportsType;
		mChallengeSuccess.setVisibility(View.INVISIBLE);
		mChallengeFailed.setVisibility(View.INVISIBLE);

		mEnginImpl = (SportsEnginImpl) TapcApp.getInstance().getSportsEngin();
		if (mEnginImpl.mWorkouting != null) {
			mHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case SPORT_RESULT:
						mWorkoutInfo = mEnginImpl.mWorkouting.getWorkoutInfo();
						if (mWorkoutInfo != null) {
							double distance = mWorkoutInfo.getDistance();
							long time = mWorkoutInfo.getTime();
							double calorie = mWorkoutInfo.getCalorie();
							mTextDistance.setText(String.format("%.2f", distance));
							mTextTime.setText(String.format("%02d:%02d", time / 60, time % 60));
							mTextCalorie.setText(String.format("%.2f", calorie));

							switch (mSportsType) {
							case ChallengeTpye.DISTANCE_5:
								isSuccess = isChallengeSuccess(distance, 5);
								break;
							case ChallengeTpye.DISTANCE_10:
								isSuccess = isChallengeSuccess(distance, 10);
								break;
							case ChallengeTpye.TIME_20:
								isSuccess = isChallengeSuccess(time, 20 * 60);
								break;
							default:
								break;
							}
							showResultLayout(isSuccess);
						} else {
							mHandler.sendMessageDelayed(mHandler.obtainMessage(SPORT_RESULT), 500);
						}
						break;
					case CHALLENGE_RESULT:
						if (mRinking != null) {
							mRinkingTx.setText(mRinking);
						}
						if (mBeatPerson != null) {
							mBeatPersonTx.setText(mBeatPerson);
						}
						break;
					default:
						break;
					}
				}
			};
			mHandler.sendMessageDelayed(mHandler.obtainMessage(SPORT_RESULT), 500);
		} else {
			isSuccess = false;
			showResultLayout(isSuccess);
		}
	}

	private void showResultLayout(boolean isSuccess) {
		if (isSuccess) {
			mChallengeSuccess.setVisibility(View.VISIBLE);
			mChallengeFailed.setVisibility(View.GONE);
		} else {
			mChallengeSuccess.setVisibility(View.GONE);
			mChallengeFailed.setVisibility(View.VISIBLE);
		}
		uploadData(mWorkoutInfo);
	}

	private boolean isChallengeSuccess(double runData, double goalData) {
		if (runData >= goalData) {
			return true;
		}
		return false;
	}

	@OnClick(R.id.challenge_result_close)
	protected void challenageCloseOnclick(View v) {
		finishShow();
	}

	@Override
	protected void onStop() {
		super.onStop();
		finishShow();
	}

	private void finishShow() {
		if (TapcApp.getInstance().sportsType != ChallengeTpye.NOMAL) {
			TapcApp.getInstance().sportsType = ChallengeTpye.NOMAL;
		}
		finish();
	}

	private String getTimeTampStr(long time) {
		String timeStr = String.format("%02d:%02d:%02d", time / 3600, time % 3600 / 60, time % 60);
		if (timeStr == null) {
			return "";
		}
		return timeStr;
	}

	private void uploadData(WorkoutInfo workoutInfo) {
		UserInfor userInfor = TapcApp.getInstance().userInfor;
		if (workoutInfo != null) {
			String calorie = String.format("%.2f", mWorkoutInfo.getCalorie());
			String distances = String.format("%.2f", mWorkoutInfo.getDistance());
			String costedTime = getTimeTampStr(mWorkoutInfo.getTime());
			String remainTime = getTimeTampStr(mWorkoutInfo.getRemainTime());
			String averageStep = String.format("%.2f", mWorkoutInfo.getPaceAvg());
			String heartRate = String.valueOf(mWorkoutInfo.getHeart() / mWorkoutInfo.getTime());
			TapcApp.getInstance()
					.getHttpClient()
					.saveChallengeData(userInfor.getUsername(), calorie, distances, costedTime, averageStep, heartRate,
							"", mSportsType, isSuccess, new Callback() {

								@Override
								public void onSuccess(String result) {
								}

								@Override
								public void onSuccess(Object o) {
									GetChallengeResultResponse response = (GetChallengeResultResponse) o;
									if (response != null) {
										if (response.getStatus() == 200) {
											ChallengeResult challengeResult = response.getResponse();
											mRinking = challengeResult.getRanking();
											mBeatPerson = challengeResult.getBeatperson();
											mHandler.sendMessageDelayed(mHandler.obtainMessage(CHALLENGE_RESULT), 0);
										}
									}
								}

								@Override
								public void onStart() {
								}

								@Override
								public void onFailure(Object o) {
									Toast.makeText(mActivity, getString(R.string.toast_connect_server_failure),
											Toast.LENGTH_SHORT).show();
								}
							});
		}
	}
}
