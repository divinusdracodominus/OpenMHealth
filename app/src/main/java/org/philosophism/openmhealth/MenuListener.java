package org.philosophism.openmhealth;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

public class MenuListener implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navView;
    Context context;

    MenuListener(Context context, NavigationView navView) {
        this.context = context;
        this.navView = navView;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        navView.setVisibility(View.GONE);
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_activity_tracker) {
            Intent tracker = new Intent(context, StartTracker.class);
            context.startActivity(tracker);
        } else if (id == R.id.nav_home) {
            Intent home = new Intent(context, MainActivity.class);
            context.startActivity(home);
        } else if (id == R.id.nav_results) {

            // Create an intent to open the file explorer
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*"); // Set the MIME type to allow all file types
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            // Start the activity with the created intent
            context.startActivity(Intent.createChooser(intent, "Select File"));


        } else if (id == R.id.nav_metrics) {
            Intent i = new Intent(context, DataManager.class);
            context.startActivity(i);
        } else if (id == R.id.nav_events) {

        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_view);
        //drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
