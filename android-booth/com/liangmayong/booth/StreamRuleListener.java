package com.liangmayong.booth;

/**
 * 数据传输流协议接口
 * 
 * @author LiangMaYong
 * @version 1.0
 */
interface StreamRuleListener {
	int headLength();

	boolean headerRule(byte[] headData, int length);

	int contentLengthRule(byte[] headData, int length);

	byte[] getDataFormat(byte[] contentData);

	void read(StreamRuleData kit);
}
