package org.philosophism.openmhealth.utils;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import org.philosophism.openmhealth.AppStatsCollector;
import org.philosophism.openmhealth.DataManager;
import org.philosophism.openmhealth.MainActivity;
import org.philosophism.openmhealth.R;
import org.philosophism.openmhealth.ResultsManager;
import org.philosophism.openmhealth.Settings;
import org.philosophism.openmhealth.StartTracker;

public class MenuListener implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navView;
    Context context;

    public MenuListener(Context context, NavigationView navView) {
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
        } /*else if (id == R.id.nav_results) {
            Intent a = new Intent(context, ResultsManager.class);
            context.startActivity(a);

        } */else if (id == R.id.nav_metrics) {
            Intent i = new Intent(context, DataManager.class);
            context.startActivity(i);
        } /*else if (id == R.id.nav_events) {
            //Intent openMap = new Intent(context, EventMap.class);
            //context.startActivity(openMap);
        } */ else if(id == R.id.usagestats) {
            Intent i = new Intent(context, AppStatsCollector.class);
            context.startActivity(i);
        }else if(id == R.id.nav_settings) {
            Intent i = new Intent(context, Settings.class);
            context.startActivity(i);
        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_view);
        //drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
