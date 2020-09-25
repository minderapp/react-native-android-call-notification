package com.incomingcall;

import android.content.Intent;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.WritableMap;

import java.util.HashMap;
import java.util.Map;

public class IncomingCallNotification {
  String channel = "Calls";
  Integer duration = 30000;

  Integer integerId = 1234;
  String uuid = null;

  String notificationTitle = "Incoming Call";
  String notificationBody = "";
  String answerButtonLabel = "Answer";
  String declineButtonLabel = "Decline";
  String callerName = "";
  String callerAvatarUrl = null;

  HashMap<String, Object> data = null;

  public IncomingCallNotification(ReadableMap json) {

    if(checkType(json , "channelName", "String")){
      channel = json.getString("channel_name");
    }

    if(checkType(json , "integerId", "Number")){
      integerId = json.getInt("integerId");
    }

    if(checkType(json , "uuid", "String")){
      uuid = json.getString("uuid");
    }

    if(checkType(json , "duration", "Number")){
      duration = json.getInt("duration");
    }

    if(checkType(json , "notificationTitle", "String")){
      notificationTitle = json.getString("notificationTitle");
    }

    if(checkType(json , "notificationBody", "String")){
      notificationBody = json.getString("notificationBody");
    }

    if(checkType(json , "callerName", "String")){
      callerName = json.getString("callerName");
    }

    if(checkType(json , "answerButtonLabel", "String")){
      answerButtonLabel = json.getString("answerButtonLabel");
    }

    if(checkType(json , "declineButtonLabel", "String")){
      declineButtonLabel = json.getString("declineButtonLabel");
    }

    if(checkType(json , "callerAvatarUrl", "String")){
      callerAvatarUrl = json.getString("callerAvatarUrl");
    }

    if (checkType(json , "data", "Map")) {
      data = json.getMap("data").toHashMap();
    }

  }

  public IncomingCallNotification(Intent intent) {

    if(checkType(intent , "channelName", "String")){
      channel = intent.getStringExtra("channel_name");
    }

    if(checkType(intent , "integerId", "Number")){
      integerId = intent.getIntExtra("integerId", integerId);
    }

    if(checkType(intent , "uuid", "String")){
      uuid = intent.getStringExtra("uuid");
    }

    if(checkType(intent , "duration", "Number")){
      duration = intent.getIntExtra("duration", duration);
    }

    if(checkType(intent , "notificationTitle", "String")){
      notificationTitle = intent.getStringExtra("notificationTitle");
    }

    if(checkType(intent , "notificationBody", "String")){
      notificationBody = intent.getStringExtra("notificationBody");
    }

    if(checkType(intent , "callerName", "String")){
      callerName = intent.getStringExtra("callerName");
    }

    if(checkType(intent , "answerButtonLabel", "String")){
      answerButtonLabel = intent.getStringExtra("answerButtonLabel");
    }

    if(checkType(intent , "declineButtonLabel", "String")){
      declineButtonLabel = intent.getStringExtra("declineButtonLabel");
    }

    if(checkType(intent , "callerAvatarUrl", "String")){
      callerAvatarUrl = intent.getStringExtra("callerAvatarUrl");
    }

    if (checkType(intent , "data", "Map")) {
      data = new HashMap<String, Object>();
      HashMap<String, String> hm = (HashMap<String, String>)intent.getSerializableExtra("data");
      for (Map.Entry<String, String> entry : hm.entrySet()) {
        data.put(entry.getKey(), entry.getValue());
      }
    }

  }

  public void populateIntentExtras(Intent intent, String type) {
    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    intent.putExtra("id", integerId);
    intent.putExtra("action", type);

    intent.putExtra("uuid", uuid);
    intent.putExtra("channel", channel);
    intent.putExtra("duration", duration);
    intent.putExtra("integerId", integerId);
    intent.putExtra("notificationTitle", notificationTitle);
    intent.putExtra("notificationBody", notificationBody);
    intent.putExtra("answerButtonLabel", answerButtonLabel);
    intent.putExtra("declineButtonLabel", declineButtonLabel);
    intent.putExtra("callerName", callerName);
    intent.putExtra("callerAvatarUrl", callerAvatarUrl);
    if (data != null){
      intent.putExtra("data", data);
    }
    intent.setAction(type);
  }

  public WritableMap toWritableMap(String action) {
    WritableMap params = Arguments.createMap();
    params.putString("action", action);
    params.putInt("id", integerId);
    params.putString("uuid", uuid);
    if (data != null) {
      for (Map.Entry<String, Object> entry : data.entrySet()) {
        params.putString(entry.getKey(), (String)entry.getValue());
      }
    }

    return params;
  }
  public static boolean checkType(ReadableMap json, String key , String type){
    try {
      if (json.hasKey(key) && json.getType(key).toString().equals(type)){
        if (type.equals("String") && json.getType(key).toString().equals("String") && json.getString(key).toString().isEmpty()){
          return false;
        }
        return  true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  };

  public static boolean checkType(Intent intent, String key , String type){
    try {
      if (intent.hasExtra(key)){
        if (type.equals("String") && intent.getStringExtra(key).isEmpty()){
          return false;
        } else if (type.equals("Number") && intent.getIntExtra(key, Integer.MAX_VALUE) == Integer.MAX_VALUE ){
          return false;
        }
        return  true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  };

}
