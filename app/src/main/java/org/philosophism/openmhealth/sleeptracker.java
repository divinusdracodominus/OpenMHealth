package org.philosophism.openmhealth;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.location.SleepClassifyEvent;
import com.google.android.gms.location.SleepSegmentEvent;

import org.philosophism.openmhealth.api.contracts.SleepContract;
import org.philosophism.openmhealth.db.SleepDBHelper;

import java.util.List;

public class sleeptracker extends BroadcastReceiver {
    Context context;
    SQLiteDatabase db = null;
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
        if(db == null) {
            db = new SleepDBHelper(context).getWritableDatabase();
        }
        ContentResolver resolver = ctx.getContentResolver();
        Uri uri = Uri.parse(SleepContract.CONTENT_URI);
        Log.i("OpenMHealth", "received broadcast for sleep data");
        if(SleepSegmentEvent.hasEvents(intent)) {
            List<SleepSegmentEvent> events = SleepSegmentEvent.extractEvents(intent);
            for(int i = 0; i < events.size(); i++) {
                SleepSegmentEvent event = events.get(i);
                ContentValues values = new ContentValues();
                values.put(SleepContract.DATE, event.getStartTimeMillis());
                values.put(SleepContract.END_DATE, event.getEndTimeMillis());
                values.put(SleepContract.DURATION, event.getSegmentDurationMillis());
                values.put(SleepContract.IS_EVENT, true);
                db.insert(SleepContract.TABLE_NAME, null, values);
                resolver.insert(uri, values);
            }
        }else if(SleepClassifyEvent.hasEvents(intent)) {
            List<SleepClassifyEvent> classifications = SleepClassifyEvent.extractEvents(intent);
            for(int i = 0; i < classifications.size(); i++) {
                ContentValues values = new ContentValues();
                SleepClassifyEvent event = classifications.get(i);
                values.put(SleepContract.DATE, event.getTimestampMillis());
                values.put(SleepContract.CONFIDENCE, event.getConfidence());
                values.put(SleepContract.BRIGHTNESS, event.getLight());
                values.put(SleepContract.MOTION, event.getMotion());
                values.put(SleepContract.IS_EVENT, false);
                db.insert(SleepContract.TABLE_NAME, null, values);
                resolver.insert(uri, values);
            }
        }
    }
}
