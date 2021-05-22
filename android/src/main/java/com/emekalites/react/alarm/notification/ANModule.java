package com.emekalites.react.alarm.notification;

import android.app.Application;
import android.os.Bundle;

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
import java.util.Random;

public class ANModule extends ReactContextBaseJavaModule {
    private final static String TAG = ANModule.class.getCanonicalName();
    private AlarmUtil alarmUtil;
    private static ReactApplicationContext mReactContext;

    private static final String E_SCHEDULE_ALARM_FAILED = "E_SCHEDULE_ALARM_FAILED";

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
    public void scheduleAlarm(ReadableMap details, Promise promise) throws ParseException {
        Bundle bundle = Arguments.toBundle(details);

        AlarmModel alarm = new AlarmModel();

        Random r = new Random();
        long time = System.currentTimeMillis() / 1000 + r.nextInt(1000) ;

        alarm.setAlarmId((int) time);

        alarm.setActive(1);
        alarm.setAutoCancel(bundle.getBoolean("auto_cancel", true));
        alarm.setChannel(bundle.getString("channel", "my_channel_id"));
        alarm.setColor(bundle.getString("color", "red"));

        Bundle data = bundle.getBundle("data");
        alarm.setData(bundle2string(data));

        alarm.setInterval(bundle.getString("repeat_interval", "hourly"));
        alarm.setLargeIcon(bundle.getString("large_icon", ""));
        alarm.setLoopSound(bundle.getBoolean("loop_sound", false));
        alarm.setMessage(bundle.getString("message", "My Notification Message"));
        alarm.setPlaySound(bundle.getBoolean("play_sound", true));
        alarm.setScheduleType(bundle.getString("schedule_type", "once"));
        alarm.setSmallIcon(bundle.getString("small_icon", "ic_launcher"));
        alarm.setSnoozeInterval((int)bundle.getDouble("snooze_interval", 1.0));
        alarm.setSoundName(bundle.getString("sound_name", null));
        alarm.setSoundNames(bundle.getString("sound_names", null));
        alarm.setTag(bundle.getString("tag", ""));
        alarm.setTicker(bundle.getString("ticker", ""));
        alarm.setTitle(bundle.getString("title", "My Notification Title"));
        alarm.setVibrate(bundle.getBoolean("vibrate", true));
        alarm.setHasButton(bundle.getBoolean("has_button", false));
        alarm.setVibration((int)bundle.getDouble("vibration", 100.0));
        alarm.setUseBigText(bundle.getBoolean("use_big_text", false));
        alarm.setVolume(bundle.getDouble("volume", 0.5));
        alarm.setIntervalValue((int)bundle.getDouble("interval_value", 1));
        alarm.setBypassDnd(bundle.getBoolean("bypass_dnd", false));

        String datetime = bundle.getString("fire_date");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
        Calendar calendar = new GregorianCalendar();

        calendar.setTime(sdf.parse(datetime));

        alarmUtil.setAlarmFromCalendar(alarm, calendar);

        // check if alarm has been set at this time
        boolean containAlarm = alarmUtil.checkAlarm(getAlarmDB().getAlarmList(1), alarm);
        if (!containAlarm) {
            try {
                int id = getAlarmDB().insert(alarm);
                alarm.setId(id);

                alarmUtil.setAlarm(alarm);

                WritableMap map = Arguments.createMap();
                map.putInt("id", id);

                promise.resolve(map);
            } catch (Exception e) {
                e.printStackTrace();
                promise.reject(E_SCHEDULE_ALARM_FAILED, e);
            }
        } else {
            promise.reject(E_SCHEDULE_ALARM_FAILED, "duplicate alarm set at date");
        }
    }

    @ReactMethod
    public void deleteAlarm(int alarmID) {
        alarmUtil.deleteAlarm(alarmID);
    }

    @ReactMethod
    public void deleteRepeatingAlarm(int alarmID) {
        alarmUtil.deleteRepeatingAlarm(alarmID);
    }

    @ReactMethod
    public void stopAlarmSound() {
        alarmUtil.stopAlarmSound();
    }

    @ReactMethod
    public void sendNotification(ReadableMap details) throws ParseException {
        Bundle bundle = Arguments.toBundle(details);

        AlarmModel alarm = new AlarmModel();

        long time = System.currentTimeMillis() / 1000;

        alarm.setAlarmId((int) time);

        alarm.setActive(1);
        alarm.setAutoCancel(bundle.getBoolean("auto_cancel", true));
        alarm.setChannel(bundle.getString("channel", "my_channel_id"));
        alarm.setColor(bundle.getString("color", "red"));

        Bundle data = bundle.getBundle("data");
        alarm.setData(bundle2string(data));

        alarm.setLargeIcon(bundle.getString("large_icon"));
        alarm.setLoopSound(bundle.getBoolean("loop_sound", false));
        alarm.setMessage(bundle.getString("message", "My Notification Message"));
        alarm.setPlaySound(bundle.getBoolean("play_sound", true));
        alarm.setSmallIcon(bundle.getString("small_icon", "ic_launcher"));
        alarm.setSnoozeInterval((int)bundle.getDouble("snooze_interval", 1));
        alarm.setSoundName(bundle.getString("sound_name"));
        alarm.setSoundNames(bundle.getString("sound_names"));
        alarm.setTag(bundle.getString("tag"));
        alarm.setTicker(bundle.getString("ticker"));
        alarm.setTitle(bundle.getString("title", "My Notification Title"));
        alarm.setVibrate(bundle.getBoolean("loop_sound", true));
        alarm.setHasButton(bundle.getBoolean("has_button", false));
        alarm.setVibration((int)bundle.getDouble("vibration", 100));
        alarm.setUseBigText(bundle.getBoolean("use_big_text", false));
        alarm.setVolume(bundle.getDouble("volume", 0.5));
        alarm.setBypassDnd(bundle.getBoolean("bypass_dnd", false));

        Calendar calendar = new GregorianCalendar();

        alarmUtil.setAlarmFromCalendar(alarm, calendar);

        try {
            int id = getAlarmDB().insert(alarm);
            alarm.setId(id);

            alarmUtil.sendNotification(alarm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ReactMethod
    public void removeFiredNotification(int id) {
        alarmUtil.removeFiredNotification(id);
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
