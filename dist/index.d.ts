import { IRequestPermissionsIOS, IScheduleAlarmDetails, ISendNotificationDetails, TCheckPermissionsCallback } from "./RNAlarmNotification";
declare namespace ReactNativeAN {
    function scheduleAlarm(details: IScheduleAlarmDetails): Promise<{
        id: number;
    }>;
    function sendNotification(details: ISendNotificationDetails): void;
    function deleteAlarm(id: number): void;
    function deleteRepeatingAlarm(id: number): void;
    function stopAlarmSound(): void;
    function removeFiredNotification(id: number): void;
    function removeAllFiredNotifications(): void;
    function getScheduledAlarms(): Promise<import("./RNAlarmNotification").IScheduledAlarms[]>;
    function parseDate(rawDate: Date): string;
    function requestPermissions(permissions: IRequestPermissionsIOS): Promise<any>;
    function checkPermissions(callback: TCheckPermissionsCallback): void;
}
export default ReactNativeAN;
