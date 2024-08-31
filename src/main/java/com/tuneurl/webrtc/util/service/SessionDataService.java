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
import com.tuneurl.webrtc.util.model.SessionData;

/**
 * SessionDataService interface.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public interface SessionDataService {

  /**
   * Save SessionData.
   *
   * @param session SessionData
   * @param logger LogMessage
   */
  public SessionData saveSessionData(SessionData session, LogMessage logger);

  /**
   * Create SessionData instance.
   *
   * @param id Long
   * @return ItemId instance
   */
  public SessionData createSessionDataById(final long id);

  /**
   * Create a SessionData.
   *
   * @param entry SessionDataEntry
   * @return SessionData
   * @throws BaseServiceException If Error at Database level
   */
  public SessionData createSessionData(final SessionDataEntry entry) throws BaseServiceException;

  /**
   * Read a SessionData by name.
   *
   * @param name String
   * @return SessionData
   * @throws BaseServiceException If Error at Database level
   */
  public SessionData getSessionDataByName(final String name) throws BaseServiceException;

  /**
   * Read a SessionData by ID.
   *
   * @param id Long
   * @return SessionData
   * @throws BaseServiceException If Error at Database level
   */
  public SessionData getSessionDataById(final long id) throws BaseServiceException;

  /**
   * Update a SessionData.
   *
   * @param entry SessionDataUpdate
   * @param name String
   * @return ItemId
   * @throws BaseServiceException If Error at Database level
   */
  public ItemId updateSessionData(final SessionDataUpdate entry, final String name)
      throws BaseServiceException;

  /**
   * Delete a SessionData by ID.
   *
   * @param id Long
   * @return ItemId
   * @throws BaseServiceException If Error at Database level
   */
  public ItemId deleteSessionData(final long id) throws BaseServiceException;
}
