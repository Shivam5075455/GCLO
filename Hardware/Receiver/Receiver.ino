/*
Youtube video link: https://www.youtube.com/watch?v=wH483V8fnN8
 */
#include <LoRa.h>
#include <SoftwareSerial.h>
const int BT_RX = 3;  //RX=3
const int BT_TX = 1;  //TX=1
SoftwareSerial btSerial(BT_TX, BT_RX);
#define SS 15
#define RST 16
#define DIO0 2
// Library for 1.3 inch OLED display
#include <SPI.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SH110X.h>

//define OLED address and size
#define Address 0x3C
#define height 64
#define width 128
#define reset -1
//pass above parameters
Adafruit_SH1106G display = Adafruit_SH1106G(width, height, &Wire, reset);

#define Buzzer 13
String receivedMessage = "";
int intervalLocation = 5000;

const long interval = 1000;  // Interval at which to send messages (milliseconds)
unsigned long previousMillis = 0;

// Message identifiers
#define USER1_ID "User1"
#define USER2_ID "User2"
String USER1_IDs = "User1";
void setup() {
  Serial.begin(9600);
  btSerial.begin(9600);
  pinMode(Buzzer, OUTPUT);
  while (!Serial)
    ;
  Serial.println("Receiver Host");
  LoRa.setPins(SS, RST, DIO0);
  if (!LoRa.begin(433E6)) {
    Serial.println("LoRa Error");
    while (1)
      ;
  }
  // OLED display code start
  delay(250);
  display.begin(Address, true);
  delay(250);
  display.display();
  delay(2000);

  display.clearDisplay();
  delay(100);
  display.setCursor(0, 0);
  display.setTextSize(1);
  display.setTextColor(SH110X_WHITE);
  display.print("Hi");
  Serial.print("Hi");
  display.display();
}
void loop() {
  unsigned long currentMillis = millis();
  Serial.print("Time: ");
  Serial.println(currentMillis);

  // Check if it's time to send a message
  if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis;
    receivedDataFromPhone();
  }

  // Check for incoming messages
  receiveDataUsingLoRa();
}

/*
void sendMessage() {
  String message = String(USER1_ID) + ": Hello from User1";
  Serial.println("Sending packet: " + message);
  LoRa.beginPacket();
  LoRa.print(message);
  LoRa.endPacket();
}

void receiveMessage() {
  int packetSize = LoRa.parsePacket();
  Serial.print("Packet size: ");
  Serial.println(packetSize);
  if (packetSize) {
    // Received a packet
    Serial.print("Received packet: ");

    // Read packet
    String receivedMessages = "";
    while (LoRa.available()) {
      receivedMessages += (char)LoRa.read();
    }
    display.clearDisplay();
    delay(500);
    display.setTextSize(1);
    display.setCursor(0, 0);
    display.setTextColor(SH110X_WHITE);
    display.print(receivedMessages);
    display.display();

    // Print RSSI of packet
    Serial.print(" with RSSI ");
    Serial.println(LoRa.packetRssi());
  }
}
*/


void displayData(String data) {
  //   // clear  buffer
  display.clearDisplay();
  delay(500);
  display.setTextSize(1);
  display.setCursor(0, 0);
  display.setTextColor(SH110X_WHITE);
  display.print(data);
  display.display();
}


void receiveDataUsingLoRa() {
  //Calls LoRa.parsePacket() to check if a LoRa packet has been received.
  // It returns the size of the received packet in bytes. If no packet is
  //  received, it returns 0.
  int packetSize = LoRa.parsePacket();
  Serial.print("Packet size: ");
  Serial.println(packetSize);
  // If a packet is received (packetSize is not zero), the code enters the block.
  if (packetSize) {
    // Prints "Receiving Data: " to the serial monitor.
    Serial.print("Receiving Data: ");
    // Enters a loop to read the available data from the LoRa module using LoRa.available()
    while (LoRa.available()) {
      // Reads the data as a string using LoRa.readString()
      String data = LoRa.readString();  //  "LoRa" is a class and "readString" is an object of this class to read data coming from transmitter
      // Prints the received data to the serial monitor.
      Serial.println(data);
      //  display data received from other side
      displayData(data);
      btSerial.print(data);
    }
  }
}

void receivedDataFromPhone() {
  if (btSerial.available()) {
    char readChar = btSerial.read();
    if (readChar != '\n') {
      receivedMessage += readChar;
    } else {
      Serial.print("Received data from mobile: ");
      Serial.print(receivedMessage);
      // setCommands(receivedMessage);

      displayData(receivedMessage);
      String completeMsg = USER1_IDs + " " + receivedMessage;
      LoRa.beginPacket();
      LoRa.print(receivedMessage);
      LoRa.print("\n");
      LoRa.endPacket();
      // sendViaLoRa(completeMsg);

      receivedMessage = "";
      completeMsg = "";
    }
  }
}

void sendViaLoRa(String data) {
  // send messages to other side of user
  LoRa.beginPacket();
  LoRa.print(data);
  LoRa.print("\n");
  LoRa.endPacket();
}

void sendAutomaticallyFromNodemcu() {

  delay(intervalLocation);
}

void sendMessageToPhone() {
  if (Serial.available()) {
    String message = Serial.readStringUntil('\n');  // Read the message from serial monitor until newline character
    Serial.print("Message to Phone: ");
    Serial.println(message);    // Print the message to Serial Monitor
    btSerial.println(message);  // Send the message to the connected Bluetooth device
  }
}

void setCommands(String command) {
  Serial.println("Commands: ");
  Serial.println(command);

  // Extracting the command for the buzzer
  if (command.startsWith("Buz1")) {
    digitalWrite(Buzzer, 1);
  } else if (command.startsWith("Buz0")) {
    digitalWrite(Buzzer, 0);
  }

  // Extracting the fixed latitude and longitude coordinates
  /*
  if (command.startsWith("Lat:")) {
    prevLatitude = command.substring(4).toDouble();
    Serial.print("Fixed Latitude: ");
    Serial.println(prevLatitude, 6);
  } else if (command.startsWith("Long")) {
    prevLongitude = command.substring(4).toDouble();
    Serial.print("Fixed Longitude: ");
    Serial.println(prevLongitude, 6);
  }


  // Extracting the fixed boundary distance
  if (command.startsWith("Dis:")) {
    fixedBoundaryDistance = command.substring(4).toFloat();
    Serial.print("Fixed Boundary Distance: ");
    Serial.println(fixedBoundaryDistance);
  }
*/
  // Extracting the interval time to send the location
  if (command.startsWith("Inter")) {
    intervalLocation = command.substring(5).toInt();
    Serial.print("Interval Location Time: ");
    Serial.println(intervalLocation);
  }
}
