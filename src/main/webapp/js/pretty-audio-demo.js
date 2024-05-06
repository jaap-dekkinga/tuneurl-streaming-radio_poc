/**
 * @license BSD 3-Clause License
 *
 * @copyright Copyright (c) 2023-2024, Jaap Dekkinga, <jaap.dekkinga@gmail.com>
 * @copyright Copyright (c) 2023-2024, Teodoro M. Albon, <albonteddy@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
"use strict";

const AUDIO_FETCH_WAIT_TIME = 5000; // in milli-seconds

const FINGERPRINT_FETCH_TIME = 6; // in seconds

const ANIMATION_DURATION = 33; // in milli-second

// Look for trigger in 100 milli-second distance.
const FINGERPRINT_BIT_DISTANCE = 4410;

const FINGERPRINT_INCREMENT = 256;

const FINGERPRINT_SCORE_THRESHOLD = parseFloat(0.10);

const FINGERPRINT_SIMILARITY_THRESHOLD = parseFloat(0.10);

const TRIGGER_POSITION_COUNTER = 32;

const TRIGGER_DURATION_TIME = 1000; // in milli-second

const DATA_DURATION_TIME_AFTER_TRIGGER = 5000; // in milli-second

const SIX_SECONDS_DATA_BITS = 264600; // 6 x 44100

const PLAY_MODAL_START_TIME = 10;

const LIVE_INIT = 0;

const LIVE_WAIT_MODAL_STATE = 1;

const LIVE_WAIT_FOR_OFFSET = 2;

const LIVE_WAIT_FOR_TRIGGER = 3;

const LIVE_WAIT_NEXT_TRIGGER = 4;

const LIVE_WAIT_FOR_CONTENT = 5;

const LIVE_END = 6;

const APP_TITLE = "Audio Demo Test";

const IF_LOAD_FROM_URL = false;

const IF_SEARCH_API_BROKEN = true;

const IF_LOAD_ALL_AUDIO_STREAM = false;

const QUEUEING_NEXT_DURATION = 5000; // in milliseconds,  5000
const QUEUEING_DURATION = QUEUEING_NEXT_DURATION + 1000; // in milliseconds,  6000
const QUEUEING_LOAD_DURATION = QUEUEING_NEXT_DURATION + 5000; // in milliseconds, 10000
const QUEUEING_TIMER_RUNTIME = 5000;                          // in milliseconds,  6000

const QUEUEING_DURATION_IN_SECONDS = parseInt(QUEUEING_DURATION / 1000); // in seconds,  6
const QUEUEING_LOAD_DURATION_IN_SECONDS = parseInt(QUEUEING_LOAD_DURATION / 1000); // in seconds, 10

var liveState = LIVE_INIT;
var liveCurrentIndex = 0;
var liveCurrentTimer = 0;
var liveTimerAtTrigger = 0;
var liveTriggerTag = null; // liveTriggerTagCounts array of parseFloat()
var liveTriggerTagCounts = 0; // duration * 1000 div playTimerSpeed
var liveTimerAccumulatedTime = 0;
var liveTimerStart = 0;
var playTimer = null;
var playTimerSpeed = 100;
var playFlag = false;

var useSetTimer = false;
var isRadioLocal = false;
var runFromDatabase = true;
var isFirstPopup = false;
var trigger_position_head = -1;
var trigger_position_tail = 0;
var trigger_position_value = null;

var modalPopupElement = null;

// ------------------------- VARS start -----
var channelData = [];
var channelPopup = [];
var channelLink = [];
var channelLoaded = [];
var channelTags = [];
var channelIndex = 0;
var channelTagIndex = 0;

var spinnerGif = null;
var playButtonObject = null;

var audioFile = null;
var triggerAudioFile = null;

var isJWTloaded = false;
var userToken = null

var triggerFingerprintData = [];
var triggerFingerprintSize = 0;

var isPlayAgain = false;
var firstTime = true;
var activeAudioTags = {
    liveTags: [],
    tagCounts: 0,
    tuneUrlCounts: 0
};

var uniquetype = [];

var isPlayButtonClicked = false;
var playButtonState = "hidden";

var tagsTotalCounts = 0;
var nextDataPosition = undefined;

// ------------------------- VARS end -----

function initVariables() {
    isPlayAgain = false;
    firstTime = true;
    triggerFingerprintData = [];
    triggerFingerprintSize = 0;
    isPlayButtonClicked = false;
    playButtonState = "hidden";
    tagsTotalCounts = 0;
    nextDataPosition = undefined;
    spinnerGif = document.getElementById("spinner");
    playButtonObject = document.getElementById("play");
}

function getModalPopupElement() {
    if (modalPopupElement === null) {
        modalPopupElement = jQuery('div#thisModal');
    }
    return modalPopupElement;
}

/**
 * Update the POC title.
 */
function updatePocTitle() {
    const ts = audioFile.getAudioContext().getOutputTimestamp();
    const audioData = audioFile.getAudioData();
    var title = APP_TITLE + ' - ' + ts.contextTime.toFixed(2) + ' / ' + audioData.duration;
    document.getElementById('pocTitle').innerHTML = title;
}

function emitTuneUrlInstruction(isWait, counts) {
    var msgs, dataset;
    dataset = parseInt(10 * audioFile.getAudioData().getDuration());
    if (isWait) {
        msgs = `<center><h5 class="primeColor3text">Please wait while scanning for TuneUrl on a ${dataset} datasets</h5></center><br/><br/>`;
    } else {
        msgs = `<center><h4 class="primeColor3text">${counts} TuneUrl found in a ${dataset} datasets</h4></center>`;
    }
    document.getElementById('channelbtn').innerHTML = msgs;
}

