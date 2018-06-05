package com.tapc.platform.dto.net;

public class UrlConsts {
	// 舒华服务器
	public static final String URL_GEN_SHUHUA = "http://112.74.87.166:8080/tapc/rest/cus/";
	// 登录
	public static final String LOGIN_URL_SH = URL_GEN_SHUHUA + "user/login";

	// 注册
	public static final String REGISTER_URL_SH = URL_GEN_SHUHUA + "user/register";

	// 保存个人健身数据
	public static final String SAVE_PERSONAL_FITNESS_DATA_SH = URL_GEN_SHUHUA + "user/addrecord";

	// 刷新验证
	public static final String REFRESH_VERIFICATION_CODE_SH = URL_GEN_SHUHUA + "user/getverifycode";

	// 邮件找回密码
	public static final String SEND_EMAIL_URL_SH = URL_GEN_SHUHUA + "user/sendemail";

	// 查询历史健身数据&查询积分
	public static final String SEARCHE_RCORD_URL_SH = URL_GEN_SHUHUA + "user/searchrecord";

	// 广告查询
	public static final String SEARCHE_ADV_URL_SH = URL_GEN_SHUHUA + "user/searchadvertisement";
	// 上传用户语言
	public static final String UPLOAD_USER_LANGUAGE_SH = URL_GEN_SHUHUA + "language/edit";
	// 上传用户公英
	public static final String UPLOAD_USER_STANDARD_SH = URL_GEN_SHUHUA + "measurement/edit";

	/**
	 * 远程版本信息
	 */
	public static final String UPDATE_VERSION = URL_GEN_SHUHUA + "task/X5";

	// 中阳天天跑步机服务器
	public static final String URL_GEN_TT = "http://ttpaobu.com/interface/";

	// 登录
	public static final String LOGIN_URL_TT = URL_GEN_TT + "user.php?method=login";

	// 注册
	public static final String REGISTER_URL_TT = URL_GEN_TT + "user.php?method=register";

	// 保存个人健身数据
	public static final String SAVE_PERSONAL_FITNESS_DATA_TT = URL_GEN_TT + "record.php?method=save";

	/**
	 * 创火科技服务器
	 */
	// 创火科技服务器
	public static final String URL_GEN_TRONSIS = "http://www.szimbox.com:8080/tapc/rest/group/";

	// 创建一个公共群
	public static final String CREATE_PUBLIC_GROUP = URL_GEN_TRONSIS + "createGroup";

	// 创建一个私人群
	public static final String CREATE_PRIVATE_GROUP = URL_GEN_TRONSIS + "createPrivateGroup";

	// 加入或获取一个群组的信息
	public static final String SET_AND_GET_GROUP_DATA = URL_GEN_TRONSIS + "setAndGetGroupData";

	// 加入私有群组
	public static final String JOIN_PRIVATE_GROUP = URL_GEN_TRONSIS + "joinPrivateGroup";

	// 获取所有的公开群组信息
	public static final String GET_PUBLIC_GROUPS = URL_GEN_TRONSIS + "getPublicGroups";

	// 获取群组信息
	public static final String GET_GROUP_INFO = URL_GEN_TRONSIS + "getGroupInfo";

	// 退出群组
	public static final String LEAVE_GROUP = URL_GEN_TRONSIS + "leaveGroup";

	// 获取当前位置
	public static final String GET_LOCATION = URL_GEN_TRONSIS + "getLocation";
	public static final String GET_LOCATION_TAOBAO = "http://ip.taobao.com/service/getIpInfo.php";

	// 点赞
	public static final String LIKE = URL_GEN_TRONSIS + "like";

	// 获取点赞数量
	public static final String GET_LIKECOUNT = URL_GEN_TRONSIS + "likeCount";

	// 挑战赛
	public static final String URL_TAPC = "http://www.szimbox.com:8080/tapc/rest/cus/";
	public static final String SAVE_CHALLENGE_DATA_SH = URL_TAPC + "user/addchallengerecord";
	public static final String SEARCH_CHALLENGE_RCORD_URL_SH = URL_TAPC + "user/challengerecord";

	public static final String UPLOAD_DEVICE_DATA = URL_GEN_SHUHUA + "user/uploadDeviceData";
}
