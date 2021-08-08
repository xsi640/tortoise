#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>
#include <Stepper2.h>

#define STEP_1 5  //D1
#define STEP_2 4  //D2
#define STEP_3 0  //D3
#define STEP_4 2  //D4

#define WATER_PIN 15  //水泵PIN D8
#define HOT_PIN 3     //加热棒PIN  D9

#define URL "http://192.168.1.254:18080/api/v1/journal"

const char *ssid     = "SO_SY";
const char *password = "suyang19830128";

ESP8266WiFiMulti WiFiMulti;
int stepPins[4] = { STEP_1, STEP_2, STEP_3, STEP_4};
Stepper2 myStepper(stepPins);

void setup() {
  Serial.begin(115200);
  Serial.println("begin init...");
  setupStepper();
  setupWiFi();
//  pinMode(WATER_PIN, OUTPUT);
//  pinMode(HOT_PIN, OUTPUT);
  Serial.println("end init...");
}

void loop() {
  if (WiFi.status() == WL_CONNECTED) {
//    digitalWrite(WATER_PIN, HIGH);
//    digitalWrite(HOT_PIN, HIGH);
//    doFeed();
  }
}

void setupStepper() {
  myStepper.setSpeed(5);//速度15rpm
}

void setupWiFi() {
  WiFi.begin(ssid, password);
  WiFi.mode(WIFI_STA);
  WiFiMulti.addAP(ssid, password);
}

void doFeed() {
  Serial.print("doFeed:");
  Serial.println(FEED_COUNT);
  myStepper.setDirection(0);
  for (int i = 0; i < FEED_COUNT; i++) {
    myStepper.turn();
  }
  myStepper.stop();
}

void requestFeed() {
  WiFiClient client;
  HTTPClient http;

  Serial.print("[HTTP] begin...\n");
  if (http.begin(client, URL)) {  // HTTP


    Serial.print("[HTTP] GET...\n");
    // start connection and send HTTP header
    http.addHeader("Content-Type", "application/json");
    int httpCode = http.POST("{\"message\":\"ok\",\"type\":\"FEED\"}");

    // httpCode will be negative on error
    if (httpCode > 0) {
      // HTTP header has been send and Server response header has been handled
      Serial.printf("[HTTP] GET... code: %d\n", httpCode);

      // file found at server
      if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {
        String payload = http.getString();
        Serial.println(payload);
      }
    } else {
      Serial.printf("[HTTP] GET... failed, error: %s\n", http.errorToString(httpCode).c_str());
    }

    http.end();
  } else {
    Serial.printf("[HTTP} Unable to connect\n");
  }
}