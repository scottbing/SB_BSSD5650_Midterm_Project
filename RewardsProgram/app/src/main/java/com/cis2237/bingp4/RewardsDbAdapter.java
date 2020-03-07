package com.cis2237.bingp4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class RewardsDbAdapter {

    // set up for Singleton Pattern
    private static RewardsDbAdapter INSTANCE = null;

    // Columns
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_AIRLINE = "airline";
    public static final String COL_STATUS = "status";
    public static final String COL_MILES = "miles";

    // Column Indexes
    public static final int INDEX_ID = 0;
    public static final int INDEX_NAME = INDEX_ID + 1;
    public static final int INDEX_AIRLINE = INDEX_ID + 2;
    public static final int INDEX_STATUS = INDEX_ID + 3;
    public static final int INDEX_MILES = INDEX_ID + 4;

    // Logging parameters
    private static final String TAG = "RewardsDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static final String DATABASE_NAME = "flyerdb";
    private static final String TABLE_NAME = "tbl_customer";
    private static final int DATABASE_VERSION = 1;
    private static Context mCtx = null;

    // SQLLite stuff
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                    COL_ID + " INTEGER PRIMARY KEY autoincrement, " +
                    COL_NAME + " TEXT, " +
                    COL_AIRLINE + " TEXT, " +
                    COL_STATUS + " TEXT, " +
                    COL_MILES + " INTEGER )";


    // Constructor
    public RewardsDbAdapter(Context ctx) {
        this.mCtx = ctx;
        int i = 0;
    }

    /*public Context getmCtx() {
        return mCtx;
    }*/

    // static method to create instance of Singleton class
    public static RewardsDbAdapter getInstance()
    {
        if (INSTANCE == null)
            INSTANCE = new RewardsDbAdapter(mCtx);
        int I=0;
        return INSTANCE;
    }

    // Open the database
    public void open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        int i=0;
        mDb = mDbHelper.getWritableDatabase();
    }

    // Close the database
    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }


    // Make a new customer
    public void createCustomer(String name, String airline, String status, int miles ) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_AIRLINE, airline);
        values.put(COL_STATUS, status);
        values.put(COL_MILES, miles);
        mDb.insert(TABLE_NAME, null, values);
}

    //  Same as above but includes customer
    public long createCustomer(Customer customer) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, customer.getName()); // Contact Name
        values.put(COL_AIRLINE, customer.getAirline());
        values.put(COL_STATUS, customer.getStatus());
        values.put(COL_MILES, customer.getMiles());

        // Inserting Row
        return mDb.insert(TABLE_NAME, null, values);
    }

    //  Fetch a single customer by id
    public Customer fetchCustomerById(int id) {

        Cursor cursor = mDb.query(TABLE_NAME, new String[]{COL_ID,
                        COL_NAME, COL_AIRLINE, COL_STATUS ,COL_MILES}, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null
        );
        if (cursor != null)
            cursor.moveToFirst();
        return new Customer(
                cursor.getInt(INDEX_ID),
                cursor.getString(INDEX_NAME),
                cursor.getString(INDEX_AIRLINE),
                cursor.getString(INDEX_STATUS),
                cursor.getInt(INDEX_MILES)
        );

    }

    //  Fetch a single customerby name
    public Customer fetchCustomerByName(String name) {

        Cursor cursor = mDb.query(TABLE_NAME, new String[]{COL_NAME,
                        COL_NAME, COL_AIRLINE, COL_STATUS, COL_MILES}, COL_NAME + "=?",
                new String[]{String.valueOf(name)}, null, null, null, null
        );
        if (cursor.getCount() != 0)
            cursor.moveToFirst();
        return new Customer(
                cursor.getInt(INDEX_ID),
                cursor.getString(INDEX_NAME),
                cursor.getString(INDEX_AIRLINE),
                cursor.getString(INDEX_STATUS),
                cursor.getInt(INDEX_MILES)
        );

    }



    // Get all customers
    public Cursor fetchAllRewards() {
        Cursor mCursor = mDb.query(TABLE_NAME, new String[]{COL_ID,
                        COL_NAME, COL_AIRLINE, COL_STATUS, COL_MILES },
                null, null, null, null, null
        );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //  Edit a customer by name
    public void updateCustomerByName(Customer customer) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, customer.getName());
        values.put(COL_AIRLINE, customer.getAirline());
        values.put(COL_STATUS, customer.getStatus());
        values.put(COL_MILES, customer.getMiles());
        int result = mDb.update(TABLE_NAME, values,
                COL_NAME + "=?", new String[]{String.valueOf(customer.getName())});
        int i =0;
    }

    //  Edit a customer
    public void updateCustomer(Customer customer) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, customer.getName());
        values.put(COL_AIRLINE, customer.getAirline());
        values.put(COL_STATUS, customer.getStatus());
        values.put(COL_MILES, customer.getMiles());
        int result = mDb.update(TABLE_NAME, values,
                COL_ID + "=?", new String[]{String.valueOf(customer.getId())});
        int i =0;
    }
    // Remove a reward
    public void deleteCustomerById(int nId) {
        mDb.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(nId)});
    }
    public void deleteAllCustomers() {
        mDb.delete(TABLE_NAME, null, null);
    }

    // SQLLITE inner class database helper
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

}