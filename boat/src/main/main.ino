#include <stdlib.h>
#include <SoftwareSerial.h>
#include <Servo.h>

// Pin initialization
int txd = 0;
int rxd = 1;
int enA = 5;
int in1 = 6;
int in2 = 7;
int in3 = 8;
int in4 = 9;
int enB = 10;
int srv = 11;

// Functionality variables
char inputStream;
char inputDirection;
char inputChars[2];
int inputInt;
int inputIndex;
int vPower = 0;
int hPower = 0;
int speed1; //speed of righthand motor
int speed2; //speed of lefthand motor

SoftwareSerial socket(txd, rxd);
Servo servo;

void setup() {

  socket.begin(9600);

  pinMode(enA, OUTPUT);
  pinMode(in1, OUTPUT);
  pinMode(in2, OUTPUT);
  pinMode(in3, OUTPUT);
  pinMode(in4, OUTPUT);
  pinMode(enB, OUTPUT);

  servo.attach(srv);
  servo.write(0);

}

void loop () {

  if (socket.available()) {
    inputDirection = 'a';
    inputChars[0] = '5';
    inputChars[1] = '0';
    inputInt = 50;
    inputIndex = 0;
  }
  while (socket.available()) {
    delay(10);
    inputStream = ((byte) socket.read());
    if (inputStream == ',') {
      break;
    } else {
      if (inputIndex == 0) {
        inputDirection = inputStream;
      } else if (inputIndex == 1) {
        inputChars[0] = inputStream;
      } else if (inputIndex == 2) {
        inputChars[1] = inputStream;
      }
      inputIndex += 1;
    }
  }
  inputInt = atoi(inputChars);

  if (inputDirection == 'v') {
    
  } else if (inputDirection == 'h') {
    int angle = inputInt * 9 / 5;
    servo.write(angle);
  }
  delay(125);
}

/*

  vPower = Serial.parseInt();
  hPower = Serial.parseInt();

  //forward thrust
  if (vPower > 50 && vPower <= 100) {

    vPower -= 50;
    vPower *= 5;
    digitalWrite(in1, HIGH);
    digitalWrite(in2, LOW);
    speed1 = map(vPower, 550, 1023, 0, 255);
    speed2 = map(vPower, 550, 1023, 0, 255);

  } else if (vPower < 50 && vPower >= 1) {

    vPower -= 50;
    vPower *= 5;
    digitalWrite(in3, LOW);
    digitalWrite(in4, HIGH);
    speed1 = map(vPower, 470, 0, 0, 255);
    speed2 = map(vPower, 470, 0, 0, 255);

  } else {
    speed1 = 0;
    speed2 = 0;
  }

  if (hPower >= 1 && hPower < 50) {
    hPower = 180 - hPower;
    rudder.write(hPower);
    delay(20);
  } else if (hPower > 50 && hPower <= 100) {
    hPower = 180 - hPower;
    rudder.write(hPower);
    delay(20);
  } else {
    rudder.write(0);
  }
*/


