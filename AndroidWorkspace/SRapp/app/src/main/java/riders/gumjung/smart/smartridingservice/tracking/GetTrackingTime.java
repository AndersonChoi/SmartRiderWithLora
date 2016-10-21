package riders.gumjung.smart.smartridingservice.tracking;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import riders.gumjung.smart.smartridingservice.dragonSns.SnsInfo;

/**
 * Created by HackerAnderson on 2016. 7. 1..
 */
public class GetTrackingTime {


    private static String CONTENT_TYPE = "application/json";

    public GetTrackingTime() {

    }

    public String getTimeLog(int deviceId,int logNumber){

        try {
            Log.e("DragonSnsRequest", "start sns information method");
            String memberUrl = "http://testweb.mybluemix.net/api/end_devices/" + deviceId + "/list/" + logNumber;
            String jsonString;


            jsonString = GetHttpRequest(memberUrl);

            JSONArray userArray = new JSONArray(jsonString);

            JSONObject insideObject = userArray.getJSONObject(0);
            String endingTime = insideObject.getString("time_stamp");

            insideObject = userArray.getJSONObject(userArray.length() - 1);
            String startingTime= insideObject.getString("time_stamp");

            String result=startingTime+"|"+endingTime;

            return result;
        }catch(Exception e){
            return "";
        }
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
