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

package com.tuneurl.webrtc.util.service;

import com.albon.auth.util.LogMessage;
import com.tuneurl.webrtc.util.controller.dto.*;
import com.tuneurl.webrtc.util.controller.dto.ItemId;
import com.tuneurl.webrtc.util.exception.BaseServiceException;
import com.tuneurl.webrtc.util.model.SdkUser;
import java.util.List;

/**
 * SdkUserService interface.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public interface SdkUserService {

  /**
   * Save SdkUser.
   *
   * @param sdk SdkUser
   * @param logger LogMessage
   */
  public SdkUser saveSdkUser(SdkUser sdk, LogMessage logger);

  /**
   * Create SdkUser instance.
   *
   * @param id Long
   * @return ItemId instance
   */
  public SdkUser createSdkUserById(final long id);

  /**
   * Create a SdkUser.
   *
   * @param entry SdkUserEntry
   * @param ldap Long LDAP ID
   * @return SdkUser
   * @throws BaseServiceException If Error at Database level
   */
  public SdkUser createSdkUser(final SdkUserEntry entry, final Long ldap)
      throws BaseServiceException;

  /**
   * Read a SdkUser by name.
   *
   * @param name String
   * @return SdkUser
   * @throws BaseServiceException If Error at Database level
   */
  public SdkUser getSdkUserByName(final String name) throws BaseServiceException;

  /**
   * Read a SdkUser by ID.
   *
   * @param id Long
   * @return SdkUser
   * @throws BaseServiceException If Error at Database level
   */
  public SdkUser getSdkUserById(final long id) throws BaseServiceException;

  /**
   * Update a SdkUser.
   *
   * @param entry SdkUserUpdate
   * @param name String
   * @return ItemId
   * @throws BaseServiceException If Error at Database level
   */
  public ItemId updateSdkUser(final SdkUserUpdate entry, final String name)
      throws BaseServiceException;

  /**
   * Delete a SdkUser by ID.
   *
   * @param id Long
   * @return ItemId
   * @throws BaseServiceException If Error at Database level
   */
  public ItemId deleteSdkUser(final long id) throws BaseServiceException;

  /**
   * Retrieves all SdkUser.
   *
   * @param offset long Starting offset
   * @param page long Page size
   * @return Array of SdkUser
   */
  public List<SdkUser> findAll(final long offset, final long page);

  /**
   * Find a SdkUser by alias.
   *
   * @param alias String
   * @return SdkUser
   * @throws BaseServiceException If Error at Database level
   */
  public SdkUser findByAlias(final String alias) throws BaseServiceException;
}
