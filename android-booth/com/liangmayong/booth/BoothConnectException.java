package com.liangmayong.booth;

/**
 * 蓝牙未连接操作异常
 * 
 * @author LiangMaYong
 * @version 1.0
 */
public class BoothConnectException extends Exception {
	private static final long serialVersionUID = -4727291290608759488L;

	public BoothConnectException(Exception e) {
		super(e);
	}

	public BoothConnectException() {
		super("Connection lost");
	}
}
