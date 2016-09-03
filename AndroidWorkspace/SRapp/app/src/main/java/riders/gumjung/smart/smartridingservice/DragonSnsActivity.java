package riders.gumjung.smart.smartridingservice;

import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by HackerAnderson on 2016. 7. 1..
 */

public class DragonSnsActivity extends AppCompatActivity {

    private double latitude;
    private double longitude;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dragon_sns);



    }
}
