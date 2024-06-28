package com.tuneurl.webrtc.util.util.fingerprint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuneurl.webrtc.util.controller.dto.*;
import com.tuneurl.webrtc.util.exception.BaseServiceException;
import com.tuneurl.webrtc.util.util.CommonUtil;
import com.tuneurl.webrtc.util.util.Converter;
import com.tuneurl.webrtc.util.util.MessageLogger;
import com.tuneurl.webrtc.util.util.ProcessHelper;
import com.tuneurl.webrtc.util.value.Constants;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class FingerprintExternals {

  private static final String fingerprint_prefix =
      "{\"fingerprint\":{\"type\":\"Buffer\",\"data\":";

  private static final String fingerprint_suffix = "},\"fingerprint_version\":\"1\"}";

  private static final String fingerprint_header =
      "# Generated using: https://streamradiolocal.tmalbon.com/audio/audio/10240-audio-streams-0230000.mp3\n# crc32 /project/tuneurl-poc/tuneurl-poc/src/main/webapp/audio/10240-audio-streams-0230000.mp3\n# bbfbe45f    /project/tuneurl-poc/tuneurl-poc/src/main/webapp/audio/10240-audio-streams-0230000.mp3\n";

  static FingerprintExternals fingerprintExternals;

  public static FingerprintExternals getFingerprintInstance() {
    if (fingerprintExternals == null) {
      fingerprintExternals = new FingerprintExternals(FingerprintUtility.getFingerprintInstance());
    }
    return fingerprintExternals;
  }

  FingerprintUtility fingerprintUtility;
  MessageLogger logger;

  public FingerprintExternals(FingerprintUtility fingerprintUtility) {
    this.fingerprintUtility = fingerprintUtility;
    this.logger = MessageLogger.getMessageLoggerInstance();
  }

  /**
   * Helper method to run external executable ./jni/fingerprintexec via
   * runExternalFingerprintModule.sh to get the result on comparing two fingerprint.
   *
   * @param random Random - use to ensure the generated file name is unique.
   * @param rootDir String
   * @param timeOffset Long
   * @param one Array of byte
   * @param onesize int
   * @param two Array of byte
   * @param twosize int
   * @return FingerprintCompareResponse
   * @throws BaseServiceException If there is error running the runExternalFingerprintModule.sh
   */
  public FingerprintCompareResponse runExternalFingerprintModule(
      Random random,
      final String rootDir,
      final long timeOffset,
      final String dataFingerprint,
      final short[] two,
      final int twosize)
      throws BaseServiceException {

    final String signature = "runExternalFingerprintModule";
    FingerprintCompareResponse response = new FingerprintCompareResponse();
    FingerprintUtility.resetResponseValue(response, timeOffset);
    // 1. a null offset signify Fingerprint extraction failure.
    response.setOffset(null);
    // 2. Create a unique temporary filename.
    String uniqueName = ProcessHelper.createUniqueFilenameEx(random);
    String outputFilename = String.format("/tmp/%s.data.txt", uniqueName);
    // 3. Make sure it is a unique file name.
    /*
    if (ProcessHelper.isFileExist(outputFilename)) {
      uniqueName = ProcessHelper.createUniqueFilenameEx(random);
      outputFilename = String.format("/tmp/%s.data.txt", uniqueName);
    }
     */
    // 4. Create an input file for ./jni/fingerprintexec
    // two size
    // [0, ..., two size-1]

    // Generate the one and onesize variables
    fingerprintUtility.writeFingerprintDataWithBuffer(
        outputFilename, dataFingerprint, two, twosize);

    // 5. the JSON string should be written to this file
    // 6. Run ./jni/fingerprintexec via runExternalFingerprintModule.sh
    // 7. read the JSON string
    String json =
        executeFingerprintExecAsProcess(
            uniqueName, rootDir, outputFilename, signature, "jsCompareFingerprint");
    if (json == null) {
      return response;
    }
    // try (FileWriter writer = new FileWriter("11.txt", true)) {
    //   writer.write(""+timeOffset);
    //   writer.write(json);
    // } catch (IOException e) {
    //     e.printStackTrace();
    // }

    CompareStringResult result = null;
    if (json.isEmpty() || json.charAt(0) != '{') {
      return response;
    }
    // 8. JSON string should be in CompareStringResult structure
    try {
      ObjectMapper mapper = new ObjectMapper();
      result = mapper.readValue(json, CompareStringResult.class);

    } catch (Exception ex) {
      logger.logExit(
          signature, "mapper.readValue(json) Failed: " + ex.getMessage() + ",json[" + json + "]");
      return response;
    }
    // 9. Fingerprint comparison is successful.
    response.setOffset(timeOffset + ProcessHelper.parseLong(result.getOffset(), 0));
    response.setSimilarity(ProcessHelper.parseLong(result.getSimilarity(), 0));
    return response;
  }

  /**
   * Alternative to ExtractFingerprint(const int16_t *, int):Fingerprint * method with stable
   * results. This make use of main.cpp compiled in the executable at ./jni/fingerprintexec .
   *
   * @param random Random - use to ensure the generated file name is unique.
   * @param rootDir String
   * @param one Array of short
   * @param onesize int
   * @return FingerprintResponse
   * @throws BaseServiceException If there is error running the runExternalFingerprintModule.sh
   */
  public FingerprintResponseNew runExternalFingerprinting(
      Random random, final String rootDir, final short[] one, final int onesize)
      throws BaseServiceException {
    // try (FileWriter writer = new FileWriter("11.txt", true)) {
    //   writer.write(json);
    // } catch (IOException e) {
    //     e.printStackTrace();
    // }
    final String signature = "runExternalFingerprintModule";
    FingerprintResponseNew response = new FingerprintResponseNew();
    // 1. Size less than 1L signify Fingerprint extraction failure.
    // response.setSize(0L);
    // response.setData(null);
    // response.setDataEx(null);
    // 2. Create a unique temporary filename.
    String uniqueName = ProcessHelper.createUniqueFilenameEx(random);
    String outputFilename = String.format("/tmp/%s.data.txt", uniqueName);
    // 3. Make sure it is a unique file name.
    /*
    if (ProcessHelper.isFileExist(outputFilename)) {
      uniqueName = ProcessHelper.createUniqueFilenameEx(random);
      outputFilename = String.format("/tmp/%s.data.txt", uniqueName);
    }
     */
    // 4. Create an input file for ./jni/fingerprintexec
    // one size
    // [0, ..., one size-1]
    fingerprintUtility.writeFingerprintingData(outputFilename, one, onesize);

    // 5. the JSON string should be written to this file
    // 6. Run ./jni/fingerprintexec via runExternalFingerprintModule.sh
    // 7. read the JSON string
    String json =
        executeFingerprintExecAsProcess(
            uniqueName, rootDir, outputFilename, signature, "fingerprint");

    if (json == null) {
      return response;
    }

    // FingerprintEntry result = null;
    if (json.isEmpty() || json.charAt(0) != '{') {
      return response;
    }

    // 8. JSON string should be in FingerprintEntry structure
    try {
      ObjectMapper mapper = new ObjectMapper();
      response = mapper.readValue(json, FingerprintResponseNew.class);
    } catch (Exception ex) {
      logger.logExit(
          signature, "mapper.readValue(json) Failed: " + ex.getMessage() + ",json[" + json + "]");
      return response;
    }

    return response;
  }

  /**
   * Alternative to ExtractFingerprint(const int16_t *, int):Fingerprint * method with stable
   * results. This make use of main.cpp compiled in the executable at ./jni/fingerprintexec .
   *
   * @param random Random - use to ensure the generated file name is unique.
   * @param rootDir String
   * @param one Array of short
   * @param onesize int
   * @return FingerprintResponse
   * @throws BaseServiceException If there is error running the runExternalFingerprintModule.sh
   */
  public FingerprintResponse runExternalFingerprinting_Ex(
      Random random, final String rootDir, final short[] one, final int onesize)
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
    /*
    if (ProcessHelper.isFileExist(outputFilename)) {
      uniqueName = ProcessHelper.createUniqueFilenameEx(random);
      outputFilename = String.format("/tmp/%s.data.txt", uniqueName);
    }
     */
    // 4. Create an input file for ./jni/fingerprintexec
    // one size
    // [0, ..., one size-1]
    fingerprintUtility.writeFingerprintingData(outputFilename, one, onesize);

    // 5. the JSON string should be written to this file
    // 6. Run ./jni/fingerprintexec via runExternalFingerprintModule.sh
    // 7. read the JSON string
    String json =
        executeFingerprintExecAsProcess(uniqueName, rootDir, outputFilename, signature, "stream");
    if (json == null) {
      return response;
    }

    FingerprintEntry result = null;
    if (json.isEmpty() || json.charAt(0) != '{') {
      return response;
    }
    // 8. JSON string should be in FingerprintEntry structure
    try {
      ObjectMapper mapper = new ObjectMapper();
      result = mapper.readValue(json, FingerprintEntry.class);
    } catch (Exception ex) {
      logger.logExit(
          signature, "mapper.readValue(json) Failed: " + ex.getMessage() + ",json[" + json + "]");
      return response;
    }
    // 9. Fingerprint extraction is successful.
    response.setSize(result.getSize());
    response.setData(result.getData());
    StringBuffer sb = getStringBuilder(response);

    response.setDataEx(sb.toString());
    return response;
  }

  private static StringBuffer getStringBuilder(FingerprintResponse response) {
    int index, limit = response.getSize().intValue();
    short[] fingerprintData = response.getData();
    // 10. Convert the fingerprint for JavaScript consumption.
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    if (limit > 0) {
      sb.append(fingerprintData[0]);
    }
    for (index = 1; index < limit; index++) {
      sb.append(",");
      sb.append(fingerprintData[index]);
    }
    sb.append("]");
    return sb;
  }

  private String executeFingerprintExecAsProcess(
      Object uniqueName, String rootDir, String outputFilename, String signature, String action) {

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
   * Helper method to write StringBuffer into a file.
   *
   * @param fileName String
   * @param sb StringBuffer
   */
  public void writeStringBuffer(final String fileName, final StringBuffer sb) {
    FileOutputStream fib = null;
    File file = null;
    try {
      file = new File(fileName);
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
   * Helper to close FileOutputStream instance.
   *
   * @param fos FileOutputStream instance
   */
  private void closeFileOutputStream(FileOutputStream fos) {
    if (fos != null) {
      try {
        fos.close();
      } catch (IOException ignore) {
      }
    }
  }

  /**
   * Helper method to save audio clips and it's fingerprint.
   *
   * @param isTrigger boolean
   * @param evaluateAudioStreamEntry entry object
   * @param fr FingerprintResponse instance
   * @param fcr FingerprintCompareResponse
   * @param prefix String
   * @param timeOffset Long
   * @param duration Long
   * @param rootDebugDir String
   * @param debugUniqueName String
   */
  public final void saveAudioClipsAt(
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
          .append(" milli-seconds")
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
    fingerprintExternals.writeStringBuffer(fileName, sb);
  }
}
