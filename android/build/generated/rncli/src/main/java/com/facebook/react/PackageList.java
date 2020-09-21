
package com.facebook.react;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainPackageConfig;
import com.facebook.react.shell.MainReactPackage;
import java.util.Arrays;
import java.util.ArrayList;

// @react-native-community/art
import com.reactnativecommunity.art.ARTPackage;
// @react-native-community/async-storage
import com.reactnativecommunity.asyncstorage.AsyncStoragePackage;
// @react-native-community/audio-toolkit
import com.reactnativecommunity.rctaudiotoolkit.AudioPackage;
// @react-native-community/blur
import com.cmcewen.blurview.BlurViewPackage;
// @react-native-community/cameraroll
import com.reactnativecommunity.cameraroll.CameraRollPackage;
// @react-native-community/geolocation
import com.reactnativecommunity.geolocation.GeolocationPackage;
// @react-native-community/netinfo
import com.reactnativecommunity.netinfo.NetInfoPackage;
// @react-native-community/slider
import com.reactnativecommunity.slider.ReactSliderPackage;
// @react-native-community/viewpager
import com.reactnativecommunity.viewpager.RNCViewPagerPackage;
// @yfuks/react-native-action-sheet
import com.actionsheet.ActionSheetPackage;
// bugsnag-react-native
import com.bugsnag.BugsnagReactNative;
// lottie-react-native
import com.airbnb.android.react.lottie.LottiePackage;
// react-native-agora
import io.agora.rtc.react.RCTAgoraRtcPackage;
// react-native-android-open-settings
import com.levelasquez.androidopensettings.AndroidOpenSettingsPackage;
// react-native-appsflyer
import com.appsflyer.reactnative.RNAppsFlyerPackage;
// react-native-branch
import io.branch.rnbranch.RNBranchPackage;
// react-native-callkeep
import io.wazo.callkeep.RNCallKeepPackage;
// react-native-camera
import org.reactnative.camera.RNCameraPackage;
// react-native-code-push
import com.microsoft.codepush.react.CodePush;
// react-native-config
import com.lugg.ReactNativeConfig.ReactNativeConfigPackage;
// react-native-contacts
import com.rt2zz.reactnativecontacts.ReactNativeContacts;
// react-native-date-picker
import com.henninghall.date_picker.DatePickerPackage;
// react-native-device-info
import com.learnium.RNDeviceInfo.RNDeviceInfo;
// react-native-fast-image
import com.dylanvann.fastimage.FastImageViewPackage;
// react-native-fbsdk
import com.facebook.reactnative.androidsdk.FBSDKPackage;
// react-native-firebase
import io.invertase.firebase.RNFirebasePackage;
// react-native-fs
import com.rnfs.RNFSPackage;
// react-native-geolocation-service
import com.agontuk.RNFusedLocation.RNFusedLocationPackage;
// react-native-gesture-handler
import com.swmansion.gesturehandler.react.RNGestureHandlerPackage;
// react-native-haptic-feedback
import com.mkuczera.RNReactNativeHapticFeedbackPackage;
// react-native-image-crop-picker
import com.reactnative.ivpusic.imagepicker.PickerPackage;
// react-native-image-picker
import com.imagepicker.ImagePickerPackage;
// react-native-image-resizer
import fr.bamlab.rnimageresizer.ImageResizerPackage;
// react-native-in-app-review
import com.ibits.react_native_in_app_review.AppReviewPackage;
// react-native-intercom
import com.robinpowered.react.Intercom.IntercomPackage;
// react-native-keychain
import com.oblador.keychain.KeychainPackage;
// react-native-linear-gradient
import com.BV.LinearGradient.LinearGradientPackage;
// react-native-localize
import com.reactcommunity.rnlocalize.RNLocalizePackage;
// react-native-maps
import com.airbnb.android.react.maps.MapsPackage;
// react-native-message-compose
import com.reactlibrary.messagecompose.RNMessageComposePackage;
// react-native-navigation
import com.reactnativenavigation.react.NavigationPackage;
// react-native-permissions
import com.reactnativecommunity.rnpermissions.RNPermissionsPackage;
// react-native-photo-view
import com.reactnative.photoview.PhotoViewPackage;
// react-native-reanimated
import com.swmansion.reanimated.ReanimatedPackage;
// react-native-restart
import com.reactnativerestart.RestartPackage;
// react-native-shake
import com.clipsub.RNShake.RNShakeEventPackage;
// react-native-svg
import com.horcrux.svg.SvgPackage;
// react-native-vector-icons
import com.oblador.vectoricons.VectorIconsPackage;
// react-native-video
import com.brentvatne.react.ReactVideoPackage;
// react-native-voip-call
import com.ajith.voipcall.RNVoipCallPackage;
// react-native-webview
import com.reactnativecommunity.webview.RNCWebViewPackage;
// rn-fetch-blob
import com.RNFetchBlob.RNFetchBlobPackage;

