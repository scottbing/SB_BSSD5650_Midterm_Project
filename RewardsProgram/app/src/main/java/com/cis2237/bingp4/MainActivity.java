/*
Scott Bing
scottbing@cnm.edu
MainActivity.java
*/

package com.cis2237.bingp4;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    public static final String PREF_NAME = "myPreferences";
    public static final String NAME = "myName";
    public static final String AIRLINE = "myAirine";
    public static final String STATUS = "myStatus";
    public static final String MILES = "myMilesFlown";

    private int miles = 0;
    private String milesFlown;
    String airlinePlan, currentStatus;
    private String name = "None";

    private Button btnFindStatus, btnRestart;
    private EditText etName, etAirline, etMilesFlown;
    private TextView txtInfo;
    private RewardsDbAdapter mDbAdapter;
    private Customer customer;

    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // instantiate the objects
        etName = (EditText) findViewById(R.id.etName);
        etMilesFlown = (EditText) findViewById(R.id.etMilesFlown);
        etAirline = (EditText) findViewById(R.id.etAirline);
        btnFindStatus = (Button) findViewById(R.id.btnFindStatus);
        btnRestart = (Button) findViewById(R.id.btnRestart);
        txtInfo = (TextView) findViewById(R.id.txtInfo);


        sharedPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Handle Find Status Button
        btnFindStatus.setOnClickListener(new View.OnClickListener() {

            // get input data
            @Override
            public void onClick(View v) {

                // get and check name
                getName(savedInstanceState);
            }

        });

        // I added this method for testing and debugging purposes
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.clear();
                editor.commit();
            }
        });

    }

    // Activity Cycle Methods
    private void releaseRewardsProgram() {
        Log.i("RewardsProgram", String.valueOf(R.string.log_release_resources));

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("RewardsProgram", String.valueOf(R.string.log_onPause));
        releaseRewardsProgram();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("RewardsProgram", String.valueOf(R.string.log_onRestart));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("RewardsProgram", String.valueOf(R.string.log_onResume));
        clearPreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseRewardsProgram();
        Log.i("RewardsProgram", String.valueOf(R.string.log_onDestroy));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("RewardsProgram", String.valueOf(R.string.log_onDestroy));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i("RewardsProgram", String.valueOf(R.string.log_onRestoreInstanceState));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("RewardsProgram", String.valueOf(R.string.log_onSaveInstanceState));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.i("RedeemRewards", String.valueOf(R.string.log_onCreateOptionsMenu));
        getMenuInflater().inflate(R.menu.main, menu);
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

                startActivity(new Intent(MainActivity.this, RulesActivity.class));
                return true;
        }
        return false;
    }

    // clear preferences
    public void clearPreferences() {
        etName.setText("");
        etMilesFlown.setText("");
        txtInfo.setText("");
    }

    // get and process the name field
    public void getName(Bundle savedInstanceState) {

        // open the database;
        mDbAdapter = new RewardsDbAdapter(this);
        mDbAdapter = RewardsDbAdapter.getInstance();
        mDbAdapter.open();
        if (savedInstanceState == null) {

            initializeDatabase();

            }


        // set up local variables
        name = etName.getText().toString();
        int num = 0;
        String nameMsg = "", milesMsg ="";

        // get name and check that name is not empty
        try {
            if  (name.isEmpty()) {
                // if entry is made then throw error
                throw new IllegalArgumentException("Name is Empty - Enter a Name.");
            } else {

                // name is okay, get miles flown and check that it is valid
                getAirline();
            }

        }
        catch (IllegalArgumentException e){ // catch the error
            txtInfo.setText(e.getMessage());
        }
    }

    // get and process the airline field
    public void getAirline() {

        // set up local variables
        airlinePlan = etAirline.getText().toString();

        // get airline and check that airline is not empty
        try {
            if  (airlinePlan.isEmpty()) {
                // if entry is made then throw error
                throw new IllegalArgumentException("Airline is Empty - Enter a Airline.");
            } else {

                // name is okay, get miles flown and check that it is valid
                getMilesFlown();
            }
        }
        catch (IllegalArgumentException e){ // catch the error
            txtInfo.setText(e.getMessage());
        }
    }

    // get an process miles flown field
    public void getMilesFlown() {


        // get miles flown
        milesFlown = etMilesFlown.getText().toString();

        // accumulate miles
        try {
            miles = Integer.parseInt(milesFlown);

            // insert customer into the database
            createCustomer();

            // debug
            // Toast.makeText(getApplicationContext(), "Get Miles Flown " + sharedPrefs.getInt(MILES, 0), Toast.LENGTH_SHORT).show();

            // display results in StatusActivity
            Intent intent = new Intent(this, StatusActivity.class);

            intent.putExtra("USER_NAME", name);
            //startActivity(new Intent(MainActivity.this, StatusActivity.class));
            startActivity(intent);

        } catch (final NumberFormatException e) {
            // Toast.makeText(getApplicationContext(), "Get Miles Flown " + num + " Invalid Number", Toast.LENGTH_LONG).show();
            txtInfo.setText("Miles Flown is Invalid or Empty");
        }
    }

    private void createCustomer() {
        boolean bool = mDbAdapter.customerExists(name);
        if (bool == true) {
            customer = mDbAdapter.fetchCustomerByName(name);
            customer.setMiles(customer.getMiles() + miles);    // tally the miles
            mDbAdapter.updateCustomerByName(customer);
        }
         mDbAdapter.createCustomer(name, airlinePlan, "No Rewards", miles);
    }

    private void initializeDatabase() {
        //Clear all data
        mDbAdapter.deleteAllCustomers();
        //Add some data
        insertSomeCustomers();
    }


    private void insertSomeCustomers() {
        mDbAdapter.createCustomer("Peter", "Delta", "Some Value", 2500);
        mDbAdapter.createCustomer("Alyssia", "United", "No Rewards", 6725);
        mDbAdapter.createCustomer("Joe", "Southwest", "No Rewards", 5500);
        mDbAdapter.createCustomer("Shelia", "American", "No Rewards",1500);
        mDbAdapter.createCustomer("Mark", "BritishAirways", "No Rewards",13000);
        mDbAdapter.createCustomer("Alexa", "United","Bronze Status", 60600);
        mDbAdapter.createCustomer("Ryan", "United","No Rewards", 18000);
        mDbAdapter.createCustomer("John", "Southwest", "Gold Status",106000);
        mDbAdapter.createCustomer("Jill", "American","No Rewards", 9000);
        mDbAdapter.createCustomer("Christie", "Southwest","No Rewards", 15000);
        mDbAdapter.createCustomer("Diamond", "Delta", "No Rewards",1500);
        mDbAdapter.createCustomer("Tuban", "Delta", "No Rewards",26000);
        mDbAdapter.createCustomer("Gloria", "American","No Rewards", 3500);
        mDbAdapter.createCustomer("Vickie", "Southwest", "Bronze Status", 29500);
        mDbAdapter.createCustomer("Ryan", "United","No Rewards", 6000);


    }

}
