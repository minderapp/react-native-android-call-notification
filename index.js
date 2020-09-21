import { NativeModules } from 'react-native';
import { listeners } from './listeners'
const { IncomingCall } = NativeModules;
const incomingCallEventHandlers = new Map();

IncomingCall.addEventListener = (type, handler) => {
    const listener = listeners[type](handler);
    incomingCallEventHandlers.set(type, listener);
};

export default IncomingCall;
