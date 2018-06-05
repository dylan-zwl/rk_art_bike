package com.tapc.platform.dto.net;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.dto.response.Response;

public class MyRequestCallBack<T> extends RequestCallBack<T> {

	private Callback mCallBack;

	private Class<T> mClazz;

	private Context mContext;

	public MyRequestCallBack(Context context, Callback callback, Class<T> clazz) {
		mContext = context;
		mCallBack = callback;
		mClazz = clazz;
	}

	@Override
	public void onFailure(HttpException arg0, String arg1) {
		if (mCallBack != null) {
			Toast.makeText(mContext, mContext.getString(R.string.toast_connect_server_failure), Toast.LENGTH_SHORT)
					.show();
			LogUtils.i("连接服务器失败");
			arg0.printStackTrace();
			mCallBack.onFailure(arg0);
		}
	}

	@Override
	public void onSuccess(ResponseInfo<T> responseInfo) {
		if (mCallBack != null) {
			try {
				// LogUtils.i(responseInfo.result.toString());
				if (Config.SERVICE_ID == 0) {
					mCallBack.onSuccess(responseInfo.result.toString());
				} else if (Config.SERVICE_ID == 1) {
					Gson gson = new Gson();
					mCallBack.onSuccess(gson.fromJson(responseInfo.result.toString(), mClazz));
				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
				mCallBack.onFailure(null);
			}
		}
	}

	@Override
	public void onStart() {
		if (mCallBack != null) {
			mCallBack.onStart();
		}
	}
}
