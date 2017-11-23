#include <stdlib.h>
#include <SoftwareSerial.h>
#include <Servo.h>

// Pin initialization
int txd = 0;
int rxd = 1;
int enA = 11;
int in1 = 9;
int in2 = 8;
int in3 = 7;
int in4 = 6;
int enB = 5;
int srv = 3;

// Functionality variables
char inputStream;
char inputDirection;
char inputChars[2];
int inputInt;
int inputIndex;
int speedMotors;

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
  servo.write(90);

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
    if (inputInt > 50) {
      //Forward Thrust
      inputInt -= 50;
      inputInt = fabs(inputInt);
      speedMotors = map(inputInt, 1, 50, 0, 255);
      //Motor right forwards
      digitalWrite(in1, LOW);
      digitalWrite(in2, HIGH);
      //Motor left backwards
      digitalWrite(in3, HIGH);
      digitalWrite(in4, LOW);
    } else if (inputInt < 50) {
      //Backward Thrust
      inputInt -= 50;
      inputInt = fabs(inputInt);
      speedMotors = map(inputInt, 1, 50, 0, 255);
      //Motor right backwards
      digitalWrite(in1, HIGH);
      digitalWrite(in2, LOW);
      //Motor left forwards
      digitalWrite(in3, LOW);
      digitalWrite(in4, HIGH);
    } else if (inputInt == 50) {
      speedMotors = 0;
      digitalWrite(in1, LOW);
      digitalWrite(in2, LOW);
      digitalWrite(in3, LOW);
      digitalWrite(in4, LOW);
    }
    analogWrite(enA, speedMotors); // Send PWM signal to motor Right
    analogWrite(enB, speedMotors); // Send PWM signal to motor Left
  } else if (inputDirection == 'h') {
    int angle = map(inputInt, 0, 100, 70, 110);
    servo.write(angle);
  }
  delay(125);
}

