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
 * Class TuneUrlTag DTO to hold Fingerprint comparison scores and the Search fingerprint API
 * results.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public class TuneUrlTag {

  /**
   * The TuneUrl ID.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setId(), getId().
   */
  @JsonProperty("id")
  private Long id;

  /**
   * The Artist name.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setName(), getName().
   */
  @JsonProperty("name")
  private String name;

  /**
   * The Description.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setDescription(), getDescription().
   */
  @JsonProperty("description")
  private String description;

  /**
   * The open_page save_page etc.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setType(), getType().
   */
  @JsonProperty("type")
  private String type;

  /**
   * The URL.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setInfo(), getInfo().
   */
  @JsonProperty("info")
  private String info;

  /**
   * The Trigger similarity.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setSimilarity(), getSimilarity().
   */
  @JsonProperty("similarity")
  private Long similarity;

  /**
   * The Index.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setIndex(), getIndex().
   */
  @JsonProperty("index")
  private Long index;

  /**
   * The Data position.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setDataPosition(), getDataPosition().
   */
  @JsonProperty("dataPosition")
  private Long dataPosition;

  /** Default constructor for TuneUrlTag class. */
  public TuneUrlTag() {
    // does nothing.
  }

  /**
   * Retrieves the TuneUrl ID.
   *
   * @return the TuneUrl ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the TuneUrl ID.
   *
   * @param id the TuneUrl ID to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Retrieves the Artist name.
   *
   * @return the Artist name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the Artist name.
   *
   * @param name the Artist name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Retrieves the Description.
   *
   * @return the Description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the Description.
   *
   * @param description the Description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Retrieves the open_page save_page etc.
   *
   * @return the open_page save_page etc
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the open_page save_page etc.
   *
   * @param type the open_page save_page etc to set
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Retrieves the URL.
   *
   * @return the URL
   */
  public String getInfo() {
    return info;
  }

  /**
   * Sets the URL.
   *
   * @param info the URL to set
   */
  public void setInfo(String info) {
    this.info = info;
  }

  /**
   * Retrieves the Trigger similarity.
   *
   * @return the Trigger similarity
   */
  public Long getSimilarity() {
    return similarity;
  }

  /**
   * Sets the Trigger similarity.
   *
   * @param similarity the Trigger similarity to set
   */
  public void setSimilarity(Long similarity) {
    this.similarity = similarity;
  }

  /**
   * Retrieves the Index.
   *
   * @return the Index
   */
  public Long getIndex() {
    return index;
  }

  /**
   * Sets the Index.
   *
   * @param index the Index to set
   */
  public void setIndex(Long index) {
    this.index = index;
  }

  /**
   * Retrieves the Data position.
   *
   * @return the Data position
   */
  public Long getDataPosition() {
    return dataPosition;
  }

  /**
   * Sets the Data position.
   *
   * @param dataPosition the Data position to set
   */
  public void setDataPosition(Long dataPosition) {
    this.dataPosition = dataPosition;
  }

  public void setFingerprintCompareResponseData(
      FingerprintCompareResponse fcr, boolean shouldSetOffset) {
    if (shouldSetOffset) {
      this.setDataPosition(fcr.getOffset());
    }
    this.setSimilarity(fcr.getSimilarity());
  }

  public void setTuneUrlEntryData(TuneUrlEntry entry) {
    this.setId(entry.getId());
    this.setName(entry.getName());
    this.setDescription(entry.getDescription());
    this.setType(entry.getType());
    this.setInfo(entry.getInfo());
  }

  public void setTuneUrlEmptyEntryData(String payload) {
    this.setId(0L);
    this.setName("");
    this.setDescription(payload);
    this.setType("open_page");
    this.setInfo("");
  }

  /**
   * To String.
   *
   * @return String
   */
  public String toString() {
    java.lang.StringBuffer sb = new java.lang.StringBuffer();
    sb.append("class TuneUrlTag {\n");
    sb.append("    \"id\": ").append(getId()).append(",\n");
    sb.append("    \"name\": ").append('"').append(getName()).append("\",\n");
    sb.append("    \"description\": ").append('"').append(getDescription()).append("\",\n");
    sb.append("    \"type\": ").append('"').append(getType()).append("\",\n");
    sb.append("    \"info\": ").append('"').append(getInfo()).append("\",\n");
    sb.append("    \"similarity\": ").append('"').append(getSimilarity()).append("\",\n");
    sb.append("    \"index\": ").append(getIndex()).append(",\n");
    sb.append("    \"dataPosition\": ").append(getDataPosition()).append("\n");
    sb.append("}\n");
    return sb.toString();
  }

  /**
   * To String.
   *
   * @return String
   */
  public String toJsonWithoutDescription() {
    java.lang.StringBuffer sb = new java.lang.StringBuffer();
    sb.append("{");
    sb.append("    \"id\": ").append(getId()).append(",");
    sb.append("    \"name\": ").append('"').append(getName()).append("\",");
    sb.append("    \"type\": ").append('"').append(getType()).append("\",");
    sb.append("    \"info\": ").append('"').append(getInfo()).append("\",");
    sb.append("    \"similarity\": ").append('"').append(getSimilarity()).append("\",");
    sb.append("    \"index\": ").append(getIndex()).append(",");
    sb.append("    \"dataPosition\": ").append(getDataPosition());
    sb.append("}");
    return sb.toString();
  }
}
