package com.emekalites.react.alarm.notification;

import java.io.Serializable;

public class AlarmModel implements Serializable {
    private int id;

    private int minute;
    private int hour;
    private int second;

    private int day;
    private int month;
    private int year;

    private int alarmId;
    private String title = "My Notification Title";
    private String message = "My Notification Message";
    private String channel = "my_channel_id";
    private String ticker;
    private boolean autoCancel = true;
    private boolean vibrate = true;
    private int vibration = 100;
    private String smallIcon = "ic_launcher";
    private String largeIcon;
    private boolean playSound = true;
    private String soundName;
    private String soundNames;                 // separate sounds with comma eg (sound1.mp3,sound2.mp3)
    private String color = "red";
    private String scheduleType = "once";      // once or repeat
    private int interval = 1;                       // in minutes
    private int snoozeInterval = 1;                       // in minutes
    private String tag;
    private String data;
    private boolean loopSound = false;
    private boolean useBigText = false;

    private int active = 1;         // 1 = yes, 0 = no

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public boolean isAutoCancel() {
        return autoCancel;
    }

    public void setAutoCancel(boolean autoCancel) {
        this.autoCancel = autoCancel;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public int getVibration() {
        return vibration;
    }

    public void setVibration(int vibration) {
        this.vibration = vibration;
    }

    public String getSmallIcon() {
        return smallIcon;
    }

    public void setSmallIcon(String smallIcon) {
        this.smallIcon = smallIcon;
    }

    public String getLargeIcon() {
        return largeIcon;
    }

    public void setLargeIcon(String largeIcon) {
        this.largeIcon = largeIcon;
    }

    public boolean isPlaySound() {
        return playSound;
    }

    public void setPlaySound(boolean playSound) {
        this.playSound = playSound;
    }

    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }

    public String getSoundNames() {
        return soundNames;
    }

    public void setSoundNames(String soundNames) {
        this.soundNames = soundNames;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getSnoozeInterval() {
        return snoozeInterval;
    }

    public void setSnoozeInterval(int snoozeInterval) {
        this.snoozeInterval = snoozeInterval;
    }

    public boolean isLoopSound() {
        return loopSound;
    }

    public void setLoopSound(boolean loopSound) {
        this.loopSound = loopSound;
    }

    public boolean isUseBigText() {
        return useBigText;
    }

    public void setUseBigText(boolean useBigText) {
        this.useBigText = useBigText;
    }

    @Override
    public String toString() {
        return "AlarmModel{" +
                "id=" + id +
                ", second=" + second +
                ", minute=" + minute +
                ", hour=" + hour +
                ", day=" + day +
                ", month=" + month +
                ", year=" + year +
                ", alarmId=" + alarmId +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", channel='" + channel + '\'' +
                ", ticker='" + ticker + '\'' +
                ", autoCancel=" + autoCancel +
                ", vibrate=" + vibrate +
                ", vibration=" + vibration +
                ", smallIcon='" + smallIcon + '\'' +
                ", largeIcon='" + largeIcon + '\'' +
                ", playSound=" + playSound +
                ", soundName='" + soundName + '\'' +
                ", soundNames='" + soundNames + '\'' +
                ", color='" + color + '\'' +
                ", scheduleType='" + scheduleType + '\'' +
                ", interval=" + interval +
                ", snoozeInterval=" + snoozeInterval +
                ", tag='" + tag + '\'' +
                ", data='" + data + '\'' +
                ", loopSound=" + loopSound +
                ", useBigText=" + useBigText +
                ", active=" + active +
                '}';
    }
}
