/**
 * 	对蓝牙通讯的封装类库
 *  Created on: 2014/06/16
 *  Author: 梁马勇   e-mail:591694077@qq.com
 */
#include "Booth.h"
#include <Arduino.h>
#include <stdlib.h>
#include <string.h>
#include <float.h>
//析构器
Booth::~Booth() {
}
void Booth::copyBytes(byte* data, int offset, byte* buf, int start,
		int length) {
	for (int i = start; i < start + length; i++) {
		buf[i] = data[offset + i];
	}
}
int Booth::getIntlength(uint32_t uint) {
	int len = 0;
	if (uint < 0) {
		len++;
		uint = 0 - uint;
	}
	if (uint < 10) {
		len += 1;
	} else if (uint < 100) {
		len += 2;
	} else if (uint < 1000) {
		len += 3;
	} else if (uint < 10000) {
		len += 4;
	} else if (uint < 100000) {
		len += 5;
	} else if (uint < 1000000) {
		len += 6;
	} else if (uint < 10000000) {
		len += 7;
	} else if (uint < 100000000) {
		len += 8;
	} else if (uint < 1000000000) {
		len += 9;
	} else {
		len += 10;
	}
	return len;
}
void Booth::write(String str) {
	stream()->print("<^");
	uint32_t len = str.length();
	int llen = getIntlength(len);
	if (llen < 10) {
		for (int i = 0; i < 10 - llen; i++) {
			stream()->print("0");
		}
		stream()->print(len);
		stream()->print("^>");
		stream()->print(str);
		stream()->println("\0");
	} else if (llen == 0) {
		stream()->print(len);
		stream()->print("^>");
		stream()->print(str);
		stream()->println("\0");
	}
}
void Booth::write(char* chars) {
	String msg = "";
	msg += chars;
	write(msg);
}
void Booth::write(double ufloat) {
	write(floatToString(floatBuffer, ufloat, 3, 7));
}
char* Booth::floatToString(char * outstr, double val, byte precision,
		byte widthp) {
	char temp[16];
	byte i;
	temp[0] = '\0';
	outstr[0] = '\0';
	if (val < 0.0) {
		strcpy(outstr, "-\0");
		val *= -1;
	}
	if (precision == 0) {
		strcat(outstr, ltoa(round(val), temp, 10));  //prints the int part
	} else {
		unsigned long frac, mult = 1;
		byte padding = precision - 1;
		while (precision--)
			mult *= 10;
		val += 0.5 / (float) mult;
		strcat(outstr, ltoa(floor(val), temp, 10));
		strcat(outstr, ".\0");
		frac = (val - floor(val)) * mult;
		unsigned long frac1 = frac;
		while (frac1 /= 10)
			padding--;
		while (padding--)
			strcat(outstr, "0\0");
		strcat(outstr, ltoa(frac, temp, 10));
	}
	if ((widthp != 0) && (widthp >= strlen(outstr))) {
		byte J = 0;
		J = widthp - strlen(outstr);
		for (i = 0; i < J; i++) {
			temp[i] = ' ';
		}
		temp[i++] = '\0';
		strcat(temp, outstr);
		strcpy(outstr, temp);
	}
	return outstr;
}
void Booth::write(uint32_t uint) {
	String msg = "";
	msg += uint;
	write(msg);
}
void Booth::disconnect() {
	write("<#o0#>");
}
int Booth::available() {
	return stream()->available();
}
void Booth::read() {
	while (stream()->available() > 0) {
		byte s = stream()->read();
		append(s);
		delay(2);
	}
}

String Booth::byteToString(byte* data, int start, int length) {
	String str = "";
	for (int i = 0; i < length; i++) {
		str += char(data[start + i]);
	}
	return str;
}
void Booth::clean() {
	for (int i = 0; i < bytes; i++) {
		data[i] = 0;
	}
	bytes = 0;
}
void Booth::sub(int start) {
	for (int i = 0; i < bytes; i++) {
		if (start + i >= bytes) {
			data[i] = 0;
		} else {
			data[i] = data[start + i];
		}
	}
	bytes -= start;
}

void Booth::append(byte b) {
	data[bytes++] = b;
	if (bytes >= 14) {
		if (data[0] == '<' && data[1] == '^' && data[12] == '^'
				&& data[13] == '>') {
			uint32_t length = byteToString(data, 2, 10).toInt();
			if (bytes >= 14 + length) {
				if (OnRead) {
					byte buf[length];
					copyBytes(data, 14, buf, 0, length);
					(OnRead)(buf, length);
				}
				sub(14 + length);
			}
		} else {
			sub(1);
		}
	}
}

