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

package com.tuneurl.webrtc.util.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuneurl.webrtc.util.controller.dto.AudioDataEntry;
import com.tuneurl.webrtc.util.controller.dto.CompareStringResult;
import com.tuneurl.webrtc.util.controller.dto.EvaluateAudioStreamEntry;
import com.tuneurl.webrtc.util.controller.dto.FingerprintCollection;
import com.tuneurl.webrtc.util.controller.dto.FingerprintCompareResponse;
import com.tuneurl.webrtc.util.controller.dto.FingerprintEntry;
import com.tuneurl.webrtc.util.controller.dto.FingerprintResponse;
import com.tuneurl.webrtc.util.controller.dto.TuneUrlEntry;
import com.tuneurl.webrtc.util.controller.dto.TuneUrlTag;
import com.tuneurl.webrtc.util.exception.BaseServiceException;
import com.tuneurl.webrtc.util.value.Constants;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Fingerprint utility.
 *
 * <ul>
 *   <li>v1.1 added pruneTagsEx(MessageLogger, List&lt;TuneUrlTag>):List&lt;TuneUrlTag>
 * </ul>
 *
 * @author albonteddy@gmail.com
 * @version 1.1
 */
public final class FingerprintUtility {

  private static final String fingerprint_prefix =
      "{\"fingerprint\":{\"type\":\"Buffer\",\"data\":";

  private static final String fingerprint_suffix = "},\"fingerprint_version\":\"1\"}";

  private static final String fingerprint_header =
      "# Generated using: https://streamradiolocal.tmalbon.com/audio/audio/10240-audio-streams-0230000.mp3\n# crc32 /project/tuneurl-poc/tuneurl-poc/src/main/webapp/audio/10240-audio-streams-0230000.mp3\n# bbfbe45f    /project/tuneurl-poc/tuneurl-poc/src/main/webapp/audio/10240-audio-streams-0230000.mp3\n";

  /**
   * Helper method to fetch the TuneUrl for the given payload.
   *
   * @param index TuneUrl tag ID.
   * @param payload String
   * @param Url String
   * @return TuneUrlEntry or null.
   */
  public static TuneUrlEntry getTuneUrlEntry(
      MessageLogger logger, final long index, final String payload, final String Url) {
    // TODO - Should call the Search Matching API here
    // Not implemented as the Search Matching API was currently broken as of 04/29/2024
    return null;
  }

  /**
   * Helper method to selection the best FingerprintCompareResponse.
   *
   * @param selection Array of FingerprintCompareResponse
   * @return FingerprintCompareResponse or null.
   */
  public static final FingerprintCompareResponse findBestFingerprint(
      List<FingerprintCompareResponse> selection) {
    if (selection == null || selection.isEmpty()) {
      return null;
    }
    FingerprintCompareResponse fcr = selection.get(0);
    for (FingerprintCompareResponse fcx : selection) {
      if (CommonUtil.compareDouble(fcx.getScore(), fcr.getScore()) >= 0) {
        if (CommonUtil.compareDouble(fcx.getSimilarity(), fcr.getSimilarity()) >= 0) {
          fcr = fcx;
        }
      }
    }
    return fcr;
  }

  public static final String convertFingerprintToString(final byte[] data) {
    int offset;
    StringBuffer sb = new StringBuffer();
    sb.append(fingerprint_prefix).append("[");
    sb.append(ProcessHelper.byte2short(data[0]));
    for (offset = 1; offset < data.length; offset++) {
      sb.append(",");
      sb.append(ProcessHelper.byte2short(data[offset]));
    }
    sb.append("]").append(fingerprint_suffix);
    return sb.toString();
  }
  /**
   * Load the TuneUrl information for the given tag.
   *
   * @param data Array of byte
   * @param tag TunerUrlTag instance
   * @param index Long the index
   * @param fcr FingerprintCompareResponse instance
   * @return Boolean
   */
  public static final boolean updateTuneUrlTag(
      MessageLogger logger,
      final byte[] data,
      TuneUrlTag tag,
      long index,
      FingerprintCompareResponse fcr) {
    final String payload = convertFingerprintToString(data);
    TuneUrlEntry entry = getTuneUrlEntry(logger, index, payload, Constants.TUNEURL_SEARCH_API_URL);
    tag.setIndex(index);
    tag.setFingerprintCompareResponseData(fcr);
    if (entry == null) {
      tag.setTuneUrlEmptyEntryData(payload);
    } else {
      tag.setTuneUrlEntryData(entry);
    }
    return true;
  }

  /**
   * Helper to initialized a FingerprintCompareResponse structure.
   *
   * @param signature String
   * @param logger MessageLogger
   * @param fcr FingerprintCompareResponse
   * @param timeOffset Long
   * @return FingerprintCompareResponse
   */
  public static final FingerprintCompareResponse resetResponseValue(
      final String signature,
      MessageLogger logger,
      FingerprintCompareResponse fcr,
      final long timeOffset) {
    double zero = 0.0;
    fcr.setOffset(timeOffset);
    fcr.setMostSimilarFramePosition(0);
    fcr.setMostSimilarStartTime(zero);
    fcr.setScore(zero);
    fcr.setSimilarity(zero);
    return fcr;
  }

