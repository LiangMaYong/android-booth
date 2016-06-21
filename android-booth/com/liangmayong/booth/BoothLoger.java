package com.liangmayong.booth;

import android.util.Log;

/**
 * BoothLoger
 * 
 * @author LiangMaYong
 * @version 1.0
 */
public class BoothLoger {
	private BoothLoger() {
	}

	private static final String LogerTag = "BoothLoger";
	private static boolean DEBUG = true;
	private static boolean ERROR = true;
	private static boolean INFO = true;
	private static boolean WARN = true;

	/**
	 * 打印debug开关
	 * 
	 * @return
	 */
	public static boolean isDebug() {
		return DEBUG;
	}

	/**
	 * 打印Warn开关
	 * 
	 * @return
	 */
	public static boolean isWarn() {
		return WARN;
	}

	/**
	 * 打印Error开关
	 * 
	 * @return
	 */
	public static boolean isError() {
		return ERROR;
	}

	/**
	 * 打印Info开关
	 * 
	 * @return
	 */
	public static boolean isInfo() {
		return INFO;
	}

	/**
	 * debug设置
	 * 
	 * @param debug
	 */
	public static void setDebug(boolean debug) {
		BoothLoger.DEBUG = debug;
	}

	/**
	 * warn设置
	 * 
	 * @param debug
	 */
	public static void setWarn(boolean warn) {
		BoothLoger.WARN = warn;
	}

	/**
	 * error设置
	 * 
	 * @param debug
	 */
	public static void setError(boolean error) {
		BoothLoger.ERROR = error;
	}

	/**
	 * info设置
	 * 
	 * @param debug
	 */
	public static void setInfo(boolean info) {
		BoothLoger.INFO = info;
	}

	/**
	 * 打印日志
	 * 
	 * @param msg
	 */
	public static void d(String msg) {
		if (DEBUG) {
			Log.d(LogerTag, "debug:" + msg);
		}
	}

	/**
	 * 打印日志
	 * 
	 * @param msg
	 */
	public static void d(String msg, Exception exception) {
		if (DEBUG) {
			Log.d(LogerTag, "debug:" + msg, exception);
		}
	}

	/**
	 * 打印日志
	 * 
	 * @param msg
	 */
	public static void e(String msg) {
		if (ERROR) {
			Log.e(LogerTag, "error:" + msg);
		}
	}

	/**
	 * 打印日志
	 * 
	 * @param msg
	 */
	public static void e(String msg, Exception exception) {
		if (ERROR) {
			Log.e(LogerTag, "error:" + msg, exception);
		}
	}

	/**
	 * 打印日志
	 * 
	 * @param msg
	 */
	public static void w(String msg, Exception exception) {
		if (WARN) {
			Log.w(LogerTag, "warn:" + msg, exception);
		}
	}

	/**
	 * 打印日志
	 * 
	 * @param msg
	 */
	public static void w(String msg) {
		if (WARN) {
			Log.w(LogerTag, "warn:" + msg);
		}
	}

	/**
	 * 打印日志
	 * 
	 * @param msg
	 */
	public static void i(String msg, Exception exception) {
		if (INFO) {
			Log.i(LogerTag, "info:" + msg, exception);
		}
	}

	/**
	 * 打印日志
	 * 
	 * @param msg
	 */
	public static void i(String msg) {
		if (INFO) {
			Log.i(LogerTag, "info:" + msg);
		}
	}

}
