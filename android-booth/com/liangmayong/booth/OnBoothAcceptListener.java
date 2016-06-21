package com.liangmayong.booth;

import android.bluetooth.BluetoothSocket;

/**
 * 蓝牙请求接收回调接口
 * 
 * @author LiangMaYong
 * @version 1.0
 */
public interface OnBoothAcceptListener {
	/**
	 * 连接成功
	 * 
	 * @param socket
	 */
	void acceptOk(BluetoothSocket socket);

	/**
	 * 等待连接中
	 * 
	 * @param millis
	 *            停止等待倒计时
	 */
	void accepting(long millis);

	/**
	 * 停止监听连接
	 */
	void stopAccepting();
}
