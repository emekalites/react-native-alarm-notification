package com.emekalites.react.alarm.notification;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ANDismissReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
      try {
          if (ANModule.getReactAppContext() != null) {
              int notificationId = intent.getExtras().getInt("com.emekalites.react.alarm.notification.notificationId");
              ANModule.getReactAppContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("OnNotificationDismissed", "{\"id\": \""+notificationId+"\"}");
          }
      } catch (Exception e) {
          System.err.println("Exception when handling notification dismiss. " + e);
      }
  }
}
