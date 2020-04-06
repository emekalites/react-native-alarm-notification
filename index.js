import { NativeModules } from 'react-native';

const { RNAlarmNotification } = NativeModules;
const ReactNativeAN = {};

ReactNativeAN.scheduleAlarm = (details) => {
	if (!details.alarm_id) {
		throw new Error('alarm_id is required for scheduled alarm');
	}
	RNAlarmNotification.scheduleAlarm(details);
};

ReactNativeAN.deleteAlarm = (alarm_id) => {
	if (!alarm_id) {
		throw new Error('alarm_id is required for scheduled alarm');
	}
	RNAlarmNotification.deleteAlarm(alarm_id);
};

ReactNativeAN.stopAlarmSound = () => {
	return RNAlarmNotification.stopAlarm();
};

ReactNativeAN.sendNotification = (details) => {
	RNAlarmNotification.sendNotification(details);
};

ReactNativeAN.removeFiredNotification = (alarm_id) => {
	if (!alarm_id) {
		throw new Error('alarm_id is required for scheduled alarm');
	}
	RNAlarmNotification.removeFiredNotification(alarm_id);
};

ReactNativeAN.removeAllFiredNotifications = () => {
	RNAlarmNotification.removeAllFiredNotifications();
};

ReactNativeAN.getScheduledAlarms = async () => {
	return await RNAlarmNotification.getScheduledAlarms();
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
