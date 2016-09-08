// Include this library to transmit with sx1272
#include <WaspSX1272.h>
#include <WaspFrame.h>
#include <WaspGPS.h>

// Wait Receive GPS signa l during Timeout(seconds)
//#define TIMEOUT 90
#define TIMEOUT 5 //test
// Define the Waspmote ID
char nodeID[] = "INC_Node_01";

// Define the Meshlium address to send packets
uint8_t meshlium = 1;

// Status variable
int8_t lora_status;
bool gps_status;

char sensorFile[] = "SENSOR.TXT";

int cnt = 0;

//double distance = 0;

void setup()
{
  // Init
  USB.ON(); RTC.ON(); // SD.ON(); 
  ACC.ON(); GPS.ON(); sx1272.ON();
  USB.println(F("USB, RTC, ACC, GPS, sx1272 ON"));
  
  // Set the Waspmote ID
  frame.setID(nodeID);
///////////////////////////  
  // Select frequency channel
  lora_status = sx1272.setChannel(CH_09_900); //922.52 MHz
  USB.print(F("Setting Channel CH_09_900.\t state ")); 
  USB.println(lora_status);

  // Select implicit (off) or explicit (on) header mode
  lora_status = sx1272.setHeaderON();
  USB.print(F("Setting Header ON.\t\t state "));  
  USB.println(lora_status); 

  // Select mode: from 1 to 10
  lora_status = sx1272.setMode(1);  
  USB.print(F("Setting Mode '1'.\t\t state "));
  USB.println(lora_status);  

  // Select CRC on or off
  lora_status = sx1272.setCRC_ON();
  USB.print(F("Setting CRC ON.\t\t\t state "));
  USB.println(lora_status);  

  // Select output power (Max, High or Low)
  lora_status = sx1272.setPower('M'); //Max 14dBm
  USB.print(F("Setting Power to 'M'.\t\t state "));  
  USB.println(lora_status); 

  // Select the node address value: from 2 to 255
  lora_status = sx1272.setNodeAddress(2);
  USB.print(F("Setting Node Address to '2'.\t state "));
  USB.println(lora_status);
  
  // Select the maximum number of retries: from '0' to '5'
  lora_status = sx1272.setRetries(3);
  USB.print(F("Setting Retries to '3'.\t\t state "));
  USB.println(lora_status);
  USB.println();
  
  delay(1000);  
  
  USB.println(F("----------------------------------------"));
  USB.println(F("Sending:")); 
  USB.println(F("----------------------------------------"));
}

