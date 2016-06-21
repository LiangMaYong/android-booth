#include <Booth.h>
Booth booth(&Serial,1024);
void setup(){
  Serial.begin(9600);
}
void loop(){
  booth.write("A ----------->");
  delay(300);
  booth.write("B ------------------->");
  delay(300);
  booth.write("C ------------------------->");
  delay(300);
}
