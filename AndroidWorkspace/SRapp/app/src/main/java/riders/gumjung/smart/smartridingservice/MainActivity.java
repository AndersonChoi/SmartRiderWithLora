package riders.gumjung.smart.smartridingservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.location.LocationManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import riders.gumjung.smart.smartridingservice.dragonSns.DragonSnsRequest;
import riders.gumjung.smart.smartridingservice.geofence.GeofenceGetRequest;
import riders.gumjung.smart.smartridingservice.geofence.GeofenceInfo;
import riders.gumjung.smart.smartridingservice.geofence.GeofenceRequest;
import riders.gumjung.smart.smartridingservice.login.LoginRequest;
import riders.gumjung.smart.smartridingservice.token.TokenRequest;
import riders.gumjung.smart.smartridingservice.weather.WeatherInfo;
import riders.gumjung.smart.smartridingservice.weather.WeatherRequest;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static int menuNumber = 0;
    private ProgressDialog progDialog, progDialog2;
    private Handler updateUIHandler;
    private double latitude;
    private double longitude;
    private LocationManager locationManager;
    private Button menuButtonGeoFence, menuButtonDragonSNS, menuButtonExercise, menuButtonLog;
    private Button startMenuButton;
    private RelativeLayout introduceBackgroundLayout;
    private TextView introduceMenuTitle, introduceMenuInfo;
    private TextView weatherStatus, temperatureAverage, temperatureMax, temperatureMin;
    private ImageView arrowImage01, arrowImage02, arrowImage03, arrowImage04;
    private WeatherInfo weatherInfo;


    private RelativeLayout loginLayout;
    private Button loginButton;
    private EditText idEditText, passwordEditText;
    private Boolean isUserExist = false;

    private RelativeLayout geofenceLayout;
    private Button geofenceUnlockButton;


    private GoogleMap map;
    private double myLatitude = 0;
    private double myLongitude = 0;
    private Handler myLocationHandler, myLocationHandler2;
    private GeofenceInfo[] geofenceInfoArray;
    private  Boolean isGeofenceCorrect=false;

    private static final String TAG = "MainActivity";


    private TimerTask mTask;
    private Timer mTimer;


    private TimerTask mWeatherTask;
    private Timer mWeatherTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale.setDefault(new Locale ("en", "US"));


        setContentView(R.layout.activity_main);


        loginLayout = (RelativeLayout) findViewById(R.id.login_layout);
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(loginButtonClickListener);
        idEditText = (EditText) findViewById(R.id.login_id_edit_text);
        passwordEditText = (EditText) findViewById(R.id.login_password_edit_text);

        geofenceLayout = (RelativeLayout) findViewById(R.id.geofence_lock_layout);
        geofenceUnlockButton = (Button) findViewById(R.id.geofence_unlock_button);
        geofenceUnlockButton.setOnClickListener(mGeofenceUnlockListener);

        SharedPreferences pref = getSharedPreferences("LoginData", MODE_PRIVATE);
        idEditText.setText(pref.getString("LoginId", ""));
        passwordEditText.setText(pref.getString("LoginPassword", ""));


        //check geofence in this


        progDialog2 = new ProgressDialog(this);
        progDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog2.setMessage("Receiving data...");
        progDialog2.setCancelable(true);
        progDialog2.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });


        //check geofence end..

        introduceBackgroundLayout = (RelativeLayout) findViewById(R.id.introduce_menu);
        introduceMenuTitle = (TextView) findViewById(R.id.menu_title_text);
        introduceMenuInfo = (TextView) findViewById(R.id.menu_introduce_text);
        arrowImage01 = (ImageView) findViewById(R.id.menu01_arrow_image);
        arrowImage02 = (ImageView) findViewById(R.id.menu02_arrow_image);
        arrowImage03 = (ImageView) findViewById(R.id.menu03_arrow_image);
        arrowImage04 = (ImageView) findViewById(R.id.menu04_arrow_image);
        weatherStatus = (TextView) findViewById(R.id.weater_status_text);
        temperatureAverage = (TextView) findViewById(R.id.average_temperature_status);
        temperatureMax = (TextView) findViewById(R.id.max_temperature_status);
        temperatureMin = (TextView) findViewById(R.id.min_temperature_status);
        startMenuButton = (Button) findViewById(R.id.start_menu_button);
        menuButtonGeoFence = (Button) findViewById(R.id.menu01_button);
        menuButtonDragonSNS = (Button) findViewById(R.id.menu02_button);
        menuButtonExercise = (Button) findViewById(R.id.menu03_button);
        menuButtonLog = (Button) findViewById(R.id.menu04_button);
        menuButtonGeoFence.setOnClickListener(menuButtonClickListener);
        menuButtonDragonSNS.setOnClickListener(menuButtonClickListener);
        menuButtonExercise.setOnClickListener(menuButtonClickListener);
        menuButtonLog.setOnClickListener(menuButtonClickListener);
        startMenuButton.setOnClickListener(startMenuButtonClickListener);


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setMessage("Receiving data....");
        progDialog.show();


        updateUIHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (weatherInfo.getStatus()) {
                    weatherStatus.setText(String.valueOf(weatherInfo.getSkyInfo()) + ",");
                    temperatureAverage.setText(String.valueOf(weatherInfo.getTemperature()) + "°C");
                    temperatureMax.setText("최고 : " + weatherInfo.getMaxTemperature() + "°C");
                    temperatureMin.setText("최저 : " + weatherInfo.getMinTemperature() + "°C");

                    //to english..
                    weatherStatus.setText("Cloudy ,");
                    temperatureAverage.setText("20.7°C");
                    temperatureMax.setText("max: 24.1°C");
                    temperatureMin.setText("min : 16.0°C");
                }
            }
        };


        myLocationHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {


                boolean isUserDataExist = false;
                int userIndex = 0;
                for (int i = 0; i < geofenceInfoArray.length; i++) {
                    if (geofenceInfoArray[i].getStatus() == true) {
                        SharedPreferences pref = getSharedPreferences("LoginData", MODE_PRIVATE);
                        String myId = pref.getString("LoginId", "");
                        if (geofenceInfoArray[i].getUserId().equals(myId)) {
                            isUserDataExist = true;
                            userIndex = i;
                            break;
                        }
                    }
                }

                if (isUserDataExist) {
                    myLatitude = Double.parseDouble(geofenceInfoArray[userIndex].getLatitude());
                    myLongitude = Double.parseDouble(geofenceInfoArray[userIndex].getLongitude());

                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(myLatitude, myLongitude))
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    );

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(geofenceInfoArray[userIndex].getLatitude()),
                            Double.parseDouble(geofenceInfoArray[userIndex].getLongitude())), 17));

                    latitude =Double.parseDouble(geofenceInfoArray[userIndex].getLatitude());
                    longitude=Double.parseDouble(geofenceInfoArray[userIndex].getLongitude());

                    progDialog.dismiss();



                    // and check user has geofence information if it is please view to gone..

                    Log.e("check geofence","geofence? : "+geofenceInfoArray[userIndex].getGenfenceExist());


                    if(geofenceInfoArray[userIndex].getGenfenceExist()){
                        //true genfence is exist..
                        geofenceLayout.setVisibility(View.VISIBLE);

                    }else{
                        geofenceLayout.setVisibility(View.GONE);

                    }
                }

            }
        };

        myLocationHandler2 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(MainActivity.this, "Data read is abnormaly done. Please contact administrator", Toast.LENGTH_LONG).show();
            }
        };



        mTask = new TimerTask() {
            @Override
            public void run() {
                Log.e("timer", "timer start!!");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        GeofenceGetRequest geofenceRequest = new GeofenceGetRequest();
                        try {
                            Log.e("geofence", "get Array");
                            geofenceInfoArray = geofenceRequest.getGeofenceInfo();
                            myLocationHandler.sendEmptyMessage(0);
                        } catch (Exception e) {
                            Log.e("error..","e:"+e);
                            myLocationHandler2.sendEmptyMessage(0);
                            finish();
                        }
                    }
                }
                ).start();
            }
        };

        mTimer = new Timer();
        mTimer.schedule(mTask, 1000, 10000);



        mWeatherTask = new TimerTask() {
            @Override
            public void run() {
                Log.e("weathertimer", "timer start!!");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e("weather", "get Array");
                            getWeather();
                            updateUIHandler.sendEmptyMessage(0);
                        } catch (Exception e) {
                            Log.e("error..","e:"+e);
                            myLocationHandler2.sendEmptyMessage(0);
                            finish();
                        }
                    }
                }
                ).start();
            }
        };
        mWeatherTimer = new Timer();
        mWeatherTimer.schedule(mWeatherTask, 3000);






        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("Firebase KEY in main", "Key: " + key + " Value: " + value);


            }
        }
        if ((pref.getString("LoginId", "").length() != 0) && (pref.getString("LoginPassword", "").length() != 0)) {
            //로그인 정보가 있으면 바로 로그인 화면을 없애버린다.
            loginLayout.setVisibility(View.GONE);
        }else{
            //로그인정보가 없음녀 로그인화면을 보여주고 프로그래스 다이얼로그를 없애버린다 로그인할수있도록
            progDialog.dismiss();
        }





    }


    void getWeather() {
        WeatherRequest weatherRequest = new WeatherRequest(latitude, longitude);
        weatherInfo = weatherRequest.getWeatherInformation();
    }


    @Override
    public void onMapReady(final GoogleMap readyMap) {

        map = readyMap;
    }

    Button.OnClickListener menuButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.menu01_button:
                    introduceBackgroundLayout.setBackgroundColor(0xff86786e);
                    introduceBackgroundLayout.invalidate();
                    introduceMenuTitle.setText("GeoFence");
                    introduceMenuInfo.setText("Execute burglar alarm system with GeoFence");
                    setArrowImageVisible(0);
                    menuNumber = 0;
                    break;
                case R.id.menu02_button:
                    introduceBackgroundLayout.setBackgroundColor(0xff2db6c9);
                    introduceMenuTitle.setText("DragonSNS");
                    introduceMenuInfo.setText("Meet social friends with DragonSNS");
                    setArrowImageVisible(1);
                    menuNumber = 1;
                    break;
                case R.id.menu03_button:
                    introduceBackgroundLayout.setBackgroundColor(0xff115486);
                    introduceMenuTitle.setText("Exercise");
                    introduceMenuInfo.setText("Record exercise");
                    setArrowImageVisible(2);
                    menuNumber = 2;
                    break;
                case R.id.menu04_button:
                    introduceBackgroundLayout.setBackgroundColor(0xfffe9200);
                    introduceMenuTitle.setText("Log");
                    introduceMenuInfo.setText("Review exercise log");
                    setArrowImageVisible(3);
                    menuNumber = 3;
                    break;
            }
        }
    };

    Button.OnClickListener mGeofenceUnlockListener = new View.OnClickListener() {
        public void onClick(View v) {


            final Handler geofenceHandler = new
                    Handler() {
                        @Override
                        public void handleMessage(Message msg) {

                            if (isGeofenceCorrect) {

                                Toast.makeText(MainActivity.this, "GeoFence is release!!", Toast.LENGTH_SHORT).show();
                                recreate();
                            } else {
                                Toast.makeText(MainActivity.this, "GeoFence is abnormaly release! Please contact administrator.", Toast.LENGTH_SHORT).show();
                                recreate();
                            }
                        }
                    };

            isGeofenceCorrect = false;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    GeofenceRequest geofenceRequest = new GeofenceRequest();
                    SharedPreferences pref = getSharedPreferences("LoginData", MODE_PRIVATE);
                    String myId = pref.getString("LoginId", "");
                    isGeofenceCorrect = geofenceRequest.sendGeofence(myId,"","","","");
                    geofenceHandler.sendEmptyMessage(0);
                }
            }
            ).start();




        }
    };

    Button.OnClickListener loginButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {


            final String inputId = idEditText.getText().toString();
            final String inputPassword = passwordEditText.getText().toString();

            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {

                    if (isUserExist) {



                        Toast.makeText(MainActivity.this, "Log in done.", Toast.LENGTH_LONG).show();


                        SharedPreferences prefs = getSharedPreferences("LoginData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("LoginId", inputId);
                        editor.putString("LoginPassword", inputPassword);
                        editor.commit();


                        loginLayout.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(MainActivity.this, "Server is blow out. Please contact administrator", Toast.LENGTH_LONG).show();
                    }
                }
            };

            new Thread() {
                public void run() {
                    LoginRequest loginRequest = new LoginRequest();
                    isUserExist = loginRequest.isUserExist(inputId, inputPassword);


                    if(isUserExist){
                        //send token to user data

                        String token = FirebaseInstanceId.getInstance().getToken();


                        TokenRequest tokenRequest = new TokenRequest();
                        tokenRequest.sendToken(inputId, token);



                    }




                    handler.sendEmptyMessage(0);
                }
            }.start();


        }
    };

    Button.OnClickListener startMenuButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent;
            switch (menuNumber) {
                case 0:
                    intent = new Intent(MainActivity.this, GeoFenceActivity.class);
                    startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(MainActivity.this, DragonSnsActivity.class);
                    startActivity(intent);
                    break;
                case 2:
                    intent = new Intent(MainActivity.this, ExerciseGoActivity.class);
                    startActivity(intent);
                    break;
                case 3:
                    intent = new Intent(MainActivity.this, ExerciseListActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };


    void setArrowImageVisible(int arrowNumber) {
        arrowImage01.setVisibility(View.GONE);
        arrowImage02.setVisibility(View.GONE);
        arrowImage03.setVisibility(View.GONE);
        arrowImage04.setVisibility(View.GONE);
        switch (arrowNumber) {
            case 0:
                arrowImage01.setVisibility(View.VISIBLE);
                break;
            case 1:
                arrowImage02.setVisibility(View.VISIBLE);
                break;
            case 2:
                arrowImage03.setVisibility(View.VISIBLE);
                break;
            case 3:
                arrowImage04.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        Log.i("test", "onDstory()");
        mTimer.cancel();
        mWeatherTimer.cancel();
        super.onDestroy();
    }

}
