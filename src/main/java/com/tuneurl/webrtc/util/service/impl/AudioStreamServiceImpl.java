/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2024, Teodoro M. Albon, <albonteddy@gmail.com>
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

package com.tuneurl.webrtc.util.service.impl;

import com.albon.auth.util.Helper;
import com.tuneurl.webrtc.util.controller.dto.*;
import com.tuneurl.webrtc.util.exception.BaseServiceException;
import com.tuneurl.webrtc.util.model.AudioStreamDatabase;
import com.tuneurl.webrtc.util.service.AudioStreamDatabaseService;
import com.tuneurl.webrtc.util.service.AudioStreamService;
import com.tuneurl.webrtc.util.util.*;
import com.tuneurl.webrtc.util.util.fingerprint.FingerprintExternals;
import com.tuneurl.webrtc.util.util.fingerprint.FingerprintThreadCollector;
import com.tuneurl.webrtc.util.util.fingerprint.FingerprintUtility;
import com.tuneurl.webrtc.util.value.Constants;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Implement AudioStreamDatabaseService interface.
 *
 * @author albonteddy@gmail.com
 * @version 1.1
 */
@Service
public class AudioStreamServiceImpl implements AudioStreamService {

  private final AudioStreamDatabaseService audioStreamDatabaseService;

  @Value("${save.audio.files:/home/justin/audio}")
  private String saveAudioFiles;

  @Value("${audio.stream.url.prefix}")
  private String streamAudioUrlPrefix;

  private final boolean isDebugOn = Constants.DEBUG_FINGERPRINTING;

  private MessageLogger logger;
  private final TagsHelper tagsHelper = new TagsHelper();

  /** Set @logger class variable with a MessageLogger. */
  protected void setupMessageLogger() {
    if (this.logger == null) {
      this.logger = new MessageLogger();
      this.logger.setLogger(LogManager.getLogger(com.tuneurl.webrtc.util.util.MessageLogger.class));
    }
  }

  protected FingerprintExternals fingerprintExternals =
      FingerprintUtility.getFingerprintInstance().getFingerprintExternals();

  public AudioStreamServiceImpl(AudioStreamDatabaseService audioStreamDatabaseService) {
    setupMessageLogger();
    this.audioStreamDatabaseService = audioStreamDatabaseService;
  }

