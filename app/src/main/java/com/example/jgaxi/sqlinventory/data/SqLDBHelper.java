package com.example.jgaxi.sqlinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.jgaxi.sqlinventory.MainActivity;

public class SqLDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "universityDB.db";
    private static final int DB_VERSION = 1;

    public SqLDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

    }

    //SQL commands to create table
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createTable =  "CREATE TABLE " + SqLContract.SqlEntry.TABLE_NAME + " ("
                + SqLContract.SqlEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SqLContract.SqlEntry.COURSE_NAME_COLUMN + " TEXT NOT NULL, "
                + SqLContract.SqlEntry.COURSE_PRICE_COLUMN + " TEXT NOT NULL, "
                + SqLContract.SqlEntry.COURSE_QUANTITY_COLUMN + " INTERGER NOT NULL DEFAULT 0, "
                + SqLContract.SqlEntry.COURSE_SUPPLIER_COLUMN + " TEXT NOT NULL, "
                + SqLContract.SqlEntry.COURSE_PHONENUM_COLUMN + " INTEGER NOT NULL);";

        sqLiteDatabase.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {
        // Update here
    }

}
