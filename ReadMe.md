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
        *	[Normal service](#NormalBackgroundService) - sequential on UI thread itself
        *	[IntentService](#IntentService) - sequential not on UI thread
    *   Bound service : client – server architecture
        *   [Local service using Binder](#BoundService) -  sequential, same in app process
        *   Using Messenger ( Sequential, isolated special process )
        *   AIDL ( Multithreading, isolated process )

### ForegroundService

<b>ForegroundServiceActivity.java</b>
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

<b>ForegroundService.java</b>
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

<b>activity_foreground_service.xml</b>
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

<b>AndroidManifest.xml</b>
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

<b>log.txt</b>
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

<b>Mobile Result</b>

![Foreground Service Start](./images/foregroundServiceStart.png?raw=true "Foreground Service Start")
![Foreground Service Started](./images/foregourdServiceStarted.png?raw=true "Foreground Service Started")
![Foreground Service Stopped](./images/foregroundServiceStopped.png?raw=true "Foreground Service Stopped")


### NormalBackgroundService

<b>NormalBackgroundServiceActivity.java</b>
```java

package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.normalBackgroundService;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.R;

public class NormalBackgroundServiceActivity extends AppCompatActivity {

    private TextView mTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_background_service);

        mTextview = findViewById(R.id.tv);
    }


    public void start(View view) {
        Intent intent = new Intent(this,NormalBackgroundService.class);
        startService(intent);
        startService(intent);
        mTextview.setText("started successfully ");
    }

    public void stop(View view) {
        Intent intent = new Intent(this,NormalBackgroundService.class);
        stopService(intent);
        mTextview.setText("stopped successfully ");
    }
}

```

<b>NormalBackgroundService.java</b>
```java


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

```

<b>activity_normal_background_service.xml</b>
```xml

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".services.normalBackgroundService.NormalBackgroundServiceActivity">

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

<b>AndroidManifest.xml</b>
```xml

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <service
            android:name=".services.normalBackgroundService.NormalBackgroundService"
            android:enabled="true"
            android:exported="true"></service>

        <activity android:name=".services.normalBackgroundService.NormalBackgroundServiceActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>

```

<b>log.txt</b>
```txt

2019-10-01 17:14:30.139 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: constructor
2019-10-01 17:14:30.140 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: onCreate
2019-10-01 17:14:30.142 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: onStartCommand
2019-10-01 17:14:30.142 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 0
2019-10-01 17:14:31.143 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 1
2019-10-01 17:14:32.144 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 2
2019-10-01 17:14:33.145 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 3
2019-10-01 17:14:34.146 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 4
2019-10-01 17:14:35.147 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 5
2019-10-01 17:14:36.149 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 6
2019-10-01 17:14:37.150 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 7
2019-10-01 17:14:38.151 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 8
2019-10-01 17:14:39.152 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 9
2019-10-01 17:14:40.162 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: onStartCommand
2019-10-01 17:14:40.162 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 0
2019-10-01 17:14:41.163 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 1
2019-10-01 17:14:42.164 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 2
2019-10-01 17:14:43.165 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 3
2019-10-01 17:14:44.166 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 4
2019-10-01 17:14:45.167 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 5
2019-10-01 17:14:46.169 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 6
2019-10-01 17:14:47.169 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 7
2019-10-01 17:14:48.170 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 8
2019-10-01 17:14:49.171 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: count : 9
2019-10-01 17:14:52.090 27686-27686/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/NormalBackgroundService: onDestroy

```

<b>Mobile Result</b>

![Normal Background Service](./images/normalBackgroundService.gif?raw=true "Normal Background Service")

### IntentService

<b>IntentServiceActivity.java</b>
```java

package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.intentService;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.R;

public class IntentServiceActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_service);

        mTextView = findViewById(R.id.tv);
    }

    public void start(View view) {
        Intent intent = new Intent(this,BackgroundIntentService.class);
        startService(intent);
        startService(intent);
        mTextView.setText("Service started successfully!");
    }

    public void stop(View view) {
        Intent intent = new Intent(this,BackgroundIntentService.class);
        stopService(intent);
        mTextView.setText("Service stopped successfully!");
    }
}

```

<b>BackgroundIntentService.java</b>
```java

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


```

<b>activity_intent_service.xml</b>
```xml

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".services.intentService.IntentServiceActivity">

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

<b>AndroidManifest.xml</b>
```xml

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue">

    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <service
            android:name=".services.intentService.BackgroundIntentService"
            android:exported="false"></service>

        <activity android:name=".services.intentService.IntentServiceActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>

```

