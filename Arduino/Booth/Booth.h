/*
 *  ������ͨѶ�ķ�װ���
 *  Created on: 2014/06/16
 *  Author: ������ e-mail:591694077@qq.com
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
	* ���캯��
	* stream_ Serial
	* maxLength ����ռ��С
	**/
	Booth(Stream *stream_, int maxLength) :
			stream_obj(stream_), bytes(0), isRule(0), length(0), maxLength(
					maxLength) {
		data = new byte[maxLength];
	}
	virtual ~Booth();
	//��������
	void write(String str);
	void write(char* chars);
	void write(uint32_t uint);
	void write(double ufloat);
	//�Ͽ����� �����ַ���<#o0#>
	void disconnect();
	int available();
	//��������
	void read();
	//�������ݻص�����
	void (*OnRead)(byte*, uint32_t);
};
#endif
