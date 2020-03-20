package com.example.watches2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TickerActivity extends AppCompatActivity{
    final static String ARG1 = "TIMEZONE";

    String timezone = "";

    TextView hoursView;
    TextView yearView;
    TextView timezoneView;

    Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticker);

        if(savedInstanceState != null){
            timezone = savedInstanceState.getString(ARG1);
        }else{
            timezone = getString(R.string.ValueTimezone);
        }

        hoursView = findViewById(R.id.timeView);
        yearView = findViewById(R.id.yearView);
        timezoneView = findViewById(R.id.timezoneView);

        Button timerBtn = findViewById(R.id.timer_button);
        Button optionsBtn = findViewById(R.id.options_button);
        
        timerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTimer();
            }
        });
        optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickOptions();
            }
        });

        timezoneView.setText(getString(R.string.TitleTimezone) + timezone);
        GregorianCalendar date = new GregorianCalendar(TimeZone.getTimeZone(timezone));
        if (date != null){
            hoursView.setText(formatHours(date));
            yearView.setText(formatYear(date));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        t.interrupt();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startClock();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG1, timezone);
    }

    private void onClickTimer() {
        Intent intent = new Intent(this, TimerActivity.class);
        startActivity(intent);
    }

    private void onClickOptions() {
        Intent intent = new Intent(this, OptionsActivity.class);
        intent.putExtra(ARG1, timezone);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {return;}
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                timezone = data.getStringExtra(ARG1);
                timezoneView.setText(getString(R.string.TitleTimezone) + timezone);
            }
        }
    }

    public void startClock(){
        t = new Thread(){
            @Override
            public void run(){
                try {
                    while(!isInterrupted()){
                        Thread.sleep(1000);  //1000ms = 1 sec
                        if(this == null)
                            return;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GregorianCalendar date = new GregorianCalendar(TimeZone.getTimeZone(timezone));
                                if (date != null){
                                    hoursView.setText(formatHours(date));
                                    yearView.setText(formatYear(date));
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        t.start();
    }

    public static String formatYear(GregorianCalendar calendar) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
        fmt.setCalendar(calendar);
        String dateFormatted = fmt.format(calendar.getTime());

        return dateFormatted;
    }

    public static String formatHours(GregorianCalendar calendar) {
        SimpleDateFormat fmt = new SimpleDateFormat("hh:mm:ss a");
        fmt.setCalendar(calendar);
        String dateFormatted = fmt.format(calendar.getTime());

        return dateFormatted;
    }
}
