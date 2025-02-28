- v3.4.1
  - update to 6.2.0 of Android package and update exoplayer version to 2.13.1 
  - update react version to 0.64.1
  
- v3.4.0
  - update to 5.1.2 of iOS Pod for AirPlay work
  - update to 6.1.6 of Android package for bugfixes

- v3.3.6
  - add java 1.8 compatibility

- v3.3.5
  - revert back to pods for iOS library

- v3.3.4
  - update min android SDK to 21
  - update gradle
  - upgrade to react native 0.64.2
  - propagate `musicQueued` event to the player

- v3.3.3
  - bump Android SDK to 6.1.3

- v3.3.1
  - update iOS Feed SDK to 4.4.15 to resolve bugs
  - replace `requestClientId()` async call with simple `clientID` property
    that is initialized when player becomes available, and updated
    when the client id changes.
  - `setClientID()` and `createNewClientID()` updated to re-retrieve
    station listings and trigger `session-updated` event.
  - expose `enableiOSAudioSession` method to force creation of
    AVAudioSession on iOS

- v3.2.2
  - fish renamed java variable/accessor causing crash

- v3.2.1
  - update Station.hasNewMusic property after every play
  - update Android library to 6.0.8

- v3.2.0
  - new getMaxSeekableLengthInSeconds and seekCurrentStationBy methods
  - new hasNewMusic property of Stations

- v3.1.7
  - when 'enableBackgroundMusic' is false in iOS, don't attempt to 
    get an AVAudioSession (that is, set disableAudioSession = TRUE)

- v3.1.6
  - update to iOS library v4.4.12

- v3.1.5
  - update to iOS library v4.4.11

- v3.1.4
  - fix improper Java call to disable audio focus

- v3.1.3
  - ensure Exoplayer runs in main thread
  - fix bad disconnect call
  - add 'invalid' handler to gracefully deal with live reload
  - update to iOS library v4.4.10

- v3.1.1
  - Simulcast streaming support

- v3.0.0 - library and RN updates
  - update to Android library v6.0.1, with ExoPlayer 2.10.6
  - update to iOS library v4.4.4

- v2.4.0 - background music
  - offer 'enableBackgroundMusic:boolean' as initialization option
  - update to iOS library v4.3.2
  - update to Android library v5.2.9

- v2.3.5 - update iOS library
  - changes to support iOS 13 and prevent crash

- v2.3.4 - add debugging option
  - new 'debug' option to assist with debugging integration

- v2.3.3 - update React Native dependency
  - update to react-native@^0.59.8

- v2.3.2 - bugfixes
  - update to Android SDK v5.2.8 to handle play -> pause transition before notification is created on Android
  - update to iOS v4.2.8 to get play/pause toggling bugfix

- v2.3.1 - client id export/import
  - new methods: requestClientID(), createNewClientID(), and setClientID()

- v2.2.3 - station change bugfix
  - update to Android library 5.2.4 and fix bad type comparison when changing 
    station

- v2.2.2 - iOS native lib update
  - update to native iOS SDK 4.2.2

- v2.2.1 - docs and timing!
  - fix an android event ordering that didn't match iOS side of things
  - update sample app
  - update README to reference using this project on Android

- v2.2.0 - Android support!
  - now includes our native Android SDK (version 5.2.0), which targets API 28

- v2.1.2 - *critical bugfix*
  - update to FeedMedia iOS SDK 4.2.1 to fix SDK not saving client id
  - fix 'skip failed' event not firing - thanks [@tnaughts](https://github.com/tnaughts)

