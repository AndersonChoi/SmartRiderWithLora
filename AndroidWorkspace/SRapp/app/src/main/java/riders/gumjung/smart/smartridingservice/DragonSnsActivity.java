package riders.gumjung.smart.smartridingservice;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import riders.gumjung.smart.smartridingservice.dragonSns.DragonSnsRequest;
import riders.gumjung.smart.smartridingservice.dragonSns.SnsInfo;
import riders.gumjung.smart.smartridingservice.weather.WeatherRequest;

/**
 * Created by HackerAnderson on 2016. 7. 1..
 */

public class DragonSnsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double latitude;
    private double longitude;
    private LocationManager locationManager;
    private SnsInfo[] snsInfosArray;
    private Handler updateUIHandler, updateUIHandler2;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dragon_sns);


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_dragon_sns);
        mapFragment.getMapAsync(this);


        new Thread(new Runnable() {
            @Override
            public void run() {
                DragonSnsRequest dragonSnsRequest = new DragonSnsRequest();

                try {
                    Log.e("snsActivity", "get Array");
                    snsInfosArray = dragonSnsRequest.getSnsInformation();
                    updateUIHandler2.sendEmptyMessage(0);

                } catch (Exception e) {
                    Log.e("snsActivity", "exception :  " + e);

                    updateUIHandler.sendEmptyMessage(0);
                    finish();
                }
            }
        }).start();


        updateUIHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(DragonSnsActivity.this, "서버에 이상이 생겼습니다 SNS를 불러올 수 없습니다.", Toast.LENGTH_LONG).show();
            }
        };

        updateUIHandler2 = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                Log.e("snsActivity", "update ui in activity");

                int countMapMarkers = 0;
                double averageLatitude = 0;
                double averageLongitude = 0;

                for (int i = 0; i < snsInfosArray.length; i++) {
                    if (snsInfosArray[i].getStatus() == true) {
                        Log.e("snsActivity", i + " getUserId() : " + snsInfosArray[i].getUserId());
                        Log.e("snsActivity", i + " getSnsMessage() : " + snsInfosArray[i].getSnsMessage());
                        Log.e("snsActivity", i + " getLatitude() : " + snsInfosArray[i].getLatitude());
                        Log.e("snsActivity", i + " getLongitude() : " + snsInfosArray[i].getLongitude());

                        countMapMarkers++;

                        averageLatitude += Double.parseDouble(snsInfosArray[i].getLatitude());
                        averageLongitude += Double.parseDouble(snsInfosArray[i].getLongitude());

                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(snsInfosArray[i].getLatitude()), Double.parseDouble(snsInfosArray[i].getLongitude())))
                                .title("[" + snsInfosArray[i].getUserId() + "]")
                                .snippet("\"" + snsInfosArray[i].getSnsMessage() + "\"")
                        ).showInfoWindow();

                    } else {
                        Log.e("snsActivity", i + " getUserId() : " + snsInfosArray[i].getUserId());
                        Log.e("snsActivity", i + " no location!!!!!!!");
                    }
                }

                //move camera!
                averageLatitude = averageLatitude / countMapMarkers;
                averageLongitude = averageLongitude / countMapMarkers;

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(averageLatitude, averageLongitude), 6));


            }
        };
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
    }







}
