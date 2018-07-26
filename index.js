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

ReactNativeAN.stopAlarm = () => {
	return RNAlarmNotification.stopAlarm();
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

ReactNativeAN.parseDate = (rawDate) => {
	let hours;
	let day;
	let month;

	if (rawDate.getHours().toString().length === 1) {
		hours = `0${rawDate.getHours()}`;
	} else {
		hours = `${rawDate.getHours()}`;
	}

	if (rawDate.getDate().toString().length === 1) {
		day = `0${rawDate.getDate()}`;
	} else {
		day = `${rawDate.getDate()}`;
	}

	if (rawDate.getMonth().toString().length === 1) {
		month = `0${rawDate.getMonth() + 1}`;
	} else {
		month = `${rawDate.getMonth() + 1}`;
	}

	return `${day}-${month}-${rawDate.getFullYear()} ${hours}:${rawDate.getMinutes()}:${rawDate.getSeconds()}`;
};
  
export default ReactNativeAN;
