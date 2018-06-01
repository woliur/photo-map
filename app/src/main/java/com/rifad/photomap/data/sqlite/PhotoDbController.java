package com.rifad.photomap.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rifad.photomap.model.Photo;

import java.util.ArrayList;


public class PhotoDbController {

    private SQLiteDatabase db;

    public PhotoDbController(Context context) {
        db = DbHelper.getInstance(context).getWritableDatabase();
    }

    public long addPhoto(Photo photo) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DbConstants.COLUMN_IMAGE_PATH, photo.getPath());
        contentValue.put(DbConstants.COLUMN_LATITUDE, photo.getLatitude());
        contentValue.put(DbConstants.COLUMN_LONGITUDE, photo.getLongitude());

        // Insert the new row, returning the primary key value of the new row
        return (int) db.insert(
                DbConstants.PHOTO_TABLE_NAME,
                DbConstants.COLUMN_NAME_NULLABLE,
                contentValue);
    }

    public ArrayList<Photo> getAllPhotos() {
        String[] projection = {
                DbConstants.COLUMN_IMAGE_PATH,
                DbConstants.COLUMN_LATITUDE,
                DbConstants.COLUMN_LONGITUDE
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = DbConstants._ID + " DESC";

        Cursor c = db.query(
                DbConstants.PHOTO_TABLE_NAME,                                       // The table name to query
                projection,                                                         // The columns to return
                null,                                                      // The columns for the WHERE clause
                null,                                                   // The values for the WHERE clause
                null,                                                       // don't group the rows
                null,                                                        // don't filter by row groups
                sortOrder                                                           // The sort order
        );

        return fetchData(c);
    }

    private ArrayList<Photo> fetchData(Cursor c) {
        ArrayList<Photo> photos = new ArrayList<>();

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    String imagePath = c.getString(c.getColumnIndexOrThrow(DbConstants.COLUMN_IMAGE_PATH));
                    double latitude = c.getDouble(c.getColumnIndexOrThrow(DbConstants.COLUMN_LATITUDE));
                    double longitude = c.getDouble(c.getColumnIndexOrThrow(DbConstants.COLUMN_LONGITUDE));

                    // wrap up data list and return
                    photos.add(new Photo(imagePath, latitude, longitude));
                } while (c.moveToNext());
            }
            c.close();
        }
        return photos;
    }
}
