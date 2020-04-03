package com.emekalites.react.alarm.notification;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

public class AlarmBootReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmBootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.e(TAG, "app killed or rebooted");
            final AlarmDatabase alarmDB = new AlarmDatabase(context);
            ArrayList<AlarmModel> alarms = alarmDB.getAlarmList(1);

            try {
                AlarmUtil alarmUtil = new AlarmUtil((Application) context);

                for (AlarmModel alarm : alarms) {
                    alarmUtil.setAlarm(alarm);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
