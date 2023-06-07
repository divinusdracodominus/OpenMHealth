package org.philosophism.openmhealth;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;

import com.google.android.gms.location.SleepClassifyEvent;
import com.google.android.gms.location.SleepSegmentEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class activitytracker extends BroadcastReceiver {

    public static void startAction(Context context, String param1, String param2) {
        Intent intent = new Intent(context, activitytracker.class);
        context.startService(intent);
    }

    static PendingIntent createSleepReceiverPendingIntent(Context context) {
        Intent sleepIntent = new Intent(context, activitytracker.class);
        return PendingIntent.getBroadcast(context, 0, sleepIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if(SleepSegmentEvent.hasEvents(intent)) {
            List<SleepSegmentEvent> events = SleepSegmentEvent.extractEvents(intent);

        }else if(SleepClassifyEvent.hasEvents(intent)) {
            List<SleepClassifyEvent> classifications = SleepClassifyEvent.extractEvents(intent);

        }
    }


}