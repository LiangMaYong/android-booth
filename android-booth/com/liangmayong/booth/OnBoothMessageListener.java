package com.liangmayong.booth;

import android.bluetooth.BluetoothDevice;

/**
 * 蓝牙消息回调接口
 * 
 * @author LiangMaYong
 * @version 1.0
 */
public interface OnBoothMessageListener {
	/**
	 * 接收消息
	 * 
	 * @param device
	 *            发送方蓝牙设备
	 * @param data
	 *            接收数据
	 */
	void message(BluetoothDevice device, byte[] data);
}