function clearTuneUrlInstruction() {
    document.getElementById('channelbtn').innerHTML = '';
}

/**
 * Access the current player offset in milliseconds.
 * This is normally use to check for validtity of converted data.
 * @returns {integer}
 */
function getCurrentPlayerTimer() {
    if (isPlayButtonClicked) {
        const ts = audioFile.getAudioContext().getOutputTimestamp();
        var contextTime = ts.contextTime.toFixed(4);
        return parseInt(contextTime * 1000);
    }
    return 0;
}

function enablePlayButtonAfterTwoSeconds(ifEnable) {
    if (ifEnable) {
        if (playButtonState !== "enable") {
            playButtonState = "enable";
            showHidePlayButton(true);
            displaySpinner(false);
        }
    } else if (playButtonState !== "enable") {
        if (tagsTotalCounts > 1) {
            playButtonState = "enable";
            showHidePlayButton(true);
            displaySpinner(false);
        }
    }
}

async function getNextDataPosition(offset) {
    let location = offset;
    if (nextDataPosition !== undefined) {
        location = nextDataPosition;
    }
    nextDataPosition = undefined;
    return location;
}

async function getData(response) {
    return await response.json();
}

async function getTextData(response) {
    return await response.text();
}

function hasJWT() {
    return isJWTloaded;
}

function appendMessages(msg) {
    console.log(msg);
}

/**
 * @class AudioData
 * @description Class to hold audio file buffer pointer, size, sample rate and duration.
 *
 * @author <albonteddy@gmail.com>
 * @version 1.0.5
 * @copyright October 2023
 *
 * @license MIT License
 */
class AudioData {
    constructor() {
        this.Url = "";
        this.Data = null;
        this.Size = 0;
        this.sampleRate = 44100;
        this.duration = 0;
        this.fingerprintRate = 10240;
    }

    /**
     * Init AudioData member properties.
     * @param {Array} data 
     * @param {integer} size 
     * @param {integer} rate 
     * @param {integer} duration 
     * @param {integer} newRate
     */
    setAudioData(data, size, rate, duration, newRate) {
        this.Data = data;
        this.Size = size;
        this.sampleRate = rate;
        this.duration = duration;
        this.fingerprintRate = newRate;
    }

    getUrl() {
        return this.Url;
    }
    setUrl(url) {
        this.Url = url;
    }

    /**
     * Get Array.
     * @returns {Array}
     */
    getData() {
        return this.Data;
    }

    /**
     * Get Array size.
     * @returns {integer}
     */
    getSize() {
        return this.Size;
    }

    /**
     * Get Sample rate.
     * @returns {integer}
     */
    getSampleRate() {
        return this.sampleRate;
    }

    /**
     * Get Duration.
     * @returns {integer}
     */
    getDuration() {
        return this.duration;
    }

    /**
     * Get FingerprintRate.
     * @returns {integer}
     */
    getFingerprintRate() {
        return this.fingerprintRate;
    }
}
// ----- class AudioData

class TuneUrlData {
    constructor() {
        this.initData(0, []);
    }
    initData(size, tags) {
        this.size = size;
        this.tags = tags;
    }
    getSize() {
        return this.size;
    }
    getTags(index) {
        if (index < 0 || index > this.size) {
            return null;
        }
        return this.tags[index];
    }
}

// ----- class TuneUrlData

/**
 * @class AudioFileLoader
 * @description Class to hold AudioBuffer for a given audio file.
 *
 * @author <albonteddy@gmail.com>
 * @version 1.0.5
 * @copyright October 2023
 *
 * @see https://developer.mozilla.org/en-US/docs/Web/API/AudioContext
 * @see https://developer.mozilla.org/en-US/docs/Web/API/AudioBuffer
 * @see https://developer.mozilla.org/en-US/docs/Web/API/AudioBufferSourceNode
 *
 * @license MIT License
 */
class AudioFileLoader {
    /**
     * @constructor Constractor with 2 input parameters.
     * @param {*} document 
     * @param {*} window 
     */
    constructor(document, window) {
        this.AMPLITUDE_SIZE = 512;
        this.HALF_AMPLITUDE_SIZE = 256;
        this.MAXIMUM_DURATION = 1020;
        this.MAXIMUM_AUDIO_BITS_TO_CONVERT = 44982e3;
        this.FINGERPRINT_SAMPLE_RATE = 10240;

        this.document = document;
        this.window = window;
        this.audioContext = new AudioContext();
        this.source = {
            buffer: null
        };
        this.arrayBuffer = null;
        this.sourceBuffer = null;
        this.audioData = new AudioData();
        this.playButton = null;
        this.audioElement = null;
        this.track = null;
    }

    /**
     * @function Access the AudioContext instance.
     * @returns {AudioContext}
     */
    getAudioContext() {
        return this.audioContext;
    }

    /**
     * @function Initialized an <audio></audio> Element.
     *
     * @param {HtmlElement} playButtonId 
     * @param {string} audioId 
     * @param {string} pUrl 
     */
    associatePlayButton(playButtonId, audioId, pUrl) {
        // console.log(`playButtonId:${playButtonId}, audioId:${audioId}, pUrl:${pUrl}`);
        this.playButton = this.document.querySelector(playButtonId);
        this.playButton.dataset.playing = "false";

        this.audioElement = this.document.querySelector(audioId);
        try {
            if (this.audioElement) this.audioElement.src = pUrl;
            this.track = this.audioContext.createMediaElementSource(this.audioElement);
            this.track.connect(this.audioContext.destination);
        } catch (e) {
            console.error(e);
        }
        if (this.audioElement) {
            this.audioElement.addEventListener(
                "ended",
                () => {
                    this.playButton.dataset.playing = "false";
                },
                false,
            );
        }
    }

