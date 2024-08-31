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
import java.util.List;

/**
 * Class EvaluateAudioStreamResponse DTO to hold array of TuneUrlTag
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public class EvaluateAudioStreamResponse {

  /**
   * The actual TuneUrlTag counts.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setTuneUrlCounts(), getTuneUrlCounts().
   */
  @JsonProperty("tuneUrlCounts")
  private Long tuneUrlCounts;

  /**
   * The TuneUrlTag counts.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setTagCounts(), getTagCounts().
   */
  @JsonProperty("tagCounts")
  private Long tagCounts;

  /**
   * The array of TuneUrlTag.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setLiveTags(), getLiveTags().
   */
  @JsonProperty("liveTags")
  private List<TuneUrlTag> liveTags;

  /** Default constructor for EvaluateAudioStreamResponse class. */
  public EvaluateAudioStreamResponse() {
    // does nothing.
  }

  /**
   * Retrieves the actual TuneUrlTag counts.
   *
   * @return the actual TuneUrlTag counts
   */
  public Long getTuneUrlCounts() {
    return tuneUrlCounts;
  }

  /**
   * Sets the actual TuneUrlTag counts.
   *
   * @param tuneUrlCounts the actual TuneUrlTag counts to set
   */
  public void setTuneUrlCounts(Long tuneUrlCounts) {
    this.tuneUrlCounts = tuneUrlCounts;
  }

  /**
   * Retrieves the TuneUrlTag counts.
   *
   * @return the TuneUrlTag counts
   */
  public Long getTagCounts() {
    return tagCounts;
  }

  /**
   * Sets the TuneUrlTag counts.
   *
   * @param tagCounts the TuneUrlTag counts to set
   */
  public void setTagCounts(Long tagCounts) {
    this.tagCounts = tagCounts;
  }

  /**
   * Retrieves the array of TuneUrlTag.
   *
   * @return the list of TuneUrlTag
   */
  public List<TuneUrlTag> getLiveTags() {
    return liveTags;
  }

  /**
   * Sets the array of TuneUrlTag.
   *
   * @param liveTags the array of TuneUrlTag to set
   */
  public void setLiveTags(List<TuneUrlTag> liveTags) {
    this.liveTags = liveTags;
  }

  /**
   * To String.
   *
   * @return String
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("class EvaluateAudioStreamResponse {\n");
    sb.append("    \"tuneUrlCounts\": ").append(getTuneUrlCounts()).append(",\n");
    sb.append("    \"tagCounts\": ").append(getTagCounts()).append(",\n");
    sb.append("    \"liveTags\": ").append('"').append(getLiveTags()).append("\"\n");
    sb.append("}\n");
    return sb.toString();
  }

  /**
   * To String.
   *
   * @return String
   */
  public String toJSON() {
    StringBuffer sb = new StringBuffer();
    sb.append("{");
    sb.append("\"tuneUrlCounts\": ").append(getTuneUrlCounts()).append(",");
    sb.append("\"tagCounts\": ").append(getTagCounts()).append(",");
    sb.append("\"liveTags\": ").append('"').append(getLiveTags()).append("\"");
    sb.append("}");
    return sb.toString();
  }
}
