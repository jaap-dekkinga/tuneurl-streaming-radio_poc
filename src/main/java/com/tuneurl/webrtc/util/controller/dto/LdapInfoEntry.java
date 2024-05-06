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
 * Simulated LDAP Info entry.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public class LdapInfoEntry {

  /** LDAP Password. */
  @JsonProperty("password")
  private String password;

  /** LDAP UUID. */
  @JsonProperty("uuid")
  private String uuid;

  /** LDAP Status. */
  @JsonProperty("status")
  private Long status;

  /** Default constructor. */
  public LdapInfoEntry() {
    // Does nothing.
  }

  /**
   * Retrieves the LDAP Password.
   *
   * @return the LDAP Password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the LDAP Password.
   *
   * @param password the LDAP Password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Retrieves the LDAP UUID.
   *
   * @return the LDAP UUID
   */
  public String getUuid() {
    return uuid;
  }

  /**
   * Sets the LDAP UUID.
   *
   * @param uuid the LDAP UUID to set
   */
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  /**
   * Retrieves the LDAP Status.
   *
   * @return the LDAP Status
   */
  public Long getStatus() {
    return status;
  }

  /**
   * Sets the LDAP Status.
   *
   * @param status the LDAP Status to set
   */
  public void setStatus(Long status) {
    this.status = status;
  }
}
