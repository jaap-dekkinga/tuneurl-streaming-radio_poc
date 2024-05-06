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
 * SDK User entry.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public class SdkUserEntry {

  /** User Email. */
  @JsonProperty("email")
  private String email;

  /** User Name. */
  @JsonProperty("fullname")
  private String fullname;

  /** User Roles. */
  @JsonProperty("roles")
  private String roles;

  /** Default constructor. */
  public SdkUserEntry() {
    // Does nothing.
  }

  /**
   * Retrieves the User Email.
   *
   * @return the User Email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the User Email.
   *
   * @param email the User Email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Retrieves the User Name.
   *
   * @return the User Name
   */
  public String getFullname() {
    return fullname;
  }

  /**
   * Sets the User Name.
   *
   * @param fullname the User Name to set
   */
  public void setFullname(String fullname) {
    this.fullname = fullname;
  }

  /**
   * Retrieves the User Roles.
   *
   * @return the User Roles
   */
  public String getRoles() {
    return roles;
  }

  /**
   * Sets the User Roles.
   *
   * @param roles the User Roles to set
   */
  public void setRoles(String roles) {
    this.roles = roles;
  }
}
