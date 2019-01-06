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

import java.util.ArrayList;

/**
 * Created by emnity on 6/25/17.
 */

public class ANModule extends ReactContextBaseJavaModule {
    private final static String TAG = ANModule.class.getCanonicalName();
    private ANHelper anHelper;
	private static ReactApplicationContext mReactContext;

    public ANModule(ReactApplicationContext reactContext) {
		super(reactContext);
        mReactContext = reactContext;        
        anHelper = new ANHelper((Application) reactContext.getApplicationContext());
    }

    static public ReactApplicationContext getReactAppContext() {
        return mReactContext;
    }

    @Override
    public String getName() {
        return "RNAlarmNotification";
    }

    @ReactMethod
    public void scheduleAlarm(ReadableMap details) {
        Bundle bundle = Arguments.toBundle(details);
        anHelper.scheduleAlarm(bundle);
    }

    @ReactMethod
    public void deleteAlarm(String notificationID) {
        anHelper.cancelAlarm(notificationID);
    }

    @ReactMethod
    public void stopAlarm() {
        anHelper.stopAlarm();
    }

    @ReactMethod
    public void sendNotification(ReadableMap details) {
        Bundle bundle = Arguments.toBundle(details);
        anHelper.sendNotification(bundle);
    }

    @ReactMethod
    public void cancelNotification(String notificationID) {
        anHelper.cancelNotification(notificationID);
    }

    @ReactMethod
    public void cancelAllNotifications() {
        anHelper.cancelAllNotifications();
    }

    @ReactMethod
    public void removeFiredNotification(String notificationID) {
        anHelper.removeFiredNotification(notificationID);
    }

    @ReactMethod
    public void removeAllFiredNotifications() {
        anHelper.removeAllFiredNotifications();
    }

    @ReactMethod
    public void getScheduledAlarms(Promise promise) {
        ArrayList<Bundle> bundles = anHelper.getScheduledAlarms();
        WritableArray array = Arguments.createArray();
        for(Bundle bundle:bundles){
            array.pushMap(Arguments.fromBundle(bundle));
        }
        promise.resolve(array);
    }
}
