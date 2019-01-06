package com.emekalites.react.alarm.notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.json.JSONObject;

public class ANDismissReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
      try {
          if (ANModule.mReactContext != null) {
              int notificationId = intent.getExtras().getInt("com.emekalites.react.alarm.notification.notificationId");
              ANModule.mReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("OnNotificationDismissed", "{\"id\": \""+notificationId+"\"}");
          }
      } catch (Exception e) {
          System.err.println("Exception when handling notification dismiss. " + e);
      }
  }
}