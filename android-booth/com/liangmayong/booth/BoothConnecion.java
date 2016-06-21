package com.liangmayong.booth;

import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;

/**
 * 蓝牙连接器
 * 
 * @author LiangMaYong
 * @version 1.0
 */
class BoothConnecion {
	private final int MSG_ID_CONNECT_DIS = 0;
	private final int MSG_ID_CONNECT_ED = 1;
	private final int MSG_ID_CONNECT_READ_DATA = 2;
	private BluetoothSocket socket;
	private BluetoothDevice device;
	private boolean isConnected = false;
	private BoothOutputStream data = new BoothOutputStream() {
		@Override
		protected void onReturn(byte[] data) {
			if (data.length == "<#o0#>".getBytes().length && "<#o0#>".equals(new String(data))) {
				disconnect();
			} else {
				handler.obtainMessage(MSG_ID_CONNECT_READ_DATA, data).sendToTarget();
			}
		}
	};

	public BoothConnecion() {
	}

	public BluetoothSocket getSocket() {
		return socket;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public BluetoothDevice getDevice() {
		return device;
	}

	private BoothStateMonitor stateMonitor = new BoothStateMonitor(new OnBoothStateListener() {

		@Override
		public void onEnableChanged(boolean enable, boolean ing) {
			disconnect();
		}

		@Override
		public void onBondChanged(BluetoothDevice device, int state) {

		}
	});

	public boolean disconnect() {
		if (!isConnected) {
			return false;
		}
		if (getDevice() != null) {
			BoothLoger.d("Booth Disconnect");
			BoothLoger.d("Device:" + getDevice().getName());
			BoothLoger.d("Address:" + getDevice().getAddress());
		} else {
			BoothLoger.d("Booth Disconnect");
			BoothLoger.d("Device:  -- ");
			BoothLoger.d("Address:  -- ");
		}
		stateMonitor.stopMonitor();
		try {
			if (socket.getOutputStream() != null) {
				socket.getOutputStream().write(data.getDataFormat("<#o0#>".getBytes()));
				socket.getOutputStream().close();
			}
		} catch (Exception e) {
		}
		try {
			if (socket.getInputStream() != null) {
				socket.getInputStream().close();
			}
		} catch (Exception e) {
		}
		if (socket != null) {
			try {
				socket.close();
				socket = null;
			} catch (IOException e) {
			}
		}
		if (device != null) {
			device = null;
		}
		handler.sendEmptyMessage(MSG_ID_CONNECT_DIS);
		isConnected = false;
		unRead();
		return true;
	}

	public boolean connected(Context context, BluetoothSocket socket) {
		if (isConnected) {
			try {
				socket.close();
			} catch (Exception e) {
			}
			return false;
		}
		try {
			BoothLoger.d("Booth connected");
			BoothLoger.d("Device:" + socket.getRemoteDevice().getName());
			BoothLoger.d("Address:" + socket.getRemoteDevice().getAddress());
		} catch (Exception e) {
		}
		stateMonitor.startMonitor(context);
		if (socket != null) {
			this.socket = socket;
			try {
				this.device = socket.getRemoteDevice();
			} catch (Exception e) {
			}
		}
		isConnected = true;
		handler.sendEmptyMessage(MSG_ID_CONNECT_ED);
		data.reset();
		onRead();
		return true;
	}

	private BoothMsgLooper looper;

	private void onRead() {
		unRead();
		looper = new BoothMsgLooper();
		looper.start();
	}

	private void unRead() {
		if (looper != null) {
			looper.cancel();
			looper = null;
		}
	}

	private void onReadData(BluetoothDevice device, byte[] data) {
		if (messageListener != null) {
			messageListener.message(device, data);
		}
	}

	private void onDisconnect() {
		if (disconnectListener != null) {
			disconnectListener.disconnect();
		}
	}

	private OnBoothMessageListener messageListener;

	public void setOnBoothMessageListener(OnBoothMessageListener messageListener) {
		this.messageListener = messageListener;
	}

	private OnBoothDisconnectListener disconnectListener;

	public void setOnBoothDisconnectListener(OnBoothDisconnectListener disconnectListener) {
		this.disconnectListener = disconnectListener;
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_ID_CONNECT_READ_DATA:
				try {
					onReadData(device, (byte[]) msg.obj);
				} catch (Exception e) {
				}
				break;
			case MSG_ID_CONNECT_DIS:
				onDisconnect();
				break;
			}
		}
	};

	private class BoothWrite extends Thread {
		private byte[] data;

		public BoothWrite(byte[] data) {
			this.data = data;
		}

		@Override
		public void run() {
			try {
				if (socket.getOutputStream() != null) {
					socket.getOutputStream().write(data);
				}
			} catch (Exception e) {
				disconnect();
			}
		}

	}

	byte[] buffer = new byte[4096];

	private class BoothMsgLooper extends Thread {
		private boolean isStop = false;
		Exception ex = null;

		public void cancel() {
			this.isStop = true;
		}

		public BoothMsgLooper() {
		}

		@Override
		public void run() {
			while (true) {
				if (socket == null) {
					throw new RuntimeException("connect lost.");
				}
				try {
					int temp = 0;
					InputStream inputStream = socket.getInputStream();
					while (!isStop && (temp = inputStream.read(buffer)) > 0) {
						data.write(buffer, 0, temp);
					}
					if (isStop) {
						throw new RuntimeException("click stop");
					}
				} catch (Exception e) {
					ex = e;
					break;
				}
			}
			try {
				stop(ex);
			} catch (Exception e) {
			}
		}

		protected void stop(Exception e) {
			BoothLoger.d("Read message stop", e);
			disconnect();
		}
	}

	public void write(double udouble) {
		write(udouble + "");
	}

	public void write(int uint) {
		write(uint + "");
	}

	public void write(String msg) {
		write(msg.getBytes());
	}

	public void write(byte[] bytes) {
		BoothLoger.d("Write data length is " + bytes.length);
		if (bytes != null && bytes.length > 0) {
			byte[] buf = data.getDataFormat(bytes);
			if (data.isDataValid(buf)) {
				new BoothWrite(buf).start();
			} else {
				BoothLoger.d("length:" + bytes.length);
				BoothLoger.d(new String(buf));
				BoothLoger.d("Write Data Is Invalid");
			}
		}
	}
}
