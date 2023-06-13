package org.philosophism.openmhealth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.philosophism.openmhealth.db.AppUsageDBHelper;

import java.util.Calendar;
import java.util.List;

public class AppStatsCollector extends AppCompatActivity {

    String type_to_string(int event_type) {
        String kind = new String();
        switch(event_type) {
            case UsageEvents.Event.ACTIVITY_PAUSED:
                kind = "PAUSED";
            case UsageEvents.Event.ACTIVITY_RESUMED:
                kind = "RESUMED";
            case UsageEvents.Event.ACTIVITY_STOPPED:
                kind = "STOPPED";
            case UsageEvents.Event.DEVICE_SHUTDOWN:
                kind = "SHUTDOWN";
            case UsageEvents.Event.DEVICE_STARTUP:
                kind = "STARTUP";
            case UsageEvents.Event.FOREGROUND_SERVICE_START:
                kind = "FOREGROUND_START";
            case UsageEvents.Event.FOREGROUND_SERVICE_STOP:
                kind = "FOREGROUND_STOP";
            case UsageEvents.Event.KEYGUARD_HIDDEN:
                kind = "KEYBOARD_HIDEEN";
            case UsageEvents.Event.KEYGUARD_SHOWN:
                kind = "KEYBOARD_SHOWN";
            case UsageEvents.Event.SCREEN_INTERACTIVE:
                kind = "SCREEN_INTERACTIVE";
            case UsageEvents.Event.SCREEN_NON_INTERACTIVE:
                kind = "SCREEN_NON_INTERACTIVE";
            case UsageEvents.Event.NONE:
                kind = "NONE";
            case UsageEvents.Event.USER_INTERACTION:
                kind = "USER_INTERACTION";
            case UsageEvents.Event.SHORTCUT_INVOCATION:
                kind = "SHORTCUT_INTERACTION";
            case UsageEvents.Event.STANDBY_BUCKET_CHANGED:
                kind = "STANDBY_BUCKET_CHANGED";
            default:
                kind = "UNRECOGNIZED";
        }
        return kind;
    }

    JSONObject grab_json(UsageEvents.Event currentEvent) throws JSONException {
        JSONObject event_data = new JSONObject();
        String kind = type_to_string(currentEvent.getEventType());
        String package_name = currentEvent.getPackageName();
        long time = currentEvent.getTimeStamp();
        event_data.put("package_name", package_name);
        event_data.put("kind", kind);
        event_data.put("time", time);
        return event_data;
    }

    UsageStatsManager usageManager;
    SQLiteDatabase database;
    void grab_stats() {
        Calendar beginCal = Calendar.getInstance();
        beginCal.set(Calendar.DATE, 1);
        beginCal.set(Calendar.MONTH, 8);
        beginCal.set(Calendar.YEAR, 2022);



        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.DATE, 1);
        endCal.set(Calendar.MONTH, 5);
        endCal.set(Calendar.YEAR, 2023);

        final List<UsageStats> queryUsageStats = usageManager
                .queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                        beginCal.getTimeInMillis(),
                        endCal.getTimeInMillis());
        final UsageEvents events = usageManager.queryEvents(beginCal.getTimeInMillis(), endCal.getTimeInMillis());

        UsageEvents.Event currentEvent;

        JSONArray usageEvents = new JSONArray();
        while (events.hasNextEvent()) {
            currentEvent = new UsageEvents.Event();
            events.getNextEvent(currentEvent);
            try {
                JSONObject data = grab_json(currentEvent);
                usageEvents.put(data);
            }catch(JSONException e) {
                Toast.makeText(this, "failed to create JSON object " + e.getMessage(), Toast.LENGTH_LONG);
            }
        }
        /*for(int i = 0; i < queryUsageStats.size(); i++) {
            UsageStats stat = queryUsageStats.get(i);
            Log.i("OpenMHealth", "collected stat: " + stat.getPackageName());
            ContentValues values = new ContentValues();
            values.put("package_name", stat.getPackageName());
            values.put("first_used", stat.getFirstTimeStamp());
            values.put("last_us ed", stat.getLastTimeStamp());
            values.put("total_foreground", stat.getTotalTimeInForeground());
            database.insert(AppUsageDBHelper.TABLE_NAME, null, values);
        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_stats_collector);

        usageManager = (UsageStatsManager)this.getSystemService(Context.USAGE_STATS_SERVICE);
        database = new AppUsageDBHelper(this).getWritableDatabase();

        Button collect = findViewById(R.id.collect);
        collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                AppOpsManager appOps = (AppOpsManager) context
                        .getSystemService(Context.APP_OPS_SERVICE);
                int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                        android.os.Process.myUid(), context.getPackageName());
                boolean granted = mode == AppOpsManager.MODE_ALLOWED;

                if(granted) {
                    grab_stats();
                }else{
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
            }
        });
    }
}