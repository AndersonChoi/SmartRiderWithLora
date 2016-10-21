package riders.gumjung.smart.smartridingservice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import riders.gumjung.smart.smartridingservice.tracking.GetDeviceId;
import riders.gumjung.smart.smartridingservice.tracking.GetTrackingCount;
import riders.gumjung.smart.smartridingservice.tracking.GetTrackingTime;

/**
 * Created by HackerAnderson on 2016. 7. 1..
 */

public class ExerciseListActivity extends AppCompatActivity {

    private ListView  m_ListView;
    private ArrayAdapter<String> m_Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale.setDefault(new Locale ("en", "US"));
        setContentView(R.layout.activity_exercise_list);


        m_Adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
        m_ListView = (ListView) findViewById(R.id.listview);
        m_ListView.setAdapter(m_Adapter);
        m_ListView.setOnItemClickListener(onClickListItem);

        // ListView에 아이템 추가


        final ArrayList<String> items = new ArrayList<String>();
        final Handler mHandler = new Handler();
        new Thread() {
            public void run() {
                SharedPreferences pref = getSharedPreferences("LoginData", MODE_PRIVATE);
                GetTrackingCount getTrackingCount = new GetTrackingCount();
                int trackingCount = getTrackingCount.getCount(pref.getString("LoginId", ""));
                GetDeviceId getDeviceId = new GetDeviceId();
                int deviceId = getDeviceId.getId(pref.getString("LoginId", ""));
                for(int i=1;i<trackingCount+1;i++)//because tracking number start 1
                {
                    GetTrackingTime getTrackingTime = new GetTrackingTime();
                    String log = getTrackingTime.getTimeLog(deviceId,i);
                    String exerciseDate = log.substring(0,10);
                    String exerciseStartTime = log.substring(log.indexOf("T")+1,log.indexOf("|")-5);
                    items.add("No."+i+"\nDate : "+exerciseDate+"\nStart time :"+exerciseStartTime);
                    Log.e("LISTVIEW",i+"번째는 "+log);
                }
                Log.e("LISTVIEW","end get!!!");

                mHandler.post(new Runnable(){
                    @Override
                    public void run() {
                        for(int i=0;i<items.size();i++)
                            m_Adapter.add(items.get(i));
                    }
                });

            }
        }.start();



    }

    // 아이템 터치 이벤트
    private AdapterView.OnItemClickListener onClickListItem = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            Toast.makeText(getApplicationContext(), "arg2 : "+(arg2+1), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ExerciseListActivity.this, ExerciseListLogMapActivity.class);
            intent.putExtra("number",(arg2+1));
            startActivity(intent);
        }
    };


}