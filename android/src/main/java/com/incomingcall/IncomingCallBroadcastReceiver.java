package com.incomingcall;

import android.app.Activity;
import android.app.Application;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

public class IncomingCallBroadcastReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Application applicationContext = (Application) context.getApplicationContext();
    // Activity activity = (Activity) context;

    IncomingCall incomingCall = new IncomingCall(context);
    Integer id = intent.getIntExtra("id", 0);
    String uuid = intent.getStringExtra("uuid");
    Log.d("BroadcastReceiver", intent.getAction());
    Log.d("BroadcastReceiver", context.getPackageName());

    switch (intent.getAction()){
      case "dismiss":
        IncomingCallNotification notification = new IncomingCallNotification(intent);
        IncomingCallModule.sendEvent( "RNIncomingCallPerformEndCallAction", notification.toWritableMap("dismiss"));
        incomingCall.clearNotification(id);
        break;
      case "answer":
        Intent startIntent = context
          .getPackageManager()
          .getLaunchIntentForPackage(context.getPackageName());
        startIntent.putExtras(intent);
        startIntent.setAction(intent.getAction());
        try {
          KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
          Boolean isPhoneLocked = myKM.inKeyguardRestrictedInputMode();
          if (isPhoneLocked) {
            startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
          }
          startIntent.putExtra("isPhoneLocked", isPhoneLocked);
        } catch (Exception ex) {}
        // if (activity.getCallingActivity() != null) {
          context.startActivity(startIntent);
        // }
        incomingCall.clearNotification(id);
        break;
      case "timeout":
        // rnVoipNotificationHelper.showMissCallNotification(intent.getStringExtra("missedCallTitle"), intent.getStringExtra("missedCallBody"), intent.getStringExtra("callerId"));
        break;
      default:
        break;
    }

  }
}
