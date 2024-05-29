// Library for GPS Module
#include <SoftwareSerial.h>
#include <TinyGPS++.h>
// Library for LoRa
#include <LoRa.h>
// Library for 1.3 inch OLED display
#include <SPI.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SH110X.h>

// Pin define for LoRa
#define SS 15   // ss -> chip select pin
#define RST 16  // reset pin
#define DIO0 2  // thi is an interrupt pin


#define Buzzer 13
// Nodemcu Pins:  RX = 3, TX = 1, SDD = 9
// Bluetooth pin
const int BT_RX = 3;                    //RX=3
const int BT_TX = 1;                    //TX=1
SoftwareSerial btSerial(BT_TX, BT_RX);  // TX=1,RX=3
// Pin define for GPS Module
//double lati, longi;
const int GPS_TX = D3;
const int GPS_RX = D4;
SoftwareSerial myGPS(GPS_TX, GPS_RX);
TinyGPSPlus gps;
//define OLED address and size
#define Address 0x3C
#define height 64
#define width 128
#define reset -1
//pass above parameters
Adafruit_SH1106G display = Adafruit_SH1106G(width, height, &Wire, reset);
double prevLatitude = 0.00;   //28.677765;
double prevLongitude = 0.00;  //77.494630;
double totalDistance = 0.0;
double latitude = 0.0, longitude = 0.0;

String receivedMessage = "";
// sent automatically
String currentname = "", currentusername = "", currentemail = "", currentphonenumber = "";
// Received by user
String name = "", username = "", email = "", phonenumber = "";
bool isUserVerified = false;
float fixedBoundaryDistance = 0.00;
// send location after a interval
int intervalLocation = 5000;

const long interval = 2000;  // Interval at which to send messages (milliseconds)
unsigned long previousMillis = 0;

// Message identifiers
// #define USER1_ID "User1"
// #define USER2_ID "User2"

String USER2_ID = "User2";
String USER1_ID = "User1";


void setup() {
  Serial.begin(9600);
  myGPS.begin(9600);  // initiate GPS serial communication
  btSerial.begin(9600);

  // Initialize oled display
  delay(250);
  display.begin(Address, true);
  delay(250);
  display.display();
  delay(2000);

  // Initialize display
  // if (!display.begin(Address, true)) {
  //   Serial.println("Display initialization failed!");
  //   while (1); // Stay in infinite loop if display initialization fails
  // }
  Serial.println("Display initialized successfully!");
  while (!Serial)
    ;
  Serial.println("Sender Host");
  LoRa.setPins(SS, RST, DIO0);     //Sets up the pin configuration for the LoRa module using LoRa.setPins()
  if (!LoRa.begin(433E6)) {        //Initializes the LoRa module using LoRa.begin(). The parameter 433E6 sets the frequency to 433 MHz.
    Serial.println("LoRa Error");  //If the LoRa module fails to initialize, the code prints "LoRa Error" and enters an infinite loop.
    delay(100);
    while (1)
      ;
  }

  // while (!isUserVerified) {
  // Display message to enter the credentials
  display.clearDisplay();
  delay(1000);
  display.setCursor(0, 0);
  display.setTextSize(1);
  display.setCursor(0, 0);
  display.setTextColor(SH110X_WHITE);
  display.print("Please enter credentials same as register with application to use the device.");
  Serial.println("Please enter credentials same as register with application to use the device.");
  // display.clearDisplay();
  display.display();
  // receivedDataFromPhone();
  // }

  // Serial.print("Is Verified: ");
  // Serial.println(isUserVerified);
}

void loop() {

  unsigned long currentMillis = millis();
  gpsLocation();
  getDistance();
  // Check if it's time to send a message
  // if (currentMillis - previousMillis >= interval) {
  //   previousMillis = currentMillis;
  //   receivedDataFromPhone();
  // }

  // Check for incoming messages
  // receiveMessage();


  // if (!isUserVerified) {
  //   registerUser();
  // }
  // Serial.print("Is Verified: ");
  // Serial.println(isUserVerified);

  sendAutomaticallyFromNodemcu();

  receivedDataFromPhone();

  // setCommands();
}

