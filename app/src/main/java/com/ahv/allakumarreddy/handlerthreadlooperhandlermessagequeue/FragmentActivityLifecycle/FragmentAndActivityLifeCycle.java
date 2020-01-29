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
