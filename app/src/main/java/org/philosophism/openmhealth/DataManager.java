package org.philosophism.openmhealth;

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

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class DataManager extends AppCompatActivity {

    //ListView metrics = null;
    Button submit = null;
    TextView output;
    CheckBox smsBtn;
    CheckBox calendarBtn;
    CheckBox callLogBtn;

    Metric current = null;
    Metric[] metric_list = new Metric[]{
            new Metric("SMS", Manifest.permission.READ_SMS, "content://sms/inbox", new String[]{"date", "date_sent", "thread_id"}),
            new Metric("call log", Manifest.permission.READ_CALL_LOG, "content://call_log/calls", null)
            //new Metric("calendar", Manifest.permission.READ_CALENDAR, CalendarContract.Calendars.CONTENT_URI, null)
    };
    boolean[] accepted = new boolean[metric_list.length];

    class DataProcThread extends Thread {
        Metric data;
        Cursor cursor;
        String filename;
        DataProcThread(Cursor cursor, String filename, Metric metric) {
            this.data = metric;
            this.filename = filename;
            this.cursor = cursor;
        }
        public void run() {

       }
    }

    private void handle_data(String filename, Metric metric) {
        Cursor cursor = getContentResolver().query(metric.uri, metric.fields, null, null, null);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<HashMap<String, String>> retrieved = read_data(cursor);
                JSONArray newData = new JSONArray(retrieved);
                String text = newData.toString();
                Log.i("OpenMHealth", "managed to make it to text point" + text);
                try {
                    File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "myfile.json");
                    Log.i("OpenMHealth", "wrote data to file: " + getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "myfile.json");

                    FileOutputStream fileOutput = new FileOutputStream(file);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutput);
                    outputStreamWriter.write(text);
                    outputStreamWriter.flush();
                    fileOutput.getFD().sync();
                    outputStreamWriter.close();
                    //output.setText(newData.toString());
                }catch(IOException e) {
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


    private ArrayList<HashMap<String, String>> read_data(Cursor cursor) {
        Log.i("OpenMHealth", "permission granted about to read messages");

        ArrayList<HashMap<String, String>> messages = new ArrayList();
        if (cursor.moveToFirst()) { // must check the result to prevent exception

            do {
                String msgData = "";

                HashMap<String, String> msgMap = new HashMap();
                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    String field = cursor.getColumnName(idx);
                    msgMap.put(field, cursor.getString(idx));
                }
                messages.add(msgMap);
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

        LinearLayout checklist = findViewById(R.id.checklist);


        for(int i = 0; i < metric_list.length; i++) {
            CheckBox btn = new CheckBox(this);
            btn.setText(metric_list[i].name);
            final int val = i;
            btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        accepted[val] = true;
                    }else {
                        accepted[val] = false;
                    }
                }
            });
            checklist.addView(btn);
        }

        submit = new Button(this);
        submit.setText("submit");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < accepted.length; i++) {
                    if(accepted[i]) {
                        handle_data(metric_list[i].name, metric_list[i]);
                    }
                }
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
}