    /**
     * @function Return the audio object associated with the given Audio file.
     * @returns {AudioData}
     */
    getAudioData() {
        return this.audioData;
    }

    /**
     * Check if the current audio is in play mode or not.
     * @returns {boolean}
     */
    isPlaying() {
        if (this.playButton !== null) {
            return this.playButton.dataset.playing === "true";
        }
        return false;
    }

    /**
     * @function Helper method to activate play and pause mode.
     *
     * @param {function} callbackPlayOrPause 
     * @returns {boolean}
     */
    doPlayOrPause(callbackPlayOrPause) {
        if (this.playButton === null || this.audioElement === null) {
            return false;
        }
        if (this.audioContext.state === "suspended") {
            this.audioContext.resume();
            // console.log('playonclick:resuming');
        }
        if (this.playButton.dataset.playing === "false") {
            callbackPlayOrPause(false); // Pause
            this.audioElement.play();
            this.playButton.dataset.playing = "true";
            // console.log('playonclick:playing');
        } else if (this.playButton.dataset.playing === "true") {
            callbackPlayOrPause(true); // Play
            this.audioElement.pause();
            this.playButton.dataset.playing = "false";
            // console.log('playonclick:paused');
        }
        return true;
    }

    /**
     * @function Asynchronously load the given URL of an audio file.
     *
     * @param {string} pUrl 
     */
    async loadFile(pUrl) {
        this.source = this.audioContext.createBufferSource();
        try {
            const response = await fetch(pUrl);
            this.arrayBuffer = await response.arrayBuffer();
            this.source.buffer = await this.audioContext.decodeAudioData(this.arrayBuffer);
            this.sourceBuffer = this.source.buffer;
            this.audioData = this._getAudioBufferChannelZeroData(this.sourceBuffer);
        } catch (e) {
            console.error(
                `Unable to fetch the audio file: ${pUrl} Error: ${e.message}`,
            );
            this.arrayBuffer = null;
            this.source.buffer = null;
            this.sourceBuffer = null;
            this.audioData = new AudioData();
        }
        this.audioData.setUrl(pUrl);
    }

    /**
     * @function Extract the AudioBuffer channel zero data for visualization.
     *
     * @param {AudioBuffer} audioBuffer 
     * @returns {AudioData}
     */
    _getAudioBufferChannelZeroData(audioBuffer) {
        // AudioBuffer.getChannelData()
        if (null === audioBuffer) {
            return new AudioData();
        }
        let float32 = audioBuffer.getChannelData(0);
        let minFloat = 0;
        let maxFloat = 0;
        let value;
        let index;
        let duration = audioBuffer.duration;
        duration = parseInt(0.5 + duration);
        if (duration > this.MAXIMUM_DURATION) duration = this.MAXIMUM_DURATION;
        let sampleRate = parseInt(audioBuffer.sampleRate);
        let max_size = sampleRate * duration;
        let _length = parseInt(float32.length);
        let size = _length > max_size ? max_size : _length;
        size = parseInt(size);
        // scan float value
        for (index = 0; index < size; index++) {
            value = float32[index];
            if (value < minFloat) minFloat = value;
            if (value > maxFloat) maxFloat = value;
        }
        let divider = 0.01 + maxFloat - minFloat;
        console.log(`divider ${divider}, minFloat = ${minFloat} maxFloat = ${maxFloat}`);
        let iMin = 0;
        let iMax = 0;
        let iSum = 0;
        let skip = parseInt(sampleRate / this.FINGERPRINT_SAMPLE_RATE);
        if (skip < 1) skip = 1;
        let limit = parseInt(size / skip);
        let results = new Array(limit);
        let offset;
        for (index = 0, offset = 0;
            (offset < limit) && (index < size); index += skip, offset++) {
            // Normalized to 0-511 values.
            value = float32[index];
            value = value * this.AMPLITUDE_SIZE;
            value = parseInt(value / divider);
            if (value < iMin) iMin = value;
            if (value > iMax) iMax = value;
            results[offset] = value;
            iSum = iSum + value;
        }
        while (offset < limit) results[offset++] = 0;
        console.log(`divider ${divider}, iMin = ${iMin} iMax = ${iMax} iSum = ${iSum}`);
        let audioData = new AudioData();
        let fingerprintRate = parseInt(sampleRate / skip);
        audioData.setAudioData(results, limit, sampleRate, duration, fingerprintRate);
        return audioData;
    }
}
// ----- class AudioFileLoader

// ----- Utilities begin

function getLocalTimeInMillis() {
    return new Date().getTime();
}

function initTimerAccumulatedTimer() {
    liveTimerAccumulatedTime = 0;
    liveTimerStart = getLocalTimeInMillis();
}

function adjustAnimationTimer() {
    var end = getLocalTimeInMillis();
    var diff = end - liveTimerStart;
    liveTimerAccumulatedTime += diff;
    liveTimerStart = end;
}

function clone(obj) {
    if (null == obj || "object" != typeof obj) return obj;
    var copy = obj.constructor();
    for (var attr in obj) {
        if (obj.hasOwnProperty(attr)) copy[attr] = obj[attr];
    }
    return copy;
}

function displaySpinner(isDisplay) {
    if (isDisplay) {
        spinnerGif.style = "display:block";
    } else {
        spinnerGif.style = "display:none";
    }
}