<b>log.txt</b>
```txt

2019-10-01 18:02:32.626 30819-30819/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: constructor
2019-10-01 18:02:32.629 30819-30819/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: oncreate
2019-10-01 18:02:32.631 30819-30819/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: onStartCommand
2019-10-01 18:02:32.631 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: onHandleIntent
2019-10-01 18:02:32.631 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 0
2019-10-01 18:02:32.634 30819-30819/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: onStartCommand
2019-10-01 18:02:33.632 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 1
2019-10-01 18:02:34.633 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 2
2019-10-01 18:02:35.633 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 3
2019-10-01 18:02:36.634 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 4
2019-10-01 18:02:37.635 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 5
2019-10-01 18:02:38.636 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 6
2019-10-01 18:02:39.636 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 7
2019-10-01 18:02:40.637 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 8
2019-10-01 18:02:41.638 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 9
2019-10-01 18:02:42.642 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: onHandleIntent
2019-10-01 18:02:42.643 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 0
2019-10-01 18:02:43.643 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 1
2019-10-01 18:02:44.646 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 2
2019-10-01 18:02:45.649 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 3
2019-10-01 18:02:46.650 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 4
2019-10-01 18:02:47.652 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 5
2019-10-01 18:02:48.653 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 6
2019-10-01 18:02:49.654 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 7
2019-10-01 18:02:50.655 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 8
2019-10-01 18:02:51.656 30819-31021/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: count : 9
2019-10-01 18:02:52.662 30819-30819/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BackgroundIntentService: onDestroy

```

<b>Mobile Result</b>

![Background Intent Service](./images/backgroundIntentService.gif?raw=true "Background Intent Service")

### BoundService

<b>BoundServiceActivity.java</b>
```java

package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.boundServiceUsingBinder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.R;

public class BoundServiceActivity extends AppCompatActivity {

    private static final String TAG = "BoundServiceActivity";
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bound_service);

        mTextView = findViewById(R.id.tv);
    }

    public void start(View view) {

        Intent intent = new Intent(this, BoundService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        mTextView.setText("Service running..");
    }

    public void stop(View view) {
        Intent intent = new Intent(this, BoundService.class);
        unbindService(mServiceConnection);
        mTextView.setText("Service Stopped..");
    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            BoundService boundService = ((BoundService.LocalBinder) service).getService();
            int res = boundService.sumOfTwo(10, 2);
            res = boundService.sumOfTwo(10, 2);
            mTextView.setText(res + "");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            mTextView.setText("service stopped");
        }
    };
}


```

<b>BoundService.java</b>
```java

package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.boundServiceUsingBinder;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BoundService extends Service {
    private static final String TAG = "BoundService";
    private IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        BoundService getService() {
            return BoundService.this;
        }
    }

    public int sumOfTwo(int a, int b) {
        for (int i=0;i<10;i++) {
            try {
                Log.d(TAG,"count : "+i);
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
        return mBinder;
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


```

<b>activity_bound_service.xml</b>
```xml

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".services.boundServiceUsingBinder.BoundServiceActivity">

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

<b>AndroidManifest.xml</b>
```xml

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <service
            android:name=".services.boundServiceUsingBinder.BoundService"
            android:enabled="true"
            android:exported="true"></service>

        <activity android:name=".services.boundServiceUsingBinder.BoundServiceActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>

```

<b>log.txt</b>
```txt

2019-09-11 02:33:17.749 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: constructor
2019-09-11 02:33:17.749 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: onCreate
2019-09-11 02:33:17.749 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: onBind
2019-09-11 02:33:17.761 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundServiceActivity: onServiceConnected
2019-09-11 02:33:17.761 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 0
2019-09-11 02:33:18.761 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 1
2019-09-11 02:33:19.762 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 2
2019-09-11 02:33:20.763 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 3
2019-09-11 02:33:21.764 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 4
2019-09-11 02:33:22.766 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 5
2019-09-11 02:33:23.767 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 6
2019-09-11 02:33:24.768 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 7
2019-09-11 02:33:25.770 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 8
2019-09-11 02:33:26.771 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 9
2019-09-11 02:33:27.772 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 0
2019-09-11 02:33:28.773 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 1
2019-09-11 02:33:29.774 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 2
2019-09-11 02:33:30.776 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 3
2019-09-11 02:33:31.778 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 4
2019-09-11 02:33:32.779 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 5
2019-09-11 02:33:33.781 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 6
2019-09-11 02:33:34.782 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 7
2019-09-11 02:33:35.783 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 8
2019-09-11 02:33:36.783 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: count : 9
2019-09-11 02:33:43.789 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: onUnBind
2019-09-11 02:33:43.789 27350-27350/com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue D/BoundService: onDestroy

```

<b>Mobile Result</b>

![Bound Service](./images/boundService.gif?raw=true "Bound Service")