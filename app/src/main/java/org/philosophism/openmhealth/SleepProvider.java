package org.philosophism.openmhealth;

import org.philosophism.openmhealth.db.SleepDBHelper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class SleepProvider extends ContentProvider {
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI("openmhealth", "sleepdata", 1);
        uriMatcher.addURI("openmhealth", "sleepdata/#", 2);
    }

    SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        db = new SleepDBHelper(getContext()).getWritableDatabase();
        return true;
    }

    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {
        /*
         * Choose the table to query and a sort order based on the code returned for the incoming
         * URI. Here, too, only the statements for table 3 are shown.
         */
        switch (uriMatcher.match(uri)) {


            // If the incoming URI was for all of table3
            case 1:

                if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                break;

            // If the incoming URI was for a single row
            case 2:
                selection = selection + "_ID = " + uri.getLastPathSegment();
                break;
            default:

                // If the URI isn't recognized, do some error handling here
        }
        // Call the code to actually do the query
        return db.query("sleepdata", projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) throws SQLException {
        long id = db.insert("sleepdata", null, values);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        return 0;
    }

    @Override
    public int delete (Uri uri, String selection, String[] selectionArgs) {
        return db.delete("sleepdata", selection, selectionArgs);
    }

    public String getType(Uri uri) {
        return "geopackage.sqlite3";
    }

}
