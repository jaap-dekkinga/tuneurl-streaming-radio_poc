/**
 * @license BSD 3-Clause License
 *
 * @copyright Copyright (c) 2023-2024, TuneURL Inc.
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
const uniqueUserId = Math.floor(Math.random() * 1e12).toString().padStart(16, '0');

const base_host = "https://streaming.tuneurl-demo.com";
// const base_host = "http://localhost:8281";
const LOAD_FROM_THIS_URL = "https://stream.radiojar.com/vzv0nkgsw7uvv";
// const TEST_MP3_FILE = base_host + "/audio/10.1s.mp3";
const TEST_MP3_FILE = base_host + "/audio/10240-audio-streams-0230000.mp3";
// const TEST_MP3_FILE = base_host + "/audio/webrtc-source_J7XLHMyC.mp3";
const TRIGGERSOUND_AUDIO_URL = base_host + "/audio/10240-triggersound.wav";


const IF_LOAD_FROM_URL = true;
const APP_TITLE = "Audio Demo Test";

const AMPLITUDE_SIZE = 512;
const HALF_AMPLITUDE_SIZE = 256;
const MAXIMUM_DURATION = 1020;
const FINGERPRINT_SAMPLE_RATE = 10240;
const STREAM_DURATION = 1.5;
const IF_SEARCH_API_BROKEN = false;

const LIVE_INIT = 0;
const LIVE_WAIT_MODAL_STATE = 1;
const LIVE_WAIT_FOR_OFFSET = 2;
const LIVE_WAIT_FOR_TRIGGER = 3;
const LIVE_WAIT_NEXT_TRIGGER = 4;
const LIVE_WAIT_FOR_CONTENT = 5;
const LIVE_END = 6;

var liveState = LIVE_INIT;
var isJWTloaded = false;
var userToken = null;

var triggerFingerprintData = null;
var triggerFingerprintZipped = {};

var spinnerGif = null;
var playButtonObject = null;
var channelTagIndex = 0;

let audioStreamURL = "";

let audioStreamPlayer = null;
let audioStreamQueue = [];
let audioAudioDataEntries = [];

let count_PostTuneURL = 0;

var uniquetype = [];
var activeAudioTags = {
    liveTags: [],
    tuneUrlCounts: 0
};
let index_DataEntry = 0;
let triggr_fingerprint = null;
let g_remove_count = 0;
let remainStream = new Float32Array(0);
let activeUrl = null;
let currentTag = null;

class ContinuousCaller extends EventTarget {
    constructor() {
        super();
    }
    async start_generateDataEntries() {
        try {
            await generateDataEntries();
            await new Promise((r => setTimeout(r, 0)))
            this.dispatchEvent(new Event('generateDataEntries_end')); // Trigger event when function finishes
        } catch (error) {
            console.error('Error occurred during start_generateDataEntries calling:', error);
        }
    }
    async start_findTriggerSound() {
        try {
            await findTriggerSound();
            await new Promise((r => setTimeout(r, 0)))
            this.dispatchEvent(new Event('findTriggerSound_end')); // Trigger event when function finishes
        } catch (error) {
            console.error('Error occurred during start_findTriggerSound calling:', error);
        }
    }
}

class AudioDataEntry {
    constructor() {
        this.Url = "";
        this.Data = null;
        this.Size = 0;
        this.sampleRate = 44100;
        this.duration = 0;
        this.fingerprintRate = FINGERPRINT_SAMPLE_RATE
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
        this.audioContext = new (window.AudioContext || window.webkitAudioContext)({sampleRate:44100});
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

        // ----------------------
        this.totalPlayTime = 0;
        this.timerInterval = null;
        this.startedPlayTime = 0;
        
        this.totaltime = 0;
        // ----------------------
    }

    async __fetchAudioStream() {
        const response = await fetch(this.url);
        const reader = response.body.getReader();    
    
        const sampleRate = 11025; // Assuming the sample rate is 44100 Hz       
        // Calculate the number of bytes to read for the desired duration

        let chunks = [];
        let bytesRead = 0;
        const bytesToRead = sampleRate * STREAM_DURATION * 4; // 4 bytes per sample (32-bit float, stereo)
    
        while (true) {

            const { done, value } = await reader.read();
            if (done) break;

            chunks.push(value);
            bytesRead += value.byteLength;

            if (bytesRead >= bytesToRead) {
                const audioBuffer = new Uint8Array(bytesRead);
                let offset = 0;
                for (const chunk of chunks) {
                    audioBuffer.set(chunk, offset);
                    offset += chunk.byteLength;
                }
                chunks = [];
                bytesRead = 0;
                
                let audioData = await this.audioContext.decodeAudioData(audioBuffer.buffer);
                // console.log('fetchAudioStream: audioData', audioData);
                // call hook_func
                if (this.callback_stream)
                    this.callback_stream(audioData);
                
                // Add new audio Segment.
                const newSegment = await this.__createNewAudioSegment(audioData);
                if (newSegment) {
                    this.audioQueue.push(newSegment);
                    this.play(false);
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
        // console.log('AudidStreamPlayer::__createNewAudioSegment', audioData.length);
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
    
        if (!this.pause_buff) {
            const totalDuration = this.audioQueue.reduce((acc, buffer) => acc + buffer.duration, 0);
            console.log('playAudioQueue: totalDuration', totalDuration);

            if (totalDuration < 16 && this.isFirstPlay) {
                return;
            }
        
            if (this.isPlaying || this.audioQueue.length === 0) {
                return;
            }    
        }
    
        this.source = this.audioContext.createBufferSource();
        this.source.connect(this.audioContext.destination);

        this.isPaused = false;
        this.isPlaying = true;
        this.isFirstPlay = false;

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

        // ************************************************************************************************
        // Record the start time
        this.startedPlayTime = Date.now();
        // Start the timer
        this.timerInterval = setInterval(() => {
            const currentTime = Date.now();
            this.totalPlayTime += (currentTime - this.startedPlayTime);  // Convert milliseconds to seconds
            updatePocTitle(this.totalPlayTime);
            this.startedPlayTime = currentTime;  // Reset start time for the next interval
        }, 10);  // Update every 10 millisecond
        // ************************************************************************************************

        this.source.onended = () => {
            // console.log('play_stream: onended');
            clearInterval(this.timerInterval);
    
            this.isPlaying = false;
            this.play(false);
        };
    }

    pause() {
        if (this.isPaused) return;

        // -----------------------------------------------------------------------
        // Clear the interval and update the total play time
        clearInterval(this.timerInterval);

        const currentTime = Date.now();
        this.totalPlayTime += (currentTime - this.startedPlayTime);
        // this.startedPlayTime = 0;
        
        this.pause_buff = this.source.buffer;
        this.source.stop();
        this.pausedAt = this.audioContext.currentTime - this.startTime;
        this.isPaused = true;
        // -----------------------------------------------------------------------
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

// press audioStream buffer
async function audioStream_load(audioData)
{
    if (audioData.length == 0)  return;    
    audioStreamQueue.push(audioData);
}

//=======================================================================
// start process A to generate the dataEntry of STREAM_DURATION
async function generateDataEntries()
{
    const totalDuration = audioStreamQueue.reduce((acc, buffer) => acc + buffer.duration, 0);
    if (totalDuration == 0) return;

    let audioData = audioStreamQueue.shift();
    const length_Entry = STREAM_DURATION * audioData.sampleRate;
    let audioStream = audioData.getChannelData(0);

    let length = remainStream.length + audioStream.length;
    if (length < length_Entry) {

        let concatenatedArray = new Float32Array(length);
        concatenatedArray.set(remainStream);
        concatenatedArray.set(audioStream, remainStream.length);
        
        remainStream = concatenatedArray;
    }
    else {

        let offset = 0;
        let offset_1 = 0;
        while (length >= length_Entry) {

            let entry_stream = new Float32Array(length_Entry);
            let length_read = length_Entry;
            if (remainStream.length) {

                entry_stream.set(remainStream);
                offset = remainStream.length;
                remainStream = new Float32Array(0);

                length_read = length_Entry - offset;
                length -= offset;
            }

            entry_stream.set(audioStream.slice(offset_1, offset_1+length_read), offset);
            audioAudioDataEntries.push(entry_stream);

            offset = 0;
            offset_1 += length_read;
            length -= length_read;
        }
        if (length) {
            remainStream = new Float32Array(length)
            remainStream.set(audioStream.slice(offset_1, offset_1+length)); 
        }
    }
}

//=======================================================================
// start process B to find the trigerSound
async function findTriggerSound()
{
    let length = 0;
    let offset = 0;
    if (g_remove_count) {

        if (g_remove_count > audioAudioDataEntries.length)
            return;
        

        for (let index = 0; index < g_remove_count; index++) {
            length += audioAudioDataEntries[index].length;
        }
        let audioStream = new Float32Array(length);
        for (let index = 0; index < g_remove_count; index++) {
            audioStream.set(audioAudioDataEntries[index], offset);
            offset += audioAudioDataEntries[index].length;
        }

       
        let pos = parseInt(triggr_fingerprint.offset/1e3 + 1.5)* 44100;
        let size = 3 * 44100;

        let tuneURL_stream = new Float32Array(size);
        tuneURL_stream.set(audioStream.slice(pos, pos + size));

        let timeOffset = index_DataEntry * STREAM_DURATION * 1e3;
        initAllTags(triggr_fingerprint, tuneURL_stream, timeOffset);

        index_DataEntry += g_remove_count;
        audioAudioDataEntries.splice(0, g_remove_count);
        g_remove_count = 0;
    }

    let count = 2;
    if (audioAudioDataEntries.length < count) return;
    if (!triggerFingerprintData) return;

    if (count > audioAudioDataEntries.length)
        count = audioAudioDataEntries.length;

    length = 0;
    for (let index = 0; index < count; index++) {
        length += audioAudioDataEntries[index].length;
    }

    offset  = 0;
    let audioStream = new Float32Array(length);
    for (let index = 0; index < count; index++) {
        audioStream.set(audioAudioDataEntries[index], offset);
        offset += audioAudioDataEntries[index].length;
    }
    
    let dataEntry = getAudioBufferChannelData(audioStream, STREAM_DURATION * count, 44100);   
    dataEntry.setUrl(audioStreamURL); 
    var datus = {
        audioData: dataEntry,
        dataFingerprint: triggerFingerprintData,
    };

    await getTurnUrlTags(datus);
}

async function extract_fingerprint(tuneURL_stream) {

    let size = tuneURL_stream.length;
    let buff = new Float32Array(size*2);

    // convert from mono to stereo
    let indexStereo = 0;
    for(let i=0; i<size; i++){

        buff[indexStereo] = tuneURL_stream[i];
        buff[indexStereo + 1] = tuneURL_stream[i];

        indexStereo = indexStereo + 2;
    }

    let tmp_audioContext = new (window.AudioContext || window.webkitAudioContext)({sampleRate:44100});
    const stereo_buff = tmp_audioContext.createBuffer(1, size*2, 44100);
    stereo_buff.copyToChannel(buff, 0);

    // Resample the audio buffer
    const resample_tuneURL_stream = await convert_to_10240(stereo_buff, FINGERPRINT_SAMPLE_RATE);
    let float32 = resample_tuneURL_stream.getChannelData(0);
    // convert from stereo to mono
    let dstIndex = 0;
    let results = new Array(size);
    for (let index = 0; index < size; index++) {
        let sample = float32[dstIndex];

        if (sample > 1.0) sample = 1.0;
        if (sample < -1.0) sample = -1.0;

        results[index] = Math.ceil(sample < 0 ? sample * 0x8000 : sample * 0x7FFF); 
        dstIndex += 2;
    }

    let tuneURL_Fingerprint = null;
    let sData = JSON.stringify(results);
    appendMessages("Calling extractfingerprint API");
    const reponse = await fetch(base_host + "/dev/v3/extractfingerprint", {
        method: "POST",
        mode: "cors",
        headers: {
            "Content-type": "application/json; charset=UTF-8",
            Accept: "application/json",
            "Access-Control-Allow-Headers": "*",
            Authorization: "Bearer " + userToken
        },
        body: sData
    });

    try {
        const text = await getTextData(reponse);
        let data = parseResponseTextDataAsJSON(text, "{", "No Trigger sound found");
        tuneURL_Fingerprint = "{\"fingerprint\":{\"type\":\"Buffer\",\"data\":" + data.dataEx + "},\"fingerprint_version\":\"1\"}";
        // tuneURL_Fingerprint = parseResponseTextDataAsJSON(fingertext, "{", "No Trigger sound found");;
        console.log(JSON.stringify({
            tuneURL_Fingerprint
        }))
    } catch (error) {
        tuneURL_Fingerprint = null;
        console.error("ERROR:", error);
        appendMessages("extract_fingerprint API on ERROR: " + error)
    }
    return tuneURL_Fingerprint;
}

async function convert_to_10240(originalAudioBuffer, targetSampleRate)
{
    // Create an OfflineAudioContext with the target sample rate
    const offlineContext = new OfflineAudioContext(
        originalAudioBuffer.numberOfChannels,
        Math.floor(originalAudioBuffer.duration * targetSampleRate),
        targetSampleRate
    );

    // Create a buffer source and set the buffer
    const bufferSource = offlineContext.createBufferSource();
    bufferSource.buffer = originalAudioBuffer;

    // Connect the buffer source to the offline context's destination
    bufferSource.connect(offlineContext.destination);

    // Start playback
    bufferSource.start(0);

    // Render the audio data to the new sample rate
    const newAudioBuffer = await offlineContext.startRendering();
    
    return newAudioBuffer;
}



async function getTurnUrlTags(datus)
{
    let data;
    let sData = JSON.stringify(datus);

    // let timeOffset = index_DataEntry * STREAM_DURATION * 1e3;
    // appendMessages(`Calling evaluateAudioStream API- ${timeOffset}`);
    // const res = await fetch(base_host + "/dev/v3/evaluateAudioStream", {
    //     method: "POST",
    //     mode: "cors",
    //     headers: {
    //         "Content-type": "application/json; charset=UTF-8",
    //         Accept: "application/json",
    //         "Access-Control-Allow-Origin": "*",
    //         Authorization: "Bearer " + userToken
    //     },
    //     body: sData
    // }).then((response => getTextData(response))).then((text => {
    //     data = parseResponseTextDataAsJSON(text, "{", "No Trigger sound found");
    //     if (data.tagCounts) {
    //         initAllTags(data.liveTags[0], timeOffset);

    //         let remove_count = Math.floor((data.liveTags[0].dataPosition/1e3 + 6) / STREAM_DURATION);
    //         index_DataEntry += remove_count;
    //         audioAudioDataEntries.splice(0, remove_count);
    //     }
    //     else {
    //         index_DataEntry += 2;
    //         audioAudioDataEntries.splice(0, 2);
    //     }

    //     console.log(JSON.stringify({
    //         tuneUrlCounts: data.tuneUrlCounts,
    //         counts: data.tagCounts,
    //         liveTags: data.liveTags
    //     }));
    // })).catch((error => {
    //     console.error("ERROR:", error);
    //     appendMessages("evaluateAudioStream API on ERROR: " + error)
    // }))        
    let timeOffset = index_DataEntry * STREAM_DURATION * 1e3;
    appendMessages(`Calling findFingerPrintsAudioStream API- ${timeOffset}`);
    const res = await fetch(base_host + "/dev/v3/findFingerPrintsAudioStream", {
        method: "POST",
        mode: "cors",
        headers: {
            "Content-type": "application/json; charset=UTF-8",
            Accept: "application/json",
            "Access-Control-Allow-Origin": "*",
            Authorization: "Bearer " + userToken
        },
        body: sData
    }).then((response => getTextData(response))).then((text => {
        data = parseResponseTextDataAsJSON(text, "{", "No Trigger sound found");
        if (data.count) {
            g_remove_count = Math.ceil((data.fingerPrint.offset/1e3 + 6) / STREAM_DURATION);
            triggr_fingerprint = data.fingerPrint;
        }
        else {
            index_DataEntry += 1;
            audioAudioDataEntries.splice(0, 1);
        }

        console.log(JSON.stringify({
            tuneUrlCounts: data.tuneUrlCounts,
            counts: data.tagCounts,
            liveTags: data.liveTags
        }));
    })).catch((error => {
        console.error("ERROR:", error);
        appendMessages("evaluateAudioStream API on ERROR: " + error)
    }))    

}

async function initAllTags(fingerPrint, tuneURL_stream, timeOffset) {
    extract_fingerprint(tuneURL_stream)
    .then((description) => {
        fingerPrint.description = description;

        let offset = fingerPrint.offset;
        let payload = "" + fingerPrint.description;
        console.log(JSON.stringify({
            offset: timeOffset,
            index: offset,
        }));
        return loadTuneUrlPage(payload, {
            similarity: fingerPrint.similarity
        });
    })
    .then((url) => {
        if (url !== null) {
            url.dataPosition = fingerPrint.offset + timeOffset;
            url.index = fingerPrint.offset;
            activeAudioTags.liveTags.push({...url});
            activeAudioTags.tuneUrlCounts += 1;
            console.log({...url});
        }
    })
    // fingerPrint.description = await extract_fingerprint(tuneURL_stream);

    // let offset = fingerPrint.offset;
    // let payload = "" + fingerPrint.description;
    // console.log(JSON.stringify({
    //     offset: timeOffset,
    //     index: offset,
    // }));
    // let url = await loadTuneUrlPage(payload, {
    //     similarity: fingerPrint.similarity
    // });
    // if (url !== null) {
    //     url.dataPosition = offset + timeOffset;
    //     url.index = offset;
    //     activeAudioTags.liveTags.push({...url});
    //     activeAudioTags.tuneUrlCounts += 1;
    //     console.log({...url});
    // }
}

function locateFingerprintWithAboveMatchPercentage(ary, index, other) {
    var j, k = -1;
    var rate = parseInt(0, 10);
    var data, matchPercentage;
    for (j = 0; j < ary.length; j++) {
        if (j !== index && j !== other) {
            data = ary[j];
            if (data.info !== null && data.info.length > 0 && (data.type === "open_page" || data.type === "save_page")) {
                matchPercentage = parseInt(data.matchPercentage, 10);
                if (matchPercentage > rate) {
                    k = j;
                    rate = matchPercentage
                }
            }
        }
    }
    if (k < 0) {
        for (j = 0; j < ary.length; j++) {
            if (j !== index && j !== other) {
                if (isUniqueId(j)) {
                    k = j;
                    break
                }
            }
        }
    }
    return [k, ary[k]]
}

function selectBestMatchApiUrl(results, json) {
    let ary = undefined;
    if (typeof results === "object" && results.length > 0) {
        ary = results[0]
    }
    if (ary === undefined || ary.id === undefined || ary.type === undefined || ary.info === undefined || ary.matchPercentage === undefined) {
        return null
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
    })
}

var simulatedSearchMatchApiResult = null;
function loadTuneUrlPage(payload, json) {
    return loadTuneUrlFromServer(payload, function(onError, datus) {
        if (onError !== null) {
            if (simulatedSearchMatchApiResult !== null) {
                return Promise.resolve(simulatedSearchMatchApiResult);
            }
            return fetch(base_host + "/json/pretty-fingerprint-results-fingerprint1.json", {
                method: "GET",
                headers: {
                    "Content-type": "application/json; charset=UTF-8",
                    Accept: "application/json"
                }
            })
            .then(response => response.json())
            .then(responseData => {
                simulatedSearchMatchApiResult = selectBestMatchApiUrl(responseData, json);
                return simulatedSearchMatchApiResult;
            })
            .catch(error => {
                console.error("ERROR:", error);
                return null;
            });
        } else {
            return Promise.resolve(datus);
        }
    });
}

const formatDate = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}${minutes}`;
};

async function reportUserInteraction(tuneurlId, interestAction = "heard") {
    const now = new Date();
    const utcTime = formatDate(now);

    const data = [
        {
            "UserID": uniqueUserId,
            "Date": utcTime,
            "TuneURL_ID": String(tuneurlId),
            "Interest_action": interestAction,
        }
    ];

    console.log(data)

    // Send the request using fetch
    fetch('https://65neejq3c9.execute-api.us-east-2.amazonaws.com/interests', {
        method: 'POST',
        headers: {
            'Content-Type': 'text/plain',
        },
        body: JSON.stringify(data),
    })
    .then(response => response.json())
    .then(data => console.log('Success:', data))
    .catch((error) => console.error('Error:', error));
}

async function loadTuneUrlFromServer(payload, callback) {
    let data, url;
    if (IF_SEARCH_API_BROKEN) {
        console.error("Search API is broken");
        return callback("Search API is broken", null)
    }

    const endpoint = "https://pnz3vadc52.execute-api.us-east-2.amazonaws.com/dev/search-fingerprint";

    async function readStream(reader) {
        const chunks = [];
        const decoder = new TextDecoder();
        let result;
        while (!(result = await reader.read()).done) {
            const chunk = decoder.decode(result.value, { stream: true });
            chunks.push(chunk)
            // Process the chunk (e.g., append to a buffer, display on UI, etc.)
        }
        return chunks
    }

    return fetch(endpoint, {
        method: "POST",
        // mode: "no-cors",
        headers: {
            accept:"application/json",
            "sec-fetch-dest":"empty",
            "sec-fetch-mode":"no-cors",
        },
        body: payload
    })
    .then((res) => {
        const reader = res.body.getReader();
        return readStream(reader)
    })
    .then((chunks) => {
        const textData = chunks[0]   
        data = parseResponseTextDataAsJSON(textData, "[", "No Matching fingerprint found");
        url = selectBestMatchApiUrl(data, {
            score: 1,
            similarity: 1
        });

        if (url === null) return callback("Empty", null);
        return callback(null, url)
    })
}

var trigger_sound_max = null;
function getAudioBufferChannelData(audioBuffer, duration, sampleRate) {

    if (null === audioBuffer) {
        return new AudioDataEntry
    }
    let float32 = audioBuffer;
    let minFloat = 0;
    let maxFloat = 0;
    let value;
    let index;
    duration = parseInt(.5 + duration);
    if (duration > MAXIMUM_DURATION) duration = MAXIMUM_DURATION;
    sampleRate = parseInt(sampleRate);
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

    // console.log(`divider ${divider}, minFloat = ${minFloat} maxFloat = ${maxFloat}`);
    let iMin = 0;
    let iMax = 0;
    let iSum = 0;
    let skip = parseInt(sampleRate / FINGERPRINT_SAMPLE_RATE);
    if (skip < 1) skip = 1;
    let limit = parseInt(size / skip);
    let max_limit = parseInt(max_size / skip);
    let results = new Array(max_limit);
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

    while (offset < max_limit) results[offset++] = 0;
    console.log(`divider ${divider}, iMin = ${iMin} iMax = ${iMax} iSum = ${iSum}`);
    let audioData = new AudioDataEntry;
    let fingerprintRate = parseInt(sampleRate / skip);
    audioData.setAudioData(results, max_limit, sampleRate, duration, fingerprintRate);
    return audioData
}

//=======================================================================
// back end C process to show / hide modal
var timerForPopupToHideModal = null;
var modalPopupElement = null;
function getModalPopupElement() {
    if (modalPopupElement === null) {
        modalPopupElement = jQuery("div#thisModal")
    }
    return modalPopupElement
}

function updatePocTitle(totalPlayTime) {
    var title = APP_TITLE + " - " + (totalPlayTime / 1000).toFixed(2) + 's';
    document.getElementById("pocTitle").innerHTML = title
}

function procToTerminatePopupModal() {
    if (timerForPopupToHideModal !== null) {
        clearInterval(timerForPopupToHideModal);
        timerForPopupToHideModal = null
    }
    getModalPopupElement().modal("hide");
    activeUrl = null;
    liveState = LIVE_WAIT_NEXT_TRIGGER;
}

async function activateChannelModal(btnumber) {
    console.log("currentTag", currentTag);
    // report api
    reportUserInteraction(currentTag.id, "heard");

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

function executeChannelModal(iRef) {
    if (parseInt(iRef) > 0) {
        reportUserInteraction(currentTag.id, "interested");
        if (activeUrl) {
            window.open(activeUrl, "_blank").focus()
        }
        activeUrl = null;
    } else {
        reportUserInteraction(currentTag.id, "uninterested");
    }
    procToTerminatePopupModal();
}

async function showPopupByAudioStream(totalPlayTime) {

    let threshold = 500;

    for (let i = 0; i < activeAudioTags.liveTags.length; i ++) {
        let diff = totalPlayTime - activeAudioTags.liveTags[i].dataPosition;

        if (diff > 0 && diff <= threshold) {
            activeUrl = activeAudioTags.liveTags[i].info;
            currentTag = activeAudioTags.liveTags[i];
            activeAudioTags.liveTags.splice(i, 1);
            activateChannelModal(0);

            break;
        }
    }
}

// =============================================
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

    triggerFingerprintData = null;

    spinnerGif = document.getElementById("spinner");
    playButtonObject = document.getElementById("play");
}

async function initTriggerAudio(triggerAudioUrl) {
    // console.log("initTriggerAudio");
    var start = getLocalTimeInMillis();

    if (triggerFingerprintData) return;

    let trigger_audioContext = new (window.AudioContext || window.webkitAudioContext)({sampleRate:44100});

    const audioResponse = await fetch(triggerAudioUrl);
    const arrayBuffer = await audioResponse.arrayBuffer();
    let audioBuff = await trigger_audioContext.decodeAudioData(arrayBuffer);

    let audioData = getAudioBufferChannelData(audioBuff.getChannelData(0), 
                audioBuff.duration, audioBuff.sampleRate);

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
        triggerFingerprintData = text;
        console.log(JSON.stringify({
            data: triggerFingerprintData
        }))
    } catch (error) {
        triggerFingerprintData = null;
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
        if (triggerFingerprintData) {
            if (IF_LOAD_FROM_URL) {
                audioStreamURL = LOAD_FROM_THIS_URL;
            } else {
                audioStreamURL = TEST_MP3_FILE;
            }
            audioStreamPlayer = new AudioStreamPlayer(audioStreamURL, audioStream_load);

            const caller = new ContinuousCaller();

            // start process A to generate the dataEntry of STREAM_DURATION
            caller.start_generateDataEntries();
            caller.addEventListener('generateDataEntries_end', () => {
                caller.start_generateDataEntries();
            });
            // start process B to find the trigerSound
            caller.start_findTriggerSound();
            caller.addEventListener('findTriggerSound_end', () => {
                caller.start_findTriggerSound();
            }); 

            // start process C to show pop-up/notification by the TurnUrlTags
            setInterval(() => showPopupByAudioStream(audioStreamPlayer.totalPlayTime), 100);
            showHidePlayButton(true);
        }
    }
    displaySpinner(false)
}