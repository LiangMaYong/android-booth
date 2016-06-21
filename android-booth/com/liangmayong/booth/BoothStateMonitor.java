package com.liangmayong.booth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 蓝牙状态监听器
 * 
 * @author LiangMaYong
 * @version 1.0
 */
public class BoothStateMonitor {
	private Context context;
	private boolean isListenering = false;
	private OnBoothStateListener stateListener;

	/**
	 * 判断是否正在监听蓝牙状态
	 * 
	 * @return
	 */
	public boolean isMonitoring() {
		return isListenering;
	}

	public BoothStateMonitor(OnBoothStateListener stateListener) {
		this.stateListener = stateListener;
	}

	/**
	 * 停止蓝牙状态的监听
	 */
	public final synchronized void stopMonitor() {
		isListenering = false;
		try {
			context.unregisterReceiver(receiverState);
		} catch (Exception e) {
		}
	}

	/**
	 * 开始监听蓝牙状态
	 * 
	 * @param context
	 */
	public final synchronized void startMonitor(Context context) {
		this.context = context;
		this.isListenering = true;
		try {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
			intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
			context.registerReceiver(receiverState, intentFilter);
		} catch (Exception e) {
		}
	}

	private final BroadcastReceiver receiverState = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (stateListener != null) {
					stateListener.onBondChanged(device, device.getBondState());
				}
			} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				int state = BluetoothAdapter.getDefaultAdapter().getState();
				boolean bstate = false;
				boolean bing = false;
				switch (state) {
				case BluetoothAdapter.STATE_OFF:
					bstate = false;
					bing = false;
					break;
				case BluetoothAdapter.STATE_ON:
					bstate = true;
					bing = false;
					break;
				case BluetoothAdapter.STATE_TURNING_ON:
					bstate = true;
					bing = true;
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
					bstate = false;
					bing = true;
					break;
				}
				if (stateListener != null) {
					stateListener.onEnableChanged(bstate, bing);
				}
			}
		}
	};

}
