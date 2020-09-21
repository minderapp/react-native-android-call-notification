package com.incomingcall;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IncomingCallModule extends ReactContextBaseJavaModule implements ActivityEventListener {

  public static ReactApplicationContext reactContext;
  public static IncomingCall incomingCall;
  public static List<ReactPackage> mReactNativeHost;

  private static final String TAG = "RNIC:IncomingCallModule";
  private WritableMap headlessExtras;

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

  public IncomingCallModule(ReactApplicationContext context, List<ReactPackage> reactNativeHost) {
    super(context);
    reactContext = context;
    incomingCall = new IncomingCall(reactContext);
    reactContext.addActivityEventListener(this);
    mReactNativeHost = reactNativeHost;
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

    if (reactContext != null) {
     incomingCall.displayNotification(c);
    }
  }

  private NotificationManager notificationManager() {
    return (NotificationManager) reactContext.getSystemService(Context.NOTIFICATION_SERVICE);
  }

  @ReactMethod
  public void dismiss() {
    UnlockScreenActivity.dismissIncoming();
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
          activity.startActivity(focusIntent);
      }
  }

  @SuppressLint("WrongConstant")
  @ReactMethod
  public void openAppFromHeadlessMode(String uuid) {
      Context context = getAppContext();
      String packageName = context.getApplicationContext().getPackageName();
      Intent focusIntent = context.getPackageManager().getLaunchIntentForPackage(packageName).cloneFilter();
      Activity activity = getCurrentActivity();
      boolean isOpened = activity != null;

      if (!isOpened) {
          focusIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                  WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                  WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                  WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

          final WritableMap response = new WritableNativeMap();
          response.putBoolean("isHeadless", true);
          response.putString("uuid", uuid);

          this.headlessExtras = response;

          getReactApplicationContext().startActivity(focusIntent);
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
  public void getInitialNotification(Promise promise) {
    Activity activity = getCurrentActivity();
    Intent intent = activity.getIntent();
    ArrayList<String> validActions = new ArrayList<String>(Arrays.asList("answer", "decline", "tap"));
    if (validActions.contains(intent.getAction())) {
      try {
        IncomingCallNotification n = new IncomingCallNotification(intent);
        promise.resolve(n.toWritableMap(intent.getAction()));
      } catch (Exception ex) {
        promise.reject(ex);
      }
    }
    promise.resolve(null);
  }

}
