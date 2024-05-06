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
 * Class FingerprintCompareResponse DTO to hold Fingerprint comparision results.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public class FingerprintCompareResponse {

  /**
   * The Similarity position.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setMostSimilarFramePosition(), getMostSimilarFramePosition().
   */
  @JsonProperty("mostSimilarFramePosition")
  private Integer mostSimilarFramePosition;

  /**
   * The Start time.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setMostSimilarStartTime(), getMostSimilarStartTime().
   */
  @JsonProperty("mostSimilarStartTime")
  private Double mostSimilarStartTime;

  /**
   * The Score.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setScore(), getScore().
   */
  @JsonProperty("score")
  private Double score;

  /**
   * The Similarity.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setSimilarity(), getSimilarity().
   */
  @JsonProperty("similarity")
  private Double similarity;

  /**
   * The Offset milli-seconds.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setOffset(), getOffset().
   */
  @JsonProperty("offset")
  private Long offset;

  /** Default constructor for FingerprintCompareResponse class. */
  public FingerprintCompareResponse() {
    // does nothing.
  }

  /**
   * Retrieves the Similarity position.
   *
   * @return the Similarity position
   */
  public Integer getMostSimilarFramePosition() {
    return mostSimilarFramePosition;
  }

  /**
   * Sets the Similarity position.
   *
   * @param mostSimilarFramePosition the Similarity position to set
   */
  public void setMostSimilarFramePosition(Integer mostSimilarFramePosition) {
    this.mostSimilarFramePosition = mostSimilarFramePosition;
  }

  /**
   * Retrieves the Start time.
   *
   * @return the Start time
   */
  public Double getMostSimilarStartTime() {
    return mostSimilarStartTime;
  }

  /**
   * Sets the Start time.
   *
   * @param mostSimilarStartTime the Start time to set
   */
  public void setMostSimilarStartTime(Double mostSimilarStartTime) {
    this.mostSimilarStartTime = mostSimilarStartTime;
  }

  /**
   * Retrieves the Score.
   *
   * @return the Score
   */
  public Double getScore() {
    return score;
  }

  /**
   * Sets the Score.
   *
   * @param score the Score to set
   */
  public void setScore(Double score) {
    this.score = score;
  }

  /**
   * Retrieves the Similarity.
   *
   * @return the Similarity
   */
  public Double getSimilarity() {
    return similarity;
  }

  /**
   * Sets the Similarity.
   *
   * @param similarity the Similarity to set
   */
  public void setSimilarity(Double similarity) {
    this.similarity = similarity;
  }

  /**
   * Retrieves the Offset milli-seconds.
   *
   * @return the Offset milli-seconds
   */
  public Long getOffset() {
    return offset;
  }

  /**
   * Sets the Offset milli-seconds.
   *
   * @param offset the Offset milli-seconds to set
   */
  public void setOffset(Long offset) {
    this.offset = offset;
  }

  /**
   * To String.
   *
   * @return String
   */
  public String toString() {
    java.lang.StringBuffer sb = new java.lang.StringBuffer();
    sb.append("class FingerprintCompareResponse {\n");
    sb.append("    \"mostSimilarFramePosition\": ")
        .append(getMostSimilarFramePosition())
        .append(",\n");
    sb.append("    \"mostSimilarStartTime\": ")
        .append('"')
        .append(getMostSimilarStartTime())
        .append("\",\n");
    sb.append("    \"score\": ").append('"').append(getScore()).append("\",\n");
    sb.append("    \"similarity\": ").append('"').append(getSimilarity()).append("\",\n");
    sb.append("    \"offset\": ").append(getOffset()).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
