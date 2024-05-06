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

package com.tuneurl.webrtc.util.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class AudioStreamData DTO to hold result of Audio Stream file capture.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public class AudioStreamData {

  /**
   * The conversion reference ID.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setConversionId(), getConversionId().
   */
  @JsonProperty("conversionId")
  private Long conversionId;

  /**
   * The Audio Stream URL with 5 seconds duration.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setFiveSecondAudioUrl(), getFiveSecondAudioUrl().
   */
  @JsonProperty("fiveSecondAudioUrl")
  private String fiveSecondAudioUrl;

  /**
   * The Audio Stream URL with more than 600 seconds duration.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setFinalAudioStreamUrl(), getFinalAudioStreamUrl().
   */
  @JsonProperty("finalAudioStreamUrl")
  private String finalAudioStreamUrl;

  /**
   * The actual duration time in seconds.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setDuration(), getDuration().
   */
  @JsonProperty("duration")
  private Long duration;

  /**
   * The conversion completion status.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setStatus(), getStatus().
   */
  @JsonProperty("status")
  private Integer status;

  /** Default constructor for AudioStreamData class. */
  public AudioStreamData() {
    // does nothing.
  }

  /**
   * Retrieves the conversion reference ID.
   *
   * @return the conversion reference ID
   */
  public Long getConversionId() {
    return conversionId;
  }

  /**
   * Sets the conversion reference ID.
   *
   * @param conversionId the conversion reference ID to set
   */
  public void setConversionId(Long conversionId) {
    this.conversionId = conversionId;
  }

  /**
   * Retrieves the Audio Stream URL with 5 seconds duration.
   *
   * @return the Audio Stream URL with 5 seconds duration
   */
  public String getFiveSecondAudioUrl() {
    return fiveSecondAudioUrl;
  }

  /**
   * Sets the Audio Stream URL with 5 seconds duration.
   *
   * @param fiveSecondAudioUrl the Audio Stream URL with 5 seconds duration to set
   */
  public void setFiveSecondAudioUrl(String fiveSecondAudioUrl) {
    this.fiveSecondAudioUrl = fiveSecondAudioUrl;
  }

  /**
   * Retrieves the Audio Stream URL with more than 600 seconds duration.
   *
   * @return the Audio Stream URL with more than 600 seconds duration
   */
  public String getFinalAudioStreamUrl() {
    return finalAudioStreamUrl;
  }

  /**
   * Sets the Audio Stream URL with more than 600 seconds duration.
   *
   * @param finalAudioStreamUrl the Audio Stream URL with more than 600 seconds duration to set
   */
  public void setFinalAudioStreamUrl(String finalAudioStreamUrl) {
    this.finalAudioStreamUrl = finalAudioStreamUrl;
  }

  /**
   * Retrieves the actual duration time in seconds.
   *
   * @return the actual duration time in seconds
   */
  public Long getDuration() {
    return duration;
  }

  /**
   * Sets the actual duration time in seconds.
   *
   * @param duration the actual duration time in seconds to set
   */
  public void setDuration(Long duration) {
    this.duration = duration;
  }

  /**
   * Retrieves the conversion completion status.
   *
   * @return the conversion completion status
   */
  public Integer getStatus() {
    return status;
  }

  /**
   * Sets the conversion completion status.
   *
   * @param status the conversion completion status to set
   */
  public void setStatus(Integer status) {
    this.status = status;
  }

  /**
   * To String.
   *
   * @return String
   */
  public String toString() {
    java.lang.StringBuffer sb = new java.lang.StringBuffer();
    sb.append("class AudioStreamData {\n");
    sb.append("    \"conversionId\": ").append(getConversionId()).append(",\n");
    sb.append("    \"fiveSecondAudioUrl\": ")
        .append('"')
        .append(getFiveSecondAudioUrl())
        .append("\",\n");
    sb.append("    \"finalAudioStreamUrl\": ")
        .append('"')
        .append(getFinalAudioStreamUrl())
        .append("\",\n");
    sb.append("    \"duration\": ").append(getDuration()).append(",\n");
    sb.append("    \"status\": ").append(getStatus()).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
