package riders.gumjung.smart.smartridingservice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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

import riders.gumjung.smart.smartridingservice.dragonSns.DragonSnsRequest;
import riders.gumjung.smart.smartridingservice.dragonSns.SnsInfo;
import riders.gumjung.smart.smartridingservice.geofence.GeofenceRequest;

/**
 * Created by HackerAnderson on 2016. 7. 6..
 */

public class GeoFenceActivity extends Activity implements OnMapReadyCallback, View.OnTouchListener {

    private double latitude;
    private double longitude;
    private ProgressDialog progDialog;
    private Handler updateUIHandler, decisionGeoFenceHandler, decisionGeoFenceHandler2;
    private LocationManager locationManager;
    private ImageView drawImageView;
    private float downx = 0, downy = 0, upx = 0, upy = 0;
    private Bitmap bitmap;
    private Button noDrawCanvasButton, setGeoFenceButton;
    private Canvas canvas;
    private Paint paint, paintCover;
    private boolean isGetLocation = false, isOnceCalled = true;


    private Boolean isGeofenceCorrect;
    private GoogleMap map;


    private SnsInfo[] snsInfosArray;


    private Handler myLocationHandler, myLocationHandler2;


    private double latitudeWidthIn17 = 0.006309;
    private double longitudeWidthIn17 = 0.004204;

    private float maxScreenWidth = 0;
    private float maxScreenHeight = 0;


    private float startX = 0;
    private float startY = 0;
    private float endX = 0;
    private float endY = 0;

    private double myLatitude = 0;
    private double myLongitude = 0;

    private double startLatitude = 0;
    private double startLongitude = 0;