  /**
   * Helper to close FileOutputStream instance.
   *
   * @param fos FileOutputStream instance
   */
  private static void closeFileOutputStream(FileOutputStream fos) {
    if (fos != null) {
      try {
        fos.close();
      } catch (IOException ignore) {
      }
    }
  }

  /**
   * Helper method to write StringBuffer into a file.
   *
   * @param fileName String
   * @param sb StringBuffer
   */
  private static void writeStringBuffer(final String fileName, final StringBuffer sb) {
    FileOutputStream fib = null;
    File file = null;
    ProcessHelper.deleteFile(fileName);
    try {
      file = new File(fileName);
      if(!file.createNewFile()) {
        throw new IOException();
      }
    } catch (IOException ex) {
      CommonUtil.BadRequestException(ex.getMessage());
      /*NOTREACH*/
    } catch (SecurityException ex) {
      CommonUtil.BadRequestException(ex.getMessage());
      /*NOTREACH*/
    }
    try {
      fib = new FileOutputStream(file);
    } catch (FileNotFoundException ex) {
      closeFileOutputStream(fib);
      CommonUtil.BadRequestException(ex.getMessage());
      /*NOTREACH*/
    }
    byte[] data = sb.toString().getBytes();
    try {
      fib.write(data);
    } catch (IOException ex) {
      closeFileOutputStream(fib);
      CommonUtil.BadRequestException(ex.getMessage());
      /*NOTREACH*/
    }
    closeFileOutputStream(fib);
  }

  /**
   * Helper to write the fingerprint into specified file.
   *
   * @param fileName String
   * @param one array of byte
   * @param onesize int
   * @param two array of byte
   * @param twosize int
   */
  private static void writeFingerprintData(
      final String fileName,
      final byte[] one,
      final int onesize,
      final byte[] two,
      final int twosize) {
    int index;
    final String SPC = " ";
    final String CRLF = "\n";
    StringBuffer sb = new StringBuffer();
    sb.append(onesize).append(CRLF).append(twosize).append(CRLF);
    for (index = 0; index < onesize; index++) {
      if (index > 0) sb.append(SPC);
      sb.append(ProcessHelper.byte2short(one[index]));
    }
    sb.append(CRLF);
    for (index = 0; index < twosize; index++) {
      if (index > 0) sb.append(SPC);
      sb.append(ProcessHelper.byte2short(two[index]));
    }
    sb.append(CRLF);

    writeStringBuffer(fileName, sb);
    if (!ProcessHelper.isFileExist(fileName)) {
      CommonUtil.InternalServerException("File not created '" + fileName + "'");
    }
  }

  /**
   * Helper method to run external executable ./jni/fingerprintexec via
   * runExternalFingerprintModule.sh to get the result on comparing two fingerprint.
   *
   * @param random Random - use to ensure the generated file name is unique.
   * @param logger MessageLogger
   * @param rootDir String
   * @param timeOffset Long
   * @param one Array of byte
   * @param onesize int
   * @param two Array of byte
   * @param twosize int
   * @return FingerprintCompareResponse
   * @throws BaseServiceException If there is error running the runExternalFingerprintModule.sh
   */
  public static final FingerprintCompareResponse runExternalFingerprintModule(
      Random random,
      MessageLogger logger,
      final String rootDir,
      final long timeOffset,
      final byte[] one,
      final int onesize,
      final byte[] two,
      final int twosize)
      throws BaseServiceException {

    final String signature = "runExternalFingerprintModule";
    FingerprintCompareResponse response = new FingerprintCompareResponse();
    resetResponseValue(signature, logger, response, timeOffset);
    // 1. a null offset signify Fingerprint extraction failure.
    response.setOffset(null);
    // 2. Create a unique temporary filename.
    String uniqueName = ProcessHelper.createUniqueFilenameEx(random);
    String outputFilename = String.format("/tmp/%s.data.txt", uniqueName);
    // 3. Make sure it is a unique file name.
    if (ProcessHelper.isFileExist(outputFilename)) {
      uniqueName = ProcessHelper.createUniqueFilenameEx(random);
      outputFilename = String.format("/tmp/%s.data.txt", uniqueName);
    }
    // 4. Create a input file for ./jni/fingerprintexec
    // one size
    // two size
    // [0, ..., one size-1]
    // [0, ..., two size-1]
    writeFingerprintData(outputFilename, one, onesize, two, twosize);

    // 5. the JSON string should be written to this file
    // 6. Run ./jni/fingerprintexec via runExternalFingerprintModule.sh
    // 7. read the JSON string
    String json = executeFingerprintExecAsProcess(uniqueName, rootDir, outputFilename, logger, signature, "jsCompareFingerprint");
    if (json == null) {
      return response;
    }
    ObjectMapper mapper = new ObjectMapper();

    CompareStringResult result = null;
    if (json.isEmpty() || json.charAt(0) != '{') {
      return response;
    }
    // 8. JSON string should be in CompareStringResult structure
    try {
      result = mapper.readValue(json, CompareStringResult.class);
    } catch (Exception ex) {
      logger.logExit(
          signature, "mapper.readValue(json) Failed: " + ex.getMessage() + ",json[" + json + "]");
      return response;
    }
    // 9. Fingerprint comparison is successful.
    response.setOffset(timeOffset);
    response.setMostSimilarFramePosition(
        (int) ProcessHelper.stod(result.getMostSimilarFramePosition()));
    response.setMostSimilarStartTime(ProcessHelper.stod(result.getMostSimilarStartTime()));
    response.setScore(ProcessHelper.stod(result.getScore()));
    response.setSimilarity(ProcessHelper.stod(result.getSimilarity()));
    return response;
  }

