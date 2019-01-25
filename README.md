# React Native Alarm Notification

[![npm version](https://badge.fury.io/js/react-native-alarm-notification.svg)](https://badge.fury.io/js/react-native-alarm-notification)
[![npm downloads](https://img.shields.io/npm/dt/react-native-alarm-notification.svg)](https://badge.fury.io/js/react-native-alarm-notification)

React Native Alarm Notification for Android

**NOTE: The iOS side of this module will be included when i have figured it out.**

## Installation

`npm install --save react-native-alarm-notification`

or

`yarn add react-native-alarm-notification`

## Linking

`react-native link react-native-alarm-notification`

**NOTE: For Android, you will still have to manually update the AndroidManifest.xml (as below) in order to use Scheduled Notifications.**

## Android manual Installation

In your `AndroidManifest.xml`

```xml
    .....
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
    <uses-permission android:name="android.permission.VIBRATE"/>
    <uses-permission android:name="android.permission.WAKE_LOCK"/>
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

    <application ....>
        <service android:name="com.emekalites.react.alarm.notification.ANService" android:enabled="true"/>
        <receiver android:name="com.emekalites.react.alarm.notification.ANAlarmReceiver" android:enabled="true"/>
        <receiver android:name="com.emekalites.react.alarm.notification.ANBootReceiver" android:enabled="true" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED"/>
                <action android:name="android.intent.action.QUICKBOOT_POWERON"/>
                <action android:name="com.htc.intent.action.QUICKBOOT_POWERON"/>
            </intent-filter>
        </receiver>
        <receiver android:name="com.emekalites.react.alarm.notification.ANDismissReceiver" android:exported="false"/>
     .....
```

In `android/settings.gradle`

```gradle
...

include ':react-native-alarm-notification'
project(':react-native-alarm-notification').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-alarm-notification/android')
```

In `app/build.gradle`

```gradle
dependencies {
    compile project(':react-native-alarm-notification')  // <--- add project
    ...
    compile fileTree(dir: "libs", include: ["*.jar"])
    compile "com.android.support:appcompat-v7:28.0.0" // <--- minimum support library version
    compile "com.facebook.react:react-native:+"  // From node_modules
}
```

Manually register module in `MainApplication.java` (if you did not use `react-native link`):

```java
import com.emekalites.react.alarm.notification.ANPackage;  // <--- Import Package

public class MainApplication extends Application implements ReactApplication {

  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
      @Override
      protected boolean getUseDeveloperSupport() {
        return BuildConfig.DEBUG;
      }

      @Override
      protected List<ReactPackage> getPackages() {
      	return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
          new ANPackage() 		// <---- Add the Package
        );
      }
  };

  ....
}
```

Register your channel e.g. in `onCreate()`

```java

...
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
...

public class MainApplication extends Application implements ReactApplication {

  ...

  @Override
  public void onCreate() {
    super.onCreate();

    ...

    String id = "my_channel_id";					// The id of the channel.
    CharSequence name = "my_channel_name";			// The user-visible name of the channel.
    String description = "my_channel_description";	// The user-visible description of the channel.

    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
 
      // Configure the notification channel. 
      mChannel.setDescription(description);
 
      mChannel.enableLights(true);
      // Sets the notification light color for notifications posted to this
      // channel, if the device supports this feature. 
      mChannel.setLightColor(Color.RED);
 
      mChannel.enableVibration(true);
      mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
 
      NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
	  mNotificationManager.createNotificationChannel(mChannel);
    }
  }
}
```

## Usage

```javascript
import ReactNativeAN from 'react-native-alarm-notification';
const fireDate = ReactNativeAN.parseDate(new Date(Date.now() + 1000));     // set the fire date for 1 second from now
or
const fireDate = '01-01-1976 00:00:00';			  // set exact date time | Format: dd-MM-yyyy HH:mm:ss

const alarmNotifData = {
	id: "12345",                                  // Required
	title: "My Notification Title",               // Required
	message: "My Notification Message",           // Required
	channel: "my_channel_id",                     // Required. Same id as specified in MainApplication's onCreate method
	ticker: "My Notification Ticker",
	auto_cancel: true,                            // default: true
	vibrate: true,
	vibration: 100,                               // default: 100, no vibration if vibrate: false
	small_icon: "ic_launcher",                    // Required
	large_icon: "ic_launcher",
	play_sound: true,
	sound_name: null,                             // Plays custom notification ringtone if sound_name: null
	color: "red",
	schedule_once: true,                          // Works with ReactNativeAN.scheduleAlarm so alarm fires once
	tag: 'some_tag',
	fire_date: fireDate,                          // Date for firing alarm, Required for ReactNativeAN.scheduleAlarm.

	// You can add any additional data that is important for the notification
	// It will be added to the PendingIntent along with the rest of the bundle.
	// e.g.
  	data: { foo: "bar" },
};

class App extends Component {
	...

    method(){
        //Schedule Future Alarm
        ReactNativeAN.scheduleAlarm(alarmNotifData);

        //Delete Scheduled Alarm
        ReactNativeAN.deleteAlarm("12345");

        //Stop Alarm
        ReactNativeAN.stopAlarm();

        //Send Local Notification Now
        ReactNativeAN.sendNotification(alarmNotifData);

        //Get All Scheduled Alarms
        ReactNativeAN.getScheduledAlarms().then(alarmNotif=>console.log(alarmNotif));

        //Clear Notification(s) From Notification Center/Tray
        ReactNativeAN.removeAllFiredNotifications()
        ReactNativeAN.removeFiredNotification("12345")

        //Removes Future Local Notifications
        ReactNativeAN.cancelAllNotifications()
        ReactNativeAN.cancelNotification("12345")
    }

	...
}
```

## Custom sounds

In android, add your custom sound file to `[project_root]/android/app/src/main/res/raw`

In the location notification json specify the full file name:

    sound_name: 'my_sound.mp3'

or check this issue if it'll help [https://github.com/emekalites/react-native-alarm-notification/issues/3](https://github.com/emekalites/react-native-alarm-notification/issues/3)

## Handle notification intent

```java

...
import android.content.Intent;
import android.os.Bundle;
import com.emekalites.react.alarm.notification.BundleJSONConverter;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import org.json.JSONObject;
...

public class MainActivity extends ReactActivity {
    ...

    @Override
    public void onNewIntent(Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            JSONObject data = BundleJSONConverter.convertToJSON(bundle);
            getReactInstanceManager().getCurrentReactContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("OnNotificationOpened", data.toString());
        } catch (Exception e){
            System.err.println("Exception when handling notification openned. " + e);
        }
    }

}
```

## Listener for notifications

**NOTE: You can add a function here to go off after notification is opened or dismissed.**

```js
componentDidMount() {
	DeviceEventEmitter.addListener('OnNotificationDismissed', async function(e) {
		const obj = JSON.parse(e);
		console.log(obj);
	});

	DeviceEventEmitter.addListener('OnNotificationOpened', async function(e) {
		const obj = JSON.parse(e);
		console.log(obj);
	});
}
	
componentWillUnmount() {
	DeviceEventEmitter.removeListener('OnNotificationDismissed');
	DeviceEventEmitter.removeListener('OnNotificationOpened');
}
```

## Some features are missing

This module is customized to help with scheduling and sending notifications (local) in react-native. A couple of helpful features may be missing but hopefully they can be added as time goes on.

NOTE: If you need a react-native module that takes care of Firebase Cloud Messaging, you could try [https://github.com/evollu/react-native-fcm](https://github.com/evollu/react-native-fcm)
