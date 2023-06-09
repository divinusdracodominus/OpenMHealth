package org.philosophism.openmhealth;

import static android.database.Cursor.FIELD_TYPE_BLOB;
import static android.database.Cursor.FIELD_TYPE_FLOAT;
import static android.database.Cursor.FIELD_TYPE_INTEGER;
import static android.database.Cursor.FIELD_TYPE_NULL;
import static android.database.Cursor.FIELD_TYPE_STRING;

import org.philosophism.openmhealth.Metric;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

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

    Metric[] metric_list = new Metric[]{
            new Metric("incoming_SMS", Manifest.permission.READ_SMS, "content://sms/inbox", new String[]{"date", "date_sent", "thread_id", "address", "body"}),
            new Metric("calllog", Manifest.permission.READ_CALL_LOG, "content://call_log/calls",
                    new String[]{"number", "date", "duration"}),
            new Metric("calendar", Manifest.permission.READ_CALENDAR, "content://com.android.calendar/events",
                    new String[] {
                            "name",
                            "title",
                            "description",
                            "ownerAccount",
                            "eventLocation",
                            "selfAttendeeStatus"
                    }),
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

                    ArrayList<JSONObject> retrieved = read_data(cursor, metadata_id);
                    JSONArray newData = new JSONArray(retrieved);
                    JSONObject output = new JSONObject();
                    output.put("metadata", data);
                    output.put("data", newData);
                    String text = output.toString();

                    Log.i("OpenMHealth", "managed to make it to text point" + text);

                    OutputStream file = getContentResolver().openOutputStream(filename);
                    Log.i("OpenMHealth", "wrote data to file: " + filename);

                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(file);
                    outputStreamWriter.write(text);
                    outputStreamWriter.flush();

                    outputStreamWriter.close();
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

    static int PERMISSION_REQUEST_CODE = 3345;

    public interface DataMetric {
        DataMetric fromCursor(Cursor cursor, UUID participant_id, UUID device_id);
        DataMetric fromJson(JSONObject obj);
        JSONObject toJson();
    }

    private ArrayList<JSONObject> read_data(Cursor cursor, UUID metadata) throws JSONException {
        Log.i("OpenMHealth", "permission granted about to read messages");

        ArrayList<JSONObject> messages = new ArrayList();
        if (cursor.moveToFirst()) { // must check the result to prevent exception

            do {
                String msgData = "";

                HashMap<String, String> msgMap = new HashMap();
                JSONObject json =new JSONObject();
                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    String field = cursor.getColumnName(idx);
                    msgMap.put(field, cursor.getString(idx));
                    int type = cursor.getType(idx);
                    switch(type) {
                        case FIELD_TYPE_STRING:
                            json.put(cursor.getColumnName(idx), cursor.getString(idx));
                            break;
                        case FIELD_TYPE_INTEGER:
                            json.put(cursor.getColumnName(idx), cursor.getLong(idx));
                            break;
                        case FIELD_TYPE_FLOAT:
                            json.put(cursor.getColumnName(idx), cursor.getFloat(idx));
                            break;
                        case FIELD_TYPE_BLOB:
                            json.put(cursor.getColumnName(idx), cursor.getBlob(idx));
                            break;
                        case FIELD_TYPE_NULL:

                            break;
                    }
                    String fieldname = cursor.getColumnName(idx);
                    try {
                        if (fieldname.equals("number") || fieldname.equals("address")) {
                            MessageDigest digest = MessageDigest.getInstance("SHA-256");
                            byte[] hash = digest.digest(cursor.getString(idx).getBytes("UTF-8"));
                            json.put("recipient_id", Hex.bytesToStringLowercase(hash));
                        }
                    }catch(NoSuchAlgorithmException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
                    }catch(UnsupportedEncodingException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
                    }
                }
                json.put("metadata", metadata.toString());
                json.put("id", UUID.randomUUID().toString());
                messages.add(json);
                // use msgData

            } while (cursor.moveToNext());


        } else {
            Log.i("OpenMHealth", "no sms to be found");
        }
        return messages;
    }

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

        /*metrics.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, metric_names));
        metrics.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        metrics.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView arg0, View item, int position, long id)
            {
                ListAdapter adapter = metrics.getAdapter();
                if(adapter.isEnabled(position)) {
                    Log.i("OpenMHealth", "check box enabled at position " + position);
                    accepted[position] = metric_list[position];

                }else{
                    Log.i("OpenMHealth", "disabled at position " + position);
                    accepted[position] = null;
                }
            }
        });*/
        //setListViewHeightBasedOnChildren(metrics);

    }

    public static void setListViewHeightBasedOnChildren(ListView myListView) {
        ListAdapter adapter = myListView.getAdapter();


            int totalHeight = 0;
            for (int i = 0; i < adapter.getCount(); i++) {
                View item= adapter.getView(i, null, myListView);
                item.measure(0, 0);
                totalHeight += item.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = myListView.getLayoutParams();
            params.height = totalHeight + (myListView.getDividerHeight() * (adapter.getCount() - 1));
            myListView.setLayoutParams(params);

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