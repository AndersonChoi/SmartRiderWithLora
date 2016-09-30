package riders.gumjung.smart.smartridingservice.token;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URI;

/**
 * Created by HackerAnderson on 2016. 9. 24..
 */

public class TokenRequest {

    private static String CONTENT_TYPE = "application/json";

    public TokenRequest() {

    }

    public boolean sendToken(String id, String token) {

        String memberUrl = "http://testweb.mybluemix.net/api/members/" + id + "/token";
        Log.e("send token", "start");
        return POST(memberUrl, token);
    }


    public boolean POST(String inputUrl, final String token) {


        try {
            final URI url = new URI(inputUrl);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            StringEntity params = new StringEntity("{\"token\":\"" + token + "\"}");

            httpPost.addHeader("content-type", CONTENT_TYPE);
            httpPost.setEntity(params);
            HttpResponse response = httpclient.execute(httpPost);
            String json_string = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = new JSONObject(json_string);
            String resultOfRequest = jsonObject.getString("message");


            return resultOfRequest.equals("geofence info updated");
        } catch (Exception e) {
            Log.e("excpetion on sns sned", e.toString());

            return false;
        }
    }


}
