package org.philosophism.openmhealth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ResultsManager extends AppCompatActivity {
    private EditText startDateEditText;
    private EditText endDateEditText;
    private Button chooseFileButton;
    private CheckBox callDataCheckbox;
    private CheckBox smsDataCheckbox;
    private Button submitButton;
    private static final int FILE_REQUEST_CODE = 123;
    private ActivityResultLauncher<String> filePickerLauncher;
    private List<SMSRecord> smsRecords = new ArrayList<>();
    private TextView summaryTextView;
    private TableLayout summaryTableLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_results_page);

        startDateEditText = findViewById(R.id.start_Date_Edit_Text);
        endDateEditText = findViewById(R.id.end_Date_Edit_Text);
        chooseFileButton = findViewById(R.id.chooseFileButton);
        callDataCheckbox = findViewById(R.id.callDataCheckbox);
        smsDataCheckbox = findViewById(R.id.smsDataCheckbox);
        submitButton = findViewById(R.id.submitButton);
        summaryTextView = findViewById(R.id.summaryTextView);
        summaryTableLayout = findViewById(R.id.summaryTableLayout);

        SMSRecord current =null;
        final ActivityResultLauncher getDirLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        // call this to persist permission across decice reboots
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        smsRecords = handle_data(uri);
                        Log.i("sms data activityresult","no"+ smsRecords.size());

                    } else {
                        // request denied by user
                    }
                }
        );

        chooseFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDirLauncher.launch(null);


            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                String startDate = startDateEditText.getText().toString();
                try {
                    Log.i("sms submit button","no"+ smsRecords.size());
                    Date date = dateFormat.parse(startDate);
                    long starttime = date.getTime() ;
                    System.out.print("startdate:"+starttime);
                    String endDate = endDateEditText.getText().toString();
                    date = dateFormat.parse(endDate);
                    long endtime = date.getTime() ;
                    System.out.print("enddate"+endtime);
                    List<SMSRecord> filteredRecords = filterSMSRecords(starttime, endtime,smsRecords);
                    int totalCount = filteredRecords.size();
                    List<MonthSummary> monthSummaries = summarizeRecordsByMonth(filteredRecords);

                    // Display the month summaries in a table
                    for (MonthSummary monthSummary : monthSummaries) {
                        Log.i("Summary", "Month-Year: " + monthSummary.getMonthYear() + ", Total Records: " + monthSummary.getTotalRecords());
                        TableRow tableRow = new TableRow(ResultsManager.this);

                        TextView monthYearTextView = new TextView(ResultsManager.this);
                        monthYearTextView.setText(monthSummary.getMonthYear());

                        TextView totalRecordsTextView = new TextView(ResultsManager.this);
                        totalRecordsTextView.setText("   " + String.valueOf(monthSummary.getTotalRecords()));

                        tableRow.addView(monthYearTextView);
                        tableRow.addView(totalRecordsTextView);

                        summaryTableLayout.addView(tableRow);
                    }
                    // Display the total number of filtered records
                    Toast.makeText(ResultsManager.this, "Total records fetched: " + totalCount, Toast.LENGTH_SHORT).show();


                }
                catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }
    private List<MonthSummary> summarizeRecordsByMonth(List<SMSRecord> records) {
        List<MonthSummary> monthSummaries = new ArrayList<>();

        // Create a map to store the records for each month
        Map<String, List<SMSRecord>> recordsByMonth = new HashMap<>();

        for (SMSRecord record : records) {
            // Convert the record's date to a month-year format (MM/yyyy)
            String monthYear = new SimpleDateFormat("MM/yyyy", Locale.US).format(new Date(record.getDate()));

            // Add the record to the corresponding month in the map
            List<SMSRecord> monthRecords = recordsByMonth.get(monthYear);
            if (monthRecords == null) {
                monthRecords = new ArrayList<>();
                recordsByMonth.put(monthYear, monthRecords);
            }
            monthRecords.add(record);
        }

        // Create MonthSummary objects for each month
        for (Map.Entry<String, List<SMSRecord>> entry : recordsByMonth.entrySet()) {
            String monthYear = entry.getKey();
            List<SMSRecord> monthRecords = entry.getValue();
            int totalRecords = monthRecords.size();

            // Create a MonthSummary object and add it to the list
            MonthSummary monthSummary = new MonthSummary(monthYear, totalRecords);
            monthSummaries.add(monthSummary);
        }

        return monthSummaries;
    }

    private List<SMSRecord> filterSMSRecords(long startTime, long endTime, List<SMSRecord> records) {
        List<SMSRecord> filteredRecords = new ArrayList<>();
        for (SMSRecord smsRecord : records) {
            long recordDate = smsRecord.getDate();
            if (recordDate >= startTime && recordDate <= endTime) {
                filteredRecords.add(smsRecord);
            }
        }
        Log.i("filtered","records size"+records.size());
        return filteredRecords;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri fileUri = data.getData();
                // Process the selected file
                // Example: Display the file name
                String fileName = getFileName(fileUri);
                Toast.makeText(ResultsManager.this, "Selected file: " + fileName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
    private String readJsonDataFromInputStream(InputStream inputStream) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            stringBuilder.append(new String(buffer, 0, bytesRead));
        }
        return stringBuilder.toString();
    }

    private List<SMSRecord> parseJsonData(String jsonData) {
        List<SMSRecord> smsRecords = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                SMSRecord smsRecord = new SMSRecord();
                smsRecord.setDate(jsonObject.getLong("date"));
                //smsRecord.setThreadId(jsonObject.getInt("thread_id"));
                //smsRecord.setDateSent(jsonObject.getLong("date_sent"));

                smsRecords.add(smsRecord);


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return smsRecords;
    }
    private List<SMSRecord> handle_data(Uri filename) {
        List<SMSRecord> metric = new ArrayList<>();
        try {
            InputStream inputStream = getContentResolver().openInputStream(filename);

            // Read the JSON data from the input stream
            String jsonData = readJsonDataFromInputStream(inputStream);

            // Parse the JSON data and store it in a collection
            metric = parseJsonData(jsonData);

            Collections.sort(metric, new Comparator<SMSRecord>() {
                @Override
                public int compare(SMSRecord record1, SMSRecord record2) {
                    // Compare the dates of the two SMSRecord objects
                    if (record1.getDate() < record2.getDate()) {
                        return -1;
                    } else if (record1.getDate() > record2.getDate()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            Log.i("sms data handle","no"+ metric.size());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return metric;
    }


}
