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

package com.tuneurl.webrtc.util.controller;

import com.tuneurl.webrtc.util.controller.dto.*;
import com.tuneurl.webrtc.util.exception.BaseServiceException;
import com.tuneurl.webrtc.util.model.*;
import com.tuneurl.webrtc.util.service.*;
import com.tuneurl.webrtc.util.util.CommonUtil;
import com.tuneurl.webrtc.util.util.Converter;
import com.tuneurl.webrtc.util.util.FingerprintUtility;
import com.tuneurl.webrtc.util.util.MessageLogger;
import com.tuneurl.webrtc.util.util.ProcessHelper;
import com.tuneurl.webrtc.util.value.Constants;
import com.tuneurl.webrtc.util.value.UserType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The AudioStreamController class. import javax.servlet.ServletContext.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
@RestController
@RequestMapping("/")
public class AudioStreamController extends BaseController {

  @Autowired protected AudioStreamDatabaseService audioStreamService;
  @Autowired protected AudioStreamTrainingChannelService audioChannelService;

  /** Default constructor. */
  public AudioStreamController() {
    super();
  }

  /**
   * Convert AudioStreamDatabase to AudioStreamDataResponse.
   *
   * @param signature String
   * @param logger MessageLogger
   * @param response AudioStreamDataResponse
   * @param asDB AudioStreamDatabase
   * @return AudioStreamDataResponse
   */
  private AudioStreamDataResponse convertAudioStreamDatabase(
      final String signature,
      MessageLogger logger,
      AudioStreamDataResponse response,
      AudioStreamDatabase asDB) {

    Long duration = asDB.getAsDuration();
    final String fiveSecondAudioUrl = asDB.getAsFilename() + ".wav";
    final String finalAudioStreamUrl = asDB.getAsFilename() + duration.toString() + ".wav";

    response.setFiveSecondAudioUrl(super.getStreamAudioUrlPrefix(fiveSecondAudioUrl));
    response.setFinalAudioStreamUrl(super.getStreamAudioUrlPrefix(finalAudioStreamUrl));

    response.setConversionId(asDB.getAsId());
    response.setDuration(duration);

    response.setStatus(asDB.getAsStatus());
    logger.logExit(signature, new Object[] {response});
    return response;
  }

  /**
   * Set default values for AudioStreamDataResponse members.
   *
   * @param signature String
   * @param logger MessageLogger
   * @param response AudioStreamDataResponse
   * @param crc32 String
   * @return AudioStreamDataResponse
   */
  private AudioStreamDataResponse resetResponseValue(
      final String signature,
      MessageLogger logger,
      AudioStreamDataResponse response,
      final String crc32) {
    final String url = super.getStreamAudioUrlPrefix(crc32 + ".wav");
    response.setFiveSecondAudioUrl(url);
    response.setFinalAudioStreamUrl(url);

    response.setConversionId(0L);
    response.setDuration(0L);

    response.setStatus(Constants.AUDIOSTREAM_STATUS_FINAL);
    logger.logExit(signature, new Object[] {response});
    return response;
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
    MessageLogger logger = super.getMessageLogger();
    if (forceUpdate) {
      asDB.setAsStatus(status);
      localDate = CommonUtil.asLocalDateTime(new Date());
      asDB.setAsModified(localDate);
      asDB = audioStreamService.saveAudioStreamDatabase(asDB, logger);

    } else if (asDB != null) {
      if (!status.equals(asDB.getAsStatus())) {
        asDB.setAsStatus(status);
        localDate = CommonUtil.asLocalDateTime(new Date());
        asDB.setAsModified(localDate);
        asDB = audioStreamService.saveAudioStreamDatabase(asDB, logger);
      }
    }
    response.setStatus(status);
    if (asDB != null) {
      response.setConversionId(asDB.getAsId());
    }
  }

  private String updateWaveFileExtension(final String fileName) {
    if (fileName.endsWith(".wav")) {
      return fileName;
    }
    return fileName + ".wav";
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
  private AudioStreamDataResponse runWebRtcScript(
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
    MessageLogger logger = super.getMessageLogger();
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
        asDB = audioStreamService.getAudioStreamDatabaseByNameByDuration(crc32, duration);
      } catch (BaseServiceException ignore) {
        asDB = null;
      }
    }

    // 2.d check if it is time to kill wget.
    if (null != asDB) {
      status = Constants.AUDIOSTREAM_STATUS_FINAL;
      if (status.equals(asDB.getAsStatus())) {
        return convertAudioStreamDatabase(signature, logger, response, asDB);
      }
      if (ProcessHelper.isConversionExpired(asDB.getAsCreated(), duration)) {
        executionMode = "KILL";
      }
    }

