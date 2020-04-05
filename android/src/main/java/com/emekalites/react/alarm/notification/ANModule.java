package com.emekalites.react.alarm.notification;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ANModule extends ReactContextBaseJavaModule {
    private final static String TAG = ANModule.class.getCanonicalName();
    private AlarmUtil alarmUtil;
    private static ReactApplicationContext mReactContext;

    ANModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        alarmUtil = new AlarmUtil((Application) reactContext.getApplicationContext());
    }

    static ReactApplicationContext getReactAppContext() {
        return mReactContext;
    }

    @Override
    public String getName() {
        return "RNAlarmNotification";
    }

    private AlarmDatabase getAlarmDB() {
        return new AlarmDatabase(mReactContext);
    }

    @ReactMethod
    public void scheduleAlarm(ReadableMap details) throws ParseException {
        Bundle bundle = Arguments.toBundle(details);

        AlarmModel alarm = new AlarmModel();

        alarm.setActive(1);
        alarm.setAutoCancel(bundle.getBoolean("auto_cancel", true));
        alarm.setChannel(bundle.getString("channel", "my_channel_id"));
        alarm.setColor(bundle.getString("color", "red"));

        Bundle data = bundle.getBundle("data");
        alarm.setData(bundle2string(data));

        alarm.setInterval(bundle.getInt("repeat_interval", 1));
        alarm.setLargeIcon(bundle.getString("large_icon", ""));
        alarm.setLoopSound(bundle.getBoolean("loop_sound", false));
        alarm.setMessage(bundle.getString("message", "My Notification Message"));
        alarm.setPlaySound(bundle.getBoolean("play_sound", true));
        alarm.setScheduleType(bundle.getString("schedule_type", "once"));
        alarm.setSmallIcon(bundle.getString("small_icon", "ic_launcher"));
        alarm.setSnoozeInterval(bundle.getInt("snooze_interval", 1));
        alarm.setSoundName(bundle.getString("sound_name", null));
        alarm.setSoundNames(bundle.getString("sound_names", null));
        alarm.setTag(bundle.getString("tag", ""));
        alarm.setTicker(bundle.getString("ticker", ""));
        alarm.setTitle(bundle.getString("title", "My Notification Title"));
        alarm.setVibrate(bundle.getBoolean("vibrate", true));
        alarm.setVibration(bundle.getInt("vibration", 100));
        alarm.setUseBigText(bundle.getBoolean("use_big_text", false));

        try {
            int alarmId = Integer.parseInt(bundle.getInt("alarm_id"));
            if (alarmId == 0) {
                alarmId = (int) System.currentTimeMillis();
            }
            alarm.setAlarmId(alarmId);
        } catch (Exception e) {
            alarm.setAlarmId((int) System.currentTimeMillis());
        }

        String datetime = bundle.getString("fire_date");
        if (datetime == null || datetime.equals("")) {
            Log.e(TAG, "failed to schedule notification because fire date is missing");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(sdf.parse(datetime));

        alarm.setMinute(calendar.get(Calendar.MINUTE));
        alarm.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        alarm.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        alarm.setMonth(calendar.get(Calendar.MONTH) - 1);
        alarm.setYear(calendar.get(Calendar.YEAR));

        boolean containAlarm = alarmUtil.checkAlarm(getAlarmDB().getAlarmList(1), alarm);
        if (!containAlarm) {
            try {
                int id = getAlarmDB().insert(alarm);
                alarm.setId(id);

                alarmUtil.setAlarm(alarm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @ReactMethod
    public void deleteAlarm(String alarmID) {
        alarmUtil.doCancelAlarm(alarmID);
    }

    @ReactMethod
    public void stopAlarm() {
        alarmUtil.stopAlarmSound();
    }

    @ReactMethod
    public void sendNotification(ReadableMap details) throws ParseException {
        Bundle bundle = Arguments.toBundle(details);

        AlarmModel alarm = new AlarmModel();

        alarm.setActive(1);
        alarm.setAutoCancel(bundle.getBoolean("auto_cancel", true));
        alarm.setChannel(bundle.getString("channel", "my_channel_id"));
        alarm.setColor(bundle.getString("color", "red"));

        Bundle data = bundle.getBundle("data");
        alarm.setData(bundle2string(data));

        alarm.setInterval(bundle.getInt("repeat_interval", 1));
        alarm.setLargeIcon(bundle.getString("large_icon"));
        alarm.setLoopSound(bundle.getBoolean("loop_sound", false));
        alarm.setMessage(bundle.getString("message", "My Notification Message"));
        alarm.setPlaySound(bundle.getBoolean("play_sound", true));
        alarm.setScheduleType(bundle.getString("schedule_type", "once"));
        alarm.setSmallIcon(bundle.getString("small_icon", "ic_launcher"));
        alarm.setSnoozeInterval(bundle.getInt("snooze_interval", 1));
        alarm.setSoundName(bundle.getString("sound_name"));
        alarm.setSoundNames(bundle.getString("sound_names"));
        alarm.setTag(bundle.getString("tag"));
        alarm.setTicker(bundle.getString("ticker"));
        alarm.setTitle(bundle.getString("title", "My Notification Title"));
        alarm.setVibrate(bundle.getBoolean("loop_sound", true));
        alarm.setVibration(bundle.getInt("vibration", 100));
        alarm.setUseBigText(bundle.getBoolean("use_big_text", false));

        Calendar calendar = GregorianCalendar.getInstance();

        alarm.setMinute(calendar.get(Calendar.MINUTE));
        alarm.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        alarm.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        alarm.setMonth(calendar.get(Calendar.MONTH) - 1);
        alarm.setYear(calendar.get(Calendar.YEAR));

        try {
            int alarmId = Integer.parseInt(bundle.getInt("alarm_id"));
            if (alarmId == 0) {
                alarmId = (int) System.currentTimeMillis();
            }
            alarm.setAlarmId(alarmId);
        } catch (Exception e) {
            alarm.setAlarmId((int) System.currentTimeMillis());
        }

        try {
            int id = getAlarmDB().insert(alarm);
            alarm.setId(id);

            alarmUtil.setAlarm(alarm);
        } catch (Exception e) {
            e.printStackTrace();
        }

        alarmUtil.sendNotification(alarm);
    }

    @ReactMethod
    public void removeFiredNotification(String alarm_id) {
        alarmUtil.removeFiredNotification(Integer.parseInt(alarm_id));
    }

    @ReactMethod
    public void removeAllFiredNotifications() {
        alarmUtil.removeAllFiredNotifications();
    }

    @ReactMethod
    public void getScheduledAlarms(Promise promise) throws JSONException {
        ArrayList<AlarmModel> alarms = alarmUtil.getAlarms();
        WritableArray array = Arguments.createArray();
        Gson gson = new Gson();
        for (AlarmModel alarm : alarms) {
            WritableMap alarmMap = alarmUtil.convertJsonToMap(new JSONObject(gson.toJson(alarm)));
            array.pushMap(alarmMap);
        }
        promise.resolve(array);
    }

    private static String bundle2string(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        StringBuilder string = new StringBuilder();
        for (String key : bundle.keySet()) {
            string.append(key).append("==>").append(bundle.get(key)).append(";;");
        }
        return string.toString();
    }
}
