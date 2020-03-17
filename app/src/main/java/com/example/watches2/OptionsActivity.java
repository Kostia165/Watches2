package com.example.watches2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OptionsActivity extends AppCompatActivity {

    private Spinner timezone_spinner;
    List<String> TIMEZONES = new ArrayList<>();

    String timezone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Intent intent = getIntent();
        if(intent != null){
            timezone = intent.getStringExtra(TickerActivity.ARG1);
        }

        List<String> tempTimezones = Arrays.asList(TimeZone.getAvailableIDs());
        Iterator<String> iterator = tempTimezones.iterator();
        Pattern pattern = Pattern.compile("^[a-zA-Z]+/[a-zA-z]+$");
        while(iterator.hasNext()){
            String text = iterator.next();
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                TIMEZONES.add(text);
            }
        }

        //set up spinner
        timezone_spinner = findViewById(R.id.spinner_timezone);
        setupTimezoneSpinner();

        //work with submit button
        Button buttonSubmit = findViewById(R.id.submit_button);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSubmit();
            }
        });
    }

    private void onClickSubmit() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(TickerActivity.ARG1, timezone_spinner.getSelectedItem().toString());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void setupTimezoneSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.item_group,
                TIMEZONES
        );
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        timezone_spinner.setAdapter(adapter);
        if(timezone != null){
            int index = TIMEZONES.indexOf(timezone);
            if(index != -1)timezone_spinner.setSelection(index);
        }
    }
}
