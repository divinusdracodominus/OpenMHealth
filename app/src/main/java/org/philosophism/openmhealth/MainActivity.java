package org.philosophism.openmhealth;

import org.json.JSONException;
import org.philosophism.openmhealth.api.Event;
import org.philosophism.openmhealth.utils.EventsAdapter;
import org.philosophism.openmhealth.utils.MenuListener;
import org.philosophism.openmhealth.utils.Metric;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    final int PERMISSION_REQUEST = 2252;
    NavigationView navView;
    Event[] sampleEvents = new Event[]{
            new Event("event 1", "description 1"),
            new Event("event 1", "description 1"),
            new Event("event 1", "description 1"),
            new Event("event 1", "description 1"),
    };

    Metric calendar = Metric.events;
    /*Metric calendar = new org.philosophism.openmhealth.utils.Metric("calendar", Manifest.permission.READ_CALENDAR, "content://com.android.calendar/events",
            new String[] {
                    CalendarContract.Events.ACCOUNT_NAME,
                    CalendarContract.Events.TITLE,
                    CalendarContract.Events.DESCRIPTION,
                    "ownerAccount",
                    "eventLocation",
                    "selfAttendeeStatus"
            },
            new String[] {"date", "selfAttendeeStatus"}
    );*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar bar = findViewById(R.id.my_toolbar);
        navView = (NavigationView) findViewById(R.id.nav_view);

        Event[] events = null;
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Cursor cursor = getContentResolver().query(calendar.uri, calendar.fields, null, null, null);
            try {
                ArrayList<JSONObject> eventdata = Metric.read_data(cursor, UUID.randomUUID());

                ArrayList<Event> tempList = new ArrayList<>();
                for(int i = 0; i < eventdata.size(); i++) {
                    Event newEvent = Event.fromJSON(eventdata.get(i));
                    if(newEvent != null) {
                        newEvent.image_url = "https://www.pdx.edu/sites/g/files/znldhr781/files/styles/large_hero_media_extra_large_1440_x_587/public/2022-10/20220711_PSU_PDX_Skyline_033.jpg";
                        newEvent.image_description = "View of Portland Oregon Downtown";
                        tempList.add(newEvent);
                    }
                }
                Event[] newEvents = new Event[tempList.size()];
                for(int i = 0; i < tempList.size(); i++) {
                    newEvents[i] = tempList.get(i);
                }
                events = newEvents;

            }catch (JSONException e) {
                Log.e("MyTag", "error: " + e.getMessage());
                e.printStackTrace();
                events = sampleEvents;
            }
        }else{
            events = sampleEvents;
        }

        EventsAdapter adapter = new EventsAdapter(MainActivity.this, events);

        adapter.setOnItemSelectedListener(new EventsAdapter.ItemSelectedListener() {
            @Override
            public void onSelect(View v, int index) {
                Log.i("MyDataManager", "item clicked at index: " + index);
            }
        });
        RecyclerView eventsview = findViewById(R.id.eventlist);
        eventsview.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        eventsview.setAdapter(adapter);

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

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean firstRun = sharedPref.getBoolean("firstRun", true);
        if (firstRun) {
            editor.putBoolean("firstRun", false);
            editor.putString("device_id", UUID.randomUUID().toString());
        }

        navView.setNavigationItemSelectedListener(new MenuListener(this, navView));

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}