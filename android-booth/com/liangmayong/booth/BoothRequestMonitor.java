package com.liangmayong.booth;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

/**
 * 蓝牙请求连接器
 * 
 * @author LiangMaYong
 * @version 1.0
 */
public class BoothRequestMonitor {
	private final UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private final int MSG_ID_CONNECT_ING = 0;
	private final int MSG_ID_CONNECT_DIS = 1;
	private final int MSG_ID_CONNECT_OK = 2;
	private long time = 0;
	private long outtime = 10000;
	private OnBoothRequestListener connectListener;
	private boolean isConnecting = false;
	private BluetoothAdapter adapter;
	private BluetoothDevice device;

	/**
	 * 判断蓝牙是否处请求连接状态
	 * 
	 * @return
	 */
	public boolean isRequesting() {
		return isConnecting;
	}

	public BoothRequestMonitor(OnBoothRequestListener connectListener) {
		this.connectListener = connectListener;
		this.adapter = BluetoothAdapter.getDefaultAdapter();
	}

	private BoothStateMonitor stateListener = new BoothStateMonitor(new OnBoothStateListener() {
		@Override
		public void onEnableChanged(boolean enable, boolean ing) {
			stopRequesting();
		}

		@Override
		public void onBondChanged(BluetoothDevice device, int state) {

		}
	});

	/**
	 * 开始请求连接
	 * 
	 * @param context
	 * @param device
	 *            连接的蓝牙设备
	 * @param millis
	 *            尝试时间长度
	 * @return
	 */
	public final synchronized boolean startRequesting(final Context context, final BluetoothDevice device,
			long millis) {
		if (isConnecting) {
			return false;
		}
		if (BoothManager.isConnected()) {
			return false;
		}
		if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
			return false;
		} else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
			stateListener.startMonitor(context);
			this.device = device;
			if (millis < 10000) {
				millis = 10000;
			}
			outtime = millis;
			time = System.currentTimeMillis();
			stopRequesting();
			connectThread = new ConnectThread();
			connectThread.start();// 开始连接
			sendMsg(MSG_ID_CONNECT_ING, device);
			connecthandler.postDelayed(runnable, 1000);
			return true;
		} else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
			createBond(device);
			return false;
		} else {
			return false;
		}
	}

	private final boolean createBond(BluetoothDevice device) {
		try {
			Method method = device.getClass().getMethod("createBond");
			Boolean reBoolean = (Boolean) method.invoke(device);
			return reBoolean.booleanValue();
		} catch (Exception e) {
		}
		return false;
	}

	private Runnable runnable = new Runnable() {
		public void run() {
			if (isConnecting) {
				if (System.currentTimeMillis() - time > outtime) {
					stopRequesting();
				} else {
					sendMsg(MSG_ID_CONNECT_ING, device);
					connecthandler.postDelayed(runnable, 1000);
				}
			} else {
				stopRequesting();
			}
		}
	};

	/**
	 * 停止请求
	 */
	public final synchronized void stopRequesting() {
		if (isConnecting) {
			sendMsg(MSG_ID_CONNECT_DIS, device);
		}
		try {
			connecthandler.removeCallbacks(runnable);
		} catch (Exception e) {
		}
		if (connectThread != null) {
			connectThread.cancel();
			connectThread = null;
		}
		isConnecting = false;
		stateListener.stopMonitor();
	}

	private ConnectThread connectThread;

	private class ConnectThread extends Thread {
		private boolean isStop = false;

		public ConnectThread() {
			isStop = false;
		}

		@SuppressLint("NewApi")
		private BluetoothSocket selectSocket() {
			BluetoothSocket tmp;
			try {
				if (Build.VERSION.SDK_INT >= 10) {
					tmp = device.createInsecureRfcommSocketToServiceRecord(UUID_SPP);
				} else {
					tmp = device.createRfcommSocketToServiceRecord(UUID_SPP);
				}
				return tmp;
			} catch (IOException e) {
			}
			return null;
		}

		@SuppressLint("NewApi")
		@Override
		public void run() {
			if (adapter == null) {
				stopRequesting();
			}
			if (!adapter.isEnabled()) {
				stopRequesting();
			}
			while (!isStop) {
				try {
					BluetoothSocket btSocket = selectSocket();
					if (btSocket == null) {
						throw new IOException("create socket is null.");
					}
					btSocket.connect();
					if (isConnecting) {
						sendMsg(MSG_ID_CONNECT_OK, btSocket);
					} else {
						try {
							btSocket.close();
							btSocket = null;
						} catch (Exception e) {
						}
					}
					break;
				} catch (Exception e) {
					BoothLoger.d("Request Connect fail", e);
					try {
						sleep(500);
					} catch (InterruptedException e2) {
					}
				}
			}
		}

		public void cancel() {
			isStop = true;
		}
	}

	private void sendMsg(int msgid, Object object) {
		switch (msgid) {
		case MSG_ID_CONNECT_DIS:
			isConnecting = false;
			connecthandler.obtainMessage(msgid, object).sendToTarget();
			stopRequesting();
			break;
		case MSG_ID_CONNECT_ING:
			isConnecting = true;
			connecthandler.obtainMessage(msgid, object).sendToTarget();
			break;
		case MSG_ID_CONNECT_OK:
			isConnecting = false;
			connecthandler.obtainMessage(msgid, object).sendToTarget();
			stopRequesting();
			break;
		default:
			break;
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler connecthandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_ID_CONNECT_ING:
				if (connectListener != null) {
					BluetoothDevice device = null;
					try {
						device = (BluetoothDevice) msg.obj;
					} catch (Exception e) {
					}
					long millis = outtime - (System.currentTimeMillis() - time) > 0
							? outtime - (System.currentTimeMillis() - time) : 0;
					connectListener.connecting(device, millis);
				}
				break;
			case MSG_ID_CONNECT_DIS:
				if (connectListener != null) {
					BluetoothDevice device = null;
					try {
						device = (BluetoothDevice) msg.obj;
					} catch (Exception e) {
					}
					connectListener.stopConnecting(device);
				}
				break;
			case MSG_ID_CONNECT_OK:
				if (connectListener != null) {
					BluetoothSocket socket = null;
					try {
						socket = (BluetoothSocket) msg.obj;
					} catch (Exception e) {
					}
					connectListener.connectOk(socket);
				}
				break;
			}
		}
	};
}