function showHidePlayButton(isDisplay) {
    if (isDisplay) {
        playButtonObject.style = "display:block";
    } else {
        playButtonObject.style = "display:none";
    }
}

function setButtonPlayOrPause(isPlay) {
    document.getElementById('playorpause').innerText = isPlay ? 'Play' : 'Pause';
}

function setButtonPlayAgain() {
    isPlayAgain = true;
    document.getElementById('playorpause').innerText = 'Play Again'
    playButtonObject.style = "display:block";
}

function setOrClearDummyTimerCallback() {
    console.error('NULL -> setOrClearDummyTimerCallback');
}

// Repeatable Play timer
function setOrClearPlayTimer(isClear, callback) {
    let myTimeout = playTimer;
    playTimer = null;
    if (myTimeout !== null) clearInterval(myTimeout);
    if (!isClear) {
        if (callback) {
            playTimer = setInterval(callback, playTimerSpeed);
        } else {
            playTimer = setInterval(setOrClearDummyTimerCallback, playTimerSpeed);
        }
    }
}

function initChannelData(activeChannel) {
    var channel = {
        channelId: 0,
        Url: "",
        offset: 0,
        duration: 0,
        category: "save_page",
        title: "Local page",
        popup: "hExPGEgaHJlZj0iaHR0cHM6Ly9zdHJlYW1yYWRpby50bWFsYm9uLmNvbS8iPkxvY2FsIHBhZ2U8L2E+CjxoMj5Mb2NhbCBwYWdlPC9oMj4KPHA+TG9jYWwgcGFnZTwvcD4K"
    };
    var tags = {
        liveTags: [],
        tagCounts: 0
    };
    channelData = [];
    channelPopup = [];
    channelLink = [];
    channelLoaded = [];
    channelTags = [];
    channelIndex = 0;
    for (var index = 0; index < 13; index++) {
        channel.channelId = index;
        channel.title = "Local page #" + index;
        channelData.push(clone(channel));
        var popx = "" + channelData[index].popup;
        var txt = jQuery.base64.decode(popx.slice(3));
        channelPopup.push(clone(txt));
        channelLink.push(null);
        channelLoaded.push(null);
        channelTags.push(clone(tags));
    }
    channelIndex = activeChannel;
}

// ----- Utilities end

// ----- Monitor Live feed begin

function getLiveTagsData(pTagIndex) {
    if (pTagIndex > 0 && pTagIndex < 12) {
        var ct = channelTags[pTagIndex];
        return ct;
    }
    return {
        liveTags: [],
        tagCounts: 0
    };
}

var timerForPopupToHideModal = null;

function procToTerminatePopupModal() {
    if (timerForPopupToHideModal !== null) {
        clearInterval(timerForPopupToHideModal);
        timerForPopupToHideModal = null;
    }
    getModalPopupElement().modal('hide');
    // activate the monitor again.
    liveTimerStart = getLocalTimeInMillis();
    liveState = LIVE_WAIT_NEXT_TRIGGER;
    setOrClearPlayTimer(false, monitorLiveAudioFeed);
}

async function activateChannelModal(btnumber) {
    setOrClearPlayTimer(true, null);
    adjustAnimationTimer();

    // Activate the Modal
    getModalPopupElement().modal('show');
    timerForPopupToHideModal = setTimeout(procToTerminatePopupModal, 7000);
    return true;
}

function executeChannelModal(iRef) {
    var link, tags, tag;
    var index = channelTagIndex;
    procToTerminatePopupModal();
    if (parseInt(iRef) > 0) {
        tags = channelTags[channelIndex];
        if (index >= 0 && index < tags.tagCounts) {
            tag = tags.liveTags[index];
            link = tag.info || "";
            if (link !== null && link.length > 10) {
                window.open(link, '_blank').focus();
            } else {
                link = channelLoaded[channelIndex];
                channelLoaded[channelIndex] = null;
                if (link !== null && link.length > 10) {
                    window.open(link, '_blank').focus();
                }
            }
        }
    }
}

var modalStateTimer = null;

function startModalState() {
    clearTimeout(modalStateTimer);
    modalStateTimer = null;
    // Display the popup
    activateChannelModal(channelIndex);
}

/**
 * N.B. this method should complete sub-process in less than 100 milli-seconds.
 *
 * @returns boolean true if completed, otherwise false.
 */
function monitorLiveAudioFeed() {
    var tags = channelTags[channelIndex];
    if (audioFile === null || (liveState !== LIVE_WAIT_NEXT_TRIGGER) || tags.tagCounts < 1) {
        setOrClearPlayTimer(true, null);
        return false;
    }
    // Temporary mark as live, to avoid re-entrancy problem.
    liveState = LIVE_INIT;
    channelLoaded[channelIndex] = null;

    // Show statistic
    updatePocTitle();

    var audioData = audioFile.getAudioData();
    var maxDuration = parseInt(1000 * (audioData.getDuration() - 6));
    const ts = audioFile.getAudioContext().getOutputTimestamp();
    var tsContextTime = parseInt(1000 * ts.contextTime);

    var currentTime = getLocalTimeInMillis();
    var contextTime = liveTimerAccumulatedTime + currentTime - liveTimerStart;
    var index = parseInt(contextTime / playTimerSpeed);
    var next = parseInt(tsContextTime / playTimerSpeed);
    if (next > index) index = next;

    // If audio player is at the end of the clip, terminate it
    if (tsContextTime >= maxDuration || (contextTime >= maxDuration) || index >= tags.tagCounts) {
        console.log('Live ended');
        liveState = LIVE_END;
        return destroyCurrentAudioClassPlayer(false);
    }

    // Do we have a TuneUrl at this time slot?
    channelTagIndex = index;
    var tag = tags.liveTags[index];

    // See URL from ?.info at locateFingerprintWithAboveMatchPercentage()
    var info = "" + (tag === null ? "" : (tag.info | ""));
    if (!tag || info.length < 1) {
        liveState = LIVE_WAIT_NEXT_TRIGGER;
        return false;
    }
    console.log(`Enable popup: ${index}, pos: ${tag.dataPosition}, time: ${tsContextTime}`);

    // Clear animation timer, start modal popup
    liveTimerStart = getLocalTimeInMillis();
    setOrClearPlayTimer(true, null);

    // Click on YES during the popup instance will open this URL.
    channelLoaded[channelIndex] = clone(info);

    // Display the TuneUrl popup
    liveState = LIVE_WAIT_MODAL_STATE;
    modalStateTimer = setTimeout(startModalState, PLAY_MODAL_START_TIME);
    return true;
}

