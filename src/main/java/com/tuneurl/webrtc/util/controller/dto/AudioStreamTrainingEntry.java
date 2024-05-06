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
 * Class AudioStreamTrainingEntry DTO to hold Audio Stream Training data.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public class AudioStreamTrainingEntry {

  /**
   * The Channel ID.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setChannel(), getChannel().
   */
  @JsonProperty("channel")
  private Long channel;

  /**
   * The Offset.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setOffset(), getOffset().
   */
  @JsonProperty("offset")
  private Long offset;

  /**
   * The Capture Duration.
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
   * The Category.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setCategory(), getCategory().
   */
  @JsonProperty("category")
  private String category;

  /**
   * The Title.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setTitle(), getTitle().
   */
  @JsonProperty("title")
  private String title;

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
   * The Popup messages.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setPopup(), getPopup().
   */
  @JsonProperty("popup")
  private String popup;

  /** Default constructor for AudioStreamTrainingEntry class. */
  public AudioStreamTrainingEntry() {
    // does nothing.
  }

  /**
   * Retrieves the Channel ID.
   *
   * @return the Channel ID
   */
  public Long getChannel() {
    return channel;
  }

  /**
   * Sets the Channel ID.
   *
   * @param channel the Channel ID to set
   */
  public void setChannel(Long channel) {
    this.channel = channel;
  }

  /**
   * Retrieves the Offset.
   *
   * @return the Offset
   */
  public Long getOffset() {
    return offset;
  }

  /**
   * Sets the Offset.
   *
   * @param offset the Offset to set
   */
  public void setOffset(Long offset) {
    this.offset = offset;
  }

  /**
   * Retrieves the Capture Duration.
   *
   * @return the Capture Duration
   */
  public Long getDuration() {
    return duration;
  }

  /**
   * Sets the Capture Duration.
   *
   * @param duration the Capture Duration to set
   */
  public void setDuration(Long duration) {
    this.duration = duration;
  }

  /**
   * Retrieves the Category.
   *
   * @return the Category
   */
  public String getCategory() {
    return category;
  }

  /**
   * Sets the Category.
   *
   * @param category the Category to set
   */
  public void setCategory(String category) {
    this.category = category;
  }

  /**
   * Retrieves the Title.
   *
   * @return the Title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the Title.
   *
   * @param title the Title to set
   */
  public void setTitle(String title) {
    this.title = title;
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
   * Retrieves the Popup messages.
   *
   * @return the Popup messages
   */
  public String getPopup() {
    return popup;
  }

  /**
   * Sets the Popup messages.
   *
   * @param popup the Popup messages to set
   */
  public void setPopup(String popup) {
    this.popup = popup;
  }

  /**
   * To String.
   *
   * @return String
   */
  public String toString() {
    java.lang.StringBuffer sb = new java.lang.StringBuffer();
    sb.append("class AudioStreamTrainingEntry {\n");
    sb.append("    \"channel\": ").append(getChannel()).append(",\n");
    sb.append("    \"offset\": ").append(getOffset()).append(",\n");
    sb.append("    \"duration\": ").append(getDuration()).append(",\n");
    sb.append("    \"category\": ").append('"').append(getCategory()).append("\",\n");
    sb.append("    \"title\": ").append('"').append(getTitle()).append("\",\n");
    sb.append("    \"Url\": ").append('"').append(getUrl()).append("\",\n");
    sb.append("    \"popup\": ").append('"').append(getPopup()).append("\"\n");
    sb.append("}\n");
    return sb.toString();
  }
}
