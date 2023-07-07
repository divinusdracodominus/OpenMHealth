package org.philosophism.openmhealth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.philosophism.openmhealth.R;
import org.philosophism.openmhealth.utils.CheckBoxAdapter;
import org.philosophism.openmhealth.utils.MenuListener;
import org.philosophism.openmhealth.utils.Metric;
import org.philosophism.openmhealth.utils.Utils;

import java.util.UUID;

public class Settings extends AppCompatActivity {

    NavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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

        Metric calendar = Metric.calendars;
        if(Utils.hasPermission(this, calendar.permission)) {
            try {
                String[] calendar_list = Utils.getField(CalendarContract.Calendars.NAME, "unnamed calendar", calendar.fetch(this, UUID.randomUUID()));
                RecyclerView calendar_views = findViewById(R.id.calendarsview);

                CheckBoxAdapter adapter = new CheckBoxAdapter(calendar_list);

                calendar_views.setLayoutManager(new LinearLayoutManager(Settings.this));
                calendar_views.setAdapter(adapter);

            }catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }
}