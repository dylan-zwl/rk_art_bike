package com.tapc.platform.dto.net;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.dto.GroupUserResponseDTO;
import com.tapc.platform.dto.LocationDTO;
import com.tapc.platform.dto.ResponseDTO;
import com.tapc.platform.dto.taobaoLocation;
import com.tapc.platform.dto.response.DeviceDataResponse;
import com.tapc.platform.dto.response.GetAdvertisementResponse;
import com.tapc.platform.dto.response.GetChallengeRankResponse;
import com.tapc.platform.dto.response.GetChallengeResultResponse;
import com.tapc.platform.dto.response.GetPwByEmailResponse;
import com.tapc.platform.dto.response.GetUserFitnessResponse;
import com.tapc.platform.dto.response.LoginResponse;
import com.tapc.platform.dto.response.RefreshVerifyCodeResponse;
import com.tapc.platform.dto.response.RegisterResponse;
import com.tapc.platform.dto.response.RemotionResponse;
import com.tapc.platform.dto.response.SameResponse;
import com.tapc.platform.dto.response.SaveUserFitnessResponse;
import com.tapc.platform.entity.SportsData;

public class MyHttpCilentImpl implements MyHttpCilent {

	private HttpUtils mHttpUtils;

	private Context mContext;

	public MyHttpCilentImpl(Context context) {
		mHttpUtils = new HttpUtils();
		mContext = context;
	}

	@Override
	public <T> HttpHandler<T> post(String url, MyRequestCallBack<T> callBack) {
		if (Config.isConnected) {
			return mHttpUtils.send(HttpMethod.POST, url, callBack);
		} else {
			Toast.makeText(mContext, mContext.getString(R.string.net_unconnected), Toast.LENGTH_SHORT).show();
			return null;
		}
	}

	@Override
	public <T> HttpHandler<T> post(String url, RequestParams params, MyRequestCallBack<T> callBack) {
		Log.i("url", "url=" + url);
		if (Config.isConnected) {
			return mHttpUtils.send(HttpMethod.POST, url, params, callBack);
		} else {
			Toast.makeText(mContext, mContext.getString(R.string.net_unconnected), Toast.LENGTH_SHORT).show();
			return null;
		}
	}

	@Override
	public <T> HttpHandler<T> get(String url, RequestParams params, MyRequestCallBack<T> callBack) {
		return mHttpUtils.send(HttpMethod.GET, url, params, callBack);
	};

