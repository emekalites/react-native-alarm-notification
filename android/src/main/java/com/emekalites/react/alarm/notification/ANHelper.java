package com.emekalites.react.alarm.notification;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by emnity on 6/25/17.
 */

public class ANHelper {
    private static final long DEFAULT_VIBRATION = 100;
    private static final String TAG = ANHelper.class.getSimpleName();
    private final static String PREFERENCES_KEY = "ReactNativeAlarmNotification";
    private Context mContext;
    private SharedPreferences sharedPreferences = null;
    private static Ringtone ringtone;

    public ANHelper(Application context) {
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public Class getMainActivityClass() {
        String packageName = mContext.getPackageName();
        Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
        String className = launchIntent.getComponent().getClassName();
        Log.e(TAG, className);
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private Uri getAlarmUri() {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }

    public int getRandomNumber(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1)) + min;
    }

    private PendingIntent createOnDismissedIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, ANDismissReceiver.class);
        intent.putExtra("com.emekalites.react.alarm.notification.notificationId", notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), notificationId, intent, 0);
        return pendingIntent;
    }

    /*
    *  Send notification after alarm rings and remove it from re-scheduling
    */
    public void sendNotification(Bundle bundle){
        try {
            Class intentClass = getMainActivityClass();
            if (intentClass == null) {
                Log.e(TAG, "No activity class found for the notification");
                return;
            }

            // title
            String title = bundle.getString("title");
            if (title == null) {
                ApplicationInfo appInfo = mContext.getApplicationInfo();
                title = mContext.getPackageManager().getApplicationLabel(appInfo).toString();
            }

            // notification id
            String notificationIdString = bundle.getString("id");
            if (notificationIdString == null) {
                notificationIdString = String.valueOf(getRandomNumber(1000, 9999)); // quick fix
            }

            int notificationID;
            try {
                notificationID = Integer.parseInt(notificationIdString);
            } catch (Exception e){
                notificationIdString = String.valueOf(getRandomNumber(1000, 9999));
                notificationID = Integer.parseInt(notificationIdString);
            }

            // message
            if (bundle.getString("message") == null) {
                Log.d(TAG, "Cannot send to notification centre because there is no 'message' field in: " + bundle);
                return;
            }

            // channel id
            String channelString = bundle.getString("channel");
            if (channelString == null) {
                Log.d(TAG, "Cannot send to notification centre because there is no 'channel' field in: " + bundle);
                return;
            }

            Resources res = mContext.getResources();
            String packageName = mContext.getPackageName();

            //icon
            int smallIconResId;
            String smallIcon = bundle.getString("small_icon");
            if (smallIcon != null) {
                smallIconResId = res.getIdentifier(smallIcon, "mipmap", packageName);
            }
            else {
                smallIconResId = res.getIdentifier("ic_launcher", "mipmap", packageName);
            }

            Intent resultIntent = new Intent(mContext, intentClass);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            resultIntent.putExtras(bundle);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, notificationID, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(mContext, channelString)
                            .setSmallIcon(smallIconResId)
                            .setContentTitle(title)
                            .setTicker(bundle.getString("ticker"))
                            .setContentText(bundle.getString("message"))
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setCategory(NotificationCompat.CATEGORY_ALARM)
                            .setAutoCancel(bundle.getBoolean("auto_cancel", true))
                            .setSound(null)
                            .setVibrate(null)
                            .setDeleteIntent(createOnDismissedIntent(mContext, notificationID));

            //large icon
            String largeIcon = bundle.getString("large_icon");
            if(largeIcon != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                int largeIconResId = res.getIdentifier(largeIcon, "mipmap", packageName);
                Bitmap largeIconBitmap = BitmapFactory.decodeResource(res, largeIconResId);

                if (largeIconResId != 0) {
                    mBuilder.setLargeIcon(largeIconBitmap);
                }
            }

            //color
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setCategory(NotificationCompat.CATEGORY_CALL);

                String color = bundle.getString("color");
                if (color != null) {
                    mBuilder.setColor(Color.parseColor(color));
                }
            }

            NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder.setContentIntent(resultPendingIntent);

            // tag
            if (bundle.containsKey("tag")) {
                String tag = bundle.getString("tag");
                mNotificationManager.notify(tag, notificationID, mBuilder.build());
            } else {
                mNotificationManager.notify(notificationID, mBuilder.build());
            }


            //vibrate
            if (!bundle.containsKey("vibrate") || bundle.getBoolean("vibrate")) {
                long vibration = bundle.containsKey("vibration") ? (long) bundle.getDouble("vibration") : DEFAULT_VIBRATION;
                if (vibration == 0)
                    vibration = DEFAULT_VIBRATION;
                Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(vibration);
            }

            //sound
            if (!bundle.containsKey("play_sound") || bundle.getBoolean("play_sound")) {
                Uri soundUri = getAlarmUri();
                String soundName = bundle.getString("sound_name");
                if (soundName != null) {
                    if (!"default".equalsIgnoreCase(soundName)) {
                        int resId;
                        if (mContext.getResources().getIdentifier(soundName, "raw", mContext.getPackageName()) != 0) {
                            resId = mContext.getResources().getIdentifier(soundName, "raw", mContext.getPackageName());
                        } else {
                            soundName = soundName.substring(0, soundName.lastIndexOf('.'));
                            resId = mContext.getResources().getIdentifier(soundName, "raw", mContext.getPackageName());
                        }

                        soundUri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + resId);
                    }
                }
                try {
                    ringtone = RingtoneManager.getRingtone(mContext, soundUri);
                    ringtone.play();
                } catch (Exception e){
                    Log.e(TAG, "failed to play ringtone", e);
                }
            }

            // cancel alarm
            cancelAlarm(notificationIdString);

            //clear out one time scheduled notification once fired
            if(bundle.containsKey("schedule_once") && bundle.getBoolean("schedule_once")) {
                Log.e(TAG, "clear out one time scheduled notification once fired");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(notificationIdString);
                editor.apply();
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to send notification", e);
        }
    }

    /*
    *  Schedule alarm
    */
    public void scheduleAlarm(Bundle bundle) {
        Log.e(TAG, "schedule alarm");

        Class intentClass = getMainActivityClass();
        if (intentClass == null) {
            return;
        }

        String notificationIdString = bundle.getString("id");
        if(notificationIdString == null){
            notificationIdString = String.valueOf(getRandomNumber(1000, 9999)); // quick fix
        }

        String fireDate = bundle.getString("fire_date");
        if (fireDate == null) {
            Log.e(TAG, "failed to schedule notification because fire date is missing");
            return;
        }
        Log.e(TAG, fireDate);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
        Date date = null;
        Long fd = null;
        try {
            date = sdf.parse(fireDate);
            fd = date.getTime();
        } catch (ParseException e){
            e.printStackTrace();
            return;
        } catch (Exception e){
            e.printStackTrace();
            return;
        }

        Intent intent = new Intent(mContext, ANAlarmReceiver.class);
        intent.putExtras(bundle);

		int notificationID;
		try {
			notificationID = Integer.parseInt(notificationIdString);
		} catch (Exception e){
			notificationIdString = String.valueOf(getRandomNumber(1000, 9999));
			notificationID = Integer.parseInt(notificationIdString);
		}
		
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.e(TAG, "fd: "+fd);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getAlarmManager().setExact(AlarmManager.RTC_WAKEUP, fd, pendingIntent);
        }else {
            getAlarmManager().set(AlarmManager.RTC_WAKEUP, fd, pendingIntent);
        }

        //store intent
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            JSONObject json = BundleJSONConverter.convertToJSON(bundle);
            editor.putString(notificationIdString, json.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cancelAlarm(String notificationIdString) {
        int notificationID = Integer.parseInt(notificationIdString);
        Intent notificationIntent = new Intent(mContext, ANAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, notificationID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        getAlarmManager().cancel(pendingIntent);
    }

    public void cancelNotification(String notificationId) {
        cancelAlarm(notificationId);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(notificationId);
        editor.apply();
    }

    public void cancelAllNotifications() {
        java.util.Map<String, ?> keyMap = sharedPreferences.getAll();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(java.util.Map.Entry<String, ?> entry:keyMap.entrySet()){
            cancelAlarm(entry.getKey());
        }
        editor.clear();
        editor.apply();
    }

    public void removeFiredNotification(String notificationId){
        getNotificationManager().cancel(Integer.parseInt(notificationId));
    }

    public void removeAllFiredNotifications(){
        getNotificationManager().cancelAll();
    }

    public ArrayList<Bundle> getScheduledAlarms(){
        ArrayList<Bundle> array = new ArrayList<Bundle>();
        java.util.Map<String, ?> keyMap = sharedPreferences.getAll();
        for(java.util.Map.Entry<String, ?> entry:keyMap.entrySet()){
            try {
                JSONObject json = new JSONObject((String)entry.getValue());
                Bundle bundle = BundleJSONConverter.convertToBundle(json);
                array.add(bundle);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    /*
    *  Stop alarm sound
    */
    public void stopAlarm() {
        try {
            ringtone.stop();
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "ringtone: "+ e.getMessage());
        }
    }
}
