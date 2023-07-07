package org.philosophism.openmhealth.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.philosophism.openmhealth.api.contracts.SleepContract;

public class SleepDBHelper extends SQLiteOpenHelper {
    private final Context context = null;
    private final String TAG = "OpenMHealth";

    // constructor
    public SleepDBHelper(Context context) {
        super(context, "sleepdata", null, 3);
        context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + SleepContract.TABLE_NAME + " ("
                + SleepContract._ID + " integer primary key, "
                + SleepContract.ID + " text unique, "
                + SleepContract.DATE + " integer not null, "
                + SleepContract.END_DATE + " integer, "
                + SleepContract.DURATION + " integer, "
                + SleepContract.CONFIDENCE + " integer, "
                + SleepContract.BRIGHTNESS + " integer, "
                + SleepContract.MOTION + " integer, "
                + SleepContract.IS_EVENT + " boolean);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS sleepdata");
        onCreate(db);

    }
}
