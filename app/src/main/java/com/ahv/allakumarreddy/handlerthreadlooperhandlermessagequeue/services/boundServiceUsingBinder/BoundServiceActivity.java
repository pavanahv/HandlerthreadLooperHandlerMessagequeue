package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.boundServiceUsingBinder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.R;

public class BoundServiceActivity extends AppCompatActivity {

    private static final String TAG = "BoundServiceActivity";
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bound_service);

        mTextView = findViewById(R.id.tv);
    }

    public void start(View view) {

        Intent intent = new Intent(this, BoundService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        mTextView.setText("Service running..");
    }

    public void stop(View view) {
        Intent intent = new Intent(this, BoundService.class);
        unbindService(mServiceConnection);
        mTextView.setText("Service Stopped..");
    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            BoundService boundService = ((BoundService.LocalBinder) service).getService();
            int res = boundService.sumOfTwo(10, 2);
            mTextView.setText(res + "");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            mTextView.setText("service stopped");
        }
    };
}
