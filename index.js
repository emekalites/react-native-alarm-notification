import { NativeModules } from 'react-native';
const RNAlarmNotification = NativeModules.RNAlarmNotification;
const ReactNativeAN = {};

ReactNativeAN.scheduleAlarm = (details) => {
	if (!details.id) {
		throw new Error('id is required for scheduled alarm');
	}
	RNAlarmNotification.scheduleAlarm(details);
};

ReactNativeAN.deleteAlarm = (notificationID) => {
	if (!notificationID) {
		return;
	}
	RNAlarmNotification.deleteAlarm(notificationID);
};

ReactNativeAN.sendNotification = (details) => {
	RNAlarmNotification.sendNotification(details);
};

ReactNativeAN.cancelNotification = (notificationID) => {
	if (!notificationID) {
		return;
	}
	RNAlarmNotification.cancelNotification(notificationID);
};

ReactNativeAN.cancelAllNotifications = () => {
	RNAlarmNotification.cancelAllNotifications();
};

ReactNativeAN.removeFiredNotification = (notificationID) => {
	if (!notificationID) {
		return;
	}
	RNAlarmNotification.removeFiredNotification(notificationID);
};

ReactNativeAN.removeAllFiredNotifications = () => {
	RNAlarmNotification.removeAllFiredNotifications();
};

ReactNativeAN.getScheduledAlarms = () => {
	return RNAlarmNotification.getScheduledAlarms();
};

export default ReactNativeAN;
