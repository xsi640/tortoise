#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>
#include <Stepper2.h>
#include <ThreeWire.h>
#include <RtcDS1302.h>

#define STEP_1 5  //D1
#define STEP_2 4  //D2
#define STEP_3 0  //D3
#define STEP_4 2  //D4

#define CLOCK_1 14  //DAT D5
#define CLOCK_2 12  //CLK D6
#define CLOCK_3 13  //RST D7

#define WATER_PIN 15  //水泵PIN D8
#define HOT_PIN 3     //加热棒PIN  D9

#define URL "http://192.168.1.254:18080/api/v1/journal"

const unsigned int FEED_COUNT = 2;  //每次喂食次数
const unsigned int FEED_HOUR = 7;   //喂食的小时
const unsigned int FEED_MINUTE = 0; //喂食的分钟

const char *ssid     = "SO_SY";
const char *password = "suyang19830128";

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP);
ESP8266WiFiMulti WiFiMulti;
ThreeWire myWire(CLOCK_1, CLOCK_2, CLOCK_3);
RtcDS1302<ThreeWire> Rtc(myWire);
int stepPins[4] = { STEP_1, STEP_2, STEP_3, STEP_4};
Stepper2 myStepper(stepPins);

unsigned int lastUpdateNtpHour = -1;
boolean feeded = false;
RtcDateTime now;

void setup() {
  Serial.begin(115200);
  Serial.println("begin init...");
  setupStepper();
  setupClock();
  setupWiFi();
  setupNtp();
//  pinMode(WATER_PIN, OUTPUT);
//  pinMode(HOT_PIN, OUTPUT);
  Serial.println("end init...");
}

void loop() {
  if (WiFi.status() == WL_CONNECTED) {
//    digitalWrite(WATER_PIN, HIGH);
//    digitalWrite(HOT_PIN, HIGH);
//    doFeed();
    fixTime();
    feed();
    updateTime();
    delay(1000);
  }
}

void setupStepper() {
  myStepper.setSpeed(5);//速度15rpm
}

void setupClock() {
  Serial.print("compiled: ");
  Serial.print(__DATE__);
  Serial.println(__TIME__);

  Rtc.Begin();

  RtcDateTime compiled = RtcDateTime(__DATE__, __TIME__);

  if (!Rtc.IsDateTimeValid())
  {
    // Common Causes:
    //    1) first time you ran and the device wasn't running yet
    //    2) the battery on the device is low or even missing
    Serial.println("RTC lost confidence in the DateTime!");
    Rtc.SetDateTime(compiled);
  }

  if (Rtc.GetIsWriteProtected())
  {
    Serial.println("RTC was write protected, enabling writing now");
    Rtc.SetIsWriteProtected(false);
  }

  if (!Rtc.GetIsRunning())
  {
    Serial.println("RTC was not actively running, starting now");
    Rtc.SetIsRunning(true);
  }

  RtcDateTime now = Rtc.GetDateTime();
  if (now < compiled)
  {
    Serial.println("RTC is older than compile time!  (Updating DateTime)");
    Rtc.SetDateTime(compiled);
  }
  else if (now > compiled)
  {
    Serial.println("RTC is newer than compile time. (this is expected)");
  }
  else if (now == compiled)
  {
    Serial.println("RTC is the same as compile time! (not expected but all is fine)");
  }
}

void setupWiFi() {
  WiFi.begin(ssid, password);
  WiFi.mode(WIFI_STA);
  WiFiMulti.addAP(ssid, password);
}

void setupNtp() {
  //+8时区
  timeClient.setTimeOffset(28800);
  timeClient.begin();
}

void fixTime() {
  if (lastUpdateNtpHour != now.Hour()) {
    if (timeClient.update()) {
      Serial.print("update ntp time: ");
      Serial.println(timeClient.getFormattedTime());

      now = Rtc.GetDateTime();
      if (now.IsValid()) {
        time_t rawtime = timeClient.getEpochTime();
        struct tm * ti;
        ti = localtime (&rawtime);
        RtcDateTime fixed = RtcDateTime(ti->tm_year + 1900, ti->tm_mon + 1, ti->tm_mday, timeClient.getHours(), timeClient.getMinutes(), timeClient.getSeconds());
        Rtc.SetDateTime(fixed);
        now = fixed;
        lastUpdateNtpHour = fixed.Hour();
      }
    }
  }
}

void feed() {
  if (feeded) {
    if (now.Hour() < 1) { //0点feeded复位
      Serial.println("feeded reset.");
      feeded = false;
    }
  }
  if (!feeded && (now.Hour() >= FEED_HOUR && now.Minute() >= FEED_MINUTE)) {
    feeded = true;
    doFeed();
    requestFeed();
  }
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

void updateTime() {
  now = Rtc.GetDateTime();
  while (!now.IsValid()) {
    // Common Causes:
    //    1) the battery on the device is low or even missing and the power line was disconnected
    Serial.println("RTC lost confidence in the DateTime!");
    delay(1000);
    now = Rtc.GetDateTime();
  }
  printDateTime(now);
}

#define countof(a) (sizeof(a) / sizeof(a[0]))
void printDateTime(const RtcDateTime& dt)
{
  char datestring[20];

  snprintf_P(datestring,
             countof(datestring),
             PSTR("%02u/%02u/%04u %02u:%02u:%02u"),
             dt.Month(),
             dt.Day(),
             dt.Year(),
             dt.Hour(),
             dt.Minute(),
             dt.Second() );
  Serial.println(datestring);
}