/**
 * @function Stop monitoring live event and hide the play button.
 *
 * @param {boolean} createNewOne - Currently not used.
 */
function destroyCurrentAudioClassPlayer(createNewOne) {
    // 0. hide spinner
    displaySpinner(false);
    // 1. clear time
    setOrClearPlayTimer(true, null);
    // 2. Pause if Playing
    if (audioFile.isPlaying()) {
        audioFile.doPlayOrPause(setButtonPlayOrPause);
    }
    // 3. Hide the play button
    showHidePlayButton(false);
    // 4. Set the marker
    liveState = LIVE_END;
    // 5. Enable the "Play Again" button.
    // setButtonPlayAgain();
}

// ----- Monitor Live feed end

/**
 * @function Load the audio file to play.
 *
 * @param {string} pUrl - URL to audio file as live audio stream.
 * @returns boolean
 */
async function initAudioContent(pUrl) {
    if (audioFile !== null) return true;
    audioFile = new AudioFileLoader(document, window);
    audioFile.associatePlayButton("button#play", "audio#audioId", pUrl);
    try {
        await audioFile.loadFile(pUrl);
    } catch (e) {
        console.error(e);
    }
    let audioData = audioFile.getAudioData();
    console.log(JSON.stringify({
        Data: audioData.Data !== null,
        Size: audioData.Size,
        sampleRate: audioData.sampleRate,
        duration: audioData.duration,
        fingerprintRate: audioData.fingerprintRate
    }));
    return true;
}

/**
 * @function Load the triggersound file for TuneUrl fingerprint detection.
 *
 * @param {string} pUrl - URL to audio file as triggersound.
 * @returns boolean
 */
async function initTriggerAudioContent(pUrl) {
    if (triggerAudioFile !== null) return true;
    triggerAudioFile = new AudioFileLoader(document, window);
    try {
        await triggerAudioFile.loadFile(pUrl);
    } catch (e) {
        console.error(e);
    }
    let audioData = triggerAudioFile.getAudioData();
    console.log(JSON.stringify({
        Data: audioData.Data !== null,
        Size: audioData.Size,
        sampleRate: audioData.sampleRate,
        duration: audioData.duration,
        fingerprintRate: audioData.fingerprintRate
    }));
    return true;
}

/**
 * @function Method to handle click on play button.
 * @returns boolean
 */
function playonclick() {
    // Mark that the end-user press the Play button.
    // N.B. access to MDN functions related to audio is only available
    // after the end-user click on the HtmlElement associated with the <audio></audio> element.
    if (isPlayAgain) {
        console.clear();
        window.location.href = "https://streamradiolocal.tmalbon.com/";
        return true;
    }
    if (!isPlayButtonClicked) clearTuneUrlInstruction();
    isPlayButtonClicked = true;
    let isPlaying = audioFile.isPlaying();
    if (isPlaying) {
        adjustAnimationTimer();

        // Clear Play mode, set Pause mode.
        setOrClearPlayTimer(true, null);
    } else {
        // Set Play mode, clear Pause mode.
        if (firstTime) {
            firstTime = false;
            initTimerAccumulatedTimer();
        } else {
            liveTimerStart = getLocalTimeInMillis();
        }
        liveState = LIVE_WAIT_NEXT_TRIGGER;
        setOrClearPlayTimer(false, monitorLiveAudioFeed);
    }
    audioFile.doPlayOrPause(setButtonPlayOrPause);
    return true;
}

/**
 * @function Helper method for displaying method performace.
 *
 * @param {string} title 
 * @param {integer} atStart 
 * @param {integer} atEnd 
 */
function showMethodRunTime(title, atStart, atEnd) {
    var diff = atEnd - atStart;
    console.log(`Runtime: ${title}, s:${atStart}, e:${atEnd}, d:${diff}`);
}

/**
 * @function Method for initiate the loading of audio file to play.
 *
 * @param {string} audioWaveFile
 */
async function initAudio(audioWaveFile) {
    console.log('initAudio');
    var start = getLocalTimeInMillis();
    await initAudioContent(audioWaveFile);
    let audioData = audioFile.getAudioData();
    let maxDuration = parseInt(1000 * audioData.duration);
    let counts = parseInt(maxDuration / playTimerSpeed);
    let tags = new Array(counts);
    for (var index = 0; index < counts; index++) tags[index] = null;
    channelTags[channelIndex] = new Object({
        liveTags: tags,
        tagCounts: counts
    });
    liveTriggerTag = tags;
    liveTriggerTagCounts = counts;
    console.log(JSON.stringify({
        liveTags: tags !== null,
        tagCounts: counts,
        interval: playTimerSpeed
    }));
    liveState = LIVE_WAIT_NEXT_TRIGGER;
    var end = getLocalTimeInMillis();
    showMethodRunTime('initAudio', start, end);
}

