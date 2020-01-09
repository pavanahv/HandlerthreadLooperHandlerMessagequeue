package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.boundServiceUsingMessenger;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class BoundService extends Service {
    private static final String TAG = "BoundService";
    public static final int SUM_OF_TWO = 1;

    public int sumOfTwo(int a, int b) {
        for (int i = 0; i < 10; i++) {
            try {
                Log.d(TAG, "count : " + i);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return a + b;
    }


    public BoundService() {
        Log.d(TAG, "constructor");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        Messenger mMessenger = new Messenger(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SUM_OF_TWO:
                        int res = sumOfTwo(msg.arg1, msg.arg2);
                        try {
                            msg.replyTo.send(Message.obtain(null, 0, res, 0));
                        } catch (RemoteException e) {
                            Log.d(TAG, e.getMessage());
                        }
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        });
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnBind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
