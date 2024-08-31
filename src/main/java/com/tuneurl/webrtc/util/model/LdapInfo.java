/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2024, TuneURL Inc.
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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Simulated LDAP Info table entry.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
@Entity
@Table(name = "ldap_info")
public class LdapInfo {

  /** Table LDAP ID. */
  @Id
  @Column(name = "li_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** LDAP Password. */
  @Column(name = "li_pwd", nullable = false, length = Constants.HASHED_PASSWORD_SIZE)
  private String password;

  /** LDAP UUID. */
  @Column(name = "li_uuid", nullable = false, length = Constants.UUID_SIZE)
  private String uuid;

  /** LDAP Status. */
  @Column(name = "li_status", nullable = false)
  private Long status;

  /** Default constructor. */
  public LdapInfo() {
    // Does nothing.
  }

  /**
   * Retrieves the Table LDAP ID.
   *
   * @return the Table LDAP ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the Table LDAP ID.
   *
   * @param id the Table LDAP ID to set
   */
  public void setId(Long id) {
    this.id = id;
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