/**
 * @function Method for initiate the loading of audio file for TuneUrl fingerprint detection.
 *
 * @param {string} triggerWaveFile
 */
async function initTriggerAudio(triggerWaveFile) {
    console.log('initTriggerAudio');
    var start = getLocalTimeInMillis();
    triggerFingerprintData = [];
    triggerFingerprintSize = 0;
    await initTriggerAudioContent(triggerWaveFile);
    var end = getLocalTimeInMillis();
    showMethodRunTime('initTriggerAudio', start, end);
    start = end;
    let audioData = triggerAudioFile.getAudioData();
    let sData = JSON.stringify(audioData);
    appendMessages("Calling calculateFingerprint API");
    const res = await fetch("https://streamradiolocal.tmalbon.com/dev/v3/calculateFingerprint", {
            method: "POST",
            mode: "cors",
            headers: {
                "Content-type": "application/json; charset=UTF-8",
                "Accept": "application/json",
                "Access-Control-Allow-Origin": "*",
                "Authorization": "Bearer " + userToken,
            },
            body: sData,
        })
        .then(response => getTextData(response))
        .then(text => {
            let data;
            try {
                data = JSON.parse(text);
            } catch (e) {
                throw Error(e.message);
            }
            if (data.message) throw Error(data.message);
            try {
                triggerFingerprintData = JSON.parse(data.dataEx);
            } catch (e) {
                throw Error(e.message);
            }
            triggerFingerprintSize = data.size;
            console.log(JSON.stringify({
                size: triggerFingerprintSize,
                data: triggerFingerprintData
            }));
        })
        .catch(error => {
            triggerFingerprintData = [];
            triggerFingerprintSize = 0;
            console.error("ERROR:", error);
            appendMessages("initTriggerAudio API on ERROR: " + error);
        });

}

function isUniqueId(index) {
    let j;
    for (j = 0; j < uniquetype.length; j++) {
        if (uniquetype[j] === index) {
            return false;
        }
    }
    return true;
}

function locateFingerprintWithAboveMatchPercentage(ary, index, other) {
    // {
    //   "id": 301,
    //   "name": "Mae Muller iTunes",
    //   "description": "",
    //   "type": "open_page",
    //   "info": "https://music.apple.com/us/album/better-days-single/1585274078",
    //   "matchPercentage": 2
    // }
    var j, k = -1;
    var rate = parseInt(0, 10);
    var data, matchPercentage;
    for (j = 0; j < ary.length; j++) {
        if (j !== index && j !== other) {
            data = ary[j];
            // Only select category 'open_page' or 'save_page'
            if ((data.info !== null && data.info.length > 0) && (data.type === 'open_page' || data.type === 'save_page')) {
                matchPercentage = parseInt(data.matchPercentage, 10);
                if (matchPercentage > rate) {
                    k = j;
                    rate = matchPercentage;
                }
            }
        }
    }
    if (k < 0) {
        for (j = 0; j < ary.length; j++) {
            if (j !== index && j !== other) {
                if (isUniqueId(j)) {
                    k = j;
                    break;
                }
            }
        }
    }
    return [k, ary[k]];
}

function selectBestMatchApiUrl(results, json) {
    let ary = undefined;
    if (typeof results === 'object' && results.length > 0) {
        ary = results[0];
    }
    if (ary === undefined ||
        ary.id === undefined ||
        ary.type === undefined ||
        ary.info === undefined ||
        ary.matchPercentage === undefined
    ) {
        return null;
    }
    uniquetype = [];
    let i_one;
    let alias = locateFingerprintWithAboveMatchPercentage(results, -1, -1);
    i_one = alias[0];
    uniquetype.push(i_one);
    ary = alias[1];
    return new Object({
        id: ary.id,
        name: ary.name,
        description: ary.description,
        type: ary.type,
        info: ary.info,
        matchPercentage: parseInt(ary.matchPercentage),
        score: json.score,
        similarity: json.similarity
    });
}

async function loadTuneUrlFromServer(payload, callback) {
    let data, url;
    if (IF_SEARCH_API_BROKEN) {
        console.error('Search API is broken');
        return callback('Search API is broken', null);
    }
    displaySpinner(true);
    const res = await fetch("https://pnz3vadc52.execute-api.us-east-2.amazonaws.com/dev/search-fingerprint", {
            method: "POST",
            mode: "no-cors",
            headers: {
                "Content-type": "application/json; charset=UTF-8",
                "Accept": "application/json",
                "Access-Control-Allow-Origin": "*",
            },
            body: payload
        })
        .then(response => getTextData(response))
        .then(textData => {
            try {
                data = JSON.parse(textData);
            } catch (e) {
                data = {
                    message: 'No Matching fingerprint found'
                };
            }
            if (data.message) throw Error(data.message);
            url = selectBestMatchApiUrl(data, {
                score: 1,
                similarity: 1
            });
            if (url === null) return callback("Empty", null);
            return callback(null, url);
        })
        .catch(error => {
            console.error(error);
            return callback(error, null);
        });
    return res;
}

var simulatedSearchMatchApiResult = null;

