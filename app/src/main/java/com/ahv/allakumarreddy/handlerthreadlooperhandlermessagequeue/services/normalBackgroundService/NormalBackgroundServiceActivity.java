package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.normalBackgroundService;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.R;

public class NormalBackgroundServiceActivity extends AppCompatActivity {

    private TextView mTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_background_service);

        mTextview = findViewById(R.id.tv);
    }


    public void start(View view) {
        Intent intent = new Intent(this,NormalBackgroundService.class);
        startService(intent);
        startService(intent);
        mTextview.setText("started successfully ");
    }

    public void stop(View view) {
        Intent intent = new Intent(this,NormalBackgroundService.class);
        stopService(intent);
        mTextview.setText("stopped successfully ");
    }
}
