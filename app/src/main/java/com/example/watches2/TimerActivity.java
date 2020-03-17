package com.example.watches2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import dalbers.com.timerpicker.TimerPickerDialogFragment;
import dalbers.com.timerpicker.TimerPickerDialogListener;

public class TimerActivity extends AppCompatActivity {
    private TimerService service;

    private int topTime;

    private boolean wasRunning;
    boolean stopWasPressed = false;
    ArrayList<Integer> circleList = new ArrayList<>();
    ScrollView mScrollView;

    String textFromCircleListForSaving = null;

    TextView timeSek;
    TextView circles;
    Button btnPause;
    String buttonText = null;

    Bundle savedInstanceState = null;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        wasRunning = service.getState();
        service.stopTimer();
    }
    @Override
    public void onResume() {
        super.onResume();
        if(service != null){
            if(wasRunning){
                service.startTimer();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        this.savedInstanceState = savedInstanceState;

        Intent intent = new Intent(this, TimerService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);


        if(savedInstanceState != null){
            topTime = savedInstanceState.getInt("topTime");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
            buttonText = savedInstanceState.getString("buttonText");
            textFromCircleListForSaving = savedInstanceState.getString("circlesString");
            circleList = savedInstanceState.getIntegerArrayList("arrayCircleList");
            stopWasPressed = savedInstanceState.getBoolean("onKeyPressed");
        }

        timeSek = findViewById(R.id.clock_face);
        circles = findViewById(R.id.circle_list);
        mScrollView = findViewById(R.id.scrollView);

        Button btnStart = findViewById(R.id.start_button);
        btnPause = findViewById(R.id.pause_button);
        Button btnStop = findViewById(R.id.stop_button);
        Button btnCircle = findViewById(R.id.circle_button);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStart();
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPause();
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStop();
            }
        });
        btnCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCircle();
            }
        });

        //to save button Pause/Resume
        if(buttonText != null){
            btnPause.setText(buttonText);
        }
        if(textFromCircleListForSaving != null){
            circles.setText(textFromCircleListForSaving);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    public void onClickStart(){
        if(service.getCounter() > 0){
            if(stopWasPressed){
                circles.setText("Кола:");
                stopWasPressed = false;
            }
            service.startTimer();
            btnPause.setText("Пауза");
        }
    }

    public void onClickPause(){
        if(service.getCounter() > 0){
            if(service.getState()){
                service.stopTimer();
                btnPause.setText("Продовжити");
            }else if(!stopWasPressed){
                service.startTimer();
                btnPause.setText("Пауза");
            }
        }
    }

    public void onClickStop(){
        circleList = new ArrayList<>();
        stopWasPressed = true;
        service.stopTimer();
        service.setCounter(topTime);
        btnPause.setText("Пауза");
    }

    public void onClickCircle(){
        if(!service.getState()){
            return;
        }
        int number = circleList.size() + 1;
        if(number == 1){
            circleList.add(topTime - service.getCounter());
            circles.append("\n" + number + ". " + (topTime - service.getCounter()) + " секунд");
        }else{
            Iterator<Integer> iterator = circleList.iterator();
            int count = 0;
            while(iterator.hasNext()){
                count += iterator.next();
            }
            circleList.add(topTime - service.getCounter() - count);
            circles.append("\n" + number + ". " + (topTime - service.getCounter() - count) + " секунд");
        }
        if ((circles.getMeasuredHeight() - mScrollView.getScrollY()) <= (mScrollView.getHeight() + circles.getLineHeight())) {
            scrollToBottom();
        }
    }

    private void scrollToBottom()
    {
        mScrollView.post(new Runnable()
        {
            public void run()
            {
                mScrollView.smoothScrollTo(0, circles.getBottom());
            }
        });
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("seconds", service.getCounter());
        outState.putInt("topTime", topTime);
        outState.putBoolean("wasRunning", wasRunning);
        outState.putString("buttonText", btnPause.getText().toString());
        outState.putString("circlesString", circles.getText().toString());
        outState.putIntegerArrayList("arrayCircleList", circleList);
        outState.putBoolean("onKeyPressed", stopWasPressed);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((TimerService.CustomBinder) binder).getService();
            Subject subject = new Subject();
            subject.attach(new Observer() {
                @Override
                public void act() {
                    Toast.makeText(TimerActivity.this, "Timer has finished!", Toast.LENGTH_LONG).show();
                    service.stopTimer();
                }
            }, "richedZero");
            subject.attach(new Observer() {
                @Override
                public void act() {
                    int counterLocal = service.getCounter();
                    int hours = counterLocal/3600;
                    int minutes = (counterLocal%3600)/60;
                    int secs = counterLocal%60;
                    String time = String.format(Locale.getDefault(),"%d:%02d:%02d", hours, minutes, secs);
                    timeSek.setText(time);
                }
            }, "changedValue");
            if(savedInstanceState != null){
                service.setCounter(savedInstanceState.getInt("seconds"));
                service.setState(savedInstanceState.getBoolean("wasRunning"));
            }else{
                topTime = 10;
                service.setCounter(topTime);
            }
            service.setSubject(subject);
            service.runTimer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timer_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // <span lang="ru-RU">Обработчик нажатия на кнопку или пункт меню</span>
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSettings() {
        TimerPickerDialogFragment timerDialog = new TimerPickerDialogFragment();
        timerDialog.show(getSupportFragmentManager(), "TimerPickerDialog");
        //timerDialog.
        timerDialog.setDialogListener(new TimerPickerDialogListener() {
            @Override
            public void timeSet(long timeInMillis) {
                topTime = (int)(timeInMillis/1000);
                service.setCounter(topTime);
            }

            @Override
            public void dialogCanceled() {
                Log.d("TimerPickerDialog","Cancelled");
            }
        });
    }
}
