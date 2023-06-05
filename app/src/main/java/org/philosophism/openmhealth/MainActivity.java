package org.philosophism.openmhealth;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    final int PERMISSION_REQUEST = 2252;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);





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
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_results) {

                    // Create an intent to open the file explorer
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*"); // Set the MIME type to allow all file types
                    intent.addCategory(Intent.CATEGORY_OPENABLE);

                    // Start the activity with the created intent
                    startActivity(Intent.createChooser(intent, "Select File"));


        } else if (id == R.id.nav_metrics) {

        } else if (id == R.id.nav_events) {

        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_view);
        //drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}