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
import java.util.List;

/**
 * Class FindFingerPrintResponse DTO to hold array of FingerprintCompareResponse
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public class FindFingerPrintResponse {

  /**
   * The actual FingerPrint counts.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setFingerPrintCount(), getFingerPrintCount().
   */
  @JsonProperty("count")
  private Long fingerPrintCount;

    /**
   * The array of FingetPrintOffset.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setFingerPrintCount(), getFingerPrintCount().
   */
  @JsonProperty("fingerPrint")
  private FingerprintCompareResponse fingerPrint;

  /** Default constructor for FindFingerPrintResponse class. */
  public FindFingerPrintResponse() {
    // does nothing.
  }

  /**
   * Retrieves the actual FingerprintCompareResponse counts.
   *
   * @return the actual FingerprintCompareResponse counts
   */
  public void setFingerPrintCount(Long fingerPrintCount) {
    this.fingerPrintCount = fingerPrintCount;
  }

  /**
   * Retrieves the FingerprintCompareResponse counts.
   *
   * @return the FingerprintCompareResponse counts
   */
  public Long getFingerPrintCount() {
    return fingerPrintCount;
  }

  /**
   * Retrieves the array of FingerprintCompareResponse.
   *
   * @return the list of FingerprintCompareResponse
   */
  public FingerprintCompareResponse getFingerPrint() {
    return fingerPrint;
  }

  /**
   * Sets the array of FingerprintCompareResponse.
   *
   * @param fingerPrints the array of FingerprintCompareResponse to set
   */
  public void setFingerPrint(FingerprintCompareResponse fingerPrint) {
    this.fingerPrint = fingerPrint;
  }

  /**
   * To String.
   *
   * @return String
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("class FindFingerPrintResponse {\n");
    sb.append("    \"fingerPrintCounts\": ").append(getFingerPrintCount()).append(",\n");
    sb.append("    \"fingerPrints\": ").append('"').append(getFingerPrint()).append("\"\n");
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
    sb.append("\"fingerPrintCounts\": ").append(getFingerPrintCount()).append(",");
    sb.append("\"fingerPrints\": ").append('"').append(getFingerPrint()).append("\"");
    sb.append("}");
    return sb.toString();
  }
}
