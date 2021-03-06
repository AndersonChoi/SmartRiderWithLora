package riders.gumjung.smart.smartridingservice.dragonSns;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import riders.gumjung.smart.smartridingservice.weather.WeatherInfo;

/**
 * Created by HackerAnderson on 2016. 7. 1..
 */
public class DragonSnsRequest {


    private SnsInfo[] snsInfo;
    private static String CONTENT_TYPE = "application/json";

    public DragonSnsRequest() {

    }

    public SnsInfo[] getSnsInformation() throws Exception {

        Log.e("DragonSnsRequest", "start sns information method");
        String memberUrl = "http://testweb.mybluemix.net/api/members";
        String jsonString;


        jsonString = GetHttpRequest(memberUrl);

        JSONArray userArray = new JSONArray(jsonString);


        snsInfo = new SnsInfo[userArray.length()];

        for (int i = 0; i < userArray.length(); i++) {

            JSONObject insideObject = userArray.getJSONObject(i);
            snsInfo[i] = new SnsInfo();
            snsInfo[i].setStatus(true);
            snsInfo[i].setUserId(insideObject.getString("id"));
            snsInfo[i].setEndDeviceId(insideObject.getString("end_device_id"));
            snsInfo[i].setSnsMessage(insideObject.getString("sns"));

            //get longitude and lengitude from end-device id
            String endDeviceUrl = "http://testweb.mybluemix.net/api/end_devices/"+snsInfo[i].getEndDeviceId();


            try {
                String getLocationString;
                getLocationString = GetHttpRequest(endDeviceUrl);
                JSONObject locationObj = new JSONObject(getLocationString);
                snsInfo[i].setLongitude(locationObj.getString("longitude"));
                snsInfo[i].setLatitude(locationObj.getString("latitude"));
            }catch(Exception e){
                snsInfo[i].setStatus(false);
                snsInfo[i].setLongitude("0");
                snsInfo[i].setLatitude("0");

            }



        }


        return snsInfo;
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
