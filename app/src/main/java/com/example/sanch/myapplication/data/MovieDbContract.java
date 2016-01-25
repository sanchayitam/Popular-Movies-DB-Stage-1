/*
This file contains Contract class which specifies the layout of the database scheme
 */
package com.example.sanch.myapplication.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/** A contract class is a container for constants that define names for URIs, tables, and columns.
 *  The contract class allows you to use the same constants across all the other classes in the same package.
 *  MovieDbContract class defines table and column names for the movie database.
 */
public class MovieDbContract {
    public static final String CONTENT_AUTHORITY = "com.example.sanch.myapplication";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "favorite_movies";

    public MovieDbContract() {
    }

    /* Inner class that defines the table contents i.e. columns of the movie table */
    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_MOVIES = "favorite_movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster_image";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_REL_DATE = "date";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_MOVIES).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIES;

        // for building URIs on insertion of data
        public static Uri buildMoviesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
