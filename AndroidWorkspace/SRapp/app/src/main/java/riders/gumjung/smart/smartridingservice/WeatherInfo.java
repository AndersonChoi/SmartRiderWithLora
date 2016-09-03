package riders.gumjung.smart.smartridingservice;

/**
 * Created by HackerAnderson on 2016. 7. 1..
 */

public class WeatherInfo {

    private boolean status;
    private String location;
    private String skyInfo;
    private double temperature;
    private double maxTemperature;
    private double minTemperature;
    private String observationTime;

    WeatherInfo() {

    }

    public void setStatus(boolean temp_status) {
        this.status = temp_status;
    }

    public void setLocation(String temp_location) {
        this.location = temp_location;
    }

    public void setSkyInfo(String temp_skyInfo) {
        this.skyInfo = temp_skyInfo;
    }

    public void setTemperature(double temp_temperature) {
        this.temperature = temp_temperature;
    }

    public void setMaxTemperature(double temp_maxTemperature) {
        this.maxTemperature = temp_maxTemperature;
    }

    public void setMinTemperature(double temp_minTemperature) {
        this.minTemperature = temp_minTemperature;
    }

    public void setTimeObservation(String temp_observationTime) {
        this.observationTime = temp_observationTime;
    }

    public boolean getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public String getSkyInfo() {
        return skyInfo;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public String getTimeObservation() {
        return observationTime;
    }


}
