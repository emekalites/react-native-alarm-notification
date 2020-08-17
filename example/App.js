import React, {Component} from 'react';
import {
	View,
	Text,
	Button,
	TextInput,
	DeviceEventEmitter,
	StyleSheet,
	ToastAndroid,
	Platform,
} from 'react-native';
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
	has_button: true,
};

const repeatAlarmNotifData = {
	title: 'Alarm',
	message: 'Stand up',
	vibrate: true,
	play_sound: true,
	channel: 'wakeup',
	data: {content: 'my notification id is 22'},
	loop_sound: true,
	schedule_type: 'repeat',
	repeat_interval: 'minutely',
	interval_value: 5, // repeat after 5 minutes
};

class App extends Component {
	state = {
		fireDate: ReactNativeAN.parseDate(new Date(Date.now())),
		update: [],
		futureFireDate: '1000',
		alarmId: null,
	};

	setAlarm = async () => {
		const {fireDate, update} = this.state;

		const details = {...alarmNotifData, fire_date: fireDate};
		console.log(`alarm set: ${fireDate}`);

		try {
			const alarm = await ReactNativeAN.scheduleAlarm(details);
			console.log(alarm);
			if (alarm) {
				this.setState({
					update: [...update, {date: `alarm set: ${fireDate}`, id: alarm.id}],
				});
			}
		} catch (e) {
			console.log(e);
		}
	};

	setRpeatAlarm = async () => {
		const {fireDate, update} = this.state;

		const details = {...repeatAlarmNotifData, fire_date: fireDate};
		console.log(`alarm set: ${fireDate}`);

		try {
			const alarm = await ReactNativeAN.scheduleAlarm(details);
			console.log(alarm);
			if (alarm) {
				this.setState({
					update: [...update, {date: `alarm set: ${fireDate}`, id: alarm.id}],
				});
			}
		} catch (e) {
			console.log(e);
		}
	};

	setFutureAlarm = async () => {
		const {futureFireDate, update} = this.state;

		const fire_date = ReactNativeAN.parseDate(
			new Date(Date.now() + parseInt(futureFireDate, 10)),
		);
		const details = {...alarmNotifData, fire_date};
		console.log(`alarm set: ${fire_date}`);

		try {
			const alarm = await ReactNativeAN.scheduleAlarm(details);
			console.log(alarm);
			if (alarm) {
				this.setState({
					update: [...update, {date: `alarm set: ${fire_date}`, id: alarm.id}],
				});
			}
		} catch (e) {
			console.log(e);
		}
	};

	stopAlarmSound = () => {
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
		DeviceEventEmitter.addListener('OnNotificationDismissed', async (e) => {
			const obj = JSON.parse(e);
			console.log(`Notification id: ${obj.id} dismissed`);
		});

		DeviceEventEmitter.addListener('OnNotificationOpened', async (e) => {
			const obj = JSON.parse(e);
			console.log(obj);
		});

		if (Platform.OS === 'ios') {
			this.showPermissions();

			ReactNativeAN.requestPermissions({
				alert: true,
				badge: true,
				sound: true,
			}).then(
				(data) => {
					console.log('RnAlarmNotification.requestPermissions', data);
				},
				(data) => {
					console.log('RnAlarmNotification.requestPermissions failed', data);
				},
			);
		}
	}

	showPermissions = () => {
		ReactNativeAN.checkPermissions((permissions) => {
			console.log(permissions);
		});
	};

	viewAlarms = async () => {
		const list = await ReactNativeAN.getScheduledAlarms();

		const update = list.map((l) => ({
			date: `alarm: ${l.day}-${l.month}-${l.year} ${l.hour}:${l.minute}:${l.second}`,
			id: l.id,
		}));

		this.setState({update});
	};

	deleteAlarm = async () => {
		const {alarmId} = this.state;
		if (alarmId !== '') {
			console.log(`delete alarm: ${alarmId}`);

			const id = parseInt(alarmId, 10);
			ReactNativeAN.deleteAlarm(id);
			this.setState({alarmId: ''});

			ToastAndroid.show('Alarm deleted!', ToastAndroid.SHORT);

			await this.viewAlarms();
		}
	};

	componentWillUnmount() {
		DeviceEventEmitter.removeListener('OnNotificationDismissed');
		DeviceEventEmitter.removeListener('OnNotificationOpened');
	}

	render() {
		const {update, fireDate, futureFireDate, alarmId} = this.state;
		return (
			<View style={styles.wrapper}>
				<Text>Alarm Date (01-01-1976 00:00:00)</Text>
				<View>
					<TextInput
						style={styles.date}
						onChangeText={(text) => this.setState({fireDate: text})}
						value={fireDate}
					/>
				</View>
				<View>
					<Text>Alarm Time From Now (eg 5):</Text>
					<TextInput
						style={styles.date}
						onChangeText={(text) => this.setState({futureFireDate: text})}
						value={futureFireDate}
					/>
				</View>
				<View style={styles.margin}>
					<Button onPress={this.setAlarm} title="Set Alarm" color="#007fff" />
				</View>
				<View style={styles.margin}>
					<Button
						onPress={this.setFutureAlarm}
						title="Set Future Alarm"
						color="#007fff"
					/>
				</View>
				<View style={styles.margin}>
					<Button
						onPress={this.setRpeatAlarm}
						title="Set Alarm with Repeat"
						color="#007fff"
					/>
				</View>
				<View style={styles.margin}>
					<Button
						onPress={this.sendNotification}
						title="Send Notification Now"
						color="#007fff"
					/>
				</View>
				<View style={styles.margin}>
					<Button
						onPress={this.stopAlarmSound}
						title="Stop Alarm Sound"
						color="#841584"
					/>
				</View>
				<View>
					<TextInput
						style={styles.date}
						onChangeText={(text) => this.setState({alarmId: text})}
						value={alarmId}
					/>
				</View>
				<View style={styles.margin}>
					<Button
						onPress={this.deleteAlarm}
						title="Delete Alarm"
						color="#841584"
					/>
				</View>
				<View style={styles.margin}>
					<Button
						onPress={this.viewAlarms}
						title="See all active alarms"
						color="#841584"
					/>
				</View>
				<Text>{JSON.stringify(update, null, 2)}</Text>
			</View>
		);
	}
}

const styles = StyleSheet.create({
	wrapper: {flex: 1, padding: 20},
	date: {height: 40, borderColor: 'gray', borderWidth: 1},
	margin: {marginVertical: 8},
});

export default App;
