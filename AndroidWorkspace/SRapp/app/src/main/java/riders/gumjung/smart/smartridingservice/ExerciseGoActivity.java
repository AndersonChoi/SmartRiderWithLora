package riders.gumjung.smart.smartridingservice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;

/**
 * Created by HackerAnderson on 2016. 7. 1..
 */

public class ExerciseGoActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale.setDefault(new Locale ("en", "US"));
        setContentView(R.layout.activity_exercise_go);



    }
}
