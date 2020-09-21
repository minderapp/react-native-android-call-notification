package com.incomingcall;

import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.net.Uri;
import android.os.Vibrator;
import android.content.Context;
import android.media.MediaPlayer;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.learnium.RNDeviceInfo.RNDeviceInfo;

import com.facebook.react.shell.MainReactPackage;
import com.reactcommunity.rnlocalize.RNLocalizePackage;
import com.reactnativecommunity.rnpermissions.RNPermissionsPackage;
import com.reactnativenavigation.NavigationActivity;
import com.squareup.picasso.Picasso;

import io.agora.rtc.react.RCTAgoraRtcPackage;

public class UnlockScreenActivity extends AppCompatActivity implements UnlockScreenActivityInterface {

  private static final String TAG = "MessagingService";
  private TextView tvName;
  private TextView tvInfo;
  private ImageView ivAvatar;

  IncomingCallNotification notification = null;

  static boolean active = false;
  private static Vibrator v = (Vibrator) IncomingCallModule.reactContext.getSystemService(Context.VIBRATOR_SERVICE);
  private long[] pattern = {0, 1000, 800};
  private static MediaPlayer player = MediaPlayer.create(IncomingCallModule.reactContext, Settings.System.DEFAULT_RINGTONE_URI);
  private static Activity fa;

  private ReactRootView mReactRootView;
  private ReactInstanceManager mReactInstanceManager;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_call_incoming);

        tvName = findViewById(R.id.tvName);
        tvInfo = findViewById(R.id.tvInfo);
        ivAvatar = findViewById(R.id.ivAvatar);
        fa = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
          setShowWhenLocked(true);
          setTurnScreenOn(true);
        }

        notification = new IncomingCallNotification(getIntent());

        if (notification.callerName != null) {
          tvName.setText(notification.callerName);
        }

        if (notification.callerAvatarUrl != null) {
          if (notification.callerAvatarUrl != null) {
            Picasso.get().load(notification.callerAvatarUrl).transform(new CircleTransform()).into(ivAvatar);
          }
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        v.vibrate(pattern, 0);
        player.start();

      AnimateImage acceptCallBtn = findViewById(R.id.ivAcceptCall);
        acceptCallBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
            try {
                v.cancel();
                player.stop();

              KeyguardManager km = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                km.requestDismissKeyguard(fa, null); // you may add callback listener here
              } else {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
              }

              acceptDialing(notification);

            } catch (Exception e) {
                WritableMap params = Arguments.createMap();
                params.putString("message", e.getMessage());
                sendEvent("error", params);
                dismissDialing();
            }
            }
        });

        AnimateImage rejectCallBtn = findViewById(R.id.ivDeclineCall);
        rejectCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            v.cancel();
            player.stop();
            dismissDialing();
            }
        });
//      ArrayList<ReactPackage> filteredReactPackageList = new ArrayList<ReactPackage>();
//      for(ReactPackage reactPackage: IncomingCallModule.mReactNativeHost) {
//        if (!reactPackage.getClass().getName().equals("com.incomingcall.IncomingCallPackage") && !reactPackage.getClass().getName().equals("com.facebook.react.shell.MainReactPackage")) {
//          filteredReactPackageList.add(reactPackage);
//        }
//      }
//        mReactRootView = new ReactRootView(this);
//        mReactInstanceManager = ReactInstanceManager.builder()
//          .setApplication(getApplication())
//          .setBundleAssetName("index.android.bundle")
//          .setJSMainModulePath("calling.android")
//          .setCurrentActivity(this)
//          .addPackage(new MainReactPackage())
//          .addPackages(IncomingCallModule.mReactNativeHost)
////          .addPackage(new MainReactPackage())
////          .addPackage(new RCTAgoraRtcPackage())
////          .addPackage(new RNPermissionsPackage())
////          .addPackage(new RNDeviceInfo())
////          .addPackage(new RNLocalizePackage())
//          .setUseDeveloperSupport(BuildConfig.DEBUG)
//          .setInitialLifecycleState(LifecycleState.BEFORE_CREATE)
//          .build();
//      mReactRootView.startReactApplication(mReactInstanceManager, "CallApp", null);
    }

    @Override
    public void onBackPressed() {
        // Dont back
    }

    public static void dismissIncoming() {
        v.cancel();
        player.stop();
        fa.finish();
    }

    private void acceptDialing(IncomingCallNotification notification) {
      Intent intent = new Intent(getApplicationContext(), IncomingCallBroadcastReceiver.class);
      notification.populateIntentExtras(intent, "answer");
      sendBroadcast(intent);
//      finish();
      setContentView(mReactRootView);
    }

    private void dismissDialing() {
        WritableMap params = Arguments.createMap();
        params.putBoolean("accept", false);
        if (notification != null) {
          params.putString("uuid", notification.uuid);
        }
        if (!IncomingCallModule.reactContext.hasCurrentActivity()) {
            params.putBoolean("isHeadless", true);
        }
        sendEvent("endCall", params);
        finish();
    }

    @Override
    public void onConnected() {
        Log.d(TAG, "onConnected: ");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "onDisconnected: ");

    }

    @Override
    public void onConnectFailure() {
        Log.d(TAG, "onConnectFailure: ");

    }

    @Override
    public void onIncoming(ReadableMap params) {
        Log.d(TAG, "onIncoming: ");
    }

    private void sendEvent(String eventName, WritableMap params) {
        IncomingCallModule.reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}
