package riders.gumjung.smart.smartridingservice.mapLog;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import riders.gumjung.smart.smartridingservice.dragonSns.SnsInfo;

/**
 * Created by HackerAnderson on 2016. 10. 21..
 */

public class RequestEndDeviceMapLog {




    private LatLng[] latlngs;
    private static String CONTENT_TYPE = "application/json";

    public RequestEndDeviceMapLog() {

    }

    public LatLng[] getMaps(int deviceId,int logNumber) throws Exception{

        Log.e("DragonSnsRequest", "start sns information method");
        String memberUrl = "http://testweb.mybluemix.net/api/end_devices/"+deviceId+"/list/"+logNumber;
        String jsonString;


        jsonString = GetHttpRequest(memberUrl);
        JSONArray userArray = new JSONArray(jsonString);
        latlngs = new LatLng[userArray.length()];

        Log.e("maplog in Request", "length : "+userArray.length());
        for (int i = 0; i < userArray.length(); i++) {
            JSONObject insideObject = userArray.getJSONObject(i);
            latlngs[i] = new LatLng(Double.parseDouble(insideObject.getString("latitude")),
                    Double.parseDouble(insideObject.getString("longitude")));
        }

        return latlngs;
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
