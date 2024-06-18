/**
 * @license BSD 3-Clause License
 *
 * @copyright Copyright (c) 2023-2024, Jaap Dekkinga, <jaap.dekkinga@gmail.com>
 * @copyright Copyright (c) 2023-2024, Jaap Dekkinga, <jaap.dekkinga@gmail.com>
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
// const base_host = "https://streaming.tuneurl-demo.com";
const base_host = "http://localhost:8281";
const LOAD_FROM_THIS_URL = "http://stream.radiojar.com/vzv0nkgsw7uvv";
const TEST_MP3_FILE = base_host + "/audio/10240-audio-streams-0230000.mp3";
const TRIGGERSOUND_AUDIO_URL = base_host + "/audio/10240-triggersound.wav";

const IF_LOAD_FROM_URL = false;
const APP_TITLE = "Audio Demo Test";

const AMPLITUDE_SIZE = 512;
const HALF_AMPLITUDE_SIZE = 256;
const MAXIMUM_DURATION = 1020;
const FINGERPRINT_SAMPLE_RATE = 10249;

const LIVE_INIT = 0;
const LIVE_WAIT_MODAL_STATE = 1;
const LIVE_WAIT_FOR_OFFSET = 2;
const LIVE_WAIT_FOR_TRIGGER = 3;
const LIVE_WAIT_NEXT_TRIGGER = 4;
const LIVE_WAIT_FOR_CONTENT = 5;
const LIVE_END = 6;


var isJWTloaded = false;
var userToken = null;

var triggerFingerprintData = [];
var triggerFingerprintSize = 0;
var triggerFingerprintZipped = {};

var spinnerGif = null;
var playButtonObject = null;

let audioStreamPlayer = null;

class AudioDataEntry {
    constructor() {
        this.Url = "";
        this.Data = null;
        this.Size = 0;
        this.sampleRate = 44100;
        this.duration = 0;
        this.fingerprintRate = 10240
    }
    setAudioData(data, size, rate, duration, newRate) {
        this.Data = data;
        this.Size = size;
        this.sampleRate = rate;
        this.duration = duration;
        this.fingerprintRate = newRate
    }
    getUrl() {
        return this.Url
    }
    setUrl(url) {
        this.Url = url
    }
    getData() {
        return this.Data
    }
    getSize() {
        return this.Size
    }
    getSampleRate() {
        return this.sampleRate
    }
    getDuration() {
        return this.duration
    }
    getFingerprintRate() {
        return this.fingerprintRate
    }
}

class AudioStreamPlayer {
    constructor(url, callback_stream) {
        this.url = url;
        this.audioContext = new (window.AudioContext || window.webkitAudioContext)();
        this.audioQueue = [];
        this.source = null;
        this.pause_buff = null;
        this.startTime = 0;
        this.pausedAt = 0;
        this.isInit = false;
        this.isFirstPlay = true;
        this.isPlaying = false;
        this.isPaused = false;
        this.callback_stream = callback_stream;
    }

    async __fetchAudioStream() {
        const response = await fetch(this.url);
        const reader = response.body.getReader();
    
        let bytesRead = 0;
        const sampleRate = 11025; // Assuming the sample rate is 44100 Hz
       
        // Calculate the number of bytes to read for the desired duration
        const bytesToRead = sampleRate * 1.5 * 2; // 4 bytes per sample (32-bit float, stereo)
    
        let audioBuffer = new Uint8Array(bytesToRead);
        let overBytes = new Uint8Array(0);
    
        while (true) {
            const { done, value } = await reader.read();
            if (done) break;
    
            let readBytes = new Uint8Array(value.byteLength);
            readBytes.set(value);
            
            while(true) {
                let bytesToCopy = Math.min(bytesToRead - bytesRead, readBytes.byteLength);
                audioBuffer.set(readBytes.slice(0, bytesToCopy), bytesRead);
                bytesRead += bytesToCopy;
        
                if (bytesRead >= bytesToRead) {
        
                    // Handle the overflow bytes
                    const overBytesLength = readBytes.byteLength - bytesToCopy;
                    if (overBytesLength > 0) {
                        overBytes = new Uint8Array(overBytesLength);
                        overBytes.set(readBytes.slice(bytesToCopy));
                    }
         
                    // Decode audio data
                    const audioData = await this.audioContext.decodeAudioData(audioBuffer.buffer.slice(0, bytesToRead));
                    console.log('fetchAudioStream: audioData', audioData);

                    // call hook_func
                    if (this.callback_stream)
                        this.callback_stream(audioData);
                    
                    // Add new audio Segment.
                    const newSegment = await this.__createNewAudioSegment(audioData);
                    if (newSegment) {
                        this.audioQueue.push(newSegment);
                        this.play(false);
                    }
    
                    bytesRead = 0;
                    audioBuffer = new Uint8Array(bytesToRead); // Reallocate audioBuffer
    
                    // Prepare for the next chunk
                    if (overBytesLength === 0)
                        break;
    
                    readBytes = new Uint8Array(overBytesLength); // Reallocate readBytes
                    readBytes.set(overBytes);
                }
                else {
                    break;
                }  
            } 
        } 
    }

    async __createNewAudioSegment(audioData) {
        if (audioData.length <= 0) {
            return null;
        }
    
        const newSegment = this.audioContext.createBuffer(
            audioData.numberOfChannels,
            audioData.length,
            audioData.sampleRate
        );
    
        for (let channel = 0; channel < audioData.numberOfChannels; channel++) {
            const newData = audioData.getChannelData(channel);
            newSegment.copyToChannel(newData, channel);
        }
        console.log('AudidStreamPlayer::__createNewAudioSegment', audioData.length);
        return newSegment;
    }

    async play(isforce=true) {
        if (this.isPaused && !isforce)  return;
        if (!this.isInit)  this.init();

        if (this.audioContext.state === 'suspended') {
            try {
                await this.audioContext.resume();
            } catch (err) {
                alert('Press OK and then click anywhere on the page to start the audio.');
                await new Promise(resolve => document.addEventListener('click', resolve, {once: true}));
                await this.audioContext.resume();
            }
        }
        const totalDuration = this.audioQueue.reduce((acc, buffer) => acc + buffer.duration, 0);
        console.log('playAudioQueue: totalDuration', totalDuration);
    
        if (totalDuration < 10 && this.isFirstPlay) {
            return;
        }
    
        if (this.isPlaying || this.audioQueue.length === 0) {
            return;
        }
    
        this.isPaused = false;
        this.isPlaying = true;
        this.isFirstPlay = false;
        console.log('AudioStreamPlayer: isPlaying', this.isPlaying);
    
        this.source = this.audioContext.createBufferSource();
        this.source.connect(this.audioContext.destination);

        if (this.pausedAt) {
            this.source.buffer = this.pause_buff;
            this.startTime = this.audioContext.currentTime - this.pausedAt;
            this.source.start(0, this.pausedAt);
            this.pausedAt = 0;
            this.pause_buff = null;
        } else {
            this.source.buffer = this.audioQueue.shift();
            this.startTime = this.audioContext.currentTime;
            this.source.start(0);
        }

        this.source.onended = () => {
            console.log('play_stream: onended');

            this.isPlaying = false;
            this.play(false);
        };
    }

    pause() {
        if (this.isPaused) return;

        this.pause_buff = this.source.buffer;
        this.source.stop();
        this.pausedAt = this.audioContext.currentTime - this.startTime;
        this.isPaused = true;
    }

    async init() {
        this.isInit = true;
        await this.__fetchAudioStream();
    }
}

function appendMessages(msg) {
    console.log(msg)
}

async function getData(response) {
    return await response.json()
}
async function getTextData(response) {
    return await response.text()
}
function getLocalTimeInMillis() {
    return (new Date).getTime()
}

function parseResponseTextDataAsJSON(text, prefix, errMsg) {
    var data = "" + text;
    if (prefix !== data.charAt(0)) {
        throw Error(errMsg)
    }
    try {
        data = JSON.parse(data)
    } catch (e) {
        data = {
            message: errMsg
        }
    }
    if (data.message) throw Error(data.message);
    return data
}
function getAudioBufferChannelData(audioBuffer, channel) {
    if (null === audioBuffer) {
        return new AudioDataEntry
    }
    let float32 = audioBuffer.getChannelData(channel);
    let minFloat = 0;
    let maxFloat = 0;
    let value;
    let index;
    let duration = audioBuffer.duration;
    duration = parseInt(.5 + duration);
    if (duration > MAXIMUM_DURATION) duration = MAXIMUM_DURATION;
    let sampleRate = parseInt(audioBuffer.sampleRate);
    let max_size = sampleRate * duration;
    let _length = parseInt(float32.length);
    let size = _length > max_size ? max_size : _length;
    size = parseInt(size);
    for (index = 0; index < size; index++) {
        value = float32[index];
        if (value < minFloat) minFloat = value;
        if (value > maxFloat) maxFloat = value
    }
    let divider = .01 + maxFloat - minFloat;
    console.log(`divider ${divider}, minFloat = ${minFloat} maxFloat = ${maxFloat}`);
    let iMin = 0;
    let iMax = 0;
    let iSum = 0;
    let skip = parseInt(sampleRate / FINGERPRINT_SAMPLE_RATE);
    if (skip < 1) skip = 1;
    let limit = parseInt(size / skip);
    let results = new Array(limit);
    let offset;
    for (index = 0, offset = 0; offset < limit && index < size; index += skip, offset++) {
        value = float32[index];
        value = value * AMPLITUDE_SIZE;
        value = parseInt(value / divider);
        if (value < iMin) iMin = value;
        if (value > iMax) iMax = value;
        results[offset] = value;
        iSum = iSum + value
    }
    while (offset < limit) results[offset++] = 0;
    console.log(`divider ${divider}, iMin = ${iMin} iMax = ${iMax} iSum = ${iSum}`);
    let audioData = new AudioDataEntry;
    let fingerprintRate = parseInt(sampleRate / skip);
    audioData.setAudioData(results, limit, sampleRate, duration, fingerprintRate);
    return audioData
}

// show / hide modal
var timerForPopupToHideModal = null;
var modalPopupElement = null;
function getModalPopupElement() {
    if (modalPopupElement === null) {
        modalPopupElement = jQuery("div#thisModal")
    }
    return modalPopupElement
}

function procToTerminatePopupModal() {
    if (timerForPopupToHideModal !== null) {
        clearInterval(timerForPopupToHideModal);
        timerForPopupToHideModal = null
    }
    getModalPopupElement().modal("hide");
    liveTimerStart = getLocalTimeInMillis();
    liveState = LIVE_WAIT_NEXT_TRIGGER;
    setOrClearPlayTimer(false, monitorLiveAudioFeed)
}

async function activateChannelModal(btnumber) {
    setOrClearPlayTimer(true, null);
    adjustAnimationTimer();
    getModalPopupElement().modal("show");
    timerForPopupToHideModal = setTimeout(procToTerminatePopupModal, 7e3);
    return true
}



function displaySpinner(isDisplay) {
    if (isDisplay) {
        spinnerGif.style = "display:block"
    } else {
        spinnerGif.style = "display:none"
    }
}

function showHidePlayButton(isDisplay) {
    if (isDisplay) {
        playButtonObject.style = "display:block"
    } else {
        playButtonObject.style = "display:none"
    }
}

function setButtonPlayOrPause(isPlay) {
    document.getElementById("playorpause").innerText = isPlay ? "Play" : "Pause"
}
function showMethodRunTime(title, atStart, atEnd) {
    var diff = atEnd - atStart;
    console.log(`Runtime: ${title}, s:${atStart}, e:${atEnd}, d:${diff}`)
}


async function playonclick() {
    if (!audioStreamPlayer.isPlaying) {
        try {
            await audioStreamPlayer.play();
            setButtonPlayOrPause(false);
        } catch (error) {
            console.error(`Error streaming audio play: ${error}`);
        }  
    }
    else {
        try {
            await audioStreamPlayer.pause();
            setButtonPlayOrPause(true);
        } catch (error) {
            console.error(`Error streaming audio pause: ${error}`);
        }  
    }

    return true
}

async function doLogin() {
    var element = document.getElementById("usertoken");
    var data = element.value || "${token}";
    isJWTloaded = false;
    if (data !== "${token}") {
        userToken = data;
        isJWTloaded = true;
        console.log(JSON.stringify({
            userId: 17001002,
            JWT: userToken
        }))
    }
}

function initVariables() {

    triggerFingerprintData = [];
    triggerFingerprintSize = 0;

    spinnerGif = document.getElementById("spinner");
    playButtonObject = document.getElementById("play");

}

async function initTriggerAudio(triggerAudioUrl) {
    console.log("initTriggerAudio");
    var start = getLocalTimeInMillis();

    if (triggerFingerprintSize) return;

    triggerFingerprintData = [];

    let trigger_audioContext = new (window.AudioContext || window.webkitAudioContext)();

    const audioResponse = await fetch(triggerAudioUrl);
    const arrayBuffer = await audioResponse.arrayBuffer();
    let audioBuff = await trigger_audioContext.decodeAudioData(arrayBuffer);
   
    let audioData = getAudioBufferChannelData(audioBuff, 0);
    audioData.setUrl(triggerAudioUrl);

    var end = getLocalTimeInMillis();
    showMethodRunTime("initTriggerAudio", start, end);
    start = end;
    let data;
    let sData = JSON.stringify(audioData);
    appendMessages("Calling calculateFingerprint API");
    const resCalculateFingerprint = await fetch(base_host + "/dev/v3/calculateFingerprint", {
        method: "POST",
        mode: "cors",
        headers: {
            "Content-type": "application/json; charset=UTF-8",
            Accept: "application/json",
            "Access-Control-Allow-Origin": "*",
            Authorization: "Bearer " + userToken
        },
        body: sData
    });

    try {
        const text = await getTextData(resCalculateFingerprint);
        data = parseResponseTextDataAsJSON(text, "{", "Missing fingerprint");
        try {
            triggerFingerprintData = JSON.parse(data.dataEx)
            // triggerFingerprintZipped = zipArray(triggerFingerprintData);
            console.log("TRIGGER: ", triggerFingerprintZipped);
        } catch (e) {
            throw Error(e.message)
        }
        triggerFingerprintSize = data.size;
        console.log(JSON.stringify({
            size: triggerFingerprintSize,
            data: triggerFingerprintData
        }))
    } catch (error) {
        triggerFingerprintData = [];
        triggerFingerprintSize = 0;
        console.error("ERROR:", error);
        appendMessages("initTriggerAudio API on ERROR: " + error)
    }
}

async function startCanvas() {
    console.log("startCanvas");
    initVariables();
    displaySpinner(true);
    setButtonPlayOrPause(true);

    await doLogin();
    if (isJWTloaded) {
        await initTriggerAudio(TRIGGERSOUND_AUDIO_URL);
        if (triggerFingerprintSize > 0) {
            if (IF_LOAD_FROM_URL) {
                audioStreamPlayer = new AudioStreamPlayer(LOAD_FROM_THIS_URL, null);
            } else {
                audioStreamPlayer = new AudioStreamPlayer(TEST_MP3_FILE, null);
            }
            showHidePlayButton(true);
        }
    }
    displaySpinner(false)
}