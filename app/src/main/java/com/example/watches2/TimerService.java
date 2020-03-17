package com.example.watches2;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class TimerService extends Service {
    private CustomBinder binder = new CustomBinder();

    private int counter;
    private int times;
    private boolean running;
    Handler handler;

    private Subject subject;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public void runTimer(){
        handler = new Handler();
        times = 0;
        handler.post(new Runnable() {
            @Override
            public void run() {
                subject.notifyObservers("changedValue");
                if (running) {
                    times++;
                    if(times == 41){
                        times = 0;
                    }
                    if(times == 40){
                        counter--;
                        if(counter == 0){
                            subject.notifyObservers("richedZero");
                        }
                    }
                }
                handler.postDelayed(this, 25);
            }
        });
    }

    public Boolean getState(){
        return running;
    }

    public void setState(Boolean b){
        running = b;
    }

    public void startTimer(){
        running = true;
    }

    public void stopTimer(){
        running = false;
    }

    public void setCounter(int cnt){
        this.counter = cnt;
    }

    public int getCounter(){
        return counter;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        handler.removeCallbacks(null);
        return super.onUnbind(intent);
    }

    public class CustomBinder extends Binder {
        public TimerService getService(){
            return TimerService.this;
        }
    }
}
