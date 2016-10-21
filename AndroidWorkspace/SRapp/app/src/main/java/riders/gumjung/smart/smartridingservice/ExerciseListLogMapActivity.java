package riders.gumjung.smart.smartridingservice;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Locale;

import riders.gumjung.smart.smartridingservice.tracking.GetDeviceId;
import riders.gumjung.smart.smartridingservice.tracking.GetTrackingCount;
import riders.gumjung.smart.smartridingservice.tracking.GetTrackingTime;

/**
 * Created by HackerAnderson on 2016. 10. 21..
 */

public class ExerciseListLogMapActivity  extends AppCompatActivity  implements OnMapReadyCallback {


    private GoogleMap map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale.setDefault(new Locale ("en", "US"));
        setContentView(R.layout.activity_exercise_log_map);

        Intent intent = getIntent();
        String number = intent.getStringExtra("number");


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_exercise_log_review);
        mapFragment.getMapAsync(this);










        final Handler mHandler = new Handler();
        new Thread() {
            public void run() {
                SharedPreferences pref = getSharedPreferences("LoginData", MODE_PRIVATE);
                GetDeviceId getDeviceId = new GetDeviceId();
                int deviceId = getDeviceId.getId(pref.getString("LoginId", ""));






                mHandler.post(new Runnable(){
                    @Override
                    public void run() {
                        //add map marker

                        /*
                        Polyline line = map.addPolyline(new PolylineOptions()
                                .add(new LatLng(51.5, -0.1), new LatLng(40.7, -74.0))
                                .width(5)
                                .color(Color.RED));*/


                    }
                });

            }
        }.start();





    }



    @Override
    public void onMapReady(final GoogleMap readyMap) {
        map = readyMap;
    }
}
