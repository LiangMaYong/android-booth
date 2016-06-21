/*
 *  对蓝牙通讯的封装类库
 *  Created on: 2014/06/16
 *  Author: 梁马勇 e-mail:591694077@qq.com
 */
#ifndef BOOTH_H_
#define BOOTH_H_
#include <Stream.h>
#include <Arduino.h>
#include <string.h>
#include <float.h>
class Booth {
private:
	byte* data;
	int bytes;
	int isRule;
	int length;
	char floatBuffer[20];
	int maxLength;
	void sub(int start);
	int getIntlength(uint32_t uint);
	void clean();
	void copyBytes(byte* data, int offset, byte* buf, int start, int length);
	String byteToString(byte* data, int start, int length);
	void append(byte b);
	char* floatToString(char * outstr, double val, byte precision, byte widthp);
protected:
	Stream *stream_obj;
	virtual inline Stream *stream() {
		return stream_obj;
	}
public:
	/**
	* 构造函数
	* stream_ Serial
	* maxLength 缓存空间大小
	**/
	Booth(Stream *stream_, int maxLength) :
			stream_obj(stream_), bytes(0), isRule(0), length(0), maxLength(
					maxLength) {
		data = new byte[maxLength];
	}
	virtual ~Booth();
	//发送数据
	void write(String str);
	void write(char* chars);
	void write(uint32_t uint);
	void write(double ufloat);
	//断开连接 发送字符串<#o0#>
	void disconnect();
	int available();
	//接收数据
	void read();
	//接收数据回调函数
	void (*OnRead)(byte*, uint32_t);
};
#endif
