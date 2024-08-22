package com.terminal.gclo.Database;


import android.annotation.SuppressLint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import androidx.annotation.NonNull;

import com.terminal.gclo.Models.PersondetailModel;


import java.util.ArrayList;
import java.util.List;

public class PersonDatabaseHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "GCLO_Database.db";
    private static String TABLE_NAME = "Person_Details"; // for all the persons that ara followed by Admin

    private static String PERSON_NAME = "PERSON_NAME";
    private static String PERSON_ID = "PERSON_ID";
    private static String PERSON_GENDER = "PERSON_GENDER";
    private static String PERSON_POST = "PERSON_POST";
    private static String PERSON_USERNAME = "PERSON_USERNAME";
    private static String PERSON_EMAIL = "PERSON_EMAIL";
    private static final String LATITUDE = "LATITUDE";
    private static final String LONGITUDE = "LONGITUDE";
    private static final String ZONE_IN = "ZONE_IN";
    private static final String ZONE_OUT = "ZONE_OUT";
    private static final String DISTANCE = "DISTANCE";

    private final static int DATABASE_VERSION = 2;

    public PersonDatabaseHelper(@NonNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        "PERSON_GENDER TEXT)");
//        db.execSQL("CREATE TABLE " + TABLE_NAME + "" +
//                " (PERSON_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                "PERSON_NAME TEXT, " +
//                "PERSON_USERNAME TEXT NOT NULL UNIQUE, " +
//                "PERSON_EMAIL TEXT NOT NULL, " +
//                "PERSON_POST TEXT, " +
//                "PERSON_GENDER TEXT, " +
//                "LATITUDE TEXT, " +
//                "LONGITUDE TEXT, " +
//                "ZONE_IN TEXT, " +
//                "DISTACNE TEXT)");


        db.execSQL("CREATE TABLE " + TABLE_NAME + "" +
                " (PERSON_ID TEXT PRIMARY KEY, " +
                "PERSON_NAME TEXT, " +
                "PERSON_USERNAME TEXT NOT NULL UNIQUE, " +
                "PERSON_EMAIL TEXT NOT NULL, " +
                "PERSON_POST TEXT, " +
                "PERSON_GENDER TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

//            public boolean insertPersonData (String id, String name, String gender){
//
//                SQLiteDatabase db = this.getWritableDatabase();
//                ContentValues contentValues = new ContentValues();
//                contentValues.put(PERSON_ID, id);
//                contentValues.put(PERSON_NAME, name);
//                contentValues.put(PERSON_GENDER, gender);
//                long result = db.insert(TABLE_NAME, null, contentValues);
//                db.close();
//                return result != -1;
//            }

    public void insertPersonData(String id, String name, String username, String email, String post, String gender) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PERSON_ID, id);
        contentValues.put(PERSON_NAME, name);
        contentValues.put(PERSON_USERNAME, username);
        contentValues.put(PERSON_EMAIL, email);
        contentValues.put(PERSON_POST, post);
        contentValues.put(PERSON_GENDER, gender);

//        contentValues.put(LATITUDE, latitude);


        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        //        insert()

//        The first argument for insert() is simply the table name.
//        The second argument tells the framework what to do in the event that the ContentValues is empty
//        (i.e., you did not put any values). If you specify the name of a column, the framework inserts a
//        row and sets the value of that column to null. If you specify null, like in this code sample, the
//        framework does not insert a row when there are no values.
    }


    @SuppressLint("Range")
    public List<PersondetailModel> readPersonsData() {
        // on below line we are creating a database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorReadData = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        List<PersondetailModel> persondetailModelList = new ArrayList<>();

        // moving our cursor to first position.
//        if (cursorReadData.moveToFirst()) {
            while (cursorReadData.moveToNext()) {
//                id, name, email, latitude, longitude, zoneIn, zoneOut,distance
                // on below line we are adding the data from cursor to our array list.
//                id, name,  username,  email,  post, gender
                persondetailModelList.add(new PersondetailModel(
                        cursorReadData.getString(cursorReadData.getColumnIndex(PERSON_ID)),
                        cursorReadData.getString(cursorReadData.getColumnIndex(PERSON_NAME)),
                        cursorReadData.getString(cursorReadData.getColumnIndex(PERSON_USERNAME)),
                        cursorReadData.getString(cursorReadData.getColumnIndex(PERSON_EMAIL)),
                        cursorReadData.getString(cursorReadData.getColumnIndex(PERSON_POST)),
                        cursorReadData.getString(cursorReadData.getColumnIndex(PERSON_GENDER))
//                        cursorReadData.getString(0),
//                        cursorReadData.getString(1),
//                        cursorReadData.getString(2),
//                        cursorReadData.getString(3),
//                        cursorReadData.getString(4),
//                        cursorReadData.getString(5)

//                        cursorReadData.getString(6),
//                        cursorReadData.getString(7)
                ));

            } ;// moving our cursor to next.
//        }
        // at last closing our cursor
        // and returning our array list.
        db.close();
        cursorReadData.close();
        return persondetailModelList;
    }

    public Cursor readData12() {
        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery("Select * from " + TABLE_NAME, null);
    }

    public boolean updatePersonData(String username, String email,String post, String gender) {
        long result = 0;
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
//        contentValues.put(PERSON_NAME, name);
//        contentValues.put(PERSON_USERNAME, username);
            contentValues.put(PERSON_EMAIL, email);
            contentValues.put(PERSON_POST, post);
            contentValues.put(PERSON_GENDER, gender);
//            contentValues.put(LATITUDE, latitude);
            result = db.update(TABLE_NAME, contentValues, "PERSON_USERNAME = ?", new String[]{username});
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return result > 0;
    }


    public int deletePersonData(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "username = ?", new String[]{username});
    }

}
