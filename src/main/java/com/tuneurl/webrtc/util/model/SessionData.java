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

package com.tuneurl.webrtc.util.model;

import com.tuneurl.webrtc.util.value.Constants;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Session Data table entry.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
@Entity
@Table(name = "session_data")
public class SessionData {

  /** Table Session ID. */
  @Id
  @Column(name = "sd_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** LDAP ID. */
  @Column(name = "sd_ldap_id", nullable = false)
  private Long ldap;

  /** Session IP Address. */
  @Column(name = "sd_ip_addr", nullable = false, length = Constants.IPV6_ADDRESS_SIZE)
  private String ipaddr;

  /** Session Start. */
  @Column(name = "sd_start", nullable = false)
  private LocalDateTime sessionStart;

  /** Session End. */
  @Column(name = "sd_end", nullable = false)
  private LocalDateTime sessionEnd;

  /** Default constructor. */
  public SessionData() {
    // Does nothing.
  }

  /**
   * Retrieves the Table Session ID.
   *
   * @return the Table Session ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the Table Session ID.
   *
   * @param id the Table Session ID to set
   */
  public void setId(Long id) {
    this.id = id;
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
