#import "RNAlarmNotification.h"
@import UserNotifications;
#import <React/RCTLog.h>


@implementation RNAlarmNotification : NSObject

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(sampleMethod:(NSString *)stringArgument numberParameter:(nonnull NSNumber *)numberArgument callback:(RCTResponseSenderBlock)callback)
{
    // TODO: Implement some actually useful functionality
    callback(@[[NSString stringWithFormat: @"numberArgument: %@ stringArgument: %@", numberArgument, stringArgument]]);
}


RCT_EXPORT_METHOD(scheduleAlarm:(NSDictionary *)details) {
    //RCTLogInfo( @"scheduleAlarm: '%@'", details );
    
    UILocalNotification *notification = [[UILocalNotification alloc] init];
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents *components = [[NSDateComponents alloc] init];
    
    NSArray *componentDate = [details[@"fire_date"] componentsSeparatedByString:@" "];
    NSString *date = componentDate[0];
    NSString *time = componentDate[1];
    //RCTLogInfo( @"date: '%@'", date );
    //RCTLogInfo( @"time: '%@'", time );
    
    NSArray *componentSplitDate = [date componentsSeparatedByString:@"-"];
    NSArray *componentSplitHour = [time componentsSeparatedByString:@":"];
    
    NSString *strNumDay = componentSplitDate[0];
    int day = [strNumDay intValue];
    
    NSString *strNumMonth = componentSplitDate[1];
    int month = [strNumMonth intValue];
    
    NSString *strNumYear = componentSplitDate[2];
    int year = [strNumYear intValue];
    
    NSString *strNumHour = componentSplitHour[0];
    int hour = [strNumHour intValue];
    
    NSString *strNumMinute = componentSplitHour[1];
    int minute = [strNumMinute intValue];
    
    NSString *strNumSecond = componentSplitHour[2];
    int second = [strNumSecond intValue];
    
    //RCTLogInfo( @"day: %d", day );
    //RCTLogInfo( @"month: %d", month );
    //RCTLogInfo( @"year: %d", year );
    //RCTLogInfo( @"hour: %d", hour );
    //RCTLogInfo( @"minute: %d", minute );
    //RCTLogInfo( @"second: %d", second );
    
    [components setDay:day];
    [components setMonth: month];
    [components setYear:year];
    [components setHour:hour];
    [components setMinute:minute];
    [components setSecond:second];
    [calendar setTimeZone: [NSTimeZone defaultTimeZone]];
    NSDate *dateToFire = [calendar dateFromComponents:components];
    //notification.fireDate = [[NSDate date] dateByAddingTimeInterval:10];
    notification.fireDate = dateToFire;
    notification.alertBody = details[@"message"];
    notification.alertTitle = details[@"title"];
    notification.timeZone = [NSTimeZone defaultTimeZone];
    notification.soundName = UILocalNotificationDefaultSoundName;
    //notification.applicationIconBadgeNumber = 10;
    
    NSDictionary *userDict = [NSDictionary dictionaryWithObject:@"alarm_id" forKey:details[@"alarm_id"]];
    notification.userInfo = userDict;
    
    //RCTLogInfo(@"Scheduled notification %@ ", notification);
    
    [[UIApplication sharedApplication] scheduleLocalNotification:notification];
    
}

RCT_EXPORT_METHOD(getScheduledAlarms:(RCTResponseSenderBlock)callback) {
    UIApplication *app = [UIApplication sharedApplication];
    NSArray *eventArray = [app scheduledLocalNotifications];
    for (int i=0; i<[eventArray count]; i++)
    {
        UILocalNotification* oneEvent = [eventArray objectAtIndex:i];
        NSDictionary *userInfoCurrent = oneEvent.userInfo;
        NSString *uid=[NSString stringWithFormat:@"%@",[userInfoCurrent valueForKey:@"uid"]];
        //RCTLogInfo(@"eventos %@ ", eventArray );
    }
    callback(eventArray);
}

RCT_EXPORT_METHOD(deleteAlarm:(NSString *)alarmID) {
    
    //RCTLogInfo(@"Evento a eliminar: %@", alarmID);
     NSString *myIDToCancel = alarmID;
     UILocalNotification *notificationToCancel=nil;
     for(UILocalNotification *aNotif in [[UIApplication sharedApplication] scheduledLocalNotifications]) {
         //RCTLogInfo(@"Eventos: %@", aNotif);
       if([[aNotif.userInfo objectForKey:alarmID] isEqualToString:myIDToCancel]) {
          notificationToCancel=aNotif;
          break;
       }
     }
     if(notificationToCancel) [[UIApplication sharedApplication] cancelLocalNotification:notificationToCancel];
}

@end
