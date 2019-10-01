# Android At a Glance

A glance for all necessary topics in ANDROID.

## Topics

* [Services](#services) - Services

## Services

![Services XML Attributes](./images/services.png?raw=true "Services XML Attributes in Manifest")

*	Normally service will run on application process that to in main thread. So if we want to create a service in new separate process:
    *   We can use <b>isolatedProcess</b> = true : separate special process will be created
    *   We can give name in <b>process</b> attribute as : if name starts with “.” Then service will be created in separate process and it can be accessible with in application or if name starts with small letter then service is created in separate process and that will be global
*	Service can be accessible for other applications along with current application components. That is via giving <b>exported</b> attribute true. Default value will depends upon using <b>intent-filter</b> in service tag in manifest file. If intent-filter is used so that it can be called from other applications also via implicit intents.
*	By default service can be instantiated, if we want to remove it we can use <b>enabled</b> attribute as false.
*	If we want our service to run before unlocking device we can use <b>directBootAware</b>.
*	There are three types of services:
    *   [Foreground Service](#ForegroundService) - should show notification
    *   Background service : runs in background
        *	Normal service ( sequential on UI thread itself )
        *	IntentService ( Multithreading )
    *   Bound service : client – server architecture
        *   Local service using Binder ( sequential, same in app process )
        *   Using Messenger ( Sequential, isolated special process )
        *   AIDL ( Multithreading, isolated process )

### ForegroundService

<u><b>ForegroundServiceActivity.java</b></u>
```java

package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.foregroundService;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.R;

public class ForegroundServiceActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreground_service);
        mTextView = findViewById(R.id.tv);
    }

    public void start(View view) {

        Intent intent = new Intent(this, ForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
            mTextView.setText("Service running..");
        }
    }

    public void stop(View view) {
        Intent intent = new Intent(this, ForegroundService.class);
        stopService(intent);
        mTextView.setText("Service Stopped..");
    }
}


```

<u><b>ForegroundService.java</b></u>
```java

package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.foregroundService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.MainActivity;
import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.R;

public class ForegroundService extends Service {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final String TAG = "ForegroundService";

    public ForegroundService() {
        Log.d(TAG, "constructor");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "oncreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("Content Text")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    Log.d(TAG, "count : " + i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ondestroy");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}

```

<u><b>activity_foreground_service.xml</b></u>
```xml

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".services.foregroundService.ForegroundServiceActivity">

    <TextView
        android:id="@+id/tv"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:lines="5"
        android:text="" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="horizontal">

        <Button
            android:id="@+id/start"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:onClick="start"
            android:text="Start" />

        <Button
            android:id="@+id/stop"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:onClick="stop"
            android:text="Stop" />
    </LinearLayout>
</LinearLayout>

```

<u><b>AndroidManifest.xml</b></u>
```xml

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue">

    <uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <service
            android:name=".services.foregroundService.ForegroundService"
            android:enabled="true"
            android:exported="true"></service>

        <activity android:name=".services.foregroundService.ForegroundServiceActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>

```

<u><b>log.txt</b></u>
```txt

2019-09-30 18:28:26.447 31920-31920/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/ForegroundService: constructor
2019-09-30 18:28:26.449 31920-31920/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/ForegroundService: oncreate
2019-09-30 18:28:26.451 31920-31920/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/ForegroundService: onStartCommand
2019-09-30 18:28:26.472 31920-31979/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/ForegroundService: count : 0
2019-09-30 18:28:27.473 31920-31979/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/ForegroundService: count : 1
2019-09-30 18:28:28.474 31920-31979/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/ForegroundService: count : 2
2019-09-30 18:28:29.475 31920-31979/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/ForegroundService: count : 3
2019-09-30 18:28:30.478 31920-31979/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/ForegroundService: count : 4
2019-09-30 18:28:31.482 31920-31979/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/ForegroundService: count : 5
2019-09-30 18:28:32.484 31920-31979/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/ForegroundService: count : 6
2019-09-30 18:28:33.488 31920-31979/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/ForegroundService: count : 7
2019-09-30 18:28:34.489 31920-31979/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/ForegroundService: count : 8
2019-09-30 18:28:35.490 31920-31979/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/ForegroundService: count : 9
2019-09-30 18:28:49.543 31920-31920/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/ForegroundService: ondestroy

```

<u><b>Mobile Result</b></u>
![Foreground Service Start](./images/foregroundServiceStart.png?raw=true "Foreground Service Start")
![Foreground Service Started](./images/foregourdServiceStarted.png?raw=true "Foreground Service Started")
![Foreground Service Stopped](./images/foregroundServiceStopped.png?raw=true "Foreground Service Stopped")