void loop()
{
    // Detect a movement
  //  move_state = ACC.();
  
  /*
  if(move_state == true){     // Moving
    PWR.deepSleep("00:00:00:20"RTC_OFFSET,RTC_ALM1_MODE1,ALL_OFF);
  } else{                     // Stopping
    if (distance < 540)
      PWR.deepSleep("00:00:01:00"RTC_OFFSET,RTC_ALM1_MODE1,ALL_OFF);
    else if (540 <= distance && distance < 900)
      PWR.deepSleep("00:00:02:00"RTC_OFFSET,RTC_ALM1_MODE1,ALL_OFF);
    else if (900 <= distance && distance < 1260)
      PWR.deepSleep("00:00:03:00"RTC_OFFSET,RTC_ALM1_MODE1,ALL_OFF);
    else if (1260 <= distance && distance < 1620)
      PWR.deepSleep("00:00:04:00"RTC_OFFSET,RTC_ALM1_MODE1,ALL_OFF);
    else if (1620 <= distance && distance < 1980)  
      PWR.deepSleep("00:00:05:00"RTC_OFFSET,RTC_ALM1_MODE1,ALL_OFF);
    else if (1980 <= distance && distance < 2340)  
      PWR.deepSleep("00:00:06:00"RTC_OFFSET,RTC_ALM1_MODE1,ALL_OFF);
    else if (2340 <= distance && distance < 2770)  
      PWR.deepSleep("00:00:07:00"RTC_OFFSET,RTC_ALM1_MODE1,ALL_OFF);
    else if (2770 <= distance && distance < 3060)  
      PWR.deepSleep("00:00:08:00"RTC_OFFSET,RTC_ALM1_MODE1,ALL_OFF);
    else if (3060 <= distance && distance < 3420)  
      PWR.deepSleep("00:00:09:00"RTC_OFFSET,RTC_ALM1_MODE1,ALL_OFF);
    else if (3420 <= distance)  
      PWR.deepSleep("00:00:10:00"RTC_OFFSET,RTC_ALM1_MODE1,ALL_OFF);
  }
  */
  if (cnt!=0){
    USB.println(F("\nsleep.................\n"));
    PWR.deepSleep("00:00:00:10",RTC_OFFSET,RTC_ALM1_MODE1,ALL_OFF);
    setup();
  }
  else {
    cnt++;
  }
  
  // GPS_1. Wait for GPS signal for specific time
  USB.println(F("Searching GPS.................."));
  gps_status = GPS.waitForSignal(TIMEOUT);
  
  if( gps_status == true )
  {
    USB.println(F("\n----------------------"));
    USB.println(F("Connected"));
    USB.println(F("----------------------"));
  }
  else
  {
    USB.println(F("\n----------------------"));
    USB.println(F("GPS TIMEOUT. NOT connected"));
    USB.println(F("----------------------"));
  }
  
  // GPS_2. If GPS is connected then get position
  if( gps_status == true )
  {
    USB.println(F("\nGET POSITION:"));
    
    // getPosition function gets all basic data 
    GPS.getPosition();
  
    // Latitude
    USB.print(F("Latitude [ddmm.mmmm]: "));
    USB.println(GPS.latitude);  
    USB.print(F("North/South indicator: "));
    USB.println(GPS.NS_indicator);

    //Longitude
    USB.print(F("Longitude [dddmm.mmmm]: "));
    USB.println(GPS.longitude);  
    USB.print(F("East/West indicator: "));
    USB.println(GPS.EW_indicator);
    
    // Altitude
    USB.print(F("Altitude [m]: "));
    USB.println(GPS.altitude);

    // Speed
    USB.print(F("Speed [km/h]: "));
    USB.println(GPS.speed);

    // Course
    USB.print(F("Course [degrees]: "));
    USB.println(GPS.course);
    
    USB.println("\nCONVERSION TO DEGREES (USEFUL FOR INTERNET SEARCH):");
    USB.print("Latitude (degrees):");
    USB.println(GPS.convert2Degrees(GPS.latitude, GPS.NS_indicator));
    USB.print("Longitude (degrees):");
    USB.println(GPS.convert2Degrees(GPS.longitude, GPS.EW_indicator));
  }
  
  // Accelator Check
  byte check = ACC.check();
  int acc_x = ACC.getX();
  int acc_y = ACC.getY();
  int acc_z = ACC.getZ();
  
  // create new frame
  frame.createFrame(ASCII);

  // add frame fields
  if(gps_status == true){
    frame.addSensor(SENSOR_GPS,
      GPS.convert2Degrees(GPS.latitude, GPS.NS_indicator),
      GPS.convert2Degrees(GPS.longitude, GPS.EW_indicator));
      
    frame.addSensor(SENSOR_ALTITUDE, GPS.altitude);
    frame.addSensor(SENSOR_SPEED, GPS.speed);
    frame.addSensor(SENSOR_COURSE, GPS.course);
  } else{
    char fail[] = "GPS connect failed.";
    frame.addSensor(SENSOR_STR, fail);
  }
  
  frame.addSensor(SENSOR_ACC, acc_x, acc_y, acc_z);
  frame.addSensor(SENSOR_BAT, PWR.getBatteryLevel());
  
  frame.addSensor(SENSOR_DATE, RTC.date, RTC.month, RTC.year);
  frame.addSensor(SENSOR_TIME, RTC.hour, RTC.minute, RTC.second);
  
  //string a
  
  frame.addSensor(SENSOR_NUM, cnt++);
 
  // Prints frame
  frame.showFrame();

  // Sending packet before ending a timeout
  lora_status = sx1272.sendPacketTimeoutACKRetries( meshlium, frame.buffer, frame.length );
  //lora_status = sx1272.sendPacketTimeout( meshlium, frame.buffer, frame.length );
  
  // Check sending status
  if( lora_status == 0 ) 
  {
    USB.println(F("Packet sent OK"));     
    
    lora_status = sx1272.getSNR();
    USB.print(F("-> SNR: "));
    USB.println(sx1272._SNR); 
    
    lora_status = sx1272.getRSSI();
    USB.print(F("-> RSSI: "));
    USB.println(sx1272._RSSI);   
    
    lora_status = sx1272.getRSSIpacket();
    USB.print(F("-> Last packet RSSI value is: "));    
    USB.println(sx1272._RSSIpacket); 
  }
  else 
  {
    USB.println(F("Error sending the packet"));  
    USB.print(F("state: "));
    USB.println(lora_status, DEC);
  } 

  //  distance = receivePacket...
  
  USB.OFF();
  
  delay(2500); 
}
