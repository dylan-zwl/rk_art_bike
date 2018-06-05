package com.tapc.platform.media;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

import com.tapc.android.data.SystemSettingsHelper;
import com.tapc.platform.TapcApp;
import com.tapc.platform.utils.SysUtils;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class VaPlayer {
	public static final String TAG = "VaPlayer";
	private static final int mVideoMaxSpeed = 200;
	private static final int mVideoMinSpeed = 100;
	public boolean PlayFlag = false;
	public List<PlayEntity> VideoList;

	private String mVaPath;
	private String mBackMusicFileName = "music";
	private String mInfoFileName = "info";
	private String mPlaylistFileName = "playlist";
	private VaInfor mVaInfor;
	private int mMsgShowTime = 4000;
	private float mVolume = 1.0f;
	private int mBackMusicIndex = 0;
	private boolean haveBackMusic = false;
	private boolean isCanSetVideo = false;
	private List<String> mBackMusicList;

	private PlayEntity nowVaPlayVideo;
	private int mVideoIndex = 0;

	public MediaPlayer mMediaPlayer = null;
	private MediaPlayer mVoicePlayer = null;
	private MediaPlayer mBackMusic = null;
	private VoiceThread mvoiceThread = null;
	private Handler mVaPlayerHandler;
	private Handler mSendMsgHandler;

	private SurfaceHolder mSurfaceHolder;
	private double mVideoSpeed = 0;
	private int mMachineIncline = 0;
	private int mLevelID = 1;
	private int mVaLanguage = 1;
	private int ORIGIN_INCLINE = 0;
	private int mCurrentPosition = -1;

	public VaPlayer(SurfaceHolder surfaceHolder, Handler messgeHandler) {
		mSendMsgHandler = messgeHandler;

		mSurfaceHolder = surfaceHolder;
		mVideoIndex = 0;

	}

	public boolean SetVaPath(String path) {
		if (!(new File(path)).exists()) {
			return false;
		}
		this.mVaPath = path;
		return true;
	}

	public String GetVaPath() {
		return mVaPath;
	}

	public void setShowMsgTime(int time) {
		this.mMsgShowTime = time;
	}

	@SuppressLint("HandlerLeak")
	public boolean init() {
		getVaInfor();
		// if (!getVideoList()) {
		// return false;
		// }

		mVaPlayerHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case handleType.SHOW_MSG:
					String data = msg.getData().getString(handleType.DATA_KEY);
					sendHandleMsg(mVaPlayerHandler, handleType.HIDE_MSG, "", mMsgShowTime);
					sendHandleMsg(mSendMsgHandler, handleType.SHOW_MSG, data, 0);
					break;
				case handleType.HIDE_MSG:
					sendHandleMsg(mSendMsgHandler, handleType.HIDE_MSG, "", 0);
					break;
				case handleType.PLAY_VOICE:
					playVideoVoice(msg.getData().getString("data"));
					break;
				case handleType.PLAY_VOIDEO:
					playVideo(mVideoIndex);
					break;
				case handleType.SET_VIDEO_SPEED:
					double speed = mVideoSpeed;
					mVideoSpeed = 0;
					setPlaySpeed(speed);
					break;
				case handleType.SET_INCLINE:
					setMachineIncline(mMachineIncline);
					break;
				case handleType.SET_VIDEO_SURFACE:
					if (mSurfaceHolder.getSurface().isValid()) {
						isCanSetVideo = false;
						mMediaPlayer.setDisplay(mSurfaceHolder);
						isCanSetVideo = true;
					}
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			};
		};
		getBackMusic();
		return true;
	}

	public VaInfor getVaInfor() {
		try {
			if (mVaInfor == null) {
				mVaInfor = new VaInfor();
			}
			InputStream Inforxml = new FileInputStream(mVaPath + "/" + mInfoFileName);
			mVaInfor = ValUtil.getvaInfor(Inforxml);
			Inforxml.close();
			Log.d(TAG, "infor : " + " title:" + mVaInfor.title + " version:" + mVaInfor.version + " minvideospeed:"
					+ mVaInfor.minvideospeed + " maxvideospeed:" + mVaInfor.maxvideospeed + " startspeed:"
					+ mVaInfor.startspeed + " stopspeed:" + mVaInfor.stopspeed + " startrpm:" + mVaInfor.startrpm
					+ " name:" + mVaInfor.name + mVaInfor.startrpm + " key:" + mVaInfor.key);
		} catch (Exception e) {
			mVaInfor = null;
			e.printStackTrace();
		}
		return mVaInfor;
	}

	public boolean getVideoList() {
		VideoList = new ArrayList<PlayEntity>();
		try {
			File FilePath = new File(mVaPath);
			ArrayList<String> List = new ArrayList<String>();
			File[] Files = FilePath.listFiles(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isFile() && (f.getName().startsWith(mPlaylistFileName));
				}
			});
			if (Files != null) {
				if (Files.length > 0) {
					for (int nums = 0; nums < Files.length; nums++) {
						List.add(Files[nums].getAbsolutePath());
					}
				}
				Collections.sort(List);
				for (String FileDiretory : List) {
					InputStream PlayListxml = new FileInputStream(FileDiretory);

					Log.d("playlistFile ", FileDiretory);
					VaPlayListAnalysis mvaPlayListAnalysis = new VaPlayListAnalysis();
					PlayEntity mvaPlayList = new PlayEntity();
					mvaPlayList = mvaPlayListAnalysis.getvaInfor(PlayListxml);
					Log.d("PlayList", " name:" + mvaPlayList.name + " description:" + mvaPlayList.description
							+ " location:" + mvaPlayList.location + " still:" + mvaPlayList.still + " uniqueid:"
							+ mvaPlayList.uniqueid);
					for (String Evtstr : mvaPlayList.evtList) {
						Log.d("PlayList Evt", Evtstr);
					}
					VideoList.add(mvaPlayList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			VideoList = null;
		}
		if (VideoList == null)
			return false;
		return true;
	}

	private void playVideo(int VideoIndex) {
		try {
			isCanSetVideo = false;
			String EvtFile = mVaPath + nowVaPlayVideo.evtList.get(VideoIndex);
			InputStream EvtInputStream = new FileInputStream(EvtFile);
			CvaFactory cvafactory = new CvaFactory();
			CvaVideo video = new CvaVideo();
			String videoDirectory = EvtFile.substring(0, EvtFile.lastIndexOf("/") + 1);
			video.setPath(videoDirectory);
			cvafactory.DeserializeVideo(EvtInputStream, EvtInputStream.available(), video);
			EvtInputStream.close();
			video.setLevel(mLevelID);
			if (mMediaPlayer == null) {
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mplayer) {
						stopVoiceThread();
						mMediaPlayer.release();
						mMediaPlayer = null;
						mCurrentPosition = -1;
						if (PlayFlag) {
							mVideoIndex++;
							if (mVideoIndex < nowVaPlayVideo.evtList.size()) {
								sendHandleMsg(mVaPlayerHandler, handleType.PLAY_VOIDEO, "", 500);
							} else {
								mVideoIndex = 0;
								sendHandleMsg(mVaPlayerHandler, handleType.PLAY_VOIDEO, "", 500);
							}
						}
					}
				});
				mMediaPlayer.setOnErrorListener(new OnErrorListener() {

					@Override
					public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
						Log.d("mMediaPlayer", "Play " + nowVaPlayVideo.evtList.get(mVideoIndex) + " error");
						return false;
					}
				});
			}
			mMediaPlayer.reset();
			mMediaPlayer.setDisplay(mSurfaceHolder);
			mMediaPlayer.setDataSource(video.getPath() + "/" + video.getFileName());
			Log.d("play video", video.getPath() + video.getFileName());
			mMediaPlayer.setVolume(0.0f, 0.0f);
			mMediaPlayer.prepare();
			if (mCurrentPosition != -1) {
				Log.d("seek to  video CurrentPosition", "" + mCurrentPosition / 1000);
				mMediaPlayer.seekTo(mCurrentPosition);
			}
			mMediaPlayer.start();
			if (mVoicePlayer != null) {
				mVoicePlayer.start();
			}
			mvoiceThread = new VoiceThread(video);
			mvoiceThread.start();
			isCanSetVideo = true;
			sendHandleMsg(mVaPlayerHandler, handleType.SET_VIDEO_SPEED, "", 500);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setVideoSurface() {
		sendHandleMsg(mVaPlayerHandler, handleType.SET_VIDEO_SURFACE, "", 300);
	}

	public int getVideoCount() {
		return VideoList.size();
	}

	private void playVideoVoice(String filePath) {
		try {
			if (mVoicePlayer == null) {
				mVoicePlayer = new MediaPlayer();
				mVoicePlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						mVoicePlayer.release();
						mVoicePlayer = null;
						Log.d(TAG, "Voice play end");
					}
				});
				mVoicePlayer.setOnErrorListener(new OnErrorListener() {

					@Override
					public boolean onError(MediaPlayer mplyer, int arg1, int arg2) {
						Log.d(TAG, "Voice play error");
						if (mVoicePlayer != null) {
							mVoicePlayer.release();
							mVoicePlayer = null;
						}
						return false;
					}
				});
			}
			mVoicePlayer.reset();
			mVoicePlayer.setDataSource(filePath);
			mVoicePlayer.setVolume(mVolume, mVolume);
			mVoicePlayer.prepare();
			mVoicePlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void playBackMusic() {
		try {
			if (mBackMusic == null) {
				mBackMusic = new MediaPlayer();
				mBackMusic.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer arg0) {
						if (PlayFlag) {
							playBackMusic();
						}
					}
				});
				mBackMusic.setOnErrorListener(new OnErrorListener() {

					@Override
					public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
						if (PlayFlag) {
							playBackMusic();
						}
						return false;
					}
				});
			}
			mBackMusic.reset();
			if (mBackMusicIndex++ >= mBackMusicList.size()) {
				mBackMusicIndex = 0;
			}
			mBackMusic.setDataSource(mBackMusicList.get(mBackMusicIndex));
			setBackMusicVisibility(haveBackMusic);
			mBackMusic.prepare();
			mBackMusic.start();
			Log.d(TAG, "Back Music next play : " + mBackMusicList.get(mBackMusicIndex));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getBackMusic() {
		File FilePath = new File(mVaPath + "/" + mBackMusicFileName);
		mBackMusicList = new ArrayList<String>();
		File[] Files = FilePath.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isFile();
			}
		});
		if (Files != null && Files.length > 0) {
			for (int nums = 0; nums < Files.length; nums++) {
				mBackMusicList.add(Files[nums].getAbsolutePath());
			}
		}
	}

	public int getBackMusicCount() {
		return mBackMusicList.size();
	}

	public void setVolume(float volume) {
		mVolume = volume;
		setPlyerVolume(mVoicePlayer);
		setPlyerVolume(mBackMusic);
	}

	private void setPlyerVolume(MediaPlayer mediaplayer) {
		if (mediaplayer != null) {
			mediaplayer.setVolume(mVolume, mVolume);
		}
	}

	public boolean setLevel(int Level) {
		mLevelID = Level;
		return true;
	}

	public int getLevel() {
		return mLevelID;
	}

	public int getLanguage() {
		return mVaLanguage;
	}

	public void setLanguage(int Language) {
		mVaLanguage = Language;
	}

	private void stopVoiceThread() {
		if (mvoiceThread != null) {
			while (!mvoiceThread.Stop())
				;
			mvoiceThread = null;
		}
	}

	public void start(PlayEntity vaPlay) {
		ORIGIN_INCLINE = (int) vaPlay.getGradient() + (int) SystemSettingsHelper.ORIGIN_INCLINE;
		stopVoiceThread();
		PlayFlag = true;
		nowVaPlayVideo = vaPlay;
		playVideo(mVideoIndex);
		// playBackMusic();
	}

	public void stop() {
		mCurrentPosition = -1;
		mVideoIndex = 0;
		stopVoiceThread();
		playerStop(mMediaPlayer);
		playerStop(mVoicePlayer);
		playerStop(mBackMusic);
		PlayFlag = false;
	}

	private void playerStop(MediaPlayer mediaplayer) {
		if (mediaplayer != null) {
			mediaplayer.release();
			mediaplayer = null;
		}
	}

	public void setPause(boolean isPause) {
		PlayFlag = !isPause;
		playerPause(mMediaPlayer, PlayFlag);
		playerPause(mVoicePlayer, PlayFlag);
		playerPause(mBackMusic, PlayFlag);
	}

	private void playerPause(MediaPlayer mediaplayer, boolean playFlag) {
		if (mediaplayer != null) {
			if (playFlag) {
				if (!mediaplayer.isPlaying()) {
					mediaplayer.start();
				}
			} else {
				if (mediaplayer.isPlaying()) {
					mediaplayer.pause();
				}
			}

		}
	}

	public double getPlaySpeed() {
		return mVideoSpeed;
	}

	public void setVideoSpeedValue(double speed) {
		mVideoSpeed = speed;
	}

	public void setPlaySpeed(double speed) {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			// double minspeed =
			// SystemSettingsHelper.getFloat(SystemSettingsHelper.KEY_MIN_SPEED);
			// double maxspeed =
			// SystemSettingsHelper.getFloat(SystemSettingsHelper.KEY_MAX_SPEED);
			double minspeed = 0;
			double maxspeed = 80;
			int videoSpeed = mVideoMinSpeed
					+ (int) (((speed - minspeed) / (maxspeed - minspeed)) * (mVideoMaxSpeed - mVideoMinSpeed));
			if (videoSpeed > mVideoMaxSpeed) {
				videoSpeed = mVideoMaxSpeed;
			} else if (videoSpeed < videoSpeed) {
				videoSpeed = mVideoMinSpeed;
			}
			if (mVideoSpeed != speed) {
				Log.d("videoSpeed : " + videoSpeed, "speed : " + speed + " maxspeed:" + maxspeed + " minspeed:"
						+ minspeed);
				if (isCanSetVideo) {
					mMediaPlayer.pause();
					if (mMediaPlayer.setPlayerSpeed(videoSpeed) < 0) {
						Log.d("set video speed", "false");
					}
					mMediaPlayer.start();
					Log.d("set video speed ( " + videoSpeed + " ) video CurrentPosition ",
							"" + mMediaPlayer.getCurrentPosition() / 1000);
				}
				mVideoSpeed = speed;
			}
		}
	}

	private void setMachineIncline(int videoIncline) {
		if (TapcApp.getInstance().getSportsEngin().isRunning()) {
			double incline = videoIncline;
			incline /= 1000;
			incline *= (SystemSettingsHelper.MAX_INCLINE + ORIGIN_INCLINE);
			incline /= SystemSettingsHelper.STEP_INCLINE;
			incline = SysUtils.formatDouble(incline);
			int inclinetemp = (int) incline;
			incline = inclinetemp * SystemSettingsHelper.STEP_INCLINE;
			Log.d("scenevideo incline set", "incline: " + incline);
			sendHandleMsg(mSendMsgHandler, handleType.SET_INCLINE, incline, 0);
		}
	}

	public void setBackMusicVisibility(boolean flag) {
		if (mBackMusic != null) {
			if (flag) {
				haveBackMusic = true;
				mBackMusic.setVolume(mVolume, mVolume);
			} else {
				haveBackMusic = false;
				mBackMusic.setVolume(0.0f, 0.0f);
			}
		}
	}

	public void setPlayPosition(VaRecordPosition vaRecordPosition) {
		if (vaRecordPosition != null) {
			mVideoIndex = vaRecordPosition.getVideoIndex();
			mCurrentPosition = vaRecordPosition.getCurrentPosition();
		} else {
			mVideoIndex = 0;
			mCurrentPosition = -1;
		}
	}

	public VaRecordPosition getPlayPosition() {
		VaRecordPosition vaRecordPosition = new VaRecordPosition();
		if (mMediaPlayer != null) {
			vaRecordPosition.setVaRecordPosition(true, mVideoIndex, mMediaPlayer.getCurrentPosition());
		}
		return vaRecordPosition;
	}

	private class VoiceThread extends Thread {
		private CvaVideo video;
		private String voiceFile;
		private String videomessage;
		private boolean _run = true;
		private boolean endFlag = false;

		public VoiceThread(CvaVideo video) {
			this.video = video;
		}

		public boolean Stop() {
			this._run = false;
			return endFlag;
		}

		@Override
		public void run() {
			if (video.getLevelCount() > 0) {
				if (mCurrentPosition != -1) {
					SystemClock.sleep(2000);
				}
				int CurrentFrame = (int) (((double) mMediaPlayer.getCurrentPosition() * video.getFramesPerSecondx1000()) / 1000000);
				if (video.findPreviousValueEventIndex(CurrentFrame)) {
					if (mCurrentPosition != -1) {
						while (video.searchIndex >= 1) {
							video.searchIndex = video.searchIndex - 1;
							if ((video.getFileNameID() != CvaEvent.INVALID_EVENT_VALUE)) {
								break;
							}
						}
					}
					// Log.d("FindPreviousValueEventIndex",
					// "true:" + video.searchIndex + " EventCount: "
					// + video.getEventCount());
					while (_run) {
						if (mMediaPlayer == null && (!mMediaPlayer.isPlaying())) {
							endFlag = true;
							return;
						}
						CurrentFrame = (int) (((double) mMediaPlayer.getCurrentPosition() * video
								.getFramesPerSecondx1000()) / 1000000);
						// Log.d("video CurrentPosition",
						// "" + mMediaPlayer.getCurrentPosition() / 1000);
						if (video.findNextEventFrame(CurrentFrame, video.searchIndex)) {
							// Log.d("FindNextEventFrame", "SearchIndex: "
							// + video.searchIndex + " CurrentFrame: "
							// + CurrentFrame + " frame: "
							// + video.nextFrame);
							if (video.getResistance() != CvaEvent.INVALID_EVENT_VALUE) {
							}
							if (video.getIncline() != CvaEvent.INVALID_EVENT_VALUE) {
								int videoIncline = video.getIncline();
								Log.d("videoIncline", "" + videoIncline);
								if (mMachineIncline != videoIncline) {
									mMachineIncline = videoIncline;
									sendHandleMsg(mVaPlayerHandler, handleType.SET_INCLINE, 0, 0);
								}
							}
							if (video.getFileNameID() != CvaEvent.INVALID_EVENT_VALUE) {
								voiceFile = video.getPath() + video.getEventMessage(mVaLanguage, video.getFileNameID());
								Log.d("voiceFile", voiceFile);
								if (new File(voiceFile).exists()) {
									sendHandleMsg(mVaPlayerHandler, handleType.PLAY_VOICE, voiceFile, 0);
								}
							}
							if (video.getMessageID() != CvaEvent.INVALID_EVENT_VALUE) {
								videomessage = video.getEventMessage(mVaLanguage, video.getMessageID());
								Log.d("videomessage", videomessage);
								sendHandleMsg(mVaPlayerHandler, handleType.SHOW_MSG, videomessage, 0);
							}
							video.searchIndex++;
						}
						SystemClock.sleep(100);
					}
				} else {
					Log.d("FindPreviousValueEventIndex", "false:" + video.searchIndex);
				}
			}
			endFlag = true;
			Log.d(TAG, "VoiceThread end");
		}
	}

	private void sendHandleMsg(Handler handler, int msgWhat, String msgStr, int delayTime) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString(handleType.DATA_KEY, msgStr);
		msg.setData(bundle);
		msg.what = msgWhat;
		handler.sendMessageDelayed(msg, delayTime);
	}

	private void sendHandleMsg(Handler handler, int msgWhat, int msgInt, int delayTime) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt(handleType.DATA_KEY, msgInt);
		msg.setData(bundle);
		msg.what = msgWhat;
		handler.sendMessageDelayed(msg, delayTime);
	}

	private void sendHandleMsg(Handler handler, int msgWhat, double msgDouble, int delayTime) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putDouble(handleType.DATA_KEY, msgDouble);
		msg.setData(bundle);
		msg.what = msgWhat;
		handler.sendMessageDelayed(msg, delayTime);
	}

	public class handleType {
		public static final String DATA_KEY = "data";
		public static final int SHOW_MSG = 0;
		public static final int HIDE_MSG = 1;
		public static final int PLAY_VOICE = 3;
		public static final int PLAY_VOIDEO = 4;
		public static final int SET_VIDEO_SPEED = 5;
		public static final int SET_INCLINE = 6;
		public static final int VEDIO_FINISH = 7;
		public static final int SET_VIDEO_SURFACE = 8;
	}
}
