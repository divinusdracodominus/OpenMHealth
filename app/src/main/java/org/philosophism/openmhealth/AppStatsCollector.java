package org.philosophism.openmhealth;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.philosophism.openmhealth.api.AppUsageRecord;
import org.philosophism.openmhealth.db.AppUsageDBHelper;
import org.philosophism.openmhealth.utils.FileManager;

import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

public class AppStatsCollector extends AppCompatActivity {

    UsageStatsManager usageManager;
    SQLiteDatabase database;

    private final ActivityResultLauncher getFileLauncher = registerForActivityResult(
            new ActivityResultContracts.CreateDocument(),
            uri -> {
                if (uri != null) {
                    // call this to persist permission across decice reboots
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        OutputStream file = FileManager.getOutputStream(this, uri);
                        Log.i("AppUsageStats", "callback entered, about to try grabbing data");
                        Toast.makeText(AppStatsCollector.this, "in launcher callback", Toast.LENGTH_LONG);

                        Calendar beginCal = Calendar.getInstance();
                        beginCal.set(Calendar.DATE, 1);
                        beginCal.set(Calendar.MONTH, 5);
                        beginCal.set(Calendar.YEAR, 2023);


                        Calendar endCal = Calendar.getInstance();
                        endCal.set(Calendar.DATE, 1);
                        endCal.set(Calendar.MONTH, 6);
                        endCal.set(Calendar.YEAR, 2023);

                        final List<UsageStats> queryUsageStats = usageManager
                                .queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                                        beginCal.getTimeInMillis(),
                                        endCal.getTimeInMillis());
                        final UsageEvents events = usageManager.queryEvents(beginCal.getTimeInMillis(), endCal.getTimeInMillis());


                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                grab_stats(events, file);
                            }
                        });
                    }catch(Exception e) {
                        Log.e("UsageStats", e.getMessage());
                    }

                } else {
                    // request denied by user
                }
            }
    );

    JSONObject grab_json(UsageEvents.Event currentEvent) throws JSONException {
        JSONObject event_data = new AppUsageRecord(currentEvent).toJson();
        return event_data;
    }

    void grab_stats(UsageEvents events, OutputStream stream) {

        UsageEvents.Event currentEvent;
        Log.i("AppUsageStats", "about to enter while loop");
        JSONArray usageEvents = new JSONArray();
        while (events.hasNextEvent()) {
            currentEvent = new UsageEvents.Event();
            events.getNextEvent(currentEvent);
            Log.d("AppStatusCollector", "inside while...");

            try {
                AppUsageRecord record = new AppUsageRecord(currentEvent);
                String recordText = record.toJson().toString();
                Log.i("AppUsageCollector", "text: " + recordText);
                usageEvents.put(record.toJson());
            } catch (JSONException e) {
                Toast.makeText(this, "failed to create JSON object " + e.getMessage(), Toast.LENGTH_LONG);
            }
        }

        try {
            FileManager.writeToOutputStream(stream, usageEvents.toString());

        }catch(Exception e) {
            Log.e("AppStatsCollector", e.getMessage());
        }
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
                Log.i("AppUSageStats", "about to launch activity");
                Toast.makeText(AppStatsCollector.this, "about to lauch activity", Toast.LENGTH_LONG);
                if(granted) {
                    getFileLauncher.launch(null);
                }else{
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
            }
        });
    }
}