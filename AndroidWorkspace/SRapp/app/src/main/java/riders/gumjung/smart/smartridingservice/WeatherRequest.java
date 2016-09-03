package riders.gumjung.smart.smartridingservice;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by HackerAnderson on 2016. 7. 1..
 */
public class WeatherRequest {

    private double latitude;
    private double longitude;
    private static String API_KEY = "4a7393c0-529c-3926-bb11-bb6ea739936e";
    private static String CONTENT_TYPE = "application/json";

    WeatherRequest(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;

    }


    public WeatherInfo getWeatherInformation() {
        String location = "";
        String skyInfo = "";
        double temperature = 0;
        double minTemperature = 0;
        double maxTemperature = 0;
        String observationTime = "";
        WeatherInfo weatherInfo = new WeatherInfo();
        String weatherUrl = "http://apis.skplanetx.com/weather/current/minutely?lon=" + longitude + "&lat=" + latitude + "&version=1";
        String jsonString;
        JSONObject jsonObj;

        try {
            jsonString = GetHttpRequest(weatherUrl);
        } catch (IOException e) {
            e.printStackTrace();
            weatherInfo.setStatus(false);
            return weatherInfo;
        }

        try {
            jsonObj = new JSONObject(jsonString);

            JSONObject weatherObj = new JSONObject(jsonObj.getString("weather"));
            JSONArray weatherArray = new JSONArray(weatherObj.getString("minutely"));
            JSONObject insideObject = weatherArray.getJSONObject(0);
            JSONObject stationObject = new JSONObject(insideObject.getString("station"));
            location = stationObject.getString("name");
            JSONObject skyObject = new JSONObject(insideObject.getString("sky"));
            skyInfo = skyObject.getString("name");
            JSONObject temperatureObject = new JSONObject(insideObject.getString("temperature"));
            temperature = Double.parseDouble(temperatureObject.getString("tc"));
            maxTemperature = Double.parseDouble(temperatureObject.getString("tmax"));
            minTemperature = Double.parseDouble(temperatureObject.getString("tmin"));
            observationTime = insideObject.getString("timeObservation");


        } catch (JSONException e) {
            e.printStackTrace();
            weatherInfo.setStatus(false);
            return weatherInfo;
        }


        weatherInfo.setStatus(true);
        weatherInfo.setLocation(location);
        weatherInfo.setSkyInfo(skyInfo);
        weatherInfo.setTemperature(temperature);
        weatherInfo.setMaxTemperature(maxTemperature);
        weatherInfo.setMinTemperature(minTemperature);
        weatherInfo.setTimeObservation(observationTime);
        return weatherInfo;
    }


    private static String GetHttpRequest(String urlStr) throws IOException {
        URL url = new URL(urlStr);

        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

        conn.setRequestProperty("Content-Type", CONTENT_TYPE);
        conn.setRequestProperty("appKey", API_KEY);

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
