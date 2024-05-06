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

package com.tuneurl.webrtc.util.service;

import com.albon.auth.util.LogMessage;
import com.tuneurl.webrtc.util.controller.dto.*;
import com.tuneurl.webrtc.util.controller.dto.ItemId;
import com.tuneurl.webrtc.util.exception.BaseServiceException;
import com.tuneurl.webrtc.util.model.LdapInfo;

/**
 * LdapInfoService interface.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public interface LdapInfoService {

  /**
   * Save LdapInfo.
   *
   * @param info LdapInfo
   * @param logger LogMessage
   */
  public LdapInfo saveLdapInfo(LdapInfo info, LogMessage logger);

  /**
   * Create LdapInfo instance.
   *
   * @param id Long
   * @return ItemId instance
   */
  public LdapInfo createLdapInfoById(final long id);

  /**
   * Create a LdapInfo.
   *
   * @param entry LdapInfoEntry
   * @return LdapInfo
   * @throws BaseServiceException If Error at Database level
   */
  public LdapInfo createLdapInfo(final LdapInfoEntry entry) throws BaseServiceException;

  /**
   * Read a LdapInfo by name.
   *
   * @param name String
   * @return LdapInfo
   * @throws BaseServiceException If Error at Database level
   */
  public LdapInfo getLdapInfoByName(final String name) throws BaseServiceException;

  /**
   * Read a LdapInfo by ID.
   *
   * @param id Long
   * @return LdapInfo
   * @throws BaseServiceException If Error at Database level
   */
  public LdapInfo getLdapInfoById(final long id) throws BaseServiceException;

  /**
   * Update a LdapInfo.
   *
   * @param entry LdapInfoUpdate
   * @param name String
   * @return ItemId
   * @throws BaseServiceException If Error at Database level
   */
  public ItemId updateLdapInfo(final LdapInfoUpdate entry, final String name)
      throws BaseServiceException;

  /**
   * Delete a LdapInfo by ID.
   *
   * @param id Long
   * @return ItemId
   * @throws BaseServiceException If Error at Database level
   */
  public ItemId deleteLdapInfo(final long id) throws BaseServiceException;
}