async function loadTuneUrlPage(payload, json) {
    const url = await loadTuneUrlFromServer(payload, async function(onError, datus) {
        if (onError !== null) {
            if (simulatedSearchMatchApiResult !== null) {
                return simulatedSearchMatchApiResult;
            }
            const res = await fetch("https://streamradiolocal.tmalbon.com/json/pretty-fingerprint-results-fingerprint1.json", {
                    method: "GET",
                    headers: {
                        "Content-type": "application/json; charset=UTF-8",
                        "Accept": "application/json",
                    },
                })
                .then(response => getData(response))
                .then(responseData => {
                    simulatedSearchMatchApiResult = selectBestMatchApiUrl(responseData, json);
                    return simulatedSearchMatchApiResult;
                })
                .catch(error => {
                    console.error("ERROR:", error);
                    return null;
                });
            return res;
        } else {
            return datus;
        }
    });
    return url;
}

async function initAllTags(liveTags, urlCounts, timeOffset) {
    let index;
    let url;
    channelIndex = 0;
    let tags = channelTags[0].liveTags;
    let count = 0;
    for (index = 0; index < urlCounts; index++) {
        let tag = liveTags[index];
        let dataPosition = tag.dataPosition;
        let offset = tag.index;
        let payload = "" + tag.description;

        console.log(JSON.stringify({
            offset: timeOffset,
            index: offset,
            dataOffset: dataPosition
        }));

        url = await loadTuneUrlPage(payload, {
            score: tag.score,
            similarity: tag.similarity
        });
        if (url !== null) {
            nextDataPosition = dataPosition + QUEUEING_NEXT_DURATION;
            url.dataPosition = dataPosition;
            url.index = offset;
            tags[offset] = clone(url);
            count += 1;
            console.log(JSON.stringify({
                next: nextDataPosition,
                tags: tags[offset]
            }));
        }
    }
    tagsTotalCounts += urlCounts;
    console.log(JSON.stringify({
        counts: count,
        total: tagsTotalCounts,
        next: nextDataPosition
    }));
    if (tagsTotalCounts > 1) enablePlayButtonAfterTwoSeconds(true);
}

/**
 * @function calculateDataOffset
 * @param {integer} timeOffset - in milliseconds.
 * @param {integer} sampleRate - Ex. 11025
 * @returns {integer}
 */
function calculateDataOffset(timeOffset, sampleRate) {
    var index = parseInt(timeOffset * sampleRate);
    return parseInt(index / 1000);
}

async function loadTuneUrl(timeOffset) {
    let audioData = audioFile.getAudioData();
    if (audioData.Data === null) {
        console.error('audioData.Data === null');
        return false;
    }
    var fingerprintRate = audioData.fingerprintRate;
    var iStart = calculateDataOffset(timeOffset, fingerprintRate);
    var iEnd = calculateDataOffset(timeOffset + QUEUEING_LOAD_DURATION, fingerprintRate);
    if (iEnd > audioData.getSize()) {
        return false;
    }
    var index, offset, limit = iEnd - iStart;
    console.log(JSON.stringify({
        start: iStart,
        end: iEnd,
        size: limit
    }));
    var data = new Array(limit);
    for (index = 0, offset = iStart;
        (index < limit) && (offset < iEnd); index++, offset++) {
        data[index] = audioData.Data[offset];
    }
    var audioDataEx = {
        Url: audioData.Url,
        Data: data,
        Size: limit,
        sampleRate: audioData.sampleRate,
        duration: QUEUEING_LOAD_DURATION_IN_SECONDS,
        fingerprintRate: fingerprintRate
    };
    var msgs, oras, end, start = getLocalTimeInMillis();
    var datus = {
        audioData: audioDataEx,
        dataFingerprint: triggerFingerprintData,
        sizeFingerprint: triggerFingerprintSize
    };
    let sData = JSON.stringify(datus);
    appendMessages(`Calling evaluateOneSecondAudioStream API for offset: ${timeOffset}`);
    displaySpinner(true);
    const res = await fetch(`https://streamradiolocal.tmalbon.com/dev/v3/evaluateOneSecondAudioStream?offset=${timeOffset}`, {
            method: "POST",
            mode: "cors",
            headers: {
                "Content-type": "application/json; charset=UTF-8",
                "Accept": "application/json",
                "Access-Control-Allow-Origin": "*",
                "Authorization": "Bearer " + userToken,
            },
            body: sData,
        })
        .then(response => getTextData(response))
        .then(text => {
            let data, ignore = false;
            end = getLocalTimeInMillis();
            oras = getCurrentPlayerTimer();
            msgs = `Data for offset ${timeOffset}`;
            if (oras > 0 && oras > timeOffset) {
                nextDataPosition = oras + QUEUEING_LOAD_DURATION;
                msgs = msgs + ' was late';
                ignore = true;
            }
            showMethodRunTime(msgs, start, end);
            if (!ignore) {
                try {
                    data = JSON.parse(text);
                } catch (e) {
                    throw Error(e.message);
                }
                console.log(data);
                activeAudioTags.liveTags = clone(data.liveTags);
                activeAudioTags.tuneUrlCounts = data.tuneUrlCounts;
                activeAudioTags.tagCounts = data.tagCounts;

                initAllTags(activeAudioTags.liveTags, activeAudioTags.tuneUrlCounts, timeOffset);
            }
        })
        .catch(error => {
            end = getLocalTimeInMillis();
            showMethodRunTime(`Error for Data for offset ${timeOffset}`, start, end);
            console.error("ERROR:", error);
            appendMessages("evaluateAudioStream API on ERROR: " + error);
        });
}

// Process A
var convertFileTimer = null;

