# Android At a Glance

A glance for all necessary topics in ANDROID.

## Topics

* [Services](#services) - Services
* [Activity & Fragment LifeCycle](#Activity & Fragment LifeCycle) - Activity & Fragment LifeCycle

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
        *   [Remote Service Using Messenger](#BoundServiceMessenger) - Sequential among applications access, Isolated process
        *   [AIDL](#AIDL) - Multithreading among applications access, isolated process

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

2019-09-30 18:28:26.447 D/ForegroundService: constructor
2019-09-30 18:28:26.449 D/ForegroundService: oncreate
2019-09-30 18:28:26.451 D/ForegroundService: onStartCommand
2019-09-30 18:28:26.472 D/ForegroundService: count : 0
2019-09-30 18:28:27.473 D/ForegroundService: count : 1
2019-09-30 18:28:28.474 D/ForegroundService: count : 2
2019-09-30 18:28:29.475 D/ForegroundService: count : 3
2019-09-30 18:28:30.478 D/ForegroundService: count : 4
2019-09-30 18:28:31.482 D/ForegroundService: count : 5
2019-09-30 18:28:32.484 D/ForegroundService: count : 6
2019-09-30 18:28:33.488 D/ForegroundService: count : 7
2019-09-30 18:28:34.489 D/ForegroundService: count : 8
2019-09-30 18:28:35.490 D/ForegroundService: count : 9
2019-09-30 18:28:49.543 D/ForegroundService: ondestroy

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

2019-10-01 17:14:30.139 D/NormalBackgroundService: constructor
2019-10-01 17:14:30.140 D/NormalBackgroundService: onCreate
2019-10-01 17:14:30.142 D/NormalBackgroundService: onStartCommand
2019-10-01 17:14:30.142 D/NormalBackgroundService: count : 0
2019-10-01 17:14:31.143 D/NormalBackgroundService: count : 1
2019-10-01 17:14:32.144 D/NormalBackgroundService: count : 2
2019-10-01 17:14:33.145 D/NormalBackgroundService: count : 3
2019-10-01 17:14:34.146 D/NormalBackgroundService: count : 4
2019-10-01 17:14:35.147 D/NormalBackgroundService: count : 5
2019-10-01 17:14:36.149 D/NormalBackgroundService: count : 6
2019-10-01 17:14:37.150 D/NormalBackgroundService: count : 7
2019-10-01 17:14:38.151 D/NormalBackgroundService: count : 8
2019-10-01 17:14:39.152 D/NormalBackgroundService: count : 9
2019-10-01 17:14:40.162 D/NormalBackgroundService: onStartCommand
2019-10-01 17:14:40.162 D/NormalBackgroundService: count : 0
2019-10-01 17:14:41.163 D/NormalBackgroundService: count : 1
2019-10-01 17:14:42.164 D/NormalBackgroundService: count : 2
2019-10-01 17:14:43.165 D/NormalBackgroundService: count : 3
2019-10-01 17:14:44.166 D/NormalBackgroundService: count : 4
2019-10-01 17:14:45.167 D/NormalBackgroundService: count : 5
2019-10-01 17:14:46.169 D/NormalBackgroundService: count : 6
2019-10-01 17:14:47.169 D/NormalBackgroundService: count : 7
2019-10-01 17:14:48.170 D/NormalBackgroundService: count : 8
2019-10-01 17:14:49.171 D/NormalBackgroundService: count : 9
2019-10-01 17:14:52.090 D/NormalBackgroundService: onDestroy

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

2019-10-01 18:02:32.626 D/BackgroundIntentService: constructor
2019-10-01 18:02:32.629 D/BackgroundIntentService: oncreate
2019-10-01 18:02:32.631 D/BackgroundIntentService: onStartCommand
2019-10-01 18:02:32.631 D/BackgroundIntentService: onHandleIntent
2019-10-01 18:02:32.631 D/BackgroundIntentService: count : 0
2019-10-01 18:02:32.634 D/BackgroundIntentService: onStartCommand
2019-10-01 18:02:33.632 D/BackgroundIntentService: count : 1
2019-10-01 18:02:34.633 D/BackgroundIntentService: count : 2
2019-10-01 18:02:35.633 D/BackgroundIntentService: count : 3
2019-10-01 18:02:36.634 D/BackgroundIntentService: count : 4
2019-10-01 18:02:37.635 D/BackgroundIntentService: count : 5
2019-10-01 18:02:38.636 D/BackgroundIntentService: count : 6
2019-10-01 18:02:39.636 D/BackgroundIntentService: count : 7
2019-10-01 18:02:40.637 D/BackgroundIntentService: count : 8
2019-10-01 18:02:41.638 D/BackgroundIntentService: count : 9
2019-10-01 18:02:42.642 D/BackgroundIntentService: onHandleIntent
2019-10-01 18:02:42.643 D/BackgroundIntentService: count : 0
2019-10-01 18:02:43.643 D/BackgroundIntentService: count : 1
2019-10-01 18:02:44.646 D/BackgroundIntentService: count : 2
2019-10-01 18:02:45.649 D/BackgroundIntentService: count : 3
2019-10-01 18:02:46.650 D/BackgroundIntentService: count : 4
2019-10-01 18:02:47.652 D/BackgroundIntentService: count : 5
2019-10-01 18:02:48.653 D/BackgroundIntentService: count : 6
2019-10-01 18:02:49.654 D/BackgroundIntentService: count : 7
2019-10-01 18:02:50.655 D/BackgroundIntentService: count : 8
2019-10-01 18:02:51.656 D/BackgroundIntentService: count : 9
2019-10-01 18:02:52.662 D/BackgroundIntentService: onDestroy

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

2019-09-11 02:33:17.749 D/BoundService: constructor
2019-09-11 02:33:17.749 D/BoundService: onCreate
2019-09-11 02:33:17.749 D/BoundService: onBind
2019-09-11 02:33:17.761 D/BoundServiceActivity: onServiceConnected
2019-09-11 02:33:17.761 D/BoundService: count : 0
2019-09-11 02:33:18.761 D/BoundService: count : 1
2019-09-11 02:33:19.762 D/BoundService: count : 2
2019-09-11 02:33:20.763 D/BoundService: count : 3
2019-09-11 02:33:21.764 D/BoundService: count : 4
2019-09-11 02:33:22.766 D/BoundService: count : 5
2019-09-11 02:33:23.767 D/BoundService: count : 6
2019-09-11 02:33:24.768 D/BoundService: count : 7
2019-09-11 02:33:25.770 D/BoundService: count : 8
2019-09-11 02:33:26.771 D/BoundService: count : 9
2019-09-11 02:33:27.772 D/BoundService: count : 0
2019-09-11 02:33:28.773 D/BoundService: count : 1
2019-09-11 02:33:29.774 D/BoundService: count : 2
2019-09-11 02:33:30.776 D/BoundService: count : 3
2019-09-11 02:33:31.778 D/BoundService: count : 4
2019-09-11 02:33:32.779 D/BoundService: count : 5
2019-09-11 02:33:33.781 D/BoundService: count : 6
2019-09-11 02:33:34.782 D/BoundService: count : 7
2019-09-11 02:33:35.783 D/BoundService: count : 8
2019-09-11 02:33:36.783 D/BoundService: count : 9
2019-09-11 02:33:43.789 D/BoundService: onUnBind
2019-09-11 02:33:43.789 D/BoundService: onDestroy

```

<b>Mobile Result</b>

![Bound Service](./images/boundService.gif?raw=true "Bound Service")

### BoundServiceMessenger

<b>BoundServiceActivity.java</b>
```java

package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.services.boundServiceUsingMessenger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.R;

public class BoundServiceActivity extends AppCompatActivity {

    private static final String TAG = BoundServiceActivity.class.getSimpleName();
    private Messenger mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bound_service2);
    }

    public void start(View view) {
        Log.d(TAG, "bind service from activity");
        Intent intent = new Intent(this, BoundService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void stop(View view) {
        Log.d(TAG, "unbind service from activity");
        unbindService(mServiceConnection);
    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            mService = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
        }
    };

    public void sum(View view) {
        Message msg = Message.obtain(null, BoundService.SUM_OF_TWO, 10, 5);
        msg.replyTo = new Messenger(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, "sum : " + msg.arg1);
            }
        });
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            Log.d(TAG, e.getMessage());
        }
    }

}

