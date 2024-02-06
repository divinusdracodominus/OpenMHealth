package org.philosophism.openmhealth;

import org.philosophism.openmhealth.api.contracts.SleepContract;
import org.philosophism.openmhealth.utils.MenuListener;
import org.philosophism.openmhealth.utils.Metric;
import static android.database.Cursor.FIELD_TYPE_BLOB;
import static android.database.Cursor.FIELD_TYPE_FLOAT;
import static android.database.Cursor.FIELD_TYPE_INTEGER;
import static android.database.Cursor.FIELD_TYPE_NULL;
import static android.database.Cursor.FIELD_TYPE_STRING;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Looper;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.Hex;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.philosophism.openmhealth.utils.FileManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import java.util.ArrayList;
import java.util.HashMap;


public class DataManager extends AppCompatActivity {

    //ListView metrics = null;
    Button submit = null;

    SharedPreferences sharedPref;
    MetaData metadata;
    Uri filename;
    Metric current = null;
    NavigationView navView;

    private final ActivityResultLauncher getDirLauncher = registerForActivityResult(
            new ActivityResultContracts.CreateDocument(),
            uri -> {
                if (uri != null) {
                    // call this to persist permission across decice reboots
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    metadata.source = current.uri.toString();
                    handle_data(uri, current, metadata);

                } else {
                    // request denied by user
                }
            }
    );

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.i("OpenmHealth", "permission granted");
                    getDirLauncher.launch(null);
                } else {
                    Log.i("OpenmHealth", "permission is not granted");
                    /// should show reason why its required
                }
            });
    // address for sms phone number, number for call log phone number
    Metric[] metric_list = new Metric[]{
            new Metric("incoming SMS", Manifest.permission.READ_SMS, "content://sms/inbox",
                    new String[]{"date", "date_sent", "thread_id", "address"},
                    new String[]{"date", "thread_id"}),
            new Metric("calllog", Manifest.permission.READ_CALL_LOG, "content://call_log/calls",
                    new String[]{"date", "number", "duration"},
                    new String[]{"date", "duration"}),
           /* new Metric("sleep data", Manifest.permission.ACTIVITY_RECOGNITION, SleepContract.CONTENT_URI, new String[] {
                    SleepContract._ID,
                    SleepContract.DATE,
                    SleepContract.END_DATE,
                    SleepContract.DURATION,
                    SleepContract.CONFIDENCE,
                    SleepContract.BRIGHTNESS,
                    SleepContract.MOTION,
                    SleepContract.IS_EVENT
            }),*/
            new Metric("calendar", Manifest.permission.READ_CALENDAR,  CalendarContract.Events.CONTENT_URI,
                    new String[] {
                            CalendarContract.Events._ID,
                            CalendarContract.Events.ACCOUNT_NAME,
                            CalendarContract.Events.TITLE,
                            CalendarContract.Events.DESCRIPTION,
                            CalendarContract.Events.EVENT_LOCATION,
                            CalendarContract.Events.DTSTART,
                            CalendarContract.Events.DTEND,
                            CalendarContract.Events.EVENT_COLOR,
                            CalendarContract.Events.DISPLAY_COLOR,
                            CalendarContract.Events.RDATE,
                            CalendarContract.Events.DURATION,
                            CalendarContract.Events.EVENT_COLOR,
                            CalendarContract.Events.ORGANIZER,
                            CalendarContract.Events.OWNER_ACCOUNT
                    },
                    new String[] {CalendarContract.Events.RDATE, "selfAttendeeStatus"}
                    ),
            /*new Metric("event_instances", Manifest.permission.READ_CALENDAR, CalendarContract.Instances.CONTENT_URI, new String[] {
                    CalendarContract.Instances.EVENT_ID,
                    CalendarContract.Instances.BEGIN,
                    CalendarContract.Instances.END
            }, new String[]{}),*/
            new Metric("outgoing_SMS", Manifest.permission.READ_SMS, "content://sms/sent", new String[]{"date", "date_sent", "thread_id", "_id", "address", "body"})
    };
    boolean[] accepted = new boolean[metric_list.length];




    private void handle_data(Uri filename, Metric metric, MetaData data) {
        if(current == null) return;
        Cursor cursor = getContentResolver().query(metric.uri, metric.fields, null, null, null);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    UUID metadata_id = data.id;

                    ArrayList<JSONObject> retrieved = Metric.read_data(cursor, metadata_id);
                    JSONArray newData = new JSONArray(retrieved);
                    JSONObject output = new JSONObject();
                    output.put("metadata", data);
                    output.put("data", newData);
                    String text = output.toString();

                    Log.i("OpenMHealth", "managed to make it to text point" + text);

                    FileManager.writeToOutputStream(FileManager.getOutputStream(DataManager.this, filename), text);
                    //output.setText(newData.toString());
                }catch(IOException e) {
                    Log.e("OpenMHealth", "Error occured: " + e.getMessage());

                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                }catch(JSONException e) {
                    Log.e("OpenMHealth", "Error occured");
                    Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    public static String[] getNames(Metric[] metrics) {
        String[] values = new String[metrics.length];
        for(int i = 0; i < metrics.length; i++) {
            values[i] = metrics[i].name;
        }
        return values;
    }

    private final String[] basedata = new String[]{"date", "date_sent", "thread_id"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_manager);
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        UUID device_id = UUID.fromString(sharedPref.getString("device_id", UUID.randomUUID().toString()));
        UUID participant_id = UUID.randomUUID();
        UUID metadata_id = UUID.randomUUID();
        metadata = new MetaData(metadata_id, device_id, participant_id);
        LinearLayout checklist = findViewById(R.id.checklist);

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

        for(int i = 0; i < metric_list.length; i++) {
            Button btn = new Button(this);
            btn.setText("export " + metric_list[i].name);
            final int val = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    current = metric_list[val];
                    accepted[val] = true;
                    requestPermission(metric_list[val]);
                }
            });
            checklist.addView(btn);
        }

        submit = new Button(this);
        submit.setText("submit");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivityForResult(Intent.createChooser(i, "Choose directory"), 9999);
                Intent next = new Intent(DataManager.this, MainActivity.class);
                startActivity(next);
            }
        });

        checklist.addView(submit);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 3345:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {} else {}
                return;
        }
    }

    void requestPermission(Metric metric) {

        if (ContextCompat.checkSelfPermission(
                this, metric.permission) ==
                PackageManager.PERMISSION_GRANTED) {
            getDirLauncher.launch(null);
        } else {
            
            requestPermissionLauncher.launch(
                    metric.permission);
        }
    }
}