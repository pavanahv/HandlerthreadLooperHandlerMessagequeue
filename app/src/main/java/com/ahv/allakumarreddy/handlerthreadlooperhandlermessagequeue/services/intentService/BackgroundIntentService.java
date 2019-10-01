package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.intentService;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class BackgroundIntentService extends IntentService {

    private static final String TAG = "BackgroundIntentService";

    public BackgroundIntentService() {
        super("BackgroundIntentService");
        Log.d(TAG, "constructor");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "oncreate");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        if (intent != null) {
            for (int i = 0; i < 10; i++) {
                Log.d(TAG, "count : " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
