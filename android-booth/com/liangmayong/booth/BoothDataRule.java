package com.liangmayong.booth;

/**
 * 蓝牙数据传输协议
 * 
 * @author LiangMaYong
 * @version 1.0
 */
class BoothDataRule implements StreamRuleListener {

	// 消息头规则
	@Override
	public boolean headerRule(byte[] headData, int length) {
		boolean flag = true;
		flag = flag && headData[0] == '<';
		flag = flag && headData[1] == '^';
		flag = flag && headData[length - 2] == '^';
		flag = flag && headData[length - 1] == '>';
		return flag;
	}

	// 消息头长度
	@Override
	public int headLength() {
		return 14;
	}

	// 从消息头中获取文件长度
	@Override
	public int contentLengthRule(byte[] headData, int length) {
		return Integer.parseInt(new String(headData, 2, 10));
	}

	// 解析消息内容
	@Override
	public void read(StreamRuleData kits) {
		int _len = kits.getTempLength();
		int _length = 0;
		do {
			if (_len >= headLength()) {
				_length = kits.getContentLength();
				if (_length == -1) {
					int index = kits.indexOf("<^", 1);
					if (index >= 0) {
						kits.subBytes(index + 1);
					} else {
						kits.reset();
					}
				} else if (_length == 0) {
					kits.subBytes(headLength());
				} else if (_length > 0) {
					if (_len >= _length + headLength()) {
						byte[] buf = kits.getData(headLength(), _length);
						if (buf != null) {
							kits.onReturn(buf);
							kits.subBytes(_length + headLength());
							int index = kits.indexOf("<^", 0);
							if (index > 0) {
								kits.subBytes(index);
							} else if (index == 0) {

							} else {
								kits.reset();
							}
						}
					}
				}
			}
			_len = kits.getTempLength();
		} while (_len >= _length + headLength());
	}

	// 内容格式化
	@Override
	public byte[] getDataFormat(byte[] contentData) {
		String header = "<^";
		int v = Integer.toString(contentData.length).length();
		if (v < 10) {
			for (int i = 0; i < 10 - v; i++) {
				header += "0";
			}
		}
		header += contentData.length;
		header += "^>";
		return StreamRuleData.byteMerger(header.getBytes(), contentData);
	}

}
