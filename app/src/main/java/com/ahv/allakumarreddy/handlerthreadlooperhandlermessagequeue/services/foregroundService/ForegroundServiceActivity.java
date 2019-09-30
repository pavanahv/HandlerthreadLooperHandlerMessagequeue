package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.foregroundService;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.R;

public class ForegroundServiceActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreground_service);
        mTextView = findViewById(R.id.tv);
    }

    public void start(View view) {

        Intent intent = new Intent(this, ForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
            mTextView.setText("Service running..");
        }
    }

    public void stop(View view) {
        Intent intent = new Intent(this, ForegroundService.class);
        stopService(intent);
        mTextView.setText("Service Stopped..");
    }
}