void gpsLocation() {
  Serial.print("gpsLocation: started");
  while (myGPS.available() > 0) {
    if (gps.encode(myGPS.read())) {
      if (gps.location.isValid()) {

        latitude = gps.location.lat();
        longitude = gps.location.lng();

        Serial.print("Latitiude: ");
        Serial.print(latitude, 6);
        Serial.print(", Longitude: ");
        Serial.print(longitude, 6);
        Serial.println("");
        // Display Latitude and Longitude over LED
        display.clearDisplay();
        delay(100);
        display.setCursor(0, 0);
        display.setTextSize(1);
        display.setCursor(0, 0);
        display.setTextColor(SH110X_WHITE);
        display.print("Lati: ");
        display.print(latitude, 6);
        display.println("");
        // Display Longitude
        display.setCursor(0, 15);
        display.print("Longi: ");
        display.print(longitude, 6);
        display.println("");
        display.display();

        unsigned long currentMillis = millis();
        if (currentMillis - previousMillis >= interval) {
          previousMillis = currentMillis;

          LoRa.beginPacket();
          // LoRa send data
          LoRa.print("Lat: ");
          LoRa.print(latitude, 6);
          LoRa.print("\n");
          LoRa.print("Long: ");
          LoRa.print(longitude, 6);
          LoRa.print("\n");
          LoRa.endPacket();
        }
      }
    }
  }
  // to check GPS is connected properly or not
  if (millis() > 5000 && gps.charsProcessed() < 10) {
    Serial.println(F("No GPS detected: check wiring or please go to open area"));
    displayData("No GPS detected: please go to open area");
    while (true)
      ;
  }
}


void getDistance() {
  Serial.print("getDistance: started");
  //        convert current coordinates into radians
  double rLatitude = radians(latitude);
  double rLongitude = radians(longitude);
  // previous and fixed coordinates and convert into radians
  double rprevLatitude = radians(prevLatitude);
  double rprevLongitude = radians(prevLongitude);

  //        find difference
  double dLat = rLatitude - rprevLatitude;
  double dLon = rLongitude - rprevLongitude;
  // apply sine formulal̥l̥ to calculate the distance
  double a = pow(sin(dLat / 2), 2) + cos(rprevLatitude) * cos(rLatitude) * pow(sin(dLon / 2), 2);
  double c = 2 * atan2(sqrt(a), sqrt(1 - a));

  const double radius = 6371000;  // Earth's radius in meters
  totalDistance = radius * c;
  Serial.println(totalDistance);

  // check if troops in the zone or not
  if (totalDistance > fixedBoundaryDistance) {
    digitalWrite(Buzzer, 1);
    Serial.println("Out of zone!");

  } else {
    digitalWrite(Buzzer, 0);
  }
  unsigned long currentMillis = millis();
  if (totalDistance > 0) {
    // Check if it's time to send a message
    if (currentMillis - previousMillis >= interval) {
      previousMillis = currentMillis;

      LoRa.beginPacket();
      // LoRa send data
      LoRa.print("Dis: ");
      LoRa.print(totalDistance);
      LoRa.print("\n");
      LoRa.endPacket();
    }
  }
}

void displayData(String data) {
  //   // clear  buffer
  display.clearDisplay();
  delay(100);
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
      // display received message on OLED display
      displayData(data);

      btSerial.print(data);
    }
  }
}

// Send the data from Nodemcu to Mobile
void sendAutomaticallyFromNodemcu() {
  Serial.print("Time interval is: ");
  Serial.println(intervalLocation);

  double lat = gps.location.lat();
  double longi = gps.location.lng();
  if (lat != 0 && longi != 0) {
    btSerial.print("Lat: ");
    btSerial.print(latitude, 6);
    btSerial.print(", Longi: ");
    btSerial.println(longitude, 6);
    // btSerial.print("\n");
  }
  delay(intervalLocation);
}

void receivedDataFromPhone() {
  if (btSerial.available()) {
    char readChar = btSerial.read();
    if (readChar != '\n') {
      receivedMessage += readChar;
    } else {
      Serial.print("Received data from mobile: ");
      Serial.println(receivedMessage);

      // display received message on OLED display
      displayData(receivedMessage);

      if (!receivedMessage.isEmpty()) {

        unsigned long currentMillis = millis();
        // Check if it's time to send a message
        if (currentMillis - previousMillis >= interval) {
          previousMillis = currentMillis;

          LoRa.beginPacket();
          LoRa.print(receivedMessage);
          LoRa.print("\n");
          LoRa.endPacket();
        }
      }
      // setCommands(receivedMessage);
      // registerUser(receivedMessage);
      // if (!isUserVerified) {
      // }
      receivedMessage = "";
    }
  }
}
void sendMessageToPhone() {
  if (Serial.available()) {
    String message = Serial.readStringUntil('\n');  // Read the message from serial monitor until newline character
    Serial.print("Message to Phone: ");
    Serial.println(message);    // Print the message to Serial Monitor
    btSerial.println(message);  // Send the message to the connected Bluetooth device
  }
}


