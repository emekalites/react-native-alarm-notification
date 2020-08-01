import React, {Component} from 'react';
import {View, Text, Button, TextInput, DeviceEventEmitter} from 'react-native';
import ReactNativeAN from 'react-native-alarm-notification';

const alarmNotifData = {
  title: 'Alarm',
  message: 'Stand up',
  vibrate: true,
  play_sound: true,
  schedule_type: 'once',
  channel: 'wakeup',
  data: {content: 'my notification id is 22'},
  loop_sound: true,
};

class App extends Component {
  state = {
    fireDate: ReactNativeAN.parseDate(new Date(Date.now())),
    update: '',
    futureFireDate: '1000',
  };

  setAlarm = async () => {
    const {fireDate} = this.state;
    const details = {...alarmNotifData, fire_date: fireDate};
    console.log(`alarm set: ${fireDate}`);
    this.setState({update: `alarm set: ${fireDate}`});
    const alarm = await ReactNativeAN.scheduleAlarm(details);
    console.log(alarm);
  };

  setFutureAlarm = async () => {
    const {futureFireDate} = this.state;
    const fire_date = ReactNativeAN.parseDate(
      new Date(Date.now() + parseInt(futureFireDate, 10)),
    );
    const details = {...alarmNotifData, fire_date};
    console.log(`alarm set: ${fire_date}`);
    this.setState({update: `alarm set: ${fire_date}`});
    const alarm = await ReactNativeAN.scheduleAlarm(details);
    console.log(alarm);
  };

  stopAlarm = () => {
    this.setState({update: ''});
    ReactNativeAN.stopAlarmSound();
  };

  sendNotification = () => {
    const details = {
      ...alarmNotifData,
      data: {content: 'my notification id is 45'},
    };
    console.log(details);
    ReactNativeAN.sendNotification(details);
  };

  componentDidMount() {
    DeviceEventEmitter.addListener('OnNotificationDismissed', async function (
      e,
    ) {
      const obj = JSON.parse(e);
      console.log(`Notification id: ${obj.id} dismissed`);
    });

    DeviceEventEmitter.addListener('OnNotificationOpened', async function (e) {
      const obj = JSON.parse(e);
      console.log(obj);
    });
  }

  viewAlarms = async () => {
    const list = await ReactNativeAN.getScheduledAlarms();
    this.setState({update: JSON.stringify(list)});
  };

  componentWillUnmount() {
    DeviceEventEmitter.removeListener('OnNotificationDismissed');
    DeviceEventEmitter.removeListener('OnNotificationOpened');
  }

  render() {
    const {update, fireDate, futureFireDate} = this.state;
    return (
      <View style={{flex: 1, padding: 20}}>
        <Text>Alarm Date (01-01-1976 00:00:00)</Text>
        <View>
          <TextInput
            style={{height: 40, borderColor: 'gray', borderWidth: 1}}
            onChangeText={(text) => this.setState({fireDate: text})}
            value={fireDate}
          />
        </View>
        <View>
          <Text>Alarm Time From Now (eg 5):</Text>
          <TextInput
            style={{height: 40, borderColor: 'gray', borderWidth: 1}}
            onChangeText={(text) => this.setState({futureFireDate: text})}
            value={futureFireDate}
          />
        </View>
        <View style={{marginVertical: 18}}>
          <Button onPress={this.setAlarm} title="Set Alarm" color="#7fff00" />
        </View>
        <View style={{marginVertical: 18}}>
          <Button
            onPress={this.setFutureAlarm}
            title="Set Future Alarm"
            color="#7fff00"
          />
        </View>
        <View style={{marginVertical: 18}}>
          <Button
            onPress={this.sendNotification}
            title="Send Notification Now"
            color="#7fff00"
          />
        </View>
        <View style={{marginVertical: 18}}>
          <Button
            onPress={this.stopAlarm}
            title="Stop Alarm Sound"
            color="#841584"
          />
        </View>
        <View style={{marginVertical: 18}}>
          <Button
            onPress={this.viewAlarms}
            title="See all active alarms"
            color="#841584"
          />
        </View>
        <Text>{update}</Text>
      </View>
    );
  }
}

export default App;
