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
import java.time.LocalDateTime;

/**
 * Class UserData DTO to hold TuneUrl User data.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public class UserData {

  /**
   * The UUID.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setUid(), getUid().
   */
  @JsonProperty("uid")
  private String uid;

  /**
   * The Email.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setEmail(), getEmail().
   */
  @JsonProperty("email")
  private String email;

  /**
   * The Reg.Date.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setRegistered(), getRegistered().
   */
  @JsonProperty("registered")
  private LocalDateTime registered;

  /**
   * The User Aliad.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setAlias(), getAlias().
   */
  @JsonProperty("alias")
  private String alias;

  /** Default constructor for UserData class. */
  public UserData() {
    // does nothing.
  }

  /**
   * Retrieves the UUID.
   *
   * @return the UUID
   */
  public String getUid() {
    return uid;
  }

  /**
   * Sets the UUID.
   *
   * @param uid the UUID to set
   */
  public void setUid(String uid) {
    this.uid = uid;
  }

  /**
   * Retrieves the Email.
   *
   * @return the Email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the Email.
   *
   * @param email the Email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Retrieves the Reg.
   *
   * @return the Reg
   */
  public LocalDateTime getRegistered() {
    return registered;
  }

  /**
   * Sets the Reg.
   *
   * @param registered the Reg to set
   */
  public void setRegistered(LocalDateTime registered) {
    this.registered = registered;
  }

  /**
   * Retrieves the User Aliad.
   *
   * @return the User Aliad
   */
  public String getAlias() {
    return alias;
  }

  /**
   * Sets the User Aliad.
   *
   * @param alias the User Aliad to set
   */
  public void setAlias(String alias) {
    this.alias = alias;
  }

  /**
   * To String.
   *
   * @return String
   */
  public String toString() {
    java.lang.StringBuffer sb = new java.lang.StringBuffer();
    sb.append("class UserData {\n");
    sb.append("    \"uid\": ").append('"').append(getUid()).append("\",\n");
    sb.append("    \"email\": ").append('"').append(getEmail()).append("\",\n");
    sb.append("    \"registered\": ").append('"').append(getRegistered()).append("\",\n");
    sb.append("    \"alias\": ").append('"').append(getAlias()).append("\"\n");
    sb.append("}\n");
    return sb.toString();
  }
}