  /**
   * Helper to write the fingerprint into specified file.
   *
   * @param fileName String
   * @param one array of byte
   * @param onesize int
   */
  private static void writeFingerprintingData(
      final String fileName, final short[] one, final int onesize) {
    int index;
    final String SPC = " ";
    final String CRLF = "\n";
    StringBuffer sb = new StringBuffer();
    sb.append(onesize).append(CRLF);
    for (index = 0; index < onesize; index++) {
      if (index > 0) sb.append(SPC);
      sb.append(one[index]);
    }
    sb.append(CRLF);

    writeStringBuffer(fileName, sb);
    if (!ProcessHelper.isFileExist(fileName)) {
      CommonUtil.InternalServerException("File not created '" + fileName + "'");
    }
  }

  /**
   * Alternative to ExtractFingerprint(const int16_t *, int):Fingerprint * method with stable
   * results. It make use of main.cpp compiled in the executable at ./jni/fingerprintexec .
   *
   * @param random Random - use to ensure the generated file name is very unique.
   * @param logger MessageLogger
   * @param rootDir String
   * @param one Array of short
   * @param onesize int
   * @return FingerprintResponse
   * @throws BaseServiceException If there is error running the runExternalFingerprintModule.sh
   */
  public static final FingerprintResponse runExternalFingerprinting(
      Random random,
      MessageLogger logger,
      final String rootDir,
      final short[] one,
      final int onesize)
      throws BaseServiceException {

    final String signature = "runExternalFingerprintModule";
    FingerprintResponse response = new FingerprintResponse();
    // 1. Size less than 1L signify Fingerprint extraction failure.
    response.setSize(0L);
    response.setData(null);
    response.setDataEx(null);
    // 2. Create a unique temporary filename.
    String uniqueName = ProcessHelper.createUniqueFilenameEx(random);
    String outputFilename = String.format("/tmp/%s.data.txt", uniqueName);
    // 3. Make sure it is a unique file name.
    if (ProcessHelper.isFileExist(outputFilename)) {
      uniqueName = ProcessHelper.createUniqueFilenameEx(random);
      outputFilename = String.format("/tmp/%s.data.txt", uniqueName);
    }
    // 4. Create a input file for ./jni/fingerprintexec
    // one size
    // [0, ..., one size-1]
    writeFingerprintingData(outputFilename, one, onesize);

    // 5. the JSON string should be written to this file
    // 6. Run ./jni/fingerprintexec via runExternalFingerprintModule.sh
    // 7. read the JSON string
    String json = executeFingerprintExecAsProcess(uniqueName, rootDir, outputFilename, logger, signature, "fingerprint");
    if (json == null) {
      return response;
    }
    ObjectMapper mapper = new ObjectMapper();

    FingerprintEntry result = null;
    if (json.isEmpty() || json.charAt(0) != '{') {
      return response;
    }
    // 8. JSON string should be in FingerprintEntry structure
    try {
      result = mapper.readValue(json, FingerprintEntry.class);
    } catch (Exception ex) {
      logger.logExit(
          signature, "mapper.readValue(json) Failed: " + ex.getMessage() + ",json[" + json + "]");
      return response;
    }
    // 9. Fingerprint extraction is successful.
    response.setSize(result.getSize());
    response.setData(result.getData());
    int index, limit = response.getSize().intValue();
    byte[] fingerprintData = response.getData();
    // 10. Convert the fingerprint for JavaScript consumption.
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    if (limit > 0) {
      sb.append(ProcessHelper.byte2short(fingerprintData[0]));
    }
    for (index = 1; index < limit; index++) {
      sb.append(",");
      sb.append(ProcessHelper.byte2short(fingerprintData[index]));
    }
    sb.append("]");
    response.setDataEx(sb.toString());
    return response;
  }

