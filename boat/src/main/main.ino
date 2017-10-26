#include <Servo.h>

int motor1 = 13;
int motor2 = 14;
int vPower = 0;
int hPower = 0;
Servo rudder;

void setup()
{
  rudder.attach(9);
  pinMode (motor1, OUTPUT);
  pinMode (motor2, OUTPUT);
  
  Serial.begin(9600);
}
void loop ()
{

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

  digitalWrite(motor1, HIGH); //righthand propeller (clockwise)
  digitalWrite(motor2, LOW); //lefthand propeller (counter-clockwise)
  speed1 = map(vPower, 550, 1023, 0, 255);
  speed2 = map(vPower, 550, 1023, 0, 255);
}
//reverse
else if(vPower < 50 && vPower >= 1)
{
  vPower -= 50;
  vPower *= 5;
  digitalWrite(motor1, LOW); //righthand propeller (counter-clockwise)
  digitalWrite(motor2, HIGH); //lefthand propeller (clockwise)
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







