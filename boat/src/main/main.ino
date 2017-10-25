int led = 13;
int state = 0;
int flag = 0;
int vPower = 0;
int hPower = 0;

void setup()
{
  pinMode(led, OUTPUT);
  digitalWrite(led, LOW);
  Serial.begin(9600);
}
void loop ()
{
if (Serial.available() > 0)
{
  state = Serial.read ();
  flag = 0;
}

if (state == 0)
{
  digitalWrite(led, LOW);
  if(flag == 0)
  {
    Serial.println("OFF");
    flag = 1;
  }
}

else if (state == 1)
{
  digitalWrite(led, HIGH);
  if (flag == 0)
  {
    Serial.println("ON");
    flag = 1;
  }
}
}

