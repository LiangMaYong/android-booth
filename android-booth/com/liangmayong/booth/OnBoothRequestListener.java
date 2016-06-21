package com.liangmayong.booth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * 蓝牙请求回调接口
 * 
 * @author LiangMaYong
 * @version 1.0
 */
public interface OnBoothRequestListener {
	/**
	 * 连接成功
	 * 
	 * @param socket
	 *            BluetoothSocket
	 */
	void connectOk(BluetoothSocket socket);

	/**
	 * 连接中
	 * 
	 * @param device
	 *            蓝牙设备
	 * @param millis
	 *            连接倒计时
	 */
	void connecting(BluetoothDevice device, long millis);

	/**
	 * 停止请求连接
	 * 
	 * @param device
	 *            蓝牙设备
	 */
	void stopConnecting(BluetoothDevice device);
}
