# React Native Alarm Notification

[![npm version](https://badge.fury.io/js/react-native-alarm-notification.svg)](https://badge.fury.io/js/react-native-alarm-notification)
[![npm downloads](https://img.shields.io/npm/dt/react-native-alarm-notification.svg)](https://badge.fury.io/js/react-native-alarm-notification)

## Installing (React Native >= 0.60.0)

`npm install react-native-alarm-notification --save`

or

`yarn add react-native-alarm-notification`

If you are using react-native version 0.60 or higher you don't need to link [react-native-alarm-notification](https://github.com/emekalites/react-native-alarm-notification). The package is automatically linked when building the app. All you need to do after installing package is:

For iOS pod installation:

```bash
npx pod-install
```

For android, the package will be linked automatically on build.

**_ IMPORTANT _**

If your app crashes on **Android**, it could probably mean auto linking didn't work. You will need to make the following changes:

**android/app/src/main/java/\<AppName\>/MainApplication.java**

- add `import com.emekalites.react.alarm.notification.ANPackage;` on the imports section
- add `packages.add(new ANPackage());` in `List<ReactPackage> getPackages()`;

**android/app/build.gradle**

add `implementation project(':react-native-alarm-notification')` in the `dependencies` block

**android/settings.gradle**

add:

```
include ':react-native-alarm-notification'
project(':react-native-alarm-notification').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-alarm-notification/android')

```

## Installing (React Native <= 0.59.x)

`npm install react-native-alarm-notification --save`

or

`yarn add react-native-alarm-notification`

Use `react-native link` to add the library to your project:

```
react-native link react-native-alarm-notification
```

**NOTE: For Android, you will have to update your AndroidManifest.xml (as below) in order to use Scheduled Notifications.**

## Android manual Installation

In your `AndroidManifest.xml`

```xml
    .....
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

    <application ....>
        <receiver
            android:name="com.emekalites.react.alarm.notification.AlarmReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="ACTION_DISMISS" />
                <action android:name="ACTION_SNOOZE" />
            </intent-filter>
        </receiver>

        <receiver
            android:name="com.emekalites.react.alarm.notification.AlarmDismissReceiver"
            android:enabled="true"
            android:exported="true" />

        <receiver
            android:name="com.emekalites.react.alarm.notification.AlarmBootReceiver"
            android:directBootAware="true"
            android:enabled="false"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
                <action android:name="android.intent.action.QUICKBOOT_POWERON" />
                <action android:name="com.htc.intent.action.QUICKBOOT_POWERON" />
                <action android:name="android.intent.action.LOCKED_BOOT_COMPLETED" />
            </intent-filter>
        </receiver>
     .....
```

## Alarm Notification Data

| Prop                              | Description                                                                                                                                                                                                                                                                                                             | Default                     |
| --------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------- |
| **`auto_cancel`**                 | Make this notification automatically dismissed when the user touches it. `[boolean]`                                                                                                                                                                                                                                    | `true`                      |
| **`channel (Android only)`**      | **Required:** - Specifies the channel the notification should be delivered on.. `[string]`                                                                                                                                                                                                                              | `"my_channel_id"`           |
| **`color (Android only)`**        | **Required:** Sets notification color. `[color]`                                                                                                                                                                                                                                                                        | `"red"`                     |
| **`data`**                        | You can add any additional data that is important for the notification.                                                                                                                                                                                                                                                 | `data: { foo: "bar" }`      |
| **`fire_date`**                   | **Required:** Set date for Alarm to get triggered. eg `'04-03-2020 00:00:00'`. Should be a date in the future .                                                                                                                                                                                                         | _None_                      |
| **`has_button`**                  | Show snooze and dismiss buttons in notification. `[boolean]`                                                                                                                                                                                                                                                            | `false`                     |
| **`large_icon (Android Only)`**   | Add a large icon to the notification content view. eg `"ic_large_icon"`. PS: make sure to add the file in your mipmap folders `[project_root]/android/app/src/main/res/mipmap*`                                                                                                                                         | _None_                      |
| **`loop_sound`**                  | Play sound continuously until you decide to stop it. `[boolean]`                                                                                                                                                                                                                                                        | `false`                     |
| **`message`**                     | **Required:** Add Notification message.                                                                                                                                                                                                                                                                                 | `"My Notification Message"` |
| **`play_sound`**                  | Play alarm notification sound. `[boolean]`                                                                                                                                                                                                                                                                              | `true`                      |
| **`repeat_interval`**             | Interval set to repeat alarm if schedule_type is "repeat". `[number]` in minutes                                                                                                                                                                                                                                        | `1`                         |
| **`schedule_type`**               | **Required:** Type of alarm schedule. `"once"` (to show alarm once) or `"repeat"` (to display alarm after set repeat_interval)                                                                                                                                                                                          | `"once"`                    |
| **`small_icon`**                  | **Required:** Set the small icon resource, which will be used to represent the notification in the status bar. eg `"ic_launcher"`. PS: make sure to add the file in your mipmap folders `[project_root]/android/app/src/main/res/mipmap*`                                                                               | `"ic_launcher"`             |
| **`snooze_interval`**             | Interval set to show alarm after snooze button is tapped. `[number]` in minutes                                                                                                                                                                                                                                         | `1`                         |
| **`sound_name`**                  | Set audio file to play when alarm is triggered. example `"sound_name.mp3"` or `"sound_name"`. PS: make sure to add the file in your res/raw folder `[project_root]/android/app/src/main/res/raw`                                                                                                                        | _None_                      |
| **`sound_names`**                 | Set multiple audio files to play when alarm is triggered, each file will be picked to play at random. Separate files with a comma (`,`) example `"sound_name1.mp3,sound_name2.mp3"` or `"sound_name1,sound_name2"`. PS: make sure to add the files in your res/raw folder `[project_root]/android/app/src/main/res/raw` | _None_                      |
| **`tag`**                         | Add a tag id to notification.                                                                                                                                                                                                                                                                                           | _None_                      |
| **`ticker`**                      | Set the "ticker" text which is sent to accessibility services.. `[String]`                                                                                                                                                                                                                                              | _None_                      |
| **`title`**                       | **Required:** Add a title to notification.                                                                                                                                                                                                                                                                              | `"My Notification Title"`   |
| **`vibrate`**                     | Set vibration when alarm is triggered. `[boolean]`                                                                                                                                                                                                                                                                      | `true`                      |
| **`vibration`**                   | Set number of milliseconds to vibrate. `[number]`                                                                                                                                                                                                                                                                       | `100`                       |
| **`use_big_text (Android only)`** | Set notification message style as big text. `[boolean]`                                                                                                                                                                                                                                                                 | `false`                     |

## Usage

```javascript
import ReactNativeAN from 'react-native-alarm-notification';

const fireDate = ReactNativeAN.parseDate(new Date(Date.now() + 1000));     // set the fire date for 1 second from now
or
const fireDate = '01-01-2060 00:00:00';			  // set exact date time | Format: dd-MM-yyyy HH:mm:ss

const alarmNotifData = {
	title: "My Notification Title",
	message: "My Notification Message",
	channel: "my_channel_id",
	small_icon: "ic_launcher",

	// You can add any additional data that is important for the notification
	// It will be added to the PendingIntent along with the rest of the bundle.
	// e.g.
  	data: { foo: "bar" },
};

class App extends Component {
	...

    async method(){
        //Schedule Future Alarm
		const alarm = await ReactNativeAN.scheduleAlarm({ ...alarmNotifData, fire_date: fireDate });
		console.log(alarm); // { id: 1 }

        //Delete Scheduled Alarm
        ReactNativeAN.deleteAlarm(alarm.id);

        //Stop Alarm
        ReactNativeAN.stopAlarmSound();

        //Send Local Notification Now
        ReactNativeAN.sendNotification(alarmNotifData);

        //Get All Scheduled Alarms
        const alarms = await ReactNativeAN.getScheduledAlarms();

        //Clear Notification(s) From Notification Center/Tray
        ReactNativeAN.removeFiredNotification(alarm.id);
        ReactNativeAN.removeAllFiredNotifications();
    }

	...
}
```

## Handle notification intent

```java

...
import android.content.Intent;
import android.os.Bundle;

import com.emekalites.react.alarm.notification.BundleJSONConverter;
import com.facebook.react.ReactActivity;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.json.JSONObject;
...

public class MainActivity extends ReactActivity {
    ...

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                JSONObject data = BundleJSONConverter.convertToJSON(bundle);
                getReactInstanceManager().getCurrentReactContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("OnNotificationOpened", data.toString());
            }
        } catch (Exception e) {
            System.err.println("Exception when handling notification opened. " + e);
        }
    }

}
```

## Listener for notifications

**NOTE: You can add a function here to go off after notification is opened or dismissed. Please remember to import DeviceEventEmitter**

```js
import { DeviceEventEmitter } from 'react-native';
```

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

## iOS Permissions

```js
import ReactNativeAN from 'react-native-alarm-notification';

// check if notification permissions has been granted for iOS
ReactNativeAN.checkPermissions((permissions) => {
	console.log(permissions);
});

// Request iOS permissions
ReactNativeAN.requestPermissions({
	alert: true,
	badge: true,
	sound: true,
}).then(
	(data) => {
		console.log('PushNotificationIOS.requestPermissions', data);
	},
	(data) => {
		console.log('PushNotificationIOS.requestPermissions failed', data);
	}
);
```

## Some features are missing

This module is customized to help with scheduling and sending notifications (local) in react-native. A couple of helpful features may be missing but hopefully they can be added as time goes on.

NOTE: If you need a react-native module that takes care of Firebase Cloud Messaging, you could use [react-native-firebase](https://github.com/invertase/react-native-firebase)
