package riders.gumjung.smart.smartridingservice.geofence;

/**
 * Created by HackerAnderson on 2016. 9. 28..
 */

public class GeofenceInfoDO {

    private String id;
    private String snsMessage;
    private String endDeviceId;

    private boolean status;
    private String latitude;
    private String longitude;
    private boolean isGeofenceExist;


    public GeofenceInfoDO() {

    }


    public void setStatus(boolean temp_status) {
        this.status = temp_status;
    }
    public void setUserId(String temp_id) {
        this.id = temp_id;
    }
    public void setSnsMessage(String temp_message) {
        this.snsMessage = temp_message;
    }
    public void setEndDeviceId(String temp_device_id) {
        this.endDeviceId = temp_device_id;
    }


    public void setLatitude(String temp_latitude) {
        this.latitude = temp_latitude;
    }
    public void setLongitude(String temp_longitude) {
        this.longitude = temp_longitude;
    }




    public void setGeofenceExist(boolean temp_exist) {
        this.isGeofenceExist = temp_exist;
    }



    public String getUserId() {
        return this.id;
    }
    public boolean getStatus() {
        return this.status;
    }
    public String getSnsMessage() {
        return this.snsMessage;
    }
    public String getEndDeviceId() {
        return this.endDeviceId;
    }

    public String getLatitude(){ return this.latitude;}
    public String getLongitude(){return this.longitude;}

    public boolean getGenfenceExist(){return this.isGeofenceExist;}





}
