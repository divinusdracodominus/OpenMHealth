package org.philosophism.openmhealth;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.SleepClassifyEvent;
import com.google.android.gms.location.SleepSegmentEvent;

import org.philosophism.openmhealth.api.contracts.ActivityContract;
import org.philosophism.openmhealth.db.ActivityDBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class activitytracker extends BroadcastReceiver {
    SQLiteDatabase db = null;

    static PendingIntent createTrackerReceiverPendingIntent(Context context) {
        Intent sleepIntent = new Intent(context, activitytracker.class);
        return PendingIntent.getBroadcast(context, 0, sleepIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
    }
    public static String getActivityType(int type) {
        switch(type) {
            case DetectedActivity.ON_BICYCLE:
                return "ON_BICYCLE";
            case DetectedActivity.IN_VEHICLE:
                return "IN_VEHICLE";
            case DetectedActivity.ON_FOOT:
                return "ON_FOOT";
            case DetectedActivity.STILL:
                return "STILL";
            case DetectedActivity.UNKNOWN:
                return "UNKNOWN";
            case DetectedActivity.TILTING:
                return "TILTING";
            case DetectedActivity.WALKING:
                return "WALKING";
            case DetectedActivity.RUNNING:
                return "RUNNING";
            default:
                return "UNRECOGNIZED";
        }
    }
    @Override
    public void onReceive(Context ctx, Intent intent) {
        ContentResolver resolver = ctx.getContentResolver();
        //Uri uri = Uri.parse("content://openmhealth/activitytracker");
        if(db == null) {
            db = new ActivityDBHelper(ctx).getWritableDatabase();
        }
        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                Log.i("activitytracker", "got result with type: " + getActivityType(event.getActivityType()));
                ContentValues values = new ContentValues();
                values.put(ActivityContract.ID, UUID.randomUUID().toString());
                values.put(ActivityContract.DATE, event.getElapsedRealTimeNanos());
                values.put(ActivityContract.PLATFORM_ACTIVITY_TYPE, event.getActivityType());
                String transition_type;
                if(event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                    transition_type = "ENTER";
                }else if(event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
                    transition_type = "EXIT";
                }else{
                    transition_type = "UNKNOWN";
                }
                values.put(ActivityContract.TRANSITION_TYPE, transition_type);
                values.put(ActivityContract.ACTIVITY, getActivityType(event.getActivityType()));
                db.insert(ActivityContract.TABLE_NAME, null, values);
                Log.i("OpenMHealth", "received activity result " + event.getActivityType());
            }
        }else{
            Log.i("OpenMHealth", "received broadcast but no activity results");
        }

    }
}