package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.intentService;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.R;

public class IntentServiceActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_service);

        mTextView = findViewById(R.id.tv);
    }

    public void start(View view) {
        Intent intent = new Intent(this,BackgroundIntentService.class);
        startService(intent);
        startService(intent);
        mTextView.setText("Service started successfully!");
    }

    public void stop(View view) {
        Intent intent = new Intent(this,BackgroundIntentService.class);
        stopService(intent);
        mTextView.setText("Service stopped successfully!");
    }
}
