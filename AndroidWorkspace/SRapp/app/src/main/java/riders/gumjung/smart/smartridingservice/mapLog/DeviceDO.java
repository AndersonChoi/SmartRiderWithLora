package riders.gumjung.smart.smartridingservice.mapLog;

/**
 * Created by HackerAnderson on 2016. 10. 21..
 */

public class DeviceDO {
    String speed;
    String battery;
    String acceleration;
    String longitude;
    String latitude;

    public DeviceDO(){
    }

    void setSpeed(String temp_speed){this.speed=temp_speed;}
    void setBattery(String temp_battery){this.battery=temp_battery;}
    void setAcceleration(String temp_acceleration){this.acceleration=temp_acceleration;}
    void setLongitude(String temp_longitude){this.longitude=temp_longitude;}
    void setLatitude(String temp_latitude){this.latitude=temp_latitude;}

    String getSpeed(){return this.speed;}
    String getBattery(){return this.battery;}
    String getAcceleration(){return this.acceleration;}
    String getLongitude(){return this.longitude;}
    String getLatitude(){return this.latitude;}

}
