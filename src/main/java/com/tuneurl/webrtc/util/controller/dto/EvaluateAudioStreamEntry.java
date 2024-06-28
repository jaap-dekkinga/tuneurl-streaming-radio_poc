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
 * Class EvaluateAudioStreamEntry DTO to hold AudioDataEntry and Trigger sound fingerprint.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public class EvaluateAudioStreamEntry {

  /**
   * The Audio bits.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setAudioData(), getAudioData().
   */
  @JsonProperty("audioData")
  private AudioDataEntry audioData;

  /**
   * The Triggersound Fingerprint Data.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setDataFingerprint(), getDataFingerprint().
   */
  @JsonProperty("dataFingerprint")
  private String dataFingerprint;

  /** Default constructor for EvaluateAudioStreamEntry class. */
  public EvaluateAudioStreamEntry() {
    // does nothing.
  }

  /**
   * Retrieves the Audio bits.
   *
   * @return the Audio bits
   */
  public AudioDataEntry getAudioData() {
    return audioData;
  }

  /**
   * Sets the Audio bits.
   *
   * @param audioData the Audio bits to set
   */
  public void setAudioData(AudioDataEntry audioData) {
    this.audioData = audioData;
  }

  /**
   * Retrieves the Triggersound Fingerprint Data.
   *
   * @return the Triggersound Fingerprint Data
   */
  public String getDataFingerprint() {
    return dataFingerprint;
  }

  /**
   * Sets the Triggersound Fingerprint Data.
   *
   * @param dataFingerprint the Triggersound Fingerprint Data to set
   */
  public void setDataFingerprint(String dataFingerprint) {
    this.dataFingerprint = dataFingerprint;
  }

  /**
   * To String.
   *
   * @return String
   */
  public String toString() {
    java.lang.StringBuffer sb = new java.lang.StringBuffer();
    sb.append("class EvaluateAudioStreamEntry {\n");
    sb.append("    \"audioData\": ").append('"').append(getAudioData()).append("\",\n");
    sb.append("    \"dataFingerprint\": ").append('"').append(getDataFingerprint()).append("\",\n");
    sb.append("}\n");
    return sb.toString();
  }
}
