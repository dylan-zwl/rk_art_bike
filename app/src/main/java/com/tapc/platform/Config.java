/**
 * Config.java[v 1.0.0]
 * classes:com.oxbix.tapc.Config
 * fch Create of at 2015�?2�?3�? 下午5:10:26
 */
package com.tapc.platform;

import com.tapc.platform.entity.BikeCtlType;

/**
 * classes:com.oxbix.tapc.Config
 *
 * @author fch <br/>
 *         Create of at 2015�?2�?3�? 下午5:10:26
 */
public class Config {

    public static final String DB_NAME = "tapc.db";

    public static final String IN_SD_FILE_PATH = "/mnt/internal_sd/";
    public static final String EX_SD_FILE_PATH = "/mnt/external_sd/";
    public static final String USB_FILE_PATH = "/mnt/usb_storage/";
    public static final String VA_FILE_PATH_SD = "/mnt/external_sd/.va/";
    public static final String VA_FILE_PATH_NAND = "/mnt/internal_sd/.va/";

    public static final double UPLOAD_MINDISTANCE = 0.2;
    public static final int NOPERSON_DELAYTIME = 120;
    public static final int NOACTION_DELAYTIME = 10 * 60;
    public static final String DEFAUL_LANGUAGE = "2";

    // 数据库版本号
    public static final int DBVERSION = 1;
    public static String TOKEN = "";
    public static boolean isConnected = false;
    public static String LANGUAGE = "";
    public static boolean hasInputVoicePlay = false;

    public static boolean HAS_SERVICE = true;
    // ID = 0 中阳服务器 , ID = 1 舒华服务器
    public static int SERVICE_ID = 1;
    public static String MACHINE_MODEL = "";
    public static String MACHINE_SERIAL = "";

    public static String VERSION_NAME = "demostic_art_bike";

    // SharedPreferences�ļ�������
    public static final String SETTING_CONFIG = "setting_config";
    public static final String MACHINE_PARAM_CONFIG = "machine_param_config";
    public static final String MACHINE_STATUS = "machine_status";
    /**
     * 升级文件名字
     */
    public static final String UPDATE_FILE_NAME = "tapc_platform.apk";
    public static final String TEST_APP_PACKAGENAME = "com.tapc.test";

    public static int TOTAL_DISTANCE = 0;
    public static long TOTAL_TIME = 0;
    public static int TOTAL_CALORIE = 0;
    public static int CURRENT_SPEED = 0;
    public static String CURRENT_LOCATON = "";
    public static boolean LEAVE_GROUP = false;

    public static String DEVICE_ID = "";
    public static byte[] GZG_UID = new byte[36];
    public static boolean HAS_SET_PCDTX = false;
    public static BikeCtlType sBikeCtlType;

    public static boolean HAS_SCAN_QR = true;
    public static boolean HAS_RFID = true;
}
