package riders.gumjung.smart.smartridingservice.dragonSns;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by HackerAnderson on 2016. 9. 24..
 */

public class SendSnsRequest {

    private static String CONTENT_TYPE = "application/json";

    public SendSnsRequest() {

    }

    public boolean sendSns(String id, String message) {

        String memberUrl = "http://testweb.mybluemix.net/api/members/" + id + "/update";
        Log.e("sendSns", "start");
        return POST(memberUrl, message);
    }


    public boolean POST(String inputUrl, final String message) {


        try {
            final URI url = new URI(inputUrl);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            StringEntity params = new StringEntity("{\"sns\":\"" + message + "\"}");
            httpPost.addHeader("content-type", CONTENT_TYPE);
            httpPost.setEntity(params);
            HttpResponse response = httpclient.execute(httpPost);
            String json_string = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = new JSONObject(json_string);
            String resultOfRequest = jsonObject.getString("message");


            return resultOfRequest.equals("member info updated");
        } catch (Exception e) {
            Log.e("excpetion on sns sned", e.toString());

            return false;
        }
    }


}
