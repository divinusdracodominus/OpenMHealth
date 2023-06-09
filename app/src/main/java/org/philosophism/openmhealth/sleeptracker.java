package org.philosophism.openmhealth;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.location.SleepClassifyEvent;
import com.google.android.gms.location.SleepSegmentEvent;

import java.util.List;

public class sleeptracker extends BroadcastReceiver {
    Context context;
    public static void startAction(Context context) {
        Intent intent = new Intent(context, activitytracker.class);
        context.startService(intent);
    }

    static PendingIntent createSleepReceiverPendingIntent(Context context) {
        Intent sleepIntent = new Intent(context, activitytracker.class);
        return PendingIntent.getBroadcast(context, 0, sleepIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        ContentResolver resolver = ctx.getContentResolver();
        Uri uri = Uri.parse("content://openmhealth/sleepdata");
        Log.i("OpenMHealth", "received broadcast for sleep data");
        if(SleepSegmentEvent.hasEvents(intent)) {
            List<SleepSegmentEvent> events = SleepSegmentEvent.extractEvents(intent);
            for(int i = 0; i < events.size(); i++) {
                SleepSegmentEvent event = events.get(i);
                ContentValues values = new ContentValues();
                values.put("time", event.getStartTimeMillis());
                values.put("end_time", event.getEndTimeMillis());
                values.put("duration", event.getSegmentDurationMillis());
                values.put("is_event", true);
                resolver.insert(uri, values);
            }
        }else if(SleepClassifyEvent.hasEvents(intent)) {
            List<SleepClassifyEvent> classifications = SleepClassifyEvent.extractEvents(intent);
            for(int i = 0; i < classifications.size(); i++) {
                ContentValues values = new ContentValues();
                SleepClassifyEvent event = classifications.get(i);
                values.put("time", event.getTimestampMillis());
                values.put("confidence", event.getConfidence());
                values.put("light", event.getLight());
                values.put("motion", event.getMotion());
                values.put("is_event", false);
                resolver.insert(uri, values);
            }
        }
    }
}
