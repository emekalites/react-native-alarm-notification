package com.emekalites.react.alarm.notification;

import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import static com.emekalites.react.alarm.notification.Constants.ADD_INTENT;
import static com.emekalites.react.alarm.notification.Constants.NOTIFICATION_ACTION_DISMISS;
import static com.emekalites.react.alarm.notification.Constants.NOTIFICATION_ACTION_SNOOZE;

class AlarmUtil {
    private static final String TAG = AlarmUtil.class.getSimpleName();

    private Context mContext;
    private AudioInterface audioInterface;
    private static final long DEFAULT_VIBRATION = 100;

    AlarmUtil(Application context) {
        mContext = context;

        audioInterface = AudioInterface.getInstance();
        audioInterface.init(mContext);
    }

    private Class getMainActivityClass() {
        String packageName = mContext.getPackageName();
        Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
        String className = launchIntent.getComponent().getClassName();
        Log.e(TAG, "main activity classname: " + className);
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

    private AlarmDatabase getAlarmDB() {
        return new AlarmDatabase(mContext);
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void playAlarmSound(String name, String names, boolean shouldLoop) {
        MediaPlayer mediaPlayer = audioInterface.getSingletonMedia(name, names);
        mediaPlayer.setLooping(shouldLoop);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    mp.stop();
                    mp.reset();
                    mp.release();
                    Log.e(TAG, "release media player");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    boolean checkAlarm(ArrayList<AlarmModel> alarms, AlarmModel alarm) {
        boolean contain = false;
        for (AlarmModel aAlarm : alarms) {
            if (aAlarm.getHour() == alarm.getHour() && aAlarm.getMinute() == alarm.getMinute() && aAlarm.getDay() == alarm.getDay() && aAlarm.getMonth() == alarm.getMonth() && aAlarm.getYear() == alarm.getYear()) {
                contain = true;
                break;
            }
        }

        if (contain) {
            Toast.makeText(mContext, "You have already set this Alarm", Toast.LENGTH_SHORT).show();
        }

        return contain;
    }

    void setBootReceiver() {
        ArrayList<AlarmModel> alarms = getAlarmDB().getAlarmList(1);
        if (alarms.size() > 0) {
            enableBootReceiver(mContext);
        } else {
            disableBootReceiver(mContext);
        }
    }

    void setAlarm(AlarmModel alarm) {
        alarm.setActive(1);
        getAlarmDB().update(alarm);

        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, alarm.getDay());
        calendar.set(Calendar.MONTH, alarm.getMonth() - 1);
        calendar.set(Calendar.YEAR, alarm.getYear());

        Log.e(TAG, alarm.getAlarmId() + " - " + calendar.getTime().toString());

        int alarmId = alarm.getAlarmId();

        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.putExtra("intentType", ADD_INTENT);
        intent.putExtra("PendingId", alarm.getId());

        PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, alarmId, intent, 0);
        AlarmManager alarmManager = this.getAlarmManager();

        String scheduleType = alarm.getScheduleType();

        if (scheduleType.equals("once")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            }
        } else if (scheduleType.equals("repeat")) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarm.getInterval() * 1000, alarmIntent);
        } else {
            Log.d(TAG, "Schedule type should either be once or repeat");
            return;
        }

        this.setBootReceiver();
    }

    void snoozeAlarm(AlarmModel alarm) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, alarm.getDay());
        calendar.set(Calendar.MONTH, alarm.getMonth() - 1);
        calendar.set(Calendar.YEAR, alarm.getYear());

        // set snooze interval
        calendar.add(Calendar.MINUTE, alarm.getSnoozeInterval());

        alarm.setMinute(calendar.get(Calendar.MINUTE));
        alarm.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        alarm.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        alarm.setMonth(calendar.get(Calendar.MONTH) - 1);
        alarm.setYear(calendar.get(Calendar.YEAR));

        alarm.setAlarmId((int) System.currentTimeMillis());

        getAlarmDB().update(alarm);

        Log.e(TAG, "snooze data - " + alarm.toString());

        int alarmId = alarm.getAlarmId();

        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.putExtra("intentType", ADD_INTENT);
        intent.putExtra("PendingId", alarm.getId());

        PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, alarmId, intent, 0);
        AlarmManager alarmManager = this.getAlarmManager();

        String scheduleType = alarm.getScheduleType();

        if (scheduleType.equals("once")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            }
        } else if (scheduleType.equals("repeat")) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarm.getInterval() * 1000, alarmIntent);
        } else {
            Log.d(TAG, "Schedule type should either be once or repeat");
        }
    }

    void doCancelAlarm(String id) {
        try {
            AlarmModel alarm = getAlarmDB().getAlarm(Integer.parseInt(id));
            this.cancelAlarm(alarm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void cancelAlarm(AlarmModel alarm) {
        alarm.setActive(0);
        getAlarmDB().update(alarm);

        AlarmManager alarmManager = this.getAlarmManager();

        int alarmId = alarm.getAlarmId();

        Intent intent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(alarmIntent);

        this.setBootReceiver();
    }

    private void enableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        int setting = pm.getComponentEnabledSetting(receiver);
        if (setting == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }

    private void disableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    private PendingIntent createOnDismissedIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, AlarmDismissReceiver.class);
        intent.putExtra(Constants.DISMISSED_NOTIFICATION_ID, notificationId);
        return PendingIntent.getBroadcast(context.getApplicationContext(), notificationId, intent, 0);
    }

    void sendNotification(AlarmModel alarm) {
        try {
            Class intentClass = getMainActivityClass();
            if (intentClass == null) {
                Log.e(TAG, "No activity class found for the notification");
                return;
            }

            boolean playSound = alarm.isPlaySound();
            if (playSound) {
                this.playAlarmSound(alarm.getSoundName(), alarm.getSoundNames(), alarm.isLoopSound());
            }

            NotificationManager mNotificationManager = getNotificationManager();
            int notificationID = alarm.getAlarmId();

            // title
            String title = alarm.getTitle();
            if (title == null || title.equals("")) {
                ApplicationInfo appInfo = mContext.getApplicationInfo();
                title = mContext.getPackageManager().getApplicationLabel(appInfo).toString();
            }

            // message
            String message = alarm.getMessage();
            if (message == null || message.equals("")) {
                Log.d(TAG, "Cannot send to notification centre because there is no 'message' found");
                return;
            }

            // channel
            String channelID = alarm.getChannel();
            if (channelID == null || channelID.equals("")) {
                Log.d(TAG, "Cannot send to notification centre because there is no 'channel' found");
                return;
            }

            Resources res = mContext.getResources();
            String packageName = mContext.getPackageName();

            //icon
            int smallIconResId;
            String smallIcon = alarm.getSmallIcon();
            if (smallIcon != null && !smallIcon.equals("")) {
                smallIconResId = res.getIdentifier(smallIcon, "mipmap", packageName);
            } else {
                smallIconResId = res.getIdentifier("ic_launcher", "mipmap", packageName);
            }

            Intent intent = new Intent(mContext, intentClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            Bundle bundle = new Bundle();
            if (alarm.getData() != null && !alarm.getData().equals("")) {
                String [] datum = alarm.getData().split(";;");
                for (String item : datum) {
                    String [] data = item.split("==>");
                    bundle.putString(data[0], data[1]);
                }

                intent.putExtras(bundle);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, channelID)
                    .setSmallIcon(smallIconResId)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setTicker(alarm.getTicker())
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(alarm.isAutoCancel())
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setSound(null)
                    .setVibrate(null)
                    .setDeleteIntent(createOnDismissedIntent(mContext, notificationID));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String color = alarm.getColor();

                NotificationChannel mChannel = new NotificationChannel(channelID, "alarmnotif", NotificationManager.IMPORTANCE_HIGH);
                mChannel.enableLights(true);
                mChannel.enableVibration(alarm.isVibrate());
                if (color != null && !color.equals("")) {
                    mChannel.setLightColor(Color.parseColor(color));
                }
                mChannel.setVibrationPattern(new long[]{1000, 2000});
                mNotificationManager.createNotificationChannel(mChannel);
                mBuilder.setChannelId(channelID);
            }

            mBuilder.setContentIntent(pendingIntent);

            Intent dismissIntent = new Intent(mContext, AlarmReceiver.class);
            dismissIntent.setAction(NOTIFICATION_ACTION_DISMISS);
            dismissIntent.putExtra("AlarmId", alarm.getId());
            PendingIntent pendingDismiss = PendingIntent.getBroadcast(mContext, notificationID, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action dismissAction = new NotificationCompat.Action(android.R.drawable.ic_lock_idle_alarm, "DISMISS", pendingDismiss);
            mBuilder.addAction(dismissAction);

            Intent snoozeIntent = new Intent(mContext, AlarmReceiver.class);
            snoozeIntent.setAction(NOTIFICATION_ACTION_SNOOZE);
            snoozeIntent.putExtra("SnoozeAlarmId", alarm.getId());
            PendingIntent pendingSnooze = PendingIntent.getBroadcast(mContext, notificationID, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action snoozeAction = new NotificationCompat.Action(R.drawable.ic_snooze, "SNOOZE", pendingSnooze);
            mBuilder.addAction(snoozeAction);

            //use big text
            if(alarm.isUseBigText()){
                mBuilder = mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            }

            //large icon
            String largeIcon = alarm.getLargeIcon();
            if (largeIcon != null && !largeIcon.equals("") && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                int largeIconResId = res.getIdentifier(largeIcon, "mipmap", packageName);
                Bitmap largeIconBitmap = BitmapFactory.decodeResource(res, largeIconResId);
                if (largeIconResId != 0) {
                    mBuilder.setLargeIcon(largeIconBitmap);
                }
            }

            //color
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String color = alarm.getColor();
                if (color != null && !color.equals("")) {
                    mBuilder.setColor(Color.parseColor(color));
                }
            }

            //vibrate
            boolean vibrate = alarm.isVibrate();
            if (vibrate) {
                long vibration = (long) alarm.getVibration();
                if (vibration == 0)
                    vibration = DEFAULT_VIBRATION;
                Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                assert vibrator != null;
                vibrator.vibrate(vibration);
            }

            // set tag and push notification
            Notification notification = mBuilder.build();

            String tag = alarm.getTag();
            if (tag != null && !tag.equals("")) {
                mNotificationManager.notify(tag, notificationID, notification);
            } else {
                Log.e(TAG, "notification done");
                mNotificationManager.notify(notificationID, notification);
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to send notification", e);
        }
    }

    void removeFiredNotification(int notificationId) {
        getNotificationManager().cancel(notificationId);
    }

    void removeAllFiredNotifications() {
        getNotificationManager().cancelAll();
    }

    void stopAlarmSound() {
        Log.e(TAG, "stop alarm sound");
        audioInterface.stopPlayer();
    }

    ArrayList<AlarmModel> getAlarms() {
        return getAlarmDB().getAlarmList(1);
    }

    WritableMap convertJsonToMap(JSONObject jsonObject) throws JSONException {
        WritableMap map = new WritableNativeMap();

        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                map.putMap(key, convertJsonToMap((JSONObject) value));
            } else if (value instanceof Boolean) {
                map.putBoolean(key, (Boolean) value);
            } else if (value instanceof Integer) {
                map.putInt(key, (Integer) value);
            } else if (value instanceof Double) {
                map.putDouble(key, (Double) value);
            } else if (value instanceof String) {
                map.putString(key, (String) value);
            } else {
                map.putString(key, value.toString());
            }
        }
        return map;
    }
}