  public EvaluateAudioStreamResponse evaluateAudioStream(
      AudioDataEntry audioDataEntry,
      EvaluateAudioStreamEntry evaluateAudioStreamEntry,
      String signature) {
    // The Audio Stream URL.
    String url = CommonUtil.getString(audioDataEntry.getUrl(), Constants.AUDIOSTREAM_URL_SIZE);
    // The Data.
    short[] data = audioDataEntry.getData();
    // The Size of data.
    int size = audioDataEntry.getSize().intValue();
    // The Sample rate.
    Long sampleRate = audioDataEntry.getSampleRate();
    // The Duration.
    Long duration = audioDataEntry.getDuration();
    // The Fingerprint rate.
    Long fingerprintRate = audioDataEntry.getFingerprintRate();

    // The Triggersound Fingerprint Data.
    // byte[] dataFingerprint = evaluateAudioStreamEntry.getDataFingerprint();
    String dataFingerprint = evaluateAudioStreamEntry.getDataFingerprint();

    this.logger.logEntry(
        signature,
        new Object[] {
          "url=", url,
          "data=", data.length == size,
          "size=", size,
          "SRate=", sampleRate,
          "duration=", duration,
          "FRate=", fingerprintRate,
          "fingerprintData=", dataFingerprint,
        });

    Converter.checkAudioDataEntryDataSize(audioDataEntry);
    Converter.validateShortDataSize(data, size);
    // Converter.validateDataSizeEx(dataFingerprint, sizeFingerprint.intValue());
    Converter.validateDurationEx(duration);
    // StringBuffer dataFingerprintBuffer =
    //     FingerprintUtility.getFingerprintBufferedPart(dataFingerprint, dataFingerprint.length);
    // int dataFingerprintBufferSize = dataFingerprint.length;

    final String fileName = Converter.validateUrlOrGencrc32(url);
    ProcessHelper.checkNullOrEmptyString(fileName, "AudioDataEntry.Url");
    if (duration < 1L || duration > 480L) {
      CommonUtil.BadRequestException("Duration must be 1 to 480 seconds only");
    }
    EvaluateAudioStreamResponse response = new EvaluateAudioStreamResponse();
    List<TuneUrlTag> liveTags = new ArrayList<TuneUrlTag>();
    long elapse;
    long timeOffset, baseOffset;
    long iStart, iEnd;
    long maxDuration = Converter.muldiv(1000, duration - 6L, 1L);
    long count, counts = Converter.muldiv(1000, duration - 6L, 100);
    int dSize;
    short[] dData;

    TuneUrlTag tag;
    String rootDir = this.getSaveAudioFilesFolder(null);
    String debugUniqueName = ProcessHelper.createUniqueFilename();
    String debugDir = String.format("%s/%s", rootDir, "debug");
    String rootDebugDir = String.format("%s/%s", debugDir, debugUniqueName);

    FingerprintCompareResponse fcr = null;
    FingerprintResponse fr = null;
    FingerprintResponse audioFr;
    if (isDebugOn) {
      ProcessHelper.makeDir(debugDir);
    }
    Random random = new Random();
    random.setSeed(new Date().getTime());

    FingerprintExternals fingerprintExternals = FingerprintExternals.getFingerprintInstance();

    LinkedList<FingerprintThreadCollector> fingerprintThreadList =
        parallelFingerprintCollect(
            data,
            fingerprintRate,
            dataFingerprint,
            maxDuration,
            counts,
            rootDir,
            random);

    for (count = 0L, elapse = 0L; count < counts && elapse < maxDuration; count++, elapse += 100L) {
      if (fingerprintThreadList.isEmpty()) {
        break;
      }
      FingerprintCollection result =
          fingerprintThreadList.removeFirst().getFingerprintCollectionResult();

      List<FingerprintResponse> frSelection = result.getFrCollection();
      List<FingerprintCompareResponse> selection = result.getFcrCollection();


      // timeOffset = elapse; // possible legacy code
      if (selection.size() == 5) {
        // fingerPrints.addAll(selection);
        Object[] fingerprintComparisonsResponse =
            FingerprintUtility.fingerprintComparisons(selection, frSelection, fcr, fr);
        fcr = (FingerprintCompareResponse) fingerprintComparisonsResponse[0];
        fr = (FingerprintResponse) fingerprintComparisonsResponse[1];

        if (fcr != null) {
          timeOffset = fcr.getOffset();
          timeOffset = timeOffset + 1500L;
          iStart = Converter.muldiv(timeOffset, fingerprintRate, 1000L);
          iEnd = Converter.muldiv(timeOffset + 3500L, fingerprintRate, 1000L);
          dSize = (int) (iEnd - iStart);
          dData = Converter.convertListShortEx(data, (int) iStart, dSize);
          // Calculate the audio's payload
          audioFr =
              fingerprintExternals.runExternalFingerprinting_Ex(random, rootDir, "fingerprintprev", dData, dData.length);

          tag = tagsHelper.newTag(true, 0L, audioFr, fcr);
          liveTags.add(tag);
          break;
        } // if (fcr != null)
      } // if (selection.size() == 5) 
    } // for (...)
    liveTags = tagsHelper.pruneTags(liveTags);
    counts = (long) liveTags.size();
    response.setTagCounts(counts);
    response.setLiveTags(liveTags);
    response.setTuneUrlCounts((long) liveTags.size());
    if (isDebugOn) {
      tagsHelper.displayLiveTags(signature, this.logger, liveTags);
    }
    this.logger.logExit(
        signature, new Object[] {"counts=", counts, "liveTags.size", liveTags.size()});

    return response;
  }

