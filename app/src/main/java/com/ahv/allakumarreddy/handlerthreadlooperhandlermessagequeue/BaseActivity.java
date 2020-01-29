package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.FragmentActivityLifecycle.FragmentAndActivityLifeCycle;
import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.aidl.AIDLActivity;
import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.boundServiceUsingBinder.BoundServiceActivity;
import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.foregroundService.ForegroundServiceActivity;
import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.intentService.IntentServiceActivity;
import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.normalBackgroundService.NormalBackgroundServiceActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        ListView listView = findViewById(R.id.lv);
        String[] arr = new String[]{
                "HandlerLooperMessageQueue",
                "NormalBackgroundService",
                "IntentService",
                "ForegroundService",
                "BoundService Using Binder (Local Service)",
                "BoundServiceActivity Using Messenger (Remote Service)",
                "AIDL",
                "FragmentActivity LifeCycle"
        };
        final Class<BaseActivity> cla[] = new Class[]{
                MainActivity.class,
                NormalBackgroundServiceActivity.class,
                IntentServiceActivity.class,
                ForegroundServiceActivity.class,
                BoundServiceActivity.class,
                com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.boundServiceUsingMessenger.BoundServiceActivity.class,
                AIDLActivity.class,
                FragmentAndActivityLifeCycle.class
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arr);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(BaseActivity.this, cla[position]));
            }
        });
    }
}
