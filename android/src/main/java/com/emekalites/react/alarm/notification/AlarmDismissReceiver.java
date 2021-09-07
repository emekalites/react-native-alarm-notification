package com.emekalites.react.alarm.notification;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.facebook.react.modules.core.DeviceEventManagerModule;

public class AlarmDismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmUtil alarmUtil = new AlarmUtil((Application) context.getApplicationContext());
        try {
            int notificationId = intent.getExtras().getInt(Constants.DISMISSED_NOTIFICATION_ID);

            if (ANModule.getReactAppContext() != null) {
                ANModule.getReactAppContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("OnNotificationDismissed", "{\"id\": \"" + notificationId + "\"}");
            }

            alarmUtil.removeFiredNotification(notificationId);
            alarmUtil.doCancelAlarm(notificationId);
        } catch (Exception e) {
            alarmUtil.stopAlarmSound();
            System.err.println("Exception when handling notification dismiss. " + e);
        }
    }
}