  private static String executeFingerprintExecAsProcess(Object uniqueName, String rootDir, String outputFilename, MessageLogger logger, String signature, String action) {
    // 5. the JSON string should be written to this file
    String resultFilename = String.format("/tmp/%s.out", uniqueName);
    String command = rootDir + "/runExternalFingerprintModule.sh";
    // 6. Run ./jni/fingerprintexec via runExternalFingerprintModule.sh
    ProcessBuilder processBuilder =
            new ProcessBuilder("/bin/bash", command, resultFilename, outputFilename, action);

    processBuilder.redirectErrorStream(true);
    processBuilder.directory(new File(rootDir));
    Process process;
    try {
      process = processBuilder.start();
      process.waitFor();
    } catch (IOException | InterruptedException ex) {
      logger.logExit(signature, "processBuilder.start() Failed: " + ex.getMessage());
      ProcessHelper.deleteFile(outputFilename);
      ProcessHelper.deleteFile(resultFilename);
      return null;
    }
    // 7. read the JSON string
    String json;
    ProcessHelper.deleteFile(outputFilename);
    if (ProcessHelper.isFileExist(resultFilename)) {
      json = ProcessHelper.readTextFile(resultFilename);
    } else {
      json = "";
    }
    ProcessHelper.deleteFile(resultFilename);

    return json;
  }