  public EvaluateAudioStreamResponse evaluateOneSecondAudioStream(
      long duration,
      Long dataOffset,
      short[] data,
      Long fingerprintRate,
      String dataFingerprint) {
    final String signature = "evaluateOneSecondAudioStream";
    final String signature2 = "evaluateOneSecondAudioStream:Pruning";
    EvaluateAudioStreamResponse response = new EvaluateAudioStreamResponse();
    List<TuneUrlTag> liveTags = new ArrayList<TuneUrlTag>();
    List<TuneUrlTag> tags;
    long elapse;
    long maxDuration = Converter.muldiv(1000, duration, 1L);
    long count, counts = Converter.muldiv(1000, duration, 100);
    long durationLimit = dataOffset + Converter.muldiv(1000, duration, 1L);
    FingerprintCompareResponse fcr;
    FingerprintResponse fr;

    String rootDir = getSaveAudioFilesFolder(null);
    String debugDir = String.format("%s/%s", rootDir, "debug");
    if (isDebugOn) {
      ProcessHelper.makeDir(debugDir);
    }
    Random random = new Random();
    random.setSeed(new Date().getTime());

    LinkedList<FingerprintThreadCollector> fingerprintThreadList =
        parallelFingerprintCollect(
            data,
            fingerprintRate,
            dataFingerprint,
            maxDuration,
            counts,
            rootDir,
            random);

    for (count = 0L, elapse = 0L; count < counts && elapse < maxDuration; count++, elapse += 100L) {
      if (fingerprintThreadList.isEmpty()) {
        break;
      }
      FingerprintCollection result =
          fingerprintThreadList.removeFirst().getFingerprintCollectionResult();

      List<FingerprintResponse> frSelection = result.getFrCollection();
      List<FingerprintCompareResponse> selection = result.getFcrCollection();
      fcr = null;
      fr = null;
      if (selection.size() == 5) {
        Object[] fingerprintComparisonsResponse =
            FingerprintUtility.fingerprintComparisons(selection, frSelection, fcr, fr);
        fcr = (FingerprintCompareResponse) fingerprintComparisonsResponse[0];
        fr = (FingerprintResponse) fingerprintComparisonsResponse[1];

        if (null != fcr) {
          TuneUrlTag tag = tagsHelper.newTag(true, dataOffset, null, fcr);
          /*
          if (isDebugOn) {
            FingerprintUtility.displayLiveTagsEx(signature, logger, tag);
          }
           */
          if (tag.getDataPosition() > durationLimit) break;
          liveTags.add(tag);
        }
      } // if (selection.size() == 5)
    } // for (count = 0L, ...)
    if (!liveTags.isEmpty()) {
      tags = tagsHelper.pruneTags(liveTags);
      if (isDebugOn) {
        logger.logExit(signature2, "before=", liveTags.size(), "after=", tags.size());
      }
      liveTags = new ArrayList<TuneUrlTag>();

      for (TuneUrlTag tag : tags) {

        tag = updatePayload(dataOffset, random, rootDir, fingerprintRate, tag, data, maxDuration);
        if (tag != null) {
          liveTags.add(tag);
          if (isDebugOn) {
            tagsHelper.displayLiveTagsEx(signature2, logger, tag);
          }
        }
      }
    }
    liveTags = tagsHelper.pruneTags(liveTags);
    counts = (long) liveTags.size();
    response.setTagCounts(counts);
    response.setLiveTags(liveTags);
    response.setTuneUrlCounts((long) liveTags.size());
    /*
    if (isDebugOn) {
      FingerprintUtility.displayLiveTags(signature, logger, liveTags);
    }
     */
    logger.logExit(
        signature,
        new Object[] {
          "counts=", counts, "liveTags.size", liveTags.size(), "durationLimit=", durationLimit
        });

    return response;
  }
  
