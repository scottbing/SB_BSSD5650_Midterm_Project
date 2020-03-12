/*
Scott Bing
scottbing@cnm.edu
SplashActivity.java
*/

package com.cis2237.bingp4;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;



import java.util.Timer;
import java.util.TimerTask;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // instantiate a timer
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                finish();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        };

        // schedule a timer
        Timer opening = new Timer();
        opening.schedule(task, 7000);

    }

    // Activity Cycle Methods
    private void releaseSplashActivity() {
        Log.i("SplashActivity", String.valueOf(R.string.log_release_resources));

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("SplashActivity", String.valueOf(R.string.log_onPause));
        releaseSplashActivity();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("SplashActivity", String.valueOf(R.string.log_onRestart));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("SplashActivity", String.valueOf(R.string.log_onResume));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseSplashActivity();
        Log.i("SplashActivity", String.valueOf(R.string.log_onDestroy));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("SplashActivity", String.valueOf(R.string.log_onDestroy));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i("SplashActivity", String.valueOf(R.string.log_onRestoreInstanceState));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("SplashActivity", String.valueOf(R.string.log_onSaveInstanceState));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.i("RedeemRewards", String.valueOf(R.string.log_onCreateOptionsMenu));
        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
