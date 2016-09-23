package riders.gumjung.smart.smartridingservice;

import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import riders.gumjung.smart.smartridingservice.dragonSns.DragonSnsRequest;
import riders.gumjung.smart.smartridingservice.dragonSns.SnsInfo;
import riders.gumjung.smart.smartridingservice.weather.WeatherRequest;

/**
 * Created by HackerAnderson on 2016. 7. 1..
 */

public class DragonSnsActivity extends AppCompatActivity {

    private double latitude;
    private double longitude;
    private LocationManager locationManager;

    private SnsInfo[] snsInfosArray;

    private Handler updateUIHandler,updateUIHandler2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dragon_sns);


        new Thread(new Runnable() {
            @Override
            public void run() {
                DragonSnsRequest dragonSnsRequest = new DragonSnsRequest();

                try {
                    Log.e("snsActivity", "get Array");
                    snsInfosArray = dragonSnsRequest.getSnsInformation();
                    updateUIHandler2.sendEmptyMessage(0);

                } catch (Exception e) {
                    Log.e("snsActivity", "exception :  "+e);

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

                Log.e("snsActivity","update ui in activity");

                for(int i=0;i<snsInfosArray.length;i++)
                {
                    Log.e("snsActivity",i+" getUserId() : "+snsInfosArray[i].getUserId());
                    Log.e("snsActivity",i+" getSnsMessage() : "+snsInfosArray[i].getSnsMessage());
                }
            }
        };
    }


}
