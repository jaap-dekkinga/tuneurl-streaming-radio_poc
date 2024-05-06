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
 * SDK User table entry.
 *
 * <p>Changes note (1.1)
 *
 * <ol>
 *   <ul>
 *     date: Wednesday, 08 November, 2023 04:16:19 PM PST
 *   </ul>
 *   <ul>
 *     added alias:String.
 *   </ul>
 *   <ul>
 *     added getAlias(), setAlias().
 *   </ul>
 *   <ul>
 *     Reason: to make this SdkUser instance an alias for TuneURL UserID id. {@link
 *     com.tuneurl.webrtc.util.controller.dto.InterestsData#userID}
 *   </ul>
 * </ol>
 *
 * <p>
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.1
 */
@Entity
@Table(name = "sdk_user")
public class SdkUser {

  /** Table User ID. */
  @Id
  @Column(name = "su_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** User Email. */
  @Column(name = "su_email", nullable = false, length = Constants.EMAIL_LENGTH)
  private String email;

  /** User Name. */
  @Column(name = "su_fullname", nullable = false, length = Constants.USER_NAME_SIZE)
  private String fullname;

  /** User Roles. */
  @Column(name = "su_roles", nullable = false, length = Constants.ROLES_SIZE)
  private String roles;

  /** LDAP ID. */
  @Column(name = "su_ldap", nullable = false)
  private Long ldap;

  /** The Date Created. */
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  /** User Alias ID */
  @Column(name = "su_alias", nullable = true, length = Constants.INTEREST_USER_SIZE)
  private String alias;

  /** Default constructor. */
  public SdkUser() {
    // Does nothing.
  }

  /**
   * Trim the String
   *
   * @param data String
   * @param limit Integer
   * @return String
   */
  public static String getString(final String data, final int limit) {
    String copy = (data == null) ? "" : data.trim();
    if (copy.length() > limit) {
      copy = copy.substring(0, limit);
    }
    return new String(copy);
  }

  /**
   * Retrieves the Table User ID.
   *
   * @return the Table User ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the Table User ID.
   *
   * @param id the Table User ID to set
   */
  public void setId(Long id) {
    this.id = id;
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
    this.email = getString(email, Constants.EMAIL_LENGTH);
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
    this.fullname = getString(fullname, Constants.USER_NAME_SIZE);
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
    this.roles = getString(roles, Constants.ROLES_SIZE);
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
   * Retrieves the Date Created.
   *
   * @return LocalDateTime
   */
  public LocalDateTime getCreatedAt() {
    return this.createdAt;
  }

  /**
   * Set the Date Created.
   *
   * @param createdAt LocalDateTime the Date Created to set
   */
  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * Retrieves the User Alias ID.
   *
   * @return the User Alias ID
   * @since 1.1
   */
  public String getAlias() {
    return alias;
  }

  /**
   * Sets the User Alias ID.
   *
   * @param alias the User Alias ID to set
   * @since 1.1
   */
  public void setAlias(String alias) {
    this.alias = getString(alias, Constants.INTEREST_USER_SIZE);
  }
}
