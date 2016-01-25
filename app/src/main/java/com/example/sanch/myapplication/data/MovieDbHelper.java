/*
This file contains SQL Helper class which contains methods to create and maintain database and tables.
 */
package com.example.sanch.myapplication.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
        private static final int DATABASE_VERSION = 3;
        static final String DATABASE_NAME = "favorite_movies.db";

        public MovieDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieDbContract.MovieEntry.TABLE_MOVIES + " (" +
                    MovieDbContract.MovieEntry._ID + " TEXT PRIMARY KEY , " +
                    MovieDbContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT, " +
                    MovieDbContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                    MovieDbContract.MovieEntry.COLUMN_POSTER + " TEXT, " +
                    MovieDbContract.MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                    MovieDbContract.MovieEntry.COLUMN_RATING + " TEXT, " +
                    MovieDbContract.MovieEntry.COLUMN_REL_DATE + " TEXT);";

            db.execSQL(SQL_CREATE_MOVIE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + MovieDbContract.MovieEntry.TABLE_MOVIES);
            onCreate(db);
        }
}
