package riders.gumjung.smart.smartridingservice.exercise;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by HackerAnderson on 2016. 10. 24..
 */

public class RequestExerciseStatus {

    private static String CONTENT_TYPE = "application/json";

    public RequestExerciseStatus() {

    }

    public void request(String deviceId,String status) throws Exception {

        String memberUrl = "http://testweb.mybluemix.net/api/members/" + deviceId + "/exercise/" + status;
        GetHttpRequest(memberUrl);
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
