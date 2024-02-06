package org.philosophism.openmhealth;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.philosophism.openmhealth.api.AppUsageRecord;
import org.philosophism.openmhealth.db.AppUsageDBHelper;
import org.philosophism.openmhealth.utils.FileManager;
import org.philosophism.openmhealth.utils.MenuListener;

import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

public class AppStatsCollector extends AppCompatActivity {

    UsageStatsManager usageManager;
    SQLiteDatabase database;
    CalendarView beginView;
    CalendarView endView;
    Calendar begin = null;
    Calendar end = null;
    NavigationView navView;

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
                        beginCal.set(Calendar.DAY_OF_MONTH, 1);
                        beginCal.set(Calendar.MONTH, 8);
                        beginCal.set(Calendar.YEAR, 2023);


                        Calendar endCal = Calendar.getInstance();
                        endCal.set(Calendar.DAY_OF_MONTH, 1);
                        endCal.set(Calendar.MONTH, 10);
                        endCal.set(Calendar.YEAR, 2023);
                        if(begin != null && end != null) {
                            Log.i("AppUsageEvents", "begining or end is null");
                            final UsageEvents events = usageManager.queryEvents(beginCal.getTimeInMillis(), endCal.getTimeInMillis());
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    grab_stats(events, file);
                                }
                            });
                        }else{
                            final UsageEvents events = usageManager.queryEvents(begin.getTimeInMillis(), end.getTimeInMillis());
                            Log.i("AppUsageEvents", "begin and end are not null");
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    grab_stats(events, file);
                                }
                            });
                        }
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

        navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new MenuListener(this, navView));

        Button open_menu = findViewById(R.id.open_menu);
        open_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(navView.getVisibility() == View.VISIBLE) {
                    navView.setVisibility(View.GONE);
                }else{
                    navView.setVisibility(View.VISIBLE);
                }
            }
        });

        beginView = findViewById(R.id.beginDate);
        endView = findViewById(R.id.endDate);
        begin = Calendar.getInstance();
        end = Calendar.getInstance();

        beginView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                begin.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                begin.set(Calendar.MONTH, month);
                begin.set(Calendar.YEAR, year);
            }
        });

        endView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                end.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                end.set(Calendar.MONTH, month);
                end.set(Calendar.YEAR, year);
            }
        });
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
                Toast.makeText(AppStatsCollector.this,  "about to launch activity", Toast.LENGTH_LONG);
                if(granted) {
                    getFileLauncher.launch(null);
                }else{
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
            }
        });
    }
}