  public FindFingerPrintResponse findFingerPrintsAudioStream(
    AudioDataEntry audioDataEntry,
    EvaluateAudioStreamEntry evaluateAudioStreamEntry,
    String signature) {
    // The Audio Stream URL.
    String url = CommonUtil.getString(audioDataEntry.getUrl(), Constants.AUDIOSTREAM_URL_SIZE);
    // The Data.
    short[] data = audioDataEntry.getData();
    // The Size of data.
    int size = audioDataEntry.getSize().intValue();
    // The Sample rate.
    Long sampleRate = audioDataEntry.getSampleRate();
    // The Duration.
    Long duration = audioDataEntry.getDuration();
    // The Fingerprint rate.
    Long fingerprintRate = audioDataEntry.getFingerprintRate();
    // The Triggersound Fingerprint Data.
    String dataFingerprint = evaluateAudioStreamEntry.getDataFingerprint();
    this.logger.logEntry(
          signature,
          new Object[] {
              "url=", url,
              "data=", data.length == size,
              "size=", size,
              "SRate=", sampleRate,
              "duration=", duration,
              "FRate=", fingerprintRate,
              "fingerprintData=", dataFingerprint,
            });
          
    Converter.checkAudioDataEntryDataSize(audioDataEntry);
    Converter.validateShortDataSize(data, size);

    final String fileName = Converter.validateUrlOrGencrc32(url);
    ProcessHelper.checkNullOrEmptyString(fileName, "AudioDataEntry.Url");

    FindFingerPrintResponse response = new FindFingerPrintResponse();
    response.setFingerPrintCount(0L);
    response.setFingerPrint(null);
    
    long elapse;
    long maxDuration = Converter.muldiv(1000, duration, 1L);
    long count, counts = Converter.muldiv(1000, maxDuration, 100);

    String rootDir = this.getSaveAudioFilesFolder(null);
    String debugUniqueName = ProcessHelper.createUniqueFilename();
    String debugDir = String.format("%s/%s", rootDir, "debug");
    String rootDebugDir = String.format("%s/%s", debugDir, debugUniqueName);

    FingerprintCompareResponse fcr = null;
    FingerprintResponse fr = null;
    if (isDebugOn) {
      ProcessHelper.makeDir(debugDir);
    }
    Random random = new Random();
    random.setSeed(new Date().getTime());

    LinkedList<FingerprintThreadCollector> fingerprintThreadList =
                parallelFingerprintCollect(
                    data,
                    fingerprintRate,
                    dataFingerprint,
                    // dataFingerprintBuffer,
                    // dataFingerprintBufferSize,
                    maxDuration,
                    counts,
                    rootDir,
                    random);
      
      // return new FindFingerPrintResponse();
    for (count = 0L, elapse = 0L; count < counts && elapse < maxDuration; count++, elapse += 100L) {
      if (fingerprintThreadList.isEmpty()) {
        break;
      }
      FingerprintCollection result =
          fingerprintThreadList.removeFirst().getFingerprintCollectionResult();

      List<FingerprintResponse> frSelection = result.getFrCollection();
      List<FingerprintCompareResponse> selection = result.getFcrCollection();

      // timeOffset = elapse; // possible legacy code
      if (selection.size() == 5) {
        // fingerPrints.addAll(selection);
        Object[] fingerprintComparisonsResponse =
            FingerprintUtility.fingerprintComparisons(selection, frSelection, fcr, fr);
        fcr = (FingerprintCompareResponse) fingerprintComparisonsResponse[0];
        fr = (FingerprintResponse) fingerprintComparisonsResponse[1];

        if (fcr != null && fcr.getOffset() < maxDuration - 1000) {
          response.setFingerPrintCount(1L);
          response.setFingerPrint(fcr);
          break;
        } // if (fcr != null)
      } // if (selection.size() == 5)
    } // for (...)
  
    return response;
}

  private void saveFingerprintsDebug(
      EvaluateAudioStreamEntry evaluateAudioStreamEntry,
      FingerprintResponse fr,
      FingerprintResponse audioFr,
      FingerprintCompareResponse fcr,
      Long baseOffset,
      String rootDebugDir,
      String debugUniqueName) {
    fingerprintExternals.saveAudioClipsAt(
        true,
        evaluateAudioStreamEntry,
        fr,
        fcr,
        "trigger",
        baseOffset,
        1000L,
        rootDebugDir,
        debugUniqueName);

    fingerprintExternals.saveAudioClipsAt(
        false,
        evaluateAudioStreamEntry,
        audioFr,
        fcr,
        "audio",
        baseOffset + 1000L,
        5000L,
        rootDebugDir,
        debugUniqueName);
  }

  /**
   * Append filename with Audio Stream URL.
   *
   * @param fileName String The url file name
   * @return String
   */
  public String getStreamAudioUrlPrefix(final String fileName) {
    if (Helper.isStringNullOrEmpty(fileName)) return saveAudioFiles;
    return String.format("%s/%s", streamAudioUrlPrefix, fileName);
  }

  /**
   * Append filename with Audio Stream folder.
   *
   * @param subDir String filename
   * @return String
   */
  public String getSaveAudioFilesFolder(final String subDir) {
    if (Helper.isStringNullOrEmpty(subDir)) return saveAudioFiles;
    return String.format("%s/%s", saveAudioFiles, subDir);
  }