// set command for buzzer and distance

void setCommands() {

  String command = "";
  if (btSerial.available()) {
    char readChar = btSerial.read();
    if (readChar != '\n') {
      command += readChar;
    } else {
      Serial.println("Received data from setCommands: ");
      Serial.println(command);

      // Serial.println("Commands: ");
      // Serial.println(command);

      // Extracting the command for the buzzer
      if (command.startsWith("Buz1")) {
        digitalWrite(Buzzer, 1);
      } else if (command.startsWith("Buz0")) {
        digitalWrite(Buzzer, 0);
      }

      // Extracting the fixed latitude and longitude coordinates
      if (command.startsWith("Lat:")) {
        prevLatitude = command.substring(4).toDouble();
        Serial.print("Fixed Latitude: ");
        Serial.println(prevLatitude);
      } else if (command.startsWith("Long")) {
        prevLongitude = command.substring(4).toDouble();
        Serial.print("Fixed Longitude: ");
        Serial.println(prevLongitude);
      }

      // Extracting the fixed boundary distance
      if (command.startsWith("Dis:")) {
        fixedBoundaryDistance = command.substring(4).toFloat();
        Serial.print("Fixed Boundary Distance: ");
        Serial.println(fixedBoundaryDistance);
      }

      // Extracting the interval time to send the location
      if (command.startsWith("Inter")) {
        intervalLocation = command.substring(5).toInt();
        Serial.print("Interval Location Time: ");
        Serial.println(intervalLocation);
      }


      receivedMessage = "";
    }
  }
}

void registerUser() {
  String credentials = "";

  if (btSerial.available()) {
    char readChar = btSerial.read();
    if (readChar != '\n') {
      credentials += readChar;
    } else {
      Serial.println("Received data from registerUser: ");
      Serial.println(credentials);
      // Serial.print("Credentials: ");
      // Serial.println(credentials);


      if (credentials.startsWith("currentname")) {
        currentname = credentials.substring(11);
        Serial.print("Current name");
        Serial.println(currentname);
      }
      if (credentials.startsWith("currentusername")) {
        currentusername = credentials.substring(15);
        Serial.print("Current username: ");
        Serial.println(currentusername);
      }
      if (credentials.startsWith("currentemail")) {
        currentemail = credentials.substring(12);
        Serial.print("Current email");
        Serial.println(currentemail);
      }
      if (credentials.startsWith("currentphonenumber")) {
        currentphonenumber = credentials.substring(18);
        Serial.print("Current phone number");
        Serial.println(currentphonenumber);
      }

      //  sent by user
      if (credentials.startsWith("myname")) {
        name = credentials.substring(6);
        Serial.print("My name: ");
        Serial.println(name);
      } else if (credentials.startsWith("myusername")) {
        username = credentials.substring(10);
        Serial.print("My Username");
        Serial.println(username);
      } else if (credentials.startsWith("myemail")) {
        email = credentials.substring(7);
        Serial.print("My Email: ");
        Serial.println(email);
      } else if (credentials.startsWith("myphonenumber")) {
        phonenumber = credentials.substring(13);
        Serial.print("my phone number");
        Serial.println(phonenumber);
      }

      credentials = "";
    }
  }
  // Print parsed values for debugging
  Serial.println("Parsed values:");
  Serial.print("Name: ");
  Serial.println(name);
  Serial.print("Username: ");
  Serial.println(username);
  Serial.print("Email: ");
  Serial.println(email);
  Serial.print("Phone number: ");
  Serial.println(phonenumber);

  if (currentname == name) {
    btSerial.print("Correct name");
  } else {
    btSerial.print("Wrong name");
  }

  if (currentusername == username) {
    btSerial.print("Correct username");
  } else {
    btSerial.print("Wrong username");
  }

  if (currentemail == email) {
    btSerial.print("Correct email");
  } else {
    btSerial.print("Wrong email");
  }
  if (currentphonenumber == phonenumber) {
    btSerial.print("Correct phonenumber");
  } else {
    btSerial.print("Wrong phonenumber");
  }

  if (currentname == name && currentusername == username && currentemail == email && currentphonenumber == phonenumber) {

    displayData("User Verified. You can start to use device.");
    isUserVerified = true;
    Serial.print("isUserVerified: ");
    Serial.println(isUserVerified);
    delay(2000);
    display.clearDisplay();
  } else {
    isUserVerified = false;
    // btSerial.print("Entered credentials are wrong.\nPlease enter correct credentials");
  }
}  // end of registerUser