	@Override
	public void login(String username, String password, Callback callback) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("username", username);
		params.addBodyParameter("password", password);
		if (Config.SERVICE_ID == 0) {
			post(UrlConsts.LOGIN_URL_TT, params, new MyRequestCallBack<LoginResponse>(mContext, callback,
					LoginResponse.class));
		} else if (Config.SERVICE_ID == 1) {
			post(UrlConsts.LOGIN_URL_SH, params, new MyRequestCallBack<LoginResponse>(mContext, callback,
					LoginResponse.class));
		}

	}

	@Override
	public void register(String username, String nickname, String gender, String height, String age, String email,
			String pwd, String rePwd, Callback callback) {
		RequestParams params = new RequestParams();
		if (Config.SERVICE_ID == 0) {
			params.addBodyParameter("username", username);
			params.addBodyParameter("password", pwd);
			params.addBodyParameter("nickname", nickname);
			post(UrlConsts.REGISTER_URL_TT, params, new MyRequestCallBack<RegisterResponse>(mContext, callback,
					RegisterResponse.class));
		} else if (Config.SERVICE_ID == 1) {
			params.addBodyParameter("username", username);
			params.addBodyParameter("password", pwd);
			params.addBodyParameter("nickname", nickname);
			params.addBodyParameter("gender", gender);
			params.addBodyParameter("height", height);
			params.addBodyParameter("age", age);
			params.addBodyParameter("email", email);
			params.addBodyParameter("repassword", rePwd);
			post(UrlConsts.REGISTER_URL_SH, params, new MyRequestCallBack<RegisterResponse>(mContext, callback,
					RegisterResponse.class));
		}
	}

	@Override
	public void refreshVerifyCode(String encoding, Callback callback) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("encoding", encoding);
		post(UrlConsts.REFRESH_VERIFICATION_CODE_SH, params, new MyRequestCallBack<RefreshVerifyCodeResponse>(mContext,
				callback, RefreshVerifyCodeResponse.class));
	}

	@Override
	public void getPwByEmail(String email, String code, String encoding, Callback callback) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("email", email);
		params.addBodyParameter("code", code);
		params.addBodyParameter("encoding", encoding);
		post(UrlConsts.SEND_EMAIL_URL_SH, params, new MyRequestCallBack<GetPwByEmailResponse>(mContext, callback,
				GetPwByEmailResponse.class));
	}

	@Override
	public void saveUserFitnessData(String token, String calorie, String duration, String minute, String mk,
			String times, String setting, Callback callback) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("token", token);
		params.addBodyParameter("calorie", calorie);
		params.addBodyParameter("duration", duration);
		params.addBodyParameter("minute", minute);
		params.addBodyParameter("averageStep", mk);
		params.addBodyParameter("heartRate", times);
		params.addBodyParameter("setting", setting);
		post(UrlConsts.SAVE_PERSONAL_FITNESS_DATA_SH, params, new MyRequestCallBack<SaveUserFitnessResponse>(mContext,
				callback, SaveUserFitnessResponse.class));
	}

	@Override
	public void getUserFitnessData(String token, int start, int end, Callback callback) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("token", token);
		params.addBodyParameter("first", String.valueOf(start));
		params.addBodyParameter("max", String.valueOf(end));
		post(UrlConsts.SEARCHE_RCORD_URL_SH, params, new MyRequestCallBack<GetUserFitnessResponse>(mContext, callback,
				GetUserFitnessResponse.class));

	}

	@Override
	public void getAdvertisement(Callback callback) {
		post(UrlConsts.SEARCHE_ADV_URL_SH, new MyRequestCallBack<GetAdvertisementResponse>(mContext, callback,
				GetAdvertisementResponse.class));
	}

	@Override
	public void uploadUserLanguage(String token, String language, Callback callback) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("token", token);
		params.addBodyParameter("language", language);
		post(UrlConsts.UPLOAD_USER_LANGUAGE_SH, params, new MyRequestCallBack<SameResponse>(mContext, callback,
				SameResponse.class));
	}

	@Override
	public void uploadUserStandard(String token, String measurement, Callback callback) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("token", token);
		params.addBodyParameter("measurement", measurement);
		post(UrlConsts.UPLOAD_USER_STANDARD_SH, params, new MyRequestCallBack<SameResponse>(mContext, callback,
				SameResponse.class));
	}

	@Override
	public void getRemotion(String versionName, Callback callback) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("versionName", versionName);
		post(UrlConsts.UPDATE_VERSION, params, new MyRequestCallBack<RemotionResponse>(mContext, callback,
				RemotionResponse.class));
	}

	@Override
	public void uploadSportsData(String uid, String username, String datetime, SportsData sprData, Callback callback) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", uid);
		params.addBodyParameter("username", username);
		params.addBodyParameter("datetime", datetime);
		params.addBodyParameter("mid", "");
		params.addBodyParameter("type", "0");
		params.addBodyParameter("model", Config.MACHINE_MODEL);
		params.addBodyParameter("serial", Config.MACHINE_SERIAL);
		params.addBodyParameter("runtime", sprData.getRuntime());
		int distance = (int) (Double.valueOf(sprData.getDistance()) * 1000);
		params.addBodyParameter("distance", Integer.toString(distance));
		params.addBodyParameter("calories", sprData.getCalories());
		params.addBodyParameter("steps", sprData.getSteps());
		params.addBodyParameter("score", "");
		params.addBodyParameter("sportdata", "");
		post(UrlConsts.SAVE_PERSONAL_FITNESS_DATA_TT, params, new MyRequestCallBack<SameResponse>(mContext, callback,
				SameResponse.class));
	}

	@Override
	public void createPrivateGroup(String group, String member, Callback callback) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("group", group);
		params.addBodyParameter("member", member);
		post(UrlConsts.CREATE_PRIVATE_GROUP, params, new MyRequestCallBack<ResponseDTO>(mContext, callback,
				ResponseDTO.class));
	}

	@Override
	public void joinPrivateGroup(String code, Callback callback) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("code", code);
		post(UrlConsts.JOIN_PRIVATE_GROUP, params, new MyRequestCallBack<ResponseDTO>(mContext, callback,
				ResponseDTO.class));// map

	}

	@Override
	public void createPublicGroup(String group, String member, Callback callback) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("group", group);
		params.addBodyParameter("member", member);
		post(UrlConsts.CREATE_PUBLIC_GROUP, params, new MyRequestCallBack<ResponseDTO>(mContext, callback,
				ResponseDTO.class));
	}

	@Override
	public void setAndGetGroupData(String group, String member, Callback callback) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("group", group);
		params.addBodyParameter("member", member);
		post(UrlConsts.SET_AND_GET_GROUP_DATA, params, new MyRequestCallBack<>(mContext, callback,
				GroupUserResponseDTO.class));// map

	}

	@Override
	public void getPublicGroups(Callback callback) {
		post(UrlConsts.GET_PUBLIC_GROUPS, new MyRequestCallBack<>(mContext, callback, ResponseDTO.class));
	}

	@Override
	public void leaveGroup(String group, String member, Callback callback) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("group", group);
		params.addBodyParameter("member", member);
		post(UrlConsts.LEAVE_GROUP, params, new MyRequestCallBack<>(mContext, callback, ResponseDTO.class));
	}

	@Override
	public void getGroupInfo(String group, Callback callback) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("group", group);
		post(UrlConsts.GET_GROUP_INFO, params, new MyRequestCallBack<>(mContext, callback, GroupUserResponseDTO.class));
	}

	@Override
	public void getLocation(Callback callBack) {
		mHttpUtils.send(HttpMethod.GET, UrlConsts.GET_LOCATION, new MyRequestCallBack<>(mContext, callBack,
				LocationDTO.class));
	}

	@Override
	public void getLocation(String ip, Callback callBack) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("ip", ip);
		post(UrlConsts.GET_LOCATION_TAOBAO, params, new MyRequestCallBack<>(mContext, callBack, taobaoLocation.class));
	}

	@Override
	public void like(String runner, Callback callBack) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("runner", runner);
		post(UrlConsts.LIKE, params, new MyRequestCallBack<>(mContext, callBack, ResponseDTO.class));
	}

	@Override
	public void likeCount(String runner, Callback callBack) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("runner", runner);
		post(UrlConsts.GET_LIKECOUNT, params, new MyRequestCallBack<>(mContext, callBack, ResponseDTO.class));
	}

	@Override
	public void saveChallengeData(String token, String calorie, String duration, String minute, String mk,
			String times, String setting, int sportType, boolean challengeSuccess, Callback callback) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("token", token);
		params.addBodyParameter("calorie", calorie);
		params.addBodyParameter("duration", duration);
		params.addBodyParameter("minute", minute);
		params.addBodyParameter("averageStep", mk);
		params.addBodyParameter("heartRate", times);
		params.addBodyParameter("setting", setting);
		params.addBodyParameter("sportType", String.valueOf(sportType));
		int challengeSuccessInt = 0;
		if (challengeSuccess) {
			challengeSuccessInt = 1;
		} else {
			challengeSuccessInt = 0;
		}
		params.addBodyParameter("challengeSuccess", String.valueOf(challengeSuccessInt));
		post(UrlConsts.SAVE_CHALLENGE_DATA_SH, params, new MyRequestCallBack<GetChallengeResultResponse>(mContext,
				callback, GetChallengeResultResponse.class));
	}

	@Override
	public void getChallengeData(String userName, int sportsType, int start, int end, Callback callback) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("token", userName);
		params.addBodyParameter("sportsType", String.valueOf(sportsType));
		params.addBodyParameter("first", String.valueOf(start));
		params.addBodyParameter("max", String.valueOf(end));
		post(UrlConsts.SEARCH_CHALLENGE_RCORD_URL_SH, params, new MyRequestCallBack<GetChallengeRankResponse>(mContext,
				callback, GetChallengeRankResponse.class));
	}

	@Override
	public void uploadDevicData(String deviceId, String parameter, Callback callBack) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addBodyParameter("device_id", deviceId);
		params.addBodyParameter("parameter", parameter);
		post(UrlConsts.UPLOAD_DEVICE_DATA, params,
				new MyRequestCallBack<>(mContext, callBack, DeviceDataResponse.class));
	}
}
