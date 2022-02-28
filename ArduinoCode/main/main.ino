#include "soundPlayer.h"

int const sonarsTrigPin[] = {13, 11, 13, 11, 13, 11};
int const sonarsEchoPin[] = {12, 7, 12, 7, 12, 7};
int const ledsPin[] = {6, 3, 6, 3, 6, 3}; // Matches to stations by their number/index. For demo, support all stations chosen by the user, but odd stations will be station 1, and even stations will be station 2.

int const INVALID_STATION_NUM = -1;
int const NUM_OF_STATIONS = 6;
int enabledStations[] = {INVALID_STATION_NUM, INVALID_STATION_NUM, INVALID_STATION_NUM, INVALID_STATION_NUM ,INVALID_STATION_NUM, INVALID_STATION_NUM};
int distancesFromSurfaces[] = {0, 0, 0, 0, 0, 0};

int numOfEnabledStations = 0;

enum StationOrder {
    CIRCULAR = 0,
    RANDOM,
    OPPOSITE
};

StationOrder stationOrder = CIRCULAR;
AudioFeedback audioFeedback = INSTRUMENTAL;

int const numberOfStations = 2;
int const piezoPin = 8;
SoundPlayer* soundPlayer;
int activeStation = 0;
char const DELIMITER[] = "$";
boolean isActiveSession = false;


void setup() {
  for (int pin = 0; pin < numberOfStations; pin++){
      pinMode(sonarsTrigPin[pin], OUTPUT);
      pinMode(sonarsEchoPin[pin], INPUT);
      pinMode(ledsPin[pin], OUTPUT);
  }
    pinMode(piezoPin, OUTPUT);
    Serial.begin(9600);
    activeStation = 0;
    soundPlayer = new SoundPlayer();
    calibrate();

    // runningWOPhone(); // a method for testing
}

void loop() {  
  if (Serial.available() > 0) {
   readOptions(); 
  }
  
  if (isActiveSession){
    RunSession();
  }
//  Serial.println("active station");
//  Serial.println(activeStation);
//  Serial.println("Zero distances");
//  Serial.println(distancesFromSurfaces[0]);
//  Serial.println(distancesFromSurfaces[1]);
//  Serial.println(distancesFromSurfaces[2]);
//  Serial.println(getDistance(0));
    
}

void RunSession() { 
  if (numOfEnabledStations == 0) {
    return;
  }
  
  int distance = getDistance(activeStation);
  Serial.println("Distance Of Active Station: @@@@@@");
  Serial.println(distance);
  int threshold = distancesFromSurfaces[activeStation] - 10;
  if (distance <= threshold && distance >= 0){
    soundPlayer->playSuccessIndication(piezoPin, ledsPin[activeStation], audioFeedback);
    lightOff(activeStation);
    activeStation = getNextActiveStation();
    lightOn(activeStation);
  }
}

void runningWOPhone(){
  numOfEnabledStations = 2;
  enabledStations[0] = 0;
  enabledStations[1] = 1;
  isActiveSession = true;
  activeStation = enabledStations[0];
  lightOn(activeStation);
}

void calibrate() {
  for (int i = 0; i < NUM_OF_STATIONS; i++){
    distancesFromSurfaces[i] = getDistance(i);
  }
  distancesFromSurfaces[0] = distancesFromSurfaces[2]; 
} 

int getNextActiveStation() {
  switch (stationOrder) {
    case CIRCULAR:
    {
      for (int i = 0; i < numOfEnabledStations;  i++) {
        if (enabledStations[i] == activeStation) {
          if (i + 1 == numOfEnabledStations) { // checking for OOB & reaching to -1
            return enabledStations[0];
          }

          return enabledStations[i+1];
        }
      }
      break;
    }
    case RANDOM:
    {
      int stationIndex = numOfEnabledStations;
      do {
        stationIndex = random(numOfEnabledStations);
      } while (enabledStations[stationIndex] == activeStation); // Continue if we got the same station as the current one
      return enabledStations[stationIndex];
      break;
    }
    case OPPOSITE:
    {
      int largestDistance = 0;
      int mostDistantStation = activeStation;
      for (int i = 0; i < numOfEnabledStations;  i++) {
        if (abs(enabledStations[i] - activeStation) > largestDistance) {
          largestDistance = abs(enabledStations[i] - activeStation);
          mostDistantStation = enabledStations[i];
        }
      }
      return mostDistantStation;
      break;
    }
    default: // Never supposed to reach here
    {
      return activeStation;
    }
  }
}

int getDistance(int station) {
  int duration, distance;

  digitalWrite(sonarsTrigPin[station], OUTPUT);
  delay(3);
  digitalWrite(sonarsTrigPin[station], LOW);

  duration = pulseIn(sonarsEchoPin[station], HIGH);

  distance = (duration/2) / 29.1;
  return distance;
}

void readOptions() {
  String serialString = Serial.readString();
  char data[15];
  serialString.toCharArray(data, 15);
  
  if (data[0] == '!') { // Meaning starting/ending session
    blinkLed(3);
    
    String curString = String(&data[1]);
    isActiveSession = (curString == "true");
    if (isActiveSession) {
      activeStation = enabledStations[0];
      lightOn(activeStation);
    } else {
      lightOff(activeStation);
    }
  } else if(data[0] == '@') { // Meaning calibration
    calibrate();
    lightOn(0);
    delay(100);
    lightOff(0);
    lightOn(1);
    delay(100);
    lightOff(1);
  } else { // Meaning we got settings
    blinkLed(6);

    // reset stations
    numOfEnabledStations = 0;
    int enabledStationsPos = 0; // Points to the next uinitialized position in enabledStations
    for (int i = 0; i < NUM_OF_STATIONS; i++) {
      enabledStations[i] = INVALID_STATION_NUM;
    }
    
    if (data[0] == *DELIMITER) { // Meaning no stations were selected
      return;
    }

    char* curData = strtok(data, DELIMITER);
    String curString = String(curData);
    // curString contains active stations:
    for (auto stationNum : curString) {
      enabledStations[enabledStationsPos] = String(stationNum).toInt();
      enabledStationsPos++;
    }
    numOfEnabledStations = enabledStationsPos;
    if (numOfEnabledStations == 1) { // Don't enable 1 station, reset it as if there are 0 stations
      enabledStations[0] = INVALID_STATION_NUM;
      numOfEnabledStations = 0;
    }

    // Go to next token - order of stations:
    curData = strtok (NULL ,DELIMITER);
    curString = String(curData);
    stationOrder = (StationOrder)curString.toInt();

    // Go to next token - audio feedback:
    curData = strtok (NULL ,DELIMITER);
    curString = String(curData);
    audioFeedback = (AudioFeedback)curString.toInt();
  }
}

void lightOn(int station) {
    digitalWrite(ledsPin[station], HIGH);
}

void lightOff(int station) {
  digitalWrite(ledsPin[station], LOW);
}


 
