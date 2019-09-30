package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.aidl.MyHandlerThread;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView mTextView = findViewById(R.id.tv);

        final Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mTextView.setText((String) msg.obj);
            }
        };

        MyHandlerThread mWorkerThread = new MyHandlerThread("Worker Thread");
        mWorkerThread.start();
        mWorkerThread.prepareLooper();
        mWorkerThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = mHandler.obtainMessage();
                msg.obj = "execution completed 1";
                mHandler.sendMessage(msg);
            }
        }).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = mHandler.obtainMessage();
                msg.obj = "execution completed 2";
                mHandler.sendMessage(msg);
            }
        });
    }
}
