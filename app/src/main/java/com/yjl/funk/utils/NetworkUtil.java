package com.yjl.funk.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetworkUtil {

	/**
	 * 获取当前网络类型
	 *
	 * @return 0：未连接   1：WIFI网络   2：Mobile网络    3：ETHERNET网络
	 */
	public static final int NETTYPE_WIFI = 1;
	public static final int NETTYPE_MOBILE = 2;

	//是否有网
	public static boolean isNetConnect(Context context) {
		int net_status = getNetworkType(context);
		if (net_status == 0) {
			return false;
		}
		return true;
	}

	//获取网络类型
	public static int getNetworkType(Context context) {
		int netType = 0;
		ConnectivityManager connectivityManager =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			netType = NETTYPE_MOBILE;
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}
	//获取网络类型
	public static String getNetworkTypestr(Context context) {
		String strNetworkType = "";

		NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				strNetworkType = "WIFI";
			} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				String _strSubTypeName = networkInfo.getSubtypeName();
				// TD-SCDMA networkType is 17
				int networkType = networkInfo.getSubtype();
				switch (networkType) {
					case TelephonyManager.NETWORK_TYPE_GPRS:
					case TelephonyManager.NETWORK_TYPE_EDGE:
					case TelephonyManager.NETWORK_TYPE_CDMA:
					case TelephonyManager.NETWORK_TYPE_1xRTT:
					case TelephonyManager.NETWORK_TYPE_IDEN: // api<8 : replace by11
						strNetworkType = "2G";
						break;
					case TelephonyManager.NETWORK_TYPE_UMTS:
					case TelephonyManager.NETWORK_TYPE_EVDO_0:
					case TelephonyManager.NETWORK_TYPE_EVDO_A:
					case TelephonyManager.NETWORK_TYPE_HSDPA:
					case TelephonyManager.NETWORK_TYPE_HSUPA:
					case TelephonyManager.NETWORK_TYPE_HSPA:
					case TelephonyManager.NETWORK_TYPE_EVDO_B: // api<9 : replace by14
					case TelephonyManager.NETWORK_TYPE_EHRPD: // api<11 : replace by12
					case TelephonyManager.NETWORK_TYPE_HSPAP: // api<13 : replace by15
						strNetworkType = "3G";
						break;
					case TelephonyManager.NETWORK_TYPE_LTE: // api<11 : replace by13
						strNetworkType = "4G";
						break;
					default:
						// http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
						if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA")
								|| _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
							strNetworkType = "3G";
						} else {
							strNetworkType = _strSubTypeName;
						}
						break;
				}
			}
		}
		return strNetworkType;
	}
	/**
	 * 打开网络设置界面
	 */
	public static void openSetting(Activity activity)
	{
		Intent intent = new Intent("/");
		ComponentName cm = new ComponentName("com.android.settings",
				"com.android.settings.WirelessSettings");
		intent.setComponent(cm);
		intent.setAction("android.intent.action.VIEW");
		activity.startActivityForResult(intent, 0);
	}
}
