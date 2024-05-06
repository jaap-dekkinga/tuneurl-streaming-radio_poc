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
import org.springframework.http.HttpStatus;

/**
 * DTO to hold Status and Error messages.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public class Error {

  /**
   * The Status.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setStatus(), getStatus().
   */
  @JsonProperty("status")
  private String status;

  /**
   * The Messages.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setMessage(), getMessage().
   */
  @JsonProperty("message")
  private String message;

  /** Hidden default constructor. */
  @SuppressWarnings("unused")
  private Error() {
    // does nothing.
  }

  /** Constructor with String. */
  public Error(final HttpStatus status) {
    setStatus(status);
    setMessage("Error " + status.toString());
  }

  /** Constructor with String and String. */
  public Error(final HttpStatus status, final String message) {
    setStatus(status);
    setMessage(message);
  }

  /** Constructor with String. */
  public Error(final String message) {
    setStatus(HttpStatus.BAD_REQUEST);
    setMessage(message);
  }

  /**
   * Retrieves the Status.
   *
   * @return the Status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets the Status.
   *
   * @param status the Status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Sets the Status.
   *
   * @param status the Integer to set
   */
  public void setStatus(final Integer status) {
    this.status = status.toString();
  }

  /**
   * Sets the Status.
   *
   * @param status the HttpStatus to set
   */
  public void setStatus(final HttpStatus status) {
    this.status = status.toString();
  }

  /**
   * Retrieves the Messages.
   *
   * @return the Messages
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the Messages.
   *
   * @param message the Messages to set
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * To String.
   *
   * @return String
   */
  public String toString() {
    java.lang.StringBuffer sb = new java.lang.StringBuffer();
    sb.append("class Error {\n");
    sb.append("    \"status\": ").append('"').append(getStatus()).append("\",\n");
    sb.append("    \"message\": ").append('"').append(getMessage()).append("\"\n");
    sb.append("}\n");
    return sb.toString();
  }
}
