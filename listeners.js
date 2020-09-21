import { NativeModules, NativeEventEmitter, Platform } from 'react-native';

const IncomingCallModule = NativeModules.IncomingCall;
const eventEmitter = new NativeEventEmitter(IncomingCallModule);

const RNIncomingCallPerformAnswerCallAction = 'RNIncomingCallPerformAnswerCallAction';
const RNIncomingCallPerformEndCallAction = 'RNIncomingCallPerformEndCallAction';
const RNIncomingCallNotificationTap = 'RNIncomingCallNotificationTap';


const onCallAnswer = handler =>
  eventEmitter.addListener(RNIncomingCallPerformAnswerCallAction, (data) => {
    handler(data)
  });

const onCallReject = handler =>
  eventEmitter.addListener(RNIncomingCallPerformEndCallAction, (data) =>{
    handler(data)}
  );

const onCallNotificationOpen = handler =>
  eventEmitter.addListener(RNIncomingCallNotificationTap, handler);  

export const listeners = {
  onCallAnswer,
  onCallReject,
  onCallNotificationOpen
};