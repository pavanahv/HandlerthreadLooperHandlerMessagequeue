package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.aidl;

import android.os.Handler;
import android.os.HandlerThread;

public class MyHandlerThread extends HandlerThread {
    private Handler mHandler;

    public MyHandlerThread(String name) {
        super(name);
    }

    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler(getLooper());
    }

    public void prepareLooper(){
        mHandler = new Handler(getLooper());
    }

    public MyHandlerThread execute(Runnable task){
        mHandler.post(task);
        return this;
    }
}
