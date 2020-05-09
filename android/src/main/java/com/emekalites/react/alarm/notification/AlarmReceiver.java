package com.emekalites.react.alarm.notification;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.ArrayList;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();

    AlarmModel alarm;

    int id;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final AlarmDatabase alarmDB = new AlarmDatabase(context);
            AlarmUtil alarmUtil = new AlarmUtil((Application) context.getApplicationContext());

            try {
                String intentType = intent.getExtras().getString("intentType");
                if (Constants.ADD_INTENT.equals(intentType)) {
                    id = intent.getExtras().getInt("PendingId");

                    try {
                        alarm = alarmDB.getAlarm(id);

                        alarmUtil.sendNotification(alarm);

                        String scheduleType = alarm.getScheduleType();
                        if (scheduleType.equals("once")) {
                            alarm.setActive(0);
                            alarmDB.update(alarm);
                        }

                        ArrayList<AlarmModel> alarms = alarmDB.getAlarmList(1);
                        alarmUtil.setBootReceiver();

                        Log.e(TAG, "alarm start: " + alarm.toString() + ", alarms left: " + alarms.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            String action = intent.getAction();
            if (action != null) {
                Log.e(TAG, "ACTION: " + action);
                switch (action) {
                    case Constants.NOTIFICATION_ACTION_SNOOZE:
                        id = intent.getExtras().getInt("SnoozeAlarmId");

                        try {
                            alarm = alarmDB.getAlarm(id);
                            alarmUtil.snoozeAlarm(alarm);
                            Log.e(TAG, "alarm snoozed: " + alarm.toString());

                            alarmUtil.stopAlarmSound();
                            alarmUtil.removeFiredNotification(alarm.getAlarmId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    case Constants.NOTIFICATION_ACTION_DISMISS:
                        id = intent.getExtras().getInt("AlarmId");

                        try {
                            alarm = alarmDB.getAlarm(id);
                            alarmUtil.cancelAlarm(alarm);
                            Log.e(TAG, "alarm cancelled: " + alarm.toString());

                            // emit notification dismissed
                            ANModule.getReactAppContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("OnNotificationDismissed", "{\"id\": \"" + alarm.getAlarmId() + "\"}");

                            alarmUtil.stopAlarmSound();
                            alarmUtil.removeFiredNotification(alarm.getAlarmId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }
    }
}
