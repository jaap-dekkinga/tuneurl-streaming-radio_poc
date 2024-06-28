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

import com.tuneurl.webrtc.util.controller.dto.*;
import com.tuneurl.webrtc.util.exception.BaseServiceException;
import com.tuneurl.webrtc.util.model.AudioStreamTrainingChannel;
import com.tuneurl.webrtc.util.value.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Process helper Structure converter.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public final class Converter {

  public static final Double ZERO = 0.0;

  /**
   * Helper to do multiplication, then division.
   *
   * @param a long multiplicant
   * @param b long multiplier
   * @param c long divider
   * @return long or zero
   */
  public static long muldiv(long a, long b, long c) {
    long d = 0L;
    double e;
    if (c > 0L) {
      e = a * b;
      e = e / c;
      d = (long) e;
    }
    return d;
  }

  /**
   * Helper method to valid the data, rate and duration values.
   *
   * @param audioData
   * @throws BaseServiceException
   */
  public static void checkAudioDataEntryDataSize(final AudioDataEntry audioData)
      throws BaseServiceException {
    long expectedSize = Converter.muldiv(audioData.getSampleRate(), audioData.getDuration(), 1L);
    long fingerprintSize =
        Converter.muldiv(audioData.getFingerprintRate(), audioData.getDuration(), 1L);
    if (fingerprintSize != expectedSize) expectedSize = fingerprintSize;
    long size = (long) audioData.getData().length;
    if (size != expectedSize) {
      CommonUtil.BadRequestException(
          "Expected size of AudioDataEntry.data is "
              + expectedSize
              + ", does not match with "
              + size);
    }
  }

  /**
   * Helper method to convert List&lt;Byte> to byte[] starting at offset with size.
   *
   * @param data List&lt;Byte>
   * @param pOffset int
   * @param size int
   * @return array of byte or null
   */
  public static byte[] convertListByteEx(List<Byte> data, int pOffset, int size) {
    int dsize = data.size() - pOffset;
    if (dsize < size) return null;
    int index, offset;
    byte[] datus = new byte[size];
    for (index = 0, offset = pOffset, dsize = pOffset + size;
        index < size && offset < dsize;
        offset++, index++) {
      datus[index] = data.get(offset);
    }
    return datus;
  }

  /**
   * Helper method to convert List&lt;Short> to short[] starting at offset with size.
   *
   * @param data Array of short
   * @param pOffset int
   * @param size int
   * @return array of byte or null
   */
  public static short[] convertListShortEx(final short[] data, int pOffset, int size) {
    return Arrays.copyOfRange(data, pOffset, pOffset + size);
  }

  /**
   * Helper method to validate Url. If valid generate crc32 from URL.
   *
   * @param url String the URL to check
   * @return String - CRC32 version of the URL
   * @throws BaseServiceException If given url is null, empty, less than 13 or does not start with
   *     https://
   */
  public static String validateUrlOrGencrc32(final String url) throws BaseServiceException {
    ProcessHelper.checkNullOrEmptyString(url, "AudioDataEntry.Url");
    // if (!url.startsWith("http://localhost") && (url.length() < 13 ||
    // !url.startsWith("https://"))) {
    //   CommonUtil.BadRequestException("Specify valid AudioDataEntry.Url");
    // }
    return ProcessHelper.genCrc32(url);
  }

  /**
   * Helper method to valid size and value of AudioDataEntry.data.
   *
   * @param data Array of byte
   * @param size int
   * @throws BaseServiceException If size mismatch
   */
  public static void validateDataSize(final byte[] data, final int size)
      throws BaseServiceException {
    if (data.length != size) {
      CommonUtil.BadRequestException(
          "The AudioDataEntry.data {" + data.length + "} != size {" + size + "}");
    }
  }

  /**
   * Helper method to valid size and value of AudioDataEntry.data.
   *
   * @param data List&lt;Short>
   * @param iSize int
   * @throws BaseServiceException If size mismatch
   */
  public static void validateShortDataSize(final short[] data, final int iSize)
      throws BaseServiceException {
    if (data.length != iSize) {
      CommonUtil.BadRequestException(
          "The AudioDataEntry.data {" + data.length + "} != size {" + iSize + "}");
    }
  }

  /**
   * Helper method to valid size and value of AudioDataEntry.data.
   *
   * @param data List&lt;Byte>
   * @param size Long
   * @throws BaseServiceException If size mismatch
   */
  public static void validateDataSizeEx(final byte[] data, final int size)
      throws BaseServiceException {
    if (data.length != size) {
      CommonUtil.BadRequestException(
          "The FingerprintData {" + data.length + "} != size {" + size + "}");
    }
  }

  /**
   * Helper method to validate duration value of a fingerprintdata.
   *
   * @param duration Long
   * @throws BaseServiceException If duration < 1L or > 480L
   */
  public static void validateDuration(final Long duration) throws BaseServiceException {
    long value = duration.longValue();
    if (value < Constants.FINGERPRINT_MIN_DURATION) {
      CommonUtil.BadRequestException(
          "Fingerprint duration must be >= " + Constants.FINGERPRINT_MIN_DURATION);
    } else if (value > Constants.FINGERPRINT_MAX_DURATION) {
      CommonUtil.BadRequestException(
          "Fingerprint duration must be <= " + Constants.FINGERPRINT_MAX_DURATION);
    }
  }

  /**
   * Helper method to validate duration value of a fingerprintdata.
   *
   * @param duration Long
   * @throws BaseServiceException If duration < 7L or > 480L
   */
  public static void validateDurationEx(final Long duration) throws BaseServiceException {
    long value = duration.longValue();
    if (value < Constants.FINGERPRINT_MIN_SEVEN_DURATION) {
      CommonUtil.BadRequestException(
          "AudioData duration must be >= " + Constants.FINGERPRINT_MIN_SEVEN_DURATION);
    } else if (value > Constants.FINGERPRINT_MAX_DURATION) {
      CommonUtil.BadRequestException(
          "AudioData duration must be <= " + Constants.FINGERPRINT_MAX_DURATION);
    }
  }

  /**
   * Convert list of AudioStreamTrainingChannel into list of AudioStreamChannelInfo
   *
   * @param lists List of AudioStreamTrainingChannel
   * @return list of AudioStreamChannelInfo
   */
  public static final List<AudioStreamChannelInfo> trainingChannelToInfo(
      List<AudioStreamTrainingChannel> lists) {
    ArrayList<AudioStreamChannelInfo> instance = new ArrayList<>();
    if (lists != null && !lists.isEmpty()) {
      for (AudioStreamTrainingChannel list : lists) {
        AudioStreamChannelInfo info = new AudioStreamChannelInfo();
        info.setChannelId(list.getAcChannel());
        info.setOffset(list.getAcOffset());
        info.setDuration(list.getAcDuration());
        info.setCategory(list.getAcCategory());
        info.setTitle(list.getAcTitle());
        info.setUrl(list.getAcUrl());
        info.setPopup(list.getAcPopup());
        instance.add(info);
      }
    }
    return instance;
  }

  /**
   * Helper method to convert TuneUrlEntry into TuneUrlTag structure.
   *
   * @param entry TuneUrlEntry instance
   * @param score Double - if null, use {@com.tuneurl.webrtc.util.util.Converter#ZERO}
   * @param similarity Double - if null, use {@com.tuneurl.webrtc.util.util.Converter#ZERO}
   * @return TuneUrlTag instance
   */
  public static final TuneUrlTag convertToTuneUrlTag(final TuneUrlEntry entry, Long similarity) {
    TuneUrlTag instance = new TuneUrlTag();
    instance.setId(entry.getId());
    instance.setName(CommonUtil.getString(entry.getName(), Constants.AUDIOSTREAM_TITLE_SIZE));
    instance.setDescription(
        CommonUtil.getString(entry.getDescription(), Constants.AUDIOSTREAM_TITLE_SIZE));
    instance.setType(entry.getType());
    instance.setInfo(CommonUtil.getString(entry.getInfo(), Constants.AUDIOSTREAM_URL_SIZE));
    instance.setSimilarity(similarity != null ? similarity : 0L);
    return instance;
  }

  /** Default constructor. */
  private Converter() {
    // Hidden.
  }
}
