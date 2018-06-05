package com.tapc.platform.activity;

/**
 * ScenePlayActivity.java[v 1.0.0]
 * classes:com.jht.tapc.platform.activity.ScenePlayActivity
 * fch Create of at 2015�?3�?20�? 下午2:10:24
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnLongClick;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.entity.BikeCtlType;
import com.tapc.platform.media.PlayEntity;
import com.tapc.platform.media.VaPlayer;
import com.tapc.platform.media.VaPlayer.handleType;
import com.tapc.platform.utils.SysUtils;
import com.tapc.platform.workout.MessageType;

/**
 * classes:com.jht.tapc.platform.activity.ScenePlayActivity
 * 
 * @author fch <br/>
 *         Create of at 2015�?3�?20�? 下午2:10:24
 */
public class ScenePlayActivity extends BaseActivity implements View.OnTouchListener {
	private static final int START_PLAY = 9;
	private static final int PLAYING = 10;
	private static final int ADD_TOUCH = 11;
	private static final int SUB_TOUCH = 12;
	@ViewInject(R.id.surface_view)
	private SurfaceView mSurfaceView;
	// ��Ļ
	@ViewInject(R.id.videoMessage)
	private TextView mVideoMessage;

	@ViewInject(R.id.play_layout)
	private RelativeLayout mPlayLayout;

	@ViewInject(R.id.show_or_hide_text_iv)
	private ImageView show_or_hide_text_iv;

	@ViewInject(R.id.full_switch)
	private ImageView full_switch;

	@ViewInject(R.id.videoname)
	private TextView mVideoName;

	@ViewInject(R.id.videodepiction)
	private TextView mVideoDepiction;

	@ViewInject(R.id.addBtn)
	private ImageButton mVaSpeedInc;
	@ViewInject(R.id.minusBtn)
	private ImageButton mVaSpeedDec;
	@ViewInject(R.id.valueText)
	private TextView mValueType;

	private VaPlayer mPlayer;

	private boolean isFullScreen = false;
	private boolean isLongTouch = true;
	private boolean isVideoPlayPause = false;
	private int delayTime = 150;
	private int speedTimeCount = 0;

	private RelativeLayout.LayoutParams mPlayParams;

	public static void launch(Context c, PlayEntity entity) {
		Intent i = new Intent(c, ScenePlayActivity.class);
		i.putExtra("sceneEntity", entity);
		c.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_scene_play);
		ViewUtils.inject(this);
		mVaSpeedInc.setOnTouchListener(this);
		mVaSpeedDec.setOnTouchListener(this);
		if (Config.sBikeCtlType == BikeCtlType.LOAD) {
			mValueType.setText(R.string.incline);
		} else {
			mValueType.setText(R.string.speed);
		}

