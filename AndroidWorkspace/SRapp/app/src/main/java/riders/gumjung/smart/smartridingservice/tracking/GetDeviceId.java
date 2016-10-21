package riders.gumjung.smart.smartridingservice.tracking;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by HackerAnderson on 2016. 9. 24..
 */

public class GetDeviceId {


    private static String CONTENT_TYPE = "application/json";

    public GetDeviceId() {

    }

    public int getId(String id) {

        String memberUrl = "http://testweb.mybluemix.net/api/members/"+id;
        String jsonString;

        try {
            jsonString = GetHttpRequest(memberUrl);
            JSONObject insideObject = new JSONObject(jsonString);
            String jsonCount = insideObject.getString("end_device_id").toString().trim();
            return Integer.parseInt(jsonCount);
        }catch(Exception e){
            Log.e("",""+e);
            return 0;
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
