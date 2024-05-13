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

package com.tuneurl.webrtc.util.controller;

import com.albon.auth.dto.UserEntry;
import com.albon.auth.jwt.JwtTool;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tuneurl.webrtc.util.model.*;

/**
 * Class ClientCredential DTO to hold SDK User credentials.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public class ClientCredential {

  /** The Sdk UUID. */
  @JsonProperty("sdk_uuid")
  private String sdkUuid;

  /** The JWT tool. */
  @JsonProperty("jwt_tool")
  private JwtTool jwtTool;

  /** The User entry. */
  @JsonProperty("user")
  private UserEntry user;

  /** The SDK user. */
  @JsonProperty("sdk_user")
  private SdkUser sdkUser;

  /** The LDP info. */
  @JsonProperty("ldap_info")
  private LdapInfo ldapInfo;

  /** The Session data. */
  @JsonProperty("session")
  private SessionData session;

  /** The Status. */
  @JsonProperty("status")
  private String status;

  /** The User LDAP info. */
  @JsonProperty("user_ldap")
  private LdapInfo userLdap;

  /** Default constructor for ClientCredential class. */
  public ClientCredential() {
    // does nothing.
  }

  /**
   * Retrieves the Sdk UUID.
   *
   * @return the Sdk UUID
   */
  public String getSdkUuid() {
    return sdkUuid;
  }

  /**
   * Sets the Sdk UUID.
   *
   * @param sdkUuid the Sdk UUID to set
   */
  public void setSdkUuid(String sdkUuid) {
    this.sdkUuid = sdkUuid;
  }

  /**
   * Retrieves the JWT tool.
   *
   * @return the JWT tool
   */
  public JwtTool getJwtTool() {
    return jwtTool;
  }

  /**
   * Sets the JWT tool.
   *
   * @param jwtTool the JWT tool to set
   */
  public void setJwtTool(JwtTool jwtTool) {
    this.jwtTool = jwtTool;
  }

  /**
   * Retrieves the User entry.
   *
   * @return the User entry
   */
  public UserEntry getUser() {
    return user;
  }

  /**
   * Sets the User entry.
   *
   * @param user the User entry to set
   */
  public void setUser(UserEntry user) {
    this.user = user;
  }

  /**
   * Retrieves the SDK user.
   *
   * @return the SDK user
   */
  public SdkUser getSdkUser() {
    return sdkUser;
  }

  /**
   * Sets the SDK user.
   *
   * @param sdkUser the SDK user to set
   */
  public void setSdkUser(SdkUser sdkUser) {
    this.sdkUser = sdkUser;
  }

  /**
   * Retrieves the LDP info.
   *
   * @return the LDP info
   */
  public LdapInfo getLdapInfo() {
    return ldapInfo;
  }

  /**
   * Sets the LDP info.
   *
   * @param ldapInfo the LDP info to set
   */
  public void setLdapInfo(LdapInfo ldapInfo) {
    this.ldapInfo = ldapInfo;
  }

  /**
   * Retrieves the Session data.
   *
   * @return the Session data
   */
  public SessionData getSession() {
    return session;
  }

  /**
   * Sets the Session data.
   *
   * @param session the Session data to set
   */
  public void setSession(SessionData session) {
    this.session = session;
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
   * Retrieves the User LDAP info.
   *
   * @return the User LDAP info
   */
  public LdapInfo getUserLdap() {
    return userLdap;
  }

  /**
   * Sets the User LDAP info.
   *
   * @param userLdap the User LDAP info to set
   */
  public void setUserLdap(LdapInfo userLdap) {
    this.userLdap = userLdap;
  }

  /**
   * To String.
   *
   * @return String
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("class ClientCredential {\n");
    sb.append("    \"sdk_uuid\": ").append('"').append(getSdkUuid()).append("\",\n");
    sb.append("    \"jwt_tool\": ").append('"').append(getJwtTool()).append("\",\n");
    sb.append("    \"user\": ").append('"').append(getUser()).append("\",\n");
    sb.append("    \"sdk_user\": ").append('"').append(getSdkUser()).append("\",\n");
    sb.append("    \"ldap_info\": ").append('"').append(getLdapInfo()).append("\",\n");
    sb.append("    \"session\": ").append('"').append(getSession()).append("\",\n");
    sb.append("    \"status\": ").append('"').append(getStatus()).append("\",\n");
    sb.append("    \"user_ldap\": ").append('"').append(getUserLdap()).append("\"\n");
    sb.append("}\n");
    return sb.toString();
  }
}
