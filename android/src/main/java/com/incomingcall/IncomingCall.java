package com.incomingcall;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.facebook.react.bridge.ReactApplicationContext;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class IncomingCall {
  public final String callChannel = "Call";

  public Context context;

  public IncomingCall(Context reactContext){
      this.context = reactContext;
  }

  public Class getMainActivityClass() {
    String packageName = context.getPackageName();
    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
    String className = launchIntent.getComponent().getClassName();
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public PendingIntent getPendingBroadcastIntent(String type, IncomingCallNotification config){
    Intent intent = new Intent(context, IncomingCallBroadcastReceiver.class);
    config.populateIntentExtras(intent, type);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(
      context,
      config.integerId,
      intent,
       android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE : PendingIntent.FLAG_UPDATE_CURRENT
    );
    return pendingIntent;
  }

  public PendingIntent getActivityPendingIntent(String type, IncomingCallNotification config){
    Intent intent = new Intent(context, getMainActivityClass());
    config.populateIntentExtras(intent, type);
    PendingIntent pendingIntent = PendingIntent.getActivity(
      context,
      config.integerId,
      intent,
      android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE : PendingIntent.FLAG_UPDATE_CURRENT
    );
    return pendingIntent;
  }

  @SuppressLint("WrongConstant")
  public void displayNotification(IncomingCallNotification config) {
    Intent fullScreenIntent = new Intent(context, UnlockScreenActivity.class);
    fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    fullScreenIntent.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    config.populateIntentExtras(fullScreenIntent, "fillScreenIntent");

    PendingIntent contentIntent = PendingIntent.getActivity(
      context,
      123,
      fullScreenIntent,
     android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE : PendingIntent.FLAG_UPDATE_CURRENT);

    Notification notification = new NotificationCompat.Builder(context, callChannel)
      .setAutoCancel(true)
      .setDefaults(0)
      .setCategory(Notification.CATEGORY_CALL)
      .setOngoing(true)
      .setTimeoutAfter(config.duration)
      .setOnlyAlertOnce(true)
      .setAutoCancel(false)
      .setSmallIcon(R.drawable.ic_call_black_24dp)
      .setFullScreenIntent(contentIntent, true)
      .setContentIntent(getActivityPendingIntent("tap", config))
      .setPriority(Notification.PRIORITY_MAX)
      .setContentTitle(config.notificationTitle)
      .setContentText(config.notificationBody)
      .addAction(0, config.answerButtonLabel, getActivityPendingIntent("answer", config))
      .addAction(0, config.declineButtonLabel,  getPendingBroadcastIntent("dismiss", config))
      .build();

    NotificationManager notificationManager = notificationManager();
    createCallNotificationChannel(notificationManager);
    notificationManager.notify(config.integerId, notification);
  }

  public void createCallNotificationChannel(NotificationManager manager){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      Uri sounduri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
      NotificationChannel channel = new NotificationChannel(callChannel, callChannel, NotificationManager.IMPORTANCE_HIGH);
      channel.setDescription("Call Notifications");
      channel.setSound(sounduri ,
        new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
          .setUsage(AudioAttributes.USAGE_UNKNOWN).build());
      channel.setVibrationPattern(new long[]{0, 1000});
      channel.enableVibration(true);
      manager.createNotificationChannel(channel);
    }
  }

  public void clearNotification(Integer notificationID) {
    NotificationManager notificationManager = notificationManager();
    notificationManager.cancel(notificationID);
  }

  public void clearAllNorifications(){
    NotificationManager manager = notificationManager();
    manager.cancelAll();
  }

  private NotificationManager notificationManager() {
    return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
  }

  public void processInitialEvent(Intent intent, ReactApplicationContext reactContext){
    try{
      String action = intent.getAction();
      IncomingCallNotification notification = new IncomingCallNotification(intent);
      switch (action){
        case "answer":
          sendEvent(reactContext,"RNIncomingCallPerformAnswerCallAction", notification.toWritableMap(action));
          break;
        case "tap":
          sendEvent(reactContext,"RNIncomingCallNotificationTap", notification.toWritableMap(action));
          break;
        case "dismiss":
          sendEvent(reactContext,"RNIncomingCallPerformEndCallAction", notification.toWritableMap(action));
          break;
        default:
          break;
      }
    } catch(NullPointerException e){

    }
  }

  private void sendEvent(ReactApplicationContext reactContext, String eventName, @Nullable WritableMap params) {
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(eventName, params);
  }

}
