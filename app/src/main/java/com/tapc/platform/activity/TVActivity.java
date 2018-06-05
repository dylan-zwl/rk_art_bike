/**
 * 
 */
package com.tapc.platform.activity;

import java.io.IOException;
import java.util.LinkedList;

import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tapc.platform.R;
import com.tapc.platform.activity.base.BaseActivity;

public class TVActivity extends BaseActivity implements SurfaceHolder.Callback {
	@ViewInject(R.id.TVmedia)
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera;

	protected int mInbufsize;
	private float voiceVolume = 1f;
	private AudioRecord mInrec;
	private byte[] mInbytes;
	private LinkedList<byte[]> mInq;
	private int mOutbufsize;
	private AudioTrack mOuttrk;
	private byte[] mOutbytes;
	private Thread mRecord;
	private Thread mPlay;
	private boolean isPlayFlag = true;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_tv);
		ViewUtils.inject(this);
		initCamera();
		startVoice();
	}

	@SuppressWarnings("deprecation")
	void initCamera() {
		mCamera = Camera.open(0);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.addCallback(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.startPreview();
		LogUtils.i("mCamera");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isPlayFlag = false;
		mRecord.stop();
		mPlay.stop();
		finish();
	}

	public void startVoice() {
		initVoice();
		mRecord = new Thread(new recordSound());
		mPlay = new Thread(new playRecord());
		mRecord.start();
		mPlay.start();
	}

	private void initVoice() {
		mInbufsize = AudioRecord.getMinBufferSize(44100,
				AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
		mInrec = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
				AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
				mInbufsize);
		mInbytes = new byte[mInbufsize];
		mInq = new LinkedList<byte[]>();

		mOutbufsize = AudioTrack.getMinBufferSize(44100,
				AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);

		mOuttrk = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
				AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT,
				mOutbufsize, AudioTrack.MODE_STREAM);
		mOuttrk.setStereoVolume(voiceVolume, voiceVolume);
		mOutbytes = new byte[mOutbufsize];
	}

	class recordSound implements Runnable {
		@Override
		public void run() {
			byte[] bytes_pkg;
			// 鐎殿噯鎷峰┑顔碱儏缂嶅秹妫呴敓锟�
			mInrec.startRecording();
			while (isPlayFlag) {
				if (mInq.size() < 1000) {
					mInrec.read(mInbytes, 0, mInbufsize);
					bytes_pkg = mInbytes.clone();
					mInq.add(bytes_pkg);
				}
			}
		}

	}

	class playRecord implements Runnable {
		@Override
		public void run() {
			byte[] bytes_pkg = null;
			mOuttrk.play();
			while (isPlayFlag) {
				try {
					if (mInq.size() > 0) {
						mOutbytes = mInq.getFirst();
						bytes_pkg = mOutbytes.clone();
						mInq.removeFirst();
						mOuttrk.write(bytes_pkg, 0, bytes_pkg.length);
					} else {
						Thread.sleep(1);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
