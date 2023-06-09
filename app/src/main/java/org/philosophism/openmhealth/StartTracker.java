package org.philosophism.openmhealth;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavAction;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.SleepSegmentRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import org.philosophism.openmhealth.activitytracker;

public class StartTracker extends AppCompatActivity {

    NavigationView navView;
    List<ActivityTransition> transitions = new ArrayList<>();

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    boolean track_sleep;
    boolean track_walking;
    boolean track_travel;
    boolean track_excercise;

    private final String tag = "MHealth StartTracker";

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.i("OpenmHealth", "permission granted");

                } else {
                    Log.i("OpenmHealth", "permission is not granted");
                    /// should show reason why its required
                }
            });

    void subscribeToSleepData(Context context, PendingIntent intent) {

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACTIVITY_RECOGNITION) ==
                PackageManager.PERMISSION_GRANTED) {
            /// can also be used to requestActivityChangeUpdate
            Task task = ActivityRecognition.getClient(context).requestSleepSegmentUpdates(intent, SleepSegmentRequest.getDefaultSleepSegmentRequest());
            task.addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Log.i(tag, "sucessfully started sleep tracker listener");
                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(tag, "failed to subscribe to sleep segment listener: " + e.getMessage());
                }
            });
        }else{
            requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
        }
    }

    void unSubscribeToSleepData(Context context, PendingIntent intent) {
        Task task = ActivityRecognition.getClient(context).removeSleepSegmentUpdates(intent);

        task.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Log.i(tag, "sucessfully stopped sleep tracker listener");
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(tag, "failed to unsubscribe to sleep segment listener: " + e.getMessage());
            }
        });
    }

    void subscribeToActivity(Context context, PendingIntent intent) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            Task task = ActivityRecognition.getClient(context).requestActivityUpdates(DetectedActivity.IN_VEHICLE, intent);

        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_tracker);

        sharedPref = StartTracker.this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        track_sleep = sharedPref.getBoolean("sleeptracking", false);
        track_walking = sharedPref.getBoolean("track_walking", false);
        track_travel = sharedPref.getBoolean("track_driving", false);
        track_excercise = sharedPref.getBoolean("track_running", false);

        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new MenuListener(this, navView));

        Button menuBtn = findViewById(R.id.open_menu);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(navView.getVisibility() == View.GONE) {
                    navView.setVisibility(View.VISIBLE);
                }else{
                    navView.setVisibility(View.GONE);
                }
            }
        });

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox sleep = findViewById(R.id.sleepbtn);
                CheckBox walkbtn = findViewById(R.id.walkbtn);
                CheckBox runbtn = findViewById(R.id.runbtn);
                CheckBox drivebtn = findViewById(R.id.drivingbtn);

                if(sleep.isChecked() && track_sleep == false) {
                    Log.i("OpenMHealth", "sleep data checked");
                    subscribeToSleepData(getApplicationContext(), sleeptracker.createSleepReceiverPendingIntent(StartTracker.this));
                    editor.putBoolean("sleeptracking", true);
                    track_sleep = true;
                }else if(sleep.isChecked() && track_sleep) {
                    unSubscribeToSleepData(getApplicationContext(), sleeptracker.createSleepReceiverPendingIntent(StartTracker.this));
                    editor.putBoolean("track_sleep", false);
                    track_sleep = false;
                }else if(walkbtn.isChecked() && track_walking == false) {
                    subscribeToActivity(getApplicationContext(), activitytracker.createTrackerReceiverPendingIntent(StartTracker.this));
                    track_walking = true;
                }else if(walkbtn.isChecked() && track_walking) {

                }
            }
        });
    }
}