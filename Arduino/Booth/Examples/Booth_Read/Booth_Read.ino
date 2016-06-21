#include <Booth.h>
Booth booth(&Serial,1024);
void setup(){
  Serial.begin(9600);
  booth.OnRead = onRead;
}
void onRead(byte* msg,uint32_t length){
  String s = "";
  for(int i = 0;i < length;i++){
   s += char(msg[i]);
  }
  Serial.println(s);
}
void loop(){
   booth.read();
}
