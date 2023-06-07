package org.philosophism.openmhealth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

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

import org.philosophism.openmhealth.activitytracker;

public class StartTracker extends AppCompatActivity {

    List<ActivityTransition> transitions = new ArrayList<>();
    PendingIntent sleepPendingIntent;
    private final String tag = "MHealth StartTracker";
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


        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.IN_VEHICLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.IN_VEHICLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        ActivityTransitionRequest request = new ActivityTransitionRequest(transitions);



    }
}