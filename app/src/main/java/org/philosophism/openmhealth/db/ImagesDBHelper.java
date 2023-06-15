package org.philosophism.openmhealth.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ImagesDBHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "savedimages";
    public static final String REMOTE_URL = "remoteurl";
    public static final String LOCAL_URI = "localuri";
    public static final String DATE = "date";
    public static final String SELECT_BY_ID = REMOTE_URL + "=?";
    Context context;

    public ImagesDBHelper(Context context) {
        super(context, TABLE_NAME, null, 3);
        context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME +
                " (" + REMOTE_URL + " text primary key, " +
                LOCAL_URI + " text NOT NULL UNIQUE, " + DATE + " datetime default current_timestamp);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(TABLE_NAME, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }
}
