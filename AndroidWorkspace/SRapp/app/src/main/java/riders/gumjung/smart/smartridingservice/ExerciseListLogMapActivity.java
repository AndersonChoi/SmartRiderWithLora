package riders.gumjung.smart.smartridingservice;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Locale;

import riders.gumjung.smart.smartridingservice.mapLog.RequestEndDeviceMapLog;
import riders.gumjung.smart.smartridingservice.tracking.GetDeviceId;
import riders.gumjung.smart.smartridingservice.tracking.GetTrackingCount;
import riders.gumjung.smart.smartridingservice.tracking.GetTrackingTime;

/**
 * Created by HackerAnderson on 2016. 10. 21..
 */

public class ExerciseListLogMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ProgressDialog progDialog2;
    private double lat = 0, lng = 0;
    private TextView logDistance, startTime, endTime, averageSpeed, exerciseEffect;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale.setDefault(new Locale("en", "US"));
        setContentView(R.layout.activity_exercise_log_map);

        logDistance = (TextView) findViewById(R.id.log_distance);
        startTime = (TextView) findViewById(R.id.log_start_time);
        endTime = (TextView) findViewById(R.id.log_end_time);
        averageSpeed = (TextView) findViewById(R.id.log_average_speed);
        exerciseEffect = (TextView) findViewById(R.id.log_exercise_effect);


        progDialog2 = new ProgressDialog(ExerciseListLogMapActivity.this);
        progDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog2.setMessage("Receiving log data...");
        progDialog2.setCancelable(true);
        progDialog2.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        progDialog2.show();


        Intent intent = getIntent();
        final int number = intent.getIntExtra("number", 0);


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_exercise_log_review);
        mapFragment.getMapAsync(this);


        final Handler mHandler = new Handler();
        new Thread() {
            public void run() {
                SharedPreferences pref = getSharedPreferences("LoginData", MODE_PRIVATE);
                GetDeviceId getDeviceId = new GetDeviceId();
                int deviceId = getDeviceId.getId(pref.getString("LoginId", ""));

                Log.e("maplog", "get lat lng ");
                try {

                    RequestEndDeviceMapLog requestEndDeviceMapLog = new RequestEndDeviceMapLog();
                    final LatLng[] latLngs;

                    Log.e("maplog", "deviceId:"+deviceId+"/number:"+number);
                    latLngs = requestEndDeviceMapLog.getMaps(deviceId, number);
                    Log.e("maplog", "latlng length:" + latLngs.length);

                    for (int i = 0; i < latLngs.length; i++) {
                        Log.e("maplog", "lat : " + latLngs[i].latitude + " / long : " + latLngs[i].longitude);
                    }


                    GetTrackingTime getTrackingTime = new GetTrackingTime();
                    String log = getTrackingTime.getTimeLog(deviceId, number);
                    final String exerciseStartTime = log.substring(log.indexOf("T") + 1, log.indexOf("|") - 5);
                    final String exerciseEndTime = log.substring(log.indexOf("T", log.indexOf("T") + 1) + 1, log.length() - 5);
                    final double duringTime = 60*(Double.parseDouble(exerciseEndTime.substring(0,2))-Double.parseDouble(exerciseStartTime.substring(0,2)))+
                            (Double.parseDouble(exerciseEndTime.substring(3,5))-Double.parseDouble(exerciseStartTime.substring(3,5)));

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            double distance = 0;

                            for (int i = 0; i < latLngs.length - 1; i++) {
                                lat += latLngs[i].latitude;
                                lng += latLngs[i].longitude;
                                map.addPolyline(new PolylineOptions()
                                        .add(latLngs[i], latLngs[i + 1])
                                        .width(5)
                                        .color(Color.RED));

                                Location locationA = new Location("point A");
                                locationA.setLatitude(latLngs[i].latitude);
                                locationA.setLongitude(latLngs[i].longitude);

                                Location locationB = new Location("point B");
                                locationB.setLatitude(latLngs[i + 1].latitude);
                                locationB.setLongitude(latLngs[i + 1].longitude);
                                distance += locationA.distanceTo(locationB);
                            }


                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs[0], 17));


                            MarkerOptions optSecond = new MarkerOptions();
                            optSecond.position(latLngs[latLngs.length - 1]);// 위도 • 경도
                            optSecond.title("End position");
                            optSecond.snippet("end time : " + exerciseEndTime);
                            map.addMarker(optSecond).showInfoWindow();

                            optSecond = new MarkerOptions();
                            optSecond.position(latLngs[0]);// 위도 • 경도
                            optSecond.title("Start position");
                            optSecond.snippet("start time : " + exerciseStartTime);
                            map.addMarker(optSecond).showInfoWindow();


                            logDistance.setText(String.format("%.2f", distance) + "m");
                            startTime.setText(exerciseStartTime);
                            endTime.setText(exerciseEndTime);
                            averageSpeed.setText(String.format("%.0f", (distance*60/duringTime)) + "m/h");
                            exerciseEffect.setText(String.format("%.2f", 65*0.13* duringTime) +"Kcal");
                            progDialog2.dismiss();
                        }
                    });

                } catch (Exception e) {
                    Log.e("maplog", "" + e);
                    finish();
                }

            }
        }.start();


    }


    @Override
    public void onMapReady(final GoogleMap readyMap) {
        map = readyMap;
    }
}
