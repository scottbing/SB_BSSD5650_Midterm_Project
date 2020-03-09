/*
Scott Bing
scottbing@cnm.edu
RedeemRewardsActivity.java
*/

package com.cis2237.bingp4;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;




public class RedeemRewardsActivity extends AppCompatActivity {

    public static final String PREF_NAME = "myPreferences";
    public static final String NAME = "myName";
    public static final String MILES = "myMilesFlown";
    public static final String STATUS = "myStatus";

    // upgrade deductions
    public static final int BRONZE_UPGRADE_DEDUCTION = 15000;
    public static final int SILVER_UPGRADE_DEDUCTION = 10000;
    public static final int GOLD_UPGRADE_DEDUCTION = 5000;

    // free flight deductions
    public static final int BRONZE_FLIGHT_DEDUCTION = 25000;
    public static final int SILVER_FLIGHT_DEDUCTION = 25000;
    public static final int GOLD_FLIGHT_DEDUCTION = 75000;

    private int miles = 0;
    int remainingMiles = 0;
    private String name = "None";
    private String airline = "None";
    private String status = "No Rewards";
    private enum Reward_Status {BRONZE, SILVER, GOLD, NO};

    private RewardsDbAdapter mDbAdapter;
    private Customer customer;

    public TextView txtRemainingMiles, txtUpdatedStatus, txtMsg;
    public EditText etFlightLength;
    public Button btnUpgradeSeat, btnRedeemMileage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_rewards);

        // instantiate objects
        txtRemainingMiles = (TextView) findViewById(R.id.txtRemainingMiles);
        txtUpdatedStatus = (TextView) findViewById(R.id.txtUpdatedStatus);
        txtMsg = (TextView) findViewById(R.id.txtMsg);
        btnUpgradeSeat = (Button) findViewById(R.id.btnUpgradeSeat);
        btnRedeemMileage = (Button) findViewById(R.id.btnRedeemMileage);
        etFlightLength = (EditText) findViewById(R.id.etFlightLength);

        // Handle Upgrade Seat Button
        btnUpgradeSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // open the database
                mDbAdapter = new RewardsDbAdapter(RedeemRewardsActivity.this);
                mDbAdapter = RewardsDbAdapter.getInstance();
                mDbAdapter.open();

                // get current customer form the database
                Intent intent = getIntent();
                String user_name = intent.getStringExtra("USER_NAME");
                customer = mDbAdapter.fetchCustomerByName(user_name);

                // get the data
                name = customer.getName();
                miles = customer.getMiles();
                airline = customer.getAirline();

                status = customer.getStatus();

                // clear the message
                txtMsg.setText("");

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RedeemRewardsActivity.this);
                alertDialogBuilder.setTitle("Upgrade Seat? ");
                alertDialogBuilder.setMessage("Click Yes to use selected miles.");
                alertDialogBuilder.setPositiveButton("Yes, use selected miles.", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        // debug
                        // Toast.makeText(getApplicationContext(), "Status in RedeemRewards: " + status, Toast.LENGTH_SHORT).show();

                        // adjust String to match enum to use case structure
                        int idx = status.indexOf(" ");
                        String str = status.substring(0, idx).toUpperCase();

                        /*
                        The mileage thresholds are:
                        75,000 miles for Gold Status
                        50,000 miles for Silver Status
                        25,000 miles for Bronze status

                        REWARD STATUS                 REWARD REDEEMED               MILES DEDUCTED
                        Bronze              		Seat Upgrade                    		15,000
                        Bronze          		free flight  miles < 1000           		25,000
                        Silver              			Seat Upgrade                    	10,000
                        Silver          		free flight 1000 >= miles < 2000    		25,000
                        Gold               			 Seat Upgrade                    		 5,000
                        Gold           		 free flight 2000 >= miles < 3000   		    75,000
                         */

                        switch (Reward_Status.valueOf(status.substring(0, idx).toUpperCase())) {

                            case BRONZE:   // Bronze Status
                                miles = customer.getMiles() - BRONZE_FLIGHT_DEDUCTION;
                                break;

                            case SILVER:   // Silver Status
                                miles = customer.getMiles()  - SILVER_UPGRADE_DEDUCTION;
                                break;

                            case GOLD:   // Gold Status
                                miles = customer.getMiles()  - GOLD_UPGRADE_DEDUCTION;
                                break;

                            case NO:   // no rewards
                                txtMsg.setText("Seat upgrade not allowed - no status yet attained.");
                                miles = customer.getMiles();

                        }

                        processRemainingMiles(miles);
                    }

                    //}
                });


                alertDialogBuilder.setNegativeButton("No, do not upgrade at this time.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        // Handle Redeem Mileage Button
        btnRedeemMileage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // open the database
                mDbAdapter = new RewardsDbAdapter(RedeemRewardsActivity.this);
                mDbAdapter = RewardsDbAdapter.getInstance();
                mDbAdapter.open();

                // get current customer form the database
                Intent intent = getIntent();
                String user_name = intent.getStringExtra("USER_NAME");
                customer = mDbAdapter.fetchCustomerByName(user_name);

                // get the data
                name = customer.getName();
                miles = customer.getMiles();
                airline = customer.getAirline();
                status = customer.getStatus();

                // clear the message
                txtMsg.setText("");

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RedeemRewardsActivity.this);
                alertDialogBuilder.setTitle("Redeem Miles? ");
                alertDialogBuilder.setMessage("Click Yes to redeem miles.");
                alertDialogBuilder.setPositiveButton("Yes, to redeem miles.", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // get selected miles

                        try {
                            miles = Integer.parseInt(etFlightLength.getText().toString());
                            checkFreeFlight();
                        } catch (final NumberFormatException e) {
                            // Toast.makeText(getApplicationContext(), "Length of Flight is Empty - Invalid Number", Toast.LENGTH_LONG).show();
                            if (status == "No Status") {
                                txtMsg.setText("Length of Flight is Invalid and an Upgrade is Not Allowed - No Status attained.");
                            } else {
                                txtMsg.setText("Length of Flight entered is invalid.");
                            }
                        }
                    }

                });

                alertDialogBuilder.setNegativeButton("No, do not redeem miles at this time.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

    }

    // Activity Cycle Methods
    private void releaseRedeemRewards() {
        Log.i("RedeemRewards", String.valueOf(R.string.log_release_resources));
        txtMsg.setText("");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("RedeemRewards", String.valueOf(R.string.log_onPause));
        releaseRedeemRewards();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("RedeemRewards", String.valueOf(R.string.log_onRestart));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("RedeemRewards", String.valueOf(R.string.log_onResume));
        txtMsg.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseRedeemRewards();
        Log.i("RedeemRewards", String.valueOf(R.string.log_onDestroy));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("RedeemRewards", String.valueOf(R.string.log_onDestroy));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i("RedeemRewards", String.valueOf(R.string.log_onRestoreInstanceState));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("RedeemRewards", String.valueOf(R.string.log_onSaveInstanceState));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.i("RedeemRewards", String.valueOf(R.string.log_onCreateOptionsMenu));
        getMenuInflater().inflate(R.menu.redeem_rewards, menu);
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
                startActivity(new Intent(RedeemRewardsActivity.this, RulesActivity.class));
                return true;

            case R.id.action_Main:
                startActivity(new Intent(RedeemRewardsActivity.this, MainActivity.class));
                return true;
        }
        return false;
    }

    public void checkFreeFlight() {

        int idx = status.indexOf(" ");
        String str = status.substring(0, idx).toUpperCase();

        /*
        The mileage thresholds are:
        75,000 miles for Gold Status
        50,000 miles for Silver Status
        25,000 miles for Bronze status

        REWARD STATUS                 REWARD REDEEMED               MILES DEDUCTED
        Bronze              		Seat Upgrade                    		15,000
        Bronze          		free flight  miles < 1000           		25,000
        Silver              			Seat Upgrade                    	10,000
        Silver          		free flight 1000 >= miles < 2000    		25,000
        Gold               			 Seat Upgrade                    		 5,000
        Gold           		 free flight 2000 >= miles < 3000   		    75,000
         */

        switch (Reward_Status.valueOf(status.substring(0, idx).toUpperCase())) {

            case BRONZE:   // Bronze Status
                if (miles > 0 && miles < 1000)
                    miles = customer.getMiles() - BRONZE_FLIGHT_DEDUCTION;
                else
                    miles  = 0;
                break;

            case SILVER:    // Silver Status
                if (miles >= 1000 && miles < 2000)
                    miles = customer.getMiles() - SILVER_FLIGHT_DEDUCTION;
                else
                    miles  = 0;
                break;

            case GOLD:   // Gold Status
                if (miles >= 2000 && miles < 3000)
                    miles = customer.getMiles() - GOLD_FLIGHT_DEDUCTION;
                else
                    miles  = 0;
                break;

            case NO:   // No Rewards
                // Toast.makeText(getApplicationContext(), "Miles entered out of range for Status.", Toast.LENGTH_LONG).show();
                txtMsg.setText("Upgrade Not Allowed - No Status attained.");
                miles  = 0;
        }

        processRemainingMiles(miles);
    }

    public void processRemainingMiles(int miles) {

        // display and update remaining miles
        if (miles > 0) {

            // update current status
            reassignStatus();
            customer.setStatus(status);

            // update the updated miles in the database
            customer.setMiles(miles);
            mDbAdapter.updateCustomerByName(customer);
            txtRemainingMiles.setText(Integer.toString(miles));
            txtUpdatedStatus.setText(status);
        } else if (miles == 0) {
            txtRemainingMiles.setText(Integer.toString(customer.getMiles()));
            txtMsg.setText("Miles entered out of range for Status.");
        } else if (miles < 0) {
            txtRemainingMiles.setText(Integer.toString(customer.getMiles()));
            txtMsg.setText("Upgrade Unsuccessful - not enough miles in the bank.");
        }
    }

    public void reassignStatus() {

        // determine Rewards
        if (miles >= 25000 && miles < 50000) {  // Bronze Status
            status = "Bronze Status";
        }
        else if (miles >= 50000 && miles < 75000) {  // Silver Status
            status = "Silver Status";
        }
        else if (miles >= 75000) {  // Gold Status
            status = "Gold Status";
        }
        else {
            status = "No Rewards";
        }

        // update the updated miles in the database
        customer.setStatus(status);
        mDbAdapter.updateCustomerByName(customer);


    }
}
