package riders.gumjung.smart.smartridingservice.exercise;

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

    public void setSpeed(String temp_speed){this.speed=temp_speed;}
    public void setBattery(String temp_battery){this.battery=temp_battery;}
    public void setAcceleration(String temp_acceleration){this.acceleration=temp_acceleration;}
    public void setLongitude(String temp_longitude){this.longitude=temp_longitude;}
    public void setLatitude(String temp_latitude){this.latitude=temp_latitude;}

    public String getSpeed(){return this.speed;}
    public String getBattery(){return this.battery;}
    public String getAcceleration(){return this.acceleration;}
    public String getLongitude(){return this.longitude;}
    public String getLatitude(){return this.latitude;}

}
