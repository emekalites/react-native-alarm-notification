import PushNotificationIOS from "@react-native-community/push-notification-ios";

export default (data) => {
    const details = {
        alertBody: data.message,
        alertTitle: data.title,
    }
    console.log(details)
    return PushNotificationIOS.presentLocalNotification(details);
}