package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.ISumOfTwoAidlInterface;
import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.R;

public class AIDLActivity extends AppCompatActivity {

    private static final String TAG = "pavan";
    private TextView tv;
    private ISumOfTwoAidlInterface iSumOfTwoAidlInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);

        tv = (TextView) findViewById(R.id.tv);

        Intent intent = new Intent(AIDLActivity.this, SumOfTwoService.class);
        intent.setAction(ISumOfTwoAidlInterface.class.getName());
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service
            iSumOfTwoAidlInterface = ISumOfTwoAidlInterface.Stub.asInterface(service);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        tv.setText("" + iSumOfTwoAidlInterface.sum(10, 2));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "Service has unexpectedly disconnected");
            iSumOfTwoAidlInterface = null;
        }
    };
}
