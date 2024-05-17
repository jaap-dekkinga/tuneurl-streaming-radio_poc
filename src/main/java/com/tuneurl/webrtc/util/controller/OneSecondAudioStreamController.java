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

import com.tuneurl.webrtc.util.controller.dto.AudioDataEntry;
import com.tuneurl.webrtc.util.controller.dto.EvaluateAudioStreamEntry;
import com.tuneurl.webrtc.util.controller.dto.EvaluateAudioStreamResponse;
import com.tuneurl.webrtc.util.controller.dto.FingerprintCollection;
import com.tuneurl.webrtc.util.controller.dto.FingerprintCompareResponse;
import com.tuneurl.webrtc.util.controller.dto.FingerprintResponse;
import com.tuneurl.webrtc.util.controller.dto.TuneUrlTag;
import com.tuneurl.webrtc.util.util.*;
import com.tuneurl.webrtc.util.util.fingerprint.FingerprintThreadCollector;
import com.tuneurl.webrtc.util.util.fingerprint.FingerprintUtility;
import com.tuneurl.webrtc.util.value.Constants;
import com.tuneurl.webrtc.util.value.UserType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OneSecondAudioStreamController.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
@RestController
@RequestMapping("/")
public class OneSecondAudioStreamController extends BaseController {

  /** Default constructor . */
  public OneSecondAudioStreamController() {}

  /**
   * Helper to update the audio segment after the trigger sound location.
   *
   * @param dataOffset long
   * @param logger MessageLogger
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
      MessageLogger logger,
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
    FingerprintResponse fr;
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

          final String payload = FingerprintUtility.convertFingerprintToString(fr.getData());
          tag.setDescription(payload);
          return tag;
        }
      }
    }
    return null;
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
      path = "/dev/v3/evaluateOneSecondAudioStream",
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
  public ResponseEntity<EvaluateAudioStreamResponse> evaluateOneSecondAudioStream(
      @Valid @RequestBody EvaluateAudioStreamEntry evaluateAudioStreamEntry,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {
    final String signature = "evaluateOneSecondAudioStream";
    final String signature2 = "evaluateOneSecondAudioStream:Pruning";
    String sOffset = httpRequest.getParameter("offset");
    Long dataOffset = CommonUtil.parseLong(sOffset, 0L);
    super.saveAnalytics(signature, httpRequest);
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
          "offset=", dataOffset,
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
    StringBuffer dataFingerprintBuffer =
        FingerprintUtility.getFingerprintBufferedPart(dataFingerprint, dataFingerprint.length);
    int dataFingerprintBufferSize = dataFingerprint.length;

    final String fileName = Converter.validateUrlOrGencrc32(url);
    ProcessHelper.checkNullOrEmptyString(fileName, "AudioDataEntry.Url");
    if (duration < 6L || duration > 17L) {
      CommonUtil.BadRequestException("Duration must be 6 to 17 seconds only");
    }
    EvaluateAudioStreamResponse response = new EvaluateAudioStreamResponse();
    List<TuneUrlTag> liveTags = new ArrayList<TuneUrlTag>();
    List<TuneUrlTag> tags;
    long elapse;
    long maxDuration = Converter.muldiv(1000, duration, 1L);
    long count, counts = Converter.muldiv(1000, duration, 100);
    long durationLimit = dataOffset + Converter.muldiv(1000, duration - 5L, 1L);
    FingerprintCompareResponse fcr;
    FingerprintResponse fr;
    boolean isDebugOn = Constants.DEBUG_FINGERPRINTING;
    String rootDir = audioStreamBaseService.getSaveAudioFilesFolder(null);
    String debugDir = String.format("%s/%s", rootDir, "debug");
    if (isDebugOn) {
      ProcessHelper.makeDir(debugDir);
    }
    Random random = new Random();
    random.setSeed(new Date().getTime());

    LinkedList<FingerprintThreadCollector> fingerprintThreadList =
        new LinkedList<FingerprintThreadCollector>();
    List<Thread> threadList = new LinkedList<Thread>();
    for (count = 0L, elapse = 0L; count < counts && elapse < maxDuration; count++, elapse += 100L) {
      FingerprintThreadCollector fingerprintThread =
          new FingerprintThreadCollector(
              rootDir,
              data,
              elapse,
              random,
              fingerprintRate,
              dataFingerprintBuffer,
              dataFingerprintBufferSize);
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

    for (count = 0L, elapse = 0L; count < counts && elapse < maxDuration; count++, elapse += 100L) {
      FingerprintCollection result =
          fingerprintThreadList.removeFirst().getFingerprintCollectionResult();

      List<FingerprintResponse> frSelection = result.getFrCollection();
      List<FingerprintCompareResponse> selection = result.getFcrCollection();
      fcr = null;
      fr = null;
      if (selection.size() == 5) {
        Object[] fingerprintComparisonsResponse =
            audioStreamBaseService.fingerprintComparisons(selection, frSelection, fcr, fr);
        fcr = (FingerprintCompareResponse) fingerprintComparisonsResponse[0];
        fr = (FingerprintResponse) fingerprintComparisonsResponse[1];

        if (null != fcr) {
          TuneUrlTag tag = FingerprintUtility.newTag(true, dataOffset, fr, fcr);
          if (isDebugOn) {
            FingerprintUtility.displayLiveTagsEx(signature, logger, tag);
          }
          if (tag.getDataPosition() > durationLimit) break;
          liveTags.add(tag);
        }
      } // if (selection.size() == 5)
    } // for (count = 0L, ...)
    if (!liveTags.isEmpty()) {
      tags = FingerprintUtility.pruneTagsEx(isDebugOn, logger, liveTags);
      if (isDebugOn) {
        logger.logExit(signature2, "before=", liveTags.size(), "after=", tags.size());
      }
      liveTags = new ArrayList<TuneUrlTag>();
      for (TuneUrlTag tag : tags) {
        if (isDebugOn) {
          logger.logExit(
              signature,
              new Object[] {
                "dataPosition=",
                tag.getDataPosition(),
                "durationLimit=",
                durationLimit,
                "maxDuration=",
                dataOffset + maxDuration,
                "Frame=",
                tag.getMostSimilarFramePosition(),
              });
        }
        tag =
            updatePayload(
                dataOffset, logger, random, rootDir, fingerprintRate, tag, data, maxDuration);
        if (tag != null) {
          liveTags.add(tag);
          if (isDebugOn) {
            FingerprintUtility.displayLiveTagsEx(signature2, logger, tag);
          }
        }
      }
    }
    counts = (long) liveTags.size();
    response.setTagCounts(counts);
    response.setLiveTags(liveTags);
    response.setTuneUrlCounts((long) liveTags.size());
    if (isDebugOn) {
      FingerprintUtility.displayLiveTags(signature, logger, liveTags);
    }
    logger.logExit(
        signature,
        new Object[] {
          "counts=", counts, "liveTags.size", liveTags.size(), "durationLimit=", durationLimit
        });
    return ResponseEntity.ok().body(response);
  }
}
