package riders.gumjung.smart.smartridingservice;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import riders.gumjung.smart.smartridingservice.exercise.DeviceDO;
import riders.gumjung.smart.smartridingservice.exercise.RequestCurrentExercise;
import riders.gumjung.smart.smartridingservice.exercise.RequestExerciseStatus;
import riders.gumjung.smart.smartridingservice.geofence.GeofenceGetRequest;
import riders.gumjung.smart.smartridingservice.mapLog.RequestEndDeviceMapLog;
import riders.gumjung.smart.smartridingservice.tracking.GetDeviceId;
import riders.gumjung.smart.smartridingservice.tracking.GetTrackingCount;
import riders.gumjung.smart.smartridingservice.tracking.GetTrackingTime;

/**
 * Created by HackerAnderson on 2016. 7. 1..
 */

public class ExerciseGoActivity extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback {


    private float mFilteringFactor = 0.7f;
    private float compassPosition = 0;
    private ImageView mPointer;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
    ImageView liveImage;

    private GoogleMap map;
    private TimerTask mTask;
    private Timer mTimer;

    private DeviceDO[] deviceDos;

    private TextView speedText, timeText;
    private int mainTime;
    private TimerTask timeTask;
    private Timer mtimeTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale.setDefault(new Locale("en", "US"));
        setContentView(R.layout.activity_exercise_go);

        liveImage = (ImageView) findViewById(R.id.live_icon_imageview);
        final Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(700);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        liveImage.startAnimation(animation);

        speedText = (TextView) findViewById(R.id.go_speed_text);
        timeText = (TextView) findViewById(R.id.go_exercise_time);


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_exercise);
        mapFragment.getMapAsync(this);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mPointer = (ImageView) findViewById(R.id.pointer);


        //1.send start action to server.
        //String memberUrl = "http://testweb.mybluemix.net/api/members/"+id+"/exercise/activate";


        new Thread() {
            public void run() {

                try {
                    RequestExerciseStatus requestExerciseStatus = new RequestExerciseStatus();
                    SharedPreferences pref = getSharedPreferences("LoginData", MODE_PRIVATE);
                    requestExerciseStatus.request(pref.getString("LoginId", ""), "activate");
                } catch (Exception e) {
                    Log.e("exercise go ", "exercise : exception : " + e);
                    Toast.makeText(ExerciseGoActivity.this, "Please contact administrator", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }.start();


        //2.get location from server(every 10seconds), and add new LATLNG array and show map current location


        //
        //String memberUrl = "http://testweb.mybluemix.net/api/end_devices/"+end_device+"/list/"+count;
        // for debug...
        // end_device=3;
        // count=1;
        // speed ok..

        final Handler mapHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                for (int i = 0; i < deviceDos.length - 1; i++) {
                    map.addPolyline(new PolylineOptions()
                            .add(new LatLng(Double.parseDouble(deviceDos[i].getLatitude()), Double.parseDouble(deviceDos[i].getLongitude())),
                                    new LatLng(Double.parseDouble(deviceDos[i + 1].getLatitude()), Double.parseDouble(deviceDos[i + 1].getLongitude()))
                            )
                            .width(5)
                            .color(Color.RED));
                }

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(deviceDos[0].getLatitude()),
                        Double.parseDouble(deviceDos[0].getLongitude())), 17));

                speedText.setText(deviceDos[0].getSpeed() + "km/h");


            }
        };

        mTask = new TimerTask() {
            @Override
            public void run() {


                //get enddevice array from server

                try {

                    GetTrackingCount getTrackingCount = new GetTrackingCount();
                    SharedPreferences pref = getSharedPreferences("LoginData", MODE_PRIVATE);
                    int count = getTrackingCount.getCount(pref.getString("LoginId", ""));
                    GetDeviceId getDeviceId = new GetDeviceId();
                    int deviceId = getDeviceId.getId(pref.getString("LoginId", ""));
                    RequestCurrentExercise requestCurrentExercise = new RequestCurrentExercise();

                    //deviceDos = requestCurrentExercise.getCurrentLog(deviceId,count);//for real
                    deviceDos = requestCurrentExercise.getCurrentLog(3, 1);//for debug

                    new Thread(new Runnable() {
                        @Override
                        public void run() {//refresh map
                            mapHandler.sendEmptyMessage(0);

                        }
                    }
                    ).start();

                } catch (Exception e) {
                    Log.e("timer", " excpetion : " + e);
                }
            }
        };

        mTimer = new Timer();
        mTimer.schedule(mTask, 1000, 10000);




        final Handler timeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {



                mainTime++;
                int hour = (mainTime/1000000) %100;
                int min = (mainTime/10000) %100;
                int sec = (mainTime / 100) %100;
                int msec = (mainTime % 100) *10;
                String strTime = String.format("%02d:%02d:%02d:%03d", hour,min,sec, msec);
                timeText.setText(strTime);
            }
        };

        timeTask = new TimerTask() {
            public void run() {
                timeHandler.sendEmptyMessage(0);
            }
        };
        mtimeTimer = new Timer();
        mtimeTimer.schedule(timeTask, 1000, 10);


    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder ab = new AlertDialog.Builder(ExerciseGoActivity.this);
        ab.setTitle("Are you sure, you want to quit exercise?");
        ab.setIcon(R.drawable.ic_launcher);
        ab.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //send end action to server.


                //
                //String memberUrl = "http://testweb.mybluemix.net/api/members/"+id+"/exercise/deactivate";


                new Thread() {
                    public void run() {

                        try {
                            RequestExerciseStatus requestExerciseStatus = new RequestExerciseStatus();
                            SharedPreferences pref = getSharedPreferences("LoginData", MODE_PRIVATE);
                            requestExerciseStatus.request(pref.getString("LoginId", ""), "deactivate");
                        } catch (Exception e) {
                            Log.e("exercise go ", "exercise : exception : " + e);
                            Toast.makeText(ExerciseGoActivity.this, "Please contact administrator", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }.start();


                finish();
            }
        });
        ab.setNegativeButton("NO", null);
        ab.show();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;

            compassPosition = (azimuthInDegress * mFilteringFactor) + (compassPosition * (1.0f - mFilteringFactor));
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    -compassPosition,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);
            ra.setDuration(400);
            ra.setFillAfter(true);
            mPointer.startAnimation(ra);
            mCurrentDegree = -azimuthInDegress;
        }
    }

    @Override
    protected void onDestroy() {
        mTimer.cancel();
        mtimeTimer.cancel();
        super.onDestroy();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onMapReady(final GoogleMap readyMap) {
        map = readyMap;
    }
}