```

<b>BoundService.java</b>
```java

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

```

<b>activity_bound_service2.xml</b>
```xml

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="horizontal"
    tools:context=".services.boundServiceUsingMessenger.BoundServiceActivity">

    <Button
        android:text="start"
        android:onClick="start"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

    <Button
        android:text="stop"
        android:onClick="stop"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

    <Button
        android:text="sum"
        android:onClick="sum"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
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
            android:name=".services.boundServiceUsingMessenger.BoundService"
            android:enabled="true"
            android:exported="true"
            android:isolatedProcess="true" />

        <activity android:name=".services.boundServiceUsingMessenger.BoundServiceActivity"></activity>

    </application>

</manifest>

```

<b>log.txt</b>
```txt

2020-01-09 16:06:30.056 D/BoundServiceActivity: bind service from activity
2020-01-09 16:06:30.512 D/BoundServiceActivity: onServiceConnected
2020-01-09 16:06:30.505 D/BoundService: constructor
2020-01-09 16:06:30.506 D/BoundService: onCreate
2020-01-09 16:06:30.507 D/BoundService: onBind
2020-01-09 16:06:47.658 D/BoundService: count : 0
2020-01-09 16:06:48.658 D/BoundService: count : 1
2020-01-09 16:06:49.659 D/BoundService: count : 2
2020-01-09 16:06:50.660 D/BoundService: count : 3
2020-01-09 16:06:51.661 D/BoundService: count : 4
2020-01-09 16:06:52.662 D/BoundService: count : 5
2020-01-09 16:06:53.662 D/BoundService: count : 6
2020-01-09 16:06:54.663 D/BoundService: count : 7
2020-01-09 16:06:55.664 D/BoundService: count : 8
2020-01-09 16:06:56.665 D/BoundService: count : 9
2020-01-09 16:06:57.668 D/BoundServiceActivity: sum : 15
2020-01-09 16:07:10.417 D/BoundServiceActivity: unbind service from activity

