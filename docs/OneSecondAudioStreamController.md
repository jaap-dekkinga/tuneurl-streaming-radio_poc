### About OneSecondAudioStreamController

#### QUESTION

```s
Monday, Jul 08, 2024
Jaap Dekkinga 04:47 PM
@teodoro, ihor found that the matching functionality you build is hard-coded at this point. Can you direct him where you coded the actual matching interaction?
```

#### ANSWER

```s
Tuesday, Jul 09, 2024
Teodoro Albon 09:10 AM
Hi @Jaap Dekkinga Good to hear from you. Regarding the matching functionality, I will create a small document how the fingerprint matching is currently implemented for POST /dev/v3/evaluateOneSecondAudioStream API asap.
```

```s
Hi Jaap Dekkinga,

The following information describes in detail how the last code I created for detecting the embedded TuneUrl information on any MP3, WAV, ETC audio clips follows:
```

1. In order to use files mention on this code traces, please download my crc32 source code from here: https://github.com/tmalbonph/crc32 . Compile it to create a crc32 executable.

2. use crc32 as follows:

```bash
crc32 src/main/java/com/tuneurl/webrtc/util/controller/OneSecondAudioStreamController.java
```

3. It will give the CRC32 of such file as follows:

```bash
# 794d4bb0    src/main/java/com/tuneurl/webrtc/util/controller/OneSecondAudioStreamController.java
```

