package com.liangmayong.booth;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

/**
 * 蓝牙管理器
 * 
 * @author LiangMaYong
 * @version 1.0
 */
public class BoothManager {

	static BoothConnecion connection = null;

	/**
	 * 判断蓝牙是否处于连接状态
	 * 
	 * @return
	 */
	public static boolean isConnected() {
		if (connection != null) {
			return connection.isConnected();
		} else {
			return false;
		}
	}

	/**
	 * 断开蓝牙连接
	 */
	public static void disconnect() {
		if (connection != null) {
			connection.disconnect();
			connection = null;
		}
	}

	private static OnBoothDisconnectListener disconnectListener = null;
	private static OnBoothMessageListener messageListener = null;

	/**
	 * 创建蓝牙连接
	 * 
	 * @param context
	 * @param socket
	 */
	public static void connected(Context context, BluetoothSocket socket) {
		connection = connect(context, socket, disconnectListener, messageListener);
	}

	/**
	 * 设置蓝牙消息监听接口
	 * 
	 * @param messageListener
	 */
	public static void setOnBoothMessageListener(OnBoothMessageListener messageListener) {
		BoothManager.messageListener = messageListener;
		if (connection != null) {
			connection.setOnBoothMessageListener(messageListener);
		}
	}

	/**
	 * 设置蓝牙断开连接监听接口
	 * 
	 * @param disconnectListener
	 */
	public static void setOnBoothDisconnectListener(OnBoothDisconnectListener disconnectListener) {
		BoothManager.disconnectListener = disconnectListener;
		if (connection != null) {
			connection.setOnBoothDisconnectListener(disconnectListener);
		}
	}

	private static BluetoothAdapter adapter;
	static {
		adapter = BluetoothAdapter.getDefaultAdapter();
	}

	private BoothManager() {
	}

	/**
	 * 判断蓝牙是否处于发现设备状态
	 * 
	 * @return
	 */
	public static final boolean isDiscovering() {
		if (!isEnabled()) {
			return false;
		}
		return BluetoothAdapter.getDefaultAdapter().isDiscovering();
	}

	private static final boolean setPin(BluetoothDevice device, String pin) {
		try {
			Method method = device.getClass().getMethod("setPin", new Class[] { byte[].class });
			Boolean reBoolean = (Boolean) method.invoke(device, new Object[] { pin.getBytes() });
			return reBoolean.booleanValue();
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 蓝牙已经配对列表
	 * 
	 * @return
	 */
	public static final List<BluetoothDevice> getBluetoothDevices() {
		if (!isEnabled()) {
			return new ArrayList<BluetoothDevice>();
		}
		Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
		List<BluetoothDevice> list = new ArrayList<BluetoothDevice>();
		for (BluetoothDevice device : pairedDevices) {
			list.add(device);
		}
		return list;
	}

	/**
	 * 根据地址获取蓝牙设备
	 * 
	 * @param address
	 * @return
	 */
	public static final BluetoothDevice getRemoteDevice(String address) {
		if (adapter != null) {
			return adapter.getRemoteDevice(address);
		}
		return null;
	}

	/**
	 * 开启蓝牙
	 * 
	 * @return
	 */
	public static final boolean openBluetooth() {
		if (adapter == null) {
			return false;
		}
		if (!adapter.isEnabled()) {
			adapter.enable();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 关闭蓝牙
	 * 
	 * @return
	 */
	public static final boolean closeBluetooth() {
		if (adapter == null) {
			return false;
		}
		if (adapter.isEnabled()) {
			adapter.disable();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 取消配对
	 * 
	 * @param device
	 * @return
	 */
	public static final boolean removeBond(BluetoothDevice device) {
		try {
			Method method = device.getClass().getMethod("removeBond");
			try {
				Boolean reBoolean = (Boolean) method.invoke(device);
				return reBoolean.booleanValue();
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
		} catch (NoSuchMethodException e) {
		}
		return false;
	}

	/**
	 * 蓝牙状态
	 * 
	 * @return
	 */
	public static final boolean isEnabled() {
		if (adapter == null) {
			return false;
		}
		if (adapter.getState() == BluetoothAdapter.STATE_ON) {
			return true;
		}
		return false;
	}

	/**
	 * 蓝牙配对
	 * 
	 * @param device
	 * @return
	 */
	public static final boolean createBond(BluetoothDevice device) {
		try {
			Method method = device.getClass().getMethod("createBond");
			Boolean reBoolean = (Boolean) method.invoke(device);
			return reBoolean.booleanValue();
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 蓝牙配对
	 * 
	 * @param device
	 * @param pin
	 * @return
	 */
	public static final boolean createBond(BluetoothDevice device, String pin) {
		setPin(device, pin);
		return createBond(device);
	}

	private static BoothConnecion connect(Context context, BluetoothSocket socket,
			final OnBoothDisconnectListener disconnectListener, OnBoothMessageListener messageListener) {
		BoothConnecion connected = new BoothConnecion();
		connected.connected(context, socket);
		connected.setOnBoothDisconnectListener(new OnBoothDisconnectListener() {
			@Override
			public void disconnect() {
				disconnectListener.disconnect();
				connection = null;
			}
		});
		connected.setOnBoothMessageListener(messageListener);
		return connected;
	}

	/**
	 * 获取连接的BluetoothSocket 未连接时返回null
	 * 
	 * @return
	 */
	public static BluetoothSocket getConnectedSocket() {
		if (connection != null) {
			return connection.getSocket();
		}
		return null;
	}

	/**
	 * 获取连接的BluetoothDevice 未连接时返回null
	 * 
	 * @return
	 */
	public static BluetoothDevice getConnectedDevice() {
		if (connection != null) {
			return connection.getDevice();
		}
		return null;
	}

	/**
	 * 发送数据
	 * 
	 * @param udouble
	 * @throws IOException
	 */
	public static void write(double udouble) throws BoothConnectException {
		if (!isConnected()) {
			throw new BoothConnectException();
		}
		connection.write(udouble);
	}

	/**
	 * 发送数据
	 * 
	 * @param uint
	 * @throws IOException
	 */
	public static void write(int uint) throws BoothConnectException {
		if (!isConnected()) {
			throw new BoothConnectException();
		}
		connection.write(uint);
	}

	/**
	 * 发送数据
	 * 
	 * @param msg
	 * @throws BoothConnectException
	 */
	public static void write(String msg) throws BoothConnectException {
		if (!isConnected()) {
			throw new BoothConnectException();
		}
		connection.write(msg);
	}

	/**
	 * 发送数据
	 * 
	 * @param bytes
	 * @throws BoothConnectException
	 */
	public static void write(byte[] bytes) throws BoothConnectException {
		if (!isConnected()) {
			throw new BoothConnectException();
		}
		connection.write(bytes);
	}
}
