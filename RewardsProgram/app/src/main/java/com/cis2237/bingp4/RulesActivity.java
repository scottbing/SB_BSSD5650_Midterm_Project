/*
Scott Bing
scottbing@cnm.edu
RulesActivity.java
*/

package com.cis2237.bingp4;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class RulesActivity extends AppCompatActivity {

    private TextView textv;
    private InputStream iFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        // instantiate Objects
        textv = (TextView) findViewById(R.id.txtReadMe);

        iFile = getResources().openRawResource(R.raw.rewardsrules);

        try
        {
            String data = readTextFile(iFile);
            textv.setText(data);
        }
        catch (IOException e)
        {
            textv.setText("There was an error with the Rules file.");
        }
    }

    // Activity Cycle Methods
    private void releaseRulesActivity() {
        Log.i("RulesActivity", String.valueOf(R.string.log_release_resources));

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("RulesActivity", String.valueOf(R.string.log_onPause));
        releaseRulesActivity();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("RulesActivity", String.valueOf(R.string.log_onRestart));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("RulesActivity", String.valueOf(R.string.log_onResume));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseRulesActivity();
        Log.i("RulesActivity", String.valueOf(R.string.log_onDestroy));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("RulesActivity", String.valueOf(R.string.log_onDestroy));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i("RulesActivity", String.valueOf(R.string.log_onRestoreInstanceState));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("RulesActivity", String.valueOf(R.string.log_onSaveInstanceState));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.i("RedeemRewards", String.valueOf(R.string.log_onCreateOptionsMenu));
        getMenuInflater().inflate(R.menu.rules, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onOptionsItemSelected(item);
        switch(item.getItemId())
        {
            case R.id.showRules:
                startActivity(new Intent(RulesActivity.this, RulesActivity.class));
                return true;

            case R.id.action_Main:
                startActivity(new Intent(RulesActivity.this, MainActivity.class));
                return true;

            case R.id.action_Return:
                finish();
                return true;
        }
        return false;
    }

    public  String readTextFile(InputStream  iFile) throws IOException
    {
        StringBuilder  stringbuilder = new StringBuilder();
        InputStreamReader inputReader = new InputStreamReader(iFile);
        BufferedReader bufferedReader  = new BufferedReader(inputReader);
        String  line = null;

        while((line = bufferedReader.readLine()) != null)
        {
            stringbuilder.append(line );
            stringbuilder.append('\n');

        }

        bufferedReader.close();
        return stringbuilder.toString();
    }

}
