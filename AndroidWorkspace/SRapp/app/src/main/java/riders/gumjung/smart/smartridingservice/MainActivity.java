package riders.gumjung.smart.smartridingservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import riders.gumjung.smart.smartridingservice.weather.WeatherInfo;
import riders.gumjung.smart.smartridingservice.weather.WeatherRequest;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static int menuNumber = 0;
    private ProgressDialog progDialog;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        latitude = 35.13501;
        longitude = 129.10445;

        setContentView(R.layout.activity_main);


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
        progDialog.setMessage("날씨를 받아오는 중입니다....");
        progDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                getWeather();

                updateUIHandler.sendEmptyMessage(0);
                progDialog.dismiss();
            }
        }).start();





        updateUIHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (weatherInfo.getStatus()) {
                    weatherStatus.setText(String.valueOf(weatherInfo.getSkyInfo())+",");
                    temperatureAverage.setText(String.valueOf(weatherInfo.getTemperature())+"°C");
                    temperatureMax.setText("최고 : "+weatherInfo.getMaxTemperature()+"°C");
                    temperatureMin.setText("최저 : "+weatherInfo.getMinTemperature()+"°C");
                }
            }
        };




    }

    void getWeather() {
        Log.e("weather", "lat,long" + latitude + "," + longitude);
        WeatherRequest weatherRequest = new WeatherRequest(latitude, longitude);
        weatherInfo = weatherRequest.getWeatherInformation();


        Log.e("weather", "weatherInfo.status:" + weatherInfo.getStatus());
        Log.e("weather", "weatherInfo.loca:" + weatherInfo.getLocation());
        Log.e("weather", "weatherInfo.skyinfo:" + weatherInfo.getSkyInfo());
        Log.e("weather", "weatherInfo.temperature:" + weatherInfo.getTemperature());
        Log.e("weather", "weatherInfo.maxT:" + weatherInfo.getMaxTemperature());
        Log.e("weather", "weatherInfo.minT:" + weatherInfo.getMinTemperature());
        Log.e("weather", "weatherInfo.obser:" + weatherInfo.getTimeObservation());
    }


    @Override
    public void onMapReady(final GoogleMap map) {


        LocationListener mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {


                MarkerOptions marker = new MarkerOptions();
                marker.position(new LatLng(location.getLatitude(), location.getLongitude()));
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location_black_24dp));
                map.clear();
                map.addMarker(marker);


                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                        .zoom(17)
                        .bearing(0)
                        .build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            }

            public void onProviderDisabled(String provider) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };


        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.MAPS_RECEIVE);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            //no permission
            Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_LONG).show();
            finish();
        } else {
            //permission ok
            //map.setMyLocationEnabled(true);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mLocationListener);

    }

    Button.OnClickListener menuButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.menu01_button:
                    introduceBackgroundLayout.setBackgroundColor(0xff86786e);
                    introduceBackgroundLayout.invalidate();
                    introduceMenuTitle.setText("GeoFence");
                    introduceMenuInfo.setText("GeoFence를 활용하여 도난방지 기능을 사용합니다.");
                    setArrowImageVisible(0);
                    menuNumber = 0;
                    break;
                case R.id.menu02_button:
                    introduceBackgroundLayout.setBackgroundColor(0xff2db6c9);
                    introduceMenuTitle.setText("DragonSNS");
                    introduceMenuInfo.setText("DragonSNS로 친구를 만나보세요.");
                    setArrowImageVisible(1);
                    menuNumber = 1;
                    break;
                case R.id.menu03_button:
                    introduceBackgroundLayout.setBackgroundColor(0xff115486);
                    introduceMenuTitle.setText("Exercise");
                    introduceMenuInfo.setText("운동을 기록할 수 있습니다.");
                    setArrowImageVisible(2);
                    menuNumber = 2;
                    break;
                case R.id.menu04_button:
                    introduceBackgroundLayout.setBackgroundColor(0xfffe9200);
                    introduceMenuTitle.setText("Log");
                    introduceMenuInfo.setText("운동 기록을 볼 수 있습니다.");
                    setArrowImageVisible(3);
                    menuNumber = 3;
                    break;
            }
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


}
