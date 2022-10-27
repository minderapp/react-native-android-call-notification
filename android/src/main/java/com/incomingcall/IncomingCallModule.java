package com.incomingcall;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.WindowManager;
import android.content.Context;
import android.util.Log;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class IncomingCallModule extends ReactContextBaseJavaModule implements ActivityEventListener {

  public static ReactApplicationContext reactContext;
  public static IncomingCall incomingCall;

  private static final String TAG = "RNIC:IncomingCallModule";
  private WritableMap headlessExtras;
  private HashMap<String, IncomingCallNotification> incomingNotifications = new HashMap<String, IncomingCallNotification>();

  @Override
  public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
    Log.d(TAG, "onActivityResult");
    incomingCall.processInitialEvent(intent, reactContext);
  }

  @Override
  public void onNewIntent(Intent intent){
    Log.d(TAG, "onNewIntent");
    incomingCall.processInitialEvent(intent, reactContext);
  }

  public IncomingCallModule(ReactApplicationContext context) {
    super(context);
    reactContext = context;
    incomingCall = new IncomingCall(reactContext);
    reactContext.addActivityEventListener(this);
  }

  @Override
  public String getName() {
      return "IncomingCall";
  }

  public void clearNotification(Integer notificationID) {
    NotificationManager notificationManager = notificationManager();
    notificationManager.cancel(notificationID);
  }

  @SuppressLint("WrongConstant")
  @ReactMethod
  public void display(ReadableMap jsonObject) {
    if (UnlockScreenActivity.active) {
      return;
    }
    IncomingCallNotification c = new IncomingCallNotification(jsonObject);
    incomingNotifications.put(c.uuid, c);

    if (reactContext != null) {
     incomingCall.displayNotification(c);
    }
  }

  private NotificationManager notificationManager() {
    return (NotificationManager) reactContext.getSystemService(Context.NOTIFICATION_SERVICE);
  }

  @ReactMethod
  public void dismiss(String uuid) {
    UnlockScreenActivity.dismissIncoming();
    IncomingCallNotification incomingCallNotification = incomingNotifications.get(uuid);
    if (incomingCallNotification != null) {
      incomingCall.clearNotification(incomingCallNotification.integerId);
      incomingNotifications.remove(uuid);
    }
    return;
  }

  private Context getAppContext() {
      return this.reactContext.getApplicationContext();
  }

  @ReactMethod
  public void backToForeground() {
      Context context = getAppContext();
      String packageName = context.getApplicationContext().getPackageName();
      Intent focusIntent = context.getPackageManager().getLaunchIntentForPackage(packageName).cloneFilter();
      Activity activity = getCurrentActivity();
      boolean isOpened = activity != null;
      Log.d(TAG, "backToForeground, app isOpened ?" + (isOpened ? "true" : "false"));

      if (isOpened) {
          focusIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
          if (activity.getCallingActivity() != null) {
              activity.startActivity(focusIntent);
          }
      }
  }

  @ReactMethod
  public void getExtrasFromHeadlessMode(Promise promise) {
      if (this.headlessExtras != null) {
          promise.resolve(this.headlessExtras);
          this.headlessExtras = null;
          return;
      }
      promise.resolve(null);
  }

  @ReactMethod
  public void isDeviceLocked(Promise promise) {
    KeyguardManager myKM = (KeyguardManager) reactContext.getSystemService(Context.KEYGUARD_SERVICE);
    promise.resolve(myKM.inKeyguardRestrictedInputMode());
  }

  @ReactMethod
  public void getInitialNotification(Promise promise) {
    Activity activity = getCurrentActivity();
    Intent intent = activity.getIntent();
    ArrayList<String> validActions = new ArrayList<String>(Arrays.asList("answer", "decline", "tap"));
    if (validActions.contains(intent.getAction())) {
      try {
        IncomingCallNotification n = new IncomingCallNotification(intent);
        WritableMap result = n.toWritableMap(intent.getAction());
        result.putBoolean("isPhoneLocked", intent.hasExtra("isPhoneLocked") && intent.getBooleanExtra("isPhoneLocked", false));
        promise.resolve(result);
      } catch (Exception ex) {
        promise.reject(ex);
      }
    }
    promise.resolve(null);
  }

  @ReactMethod
  public void allowAppToShowOnLockScreen() {
    Activity activity = getCurrentActivity();

    activity.runOnUiThread(() -> {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        activity.setShowWhenLocked(true);
        activity.setTurnScreenOn(true);
      }
      activity.getWindow().addFlags(
          WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
          | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
          | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
          | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
      );
    });
  }

  @ReactMethod
  public void preventAppFromShowingOnLockScreen() {
    Activity activity = getCurrentActivity();
    activity.runOnUiThread(() -> {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        activity.setShowWhenLocked(false);
        activity.setTurnScreenOn(false);
      }
      activity.getWindow().clearFlags(
          WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
          | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
          | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
          | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
      );
    });
  }

  public static void sendEvent(String event, WritableMap params) {
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(event, params);
  }

}

