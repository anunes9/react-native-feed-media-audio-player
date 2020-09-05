
package fm.feed.android.react;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fm.feed.android.playersdk.AvailabilityListener;
import fm.feed.android.playersdk.ClientIdListener;
import fm.feed.android.playersdk.FeedAudioPlayer;
import fm.feed.android.playersdk.FeedPlayerService;
import fm.feed.android.playersdk.PlayListener;
import fm.feed.android.playersdk.SkipListener;
import fm.feed.android.playersdk.State;
import fm.feed.android.playersdk.StateListener;
import fm.feed.android.playersdk.StationChangedListener;
import fm.feed.android.playersdk.models.Play;
import fm.feed.android.playersdk.models.Station;


public class RNFMAudioPlayerModule extends ReactContextBaseJavaModule implements StateListener,
        StationChangedListener, PlayListener, SkipListener {

  public final static String TAG = RNFMAudioPlayerModule.class.getName();

  private final ReactApplicationContext reactContext;
  private FeedAudioPlayer mFeedAudioPlayer;

  public RNFMAudioPlayerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNFMAudioPlayer";
  }


  @ReactMethod
  public void setClientID(String clientID){
    mFeedAudioPlayer.setClientId(clientID);
  }


  @ReactMethod
  public void requestClientId(){
    String str = mFeedAudioPlayer.getClientId();
    WritableMap params = Arguments.createMap();
    params.putString("ClientID", str);
    sendEvent(reactContext, "newClientID", params);
  }


  @ReactMethod
  public void createNewClientID() {
    mFeedAudioPlayer.createNewClientId(new ClientIdListener() {
      @Override
      public void onClientId(String s) {
        WritableMap params = Arguments.createMap();
        params.putString("ClientID", s);
        sendEvent(reactContext, "newClientID", params);
      }

      @Override
      public void onError() {
        Log.e(TAG, "Error while generating a new client id");
      }
    });
  }

  @ReactMethod
  public void initializeWithToken(String token, String secret, boolean enableBackgroundMusic) {

     AvailabilityListener listener = new AvailabilityListener() {
      @Override
      public void onPlayerAvailable(FeedAudioPlayer feedAudioPlayer) {
        WritableMap params = Arguments.createMap();
        params.putBoolean("available", true);
        String strStations = toJson(mFeedAudioPlayer.getStationList());

        try {
          JSONArray jsArray = new JSONArray(strStations);
          WritableArray wArray = convertJsonToArray(jsArray);
          params.putArray("stations", wArray);
          params.putInt("activeStationId", mFeedAudioPlayer.getActiveStation().getId());
          sendEvent(reactContext, "availability", params);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }

      @Override
      public void onPlayerUnavailable(Exception e) {
        WritableMap params = Arguments.createMap();
        params.putBoolean("available", false);
        sendEvent(reactContext, "availability", params);
      }
    };

    if (enableBackgroundMusic) {
      FeedPlayerService.initialize(reactContext, token, secret);

      mFeedAudioPlayer = FeedPlayerService.getInstance();
      FeedPlayerService.getInstance(listener);

    } else {
      mFeedAudioPlayer = new FeedAudioPlayer.Builder(reactContext, token, secret)
        .setAvailabilityListener(listener)
        .build();
    }

    mFeedAudioPlayer.addPlayListener(RNFMAudioPlayerModule.this);
    mFeedAudioPlayer.addSkipListener(RNFMAudioPlayerModule.this);
    mFeedAudioPlayer.addStationChangedListener(RNFMAudioPlayerModule.this);
    mFeedAudioPlayer.addStateListener(RNFMAudioPlayerModule.this);
  }


  @ReactMethod
  public void play(){
    mFeedAudioPlayer.play();
  }

  @ReactMethod
  public void pause() {
    mFeedAudioPlayer.pause();
  }

  @ReactMethod
  public void setActiveStation(Integer station) {

    //Log.i(TAG, "Station id ="+station.toString());
    boolean flag = false;
    for (Station st: mFeedAudioPlayer.getStationList()) {

      if(st.getId().toString().equals(station.toString()))
      {
        mFeedAudioPlayer.setActiveStation(st, false);
        flag = true;
        break;
      }
    }
    if(!flag)
    {
      Log.e(TAG, "Cannot set active station to "+station+" because no station found with that id");
    }
  }

  @ReactMethod
  public void skip() {
    mFeedAudioPlayer.skip();
  }

  @ReactMethod
  public void stop() {
    mFeedAudioPlayer.stop();
  }

  @ReactMethod
  public void setVolume(float volume) {
    mFeedAudioPlayer.setVolume(volume);
  }


  private void sendEvent(ReactContext reactContext,
                         String eventName,
                        @Nullable WritableMap params) {
    reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
  }

  private static Gson createDefaultGson() {
    GsonBuilder builder = new GsonBuilder();
    return builder.create();
  }


  private String toJson(Object json) {
    return createDefaultGson().toJson(json);
  }

  private WritableMap convertJsonToMap(JSONObject jsonObject) throws JSONException {
    WritableMap map = new WritableNativeMap();

    Iterator<String> iterator = jsonObject.keys();
    while (iterator.hasNext()) {
      String key = iterator.next();
      Object value = jsonObject.get(key);
      if (value instanceof JSONObject) {
        map.putMap(key, convertJsonToMap((JSONObject) value));
      } else if (value instanceof JSONArray) {
        map.putArray(key, convertJsonToArray((JSONArray) value));
        if(("option_values").equals(key)) {
          map.putArray("options", convertJsonToArray((JSONArray) value));
        }
      } else if (value instanceof Boolean) {
        map.putBoolean(key, (Boolean) value);
      } else if (value instanceof Integer) {
        map.putInt(key, (Integer) value);
      } else if (value instanceof Double) {
        map.putDouble(key, (Double) value);
      } else if (value instanceof String)  {
        map.putString(key, (String) value);
      } else {
        map.putString(key, value.toString());
      }
    }
    return map;
  }

  private WritableArray convertJsonToArray(JSONArray jsonArray) throws JSONException {
    WritableArray array = new WritableNativeArray();

    for (int i = 0; i < jsonArray.length(); i++) {
      Object value = jsonArray.get(i);
      if (value instanceof JSONObject) {
        array.pushMap(convertJsonToMap((JSONObject) value));
      } else if (value instanceof JSONArray) {
        array.pushArray(convertJsonToArray((JSONArray) value));
      } else if (value instanceof Boolean) {
        array.pushBoolean((Boolean) value);
      } else if (value instanceof Integer) {
        array.pushInt((Integer) value);
      } else if (value instanceof Double) {
        array.pushDouble((Double) value);
      } else if (value instanceof String)  {
        array.pushString((String) value);
      } else {
        array.pushString(value.toString());
      }
    }
    return array;
  }

  private JSONObject convertMapToJson(ReadableMap readableMap) throws JSONException {
    JSONObject object = new JSONObject();
    ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
    while (iterator.hasNextKey()) {
      String key = iterator.nextKey();
      switch (readableMap.getType(key)) {
        case Null:
          object.put(key, JSONObject.NULL);
          break;
        case Boolean:
          object.put(key, readableMap.getBoolean(key));
          break;
        case Number:
          object.put(key, readableMap.getDouble(key));
          break;
        case String:
          object.put(key, readableMap.getString(key));
          break;
        case Map:
          object.put(key, convertMapToJson(readableMap.getMap(key)));
          break;
        case Array:
          object.put(key, convertArrayToJson(readableMap.getArray(key)));
          break;
      }
    }
    return object;
  }

  private JSONArray convertArrayToJson(ReadableArray readableArray) throws JSONException {

    JSONArray array = new JSONArray();
    for (int i = 0; i < readableArray.size(); i++) {
      switch (readableArray.getType(i)) {
        case Null:
          break;
        case Boolean:
          array.put(readableArray.getBoolean(i));
          break;
        case Number:
          array.put(readableArray.getDouble(i));
          break;
        case String:
          array.put(readableArray.getString(i));
          break;
        case Map:
          array.put(convertMapToJson(readableArray.getMap(i)));
          break;
        case Array:
          array.put(convertArrayToJson(readableArray.getArray(i)));
          break;
      }
    }
    return array;
  }


  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put("audioPlayerPlaybackStateUnavailable" , State.UNAVAILABLE.ordinal());
    constants.put("audioPlayerPlaybackStateUninitialized", State.UNINITIALIZED.ordinal());
    constants.put("audioPlayerPlaybackStateWaitingForItem", State.WAITING_FOR_ITEM.ordinal());
    constants.put("audioPlayerPlaybackStateReadyToPlay", State.READY_TO_PLAY.ordinal());
    constants.put("audioPlayerPlaybackStatePlaying", State.PLAYING.ordinal());
    constants.put("audioPlayerPlaybackStatePaused", State.PAUSED.ordinal());
    constants.put("audioPlayerPlaybackStateStalled", State.STALLED.ordinal());
    constants.put("audioPlayerPlaybackStateOfflineOnly", State.UNAVAILABLE.ordinal());
    return constants;
  }

  @Override
  public void onStateChanged(State state) {
    WritableMap params = Arguments.createMap();
    switch (state)
    {
      case PAUSED:                 params.putInt("state",State.PAUSED.ordinal()); break;
      case PLAYING:                params.putInt("state",State.PLAYING.ordinal()); break;
      case STALLED:                params.putInt("state",State.STALLED.ordinal()); break;
      case UNAVAILABLE:            params.putInt("state",State.UNAVAILABLE.ordinal()); break;
      case READY_TO_PLAY:          params.putInt("state",State.READY_TO_PLAY.ordinal()); break;
      case UNINITIALIZED:          params.putInt("state",State.UNINITIALIZED.ordinal()); break;
      case WAITING_FOR_ITEM:       params.putInt("state",State.WAITING_FOR_ITEM.ordinal()); break;
      case AVAILABLE_OFFLINE_ONLY: params.putInt("state",State.AVAILABLE_OFFLINE_ONLY.ordinal()); break;
    }

    sendEvent(reactContext, "state-change", params);

  }

  @Override
  public void onStationChanged(Station station) {

    WritableMap params = Arguments.createMap();
    params.putInt("activeStationId", station.getId());
    sendEvent(reactContext, "station-change", params);

  }

  @Override
  public void onSkipStatusChanged(boolean b) {

  }

  @Override
  public void onProgressUpdate(@NotNull Play play, float v, float v1) {

  }

  @Override
  public void onPlayStarted(Play play) {

      String str  = toJson(play.getAudioFile().getMetadata());

      try {
          JSONObject object = new JSONObject(str);
          WritableMap options  = convertJsonToMap(object);
          WritableMap playParams = Arguments.createMap();
          playParams.putMap("metadata",options);
          playParams.putString("id", play.getAudioFile().getId());
          playParams.putString("title", play.getAudioFile().getTrack().getTitle());
          playParams.putString("artist", play.getAudioFile().getArtist().getName());
          playParams.putString("album", play.getAudioFile().getRelease().getTitle());
          playParams.putString("artist", play.getAudioFile().getArtist().getName());
          playParams.putBoolean("canSkip", mFeedAudioPlayer.canSkip());
          playParams.putInt("duration", (int)play.getAudioFile().getDurationInSeconds());
          WritableMap params = Arguments.createMap();
          params.putMap("play", playParams);
          sendEvent(reactContext, "play-started", params);

      } catch (JSONException e) {
          e.printStackTrace();
      }

  }

  // Skip
  @Override
  public void requestCompleted(boolean b) {
      if(!b) {
          WritableMap params = Arguments.createMap();
          sendEvent(reactContext, "skip-failed", params);
      }

  }
}
