package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.normalBackgroundService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class NormalBackgroundService extends Service {
    private static final String TAG = "NormalBackgroundService";

    public NormalBackgroundService() {
        Log.d(TAG, "constructor");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        for (int i = 0; i < 10; i++) {
            Log.d(TAG, "count : " + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