```

### AIDL

<b>AIDLActivity.java</b>
```java

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
import android.view.View;
import android.widget.TextView;

import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.ISumOfTwoAidlInterface;
import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.R;

public class AIDLActivity extends AppCompatActivity {

    private static final String TAG = AIDLActivity.class.getSimpleName();
    private TextView tv;
    private ISumOfTwoAidlInterface iSumOfTwoAidlInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            // this gets an instance of the IRemoteInterface, which we can use to call on the service
            iSumOfTwoAidlInterface = ISumOfTwoAidlInterface.Stub.asInterface(service);
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "Service has unexpectedly disconnected");
            iSumOfTwoAidlInterface = null;
        }
    };

    public void start(View view) {
        Intent intent = new Intent(AIDLActivity.this, SumOfTwoService.class);
        intent.setAction(ISumOfTwoAidlInterface.class.getName());
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void stop(View view) {
        unbindService(mConnection);
    }

    public void sum(View view) {
        int sum = 0;
        try {
            sum = iSumOfTwoAidlInterface.sum(10, 2);
        } catch (RemoteException e) {
            Log.d(TAG, e.getMessage());
        }
        Log.d(TAG, "Sum:" + sum);
    }
}

```

<b>activity_aidl.xml</b>
```xml

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="horizontal"
    tools:context=".services.aidl.AIDLActivity">

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:onClick="start"
        android:text="start" />

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:onClick="sum"
        android:text="Sum" />

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:onClick="stop"
        android:text="stop" />
</LinearLayout>

```

<b>SumOfTwoService.java</b>
```java

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

```

<b>ISumOfTwoAidlInterface.aidl</b>
```java

// ISumOfTwoAidlInterface.aidl
package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue;

