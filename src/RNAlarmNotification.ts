// @ts-ignore
import { NativeModules } from 'react-native';

type TRepeatIntervals = 'minutely' | 'hourly' | 'daily' | 'weekly';
type TScheduleTypes = 'once' | 'repeat';
type TData = {
	[key: string]: string
};

/**
 * These interfaces and types add typing for the react-native java/ios modules
 * that are used from NativeModules.RNAlarmNotification
 *
 * @todo fix the following keys so they are actually required
 * @see ISendNotificationDetails.channel
 * @see ISendNotificationDetails.color
 * @see ISendNotificationDetails.message
 * @see IScheduleAlarmDetails.schedule_type
 * @see ISendNotificationDetails.small_icon
 * @see ISendNotificationDetails.title
 * (no default in ANModule.java on bundle.get*'s) OR change to optional).
 */

export interface ISendNotificationDetails {
	auto_cancel?: boolean; // Make this notification automatically dismissed when the user touches it. [boolean]
	channel?: string; // (Android only) Required: - Specifies the channel the notification should be delivered on.. [string]
	color?: string; // (Android only) Required: Sets notification color. [color]
	data?: TData; // You can add any additional data that is important for the notification.
	large_icon?: string; // (Android Only) Add a large icon to the notification content view. eg "ic_large_icon". PS: make sure to add the file in your mipmap folders [project_root]/android/app/src/main/res/mipmap*
	loop_sound?: boolean; // Play sound continuously until you decide to stop it. [boolean]
	message?: string; // Required: Add Notification message.
	play_sound?: boolean; // Play alarm notification sound. [boolean]
	small_icon?: string; // Required: Set the small icon resource, which will be used to represent the notification in the status bar. eg "ic_launcher". PS: make sure to add the file in your mipmap folders [project_root]/android/app/src/main/res/mipmap*
	snooze_interval?: number; // Interval set to show alarm after snooze button is tapped. [number] in minutes
	sound_name?: string; // Set audio file to play when alarm is triggered. example "sound_name.mp3". Add the file in your res/raw folder [project_root]/android/app/src/main/res/raw/ for android and in your ios folder [project_root]/ios/ for iOS. PS: After adding file in your iOS folder, open your application in xcode and drag your audio file into your project.
	sound_names?: string; // Set multiple audio files to play when alarm is triggered, each file will be picked to play at random. Separate files with a comma (,) example "sound_name1.mp3,sound_name2.mp3". PS: make sure to add the files in your res/raw folder [project_root]/android/app/src/main/res/raw/
	tag?: string; // Add a tag id to notification.
	ticker?: string; // Set the "ticker" text which is sent to accessibility services.. [String]
	title?: string; // Required: Add a title to notification.
	has_button?: boolean; // Show snooze and dismiss buttons in notification. [boolean]
	vibration?: number; // Set number of milliseconds to vibrate. [number]
	use_big_text?: boolean; // (Android only) Set notification message style as big text. [boolean]
	volume?: number; // Set volume. [number between 0.0 and 1.0]
	bypass_dnd?: boolean; // Sets whether or not notifications posted to this channel can interrupt the user. [boolean]
}

export interface IScheduleAlarmDetails extends ISendNotificationDetails {
	repeat_interval?: TRepeatIntervals; // Interval set to repeat alarm if schedule_type is "repeat". [minutely, hourly, daily, weekly]
	schedule_type?: TScheduleTypes; // Required: Type of alarm schedule. "once" (to show alarm once) or "repeat" (to display alarm after set repeat_interval)
	vibrate?: boolean; // Set vibration when alarm is triggered. [boolean]
	interval_value?: number; // Set interval_value if repeat_interval is minutely and hourly. [number]
	fire_date: string; // Required: Set date for Alarm to get triggered. eg '04-03-2020 00:00:00'. Should be a date in the future .
}

type TScheduleAlarmDetailsResolve = {id: number};

export interface IScheduledAlarms {
	volume: number,
	vibration: number,
	vibrate: boolean,
	title: string,
	ticker: string,
	soundName: string,
	color: string,
	snoozeInterval: number,
	playSound: boolean,
	tag: string,
	month: number,
	minute: number,
	loopSound: boolean,
	second: number,
	bypassDnd: boolean,
	message: string,
	hasButton: boolean,
	interval: string,
	largeIcon: "",
	year: number,
	alarmId: number,
	channel: string,
	day: number,
	scheduleType: string,
	useBigText: boolean,
	active: number,
	intervalValue: number,
	id: number,
	data: string,
	smallIcon: string,
	autoCancel: boolean,
	hour: number
}

export interface IRequestPermissionsIOS {
	alert?: boolean,
	badge?: boolean,
	sound?: boolean,
}

export interface ICheckPermissionsIOS {
	notificationCenter: boolean;
	alert: boolean;
	lockScreen: boolean;
	badge: boolean;
	sound: boolean;
}

export type TCheckPermissionsCallback = (permissions: ICheckPermissionsIOS) => void;

interface RNAlarmNotificationI {
	scheduleAlarm: (details: IScheduleAlarmDetails) => Promise<TScheduleAlarmDetailsResolve>;
	deleteAlarm: (alarmID: number) => void;
	deleteRepeatingAlarm: (alarmID: number) => void;
	stopAlarmSound: () => void;
	sendNotification: (details: ISendNotificationDetails) => void;
	removeFiredNotification: (id: number) => void;
	removeAllFiredNotifications: () => void;
	getScheduledAlarms: () => Promise<IScheduledAlarms[]>;
	requestPermissions: (permissions: IRequestPermissionsIOS) => Promise<any>;
	checkPermissions: (callback: TCheckPermissionsCallback) => void;
}

const RNAlarmNotification: RNAlarmNotificationI = NativeModules.RNAlarmNotification;

export default RNAlarmNotification;
