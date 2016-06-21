package com.liangmayong.booth;

import android.bluetooth.BluetoothDevice;

/**
 * 蓝牙设备搜索回调接口
 * 
 * @author LiangMaYong
 * @version 1.0
 */
public interface OnBoothSearchListener {
	/**
	 * 开始搜索设备
	 */
	void startSearch();

	/**
	 * 返回搜索结果
	 * 
	 * @param device
	 *            蓝牙设备
	 */
	void found(BluetoothDevice device);

	/**
	 * 停止搜索设备
	 */
	void stopSearch();
}
