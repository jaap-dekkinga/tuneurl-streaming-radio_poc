/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2024, TuneURL Inc.
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
 * Class AudioStreamEntry DTO to hold Audio Stream URL and capture time duration.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public class AudioStreamEntry {

  /**
   * The Audio Stream URL.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setUrl(), getUrl().
   */
  @JsonProperty("Url")
  private String url;

  /**
   * The duration time to capture.
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
   * The sample rate of audio in PCM16 format.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setSampleRate(), getSampleRate().
   */
  @JsonProperty("sampleRate")
  private Long sampleRate;

  /** Default constructor for AudioStreamEntry class. */
  public AudioStreamEntry() {
    // does nothing.
  }

  /**
   * Retrieves the Audio Stream URL.
   *
   * @return the Audio Stream URL
   */
  public String getUrl() {
    return url;
  }

  /**
   * Sets the Audio Stream URL.
   *
   * @param url the Audio Stream URL to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Retrieves the duration time to capture.
   *
   * @return the duration time to capture
   */
  public Long getDuration() {
    return duration;
  }

  /**
   * Sets the duration time to capture.
   *
   * @param duration the duration time to capture to set
   */
  public void setDuration(Long duration) {
    this.duration = duration;
  }

  /**
   * Retrieves the sample rate of audio in PCM16 format.
   *
   * @return the sample rate of audio in PCM16 format
   */
  public Long getSampleRate() {
    return sampleRate;
  }

  /**
   * Sets the sample rate of audio in PCM16 format.
   *
   * @param sampleRate the sample rate of audio in PCM16 format to set
   */
  public void setSampleRate(Long sampleRate) {
    this.sampleRate = sampleRate;
  }

  /**
   * To String.
   *
   * @return String
   */
  public String toString() {
    java.lang.StringBuffer sb = new java.lang.StringBuffer();
    sb.append("class AudioStreamEntry {\n");
    sb.append("    \"Url\": ").append('"').append(getUrl()).append("\",\n");
    sb.append("    \"duration\": ").append(getDuration()).append(",\n");
    sb.append("    \"sampleRate\": ").append(getSampleRate()).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