  /**
   * Alternative to CompareFingerprints(Fingerprint *,Fingerprint *):FingerprintSimilarity method
   * with stable results. It make use of main.cpp compiled in the executable at
   * ./jni/fingerprintexec .
   *
   * @param fr FingerprintResponse
   * @param timeOffset Long
   * @param logger MessageLogger
   * @param rootDir String
   * @param data Array of short
   * @param random Random - use to ensure the generated file name is very unique.
   * @param fingerprintRate Long
   * @param dataFingerprint Array of byte
   * @return FingerprintCompareResponse
   */
  public static final FingerprintCompareResponse compareFingerprint(
      FingerprintResponse fr,
      Long timeOffset,
      MessageLogger logger,
      final String rootDir,
      final short[] data,
      Random random,
      final Long fingerprintRate,
      final byte[] dataFingerprint) {
    byte[] cData;
    FingerprintCompareResponse fcr = null;
    if ((null != fr) && fr.getSize().longValue() > 1L) {
      cData = fr.getData();
      try {
        fcr =
            FingerprintUtility.runExternalFingerprintModule(
                random,
                logger,
                rootDir,
                timeOffset,
                dataFingerprint,
                dataFingerprint.length,
                cData,
                cData.length);
        if (null == fcr.getOffset()) {
          // Retry again
          fcr =
              FingerprintUtility.runExternalFingerprintModule(
                  random,
                  logger,
                  rootDir,
                  timeOffset,
                  dataFingerprint,
                  dataFingerprint.length,
                  cData,
                  cData.length);
          if (null == fcr.getOffset()) {
            fcr = null;
          }
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        fcr = null;
      }
    }
    return fcr;
  }

  /**
   * Helper to process fingerprint
   *
   * @param logger MessageLogger
   * @param rootDir String
   * @param data Array of short
   * @param elapse Long
   * @param random Random
   * @param fingerprintRate Long
   * @param dataFingerprint Array of byte
   * @param incrementDelta int
   * @return FingerprintCollection
   */
  public static final FingerprintCollection collectFingerprint(
      MessageLogger logger,
      final String rootDir,
      final short[] data,
      Long elapse,
      Random random,
      final Long fingerprintRate,
      final byte[] dataFingerprint,
      int incrementDelta) {
    final String signature = "collectFingerprint";
    ArrayList<FingerprintResponse> frSelection = new ArrayList<>();
    ArrayList<FingerprintCompareResponse> selection = new ArrayList<>();
    int increment;
    Long timeOffset;
    long iStart, iEnd;
    short[] dData;
    int dSize;
    FingerprintResponse fr = null;
    FingerprintCompareResponse fcr = null;
    boolean isDebugOn = Constants.DEBUG_FINGERPRINTING;
    for (increment = 0; increment < 100; increment += incrementDelta) {
      timeOffset = elapse + increment;

      iStart = Converter.muldiv(timeOffset, fingerprintRate, 1000L);
      iEnd = Converter.muldiv(timeOffset + 1000L, fingerprintRate, 1000L);

      dSize = (int) (iEnd - iStart);
      if (dSize < 16) continue;
      dData = Converter.convertListShortEx(data, (int) iStart, dSize);
      if (dData == null) break;

      fr =
          FingerprintUtility.runExternalFingerprinting(
              random, logger, rootDir, dData, dData.length);

      fcr =
          FingerprintUtility.compareFingerprint(
              fr, timeOffset, logger, rootDir, data, random, fingerprintRate, dataFingerprint);

      if (fcr != null) {
        selection.add(fcr);
        frSelection.add(fr);
        if (isDebugOn) {
          logger.logEntry(
              signature,
              new Object[] {
                "pos=", fcr.getOffset(),
                "score=", fcr.getScore(),
                "similarity=", fcr.getSimilarity(),
                "Frame=", fcr.getMostSimilarFramePosition(),
                "StartTime=", fcr.getMostSimilarStartTime()
              });
        }
      }
    }
    FingerprintCollection result = new FingerprintCollection();
    result.setFrCollection(frSelection);
    result.setFcrCollection(selection);
    return result;
  }

  /**
   * Helper method to save audio clips and it's fingerprint.
   *
   * @param logger MessageLogger
   * @param isTrigger boolean
   * @param evaluateAudioStreamEntry
   * @param fr FingerprintResponse instance
   * @param fcr FingerprintCompareResponse
   * @param prefix String
   * @param timeOffset Long
   * @param duration Long
   * @param rootDebugDir String
   * @param debugUniqueName String
   */
  public static final void saveAudioClipsAt(
      MessageLogger logger,
      boolean isTrigger,
      EvaluateAudioStreamEntry evaluateAudioStreamEntry,
      FingerprintResponse fr,
      FingerprintCompareResponse fcr,
      final String prefix,
      Long timeOffset,
      Long duration,
      final String rootDebugDir,
      final String debugUniqueName) {

    final String signature = "saveAudioClipsAt";
    final String which = isTrigger ? "Trigger audio bits" : "Audio bits";
    final String CRLF = "\n";
    final String NEXT_LINE = "' \\\n";
    AudioDataEntry audioDataEntry = evaluateAudioStreamEntry.getAudioData();
    String url = CommonUtil.getString(audioDataEntry.getUrl(), Constants.AUDIOSTREAM_URL_SIZE);
    Long fingerprintRate = audioDataEntry.getFingerprintRate();
    long iStart = Converter.muldiv(timeOffset, fingerprintRate, 1000L);
    long iEnd = Converter.muldiv(timeOffset + duration, fingerprintRate, 1000L);
    long index, offset, diff = iEnd - iStart;
    // The Data.
    short[] data = audioDataEntry.getData();
    long size = (long) data.length;
    String fileName =
        String.format(
            "%s/%s-%s-%s.txt", rootDebugDir, prefix, timeOffset.toString(), debugUniqueName);
    StringBuffer sb = new StringBuffer();

    logger.logEntry(
        signature,
        new Object[] {
          "created=", fileName,
          "which=", which
        });
    // bash script header
    if (!isTrigger) sb.append("#/bin/bash").append(CRLF);

    // File
    sb.append("# File = '").append(fileName).append("'").append(CRLF);

    // URL
    sb.append("# URL = '")
        .append(url)
        .append("', size: ")
        .append(size)
        .append(", sample rate: ")
        .append(fingerprintRate)
        .append(CRLF);

    // Formula
    sb.append(
            "# iStart = parseInt((offset * rate) / 1000), iEnd = parseInt(((offset + duration) * rate) / 1000);")
        .append(CRLF);

    // Values
    sb.append("# ")
        .append(which)
        .append(" at offset ")
        .append(timeOffset)
        .append(" milli-seconds, duration: ")
        .append(duration)
        .append(" milli-seconds, iStart: ")
        .append(iStart)
        .append(", iEnd: ")
        .append(iEnd)
        .append(", iEnd-iStart: ")
        .append(diff)
        .append(" bytes")
        .append(CRLF);

    // RAW data
    sb.append("# raw data: [");
    for (index = 0L, offset = iStart; (offset < iEnd) && (index < diff); index++, offset++) {
      if (index > 0L) sb.append(",");
      sb.append(data[(int) offset]);
    }
    sb.append("]").append(CRLF);

    if (isTrigger) {
      sb.append("# Fingerprint size: ")
          .append(fr.getSize())
          .append(", data: ")
          .append(fr.getDataEx())
          .append(CRLF);
    } else {
      // Where the audio came from
      sb.append(fingerprint_header).append(CRLF);

      // Offset, score, similarity
      sb.append("# offset position : ")
          .append(timeOffset)
          .append(" milli-seconds, score: ")
          .append(fcr.getScore())
          .append(", similarity: ")
          .append(fcr.getSimilarity())
          .append(CRLF);

      // Curl statement
      sb.append("echo \"\"").append(CRLF);
      sb.append(
              "curl -X 'POST' 'https://pnz3vadc52.execute-api.us-east-2.amazonaws.com/dev/search-fingerprint")
          .append(NEXT_LINE);
      sb.append("-H 'accept: application/json").append(NEXT_LINE);
      sb.append("-H 'Content-Type: application/json; charset=UTF-8").append(NEXT_LINE);
      sb.append("-H 'Access-Control-Allow-Origin: *").append(NEXT_LINE);
      sb.append("-d '").append(fingerprint_prefix);
      sb.append(fr.getDataEx());
      sb.append(fingerprint_suffix).append("'").append(CRLF).append(CRLF);
      sb.append("echo \"\"").append(CRLF);
      sb.append("exit 0").append(CRLF).append(CRLF);
    }

    ProcessHelper.makeDir(rootDebugDir);
    writeStringBuffer(fileName, sb);
  }

  /**
   * Helper method to check if FingerprintCompareResponse Frame and StartTime have negative values.
   *
   * @param fcr FingerprintCompareResponse
   * @return boolean true if both Frame and StartTime have negative values, otherwise false.
   */
  public static final boolean hasNegativeFrameStartTimeEx(FingerprintCompareResponse fcr) {
    return (fcr.getMostSimilarFramePosition().intValue() < 0)
        && (CommonUtil.compareDouble(fcr.getMostSimilarStartTime(), Converter.ZERO) < 0);
  }

  /**
   * Helper method to check if FingerprintCompareResponse Frame and StartTime have positive values.
   *
   * @param fcr FingerprintCompareResponse
   * @return boolean true if both Frame and StartTime have positive values, otherwise false.
   */
  public static final boolean hasPositiveFrameStartTimeEx(FingerprintCompareResponse fcr) {
    return (fcr.getMostSimilarFramePosition().intValue() > 0)
        && (CommonUtil.compareDouble(fcr.getMostSimilarStartTime(), Converter.ZERO) > 0);
  }

  /**
   * Helper to comapare two FingerprintCompareResponse were equal.
   *
   * @param fca FingerprintCompareResponse
   * @param fcb FingerprintCompareResponse
   * @return boolean true if equal, otherwise false
   */
  public static final boolean isFrameStartTimeEqual(
      FingerprintCompareResponse fca, FingerprintCompareResponse fcb) {
    if (fca.getMostSimilarFramePosition().intValue()
        == fcb.getMostSimilarFramePosition().intValue()) {

      return CommonUtil.compareDouble(fca.getMostSimilarStartTime(), fcb.getMostSimilarStartTime())
          == 0;
    }
    return false;
  }

  /**
   * Helper method to check if two given tag have similar Frame and StartTime values.
   *
   * @param a TuneUrlTag
   * @param b TuneUrlTag
   * @return boolean true if tags have similar Frame and StartTime values, otherwise false.
   */
  public static final boolean hasSimilarFrameStartTime(TuneUrlTag a, TuneUrlTag b) {
    return a.getMostSimilarFramePosition().intValue() == b.getMostSimilarFramePosition().intValue()
        && CommonUtil.compareDouble(a.getMostSimilarStartTime(), b.getMostSimilarStartTime()) == 0;
  }

  /**
   * Helper method to check if two given tag have similar and negative values for Frame and
   * StartTime.
   *
   * @param a TuneUrlTag
   * @param b TuneUrlTag
   * @return boolean true if tags have similar and negative values for Frame and StartTime,
   *     otherwise false.
   */
  public static final boolean hasNegativeSimilarFrameStartTime(TuneUrlTag a, TuneUrlTag b) {
    if (a.getMostSimilarFramePosition().intValue() < 0) {

      if (a.getMostSimilarFramePosition().intValue()
          == b.getMostSimilarFramePosition().intValue()) {

        if (CommonUtil.compareDouble(a.getMostSimilarStartTime(), 0.0) < 0) {

          return CommonUtil.compareDouble(a.getMostSimilarStartTime(), b.getMostSimilarStartTime())
              == 0;
        }
      }
    }
    return false;
  }

  /**
   * Helper method to check the given tag have negative Frame and StartTime values.
   *
   * @param a TuneUrlTag
   * @return boolean true if tag have negative Frame and StartTime values, otherwise false.
   */
  public static final boolean hasNegativeSimilarFrameStartTimeEx(TuneUrlTag a) {
    if (a.getMostSimilarFramePosition().intValue() < 0) {

      return CommonUtil.compareDouble(a.getMostSimilarStartTime(), 0.0) < 0;
    }
    return false;
  }

  /**
   * Helper method to check the given tag have positive Frame and StartTime values.
   *
   * @param a TuneUrlTag
   * @return boolean true if tag have positive Frame and StartTime values, otherwise false.
   */
  public static final boolean hasPositiveSimilarFrameStartTime(TuneUrlTag a) {
    if (a.getMostSimilarFramePosition().intValue() > 0) {

      return CommonUtil.compareDouble(a.getMostSimilarStartTime(), 0.0) > 0;
    }
    return false;
  }

  /**
   * Helper method to create TuneUrlTag from FingerprintResponse and FingerprintCompareResponse.
   *
   * @param updateOffset boolean
   * @param dataOffset Long
   * @param fr FingerprintResponse
   * @param fcr FingerprintCompareResponse
   * @return TuneUrlTag
   */
  public static final TuneUrlTag newTag(
      boolean updateOffset,
      Long dataOffset,
      FingerprintResponse fr,
      FingerprintCompareResponse fcr) {
    Integer one = 1;
    TuneUrlTag tag = new TuneUrlTag();
    long offset = fcr.getOffset().longValue();
    offset += dataOffset;
    if (updateOffset) fcr.setOffset(offset);
    tag.setDataPosition(offset);
    offset = Converter.muldiv(1L, offset, 100L);
    tag.setIndex(offset);
    tag.setScore(fcr.getScore());
    tag.setSimilarity(fcr.getSimilarity());
    tag.setMostSimilarFramePosition(fcr.getMostSimilarFramePosition());
    tag.setMostSimilarStartTime(fcr.getMostSimilarStartTime());
    tag.setId(0L);
    tag.setName("");
    tag.setType("open_page");
    tag.setInfo("");
    tag.setMatchPercentage(one);
    final String payload = convertFingerprintToString(fr.getData());
    tag.setDescription(payload);
    return tag;
  }

  /**
   * Display live tags for debugging.
   *
   * @param signature String
   * @param logger MessageLogger
   * @param tag TuneUrlTag
   */
  public static final void displayLiveTagsEx(
      final String signature, MessageLogger logger, TuneUrlTag tag) {
    logger.logExit(
        signature,
        new Object[] {
          "pos=", tag.getDataPosition(),
          "score=", tag.getScore(),
          "similarity=", tag.getSimilarity(),
          "Frame=", tag.getMostSimilarFramePosition(),
          "StartTime=", tag.getMostSimilarStartTime(),
          "index=", tag.getIndex()
        });
  }

  /**
   * Display live tags for debugging.
   *
   * @param signature String
   * @param logger MessageLogger
   * @param liveTags List&lt;TuneUrlTag>
   */
  public static final void displayLiveTags(
      final String signature, MessageLogger logger, List<TuneUrlTag> liveTags) {
    for (int index = 0; index < liveTags.size(); index++) {
      displayLiveTagsEx(signature, logger, liveTags.get(index));
    }
  }

  /**
   * Helper method to create Array of tags.
   *
   * @param dataOffset Long data offset
   * @param frSelection List&lt;FingerprintResponse>
   * @param fcrSelection List&lt;FingerprintCompareResponse>
   * @return List&lt;TuneUrlTag>
   */
  public static final List<TuneUrlTag> createTags(
      Long dataOffset,
      List<FingerprintResponse> frSelection,
      List<FingerprintCompareResponse> fcrSelection) {
    ArrayList<TuneUrlTag> tags = new ArrayList<>();
    TuneUrlTag tag;
    FingerprintResponse fx;
    FingerprintResponse fr;
    FingerprintCompareResponse fcx;
    FingerprintCompareResponse fcr;
    int index, limit = fcrSelection.size();
    if (limit > 2) {
      for (index = 0; index < limit; index++) {
        fr = frSelection.get(index);
        fcr = fcrSelection.get(index);
        tag = newTag(false, dataOffset, fr, fcr);
        tags.add(tag);
      }
      // Try to locate the X-Y-X or X-Y-Z-Y-X pattern
      List<TuneUrlTag> newTag = FingerprintUtility.pruneTags(tags);
      if (newTag.size() > 0) return newTag;
      tags.clear();
    }
    if (limit == 1) {
      tag = newTag(true, dataOffset, frSelection.get(0), fcrSelection.get(0));
      tags.add(tag);
    } else if (limit > 1) {
      fx = frSelection.get(0);
      fcx = fcrSelection.get(0);
      for (index = 1; index < limit; index++) {
        fr = frSelection.get(index);
        fcr = fcrSelection.get(index);
        if (CommonUtil.compareDouble(fcr.getScore(), fcx.getScore()) > 0) {
          fcx = fcr;
          fx = fr;
        }
      }
      tag = newTag(true, dataOffset, fx, fcx);
      tags.add(tag);
    }
    return tags;
  }

  /**
   * Helper to prune List of TuneUrlTag
   *
   * @param tags List&lt;TuneUrlTag>
   * @return List&lt;TuneUrlTag>
   */
  public static final List<TuneUrlTag> pruneTags(List<TuneUrlTag> tags) {

    ArrayList<TuneUrlTag> newTags = new ArrayList<>();
    int index, limit = tags.size() - 4;
    long distance;
    TuneUrlTag a;
    TuneUrlTag b;
    TuneUrlTag c;
    TuneUrlTag d;

    for (index = 0; index <= limit; index++) {
      a = tags.get(index);
      b = tags.get(index + 1);
      c = tags.get(index + 2);
      d = tags.get(index + 3);
      distance = b.getDataPosition() - a.getDataPosition();
      if (distance > 800L) continue;
      distance = c.getDataPosition() - b.getDataPosition();
      if (distance > 800L) continue;
      distance = d.getDataPosition() - c.getDataPosition();
      if (distance > 800L) continue;
      // 509 509 -214748364 -50
      if (c.getMostSimilarFramePosition().intValue() == Constants.FRAME_LOWEST_VALUE) {
        if (FingerprintUtility.hasPositiveSimilarFrameStartTime(a)
            && FingerprintUtility.hasSimilarFrameStartTime(a, b)
            && FingerprintUtility.hasNegativeSimilarFrameStartTimeEx(d)) {
          newTags.add(c);
          index += 3;
          continue;
        }
      }
      // 509 -214748364 -50 -50
      if (b.getMostSimilarFramePosition().intValue() == Constants.FRAME_LOWEST_VALUE) {
        if (FingerprintUtility.hasPositiveSimilarFrameStartTime(a)
            && FingerprintUtility.hasNegativeSimilarFrameStartTimeEx(c)
            && FingerprintUtility.hasSimilarFrameStartTime(c, d)) {
          newTags.add(d);
          index += 3;
          continue;
        }
      }
      // -214748364 -50 -50 -50
      if (a.getMostSimilarFramePosition().intValue() == Constants.FRAME_LOWEST_VALUE) {
        if (FingerprintUtility.hasNegativeSimilarFrameStartTimeEx(b)
            && FingerprintUtility.hasSimilarFrameStartTime(b, c)
            && FingerprintUtility.hasSimilarFrameStartTime(c, d)) {

          distance = d.getDataPosition() - a.getDataPosition();
          if (distance < 1100L) continue;
          if (distance > 1100L) {
            newTags.add(b);
          } else {
            newTags.add(d);
          }
          index += 3;
          continue;
        }
      }
    }
    return newTags;
  }

  /**
   * Helper to prune List of TuneUrlTag for /dev/v3/evaluateOneSecondAudioStream .
   *
   * @param isDebugOn boolean
   * @param logger MessageLogger
   * @param tags List&lt;TuneUrlTag>
   * @return List&lt;TuneUrlTag>
   */
  public static final List<TuneUrlTag> pruneTagsEx(
      boolean isDebugOn, MessageLogger logger, List<TuneUrlTag> tags) {

    ArrayList<TuneUrlTag> newTags = new ArrayList<>();
    int index, limit = tags.size() - 4;
    long d1, d2, d3, distance;
    TuneUrlTag a;
    TuneUrlTag b;
    TuneUrlTag c;
    TuneUrlTag d;

    for (index = 0; index <= limit; index++) {
      a = tags.get(index);
      b = tags.get(index + 1);
      c = tags.get(index + 2);
      d = tags.get(index + 3);
      d1 = b.getDataPosition() - a.getDataPosition();
      if (d1 > 800L) continue;
      d2 = c.getDataPosition() - b.getDataPosition();
      if (d2 > 800L) continue;
      d3 = d.getDataPosition() - c.getDataPosition();
      if (d3 > 800L) continue;
      distance = d.getDataPosition() - a.getDataPosition();
      if (isDebugOn) {
        logger.debug(
            "{\"Leaving\":{pruneTagsEx(%d %d %d %d %d, diff=%d %d %d %d)}}",
            a.getDataPosition(),
            a.getMostSimilarFramePosition(),
            b.getMostSimilarFramePosition(),
            c.getMostSimilarFramePosition(),
            d.getMostSimilarFramePosition(),
            d1,
            d2,
            d3,
            distance);
      }
      // 509 509 -214748364 -50
      // 509 509          N -50 SELECT C 2880
      if (c.getMostSimilarFramePosition().intValue() == Constants.FRAME_LOWEST_VALUE) {
        if (FingerprintUtility.hasPositiveSimilarFrameStartTime(a)
            && FingerprintUtility.hasSimilarFrameStartTime(a, b)
            && FingerprintUtility.hasNegativeSimilarFrameStartTimeEx(d)) {

          if (distance > 1840L) {
            newTags.add(c);
            index += 3;
          }
          continue;
        }
      }
      // 509 -214748364 -50 -50
      // 509          N -50 -50 SELECT C 13200
      if (b.getMostSimilarFramePosition().intValue() == Constants.FRAME_LOWEST_VALUE) {
        if (FingerprintUtility.hasPositiveSimilarFrameStartTime(a)
            && FingerprintUtility.hasNegativeSimilarFrameStartTimeEx(c)
            && FingerprintUtility.hasSimilarFrameStartTime(c, d)) {

          if (distance > 1280L) {
            newTags.add(c);
            index += 3;
          }
          continue;
        }
      }
      // -214748364 -50 -50 -50
      //          N -50 -50 -50 SELECT B 33900
      if (a.getMostSimilarFramePosition().intValue() == Constants.FRAME_LOWEST_VALUE) {
        if (FingerprintUtility.hasNegativeSimilarFrameStartTimeEx(b)
            && FingerprintUtility.hasSimilarFrameStartTime(b, c)
            && FingerprintUtility.hasSimilarFrameStartTime(c, d)) {

          if (distance > 1096L) {
            newTags.add(b);
            index += 3;
          }
          continue;
        }
      }
    }
    return newTags;
  }

  /** Default constructor. */
  private FingerprintUtility() {
    // Hidden
  }
}
