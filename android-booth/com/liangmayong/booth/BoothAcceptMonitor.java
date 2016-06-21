package com.liangmayong.booth;

import java.io.IOException;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

/**
 * 蓝牙请求接收器
 * 
 * @author LiangMaYong
 * @version 1.0
 */
public class BoothAcceptMonitor {
	private final UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private final String TAG = "IConBluetooth";
	private final int MSG_ID_ACCEPT_OK = 0;// 连接成功
	private final int MSG_ID_ACCEPT_ING = 1;// 监听中
	private final int MSG_ID_ACCEPT_DIS = 2;// 未监听
	private AcceptThread acceptThread = null;
	private boolean isAccepting = false;
	private long outtime = 10000;
	private long time = 0;
	private OnBoothAcceptListener acceptListener;
	/**
	 * 监听蓝牙状态，当蓝牙状态改变时停止监听
	 */
	private BoothStateMonitor stateListener = new BoothStateMonitor(new OnBoothStateListener() {
		@Override
		public void onEnableChanged(boolean enable, boolean ing) {
			stopAccepting();
		}

		@Override
		public void onBondChanged(BluetoothDevice device, int state) {

		}
	});

	public BoothAcceptMonitor(OnBoothAcceptListener acceptListener) {
		this.acceptListener = acceptListener;
	}

	/**
	 * 判断蓝牙是否处请求等待连接中状态
	 * 
	 * @return
	 */
	public boolean isAccepting() {
		return isAccepting;
	}

	/**
	 * 开始等待连接
	 * 
	 * @param context
	 * @param millis
	 *            等待时长
	 * @return
	 */
	public final synchronized boolean startAccepting(Context context, long millis) {
		if (isAccepting) {
			return false;
		}
		if (BoothManager.isConnected()) {
			return false;
		}
		stateListener.startMonitor(context);
		if (millis < 10000) {
			millis = 10000;
		}
		outtime = millis;
		time = System.currentTimeMillis();
		stopAccepting();
		acceptThread = new AcceptThread();
		acceptThread.start();
		sendMsg(MSG_ID_ACCEPT_ING, null);
		accepthandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (BoothManager.isConnected()) {
					stopAccepting();
					accepthandler.removeCallbacks(this);
				} else {
					if (isAccepting) {
						if (System.currentTimeMillis() - time > outtime) {
							stopAccepting();
							accepthandler.removeCallbacks(this);
						} else {
							sendMsg(MSG_ID_ACCEPT_ING, null);
							accepthandler.postDelayed(this, 1000);
						}
					} else {
						accepthandler.removeCallbacks(this);
					}
				}
			}
		}, 1000);
		return true;
	}

	/**
	 * 停止等待
	 */
	public final synchronized void stopAccepting() {
		if (isAccepting) {
			stateListener.stopMonitor();
			sendMsg(MSG_ID_ACCEPT_DIS, null);
		}
		isAccepting = false;
		if (acceptThread != null) {
			acceptThread.cancel();
			acceptThread = null;
		}
	}

	// 发送消息
	private void sendMsg(int msgid, Object object) {
		switch (msgid) {
		case MSG_ID_ACCEPT_ING:
			isAccepting = true;
			accepthandler.obtainMessage(msgid, object).sendToTarget();
			break;
		case MSG_ID_ACCEPT_DIS:
			if (acceptThread != null) {
				acceptThread.cancel();
				acceptThread = null;
			}
			isAccepting = false;
			accepthandler.obtainMessage(msgid, object).sendToTarget();
			break;
		case MSG_ID_ACCEPT_OK:
			if (acceptThread != null) {
				acceptThread.cancel();
				acceptThread = null;
			}
			isAccepting = false;
			accepthandler.obtainMessage(msgid, object).sendToTarget();
			break;
		default:
			break;
		}
	}

	/************************* 蓝牙请求接收线程 *****************************/
	private class AcceptThread extends Thread {
		private final BluetoothServerSocket serverSocket;
		private boolean isStop = false;

		// 初始化线程
		@SuppressLint("NewApi")
		public AcceptThread() {
			BluetoothServerSocket tmpSS = null;
			try {
				if (Build.VERSION.SDK_INT >= 10) {
					tmpSS = BluetoothAdapter.getDefaultAdapter().listenUsingInsecureRfcommWithServiceRecord(TAG,
							UUID_SPP);
				} else {
					tmpSS = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(TAG, UUID_SPP);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			isStop = false;
			serverSocket = tmpSS;
		}

		// 开始监听连接
		public void run() {
			BluetoothSocket btSocket = null;
			while (!isStop) {
				try {
					btSocket = serverSocket.accept();
				} catch (Exception e) {
					break;
				}
				if (btSocket != null) {
					sendMsg(MSG_ID_ACCEPT_OK, btSocket);
					break;
				}
			}
		}

		// 取消监听
		public void cancel() {
			isStop = true;
			try {
				serverSocket.close();
			} catch (Exception e) {
			}
		}
	}

	/****************** 蓝牙请消息机制 ********************/
	@SuppressLint("HandlerLeak")
	private Handler accepthandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_ID_ACCEPT_ING:// 等待连接中
				long millis = outtime - (System.currentTimeMillis() - time) > 0
						? outtime - (System.currentTimeMillis() - time) : 0;
				if (acceptListener != null) {
					acceptListener.accepting(millis);
				}
				break;
			case MSG_ID_ACCEPT_DIS:// 停止请求连接
				if (acceptListener != null) {
					acceptListener.stopAccepting();
				}
				break;
			case MSG_ID_ACCEPT_OK:// 连接成功
				BluetoothSocket socket = null;
				try {
					socket = (BluetoothSocket) msg.obj;
				} catch (Exception e) {
				}
				if (acceptListener != null) {
					acceptListener.acceptOk(socket);
				}
				break;
			}
		}
	};

}
