declare type TRepeatIntervals = 'minutely' | 'hourly' | 'daily' | 'weekly';
declare type TScheduleTypes = 'once' | 'repeat';
declare type TData = {
    [key: string]: string;
};
export interface ISendNotificationDetails {
    auto_cancel?: boolean;
    channel?: string;
    color?: string;
    data?: TData;
    large_icon?: string;
    loop_sound?: boolean;
    message?: string;
    play_sound?: boolean;
    small_icon?: string;
    snooze_interval?: number;
    sound_name?: string;
    sound_names?: string;
    tag?: string;
    ticker?: string;
    title?: string;
    has_button?: boolean;
    vibration?: number;
    use_big_text?: boolean;
    volume?: number;
    bypass_dnd?: boolean;
}
export interface IScheduleAlarmDetails extends ISendNotificationDetails {
    repeat_interval?: TRepeatIntervals;
    schedule_type?: TScheduleTypes;
    vibrate?: boolean;
    interval_value?: number;
    fire_date: string;
}
declare type TScheduleAlarmDetailsResolve = {
    id: number;
};
export interface IScheduledAlarms {
    volume: number;
    vibration: number;
    vibrate: boolean;
    title: string;
    ticker: string;
    soundName: string;
    color: string;
    snoozeInterval: number;
    playSound: boolean;
    tag: string;
    month: number;
    minute: number;
    loopSound: boolean;
    second: number;
    bypassDnd: boolean;
    message: string;
    hasButton: boolean;
    interval: string;
    largeIcon: "";
    year: number;
    alarmId: number;
    channel: string;
    day: number;
    scheduleType: string;
    useBigText: boolean;
    active: number;
    intervalValue: number;
    id: number;
    data: string;
    smallIcon: string;
    autoCancel: boolean;
    hour: number;
}
export interface IRequestPermissionsIOS {
    alert?: boolean;
    badge?: boolean;
    sound?: boolean;
}
export interface ICheckPermissionsIOS {
    notificationCenter: boolean;
    alert: boolean;
    lockScreen: boolean;
    badge: boolean;
    sound: boolean;
}
export declare type TCheckPermissionsCallback = (permissions: ICheckPermissionsIOS) => void;
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
declare const RNAlarmNotification: RNAlarmNotificationI;
export default RNAlarmNotification;
