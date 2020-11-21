'use strict';
var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.checkPermissions = exports.requestPermissions = exports.parseDate = exports.getScheduledAlarms = exports.removeAllFiredNotifications = exports.removeFiredNotification = exports.stopAlarmSound = exports.deleteRepeatingAlarm = exports.deleteAlarm = exports.sendNotification = exports.scheduleAlarm = void 0;
var RNAlarmNotification_1 = __importDefault(require("./RNAlarmNotification"));
var parseDateString = function (string) {
    var splits = string.split(' ');
    var dateSplits = splits[0].split('-');
    var timeSplits = splits[1].split(':');
    var year = dateSplits[2];
    var month = dateSplits[1];
    var day = dateSplits[0];
    var hours = timeSplits[0];
    var minutes = timeSplits[1];
    var seconds = timeSplits[2];
    return new Date(parseInt(year), parseInt(month) - 1, parseInt(day), parseInt(hours), parseInt(minutes), parseInt(seconds));
};
function scheduleAlarm(details) {
    return __awaiter(this, void 0, void 0, function () {
        var past, now, repeat_interval, interval_value;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    if (!details.fire_date || (details.fire_date && details.fire_date === '')) {
                        throw new Error('failed to schedule alarm because fire date is missing');
                    }
                    past = parseDateString(details.fire_date);
                    now = new Date();
                    if (past < now) {
                        throw new Error('failed to schedule alarm because fire date is in the past');
                    }
                    repeat_interval = details.repeat_interval || 'hourly';
                    interval_value = details.interval_value || 1;
                    if (isNaN(interval_value)) {
                        throw new Error('interval value should be a number');
                    }
                    if (repeat_interval === 'minutely' &&
                        (interval_value < 1 || interval_value > 59)) {
                        throw new Error('interval value should be between 1 and 59 minutes');
                    }
                    if (repeat_interval === 'hourly' &&
                        (interval_value < 1 || interval_value > 23)) {
                        throw new Error('interval value should be between 1 and 23 hours');
                    }
                    return [4, RNAlarmNotification_1.default.scheduleAlarm(__assign(__assign({}, details), { has_button: details.has_button || false, vibrate: (typeof details.vibrate === 'undefined' ? true : details.vibrate), play_sound: (typeof details.play_sound === 'undefined' ? true : details.play_sound), schedule_type: details.schedule_type || 'once', repeat_interval: repeat_interval,
                            interval_value: interval_value, volume: details.volume || 0.5, sound_name: details.sound_name || '', snooze_interval: details.snooze_interval || 1, data: details.data || {} }))];
                case 1: return [2, _a.sent()];
            }
        });
    });
}
exports.scheduleAlarm = scheduleAlarm;
function sendNotification(details) {
    RNAlarmNotification_1.default.sendNotification(__assign(__assign({}, details), { has_button: false, play_sound: (typeof details.play_sound === 'undefined' ? true : details.play_sound), volume: details.volume || 0.5, sound_name: details.sound_name || '', snooze_interval: details.snooze_interval || 1, data: details.data || {} }));
}
exports.sendNotification = sendNotification;
function deleteAlarm(id) {
    if (!id) {
        throw new Error('id is required to delete alarm');
    }
    RNAlarmNotification_1.default.deleteAlarm(id);
}
exports.deleteAlarm = deleteAlarm;
function deleteRepeatingAlarm(id) {
    if (!id) {
        throw new Error('id is required to delete alarm');
    }
    RNAlarmNotification_1.default.deleteRepeatingAlarm(id);
}
exports.deleteRepeatingAlarm = deleteRepeatingAlarm;
function stopAlarmSound() {
    return RNAlarmNotification_1.default.stopAlarmSound();
}
exports.stopAlarmSound = stopAlarmSound;
function removeFiredNotification(id) {
    if (!id) {
        throw new Error('id is required to remove notification');
    }
    RNAlarmNotification_1.default.removeFiredNotification(id);
}
exports.removeFiredNotification = removeFiredNotification;
function removeAllFiredNotifications() {
    RNAlarmNotification_1.default.removeAllFiredNotifications();
}
exports.removeAllFiredNotifications = removeAllFiredNotifications;
function getScheduledAlarms() {
    return __awaiter(this, void 0, void 0, function () {
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4, RNAlarmNotification_1.default.getScheduledAlarms()];
                case 1: return [2, _a.sent()];
            }
        });
    });
}
exports.getScheduledAlarms = getScheduledAlarms;
function parseDate(rawDate) {
    var hours;
    var day;
    var month;
    if (rawDate.getHours().toString().length === 1) {
        hours = "0" + rawDate.getHours();
    }
    else {
        hours = "" + rawDate.getHours();
    }
    if (rawDate.getDate().toString().length === 1) {
        day = "0" + rawDate.getDate();
    }
    else {
        day = "" + rawDate.getDate();
    }
    if (rawDate.getMonth().toString().length === 1) {
        month = "0" + (rawDate.getMonth() + 1);
    }
    else {
        month = "" + (rawDate.getMonth() + 1);
    }
    return day + "-" + month + "-" + rawDate.getFullYear() + " " + hours + ":" + rawDate.getMinutes() + ":" + rawDate.getSeconds();
}
exports.parseDate = parseDate;
function requestPermissions(permissions) {
    return __awaiter(this, void 0, void 0, function () {
        var requestedPermissions;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    requestedPermissions = {
                        alert: true,
                        badge: true,
                        sound: true,
                    };
                    if (permissions) {
                        requestedPermissions = {
                            alert: !!permissions.alert,
                            badge: !!permissions.badge,
                            sound: !!permissions.sound,
                        };
                    }
                    return [4, RNAlarmNotification_1.default.requestPermissions(requestedPermissions)];
                case 1: return [2, _a.sent()];
            }
        });
    });
}
exports.requestPermissions = requestPermissions;
function checkPermissions(callback) {
    RNAlarmNotification_1.default.checkPermissions(callback);
}
exports.checkPermissions = checkPermissions;
