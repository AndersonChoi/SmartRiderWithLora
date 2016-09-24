package riders.gumjung.smart.smartridingservice.login;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import riders.gumjung.smart.smartridingservice.dragonSns.SnsInfo;

/**
 * Created by HackerAnderson on 2016. 9. 24..
 */

public class LoginRequest {


    private static String CONTENT_TYPE = "application/json";

    public LoginRequest() {

    }

    public Boolean isUserExist(String id, String password) {

        String memberUrl = "http://testweb.mybluemix.net/api/members/"+id;
        String jsonString;

        Log.e("loginRequest","start");

        try {
            jsonString = GetHttpRequest(memberUrl);
            JSONObject insideObject = new JSONObject(jsonString);
            String jsonPassword = insideObject.getString("password").toString().trim();
            Log.e("loginRequest","jsonPassword:"+jsonPassword);
            Log.e("loginRequest","input password:"+password);
            return jsonPassword.equals(password);
        }catch(Exception e){
            Log.e("loginRequest","exception:"+e);
            return false;
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
