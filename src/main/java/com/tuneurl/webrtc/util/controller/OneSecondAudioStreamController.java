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
import com.tuneurl.webrtc.util.util.*;
import com.tuneurl.webrtc.util.value.Constants;
import com.tuneurl.webrtc.util.value.UserType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    // EvaluateAudioStreamResponse cachedResult =
    //     this.redis.getOneSecondAudioStreamCache(
    //         httpRequest.getParameter("offset"),
    //         evaluateAudioStreamEntry.getAudioData().getUrl(),
    //         evaluateAudioStreamEntry.getDataFingerprint());
    // if (cachedResult != null) {
    //   return ResponseEntity.ok().body(cachedResult);
    // }

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
    // byte[] dataFingerprint = evaluateAudioStreamEntry.getDataFingerprint();\
    String dataFingerprint = evaluateAudioStreamEntry.getDataFingerprint();
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
          "fingerprintData=", dataFingerprint,
        });

    // 1. Check for ADMIN or USER role.
    if (!super.canAccessAudioWithoutLogin()) {
      super.getSdkClientCredentials(signature, UserType.LOGIN_FOR_USER, httpRequest, httpResponse);
    }

    Converter.checkAudioDataEntryDataSize(audioDataEntry);
    Converter.validateShortDataSize(data, size);

    final String fileName = Converter.validateUrlOrGencrc32(url);
    ProcessHelper.checkNullOrEmptyString(fileName, "AudioDataEntry.Url");
    if (duration < 2L || duration > 17L) {
      CommonUtil.BadRequestException("Duration must be 6 to 17 seconds only");
    }

    EvaluateAudioStreamResponse response =
        audioStreamBaseService.evaluateOneSecondAudioStream(
            duration, dataOffset, data, fingerprintRate, dataFingerprint);

    // this.redis.setOneSecondAudioStreamCache(
    //     httpRequest.getParameter("offset"),
    //     evaluateAudioStreamEntry.getAudioData().getUrl(),
    //     evaluateAudioStreamEntry.getDataFingerprint(),
    //     response);

    return ResponseEntity.ok().body(response);
  }
}
