package riders.gumjung.smart.smartridingservice.exercise;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import riders.gumjung.smart.smartridingservice.geofence.GeofenceInfoDO;

/**
 * Created by HackerAnderson on 2016. 10. 24..
 */

public class RequestCurrentExercise {


    private DeviceDO[] deviceDOs;
    private static String CONTENT_TYPE = "application/json";

    public RequestCurrentExercise() {

    }

    public DeviceDO[] getCurrentLog(int deviceId,int countNumber) throws Exception {


        String memberUrl = "http://testweb.mybluemix.net/api/end_devices/"+deviceId+"/list/"+countNumber;
        String jsonString;

        jsonString = GetHttpRequest(memberUrl);
        JSONArray userArray = new JSONArray(jsonString);
        deviceDOs = new DeviceDO[userArray.length()];
        for (int i = 0; i < userArray.length(); i++) {
            JSONObject insideObject = userArray.getJSONObject(i);
            deviceDOs[i] = new DeviceDO();
            deviceDOs[i].setLatitude(insideObject.getString("latitude"));
            deviceDOs[i].setLongitude(insideObject.getString("longitude"));
            deviceDOs[i].setSpeed(insideObject.getString("speed"));
            deviceDOs[i].setAcceleration(insideObject.getString("acceleration"));
        }
        return deviceDOs;
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
