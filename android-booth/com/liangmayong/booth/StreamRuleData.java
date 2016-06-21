package com.liangmayong.booth;

import java.io.ByteArrayOutputStream;

/**
 * 数据传输数据协议接口
 * 
 * @author LiangMaYong
 * @version 1.0
 */
abstract class StreamRuleData {
	private StreamRuleListener listener;

	public StreamRuleData(StreamRuleListener listener) {
		this.listener = listener;
		arrayOutputStream = new ByteArrayOutputStream();
	}

	private ByteArrayOutputStream arrayOutputStream;

	protected abstract void onReturn(byte[] data);

	public int headLength() {
		if (listener != null) {
			return listener.headLength();
		}
		return 0;
	}

	/**
	 * 头文件规则，返回是否符合格式
	 * 
	 * @param headData
	 * @param length
	 * @return
	 */
	public boolean headerRule(byte[] headData, int length) {
		if (listener != null) {
			return listener.headerRule(headData, length);
		}
		return false;
	}

	/**
	 * 内容长度规则，返回内容长度
	 * 
	 * @param headData
	 * @param length
	 * @return
	 */
	public int contentLengthRule(byte[] headData, int length) {
		if (listener != null) {
			return listener.contentLengthRule(headData, length);
		}
		return 0;
	}

	/**
	 * 获取数据总长度
	 * 
	 * @return
	 */
	public int getTempLength() {
		return arrayOutputStream.size();
	}

	/**
	 * 读取数据
	 * 
	 * @param start
	 * @param len
	 * @return
	 */
	public byte[] getData(int start, int len) {
		try {
			if (arrayOutputStream.size() >= len + start) {
				byte[] buf = new byte[len];
				System.arraycopy(arrayOutputStream.toByteArray(), start, buf, 0, len);
				return buf;
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 验证数据格式
	 * 
	 * @param data
	 * @return
	 */
	public boolean isDataValid(byte[] data) {
		if (data.length <= headLength()) {
			BoothLoger.d("data.length <= headLength()");
			return false;
		}
		byte[] headData = new byte[headLength()];
		System.arraycopy(data, 0, headData, 0, headLength());
		if (headerRule(headData, headLength())) {
			int _length = contentLengthRule(headData, headLength());
			if (_length > 0 && data.length == _length + headLength()) {
				return true;
			}
			BoothLoger.d(_length + " / " + headLength() + " / " + data.length);
			return false;
		} else {
			BoothLoger.d("! headerRule()");
			return false;
		}
	}

	/**
	 * 在数据中查找某字符串
	 * 
	 * @param str
	 * @param start
	 * @return
	 */
	public int indexOf(String str, int start) {
		if (start < 0) {
			start = 0;
		}
		try {
			if (arrayOutputStream.size() > start) {
				byte[] dys = getData(start, arrayOutputStream.size() - start);
				if (dys != null) {
					return (new String(dys)).indexOf(str);
				}
			}
		} catch (Exception e) {
		}
		return -1;
	}

	/**
	 * 在数据中查找某字符串
	 * 
	 * @param str
	 * @param start
	 * @param len
	 * @return
	 */
	public int indexOf(String str, int start, int len) {
		if (start < 0) {
			start = 0;
		}
		try {
			if (arrayOutputStream.size() > start) {
				byte[] dys = getData(start,
						len > arrayOutputStream.size() - start ? arrayOutputStream.size() - start : len);
				if (dys != null) {
					return (new String(dys)).indexOf(str);
				}
			}
		} catch (Exception e) {
		}
		return -1;
	}

	/**
	 * 获取内容格式 封装头文件
	 * 
	 * @param data
	 * @return
	 */
	public byte[] getDataFormat(byte[] data) {
		if (listener != null) {
			return listener.getDataFormat(data);
		}
		return data;
	}

	/**
	 * 写入数据
	 * 
	 * @param b
	 * @param start
	 * @param length
	 */
	public void write(byte[] b, int start, int length) {
		try {
			synchronized (arrayOutputStream) {
				if (length + start > b.length) {
					length = b.length - start;
				}
				arrayOutputStream.write(b, start, length);
				if (listener != null) {
					listener.read(this);
				}
			}
		} catch (Exception e) {
		}
	}

	@Override
	public String toString() {
		return "length:" + getTempLength() + " data:" + arrayOutputStream.toString();
	}

	/**
	 * 获取下一条数据内容长度 -2表示当前数据长度不够头文件长度 -1表示当前数据长度够头文件长度，但头文件格式不正确
	 * 
	 * @return
	 */
	public int getContentLength() {
		if (getTempLength() < headLength()) {
			return -2;
		}
		byte[] headData = getData(0, headLength());
		if (headData != null) {
			if (headerRule(headData, headLength())) {
				return contentLengthRule(headData, headLength());
			} else {
				return -1;
			}
		} else {
			return -2;
		}
	}

	/**
	 * 写入数据
	 * 
	 * @param b
	 */
	public void write(byte[] b) {
		write(b, 0, b.length);
	}

	/**
	 * 写入数据
	 * 
	 * @param b
	 * @param start
	 */
	public void write(byte[] b, int start) {
		write(b, start, b.length);
	}

	/**
	 * 初始化数据
	 */
	public void reset() {
		arrayOutputStream.reset();
	}

	/**
	 * 剔除前start个数据
	 * 
	 * @param start
	 */
	public void subBytes(int start) {
		try {
			if (start < 0) {
				start = 0;
			}
			if (start >= arrayOutputStream.size() - 1) {
				arrayOutputStream.reset();
			} else {
				byte[] data = arrayOutputStream.toByteArray();
				arrayOutputStream.reset();
				arrayOutputStream.write(data, start, data.length - start);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 数组合并
	 * 
	 * @param byte_1
	 * @param byte_2
	 * @return
	 */
	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
		byte[] byte_3 = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}
}
