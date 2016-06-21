package com.liangmayong.booth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

/**
 * 搜索设备监听器
 * 
 * @author LiangMaYong
 * @version 1.0
 */
public class BoothSearchMonitor {
	private boolean isFinding = false;
	private Handler handler = new Handler();

	/**
	 * 判断蓝牙是否处搜索设备状态
	 * 
	 * @return
	 */
	public boolean isSearching() {
		return isFinding;
	}

	private Context context;
	private OnBoothSearchListener findListener;

	public BoothSearchMonitor(OnBoothSearchListener findListener) {
		this.findListener = findListener;
	}

	/**
	 * 停止搜索
	 * 
	 * @param context
	 */
	public final synchronized void stopSearch(Context context) {
		this.context = context;
		if (BluetoothAdapter.getDefaultAdapter() == null) {
			return;
		}
		if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			return;
		}
		if (BluetoothAdapter.getDefaultAdapter().isDiscovering()) {
			BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
		}
	}

	private Runnable findRunnableTime = new Runnable() {
		@Override
		public void run() {
			if (isFinding) {
				if (findListener != null) {
					findListener.stopSearch();
				}
				isFinding = false;
			}
			try {
				handler.removeCallbacks(findRunnableTime);
			} catch (Exception e) {
			}
		}
	};

	/**
	 * 开始搜索
	 */
	public final synchronized void startSearch() {
		if (BluetoothAdapter.getDefaultAdapter() == null) {
			return;
		}
		if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			return;
		}
		if (BluetoothAdapter.getDefaultAdapter().isDiscovering()) {
			return;
		}
		isFinding = true;
		try {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
			intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			context.registerReceiver(receiverFind, intentFilter);
			BluetoothAdapter.getDefaultAdapter().startDiscovery();
			if (findListener != null) {
				findListener.startSearch();
			}
			handler.postDelayed(findRunnableTime, 12000);
		} catch (Exception e) {
		}
	}

	private final BroadcastReceiver receiverFind = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					if (isFinding) {
						if (findListener != null) {
							findListener.found(device);
						}
					}
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				if (isFinding) {
					if (findListener != null) {
						findListener.stopSearch();
					}
					try {
						handler.removeCallbacks(findRunnableTime);
					} catch (Exception e) {
					}
					isFinding = false;
					try {
						context.unregisterReceiver(receiverFind);
					} catch (Exception e) {
					}
				}
			}
		}
	};

}
