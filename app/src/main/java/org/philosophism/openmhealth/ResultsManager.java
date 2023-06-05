package org.philosophism.openmhealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ResultsManager extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_results_page);

        Button button = findViewById(R.id.filePickerButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to open the file explorer
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*"); // Set the MIME type to allow all file types
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // Start the activity with the created intent
                startActivity(Intent.createChooser(intent, "Select File"));
            }
        });
    }
}
