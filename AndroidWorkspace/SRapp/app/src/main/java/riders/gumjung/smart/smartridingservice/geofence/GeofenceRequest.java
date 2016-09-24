package riders.gumjung.smart.smartridingservice.geofence;

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

public class GeofenceRequest {

    private static String CONTENT_TYPE = "application/json";

    public GeofenceRequest() {

    }

    public boolean sendGeofence(String id, String startLatitude, String startLongitude, String endLatitude, String endLongitude) {

        String memberUrl = "http://testweb.mybluemix.net/api/members/" + id + "/geofence";
        Log.e("sendSns", "start");
        return POST(memberUrl, startLatitude, startLongitude, endLatitude, endLongitude);
    }


    public boolean POST(String inputUrl, final String startLatitude, final String startLongitude, final String endLatitude, final String endLongitude) {



        /*


    "end_longitude": "35.124",
    "end_latitude": "127.12421",
    "start_longitude": "35.124",
    "start_latitude": "127.12421"


         */
        try {
            final URI url = new URI(inputUrl);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            StringEntity params = new StringEntity("{\"start_latitude\":\"" + startLatitude + "\",\"start_longitude\":\"" + startLongitude + "\"," +
                    "\"end_latitude\":\"" + endLatitude + "\",\"end_longitude\":\"" + endLongitude + "\"}");

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