// Declare any non-default types here with import statements

interface ISumOfTwoAidlInterface {

    int sum(int a, int b);
}

```

<b>ISumOfTwoAidlInterface.java</b>
```java

/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\others\\HandlerthreadLooperHandlerMessagequeue\\app\\src\\main\\aidl\\com\\ahv\\allakumarreddy\\handlerthreadlooperhandlermessagequeue\\ISumOfTwoAidlInterface.aidl
 */
package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue;
// Declare any non-default types here with import statements

public interface ISumOfTwoAidlInterface extends android.os.IInterface {
    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.ISumOfTwoAidlInterface {
        private static final java.lang.String DESCRIPTOR = "com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.ISumOfTwoAidlInterface";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.ISumOfTwoAidlInterface interface,
         * generating a proxy if needed.
         */
        public static com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.ISumOfTwoAidlInterface asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.ISumOfTwoAidlInterface))) {
                return ((com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.ISumOfTwoAidlInterface) iin);
            }
            return new com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.ISumOfTwoAidlInterface.Stub.Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            java.lang.String descriptor = DESCRIPTOR;
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_sum: {
                    data.enforceInterface(descriptor);
                    int _arg0;
                    _arg0 = data.readInt();
                    int _arg1;
                    _arg1 = data.readInt();
                    int _result = this.sum(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        private static class Proxy implements com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.ISumOfTwoAidlInterface {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public int sum(int a, int b) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(a);
                    _data.writeInt(b);
                    mRemote.transact(Stub.TRANSACTION_sum, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }
        }

        static final int TRANSACTION_sum = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    }

    public int sum(int a, int b) throws android.os.RemoteException;
}

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
            android:name=".services.aidl.SumOfTwoService"
            android:enabled="true"
            android:isolatedProcess="true"
            android:exported="true" />

        <activity android:name=".services.aidl.AIDLActivity" />
    </application>

</manifest>

```

<b>log.txt</b>
```txt

2020-01-09 16:51:28.407 D/SumOfTwoService: oncreate
2020-01-09 16:51:28.407 D/SumOfTwoService: onBind
2020-01-09 16:51:30.519 D/SumOfTwoService: count : 0
2020-01-09 16:51:31.520 D/SumOfTwoService: count : 1
2020-01-09 16:51:32.521 D/SumOfTwoService: count : 2
2020-01-09 16:51:33.521 D/SumOfTwoService: count : 3
2020-01-09 16:51:34.522 D/SumOfTwoService: count : 4
2020-01-09 16:51:35.523 D/SumOfTwoService: count : 5
2020-01-09 16:51:36.524 D/SumOfTwoService: count : 6
2020-01-09 16:51:37.525 D/SumOfTwoService: count : 7
2020-01-09 16:51:38.526 D/SumOfTwoService: count : 8
2020-01-09 16:51:39.526 D/SumOfTwoService: count : 9
2020-01-09 16:51:40.529 D/AIDLActivity: Sum:12

```

## Activity & Fragment LifeCycle

![Activity & Fragment LifeCycle](./images/Activity_Fragment_LifeCycle.png?raw=true "Activity & Fragment LifeCycle")

<b>FragmentAndActivityLifeCycle.java</b>
```java

package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.FragmentActivityLifecycle;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.R;

public class FragmentAndActivityLifeCycle extends AppCompatActivity {

    private static final String TAG = FragmentAndActivityLifeCycle.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_and_life_cycle);
        Log.d(TAG, "onCreate");
        FragmentTransaction manager = getSupportFragmentManager().beginTransaction();
        manager.replace(R.id.main, new FragmentBlank());
        manager.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionMenu");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }
}


```


<b>activity_fragment_and_life_cycle.txt</b>
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:id="@+id/main"
    tools:context=".FragmentActivityLifecycle.FragmentAndActivityLifeCycle">

</FrameLayout>
```

