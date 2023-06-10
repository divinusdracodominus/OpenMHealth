package org.philosophism.openmhealth;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.SleepClassifyEvent;
import com.google.android.gms.location.SleepSegmentEvent;

import java.util.ArrayList;
import java.util.List;

public class activitytracker extends BroadcastReceiver {
    Context context;
    public static void startAction(Context context) {
        Intent intent = new Intent(context, activitytracker.class);
        context.startService(intent);
    }

    static PendingIntent createTrackerReceiverPendingIntent(Context context) {
        Intent sleepIntent = new Intent(context, activitytracker.class);
        return PendingIntent.getBroadcast(context, 0, sleepIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        ContentResolver resolver = ctx.getContentResolver();
        //Uri uri = Uri.parse("content://openmhealth/activitytracker");
        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                Log.i("OpenMHealth", "received activity result " + event.getActivityType());
            }
        }else{
            Log.i("OpenMHealth", "received broadcast but no activity results");
        }

    }
}