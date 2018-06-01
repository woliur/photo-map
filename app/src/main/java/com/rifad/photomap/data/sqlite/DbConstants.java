package com.rifad.photomap.data.sqlite;

import android.provider.BaseColumns;


public class DbConstants implements BaseColumns {
    public static final String PHOTO_TABLE_NAME = "photos";
    private static final String TEXT_TYPE = " TEXT";
    private static final String TEXT_REAL = " REAL";
    private static final String COMMA_SEP = ",";
    public static final String COLUMN_NAME_NULLABLE = null;

    public static final String COLUMN_IMAGE_PATH = "image_path";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    public static final String SQL_CREATE_TABLE_PHOTOS =
            "CREATE TABLE " + PHOTO_TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_IMAGE_PATH + TEXT_TYPE + COMMA_SEP +
                    COLUMN_LATITUDE + TEXT_REAL + COMMA_SEP +
                    COLUMN_LONGITUDE + TEXT_REAL +
                    " )";

    public static final String SQL_DELETE_TABLE_PHOTOS =
            "DROP TABLE IF EXISTS " + PHOTO_TABLE_NAME;

}
