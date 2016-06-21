package com.liangmayong.booth;

import android.bluetooth.BluetoothDevice;

/**
 * 蓝牙状态监听回调接口
 * 
 * @author LiangMaYong
 * @version 1.0
 */
public interface OnBoothStateListener {
	/**
	 * 蓝牙开/关状态
	 * 
	 * @param enable
	 *            状态
	 * @param ing
	 *            进行时
	 */
	void onEnableChanged(boolean enable, boolean ing);

	/**
	 * 绑定，解除绑定回调接口
	 * 
	 * @param device
	 *            蓝牙设备
	 * @param state
	 *            绑定状态
	 */
	void onBondChanged(BluetoothDevice device, int state);
}
