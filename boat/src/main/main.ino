#include <Servo.h>

//change motor1 and motor2!!
int vPower = 0;
int hPower = 0;
int in1 = 11;
int in2 = 10;
int in3 = 9;
int in4 = 8;



Servo rudder;

void setup()
{
 
  pinMode (in1, OUTPUT);
  pinMode (in2, OUTPUT);
  pinMode (in3, OUTPUT);
  pinMode (in4, OUTPUT);
    
  rudder.attach(9);
  
  
  Serial.begin(9600);
}
void loop () {

if (Serial.available()>0)
{
  vPower = Serial.read();
  hPower = Serial.read();
}
vPower = Serial.parseInt();
hPower = Serial.parseInt();

int speed1; //speed of righthand motor
int speed2; //speed of lefthand motor

//forward thrust
if (vPower > 50 && vPower <=100)
{
vPower -= 50;
vPower *= 5;

  digitalWrite(in1, HIGH); //righthand propeller (clockwise)
  digitalWrite(in2, LOW); //lefthand propeller (counter-clockwise)
  speed1 = map(vPower, 550, 1023, 0, 255);
  speed2 = map(vPower, 550, 1023, 0, 255);
}
//reverse
else if(vPower < 50 && vPower >= 1)
{
  vPower -= 50;
  vPower *= 5;
  digitalWrite(in3, LOW); //righthand propeller (counter-clockwise)
  digitalWrite(in4, HIGH); //lefthand propeller (clockwise)
  speed1 = map(vPower, 470, 0, 0, 255);
  speed2 = map(vPower, 470, 0, 0, 255);
}
else
{
  speed1 = 0;
  speed2 = 0;
}

  
if(hPower>=1 && hPower < 50)
{
  hPower = 180 - hPower; 
  rudder.write(hPower);
  delay(20);
} 

else if (hPower >50 && hPower <= 100)
{
  hPower = 180 - hPower;
  rudder.write(hPower);
  delay(20);
}
else {
  rudder.write(0);
}
delay(60);
}







