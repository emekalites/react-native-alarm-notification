package com.emekalites.react.alarm.notification;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Date;

/**
 * Created by emnity on 6/25/17.
 */

public class ANService extends Service {
    private static final String TAG = ANService.class.getSimpleName();

    private boolean isRunning;
    private Thread backgroundThread;
    private Intent intent;

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }

    private Runnable myTask = new Runnable() {
        public void run() {
            new ANHelper((Application) getApplicationContext()).sendNotification(intent.getExtras());
            stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "event received in service: " + new Date().toString());
        this.intent = intent;
        if(!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }
}
