package com.tapc.platform.media;

public class VaRecordPosition {
	private boolean needToResume = false;
	private int videoIndex = 0;
	private int currentPosition = -1;

	public void setVaRecordPosition(boolean needToResume, int videoIndex,
			int currentPosition) {
		this.needToResume = needToResume;
		this.videoIndex = videoIndex;
		this.currentPosition = currentPosition;
	}

	public boolean isNeedToResume() {
		return needToResume;
	}

	public void setNeedToResume(boolean needToResume) {
		this.needToResume = needToResume;
	}

	public void setVideoIndex(int videoIndex) {
		this.videoIndex = videoIndex;
	}

	public int getVideoIndex() {
		return videoIndex;
	}

	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}

	public int getCurrentPosition() {
		return currentPosition;
	}
}
