import { IRequestPermissionsIOS, IScheduleAlarmDetails, ISendNotificationDetails, TCheckPermissionsCallback } from "./RNAlarmNotification";
export declare function scheduleAlarm(details: IScheduleAlarmDetails): Promise<{
    id: number;
}>;
export declare function sendNotification(details: ISendNotificationDetails): void;
export declare function deleteAlarm(id: number): void;
export declare function deleteRepeatingAlarm(id: number): void;
export declare function stopAlarmSound(): void;
export declare function removeFiredNotification(id: number): void;
export declare function removeAllFiredNotifications(): void;
export declare function getScheduledAlarms(): Promise<import("./RNAlarmNotification").IScheduledAlarms[]>;
export declare function parseDate(rawDate: Date): string;
export declare function requestPermissions(permissions: IRequestPermissionsIOS): Promise<any>;
export declare function checkPermissions(callback: TCheckPermissionsCallback): void;
