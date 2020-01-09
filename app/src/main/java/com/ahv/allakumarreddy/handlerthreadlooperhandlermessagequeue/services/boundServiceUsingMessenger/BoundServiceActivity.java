package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.boundServiceUsingMessenger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.R;

public class BoundServiceActivity extends AppCompatActivity {

    private static final String TAG = BoundServiceActivity.class.getSimpleName();
    private Messenger mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bound_service2);
    }

    public void start(View view) {
        Log.d(TAG, "bind service from activity");
        Intent intent = new Intent(this, BoundService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void stop(View view) {
        Log.d(TAG, "unbind service from activity");
        unbindService(mServiceConnection);
    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            mService = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
        }
    };

    public void sum(View view) {
        Message msg = Message.obtain(null, BoundService.SUM_OF_TWO, 10, 5);
        msg.replyTo = new Messenger(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, "sum : " + msg.arg1);
            }
        });
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            Log.d(TAG, e.getMessage());
        }
    }

}
