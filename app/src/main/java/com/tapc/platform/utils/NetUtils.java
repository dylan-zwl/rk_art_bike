package com.tapc.platform.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;

/**
 * Created by Administrator on 2017/9/8.
 */

public class NetUtils {
    private NetUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 功能描述 : 判断网络是否连接
     *
     * @param :
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        if (null != connectivityManager) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 功能描述 : 判断网络连接类型
     *
     * @param :
     */
    public static boolean isOfConnectType(Context context, int type) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        if (null != connectivityManager) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (connectivityManager.getActiveNetworkInfo().getType() == type) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 功能描述 : 获取mac地址
     *
     * @param :
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
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 功能描述 : 获取mac地址，wifi没有打开时会强制打开获取mac地址
     */
    public static String getDeviceId(Context context) {
        String deviceId = "";
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager.isWifiEnabled()) {
            deviceId = getLocalMacAddress(context);
        } else {
            mWifiManager.setWifiEnabled(true);
            deviceId = getLocalMacAddress(context);
            mWifiManager.setWifiEnabled(false);
        }
        return deviceId;
    }

    /**
     * 功能描述 : 获取域名IP地址
     *
     * @param : host  域名
     */
    public static String getInetAddress(String host) {
        String IPAddress = "";
        InetAddress ReturnStr1 = null;
        try {
            ReturnStr1 = InetAddress.getByName(host);
            IPAddress = ReturnStr1.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return IPAddress;
    }
}