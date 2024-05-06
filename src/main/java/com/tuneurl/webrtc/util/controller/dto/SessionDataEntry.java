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
 * Session Data entry.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public class SessionDataEntry {

  /** LDAP ID. */
  @JsonProperty("ldap")
  private Long ldap;

  /** Session IP Address. */
  @JsonProperty("ipaddr")
  private String ipaddr;

  /** Session Start. */
  @JsonProperty("sessionStart")
  private LocalDateTime sessionStart;

  /** Session End. */
  @JsonProperty("sessionEnd")
  private LocalDateTime sessionEnd;

  /** Default constructor. */
  public SessionDataEntry() {
    // Does nothing.
  }

  /**
   * Retrieves the LDAP ID.
   *
   * @return the LDAP ID
   */
  public Long getLdap() {
    return ldap;
  }

  /**
   * Sets the LDAP ID.
   *
   * @param ldap the LDAP ID to set
   */
  public void setLdap(Long ldap) {
    this.ldap = ldap;
  }

  /**
   * Retrieves the Session IP Address.
   *
   * @return the Session IP Address
   */
  public String getIpaddr() {
    return ipaddr;
  }

  /**
   * Sets the Session IP Address.
   *
   * @param ipaddr the Session IP Address to set
   */
  public void setIpaddr(String ipaddr) {
    this.ipaddr = ipaddr;
  }

  /**
   * Retrieves the Session Start.
   *
   * @return the Session Start
   */
  public LocalDateTime getSessionStart() {
    return sessionStart;
  }

  /**
   * Sets the Session Start.
   *
   * @param sessionStart the Session Start to set
   */
  public void setSessionStart(LocalDateTime sessionStart) {
    this.sessionStart = sessionStart;
  }

  /**
   * Retrieves the Session End.
   *
   * @return the Session End
   */
  public LocalDateTime getSessionEnd() {
    return sessionEnd;
  }

  /**
   * Sets the Session End.
   *
   * @param sessionEnd the Session End to set
   */
  public void setSessionEnd(LocalDateTime sessionEnd) {
    this.sessionEnd = sessionEnd;
  }
}
