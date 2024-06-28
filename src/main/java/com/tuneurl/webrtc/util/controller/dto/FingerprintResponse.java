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
 * Class FingerprintResponse DTO to hold Fingerprint response.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public class FingerprintResponse {

  /**
   * The size.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setSize(), getSize().
   */
  @JsonProperty("size")
  private Long size;

  /**
   * The data.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setData(), getData().
   */
  @JsonProperty("dataEx")
  private String dataEx;

  @JsonProperty("data")
  private short[] data;

  /** Default constructor for FingerprintResponse class. */
  public FingerprintResponse() {
    // does nothing.
  }

  /**
   * Retrieves the size.
   *
   * @return the size
   */
  public Long getSize() {
    return size;
  }

  /**
   * Sets the size.
   *
   * @param size the size to set
   */
  public void setSize(Long size) {
    this.size = size;
  }

  /**
   * Retrieves the data.
   *
   * @return the data
   */
  public String getDataEx() {
    return dataEx;
  }

  /**
   * Sets the data.
   *
   * @param dataEx the data to set
   */
  public void setDataEx(String dataEx) {
    this.dataEx = dataEx;
  }

  /**
   * Sets the data.
   *
   * @param data the data to set
   */
  public void setData(short[] data) {
    this.data = data;
  }

  /**
   * Retrieves the data.
   *
   * @return the data
   */
  public short[] getData() {
    return data;
  }

  /**
   * To String.
   *
   * @return String
   */
  public String toString() {
    java.lang.StringBuffer sb = new java.lang.StringBuffer();
    sb.append("class FingerprintResponse {\n");
    sb.append("    \"size\": ").append(getSize()).append(",\n");
    sb.append("    \"data\": ").append('"').append(getData()).append("\"\n");
    sb.append("}\n");
    return sb.toString();
  }

  /**
   * To String.
   *
   * @return String
   */
  public String toJson() {
    java.lang.StringBuffer sb = new java.lang.StringBuffer();
    sb.append("{");
    sb.append("\"size\": ").append(getSize()).append(",");
    sb.append("\"dataEx\": ").append('"').append(getDataEx()).append("\"");
    sb.append("}");
    return sb.toString();
  }
}
