package org.philosophism.openmhealth;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SleepDBHelper extends SQLiteOpenHelper {
    private final Context context = null;
    private final String TAG = "OpenMHealth";

    // constructor
    SleepDBHelper(Context context) {
        super(context, "sleepdata", null, 3);
        context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE sleepdata (_id integer primary key, time integer not null, end_time integer, duration integer, confidence integer, light integer, motion integer, is_event boolean);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS sleepdata");
        onCreate(db);

    }
}
