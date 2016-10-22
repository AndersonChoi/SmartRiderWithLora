package riders.gumjung.smart.smartridingservice;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder ab = new AlertDialog.Builder(ExerciseGoActivity.this);
        ab.setMessage("Are you sure, you want to quit exercise?");
        ab.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        ab.setNegativeButton("NO",null);
        ab.show();
    }



}
