package com.emekalites.react.alarm.notification;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by emnity on 6/24/17.
 */

public class ANAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = ANAlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, ANService.class);
        service.putExtras(intent);
        context.startService(service);
    }
}