public class PackageList {
  private Application application;
  private ReactNativeHost reactNativeHost;
  private MainPackageConfig mConfig;

  public PackageList(ReactNativeHost reactNativeHost) {
    this(reactNativeHost, null);
  }

  public PackageList(Application application) {
    this(application, null);
  }

  public PackageList(ReactNativeHost reactNativeHost, MainPackageConfig config) {
    this.reactNativeHost = reactNativeHost;
    mConfig = config;
  }

  public PackageList(Application application, MainPackageConfig config) {
    this.reactNativeHost = null;
    this.application = application;
    mConfig = config;
  }

  private ReactNativeHost getReactNativeHost() {
    return this.reactNativeHost;
  }

  private Resources getResources() {
    return this.getApplication().getResources();
  }

  private Application getApplication() {
    if (this.reactNativeHost == null) return this.application;
    return this.reactNativeHost.getApplication();
  }

  private Context getApplicationContext() {
    return this.getApplication().getApplicationContext();
  }

  public ArrayList<ReactPackage> getPackages() {
    return new ArrayList<>(Arrays.<ReactPackage>asList(
      new MainReactPackage(mConfig),
      new ARTPackage(),
      new AsyncStoragePackage(),
      new AudioPackage(),
      new BlurViewPackage(),
      new CameraRollPackage(),
      new GeolocationPackage(),
      new NetInfoPackage(),
      new ReactSliderPackage(),
      new RNCViewPagerPackage(),
      new ActionSheetPackage(),
      BugsnagReactNative.getPackage(),
      new LottiePackage(),
      new RCTAgoraRtcPackage(),
      new AndroidOpenSettingsPackage(),
      new RNAppsFlyerPackage(),
      new RNBranchPackage(),
      new RNCallKeepPackage(),
      new RNCameraPackage(),
      new CodePush(getResources().getString(com.minderapps.minder.R.string.CodePushDeploymentKey), getApplicationContext(), com.minderapps.minder.BuildConfig.DEBUG),
      new ReactNativeConfigPackage(),
      new ReactNativeContacts(),
      new DatePickerPackage(),
      new RNDeviceInfo(),
      new FastImageViewPackage(),
      new FBSDKPackage(),
      new RNFirebasePackage(),
      new RNFSPackage(),
      new RNFusedLocationPackage(),
      new RNGestureHandlerPackage(),
      new RNReactNativeHapticFeedbackPackage(),
      new PickerPackage(),
      new ImagePickerPackage(),
      new ImageResizerPackage(),
      new AppReviewPackage(),
      new IntercomPackage(),
      new KeychainPackage(),
      new LinearGradientPackage(),
      new RNLocalizePackage(),
      new MapsPackage(),
      new RNMessageComposePackage(),
      new NavigationPackage(reactNativeHost),
      new RNPermissionsPackage(),
      new PhotoViewPackage(),
      new ReanimatedPackage(),
      new RestartPackage(),
      new RNShakeEventPackage(),
      new SvgPackage(),
      new VectorIconsPackage(),
      new ReactVideoPackage(),
      new RNVoipCallPackage(),
      new RNCWebViewPackage(),
      new RNFetchBlobPackage()
    ));
  }
}
