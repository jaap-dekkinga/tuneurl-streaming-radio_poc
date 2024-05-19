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
    final String Comma = ",";
    StringBuilder sb = new StringBuilder();
    sb.append(fingerprint_prefix).append("[");
    if (data.length > 0) {
      sb.append(ProcessHelper.byte2short(data[0]));
      for (offset = 1; offset < data.length; offset++) {
        sb.append(Comma);
        sb.append(ProcessHelper.byte2short(data[offset]));
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
      final byte[] data,
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
    double zero = 0.0;
    fcr.setOffset(timeOffset);
    fcr.setMostSimilarFramePosition(0);
    fcr.setMostSimilarStartTime(zero);
    fcr.setScore(zero);
    fcr.setSimilarity(zero);
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
      final String fileName,
      final StringBuffer bufferedOne,
      final byte[] two,
      final int onesize,
      final int twosize) {
    final String SPC = " ";
    final String CRLF = "\n";
    StringBuffer sb = new StringBuffer();
    sb.append(onesize).append(CRLF).append(twosize).append(CRLF);
    sb.append(bufferedOne);

    if (twosize > 0) {
      sb.append(ProcessHelper.byte2short(two[0]));
      for (int index = 1; index < twosize; index++) {
        sb.append(SPC);
        sb.append(ProcessHelper.byte2short(two[index]));
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
    if (!ProcessHelper.isFileExist(fileName)) {
      CommonUtil.InternalServerException("File not created '" + fileName + "'");
    }
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
      StringBuffer dataFingerprintBuffer,
      int dataFingerprintSize) {
    byte[] cData;
    FingerprintCompareResponse fcr = null;
    if ((null != fr) && fr.getSize() > 1L) {
      cData = fr.getData();
      try {
        fcr =
            fingerprintExternals.runExternalFingerprintModule(
                random,
                rootDir,
                timeOffset,
                dataFingerprintBuffer,
                dataFingerprintSize,
                cData,
                cData.length);
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
    FingerprintCompareResponse fca;
    FingerprintCompareResponse fcb;
    FingerprintCompareResponse fcc;
    FingerprintCompareResponse fcd;
    FingerprintCompareResponse fce;

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

    return new Object[] {fcr, fr};
  }

  /**
   * Helper method to check if FingerprintCompareResponse Frame and StartTime have negative values.
   *
   * @param fcr FingerprintCompareResponse
   * @return boolean true if both Frame and StartTime have negative values, otherwise false.
   */
  public static final boolean hasNegativeFrameStartTimeEx(FingerprintCompareResponse fcr) {
    return (fcr.getMostSimilarFramePosition() < 0)
        && (CommonUtil.compareDouble(fcr.getMostSimilarStartTime(), Converter.ZERO) < 0);
  }

  /**
   * Helper method to check if FingerprintCompareResponse Frame and StartTime have positive values.
   *
   * @param fcr FingerprintCompareResponse
   * @return boolean true if both Frame and StartTime have positive values, otherwise false.
   */
  public static final boolean hasPositiveFrameStartTimeEx(FingerprintCompareResponse fcr) {
    return (fcr.getMostSimilarFramePosition() > 0)
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
    if (a.getMostSimilarFramePosition() < 0) {

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
    if (a.getMostSimilarFramePosition() < 0) {

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
    if (a.getMostSimilarFramePosition() > 0) {

      return CommonUtil.compareDouble(a.getMostSimilarStartTime(), 0.0) > 0;
    }
    return false;
  }
}
