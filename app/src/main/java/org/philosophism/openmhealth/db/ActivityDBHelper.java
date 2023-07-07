package org.philosophism.openmhealth.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.philosophism.openmhealth.api.contracts.ActivityContract;

public class ActivityDBHelper extends SQLiteOpenHelper {
    private final Context context = null;
    private final String TAG = "OpenMHealth";
    public static final String TABLE_NAME = "activitytracking";

    // constructor
    public ActivityDBHelper(Context context) {
        super(context, "sleepdata", null, 3);
        context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "("
                + ActivityContract._ID + " integer primary key, "
                + ActivityContract.ID + " text unique not null, "
                + ActivityContract.DATE + " integer, "
                + ActivityContract.TRANSITION_TYPE + " text, "
                + ActivityContract.ACTIVITY + " text, "
                + ActivityContract.PLATFORM_ACTIVITY_TYPE + " integer);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }
}
