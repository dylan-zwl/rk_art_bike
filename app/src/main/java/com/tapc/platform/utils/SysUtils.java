/**
 * SysUtils.java[v 1.0.0]
 * classes:com.jht.tapc.platform.utils.SysUtils
 * fch Create of at 2015年4月23日 下午3:03:32
 */
package com.tapc.platform.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Xml;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.entity.CtlParameter;
import com.tapc.platform.entity.PackageDeleteObserver;
import com.tapc.platform.entity.PackageInstallObserver;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * classes:com.jht.tapc.platform.utils.SysUtils
 * 
 * @author fch <br/>
 *         Create of at 2015年4月23日 下午3:03:32
 */
public class SysUtils {

	public static double formatDouble(double value) {
		BigDecimal bg = new BigDecimal(value).setScale(1, RoundingMode.HALF_UP);
		return bg.doubleValue();
	}

	public static double formatDouble(int bit, double value) {
		BigDecimal bg = new BigDecimal(value).setScale(bit, RoundingMode.HALF_UP);
		return bg.doubleValue();
	}

	public static double formatDouble(int bit, double value, RoundingMode roundingMode) {
		BigDecimal bg = new BigDecimal(value).setScale(bit, roundingMode);
		return bg.doubleValue();
	}

	public static String getLocalVersionCode(Context context) {
		String versionName = "";
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			versionName = packageInfo.versionName;
		} catch (Exception e) {
		}
		return versionName;
	}

	/**
	 * 启动apk
	 * 
	 * @param packageName
	 */
	public static void startApk(String packageName, Activity activity) {
		PackageManager pm = activity.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = activity.getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			Toast.makeText(activity, packageName + (R.string.not_install), Toast.LENGTH_SHORT).show();
			return;
		}

		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(pi.packageName);

		List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);

		ResolveInfo ri = apps.iterator().next();
		if (ri != null) {
			packageName = ri.activityInfo.packageName;
			String className = ri.activityInfo.name;

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);

			ComponentName cn = new ComponentName(packageName, className);

			intent.setComponent(cn);
			activity.startActivity(intent);
		}
	}

	/**
	 * 安装apk
	 * 
	 * @param context
	 * @param path
	 */
	public static void installApk(Context context, File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	public static void installApk(Context context, File file, PackageInstallObserver observer) {
		Uri uri = Uri.fromFile(file);
		PackageManager pm = context.getPackageManager();
		pm.installPackage(uri, observer, PackageManager.INSTALL_REPLACE_EXISTING, file.getAbsolutePath());
	}

	public static void unInstallApk(Context context, String pkgName, PackageDeleteObserver observer) {
		PackageManager pm = context.getPackageManager();
		pm.deletePackage(pkgName, observer, 0);
	}

	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 递归删除文件和文件夹
	 * 
	 * @param file
	 *            要删除的根目录
	 */
	public static void RecursionDeleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory() && !file.getAbsolutePath().equals(Config.IN_SD_FILE_PATH + ".va")) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				RecursionDeleteFile(f);
			}
			file.delete();
		}
	}

	public static CtlParameter loadCtlParameter(Context context) {
		InputStream xml;
		CtlParameter ctlParameter = null;
		try {
			xml = context.getResources().getAssets().open("controler_parameter.xml");
			ctlParameter = new CtlParameter();
			XmlPullParser pullParser = Xml.newPullParser();
			pullParser.setInput(xml, "UTF-8");
			int event = pullParser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				String nodeName = pullParser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = pullParser.next();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return ctlParameter;
	}

	private static void releasePlayer(MediaPlayer players) {
		if (players != null) {
			players.release();
			players = null;
		}
	}

	public static void playBeep(final Context context, final int rid) {
		try {
			new Thread(new Runnable() {
				public void run() {
					if (rid != 0) {
						MediaPlayer player = MediaPlayer.create(context, rid);
						player.start();
						player.setOnCompletionListener(new OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer players) {
								releasePlayer(players);
							}
						});
						player.setOnErrorListener(new OnErrorListener() {
							@Override
							public boolean onError(MediaPlayer players, int arg1, int arg2) {
								releasePlayer(players);
								return false;
							}
						});
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void playBeep(String path) {
		if (!path.isEmpty()) {
			try {
				MediaPlayer player = new MediaPlayer();
				player.setDataSource(path);
				player.prepareAsync();
				player.setOnPreparedListener(new OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer players) {
						players.start();
					}
				});
				player.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer players) {
						releasePlayer(players);
					}
				});
				player.setOnErrorListener(new OnErrorListener() {
					@Override
					public boolean onError(MediaPlayer players, int arg1, int arg2) {
						releasePlayer(players);
						return false;
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("SimpleDateFormat")
	public static String getDataTimeStr(String pattern, long date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(date);
	}

	public static String getString(InputStream inputStream) {
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(inputStream, "gbk");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(inputStreamReader);
		StringBuffer sb = new StringBuffer("");
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static void sendKeyEvent(final int key) {
		new Thread() {
			public void run() {
				try {
					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(key);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * 获取本机Mac地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getLocalMacAddress(Context context) {
		try {
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo infor = wifi.getConnectionInfo();
			if (infor != null) {
				String localMacAddress = infor.getMacAddress();
				if (localMacAddress != null && localMacAddress.length() > 0) {
					return localMacAddress.replace(":", "");
				}
			}
		} catch (Exception e) {
		}
		return "";
	}

	/**
	 * 将int类型的数据转换为byte数组 原理：将int数据中的四个byte取出，分别存储
	 * 
	 * @param n
	 *            int数据
	 * @return 生成的byte数组
	 */
	public static byte[] intToBytes(int n) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) ((n >> (24 - i * 8)) & 0xFF);
		}
		return b;
	}

	/**
	 * 将byte数组转换为int数据
	 * 
	 * @param b
	 *            字节数组
	 * @return 生成的int数据
	 */
	public static int bytesToInt(byte[] ary) {
		int value;
		value = (int) ((ary[3] & 0xFF) | ((ary[2] << 8) & 0xFF00) | ((ary[1] << 16) & 0xFF0000) | ((ary[0] << 24) & 0xFF000000));
		return value;
	}

	/**
	 * 检测网络是否连接
	 * 
	 * @return
	 */
	public static boolean isNetConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo[] infos = cm.getAllNetworkInfo();
			if (infos != null) {
				for (NetworkInfo ni : infos) {
					if (ni.isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static Map<String, Integer> getLoadMap(Context context) {
		String pwmData = PreferenceHelper.readString(context, Config.SETTING_CONFIG, "bike_pwm", null);
		Map<String, Integer> loadMap = null;
		if (pwmData != null) {
			Gson gson = new Gson();
			loadMap = gson.fromJson(pwmData, new TypeToken<Map<String, Integer>>() {
			}.getType());
		}
		if (loadMap == null) {
			int[] levelPwm = new int[] { 39, 63, 84, 103, 120, 134, 146, 160, 172, 184, 196, 206, 216, 226, 236, 243,
					250, 258, 267, 276, 282, 290, 298, 306, 314, 322, 330, 337, 345, 353, 359, 367, 374, 381, 388, 395,
					403, 409, 419, 426 };
			loadMap = new HashMap<String, Integer>();
			for (int i = 0; i < levelPwm.length; i++) {
				Integer data = Integer.valueOf(levelPwm[i]);
				loadMap.put(String.valueOf(i + 1), data);
			}
			saveLoadMap(context, loadMap);
		}
		return loadMap;
	}

	public static void saveLoadMap(Context context, Map<String, Integer> loadMap) {
		String jsonData = new Gson().toJson(loadMap);
		PreferenceHelper.write(context, Config.SETTING_CONFIG, "bike_pwm", jsonData);
	}

	/**
	 * 生成二维码图片
	 */
	public static Bitmap createImage(String QrcodeStr, int width, int height, int minPandingSize) {
		Bitmap bitmap;
		try {
			int startX = 0;
			int startY = 0;
			boolean isFirstBlackPoint = false;

			QRCodeWriter writer = new QRCodeWriter();
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

			BitMatrix martix = writer.encode(QrcodeStr, BarcodeFormat.QR_CODE, width, height, hints);
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (martix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
						if (isFirstBlackPoint == false) {
							isFirstBlackPoint = true;
							startX = x;
							startY = y;
						}
					} else {
						pixels[y * width + x] = 0xffffffff;
					}
				}
			}

			bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

			// 剪切中间的二维码区域，减少padding区域
			if (startX <= minPandingSize)
				return bitmap;
			int x1 = startX - minPandingSize;
			int y1 = startY - minPandingSize;
			if (x1 < 0 || y1 < 0)
				return bitmap;
			int w1 = width - x1 * 2;
			int h1 = height - y1 * 2;
			Bitmap bitmapQR = Bitmap.createBitmap(bitmap, x1, y1, w1, h1);
			return bitmapQR;
		} catch (WriterException e) {
			e.printStackTrace();
			bitmap = null;
		}
		return bitmap;
	}

	public static Bitmap addQrLogo(Bitmap src, Bitmap logo) {
		if (src == null) {
			return null;
		}

		if (logo == null) {
			return src;
		}
		// 获取图片的宽高
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();
		int logoWidth = logo.getWidth();
		int logoHeight = logo.getHeight();

		if (srcWidth == 0 || srcHeight == 0) {
			return null;
		}

		if (logoWidth == 0 || logoHeight == 0) {
			return src;
		}
		// logo大小为二维码整体大小的1/5
		float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
		Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
		try {
			Canvas canvas = new Canvas(bitmap);
			canvas.drawBitmap(src, 0, 0, null);
			canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
			canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
		} catch (Exception e) {
			bitmap = null;
			e.getStackTrace();
		}
		return bitmap;
	}

}
