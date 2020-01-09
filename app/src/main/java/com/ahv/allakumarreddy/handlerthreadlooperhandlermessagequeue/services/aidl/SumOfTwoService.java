package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.ISumOfTwoAidlInterface;

public class SumOfTwoService extends Service {
    private static final String TAG = SumOfTwoService.class.getSimpleName();
    private final ISumOfTwoAidlInterface.Stub mBinder = new ISumOfTwoAidlInterface.Stub() {
        @Override
        public int sum(int a, int b) throws RemoteException {
            int res = sumOfTwo(a, b);
            return res;
        }
    };

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

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "oncreate");
    }

    public SumOfTwoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "ondestroy");
        super.onDestroy();
    }
}
