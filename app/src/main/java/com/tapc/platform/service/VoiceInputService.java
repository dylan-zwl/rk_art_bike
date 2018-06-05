package com.tapc.platform.service;

import java.util.LinkedList;

import com.tapc.platform.TapcApp;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class VoiceInputService extends Service {
	protected int mInbufsize;
	private float mVoiceVolume = 1f;
	private AudioRecord mInrec;
	private byte[] mInbytes;
	private LinkedList<byte[]> mInq;
	private int mOutbufsize;
	private AudioTrack mOuttrk;
	private byte[] mOutbytes;
	private Thread mRecord;
	private Thread mPlay;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate();
	}

	public void onStart(Intent intent, int startId) {
		initVoice();
		mRecord = new Thread(new recordSound());
		mPlay = new Thread(new playRecord());
		mRecord.start();
		mPlay.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("stop sevice", "VoiceInputService");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
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
		mOuttrk.setStereoVolume(mVoiceVolume, mVoiceVolume);
		mOutbytes = new byte[mOutbufsize];
	}

	class recordSound implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					byte[] bytes_pkg;
					mInrec.startRecording();
					while (true) {
						if (mInq.size() < 1000) {
							mInrec.read(mInbytes, 0, mInbufsize);
							bytes_pkg = mInbytes.clone();
							mInq.add(bytes_pkg);
						} else {
							Thread.sleep(1000);
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mInrec.stop();
			}
		}
	}

	class playRecord implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					byte[] bytes_pkg = null;
					mOuttrk.play();
					while (true) {
						if (mInq.size() > 0) {
							mOutbytes = mInq.getFirst();
							bytes_pkg = mOutbytes.clone();
							mInq.removeFirst();
							mOuttrk.write(bytes_pkg, 0, bytes_pkg.length);
						} else {
							Thread.sleep(1000);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				mOuttrk.stop();
			}
		}
	}
}
