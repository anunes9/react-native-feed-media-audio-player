
# react-native-feed-media-audio-player

This library will expose the iOS and Android Feed.fm SDKs for use in React
Native projects for music playback. 

## Getting started

`$ npm install react-native-feed-media-audio-player --save`

### Mostly automatic installation

`$ react-native link react-native-feed-media-audio-player`

**note** - Android users see item 4 in the Android section below regarding
API levels and Java 1.8 issues.

### Manual installation

#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-feed-media-audio-player` and add `RNFMAudioPlayer.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNFMAudioPlayer.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import fm.feed.adroid.react.RNFMAudioPlayerPackage;` to the imports at the top of the file
  - Add `new RNFMAudioPlayerPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle` before the `include ':app'` line:
        ```
        include ':react-native-feed-media-audio-player'
        project(':react-native-feed-media-audio-player').projectDir = new File(rootProject.projectDir,   '../node_modules/react-native-feed-media-audio-player/android')

        ```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
        ```
      implementation project(':react-native-feed-media-audio-player')
        ```

4. Our library uses version 28.0.0 of the Android support library and Java 1.8, so you may need to
update your build and target versions to be compatible if you're using an older
version of React Native. Change your `android/app/build.gradle`
to the following:

        ```
      buildscript {
        ext {
          buildToolsVersion = "28.0.3"
          minSdkVersion = 16
          compileSdkVersion = 28
          targetSdkVersion = 28
          supportLibVersion = "28.0.0"
        }
        ...
      }
        ```

and, near the top of your `android/build.gradle`, add the following compile options to make sure
the Java 1.8 compiles nicely:

        ```
        android {
          compileSdkVersion rootProject.ext.compileSdkVersion
          
          // support java 1.8:
          compileOptions {
              sourceCompatibility JavaVersion.VERSION_1_8
              targetCompatibility JavaVersion.VERSION_1_8
          }
          ...
        }
        ```

## Sample

Check out [ExampleUI.js](ExampleUI.js) in this package for a minimal native
React component that plays music using this library and displays play/pause/skip/volume
controls along with the current song.

## Usage


At the start of your app, call initialize to create the
singleton player instance and have it contact feed.fm and
wait for a list of available music stations:

```javascript
import audioPlayerService from 'react-native-feed-media-audio-player';

audioPlayerService.initialize({ token: 'demo', secret: 'demo' });
```

The audioPlayerService exposes the singleton player via `audioPlayerService.player`.

The `player` instance has a number of simple playback methods to
control playback: `play()`, `pause()`, `skip()`, `stop()`. 

The player holds a `playbackState` that indicates what it is doing.
That state is one of:

- `UNINITIALIZED`
  the player is still trying to contact feed.fm
- `UNAVAILABLE`
  the player has no connectivity or feed.fm determined the client
  isn't allowed to play music at this time
- `WAITING_FOR_ITEM`
  the player is waiting for the next song to play from feed.fm
- `READY_TO_PLAY`
  the player is idle and ready to play music
- `PLAYING`
  the player is actively playing a song
- `PAUSED`
  the player has paused playback of the current song
- `STALLED`
  the player is waiting for more audio data to arrive over the network

The player holds a `stations` property that is a list of stations that
it can pull music from. If the player is playing a song, details
of the current song are available via the `currentPlay`.

The player's `activeStation` property can be assigned one of the
stations from `stations`.

The player emits events to announce changes in its state. Clients
can subscribe to events via `player.on(event, callback)`, which
returns a function to unsubscribe from the event. The events
(and the objects passed with them to subscribers) are:

- `play-started` (play)
  A new song has started playback
- `state-change` (playbackState)
  The player's state has changed
- `station-change` (station)
  The current station has changed
- `skip-failed` 
  The last skip request has failed

The player is of no use until it successfully contacts feed.fm
and receives a list of stations that the client can play music
from. We say that the player is determining if music is `available`.
To simplify checking whether the player has finished contacting
feed.fm and determined if music is available, the `player.whenAvailable(callback)`
method can be used. That method calls the provided function as soon
as the player knows whether music is available or not:

```
player.whenAvailable((available) => {
  if (!available) {
    // no music is available for this client
    return;

  } else {
    // music is available! listen to events..
    player.on('xxx', () => { });

    // pick a station from player.stations:
    player.activeStation = player.stations[indexOfSomeStation];

    // start playback!
    player.play();

  }
});
```

When the player is not available, there is no music
that the user can listen to (due to either lack of Internet connectivity
or the user is in a location where playback is not licensed). The
player's playback state will be `UNAVAILABLE`.
In that situation, you should not render any music playback
controls, as the player is effectively useless.

Otherwise, when the player is available, it will hold an `activeStation`
and a list of available `stations`, and will respond to playback
methods.

```


