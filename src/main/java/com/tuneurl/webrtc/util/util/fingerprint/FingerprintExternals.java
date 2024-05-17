package com.tuneurl.webrtc.util.util.fingerprint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuneurl.webrtc.util.controller.dto.CompareStringResult;
import com.tuneurl.webrtc.util.controller.dto.FingerprintCompareResponse;
import com.tuneurl.webrtc.util.controller.dto.FingerprintEntry;
import com.tuneurl.webrtc.util.controller.dto.FingerprintResponse;
import com.tuneurl.webrtc.util.exception.BaseServiceException;
import com.tuneurl.webrtc.util.util.MessageLogger;
import com.tuneurl.webrtc.util.util.ProcessHelper;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class FingerprintExternals {

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
      final StringBuffer one,
      final int onesize,
      final byte[] two,
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
    if (ProcessHelper.isFileExist(outputFilename)) {
      uniqueName = ProcessHelper.createUniqueFilenameEx(random);
      outputFilename = String.format("/tmp/%s.data.txt", uniqueName);
    }
    // 4. Create an input file for ./jni/fingerprintexec
    // one size
    // two size
    // [0, ..., one size-1]
    // [0, ..., two size-1]
    fingerprintUtility.writeFingerprintDataWithBuffer(outputFilename, one, two, onesize, twosize);

    // 5. the JSON string should be written to this file
    // 6. Run ./jni/fingerprintexec via runExternalFingerprintModule.sh
    // 7. read the JSON string
    String json =
        executeFingerprintExecAsProcess(
            uniqueName, rootDir, outputFilename, signature, "jsCompareFingerprint");
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
  public FingerprintResponse runExternalFingerprinting(
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
    if (ProcessHelper.isFileExist(outputFilename)) {
      uniqueName = ProcessHelper.createUniqueFilenameEx(random);
      outputFilename = String.format("/tmp/%s.data.txt", uniqueName);
    }
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
    StringBuilder sb = getStringBuilder(response);
    response.setDataEx(sb.toString());
    return response;
  }

  private static StringBuilder getStringBuilder(FingerprintResponse response) {
    int index, limit = response.getSize().intValue();
    byte[] fingerprintData = response.getData();
    // 10. Convert the fingerprint for JavaScript consumption.
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    if (limit > 0) {
      sb.append(ProcessHelper.byte2short(fingerprintData[0]));
    }
    for (index = 1; index < limit; index++) {
      sb.append(",");
      sb.append(ProcessHelper.byte2short(fingerprintData[index]));
    }
    sb.append("]");
    return sb;
  }

  private String executeFingerprintExecAsProcess(
      Object uniqueName, String rootDir, String outputFilename, String signature, String action) {
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
}
