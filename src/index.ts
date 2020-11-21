'use strict';

import RNAlarmNotification, {
	IRequestPermissionsIOS,
	IScheduleAlarmDetails,
	ISendNotificationDetails, TCheckPermissionsCallback
} from "./RNAlarmNotification";

const parseDateString = (string: string) => {
	const splits = string.split(' ');
	const dateSplits = splits[0].split('-');
	const timeSplits = splits[1].split(':');

	const year = dateSplits[2];
	const month = dateSplits[1];
	const day = dateSplits[0];

	const hours = timeSplits[0];
	const minutes = timeSplits[1];
	const seconds = timeSplits[2];

	return new Date(parseInt(year), parseInt(month) - 1, parseInt(day), parseInt(hours), parseInt(minutes), parseInt(seconds));
};

export async function scheduleAlarm (details: IScheduleAlarmDetails) {
	if (!details.fire_date || (details.fire_date && details.fire_date === '')) {
		throw new Error('failed to schedule alarm because fire date is missing');
	}

	const past = parseDateString(details.fire_date);
	const now = new Date();
	if (past < now) {
		throw new Error(
			'failed to schedule alarm because fire date is in the past'
		);
	}

	const repeat_interval = details.repeat_interval || 'hourly';
	const interval_value = details.interval_value || 1;
	if (isNaN(interval_value)) {
		throw new Error('interval value should be a number');
	}

	if (
		repeat_interval === 'minutely' &&
		(interval_value < 1 || interval_value > 59)
	) {
		throw new Error('interval value should be between 1 and 59 minutes');
	}

	if (
		repeat_interval === 'hourly' &&
		(interval_value < 1 || interval_value > 23)
	) {
		throw new Error('interval value should be between 1 and 23 hours');
	}

	return await RNAlarmNotification.scheduleAlarm({
		...details,
		has_button: details.has_button || false,
		vibrate: (typeof details.vibrate === 'undefined' ? true : details.vibrate),
		play_sound: (typeof details.play_sound === 'undefined' ? true : details.play_sound),
		schedule_type: details.schedule_type || 'once',
		repeat_interval,
		interval_value,
		volume: details.volume || 0.5,
		sound_name: details.sound_name || '',
		snooze_interval: details.snooze_interval || 1,
		data: details.data || {},
	});
}

export function sendNotification (details: ISendNotificationDetails) {
	RNAlarmNotification.sendNotification({
		...details,
		has_button: false,
		play_sound: (typeof details.play_sound === 'undefined' ? true : details.play_sound),
		volume: details.volume || 0.5,
		sound_name: details.sound_name || '',
		snooze_interval: details.snooze_interval || 1,
		data: details.data || {},
	});
}

export function deleteAlarm (id: number) {
	if (!id) {
		throw new Error('id is required to delete alarm');
	}

	RNAlarmNotification.deleteAlarm(id);
}

export function deleteRepeatingAlarm (id: number) {
	if (!id) {
		throw new Error('id is required to delete alarm');
	}

	RNAlarmNotification.deleteRepeatingAlarm(id);
}

export function stopAlarmSound () {
	return RNAlarmNotification.stopAlarmSound();
}

export function removeFiredNotification (id: number) {
	if (!id) {
		throw new Error('id is required to remove notification');
	}

	RNAlarmNotification.removeFiredNotification(id);
}

export function removeAllFiredNotifications () {
	RNAlarmNotification.removeAllFiredNotifications();
}

export async function getScheduledAlarms () {
	return await RNAlarmNotification.getScheduledAlarms();
}

export function parseDate (rawDate: Date) {
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
}

/**
 * iOS Only.
 */
export async function requestPermissions (permissions: IRequestPermissionsIOS) {
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
}

// ios check permission
export function checkPermissions (callback: TCheckPermissionsCallback) {
	RNAlarmNotification.checkPermissions(callback);
}
