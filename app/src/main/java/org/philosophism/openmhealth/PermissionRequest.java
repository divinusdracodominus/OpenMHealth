package org.philosophism.openmhealth;

import org.json.JSONObject;
import org.philosophism.openmhealth.Metric;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PermissionRequest extends AppCompatActivity {

    String permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_request);

        Intent i = getIntent();
        permission = i.getStringExtra("permission");
        String usage = i.getStringExtra("usage");

        TextView usage_msg = (TextView)findViewById(R.id.usageView);
        usage_msg.setText(usage);

        Button accept = (Button) findViewById(R.id.acceptbtn);
        Button reject = (Button) findViewById(R.id.rejectbtn);
        
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission(permission);
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle_result(false);
            }
        });
        requestPermission(permission);
    }
    private ActivityResultLauncher<String> requestPermissionLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                Log.i("OpenmHealth", "permission granted");
                handle_result(true);
            } else {
                Log.i("OpenmHealth", "permission is not granted");

            }
        });
    void requestPermission(String permission) {

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), permission) ==
                PackageManager.PERMISSION_GRANTED) {
            handle_result(true);
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.

            requestPermissionLauncher.launch(
                    permission);
        }
    }

    public void handle_result(boolean granted) {
        Intent res = new Intent();
        res.putExtra("action", "permission request");
        res.putExtra("permission", permission);
        res.putExtra("granted", granted);
        setResult(Activity.RESULT_OK, res);
        this.finish();
    }
}