<b>FragmentBalnk.java</b>
```java
package com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.FragmentActivityLifecycle;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ahv.allakumarreddy.handlerthreadlooperhandlermessagequeue.R;

public class FragmentBlank extends Fragment {

    private static final String TAG = FragmentBlank.class.getSimpleName();

    public FragmentBlank() {
        // Required empty public constructor
    }

    public static FragmentBlank newInstance(String param1, String param2) {
        FragmentBlank fragment = new FragmentBlank();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_fragment_blank, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPaues");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "onCreateOptionMenu");
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
        Log.d(TAG, "onDestroyOptionMenu");
    }

}

```


<b>fragment_fragment_blank.xml</b>
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".FragmentActivityLifecycle.FragmentBlank">

    <!-- TODO: Update blank fragment layout -->
    <TextView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:text="@string/hello_blank_fragment" />

</FrameLayout>
```

<b>log.txt</b>
```txt

2019-10-09 18:51:38.912 D/FragmentAndActivityLifeCycle: onCreate
2019-10-09 18:51:38.943 D/FragmentBlank: onAttach
2019-10-09 18:51:38.944 D/FragmentBlank: onCreate
2019-10-09 18:51:38.945 D/FragmentBlank: onCreateView
2019-10-09 18:51:38.957 D/FragmentBlank: onActivityCreated
2019-10-09 18:51:38.958 D/FragmentBlank: onStart
2019-10-09 18:51:38.958 D/FragmentAndActivityLifeCycle: onStart
2019-10-09 18:51:38.962 D/FragmentAndActivityLifeCycle: onResume
2019-10-09 18:51:38.964 D/FragmentBlank: onResume
2019-10-09 18:51:39.080 D/FragmentAndActivityLifeCycle: onCreateOptionMenu
2019-10-09 18:51:39.080 D/FragmentBlank: onCreateOptionMenu
2019-10-09 18:52:05.182 D/FragmentBlank: onPaues
2019-10-09 18:52:05.183 D/FragmentAndActivityLifeCycle: onPause
2019-10-09 18:52:05.244 D/FragmentBlank: onStop
2019-10-09 18:52:05.245 D/FragmentAndActivityLifeCycle: onStop

// switching between activities
2019-10-09 18:53:14.172 D/FragmentAndActivityLifeCycle: onCreate
2019-10-09 18:53:14.205 D/FragmentBlank: onAttach
2019-10-09 18:53:14.205 D/FragmentBlank: onCreate
2019-10-09 18:53:14.207 D/FragmentBlank: onCreateView
2019-10-09 18:53:14.220 D/FragmentBlank: onActivityCreated
2019-10-09 18:53:14.220 D/FragmentBlank: onStart
2019-10-09 18:53:14.221 D/FragmentAndActivityLifeCycle: onStart
2019-10-09 18:53:14.225 D/FragmentAndActivityLifeCycle: onResume
2019-10-09 18:53:14.226 D/FragmentBlank: onResume
2019-10-09 18:53:14.368 D/FragmentAndActivityLifeCycle: onCreateOptionMenu
2019-10-09 18:53:14.369 D/FragmentBlank: onCreateOptionMenu
2019-10-09 18:53:15.662 D/FragmentBlank: onPaues
2019-10-09 18:53:15.663 D/FragmentAndActivityLifeCycle: onPause
2019-10-09 18:53:15.728 D/FragmentBlank: onStop
2019-10-09 18:53:15.729 D/FragmentAndActivityLifeCycle: onStop
2019-10-09 18:53:16.605 D/FragmentAndActivityLifeCycle: onRestart
2019-10-09 18:53:16.608 D/FragmentBlank: onStart
2019-10-09 18:53:16.609 D/FragmentAndActivityLifeCycle: onStart
2019-10-09 18:53:16.630 D/FragmentAndActivityLifeCycle: onResume
2019-10-09 18:53:16.631 D/FragmentBlank: onResume
2019-10-09 18:53:17.867 D/FragmentBlank: onPaues
2019-10-09 18:53:17.868 D/FragmentAndActivityLifeCycle: onPause
2019-10-09 18:53:17.923 D/FragmentBlank: onStop
2019-10-09 18:53:17.924 D/FragmentAndActivityLifeCycle: onStop

```