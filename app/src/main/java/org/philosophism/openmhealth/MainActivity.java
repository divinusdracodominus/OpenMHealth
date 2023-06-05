package org.philosophism.openmhealth;

import androidx.activity.result.ActivityResult;
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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    final int PERMISSION_REQUEST = 2252;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean firstRun = sharedPref.getBoolean("firstRun", true);
        if(firstRun) {
            editor.putBoolean("firstRun", false);
            editor.putString("device_id", UUID.randomUUID().toString());
        }

        Intent intent = new Intent(MainActivity.this, PermissionRequest.class);
        intent.putExtra("permission", Manifest.permission.READ_SMS);
        intent.putExtra("usage", "tracking frequency of communications");

        ActivityResultLauncher resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()== Activity.RESULT_OK)
                {
                    Log.i("OpenMHealth", "callback triggered");
                    Intent intent = result.getData();
                    if(intent.getBooleanExtra("granted", false)) {
                        Log.i("OpenMHealth", "permission wasn'ted granted");
                    }else {
                        Log.i("OpenMHealth", "permission was granted");
                    }
                }
            }
        });

        Button smsBtn = findViewById(R.id.smspermissionbtn);
        smsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("OpenMhealth", "button clicked");

                Intent i = new Intent(MainActivity.this, DataManager.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PERMISSION_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}