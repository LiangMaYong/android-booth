# android-booth
android booth 蓝牙开发封装库
## 消息协议
消息头用<^和^>包括的是消息长度，长度为10位，后面接着是消息内容

如：

消息：“booth”  的发送的实际内容为： “ <^0000000005^>booth”  

```
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
```
##技术交流
交流：QQ群297798093，博主QQ591694077

email：ibeam@qq.com
