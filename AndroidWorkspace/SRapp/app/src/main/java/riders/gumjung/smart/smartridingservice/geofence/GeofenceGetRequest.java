package riders.gumjung.smart.smartridingservice.geofence;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by HackerAnderson on 2016. 9. 28..
 */
public class GeofenceGetRequest {

    private GeofenceInfo[] geoInfo;
    private static String CONTENT_TYPE = "application/json";

    public GeofenceGetRequest() {

    }

    public GeofenceInfo[] getGeofenceInfo() throws Exception {


        try {
            Log.e("DragonSnsRequest", "start sns information method");
            String memberUrl = "http://testweb.mybluemix.net/api/members";
            String jsonString;


            jsonString = GetHttpRequest(memberUrl);

            JSONArray userArray = new JSONArray(jsonString);

            geoInfo = new GeofenceInfo[userArray.length()];

            for (int i = 0; i < userArray.length(); i++) {

                JSONObject insideObject = userArray.getJSONObject(i);
                geoInfo[i] = new GeofenceInfo();
                geoInfo[i].setStatus(true);
                geoInfo[i].setUserId(insideObject.getString("id"));
                geoInfo[i].setEndDeviceId(insideObject.getString("end_device_id"));
                geoInfo[i].setSnsMessage(insideObject.getString("sns"));

                //get longitude and lengitude from end-device id
                String endDeviceUrl = "http://testweb.mybluemix.net/api/end_devices/" + geoInfo[i].getEndDeviceId();


                try {
                    String getLocationString;
                    getLocationString = GetHttpRequest(endDeviceUrl);
                    JSONObject locationObj = new JSONObject(getLocationString);
                    geoInfo[i].setLongitude(locationObj.getString("longitude"));
                    geoInfo[i].setLatitude(locationObj.getString("latitude"));
                } catch (Exception e) {
                    geoInfo[i].setStatus(true);
                    geoInfo[i].setLongitude("0");
                    geoInfo[i].setLatitude("0");
                }

                if (insideObject.getString("start_longitude").length() == 0 && insideObject.getString("start_latitude").length() == 0) {
                    geoInfo[i].setGeofenceExist(false);
                } else {
                    geoInfo[i].setGeofenceExist(true);
                }


            }
        } catch (Exception e) {
            geoInfo[0] = new GeofenceInfo();
            geoInfo[0].setStatus(false);
        }


        return geoInfo;
    }


    private static String GetHttpRequest(String urlStr) throws IOException {
        URL url = new URL(urlStr);

        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

        conn.setRequestProperty("Content-Type", CONTENT_TYPE);
        if (conn.getResponseCode() != 200) {
            throw new IOException(conn.getResponseMessage());
        }
        // Buffer the result into a string
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        return sb.toString();
    }


}
