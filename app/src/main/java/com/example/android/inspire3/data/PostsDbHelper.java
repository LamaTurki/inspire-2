package com.example.android.inspire3.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lama on 9/28/2017 AD.
 */

public class PostsDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "posts.db";
    private static final int DATABASE_VERSION = 1;

    public PostsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_STATMENT = "CREATE TABLE " + PostsContract.PostEntry.TABLE_NAME + " ( " +
                PostsContract.PostEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PostsContract.PostEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                PostsContract.PostEntry.COLUMN_TAG + " TEXT NOT NULL , " +
                PostsContract.PostEntry.COLUMN_DATE + " DATE default CURRENT_DATE , " +
                PostsContract.PostEntry.COLUMN_IMAGE + " BLOB ); ";
        sqLiteDatabase.execSQL(CREATE_STATMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
