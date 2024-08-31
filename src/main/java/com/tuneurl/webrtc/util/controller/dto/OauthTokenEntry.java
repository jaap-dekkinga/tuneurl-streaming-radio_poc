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

package com.tuneurl.webrtc.util.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class OauthTokenEntry DTO to hold JWT for oAuth refresh token request.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public class OauthTokenEntry {

  /**
   * The Grant Type.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setGrantType(), getGrantType().
   */
  @JsonProperty("grant_type")
  private String grantType;

  /**
   * The Previous JWT.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setRefreshToken(), getRefreshToken().
   */
  @JsonProperty("refresh_token")
  private String refreshToken;

  /**
   * The Token expiration.
   *
   * <p>It can be any value.
   *
   * <p>It has both getter and setter.
   *
   * <p>It is used in setTokenExpiration(), getTokenExpiration().
   */
  @JsonProperty("token_expiration")
  private Long tokenExpiration;

  /** Default constructor for OauthTokenEntry class. */
  public OauthTokenEntry() {
    // does nothing.
  }

  /**
   * Retrieves the Grant Type.
   *
   * @return the Grant Type
   */
  public String getGrantType() {
    return grantType;
  }

  /**
   * Sets the Grant Type.
   *
   * @param grantType the Grant Type to set
   */
  public void setGrantType(String grantType) {
    this.grantType = grantType;
  }

  /**
   * Retrieves the Previous JWT.
   *
   * @return the Previous JWT
   */
  public String getRefreshToken() {
    return refreshToken;
  }

  /**
   * Sets the Previous JWT.
   *
   * @param refreshToken the Previous JWT to set
   */
  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  /**
   * Retrieves the Token expiration.
   *
   * @return the Token expiration
   */
  public Long getTokenExpiration() {
    return tokenExpiration;
  }

  /**
   * Sets the Token expiration.
   *
   * @param tokenExpiration the Token expiration to set
   */
  public void setTokenExpiration(Long tokenExpiration) {
    this.tokenExpiration = tokenExpiration;
  }

  /**
   * To String.
   *
   * @return String
   */
  public String toString() {
    java.lang.StringBuffer sb = new java.lang.StringBuffer();
    sb.append("class OauthTokenEntry {\n");
    sb.append("    \"grant_type\": ").append('"').append(getGrantType()).append("\",\n");
    sb.append("    \"refresh_token\": ").append('"').append(getRefreshToken()).append("\",\n");
    sb.append("    \"token_expiration\": ").append(getTokenExpiration()).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
