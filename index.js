import { NativeModules } from 'react-native';

const { RNAlarmNotification } = NativeModules;
const ReactNativeAN = {};

ReactNativeAN.scheduleAlarm = async (details) => {
	if (!details.fire_date || (details.fire_date && details.fire_date === '')) {
		throw new Error('failed to schedule alarm because fire date is missing');
	}

	return await RNAlarmNotification.scheduleAlarm(details);
};

ReactNativeAN.deleteAlarm = (id) => {
	if (!id) {
		throw new Error('id is required to delete alarm');
	}

	RNAlarmNotification.deleteAlarm(id);
};

ReactNativeAN.stopAlarmSound = () => {
	return RNAlarmNotification.stopAlarmSound();
};

ReactNativeAN.sendNotification = (details) => {
	RNAlarmNotification.sendNotification(details);
};

ReactNativeAN.removeFiredNotification = (id) => {
	if (!id) {
		throw new Error('id is required to remove notification');
	}

	RNAlarmNotification.removeFiredNotification(id);
};

ReactNativeAN.removeAllFiredNotifications = () => {
	RNAlarmNotification.removeAllFiredNotifications();
};

ReactNativeAN.getScheduledAlarms = async () => {
	return await RNAlarmNotification.getScheduledAlarms();
};

// ios request permission
ReactNativeAN.requestPermissions = async (permissions) => {
	let requestedPermissions = {
		alert: true,
		badge: true,
		sound: true,
	};

	if (permissions) {
		requestedPermissions = {
			alert: !!permissions.alert,
			badge: !!permissions.badge,
			sound: !!permissions.sound,
		};
	}

	return await RNAlarmNotification.requestPermissions(requestedPermissions);
};

// ios check permission
ReactNativeAN.checkPermissions = (callback) => {
	RNAlarmNotification.checkPermissions(callback);
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