    // 3. get path where to save the audio files
    String rootDir = super.getSaveAudioFilesFolder(null);

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
      ex.printStackTrace();
      ProcessHelper.deleteFile(outputFilename);
      return resetResponseValue(signature, logger, response, crc32);
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
          // Check if run_webrtc_script.sh setup the correct Filename.
          if (!crc32.equals(value)) {
            if (error.length() > 0) error += "\n";
            error += "Invalid Filename value";
          }
          break;
        case "fiveSecondAudioUrl":
          response.setFiveSecondAudioUrl(
              updateWaveFileExtension(super.getStreamAudioUrlPrefix(value)));
          break;
        case "finalAudioStreamUrl":
          response.setFinalAudioStreamUrl(
              updateWaveFileExtension(super.getStreamAudioUrlPrefix(value)));
          break;
        case "duration":
          response.setDuration(ProcessHelper.parseLong(value, duration));
          break;
        case "status":
          status = ProcessHelper.parseInt(value, Constants.AUDIOSTREAM_STATUS_INIT);
          break;
        case "ERROR":
          if (!"NONE".equals(value)) {
            if (error.length() > 0) error += "\n";
            error += value;
          }
          break;
      }
    }

    if (error.length() > 0) {
      CommonUtil.BadRequestException(error);
      /** NOTREACH */
    }
    if (executionMode.equals("KILL")) {
      status = Constants.AUDIOSTREAM_STATUS_FINAL;
      updateStatus(true, response, status, asDB);
    } else if (isExecute) {
      if (asDB == null) {
        // 11. Save conversion into audio_stream_data table.
        asDB = audioStreamService.createAudioStreamDatabase();
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
   * Access the Wave file.
   *
   * <p>If x-audio-return header exist and it's value is <code>octet</code>,
   *
   * <p>then return as MediaType.APPLICATION_OCTET_STREAM_VALUE,
   *
   * <p>otherwise return type as "audio/wav".
   *
   * <p>
   *
   * @param sWaveFile String Wave filename
   * @param httpRequest HttpServletRequest
   * @param httpResponse HttpServletResponse
   */
  @RequestMapping(value = "/dev/v3/audio/{WaveFile}", method = RequestMethod.GET)
  @CrossOrigin("*")
  public void getWaveFileAsByteArray(
      @PathVariable(value = "WaveFile", required = true) String sWaveFile,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {
    final String signature = "getWaveFileAsByteArray";
    super.saveAnalytics(signature, httpRequest);
    MessageLogger logger = super.getMessageLogger();
    final String xAudioReturn = httpRequest.getHeader("x-audio-return");
    logger.logEntry(
        signature,
        new Object[] {
          "wave=", sWaveFile,
          "x-audio-return=", xAudioReturn
        });

    // 1. Check for valid user. Caller must be previously login. Error 401, 403, 500
    if (!super.canAccessAudioWithoutLogin()) {
      super.getSdkClientCredentials(signature, UserType.LOGIN_FOR_USER, httpRequest, httpResponse);
    }
    // 2. Filename is in CRC32 pattern + duration, E.g. aabbccddXXXX.wav
    final String pWaveFile =
        ProcessHelper.getStringEx(sWaveFile, Constants.AUDIOSTEAM_MAXIMUM_FILE_SIZE, "WaveFile");
    int offset = pWaveFile.indexOf(".");
    if (offset < Constants.AUDIOSTEAM_FILENAME_MIN_DOTPOS
        || offset > Constants.AUDIOSTEAM_FILENAME_MAX_DOTPOS) {
      CommonUtil.NotFoundException("Invalid file '" + pWaveFile + "'");
      /*NOTREACH*/
    }
    final String wavePath = super.getSaveAudioFilesFolder(pWaveFile);
    FileInputStream fileInput = null;
    try {
      fileInput = new FileInputStream(wavePath);
    } catch (FileNotFoundException ex) {
      // ex.printStackTrace();
      CommonUtil.NotFoundException("Missing '" + pWaveFile + "'");
      /*NOTREACH*/
    }
    if (xAudioReturn != null && xAudioReturn.equalsIgnoreCase("octet")) {
      httpResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    } else {
      httpResponse.setContentType("audio/wav");
    }
    try {
      IOUtils.copy(fileInput, httpResponse.getOutputStream());
    } catch (Exception ignore) {
      logger.logExit(
          signature, new Object[] {"Retry: " + pWaveFile + " is not available right now."});
    }
  }

  /**
   * Save Audio Stream. <br>
   * <br>
   * <b>Implementation Notes</b>: <br>
   * <b>A. Input is <code>AudioStreamEntry</code>.</b>
   *
   * <ul>
   *   <li><code>AudioStreamEntry.Url</code>: the Audio Stream URL to save.
   *   <li><code>AudioStreamEntry.duration</code>: the total audio stream to save in seconds.
   *   <li><code>AudioStreamEntry.sampleRate</code>: the audio sample rate as per fingerprinting
   *       requirement. This currently ignored and always set to 10240.
   * </ul>
   *
   * <br>
   * <b>B. How the Save Audio Stream works.</b>
   *
   * <ul>
   *   <li>The Url and duration is concatenated and is converted into crc32 String to be use as
   *       unique filename.
   *   <li>If the filename already exist, this end-point return immediately,
   *   <li>otherwise it will get 5 seconds of the audio stream and pre-process it.
   *   <li>The audio stream is downloaded by wget until the duration expires.
   *   <li>The downloaded audio is then ffmpeg converted into audio PCM 16 with 10240 sample rate.
   *   <li>The PCM audio is then converted into fingerprint
   *   <li>The trigger audio is then search on the PCM audio fingerprint
   *   <li>The conversion information can then be accessible by /dev/v2/getAudioStreamInfo.
   * </ul>
   *
   * <br>
   * <b>C. Output is AudioStreamResponse</b>
   *
   * <ul>
   *   <li><code>AudioStreamResponse.conversionId</code>: Audio stream conversion ID
   *   <li><code>AudioStreamResponse.fiveSecondAudio</code>: URL for a five seconds audio
   *   <li><code>AudioStreamResponse.finalAudioStream</code>: URL for a 600+ seconds audio
   *   <li><code>AudioStreamResponse.duration</code>: the actual audio stream duration in seconds.
   *   <li>Noted that: the value of <code>AudioStreamResponse.finalAudioStream</code> is the same
   *       with <code>AudioStreamResponse.fiveSecondAudio</code>,
   *   <li>until the conversion is completed
   * </ul>
   *
   * @param audioStreamEntry AudioStreamEntry,
   * @param httpRequest HttpServletRequest HTTP Request
   * @param httpResponse HttpServletResponse HTTP Response
   * @return ResponseEntity &lt;AudioStreamDataResponse>
   */
  @PostMapping(
      path = "/dev/v3/saveAudioStream",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  @ApiOperation(value = "Save Audio Stream", response = AudioStreamDataResponse.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "AudioStreamDataResponse"),
        @ApiResponse(code = 400, message = "BadRequest"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "NotFound"),
        @ApiResponse(code = 500, message = "InternalServerError"),
      })
  @CrossOrigin("*")
  public ResponseEntity<AudioStreamDataResponse> saveAudioStream(
      @Valid @RequestBody AudioStreamEntry audioStreamEntry,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {
    final String signature = "saveAudioStream";
    super.saveAnalytics(signature, httpRequest);
    MessageLogger logger = super.getMessageLogger();

    logger.logEntry(signature, new Object[] {audioStreamEntry});

    // 1. Check for ADMIN or USER role.
    if (!super.canAccessAudioWithoutLogin()) {
      super.getSdkClientCredentials(signature, UserType.LOGIN_FOR_USER, httpRequest, httpResponse);
    }

    // 2. URL can't be null or empty! Error 400
    String url =
        ProcessHelper.getStringEx(
            audioStreamEntry.getUrl(), Constants.AUDIOSTREAM_URL_SIZE, "AudioStreamEntry.Url");

    // 3. Make sure duration in 10 to 800 seconds range, inclusive.
    Long duration = audioStreamEntry.getDuration();
    if (null == duration) {
      duration = Constants.AUDIOSTEAM_MIN_DURATION;
    } else if (duration.longValue() < Constants.AUDIOSTEAM_MIN_DURATION) {
      duration = Constants.AUDIOSTEAM_MIN_DURATION;
    } else if (duration.longValue() > Constants.AUDIOSTEAM_MAX_DURATION) {
      duration = Constants.AUDIOSTEAM_MAX_DURATION;
    }

    /* 4. Assemble path where the bash shell reside: /home/ubuntu/audio/run_webrtc_script.sh */
    final String command = super.getSaveAudioFilesFolder(Constants.RUN_WEBRTC_SCRIPT);

    // 5. Run the script.
    AudioStreamDataResponse response =
        runWebRtcScript(signature, true, command, url, duration, null);

    // 6. Return the result
    return ResponseEntity.ok().body(response);
  }

  /**
   * Get Status of previously save Audio Stream. <br>
   * <br>
   * <b>Implementation Notes</b>: <br>
   * <b>A. Input is <code>conversionId</code>.</b>
   *
   * <ul>
   *   <li>The <code>conversionId</code> is the conversation ID returned from calling POST
   *       /dev/v2/saveAudioStream.
   * </ul>
   *
   * <br>
   * <b>B. How the get Audio Stream Data works.</b>
   *
   * <ul>
   *   <li>It will check for the status of previous conversion.
   *   <li>The <code>AudioStreamResponse.duration</code> will be updated with home much time in
   *       second had pass since the last call.
   *   <li>The maximum value of <code>AudioStreamResponse.duration</code> will be 1 more than the
   *       requested duration.
   *   <li>If the value of <code>AudioStreamResponse.duration</code> is greater than the requested
   *       duration, the conversion is completed.
   *   <li>If the conversion is completed, the <code>AudioStreamResponse.finalAudioStream</code> is
   *       fully created,
   *   <li>otherwise, caller must still use the value of <code>AudioStreamResponse.fiveSecondAudio
   *       </code>.
   * </ul>
   *
   * <br>
   * <b>C. Output is AudioStreamResponse</b>
   *
   * <ul>
   *   <li><code>AudioStreamResponse.conversionId</code>: Audio stream conversion ID
   *   <li><code>AudioStreamResponse.fiveSecondAudio</code>: URL for a five seconds audio
   *   <li><code>AudioStreamResponse.finalAudioStream</code>: URL for a 600+ seconds audio
   *   <li><code>AudioStreamResponse.duration</code>: the actual audio stream duration in seconds.
   *   <li>Noted that: the value of <code>AudioStreamResponse.finalAudioStream</code> is the same
   *       with <code>AudioStreamResponse.fiveSecondAudio</code>,
   *   <li>until the conversion is completed
   * </ul>
   *
   * @param pConversionid Long the The conversion ID created during previous saveAudioStream call
   * @param httpRequest HttpServletRequest HTTP Request
   * @param httpResponse HttpServletResponse HTTP Response
   * @return ResponseEntity &lt;AudioStreamDataResponse>
   */
  @GetMapping(
      path = "/dev/v3/getAudioStream",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  @ApiOperation(
      value = "Get Status of previously save Audio Stream",
      response = AudioStreamDataResponse.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "AudioStreamDataResponse"),
        @ApiResponse(code = 400, message = "BadRequest"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "NotFound"),
        @ApiResponse(code = 500, message = "InternalServerError"),
      })
  @CrossOrigin("*")
  public ResponseEntity<AudioStreamDataResponse> getAudioStreamData(
      @RequestParam(value = "conversionId", required = true) Long pConversionid,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {
    final String signature = "getAudioStreamData";
    super.saveAnalytics(signature, httpRequest);
    MessageLogger logger = super.getMessageLogger();

    logger.logEntry(signature, new Object[] {"conversionId=", pConversionid});

    // 1. Check for ADMIN or USER role.
    if (!super.canAccessAudioWithoutLogin()) {
      super.getSdkClientCredentials(signature, UserType.LOGIN_FOR_USER, httpRequest, httpResponse);
    }

    // 2. Conversion ID my be valid. Error 400
    if (null == pConversionid || pConversionid.longValue() < Constants.AUDIOSTREAM_MIN_ID) {
      CommonUtil.BadRequestException("Invalid Audio Stream conversionId '" + pConversionid + "'.");
      /** NOTREACH */
    }

    // 3. Conversion ID must exist on database. Error 404, 500.
    AudioStreamDatabase asdb = audioStreamService.getAudioStreamDatabaseById(pConversionid);

    /* 4. Assemble path where the bash shell reside: /home/ubuntu/audio/run_webrtc_script.sh */
    final String command = super.getSaveAudioFilesFolder(Constants.RUN_WEBRTC_SCRIPT);

    // 5. Run the script.
    AudioStreamDataResponse response =
        runWebRtcScript(signature, false, command, asdb.getAsUrl(), asdb.getAsDuration(), asdb);
    response.setConversionId(asdb.getAsId());

    return ResponseEntity.ok().body(response);
  }

  /**
   * Associate Audio Stream with action. <br>
   * <br>
   * <b>Implementation Notes</b>: <br>
   * <b>A. Input is <code>AudioStreamTrainingEntry</code>.</b>
   *
   * <ul>
   *   <li><code>AudioStreamTrainingEntry.channel</code>: the Channel ID
   *   <li><code>AudioStreamTrainingEntry.Url</code>: the Audio Stream URL to play
   *   <li><code>AudioStreamTrainingEntry.offset</code>: the starting location 1 second after
   *       trigger audio location.
   *   <li><code>AudioStreamTrainingEntry.duration</code>: the total audio stream to play in
   *       seconds.
   *   <li><code>AudioStreamTrainingEntry.category</code>: category type.
   *   <li><code>AudioStreamTrainingEntry.title</code>: the audio sample title.
   *   <li><code>AudioStreamTrainingEntry.popup</code>: the popp messages.
   * </ul>
   *
   * <br>
   * <b>B. How the train Audio Stream works.</b>
   *
   * <ul>
   *   <li>The given Audio stream is associated with category and its description is at the title
   *       field.
   *   <li>The location of the trigger audio plus 1 second is specified on the offset field
   *   <li>If the channel ID already exists, its content will be overwritten.
   * </ul>
   *
   * <br>
   * <b>C. Output is AudioStreamTrainingResponse</b>
   *
   * <ul>
   *   <li><code>ItemId.id</code>: Audio stream training ID
   * </ul>
   *
   * @param audioStreamTrainingEntry AudioStreamTrainingEntry,
   * @param httpRequest HttpServletRequest HTTP Request
   * @param httpResponse HttpServletResponse HTTP Response
   * @return ResponseEntity &lt;ItemId>
   */
  @PostMapping(
      path = "/dev/v3/trainAudioStream",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  @ApiOperation(value = "Associate Audio Stream with action", response = ItemId.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "ItemId"),
        @ApiResponse(code = 400, message = "BadRequest"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "NotFound"),
        @ApiResponse(code = 500, message = "InternalServerError"),
      })
  @CrossOrigin("*")
  public ResponseEntity<ItemId> trainAudioStream(
      @Valid @RequestBody AudioStreamTrainingEntry audioStreamTrainingEntry,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {
    final String signature = "trainAudioStream";
    super.saveAnalytics(signature, httpRequest);
    MessageLogger logger = super.getMessageLogger();
    // 1. Check inputs
    String category = null;
    String title = null;
    String url = null;
    String popup = null;
    Long channelId = audioStreamTrainingEntry.getChannel();
    try {
      category =
          ProcessHelper.getStringEx(
              audioStreamTrainingEntry.getCategory(),
              Constants.AUDIOSTREAM_CATEGORY_SIZE,
              "category");
      title =
          ProcessHelper.getStringEx(
              audioStreamTrainingEntry.getTitle(), Constants.AUDIOSTREAM_TITLE_SIZE, "title");
      url =
          ProcessHelper.getStringEx(
              audioStreamTrainingEntry.getUrl(), Constants.AUDIOSTREAM_URL_SIZE, "url");
      popup =
          ProcessHelper.getStringEx(
              audioStreamTrainingEntry.getPopup(), Constants.AUDIOSTREAM_POPUP_SIZE, "popup");
    } catch (IllegalArgumentException ex) {
      CommonUtil.BadRequestException(ex.getMessage());
      /*NOTREACH*/
    }
    logger.logEntry(
        signature,
        new Object[] {
          "channel=", channelId,
          "offset=", audioStreamTrainingEntry.getOffset(),
          "duration=", audioStreamTrainingEntry.getDuration(),
          "category=", category,
          "title=", title,
          "url=", url,
          "popup=", popup
        });

    // 2. Check for ADMIN or USER role.
    super.getSdkClientCredentials(signature, UserType.LOGIN_FOR_USER, httpRequest, httpResponse);

    // 3. Check channel ID
    if (channelId.longValue() < Constants.CHANNEL_MIN_COUNT
        || channelId.longValue() > Constants.CHANNEL_MAX_COUNT) {
      CommonUtil.BadRequestException("Invalid channel ID: " + channelId);
      /*NOTREACH*/
    }
    // 4. popup must be hEx encoded
    if (!popup.startsWith(Constants.HEX_PREFIX)) {
      CommonUtil.BadRequestException("Popup must be hEx encoded");
      /*NOTREACH*/
    }
    ItemId response = new ItemId();
    AudioStreamTrainingChannel audioChannel;
    try {
      audioChannel = audioChannelService.findChannelById(channelId);
    } catch (BaseServiceException ex) {
      // 4. Create the channel if not on DB yet.
      audioChannel = audioChannelService.createAudioStreamTrainingChannel();
      audioChannel.setAcChannel(channelId);
    }
    // 5. Update the channel
    audioChannel.setAcOffset(audioStreamTrainingEntry.getOffset());
    audioChannel.setAcDuration(audioStreamTrainingEntry.getDuration());
    audioChannel.setAcCategory(category);
    audioChannel.setAcTitle(title);
    audioChannel.setAcUrl(url);
    audioChannel.setAcPopup(popup);
    audioChannel.setAcStatus(1);
    audioChannel.setAcFilename(ProcessHelper.extractFilenameFromUrl(url));
    // 6. Save it
    audioChannel = audioChannelService.saveAudioStreamTrainingChannel(audioChannel, logger);
    response.setId(audioChannel.getAcId());
    return ResponseEntity.ok().body(response);
  }

  /**
   * Retrieves Channel data. <br>
   * <br>
   * <b>Implementation Notes</b>: <br>
   * <b>A. Input is <code>channelCounts</code>.</b>
   *
   * <ul>
   *   <li>The <code>channelCounts</code> specify the total number of Channel data to retrieve.
   *       Valid value from 1 to 12.
   * </ul>
   *
   * <br>
   * <b>B. Output is AudioStreamChannelDataResponse</b>
   *
   * <ul>
   *   <li><code>AudioStreamChannelDataResponse.channelId</code>: Channel ID
   *   <li><code>AudioStreamChannelDataResponse.offset</code>: start of audio to play
   *   <li><code>AudioStreamChannelDataResponse.duration</code>: the actual audio stream duration in
   *       seconds.
   * </ul>
   *
   * @param pChannelcounts Long the The channel count to retrieved. Valid is 1 to 12.
   * @param httpRequest HttpServletRequest HTTP Request
   * @param httpResponse HttpServletResponse HTTP Response
   * @return ResponseEntity &lt;AudioStreamChannelDataResponse>
   */
  @GetMapping(
      path = "/dev/v3/getAudioStreamChannel",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  @ApiOperation(value = "Retrieves Channel data", response = AudioStreamChannelDataResponse.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "AudioStreamChannelDataResponse"),
        @ApiResponse(code = 400, message = "BadRequest"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "NotFound"),
        @ApiResponse(code = 500, message = "InternalServerError"),
      })
  @CrossOrigin("*")
  public ResponseEntity<AudioStreamChannelDataResponse> getAudioStreamChannel(
      @RequestParam(value = "channelCounts", required = true) Long pChannelcounts,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {
    final String signature = "getAudioStreamChannel";
    super.saveAnalytics(signature, httpRequest);
    MessageLogger logger = super.getMessageLogger();

    logger.logEntry(signature, new Object[] {"channelCounts=", pChannelcounts});

    // 1. Check for ADMIN or USER role.
    super.getSdkClientCredentials(signature, UserType.LOGIN_FOR_USER, httpRequest, httpResponse);

    AudioStreamChannelDataResponse response = new AudioStreamChannelDataResponse();
    List<AudioStreamTrainingChannel> lists =
        audioChannelService.listActiveChannel(pChannelcounts, logger);
    response.setData(Converter.trainingChannelToInfo(lists));
    return ResponseEntity.ok().body(response);
  }

  /**
   * Convert the given data bits into fingerprint.. <br>
   * <br>
   * <b>Implementation Notes</b>: <br>
   * <b>A. Input is <code>AudioDataEntry</code>.</b>
   *
   * <ul>
   *   <li><code>AudioDataEntry.Url</code>: The Audio Stream URL.
   *   <li><code>AudioDataEntry.Data</code>: Audio data bits. Array of Byte.
   *   <li><code>AudioDataEntry.Size</code>: Size of Data.
   *   <li><code>AudioDataEntry.sampleRate</code>: Audio sampel rate. Normally 44100.
   *   <li><code>AudioDataEntry.duration</code>: Length of audio bits in seconds.
   *   <li><code>AudioDataEntry.fingerprintRate</code>: the Data is converted into this sample rate.
   *       Normall 11025.
   * </ul>
   *
   * <br>
   * <b>B. Output is FingerprintResponse</b>
   *
   * <ul>
   *   <li><code>FingerprintResponse.size</code>: size of Fingerprint.
   *   <li><code>FingerprintResponse.data</code>: the Fingeprint. Array of Byte.
   * </ul>
   *
   * @param audioDataEntry AudioDataEntry,
   * @param httpRequest HttpServletRequest HTTP Request
   * @param httpResponse HttpServletResponse HTTP Response
   * @return ResponseEntity &lt;FingerprintResponse>
   */
  @PostMapping(
      path = "/dev/v3/calculateFingerprint",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  @ApiOperation(
      value = "Convert the given data bits into fingerprint.",
      response = FingerprintResponse.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "FingerprintResponse"),
        @ApiResponse(code = 400, message = "BadRequest"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "NotFound"),
        @ApiResponse(code = 500, message = "InternalServerError"),
      })
  @CrossOrigin("*")
  public ResponseEntity<FingerprintResponse> calculateFingerprint(
      @Valid @RequestBody AudioDataEntry audioDataEntry,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {
    final String signature = "Controller:calculateFingerprint";
    super.saveAnalytics(signature, httpRequest);
    MessageLogger logger = super.getMessageLogger();
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
    // Root dir
    String rootDir = super.getSaveAudioFilesFolder(null);
    logger.logEntry(
        signature,
        new Object[] {
          "url=", url,
          "data=", data.length == size,
          "size=", size,
          "SRate=", sampleRate,
          "duration=", duration,
          "FRate=", fingerprintRate
        });
    // 1. Check for ADMIN or USER role.
    if (!super.canAccessAudioWithoutLogin()) {
      super.getSdkClientCredentials(signature, UserType.LOGIN_FOR_USER, httpRequest, httpResponse);
    }
    Converter.checkAudioDataEntryDataSize(audioDataEntry);
    Converter.validateShortDataSize(data, size);
    Converter.validateDuration(duration);
    final String fileName = Converter.validateUrlOrGencrc32(url);
    ProcessHelper.checkNullOrEmptyString(fileName, "AudioDataEntry.Url");
    // FingerprintResponse response = ExternalCppModules.calculateFingerprint(logger, data,
    // data.length);
    Random random = new Random();
    random.setSeed(new Date().getTime());
    FingerprintResponse response =
        FingerprintUtility.runExternalFingerprinting(random, logger, rootDir, data, data.length);
    return ResponseEntity.ok().body(response);
  }

  /**
   * Find all triggersound from the given audio stream. <br>
   * <br>
   * <b>Implementation Notes</b>: <br>
   * <b>A. Input is <code>EvaluateAudioStreamEntry</code>.</b>
   *
   * <ul>
   *   <li><code>EvaluateAudioStreamEntry.audioData</code>: The AudioDataEntry.
   *   <li><code>EvaluateAudioStreamEntry.sizeFingerprint</code>: Size of dataFingerprint.
   *   <li><code>EvaluateAudioStreamEntry.dataFingerprint</code>: Triggersound fingerprint. Array of
   *       Byte.
   * </ul>
   *
   * <br>
   * <b>B. Output is EvaluateAudioStreamEntry</b>
   *
   * <ul>
   *   <li><code>EvaluateAudioStreamResponse.tuneUrlCounts</code>: 0 to 64 total number TuneUrlTag
   *       found.
   *   <li><code>EvaluateAudioStreamResponse.tagCounts</code>: total number of TuneUrlTag.
   *   <li><code>EvaluateAudioStreamResponse.liveTags</code>: array of TuneUrlTag.
   * </ul>
   *
   * @param evaluateAudioStreamEntry EvaluateAudioStreamEntry,
   * @param httpRequest HttpServletRequest HTTP Request
   * @param httpResponse HttpServletResponse HTTP Response
   * @return ResponseEntity &lt;EvaluateAudioStreamResponse>
   */
  @PostMapping(
      path = "/dev/v3/evaluateAudioStream",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  @ApiOperation(
      value = "Find all triggersound from the given audio stream",
      response = EvaluateAudioStreamResponse.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "EvaluateAudioStreamResponse"),
        @ApiResponse(code = 400, message = "BadRequest"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "NotFound"),
        @ApiResponse(code = 500, message = "InternalServerError"),
      })
  @CrossOrigin("*")
  public ResponseEntity<EvaluateAudioStreamResponse> evaluateAudioStream(
      @Valid @RequestBody EvaluateAudioStreamEntry evaluateAudioStreamEntry,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {
    final String signature = "evaluateAudioStream";
    // final String signature2 = "evaluateAudioStream:inner";
    super.saveAnalytics(signature, httpRequest);
    MessageLogger logger = super.getMessageLogger();
    AudioDataEntry audioDataEntry = evaluateAudioStreamEntry.getAudioData();
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
    byte[] dataFingerprint = evaluateAudioStreamEntry.getDataFingerprint();
    // The size of Fingerprint Data.
    Long sizeFingerprint = evaluateAudioStreamEntry.getSizeFingerprint();
    logger.logEntry(
        signature,
        new Object[] {
          "url=", url,
          "data=", data.length == size,
          "size=", size,
          "SRate=", sampleRate,
          "duration=", duration,
          "FRate=", fingerprintRate,
          "fingerprintData=", dataFingerprint.length == sizeFingerprint,
          "sizeFingerprint=", sizeFingerprint
        });

    // 1. Check for ADMIN or USER role.
    if (!super.canAccessAudioWithoutLogin()) {
      super.getSdkClientCredentials(signature, UserType.LOGIN_FOR_USER, httpRequest, httpResponse);
    }
    Converter.checkAudioDataEntryDataSize(audioDataEntry);
    Converter.validateShortDataSize(data, size);
    Converter.validateDataSizeEx(dataFingerprint, sizeFingerprint.intValue());
    Converter.validateDurationEx(duration);
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
    FingerprintCompareResponse fcr = null;
    FingerprintCompareResponse fca;
    FingerprintCompareResponse fcb;
    FingerprintCompareResponse fcc;
    FingerprintCompareResponse fcd;
    FingerprintCompareResponse fce;
    TuneUrlTag tag;
    boolean isDebugOn = Constants.DEBUG_FINGERPRINTING;
    String rootDir = super.getSaveAudioFilesFolder(null);
    String debugUniqueName = ProcessHelper.createUniqueFilename();
    String debugDir = String.format("%s/%s", rootDir, "debug");
    String rootDebugDir = String.format("%s/%s", debugDir, debugUniqueName);
    FingerprintResponse fr = null;
    FingerprintResponse audioFr = null;
    if (isDebugOn) {
      ProcessHelper.makeDir(debugDir);
    }
    Random random = new Random();
    random.setSeed(new Date().getTime());
    for (count = 0L, elapse = 0L; count < counts && elapse < maxDuration; count++, elapse += 100L) {

      FingerprintCollection result =
          FingerprintUtility.collectFingerprint(
              logger,
              rootDir,
              data,
              elapse,
              random,
              fingerprintRate,
              dataFingerprint,
              Constants.FINGERPRINT_INCREMENT_DELTA);

      List<FingerprintResponse> frSelection = result.getFrCollection();
      List<FingerprintCompareResponse> selection = result.getFcrCollection();

      timeOffset = elapse;
      fcr = null;
      fr = null;
      if (selection.size() == 5) {
        fca = selection.get(0);
        fcb = selection.get(1);
        fcc = selection.get(2);
        fcd = selection.get(3);
        fce = selection.get(4);

        //  8: N P N N N => P is the valid TuneUrl trigger sound
        // 15: N P P P P => N is the valid TuneUrl trigger sound
        // 30: P P P P N => N is the valid TuneUrl trigger sound
        if (FingerprintUtility.hasNegativeFrameStartTimeEx(fca)
            && FingerprintUtility.hasPositiveFrameStartTimeEx(fcb)) {
          if (FingerprintUtility.hasNegativeFrameStartTimeEx(fcc)) {
            // N P N
            if (FingerprintUtility.isFrameStartTimeEqual(fca, fcc)
                && FingerprintUtility.isFrameStartTimeEqual(fcc, fcd)
                && FingerprintUtility.isFrameStartTimeEqual(fcd, fce)) {
              // N P N N N => P is the valid TuneUrl trigger sound
              fcr = selection.get(1);
              fr = frSelection.get(1);
            }
          } else if (FingerprintUtility.hasPositiveFrameStartTimeEx(fcc)
              && FingerprintUtility.isFrameStartTimeEqual(fcc, fcb)
              && FingerprintUtility.isFrameStartTimeEqual(fcc, fcd)
              && FingerprintUtility.isFrameStartTimeEqual(fcd, fce)) {
            // N P P P P => N is the valid TuneUrl trigger sound
            fcr = selection.get(0);
            fr = frSelection.get(0);
          }
        } else if (FingerprintUtility.hasPositiveFrameStartTimeEx(fca)
            && FingerprintUtility.hasNegativeFrameStartTimeEx(fce)) {
          // P . . . N
          if (FingerprintUtility.isFrameStartTimeEqual(fca, fcb)
              && FingerprintUtility.isFrameStartTimeEqual(fcb, fcc)
              && FingerprintUtility.isFrameStartTimeEqual(fcc, fcd)) {
            // P P P P N => N is the valid TuneUrl trigger sound
            fcr = selection.get(4);
            fr = frSelection.get(4);
          }
        }

        if (fcr != null) {

          // Grab the audio after the triggersound
          timeOffset = fcr.getOffset();
          baseOffset = timeOffset;

          timeOffset = timeOffset + 1000L;
          iStart = Converter.muldiv(timeOffset, fingerprintRate, 1000L);
          iEnd = Converter.muldiv(timeOffset + 5000L, fingerprintRate, 1000L);
          dSize = (int) (iEnd - iStart);
          dData = Converter.convertListShortEx(data, (int) iStart, dSize);
          if (dData == null) break;
          // Calculate the audio's fingerprint
          // audioFr = ExternalCppModules.calculateFingerprint(null, dData, dData.length);
          audioFr =
              FingerprintUtility.runExternalFingerprinting(
                  random, logger, rootDir, dData, dData.length);

          tag = FingerprintUtility.newTag(false, 0L, audioFr, fcr);
          liveTags.add(tag);

          if (isDebugOn) {
            FingerprintUtility.saveAudioClipsAt(
                logger,
                true,
                evaluateAudioStreamEntry,
                fr,
                fcr,
                "trigger",
                baseOffset,
                1000L,
                rootDebugDir,
                debugUniqueName);

            FingerprintUtility.saveAudioClipsAt(
                logger,
                false,
                evaluateAudioStreamEntry,
                audioFr,
                fcr,
                "audio",
                baseOffset + 1000L,
                5000L,
                rootDebugDir,
                debugUniqueName);
          } // if (isDebugOn)
        } // if (fcr != null)
      } // if (selection.size() == 5)
    } // for (...)
    liveTags = FingerprintUtility.pruneTags(liveTags);
    counts = (long) liveTags.size();
    response.setTagCounts(counts);
    response.setLiveTags(liveTags);
    response.setTuneUrlCounts((long) liveTags.size());
    if (isDebugOn) {
      FingerprintUtility.displayLiveTags(signature, logger, liveTags);
    }
    logger.logExit(signature, new Object[] {"counts=", counts, "liveTags.size", liveTags.size()});
    return ResponseEntity.ok().body(response);
  }
}
