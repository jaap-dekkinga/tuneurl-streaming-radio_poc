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

package com.tuneurl.webrtc.util.util.fingerprint;

import com.tuneurl.webrtc.util.controller.dto.*;
import com.tuneurl.webrtc.util.util.*;
import com.tuneurl.webrtc.util.value.Constants;
import java.util.List;
import java.util.Random;
import lombok.Getter;

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
@Getter
public class FingerprintUtility {

  static FingerprintUtility fingerprintInstance;

  public static FingerprintUtility getFingerprintInstance() {
    if (fingerprintInstance == null) {
      fingerprintInstance = new FingerprintUtility();
    }
    return fingerprintInstance;
  }

  FingerprintExternals fingerprintExternals;
  /** Default constructor. */
  public FingerprintUtility() {
    this.fingerprintExternals = new FingerprintExternals(this);
  }

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
   * Helper to process fingerprint
   *
   * @param rootDir String
   * @param data Array of short
   * @param elapse Long
   * @param random Random
   * @param fingerprintRate Long
   * @param dataFingerprintBuffer StringBuffer
   * @param dataFingerprintBufferSize int
   * @return FingerprintCollection
   */
  public FingerprintCollection collectFingerprint(
      final String rootDir,
      final short[] data,
      Long elapse,
      Random random,
      final Long fingerprintRate,
      StringBuffer dataFingerprintBuffer,
      int dataFingerprintBufferSize) {
    FingerprintThreadCollector fingerprintThread = new FingerprintThreadCollector();
    return fingerprintThread.collectFingerprint(
        rootDir,
        data,
        elapse,
        random,
        fingerprintRate,
        dataFingerprintBuffer,
        dataFingerprintBufferSize);
  }

  public static final String convertFingerprintToString(final short[] data) {
    int offset;
    final String Comma = ",";
    StringBuffer sb = new StringBuffer();
    sb.append(fingerprint_prefix).append("[");
    if (data.length > 0) {
      sb.append(data[0]);
      for (offset = 1; offset < data.length; offset++) {
        sb.append(Comma);
        sb.append(data[offset]);
      }
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
      final short[] data,
      TuneUrlTag tag,
      long index,
      FingerprintCompareResponse fcr) {
    final String payload = convertFingerprintToString(data);
    TuneUrlEntry entry = getTuneUrlEntry(logger, index, payload, Constants.TUNEURL_SEARCH_API_URL);
    tag.setIndex(index);
    tag.setFingerprintCompareResponseData(fcr, true);
    if (entry == null) {
      tag.setTuneUrlEmptyEntryData(payload);
    } else {
      tag.setTuneUrlEntryData(entry);
    }
    return true;
  }

  /**
   * Helper to initialize a FingerprintCompareResponse structure.
   *
   * @param fcr FingerprintCompareResponse
   * @param timeOffset Long
   * @return FingerprintCompareResponse
   */
  public static final FingerprintCompareResponse resetResponseValue(
      FingerprintCompareResponse fcr, final long timeOffset) {
    fcr.setOffset(timeOffset);
    fcr.setSimilarity(0L);
    return fcr;
  }

  /**
   * Helper to cache the fingerprint base part.
   *
   * @param one array of byte
   * @param onesize int
   * @return a StringBuffer to optimize a recurring assemblage
   */
  public static StringBuffer getFingerprintBufferedPart(final byte[] one, final int onesize) {
    final String SPC = " ";
    final String CRLF = "\n";
    StringBuffer sb = new StringBuffer();
    for (int index = 0; index < onesize; index++) {
      if (index > 0) sb.append(SPC);
      sb.append(ProcessHelper.byte2short(one[index]));
    }
    sb.append(CRLF);

    return sb;
  }

  /**
   * Helper to write the fingerprint with buffer help into specified file.
   *
   * @param fileName String
   * @param bufferedOne array of byte
   * @param onesize int
   * @param two array of byte
   * @param twosize int
   */
  public void writeFingerprintDataWithBuffer(
      final String fileName, final String dataFingerprint, final short[] two, final int twosize) {

    final String SPC = " ";
    final String CRLF = "\n";
    StringBuffer sb = new StringBuffer();

    sb.append(dataFingerprint).append(CRLF).append(twosize).append(CRLF);
    if (twosize > 0) {
      sb.append(two[0]);
      for (int index = 1; index < twosize; index++) {
        sb.append(SPC);
        sb.append(two[index]);
      }
    }
    sb.append(CRLF);

    fingerprintExternals.writeStringBuffer(fileName, sb);
    if (!ProcessHelper.isFileExist(fileName)) {
      CommonUtil.InternalServerException("File not created '" + fileName + "'");
    }
  }

  /**
   * Helper to write the fingerprint into specified file.
   *
   * @param fileName String
   * @param one array of byte
   * @param onesize int
   */
  public void writeFingerprintingData(final String fileName, final short[] one, final int onesize) {
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

    fingerprintExternals.writeStringBuffer(fileName, sb);
  }

  /**
   * Alternative to CompareFingerprints(Fingerprint *,Fingerprint *):FingerprintSimilarity method
   * with stable results. This make use of main.cpp compiled in the executable at
   * ./jni/fingerprintexec .
   *
   * @param fr FingerprintResponse
   * @param timeOffset Long
   * @param rootDir String
   * @param random Random - use to ensure the generated file name is unique.
   * @param dataFingerprintBuffer A String Buffer with the datafingerprint already processed
   * @param dataFingerprintSize the lenght of the dataFingerprint - before buffered
   * @return FingerprintCompareResponse
   */
  public FingerprintCompareResponse compareFingerprint(
      FingerprintResponse fr,
      Long timeOffset,
      final String rootDir,
      Random random,
      String dataFingerprint) {

    short[] cData;
    FingerprintCompareResponse fcr = null;
    if ((null != fr) && fr.getSize() > 1L) {
      cData = fr.getData();
      try {
        fcr =
            fingerprintExternals.runExternalFingerprintModule(
                random, rootDir, timeOffset, dataFingerprint, cData, cData.length);
        if (null == fcr.getOffset()) {
          fcr = null;
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        fcr = null;
      }
    }
    return fcr;
  }

  public static Object[] fingerprintComparisons(
      List<FingerprintCompareResponse> selection,
      List<FingerprintResponse> frSelection,
      FingerprintCompareResponse fcr,
      FingerprintResponse fr) {

    for (int i = 0; i < selection.size(); i++) {
      FingerprintCompareResponse f = selection.get(i);
      if (f.getSimilarity() > 0) {
        fcr = f;
        fr = frSelection.get(i); // Assuming frSelection is another list corresponding to selection
        break; // Exit loop once a valid selection is made
      }
    }
    return new Object[] {fcr, fr};
  }
}