4. Install at least [NODEJS](https://nodejs.org/en/download/package-manager) version v16.20.0; as this is the recommended version when working with WEB Assembly (emscripten).

5. Install js-beautify@1.14.11 as follows:

```bash
npm install js-beautify@1.14.11 -g
```

6. Checkout **tuneurl-streaming-radio_poc** and use **commit hash 85e462ae277a349280f4ec32d1a767c406aa5a83**

  Link: https://github.com/jaap-dekkinga/tuneurl-streaming-radio_poc/tree/85e462ae277a349280f4ec32d1a767c406aa5a83

```bash
git clone https://github.com/jaap-dekkinga/tuneurl-streaming-radio_poc.git

# or

git clone git@github.com:jaap-dekkinga/tuneurl-streaming-radio_poc.git

cd tuneurl-streaming-radio_poc

git checkout 85e462ae277a349280f4ec32d1a767c406aa5a83

# From here onward, you will be using the code that matches my local files for TuneUrl-POC
```

7. Convert audio-demo.js into readable form as follows:

```bash
js-beautify ./src/main/webapp/js/audio-demo.js > ./src/main/webapp/js/pretty-audio-demo.js
```

8. Looking at lines 906 to 982 at `./src/main/webapp/js/pretty-audio-demo.js`, the JavaScript function **loadTuneUrl()** is responsible for calling **POST /dev/v3/evaluateOneSecondAudioStream?offset=OFFSET**

9. Files to checkout (with crc32) as follows:

```bash
3e5c072f    src/main/webapp/js/audio-demo.js
3b0cc818    src/main/webapp/js/pretty-audio-demo.js   ## (CREATED via js-beautify)

794d4bb0    src/main/java/com/tuneurl/webrtc/util/controller/OneSecondAudioStreamController.java
ecb158bb    src/main/java/com/tuneurl/webrtc/util/util/FingerprintUtility.java

```

10. Fingerprint matching algorithm API entry point is **from lines 131 to 367 at src/main/java/com/tuneurl/webrtc/util/controller/OneSecondAudioStreamController.java** on method **OneSecondAudioStreamController.evaluateOneSecondAudioStream()** to implement end-point **POST /dev/v3/evaluateOneSecondAudioStream?offset=OFFSET**

11. The audio trigger is a 1000 milli-second audio-clip mention at **line 45 of src/main/webapp/js/pretty-audio-demo.js**
as **const TRIGGERSOUND_AUDIO_URL = "https://streaming.tuneurl-demo.com/audio/10240-triggersound.wav";**

12. The audio trigger is converted into fingerprint using **function initTriggerAudio()** from **lines 700 to 741 of src/main/webapp/js/pretty-audio-demo.js** and these fingerprint value is saved as **triggerFingerprintData** and **triggerFingerprintSize** on `pretty-audio-demo.js` .

13. In order to identify a valid **TuneUrl** embedded into any audio-clips (mp3, wav, etc), the audio data must have at least 6000 milli-seconds duration. The 1000 milli-seconds for the embedded audio tigger and the adjacent 5000 milli-seconds audio clip is an audio associated with the **TuneUrl**.

14. The goal here is to locate the 1000 milli-seconds audio (the audio trigger), and once located, the adjacent 5000 milli-seconds audio-clip is assume to be the audio that is associated with the **TuneUrl** that was save at the **separate server (see line 814 "https://pnz3vadc52.execute-api.us-east-2.amazonaws.com/dev/search-fingerprint")** and that **TuneUrl** information will be consulted at lines 753 to 863 on `pretty-audio-demo.js` via the following functions as follows:

```JavaScript
function isUniqueId(index)

function locateFingerprintWithAboveMatchPercentage(ary, index, other)

function selectBestMatchApiUrl(results, json)

async function loadTuneUrlFromServer(payload, callback)

async function loadTuneUrlPage(payload, json)

```

15. Looking at the `pretty-audio-demo.js`, the audio-clip to test for **TuneUrl** is saved at `audioFile`. It an AudioFileLoader class that convert audio-clip into array of integers and accessible as **audioFile.getAudioData()** . The `audioFile` can came from a URL (**see const LOAD_FROM_THIS_URL = "http://stream.radiojar.com/vzv0nkgsw7uvv";**) or Audio file (**see const TEST_MP3_FILE = "https://streaming.tuneurl-demo.com/audio/10240-audio-streams-0230000.mp3";**).


16. For every `QUEUEING_NEXT_DURATION` (5000 milli-seconds), a `QUEUEING_LOAD_DURATION` (10000 milli-seconds) audio data is send to **POST /dev/v3/evaluateOneSecondAudioStream?offset=OFFSET** to check if such audio have valid **TuneUrl** information at the given offset.

17. If the given data at the given offset have a valid **TuneUrl** information, the return TAG will be save into `activeAudioTags` structure and the **function initAllTags()** will get the **TuneUrl information** by calling **async function loadTuneUrlPage(payload,...)** and copy the resulting TAG structure to channelTags[0].liveTags[100-milli-seconds-time-offset].

18. Example of a `TAG structure` (see **function selectBestMatchApiUrl(results, json) for details.**) as follows:

```JavaScript
/* const */ TAG = {
  /* integer */ id: 306
  /* string */ name: "itunes Mae Muller",
  /* string */ description: "Mae Muller itunes page",
  /* string */ type: "open_page",
  /* string */ info: "https://music.apple.com/us/album/better-days-single/1585274078",
  /* integer */ matchPercentagestring: 2,
  /* float */ score: 1.0,
  /* float */ similarity: 1.0,
  /* integer */ dataPosition: 0,
  /* integer */ index: 0
}

/* NOTES: The first 6 fields came from calling "https://pnz3vadc52.execute-api.us-east-2.amazonaws.com/dev/search-fingerprint", while score, similarity, dataPosition, index were added by function initAllTags() */
```

19. The **TAG.info** is use for calling the TuneUrl site if the **TAG.type** is either `open_page` or `save_page`. See `function locateFingerprintWithAboveMatchPercentage(ary, index, other)` from lines 753 to 780 and the checking for **TAG.type** is at line 760 on `pretty-audio-demo.js` .

20. The total number of TAG data on the channelTags[0].liveTags[] structure is computed as the **total number of 100 milli-seconds sampling duration** (the value of `playTimerSpeed`) available on the audio data being played at **AudioFileLoader class** named `audioFile`. See `async function initAudio(audioWaveFile)` from lines 676 to 699 how to calculate the number of TAG data saved at `liveTriggerTagCounts`.

21. The **function monitorLiveAudioFeed()** will check every 100 milli-seconds (the value of `playTimerSpeed`) if there is a TAG for that time offset. If found one, it will enable a popup prompting to select Yes or No. The popup will automatically disappear after 7 seconds regardless if the popup Yes or No button is clicked or not.

22. How to calculate the content of the TAG? The end-point **POST /dev/v3/evaluateOneSecondAudioStream?offset=OFFSET** will be used for creating content of the TAG structure.

23. Refer to `OneSecondAudioStreamController.evaluateOneSecondAudioStream(EvaluateAudioStreamEntry, HttpServletRequest, HttpServletResponse)` from lines 131 to 367. After validation on the values of EvaluateAudioStreamEntry structure, the EvaluateAudioStreamEntry.audioData.Data is parsed every 100 milli-seconds interval at line 252.

24. For each 100 milli-seconds audio bits, such bits were split into **5 group of 20 milli-seconds duration** (value of `Constants.FINGERPRINT_INCREMENT_DELTA`) by calling **FingerprintUtility.collectFingerprint() method** at line 254.

25. This **List of FingerprintCompareResponse structure** can detect zero to 5 fingerprint, hence it can have **combination of 32 possible fingerprint** values. The "N P N N N", "N P P P P", and "P P P P N" fingerprint pattern is currently the only pattern that identify a valid fingerprint as coded from lines 269 to 318 on `src/main/java/com/tuneurl/webrtc/util/controller/OneSecondAudioStreamController.java`

26. The **FingerprintUtility.pruneTagsEx() method** on `src/main/java/com/tuneurl/webrtc/util/util/FingerprintUtility.java` is then use for converting these **FingerprintCompareResponse** into **list of TuneUrlTag structure** from lines 320 to 350 on `OneSecondAudioStreamController.java` .

27. To identify other fingerprint pattern, set the global debug flag to true at **Constants.DEBUG_FINGERPRINTING**, run `kill.sh`, run `build.sh clean`, run `run.sh`, and run `tail -n 1000 -f boot.log`

28. The main log at **boot.log** and the debug log for each 100 milli-second entries will be at /home/ubuntu/audio/debug/XXXX/[*.txt, *.log]
