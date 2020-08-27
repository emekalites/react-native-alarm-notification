#import "RnAlarmNotification.h"

#import <UserNotifications/UserNotifications.h>

#import <React/RCTBridge.h>
#import <React/RCTConvert.h>
#import <React/RCTEventDispatcher.h>
#import <React/RCTUtils.h>
#import <AudioToolbox/AudioToolbox.h>

@implementation RnAlarmNotification

RCT_EXPORT_MODULE(RNAlarmNotification)

- (void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void (^)(UNNotificationPresentationOptions options))completionHandler  API_AVAILABLE(ios(10.0)) {
    if ([response.actionIdentifier isEqualToString:UNNotificationDismissActionIdentifier]) {
        NSLog(@"dimiss notification");
    } else if ([response.actionIdentifier isEqualToString:UNNotificationDefaultActionIdentifier]) {
        NSLog(@"open app");
    }
    
    completionHandler(UNNotificationPresentationOptionSound);
}

- (void)userNotificationCenter:(UNUserNotificationCenter *)center
didReceiveNotificationResponse:(UNNotificationResponse *)response
         withCompletionHandler:(void (^)(void))completionHandler  API_AVAILABLE(ios(10.0)){
    if ([response.notification.request.content.categoryIdentifier isEqualToString:@"TIMER_EXPIRED"]) {
        if ([response.actionIdentifier isEqualToString:@"SNOOZE_ACTION"]) {
            NSLog(@"snooze notification");
        } else if ([response.actionIdentifier isEqualToString:@"STOP_ACTION"]) {
            NSLog(@"delete notifications");
        }
        
//        completionHandler();
    }
}

+ (void)vibratePhone {
    NSLog(@"vibratePhone %@", @"here");
//    if([[UIDevice currentDevice].model isEqualToString:@"iPhone"]) {
//        AudioServicesPlaySystemSound (kSystemSoundID_Vibrate);
//    } else {
//        AudioServicesPlayAlertSound (kSystemSoundID_Vibrate);
//    }
//
//    [RnAlarmNotification vibratePhone];
}

RCT_EXPORT_METHOD(scheduleAlarm: (NSDictionary *)details resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    if (@available(iOS 10.0, *)) {
        UNMutableNotificationContent* content = [[UNMutableNotificationContent alloc] init];
        content.title = [NSString localizedUserNotificationStringForKey:details[@"title"] arguments:nil];
        content.body = [NSString localizedUserNotificationStringForKey:details[@"message"] arguments:nil];
        
        NSArray *fire_date = [details[@"fire_date"] componentsSeparatedByString:@" "];
        NSString *date = fire_date[0];
        NSString *time = fire_date[1];
        
        NSArray *splitDate = [date componentsSeparatedByString:@"-"];
        NSArray *splitHour = [time componentsSeparatedByString:@":"];
        
        NSString *strNumDay = splitDate[0];
        NSString *strNumMonth = splitDate[1];
        NSString *strNumYear = splitDate[2];
        
        NSString *strNumHour = splitHour[0];
        NSString *strNumMinute = splitHour[1];
        NSString *strNumSecond = splitHour[2];
        
        // Configure the trigger for date
        NSDateComponents* fireDate = [[NSDateComponents alloc] init];
        fireDate.day = [strNumDay intValue];
        fireDate.month = [strNumMonth intValue];
        fireDate.year = [strNumYear intValue];
        fireDate.hour = [strNumHour intValue];
        fireDate.minute = [strNumMinute intValue];
        fireDate.second = [strNumSecond intValue];
        fireDate.timeZone = [NSTimeZone defaultTimeZone];
        
        UNCalendarNotificationTrigger* trigger = [UNCalendarNotificationTrigger
                                                  triggerWithDateMatchingComponents:fireDate repeats:NO];
        
        content.sound = [UNNotificationSound defaultSound];
        
        NSString *alarmId = [NSString stringWithFormat: @"%ld", (long) NSDate.date.timeIntervalSince1970];
        
        // Create the request object.
        UNNotificationRequest* request = [UNNotificationRequest
                                          requestWithIdentifier:alarmId content:content trigger:trigger];
        
        UNUserNotificationCenter* center = [UNUserNotificationCenter currentNotificationCenter];
        
        [center addNotificationRequest:request withCompletionHandler:^(NSError * _Nullable error) {
            if (error != nil) {
                NSLog(@"%@", error.localizedDescription);
                reject(@"error", nil, error);
            }
        }];
        
        NSDictionary *alarm = [NSDictionary dictionaryWithObjectsAndKeys: alarmId, @"id", nil];
        
        resolve(alarm);
    } else {
        // Fallback on earlier versions
    }
}

RCT_EXPORT_METHOD(sendNotification: (NSDictionary *)details){
    NSLog(@"send notification");
}

