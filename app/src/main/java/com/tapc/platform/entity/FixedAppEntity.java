package com.tapc.platform.entity;

public enum FixedAppEntity {
	INTERNET("com.tencent.padbrowser", "com.tencent.padbrowser.SplashActivity"), 
	WEATHER("sina.mobile.tianqitonghd", "sina.mobile.tianqitonghd.ui.LaunchActivity"), 
	GAME("com.rovio.angrybirds", "com.rovio.ka3d.App"),
	STORE("com.taobao.apad", "com.taobao.apad.activity.MainActivity"), 
	VIDEO("com.mxtech.videoplayer.pro", "com.mxtech.videoplayer.ActivityMediaList"), 
	MUSIC("com.kugou.playerHD", "com.kugou.playerHD.activity.MediaActivity"),
	NEWS("com.tencent.news", "com.tencent.news.activity.SplashActivity");
	
	public String packageName;
	public String className;

	private FixedAppEntity(String packageName, String className) {
		this.packageName = packageName;
		this.className = className;
	}
}