    private double endLatitude = 0;
    private double endLongitude = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence);


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        maxScreenWidth = size.x;
        maxScreenHeight = size.y;

        Log.e("dd", "width:" + maxScreenWidth + "// height:" + maxScreenHeight);


        drawImageView = (ImageView) findViewById(R.id.draw_image_view);
        noDrawCanvasButton = (Button) findViewById(R.id.noDrawCanvasButton);
        setGeoFenceButton = (Button) findViewById(R.id.setGeoFenceButton);
        setGeoFenceButton.setOnClickListener(setGeoFenceHandler);

        Display currentDisplay = getWindowManager().getDefaultDisplay();
        float dw = currentDisplay.getWidth();
        float dh = currentDisplay.getHeight();

        bitmap = Bitmap.createBitmap((int) dw, (int) dh, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paintCover = new Paint();
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
                Toast.makeText(GeoFenceActivity.this, "GeoFence 범위를 드래그 하세요!", Toast.LENGTH_LONG).show();
            }
        };
        decisionGeoFenceHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                noDrawCanvasButton.setVisibility(View.VISIBLE);
                setGeoFenceButton.setVisibility(View.VISIBLE);
                Toast.makeText(GeoFenceActivity.this, "해당범위로 GeoFence를 설정하시겠습니까?", Toast.LENGTH_LONG).show();
            }
        };


        decisionGeoFenceHandler2 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(GeoFenceActivity.this, "반드시 전동휠이 지오펜스 내부에 있어야 합니다!! 다시 설정해주세요.", Toast.LENGTH_LONG).show();
                recreate();
            }
        };

        myLocationHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {


                boolean isUserDataExist = false;
                int userIndex = 0;
                for (int i = 0; i < snsInfosArray.length; i++) {
                    if (snsInfosArray[i].getStatus() == true) {
                        SharedPreferences pref = getSharedPreferences("LoginData", MODE_PRIVATE);
                        String myId = pref.getString("LoginId", "");
                        if (snsInfosArray[i].getUserId().equals(myId)) {
                            isUserDataExist = true;
                            userIndex = i;
                            break;
                        }
                    }
                }

                if (isUserDataExist) {
                    myLatitude = Double.parseDouble(snsInfosArray[userIndex].getLatitude());
                    myLongitude = Double.parseDouble(snsInfosArray[userIndex].getLongitude());

                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(myLatitude, myLongitude))
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    );

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(snsInfosArray[userIndex].getLatitude()),
                            Double.parseDouble(snsInfosArray[userIndex].getLongitude())), 17));
                    progDialog.dismiss();
                }

            }
        };


        myLocationHandler2 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(GeoFenceActivity.this, "내 위치를 가져올 수 없습니다. 관리자에게 문의하세요!", Toast.LENGTH_LONG).show();
                finish();
            }
        };


        new Thread(new Runnable() {
            @Override
            public void run() {
                DragonSnsRequest dragonSnsRequest = new DragonSnsRequest();

                try {
                    Log.e("geofence", "get Array");
                    snsInfosArray = dragonSnsRequest.getSnsInformation();
                    myLocationHandler.sendEmptyMessage(0);
                } catch (Exception e) {
                    Log.e("geofence", "exception :  " + e);
                    myLocationHandler2.sendEmptyMessage(0);
                }
            }
        }
        ).start();

    }


    Button.OnClickListener setGeoFenceHandler = new View.OnClickListener() {
        public void onClick(View v) {

            final Handler geofenceHandler = new
                    Handler() {
                        @Override
                        public void handleMessage(Message msg) {

                            if (isGeofenceCorrect) {

                                Toast.makeText(GeoFenceActivity.this, "GeoFence가 설정되었습니다!!", Toast.LENGTH_SHORT).show();

                                Intent reLaunchMain=new Intent(GeoFenceActivity.this,MainActivity.class);
                                reLaunchMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(reLaunchMain);



                            } else {
                                Toast.makeText(GeoFenceActivity.this, "GeoFence가 설정가 설정되지 않았습니다. 관리자에게 문의하세요.", Toast.LENGTH_SHORT).show();
                                finish();
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
                    isGeofenceCorrect = geofenceRequest.sendGeofence(myId, String.format("%.6f", startLatitude)
                            , String.format("%.6f", startLongitude)
                            , String.format("%.6f", endLatitude)
                            , String.format("%.6f", endLongitude));

                    geofenceHandler.sendEmptyMessage(0);
                }
            }
            ).start();

        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.e("xy", "touched..");
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = event.getX();
                downy = event.getY();
                Log.e("xy", "down x:" + downx + ",y:" + downy);
                startX = downx;
                startY = downy;
                break;
            case MotionEvent.ACTION_UP:
                upx = event.getX();
                upy = event.getY();
                Log.e("xy", "up x:" + upx + ",y:" + upy);
                canvas.drawRect(downx, downy, upx, upy, paintCover);
                canvas.drawRect(downx, downy, upx, upy, paint);
                drawImageView.invalidate();

                endX = upx;
                endY = upy;
                boolean isContainCenter = true;
                if (isContainCenter) {

                    //calculate start and end langitude, longitude in this place...


                    Log.e("xy", "start: " + startX + "," + startY + " / end: " + endX + "," + endY);

                    float rectangleStartX = 0;
                    float rectangleStartY = 0;
                    float rectangleEndX = 0;
                    float rectangleEndY = 0;


                    rectangleStartX = startX > endX ? endX : startX;
                    rectangleStartY = startY < endY ? startY : endY;

                    rectangleEndX = startX > endX ? startX : endX;
                    rectangleEndY = startY < endY ? endY : startY;


                    Log.e("xy", "EDIT start: " + rectangleStartX + "," + rectangleStartY + " / end: " + rectangleEndX + "," + rectangleEndY);

                    float deviceStartLatitude = (float) myLatitude + (float) (latitudeWidthIn17 / 2);
                    float deviceStartLongitude = (float) myLongitude - (float) (longitudeWidthIn17 / 2);

                    Log.e("xy result ", "\t ====");
                    Log.e("xy result ", "\t device :" + deviceStartLatitude + "," + deviceStartLongitude);

                    startLatitude = deviceStartLatitude - (latitudeWidthIn17 * (rectangleStartX / maxScreenWidth));
                    startLongitude = deviceStartLongitude + (longitudeWidthIn17 * (rectangleStartY / maxScreenHeight));

                    endLatitude = deviceStartLatitude - (latitudeWidthIn17 * (rectangleEndX / maxScreenWidth));
                    endLongitude = deviceStartLongitude + (longitudeWidthIn17 * (rectangleEndY / maxScreenHeight));


                    boolean isInRectangle = false;

                    if (rectangleStartX < (maxScreenWidth / 2)
                            && rectangleEndX > (maxScreenWidth / 2)
                            && rectangleStartY < (maxScreenHeight / 2)
                            && rectangleEndY > (maxScreenHeight / 2))
                        isInRectangle = true;

                    if (isInRectangle) {
                        decisionGeoFenceHandler.sendEmptyMessage(0);
                    } else {
                        decisionGeoFenceHandler2.sendEmptyMessage(0);
                    }


                } else {

                }
                break;
            case MotionEvent.ACTION_MOVE:
                upx = event.getX();
                upy = event.getY();
                //Log.e("xy", "move x:" + upx + ",y:" + upy);
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                canvas.drawRect(downx, downy, upx, upy, paintCover);
                canvas.drawRect(downx, downy, upx, upy, paint);
                drawImageView.invalidate();
                break;
        }
        return true;
    }


    @Override
    public void onMapReady(final GoogleMap readyMap) {
        map = readyMap;

    }
}
