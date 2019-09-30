package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.ISumOfTwoAidlInterface;

public class SumOfTwoService extends Service {
    private final ISumOfTwoAidlInterface.Stub mBinder = new ISumOfTwoAidlInterface.Stub() {
        @Override
        public int sum(int a, int b) throws RemoteException {
            return a + b;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("pavan", "oncreate");
    }

    public SumOfTwoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Log.d("pavan", "ondestroy");
        super.onDestroy();
    }
}