  /**
   * Update status value.
   *
   * @param forceUpdate boolean
   * @param response AudioStreamDataResponse
   * @param status Integer
   * @param asDB AudioStreamDatabase
   */
  private void updateStatus(
      boolean forceUpdate,
      AudioStreamDataResponse response,
      Integer status,
      AudioStreamDatabase asDB) {
    LocalDateTime localDate;
    if (forceUpdate || (asDB != null && !status.equals(asDB.getAsStatus()))) {
      asDB.setAsStatus(status);
      localDate = CommonUtil.asLocalDateTime(new Date());
      asDB.setAsModified(localDate);
      asDB = audioStreamDatabaseService.saveAudioStreamDatabase(asDB, this.logger);
    }
    response.setStatus(status);
    if (asDB != null) {
      response.setConversionId(asDB.getAsId());
    }
  }

  /**
   * Convert AudioStreamDatabase to AudioStreamDataResponse.
   *
   * @param signature String
   * @param response AudioStreamDataResponse
   * @param asDB AudioStreamDatabase
   * @return AudioStreamDataResponse
   */
  private AudioStreamDataResponse convertAudioStreamDatabase(
      final String signature, AudioStreamDataResponse response, AudioStreamDatabase asDB) {

    Long duration = asDB.getAsDuration();
    final String fiveSecondAudioUrl = asDB.getAsFilename() + ".wav";
    final String finalAudioStreamUrl = asDB.getAsFilename() + duration.toString() + ".wav";

    response.setFiveSecondAudioUrl(getStreamAudioUrlPrefix(fiveSecondAudioUrl));
    response.setFinalAudioStreamUrl(getStreamAudioUrlPrefix(finalAudioStreamUrl));

    response.setConversionId(asDB.getAsId());
    response.setDuration(duration);

    response.setStatus(asDB.getAsStatus());
    this.logger.logExit(
        signature, new Object[] {response}); // TODO - Check if it is necessary to kill log
    return response;
  }

  /**
   * Set default values for AudioStreamDataResponse members.
   *
   * @param signature String
   * @param response AudioStreamDataResponse
   * @param crc32 String
   * @return AudioStreamDataResponse
   */
  private AudioStreamDataResponse resetResponseValue(
      final String signature, AudioStreamDataResponse response, final String crc32) {
    final String url = getStreamAudioUrlPrefix(crc32 + ".wav");
    response.setFiveSecondAudioUrl(url);
    response.setFinalAudioStreamUrl(url);

    response.setConversionId(0L);
    response.setDuration(0L);

    response.setStatus(Constants.AUDIOSTREAM_STATUS_FINAL);
    this.logger.logExit(
        signature, new Object[] {response}); // TODO - Check if it is necessary to kill log
    return response;
  }

  private String updateWaveFileExtension(final String fileName) {
    if (fileName.endsWith(".wav")) {
      return fileName;
    }
    return fileName + ".wav";
  }

  private AudioStreamDatabase updateAsDB(String crc32, String url, Long duration, Integer status) {
    // 11. Save conversion into audio_stream_data table.
    AudioStreamDatabase asDB = audioStreamDatabaseService.createAudioStreamDatabase();
    // 12. Set the unique filename based from the CRC32 of the given URL.
    asDB.setAsFilename(crc32);
    asDB.setAsUrl(url);
    // asDB.setAsDuration(response.getDuration());
    asDB.setAsDuration(duration);
    // 13. Status becomes Constants.AUDIOSTREAM_STATUS_FINAL if audio stream conversion is
    // completed.
    asDB.setAsStatus(status);
    LocalDateTime localDate = CommonUtil.asLocalDateTime(new Date());
    asDB.setAsCreated(localDate);
    asDB.setAsModified(localDate);

    return asDB;
  }