		if (!TapcApp.getInstance().getSportsEngin().isRunning()) {
			TapcApp.getInstance().sendUIMessage(MessageType.MSG_UI_MAIN_START);
		}
		mVaPlayerHandler.sendMessageDelayed(mVaPlayerHandler.obtainMessage(START_PLAY), 500);
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtils.e("场景跑销毁");
		if (mPlayer != null) {
			TapcApp.getInstance().vaRecordPosition = mPlayer.getPlayPosition();
			mPlayer.stop();
			mPlayer = null;
		}
	}

	@OnClick(R.id.change_play_list)
	private void changePlayListClick(View v) {
		SceneRunActivity.launch(this);
		finish();
	}

	/**
	 * 点击全屏
	 * 
	 * @param v
	 */
	@OnClick(R.id.full_switch)
	private void fullSwitch(View v) {
		if (isFullScreen) {
			isFullScreen = false;
			mPlayLayout.setPadding(0, 0, 0, 0);
			mPlayLayout.setLayoutParams(mPlayParams);
			full_switch.setImageResource(R.drawable.b_97);
		} else {
			isFullScreen = true;
			mPlayLayout.setPadding(0, 0, 0, 0);
			mPlayParams = (RelativeLayout.LayoutParams) mPlayLayout.getLayoutParams();
			mPlayLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT));
			full_switch.setImageResource(R.drawable.unfullscreen);
		}
	}

	@OnClick(R.id.show_or_hide_text_iv)
	private void setVideoMassage(View v) {
		if (mVideoMessage.isShown()) {
			mVideoMessage.setVisibility(View.GONE);
			show_or_hide_text_iv.setBackgroundResource(R.drawable.scene_msg_hide);
		} else {
			mVideoMessage.setVisibility(View.VISIBLE);
			show_or_hide_text_iv.setBackgroundResource(R.drawable.j_03);
		}
	}

	private void init() {
		PlayEntity playEntity = TapcApp.getInstance().getSportsEngin().getScene();
		mVideoName.setText(playEntity.name);
		mVideoDepiction.setText(playEntity.description);

		mPlayer = new VaPlayer(mSurfaceView.getHolder(), mVaPlayerHandler);
		if (TapcApp.getInstance().getSportsEngin().getScene().getPath().equals(Config.VA_FILE_PATH_SD)) {
			mPlayer.SetVaPath(Config.VA_FILE_PATH_SD);
		} else if (TapcApp.getInstance().getSportsEngin().getScene().getPath().equals(Config.VA_FILE_PATH_NAND)) {
			mPlayer.SetVaPath(Config.VA_FILE_PATH_NAND);
		}
		mPlayer.init();
		mPlayer.setBackMusicVisibility(false);
		mPlayer.setShowMsgTime(5000);
		mPlayer.setVideoSpeedValue(TapcApp.getInstance().mainActivity.getSpeedCtrl().getCtlValue());
		mPlayer.setPlayPosition(TapcApp.getInstance().vaRecordPosition);
		mPlayer.start(playEntity);
	}

	@SuppressLint("HandlerLeak")
	Handler mVaPlayerHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case handleType.SHOW_MSG:
				mVideoMessage.setText(msg.getData().getString(handleType.DATA_KEY));
				break;
			case handleType.HIDE_MSG:
				mVideoMessage.setText("");
				break;
			case handleType.VEDIO_FINISH:
				if (mPlayer != null) {
					mPlayer.stop();
					mPlayer = null;
				}
				finish();
				TapcApp.getInstance().sendUIMessage(MessageType.MSG_UI_MAIN_STOP);
				break;
			case handleType.SET_INCLINE:
				if (Config.sBikeCtlType == BikeCtlType.LOAD) {
					double incline = msg.getData().getDouble(handleType.DATA_KEY);
					incline = incline * 2;
					if (incline < TapcApp.MIN_INCLINE) {
						incline = TapcApp.MIN_INCLINE;
					} else if (incline > TapcApp.MAX_INCLINE) {
						incline = TapcApp.MAX_INCLINE;
					}
					TapcApp.getInstance().mainActivity.getInclineCtrl().setCtlValue(incline);
				}
				break;
			case START_PLAY:
				if (TapcApp.getInstance().getSportsEngin().isRunning()) {
					TapcApp.getInstance().menuBar.showOSD(true);
					init();
					mVaPlayerHandler.sendMessageDelayed(mVaPlayerHandler.obtainMessage(PLAYING), 1000);
					isVideoPlayPause = false;
				} else {
					mVaPlayerHandler.sendMessageDelayed(mVaPlayerHandler.obtainMessage(START_PLAY), 1000);
				}
				break;
			case PLAYING:
				if (mPlayer != null) {
					double speed = SysUtils.formatDouble(TapcApp.getInstance().mainActivity.getBikeSpeed());
					if (mPlayer.getPlaySpeed() != speed) {
						if (speedTimeCount >= 10) {
							speedTimeCount = 0;
							mPlayer.setPlaySpeed(speed);
						} else {
							speedTimeCount++;
						}
					}
					if (TapcApp.getInstance().getSportsEngin().isPause()) {
						if (!isVideoPlayPause) {
							isVideoPlayPause = true;
							mPlayer.setPause(isVideoPlayPause);
						}
					} else {
						if (isVideoPlayPause) {
							isVideoPlayPause = false;
							mPlayer.setPause(isVideoPlayPause);
						}
						// if (speed <= 0) {
						// mPlayer.setPause(true);
						// } else {
						// mPlayer.setPause(false);
						// }
					}
				}
				mVaPlayerHandler.sendMessageDelayed(mVaPlayerHandler.obtainMessage(PLAYING), 1000);
				break;

			case ADD_TOUCH:
				if (isLongTouch) {
					addClick(null);
					mVaPlayerHandler.sendMessageDelayed(mVaPlayerHandler.obtainMessage(ADD_TOUCH), delayTime);
				}
				break;
			case SUB_TOUCH:
				if (isLongTouch) {
					minutClick(null);
					mVaPlayerHandler.sendMessageDelayed(mVaPlayerHandler.obtainMessage(SUB_TOUCH), delayTime);
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@OnClick(R.id.addBtn)
	private void addClick(View v) {
		if (mPlayer != null) {
			if (Config.sBikeCtlType == BikeCtlType.LOAD) {
				TapcApp.getInstance().mainActivity.getInclineCtrl().addClick(null);
			} else {
				TapcApp.getInstance().mainActivity.getSpeedCtrl().addClick(null);
			}
		}
	}

	@OnClick(R.id.minusBtn)
	private void minutClick(View v) {
		if (mPlayer != null) {
			if (Config.sBikeCtlType == BikeCtlType.LOAD) {
				TapcApp.getInstance().mainActivity.getInclineCtrl().subClick(null);
			} else {
				TapcApp.getInstance().mainActivity.getSpeedCtrl().subClick(null);
			}
		}
	}

	@OnLongClick(R.id.addBtn)
	protected boolean subLongClick(View v) {
		isLongTouch = true;
		mVaPlayerHandler.sendMessageDelayed(mVaPlayerHandler.obtainMessage(ADD_TOUCH), delayTime);
		return true;
	}

	@OnLongClick(R.id.minusBtn)
	protected boolean addLongClick(View v) {
		isLongTouch = true;
		mVaPlayerHandler.sendMessageDelayed(mVaPlayerHandler.obtainMessage(SUB_TOUCH), delayTime);
		return true;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			isLongTouch = false;
		}
		return false;
	}
}
