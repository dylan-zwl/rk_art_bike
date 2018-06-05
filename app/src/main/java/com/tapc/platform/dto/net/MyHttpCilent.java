package com.tapc.platform.dto.net;

import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.tapc.platform.entity.SportsData;

public interface MyHttpCilent {

	/**
	 * 其他的api接口都会调用的post请求接口
	 * 
	 * @param context
	 * @param callBack
	 * @return
	 */

	<T> HttpHandler<T> post(String url, MyRequestCallBack<T> callBack);

	<T> HttpHandler<T> post(String url, RequestParams params,
			MyRequestCallBack<T> callBack);

	<T> HttpHandler<T> get(String url, RequestParams params,
			MyRequestCallBack<T> callBack);

	/**
	 * 登录
	 * 
	 * @param p
	 * @param u
	 * @param callback
	 */
	void login(String username, String password, Callback callback);

	/**
	 * 注册
	 * 
	 * @param p
	 * @param u
	 * @param callback
	 */
	void register(String username, String nickname, String gender,
			String height, String age, String email, String pwd, String rePwd,
			Callback callback);

	/**
	 * 刷新验证
	 * 
	 * @param callback
	 */
	void refreshVerifyCode(String encoding, Callback callback);

	/**
	 * 邮件找回密码
	 * 
	 * @param callback
	 */
	void getPwByEmail(String email, String code, String encoding,
			Callback callback);

	/**
	 * 保存个人健身数据并返回一个页
	 * 
	 * @param callback
	 */
	void saveUserFitnessData(String token, String calorie, String duration,
			String minute, String mk, String times, String setting,
			Callback callback);

	/**
	 * 上传用户设置的语言
	 * 
	 * @param callback
	 */
	void uploadUserLanguage(String token, String lang, Callback callback);

	/**
	 * 上传用户设置的公英制
	 * 
	 * @param callback
	 */
	void uploadUserStandard(String token, String stand, Callback callback);

	/**
	 * 查询历史健身数据
	 * 
	 * @param callback
	 */
	void getUserFitnessData(String token, int start, int end, Callback callback);

	/**
	 * 广告查询
	 * 
	 * @param callback
	 */
	void getAdvertisement(Callback callback);

	/**
	 * 获取远程信息
	 * 
	 * @param callback
	 */
	void getRemotion(String versionName, Callback callback);

	/**
	 * 上传运动数据
	 * 
	 * @param callback
	 */
	void uploadSportsData(String uid, String username, String datetime, SportsData sprData, Callback callback);

	/**
	 * 创建公共群组
	 * 
	 * @param group
	 *            群组名称
	 * @param member
	 * @param callback
	 */
	void createPublicGroup(String group, String member, Callback callback);

	/**
	 * 创建一个私有群组
	 * 
	 * @param group
	 * @param member
	 *            (创建者信息Json)
	 * @param callback
	 */
	void createPrivateGroup(String group, String member, Callback callback);

	/**
	 * 加入私有群组
	 * 
	 * @param code
	 * @param callback
	 */
	void joinPrivateGroup(String code, Callback callback);

	/**
	 * @param group
	 *            long型群组id
	 * @param member
	 * @param callback
	 */
	void setAndGetGroupData(String group, String member, Callback callback);

	/**
	 * 获取群的信息
	 * 
	 * @param group
	 *            id
	 * @param callback
	 */
	void getGroupInfo(String group, Callback callback);

	/**
	 * 获取所有的公开群组信息
	 * 
	 * @param callback
	 */
	void getPublicGroups(Callback callback);

	/**
	 * 退出群组
	 * 
	 * @param group
	 *            long型群组id
	 * @param member
	 *            String名字
	 * @param callback
	 */
	void leaveGroup(String group, String member, Callback callback);

	/**
	 * 获取当前位置信息
	 * 
	 * @param callBack
	 */
	void getLocation(Callback callBack);
	void getLocation(String ip, Callback callBack);
	
	/**
	 * 点赞
	 * @param runner 名字
	 * @param callBack
	 */
	void like(String runner, Callback callBack);
	
	/**
	 * 获取点赞数量
	 * @param runner
	 * @param callBack
	 */
	void likeCount(String runner, Callback callBack);
	
	/**
	 * 挑战赛
	 */
	void getChallengeData(String username, int mChallengeMode, int start, int end, Callback callback);

	void saveChallengeData(String username, String calorie, String duration, String minute, String mk, String times,
			String setting, int sportType, boolean challengeSuccess, Callback callback);
	void uploadDevicData(String deviceId, String parameter, Callback callBack);
}