  /**
   * Run WebRTC Script.
   *
   * @param signature String
   * @param isExecute boolean
   * @param command String
   * @param url String
   * @param duration Long
   * @return AudioStreamDataResponse
   */
  public AudioStreamDataResponse runWebRtcScript(
      final String signature,
      boolean isExecute,
      final String command,
      final String url,
      final Long duration,
      AudioStreamDatabase pDB)
      throws BaseServiceException {
    AudioStreamDatabase asDB = null;
    AudioStreamDataResponse response = new AudioStreamDataResponse();
    Map<String, String> results = null;
    MessageLogger logger = this.logger;
    // 1. Create a unique temporary filename.
    String uniqueName = ProcessHelper.createUniqueFilename();
    String outputFilename = String.format("/tmp/%s.txt", uniqueName);
    // 2.a Make sure we will create a unique file name.
    if (ProcessHelper.isFileExist(outputFilename)) {
      uniqueName = ProcessHelper.createUniqueFilename();
      outputFilename = String.format("/tmp/%s.txt", uniqueName);
    }
    String executionMode = isExecute ? "TRUE" : "FALSE";
    final String crc32Data = url + '-' + duration.toString();
    String crc32 = ProcessHelper.genCrc32(crc32Data);
    Integer status;
    logger.logEntry(
        signature,
        new Object[] {
          "EXEC=", executionMode,
          "FNAME=", uniqueName,
          "CMD=", command,
          "URL=", url,
          "TIME=", duration,
          "CRC32=", crc32
        });

    // 2.b if isExecute is false, don't check the DB but check the status of wget
    // 2.c If this crc32 already in DB, return immediately.
    if (pDB != null) {
      asDB = pDB;
    } else {
      try {
        asDB = audioStreamDatabaseService.getAudioStreamDatabaseByNameByDuration(crc32, duration);
      } catch (BaseServiceException ignore) {
        asDB = null;
      }
    }

    // 2.d check if it is time to kill wget.
    if (null != asDB) {
      status = Constants.AUDIOSTREAM_STATUS_FINAL;
      if (status.equals(asDB.getAsStatus())) {
        return convertAudioStreamDatabase(signature, response, asDB);
      }
      if (ProcessHelper.isConversionExpired(asDB.getAsCreated(), duration)) {
        executionMode = "KILL";
      }
    }

    // 3. get path where to save the audio files
    String rootDir = getSaveAudioFilesFolder(null);

    // 4. Execute the script that uses sleep, wget, and ffmpeg to convert the audio stream into a
    // 10240 .wav file.
    // 5. If the audio file exist, it will return immediately
    // 6. If the audio does not exist, the script will download at least 5 seconds of the audio
    // 7. create the outputFilename and write configuration on it
    // 8. crc32 value is the value saved into DB table audio_stream_data.as_filename
    ProcessBuilder processBuilder =
        new ProcessBuilder(
            "/bin/bash",
            command,
            uniqueName,
            outputFilename,
            crc32,
            rootDir,
            url,
            duration.toString(),
            executionMode);
    processBuilder.redirectErrorStream(true);
    processBuilder.directory(new File(rootDir));
    Process process;
    try {
      process = processBuilder.start();
      process.waitFor();
    } catch (IOException | InterruptedException ex) {
      // 9. On error, return connection ID as zero.
      logger.logExit(signature, "processBuilder.start() Failed: " + ex.getMessage());
      // ex.printStackTrace(); // TODO - Avoid using stacktraces
      ProcessHelper.deleteFile(outputFilename);
      return resetResponseValue(signature, response, crc32);
    }
    // 10. Extract audio stream conversion status after running run_webrtc_script.sh
    response.setConversionId(0L);
    response.setDuration(duration);
    status = Constants.AUDIOSTREAM_STATUS_INIT;
    results = ProcessHelper.readTextFileAsArray(outputFilename, true);
    String error = "";
    for (String key : results.keySet()) {
      String value = results.get(key);
      switch (key) {
        case "FILENAME":
          // Check if run_webrtc_script.sh set up the correct Filename.
          if (!crc32.equals(value)) {
            if (!error.isEmpty()) {
              error += "\nInvalid Filename value";
            } else {
              error += "Invalid Filename value";
            }
          }
          break;
        case "fiveSecondAudioUrl":
          response.setFiveSecondAudioUrl(updateWaveFileExtension(getStreamAudioUrlPrefix(value)));
          break;
        case "finalAudioStreamUrl":
          response.setFinalAudioStreamUrl(updateWaveFileExtension(getStreamAudioUrlPrefix(value)));
          break;
        case "duration":
          response.setDuration(ProcessHelper.parseLong(value, duration));
          break;
        case "status":
          status = ProcessHelper.parseInt(value, Constants.AUDIOSTREAM_STATUS_INIT);
          break;
        case "ERROR":
          if (!"NONE".equals(value)) {
            if (!error.isEmpty()) error += "\n";
            error += value;
          }
          break;
      }
    }

    if (!error.isEmpty()) {
      CommonUtil.BadRequestException(error); // Do not reach further
    }
    if (executionMode.equals("KILL")) {
      status = Constants.AUDIOSTREAM_STATUS_FINAL;
      updateStatus(true, response, status, asDB);
    } else if (isExecute) {
      if (asDB == null) {
        // 11. Save conversion into audio_stream_data table.
        // 12. Set the unique filename based from the CRC32 of the given URL.
        // 13. Status becomes Constants.AUDIOSTREAM_STATUS_FINAL if audio stream conversion is
        asDB = updateAsDB(crc32, url, duration, status);
        // 14. Commit the new audio stream conversion
        updateStatus(true, response, status, asDB);
      } else {
        updateStatus(false, response, status, asDB);
      }
    } else {
      updateStatus(false, response, status, asDB);
    }

    logger.logExit(signature, new Object[] {response.toString()});
    return response;
  }