function convertFile() {
    if (null === convertFileTimer) return false;
    var targetOffset = nextDataPosition;
    var playerOffset = getCurrentPlayerTimer();
    if (playerOffset > 0) {
        if (targetOffset < playerOffset) {
            targetOffset = playerOffset + QUEUEING_LOAD_DURATION;
            nextDataPosition = targetOffset;
        }
    }
    var maxDuration = parseInt(1000 * audioFile.getAudioData().getDuration()) - QUEUEING_DURATION;
    if (targetOffset < maxDuration) {
        nextDataPosition += QUEUEING_LOAD_DURATION;
        loadTuneUrl(targetOffset);
    } else {
        clearInterval(convertFileTimer);
        convertFileTimer = null;
        enablePlayButtonAfterTwoSeconds(true);
        displaySpinner(false);
    }
    return true;
}

/**
 * @function Method for sending bits of audio to play and calculate where the TuneUrl were located.
 * @returns {TuneUrlData}
 */
async function saveAudio() {
    var limit, audioData;
    // For testing pre-load every 6000 milliseconds audio segment
    audioData = audioFile.getAudioData();
    if (audioData.Data !== null && userToken !== null) {
        limit = audioData.getSize() - QUEUEING_LOAD_DURATION;
        if (limit >= QUEUEING_LOAD_DURATION) {
            // pre-load 10 seconds before activating the play button
            await loadTuneUrl(0);
            await loadTuneUrl(QUEUEING_NEXT_DURATION);
            enablePlayButtonAfterTwoSeconds(true);
            nextDataPosition = 2 * QUEUEING_NEXT_DURATION;
            convertFileTimer = setInterval(convertFile, QUEUEING_TIMER_RUNTIME); // Run every 6 seconds
            convertFile();
        }
    }
}

async function initAllTagsEx(liveTags, urlCounts) {
    let index;
    let url;
    channelIndex = 0;
    let tags = channelTags[0].liveTags;
    let count = 0;
    for (index = 0; index < urlCounts; index++) {
        let tag = liveTags[index];
        let dataPosition = tag.dataPosition;
        let offset = tag.index;
        let payload = "" + tag.description;
        url = await loadTuneUrlPage(payload, {
            score: tag.score,
            similarity: tag.similarity
        });
        if (url !== null) {
            url.dataPosition = dataPosition;
            url.index = offset;
            tags[offset] = clone(url);
            count += 1;
            console.log(tags[offset]);
        }
    }
    console.log({
        counts: count,
        total: urlCounts
    });
    emitTuneUrlInstruction(false, urlCounts);
    displaySpinner(false);
    showHidePlayButton(true);
}

/**
 * @function Method for sending bits of audio to play and calculate where the TuneUrl were located.
 * @returns {TuneUrlData}
 */
async function saveAudioEx(callback) {
    let audioData = audioFile.getAudioData();
    if (audioData.Data === null) {
        console.error('audioData.Data === null');
        return false;
    }
    var end, start = getLocalTimeInMillis();
    var datus = {
        audioData: audioData,
        dataFingerprint: triggerFingerprintData,
        sizeFingerprint: triggerFingerprintSize
    };
    let sData = JSON.stringify(datus);
    appendMessages("Calling evaluateAudioStream API");
    displaySpinner(true);
    const res = await fetch("https://streamradiolocal.tmalbon.com/dev/v3/evaluateAudioStream", {
            method: "POST",
            mode: "cors",
            headers: {
                "Content-type": "application/json; charset=UTF-8",
                "Accept": "application/json",
                "Access-Control-Allow-Origin": "*",
                "Authorization": "Bearer " + userToken,
            },
            body: sData,
        })
        .then(response => getTextData(response))
        .then(text => {
            let data;
            end = getLocalTimeInMillis();
            showMethodRunTime('saveAudioEx:Okay', start, end);
            try {
                data = JSON.parse(text);
            } catch (e) {
                throw Error(e.message);
            }
            console.log(data);
            activeAudioTags.liveTags = clone(data.liveTags);
            activeAudioTags.tuneUrlCounts = data.tuneUrlCounts;
            activeAudioTags.tagCounts = data.tagCounts;

            callback(activeAudioTags.liveTags, activeAudioTags.tuneUrlCounts);
        })
        .catch(error => {
            end = getLocalTimeInMillis();
            showMethodRunTime('saveAudioEx:Error', start, end);
            console.error("ERROR:", error);
            appendMessages("evaluateAudioStream API on ERROR: " + error);
        });
}

async function doLogin() {
    var element = document.getElementById('usertoken');
    var data = element.value || '${token}';
    isJWTloaded = false;
    if (data !== '${token}') {
        userToken = data;
        isJWTloaded = true;
        console.log(JSON.stringify({
            userId: 17001002,
            JWT: userToken
        }));
    }
}

async function initLoadFromUrl(url) {
    await initAudio('https://streamradiolocal.tmalbon.com/audio/10240-audio-streams-0230000.mp3');
}

async function startCanvas() {
    console.log('startCanvas');
    initVariables();
    displaySpinner(true);
    initChannelData(0);
    setButtonPlayOrPause(true);
    await doLogin();
    if (isJWTloaded) {
        await initTriggerAudio('https://streamradiolocal.tmalbon.com/audio/10240-triggersound.wav');
        if (triggerFingerprintSize > 0) {
            await initLoadFromUrl('http://stream.radiojar.com/vzv0nkgsw7uvv');
            if (IF_LOAD_ALL_AUDIO_STREAM) {
                emitTuneUrlInstruction(true, 0);
                await saveAudioEx(initAllTagsEx);
            } else {
                saveAudio();
            }
        }
    }
    displaySpinner(false);
}