RCT_EXPORT_METHOD(deleteAlarm: (NSInteger *)id){
    NSLog(@"delete alarm: %li", (long) id);
}

RCT_EXPORT_METHOD(deleteRepeatingAlarm: (NSInteger *)id){
    NSLog(@"delete alarm: %li", (long) id);
}

RCT_EXPORT_METHOD(stopAlarmSound){
    NSLog(@"stop alarm sound");
}

RCT_EXPORT_METHOD(removeFiredNotification: (NSInteger)id){
    NSLog(@"remove fired notification: %li", (long) id);
}

RCT_EXPORT_METHOD(removeAllFiredNotifications){
    NSLog(@"remove all notifications");
    if (@available(iOS 10.0, *)) {
        if ([UNUserNotificationCenter class]) {
            UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
            [center removeAllDeliveredNotifications];
        }
    } else {
        // Fallback on earlier versions
    }
}

//RCT_EXPORT_METHOD(getScheduledAlarms: (RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
//    NSLog(@"get all notifications");
//    NSArray<UILocalNotification *> *scheduledLocalNotifications = RCTSharedApplication().scheduledLocalNotifications;
//    NSMutableArray<NSDictionary *> *formattedScheduledLocalNotifications = [NSMutableArray new];
//    for (UILocalNotification *notification in scheduledLocalNotifications) {
//        [formattedScheduledLocalNotifications addObject:RCTFormatLocalNotification(notification)];
//    }
//    resolve(@[formattedScheduledLocalNotifications]);
//}

RCT_EXPORT_METHOD(requestPermissions:(NSDictionary *)permissions
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {
    if (RCTRunningInAppExtension()) {
        reject(@"E_UNABLE_TO_REQUEST_PERMISSIONS", nil, RCTErrorWithMessage(@"Requesting push notifications is currently unavailable in an app extension"));
        return;
    }
    
    UIUserNotificationType types = UIUserNotificationTypeNone;
    if (permissions) {
        if ([RCTConvert BOOL:permissions[@"alert"]]) {
            types |= UIUserNotificationTypeAlert;
        }
        if ([RCTConvert BOOL:permissions[@"badge"]]) {
            types |= UIUserNotificationTypeBadge;
        }
        if ([RCTConvert BOOL:permissions[@"sound"]]) {
            types |= UIUserNotificationTypeSound;
        }
    } else {
        types = UIUserNotificationTypeAlert | UIUserNotificationTypeBadge | UIUserNotificationTypeSound;
    }
    
    if (@available(iOS 10.0, *)) {
        [UNUserNotificationCenter.currentNotificationCenter
         requestAuthorizationWithOptions:types
         completionHandler:^(BOOL granted, NSError *_Nullable error) {
            
            if (error != NULL) {
                reject(@"-1", @"Error - Push authorization request failed.", error);
            } else {
                dispatch_async(dispatch_get_main_queue(), ^(void){
                    [RCTSharedApplication() registerForRemoteNotifications];
                });
                [UNUserNotificationCenter.currentNotificationCenter getNotificationSettingsWithCompletionHandler:^(UNNotificationSettings * _Nonnull settings) {
                    resolve(RCTPromiseResolveValueForUNNotificationSettings(settings));
                }];
            }
        }];
    } else {
        // Fallback on earlier versions
        resolve(nil);
    }
}

RCT_EXPORT_METHOD(checkPermissions:(RCTResponseSenderBlock)callback) {
    if (RCTRunningInAppExtension()) {
        callback(@[RCTSettingsDictForUNNotificationSettings(NO, NO, NO, NO, NO)]);
        return;
    }
    
    if (@available(iOS 10.0, *)) {
        [UNUserNotificationCenter.currentNotificationCenter getNotificationSettingsWithCompletionHandler:^(UNNotificationSettings * _Nonnull settings) {
            callback(@[RCTPromiseResolveValueForUNNotificationSettings(settings)]);
        }];
    } else {
        // Fallback on earlier versions
    }
}

API_AVAILABLE(ios(10.0))
static inline NSDictionary *RCTPromiseResolveValueForUNNotificationSettings(UNNotificationSettings* _Nonnull settings) {
    return RCTSettingsDictForUNNotificationSettings(settings.alertSetting == UNNotificationSettingEnabled, settings.badgeSetting == UNNotificationSettingEnabled, settings.soundSetting == UNNotificationSettingEnabled, settings.lockScreenSetting == UNNotificationSettingEnabled, settings.notificationCenterSetting == UNNotificationSettingEnabled);
}

static inline NSDictionary *RCTSettingsDictForUNNotificationSettings(BOOL alert, BOOL badge, BOOL sound, BOOL lockScreen, BOOL notificationCenter) {
    return @{@"alert": @(alert), @"badge": @(badge), @"sound": @(sound), @"lockScreen": @(lockScreen), @"notificationCenter": @(notificationCenter)};
}

@end
