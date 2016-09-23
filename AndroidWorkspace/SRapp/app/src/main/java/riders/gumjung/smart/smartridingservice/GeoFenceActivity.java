package riders.gumjung.smart.smartridingservice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by HackerAnderson on 2016. 7. 6..
 */

public class GeoFenceActivity extends Activity implements OnMapReadyCallback, View.OnTouchListener {

    private double latitude;
    private double longitude;
    private ProgressDialog progDialog;
    private Handler updateUIHandler,decisionGeoFenceHandler;
    private LocationManager locationManager;
    private ImageView drawImageView;
    private float downx = 0, downy = 0, upx = 0, upy = 0;
    private Bitmap bitmap;
    private Button noDrawCanvasButton,setGeoFenceButton;
    private Canvas canvas;
    private Paint paint,paintCover;
    private boolean isGetLocation=false,isOnceCalled=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence);
        drawImageView = (ImageView) findViewById(R.id.draw_image_view);
        noDrawCanvasButton = (Button)findViewById(R.id.noDrawCanvasButton);
        setGeoFenceButton = (Button)findViewById(R.id.setGeoFenceButton);
        setGeoFenceButton.setOnClickListener(setGeoFenceHandler);

        Display currentDisplay = getWindowManager().getDefaultDisplay();
        float dw = currentDisplay.getWidth();
        float dh = currentDisplay.getHeight();

        bitmap = Bitmap.createBitmap((int) dw, (int) dh ,Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paintCover=new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.STROKE);
        paintCover.setColor(Color.BLUE);
        paintCover.setStrokeWidth(10);
        paintCover.setAlpha(40);
        paintCover.setStyle(Paint.Style.FILL);




        drawImageView.setImageBitmap(bitmap);
        drawImageView.setOnTouchListener(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_geofence);
        mapFragment.getMapAsync(this);



        progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setMessage("위치를 받아오는 중입니다....");
        progDialog.setCancelable(true);
        progDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        progDialog.show();

        updateUIHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                Toast.makeText(GeoFenceActivity.this,"GeoFence 범위를 드래그 하세요!",Toast.LENGTH_LONG).show();
            }
        };
        decisionGeoFenceHandler= new Handler() {
            @Override
            public void handleMessage(Message msg) {
                noDrawCanvasButton.setVisibility(View.VISIBLE);
                setGeoFenceButton.setVisibility(View.VISIBLE);
                Toast.makeText(GeoFenceActivity.this,"해당범위로 GeoFence를 설정하시겠습니까?",Toast.LENGTH_LONG).show();
            }
        };

    }




    Button.OnClickListener setGeoFenceHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(GeoFenceActivity.this,"GeoFence가 설정되었습니다!!",Toast.LENGTH_SHORT).show();
            finish();
        }
    };
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.e("xy","touched..");
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = event.getX();
                downy = event.getY();
                Log.e("xy","down x:"+downx+",y:"+downy);
                break;
            case MotionEvent.ACTION_UP:
                upx = event.getX();
                upy = event.getY();
                Log.e("xy","down x:"+upx+",y:"+upy);
                canvas.drawRect(downx, downy, upx, upy, paintCover);
                canvas.drawRect(downx, downy, upx, upy, paint);
                drawImageView.invalidate();
                boolean isContainCenter=true;
                if(isContainCenter)
                {
                    decisionGeoFenceHandler.sendEmptyMessage(0);
                }else
                {

                }
                break;
            case MotionEvent.ACTION_MOVE:
                upx = event.getX();
                upy = event.getY();
                Log.e("xy","move x:"+upx+",y:"+upy);
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                canvas.drawRect(downx, downy, upx, upy, paintCover);
                canvas.drawRect(downx, downy, upx, upy, paint);
                drawImageView.invalidate();
                break;
        }
        return true;
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


                isGetLocation=true;
                if(isGetLocation&&isOnceCalled){
                    isOnceCalled=false;
                    updateUIHandler.sendEmptyMessage(0);
                    progDialog.dismiss();
                }

            }

            public void onProviderDisabled(String provider) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };


        int permissionCheck = ContextCompat.checkSelfPermission(GeoFenceActivity.this, Manifest.permission.MAPS_RECEIVE);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            //no permission
            Toast.makeText(GeoFenceActivity.this, "Permission denied", Toast.LENGTH_LONG);
            finish();
        } else {
            //permission ok
            //map.setMyLocationEnabled(true);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mLocationListener);

    }
}