  /**
   * Helper to update the audio segment after the trigger sound location.
   *
   * @param dataOffset long
   * @param random Random
   * @param rootDir String
   * @param fingerprintRate Long
   * @param tag TunerUrlTag
   * @param data Array of short
   * @param maxDuration Long
   * @return TuneUrlTag or null
   */
  private TuneUrlTag updatePayload(
      long dataOffset,
      Random random,
      final String rootDir,
      long fingerprintRate,
      TuneUrlTag tag,
      final short[] data,
      long maxDuration) {
    long tagOffset = tag.getDataPosition() + 1000L; // 2880 + 1000 := 3880
    long endOffset = tagOffset + 5000L; // 3880 + 5000 := 8880
    long duration = dataOffset + maxDuration; //    0 + 10000
    long iStart, iEnd;
    short[] dData;
    int dSize;
    FingerprintResponseNew fr;
    if (endOffset < duration) {
      tagOffset = tagOffset - dataOffset; // 3880 - 0
      endOffset = endOffset - dataOffset; // 8880 - 0
      iStart =
          Converter.muldiv(tagOffset, fingerprintRate, 1000L); // 3380 x 11025 / 1000 := 37264.5
      iEnd = Converter.muldiv(endOffset, fingerprintRate, 1000L); // 8880 x 11025 / 1000 := 97902
      dSize = (int) (iEnd - iStart);
      if (dSize < data.length) { // 10 seconds x 11025 := 110250
        dData = Converter.convertListShortEx(data, (int) iStart, dSize);
        if (dData != null) {
          fr = fingerprintExternals.runExternalFingerprinting(random, rootDir, dData, dData.length);

          final String payload = fr.toJson();
          tag.setDescription(payload);
          return tag;
        }
      }
    }
    return null;
  }

  public LinkedList<FingerprintThreadCollector> parallelFingerprintCollect(
      short[] data,
      Long fingerprintRate,
      String dataFingerprint,
      long maxDuration,
      long counts,
      String rootDir,
      Random random) {
    long count;
    long elapse;
    LinkedList<FingerprintThreadCollector> fingerprintThreadList =
        new LinkedList<FingerprintThreadCollector>();
    List<Thread> threadList = new LinkedList<Thread>();
    for (count = 0L, elapse = 0L; count < counts && elapse < maxDuration; count++, elapse += 100L) {
      if (count == 50L) {
        break;
      }
      FingerprintThreadCollector fingerprintThread =
          new FingerprintThreadCollector(
              rootDir,
              data,
              elapse,
              random,
              fingerprintRate,
              dataFingerprint);
      fingerprintThreadList.add(fingerprintThread);

      Thread t = new Thread(fingerprintThread);
      t.start();
      threadList.add(t);
    }

    for (Thread thread : threadList) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    return fingerprintThreadList;
  }

  @Override
  public LinkedList<FingerprintThreadCollector> parallelFingerprintCollect(short[] data, Long fingerprintRate,
      StringBuffer dataFingerprintBuffer, int dataFingerprintBufferSize, long maxDuration, long counts, String rootDir,
      Random random) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'parallelFingerprintCollect'");
  }
}
