import React, { Component } from 'react';
import { View, Text, Button, TextInput, DeviceEventEmitter } from 'react-native';
import ReactNativeAN from 'react-native-alarm-notification';

const alarmNotifData = {
	id: "22",
	title: "Alarm",
	message: "Stand up",
	vibrate: true,
	play_sound: true,
	schedule_once: true,
	channel: "wakeup",
	data: { content: "my notification id is 22" },
};

class App extends Component {
	constructor(props, context) {
		super(props, context);
		this.state = {
			fireDate: '',
			update: '',
			futureFireDate: '0',
		};
		this.setAlarm = this.setAlarm.bind(this);
		this.stopAlarm = this.stopAlarm.bind(this);
	}

	setAlarm = () => {
		const { fireDate } = this.state;
		const details  = { ...alarmNotifData, fire_date: fireDate };
		console.log(`alarm set: ${fireDate}`);
		this.setState({ update: `alarm set: ${fireDate}` });
		ReactNativeAN.scheduleAlarm(details);
	};

	setFutureAlarm = () => {
		const { futureFireDate } = this.state;
		const fire_date = ReactNativeAN.parseDate(new Date(Date.now() + parseInt(futureFireDate)));
		const details  = { ...alarmNotifData, fire_date };
		console.log(`alarm set: ${fire_date}`);
		this.setState({ update: `alarm set: ${fire_date}` });
		ReactNativeAN.scheduleAlarm(details);
	};

	stopAlarm = () => {
		this.setState({ update: '' });
		ReactNativeAN.stopAlarm();
	};

	sendNotification = () => {
		const details = { ...alarmNotifData, id: 45, data: { content: "my notification id is 45" }, };
		console.log(details);
		ReactNativeAN.sendNotification(details);
	};

	componentDidMount() {
		DeviceEventEmitter.addListener('OnNotificationDismissed', async function(e) {
			const obj = JSON.parse(e);
			console.log(`Notification ${obj.id} dismissed`);
		});
		
		DeviceEventEmitter.addListener('OnNotificationOpened', async function(e) {
			const obj = JSON.parse(e);
			console.log(obj);
		});
	}
	
	componentWillUnmount() {
		DeviceEventEmitter.removeListener('OnNotificationDismissed');
		DeviceEventEmitter.removeListener('OnNotificationOpened');
	}
	

	render() {
		const { update, fireDate, futureFireDate } = this.state;
		return (
			<View style={{flex:1, padding: 20}}>
				<Text>Alarm Schedule</Text>
				<View>
					<TextInput
						style={{height: 40, borderColor: 'gray', borderWidth: 1}}
						onChangeText={(text) => this.setState({ fireDate: text })}
						value={fireDate}
					/>
				</View>
				<View>
					<Text>Future Time From Now:</Text>
					<TextInput
						style={{height: 40, borderColor: 'gray', borderWidth: 1}}
						onChangeText={(text) => this.setState({ futureFireDate: text })}
						value={futureFireDate}
					/>
				</View>
				<View style={{marginVertical: 18}}>
					<Button
						onPress={this.setAlarm}
						title="Set Alarm"
						color="#7fff00"
					/>
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
				<Text>{update}</Text>
			</View>
		);
	}
}

export default App;