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

package com.tuneurl.webrtc.util.service.impl;

import com.albon.auth.util.LogMessage;
import com.tuneurl.webrtc.util.controller.dto.*;
import com.tuneurl.webrtc.util.controller.dto.ItemId;
import com.tuneurl.webrtc.util.exception.BaseServiceException;
import com.tuneurl.webrtc.util.model.SessionData;
import com.tuneurl.webrtc.util.repository.SessionDataRepository;
import com.tuneurl.webrtc.util.service.SessionDataService;
import com.tuneurl.webrtc.util.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implement SessionDataService interface.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
@Service
public class SessionDataServiceImpl implements SessionDataService {

  @Autowired private SessionDataRepository repository;

  /**
   * Save the SessionData data.
   *
   * @param object SessionData
   * @return SessionData instance
   * @throws BaseServiceException If Error at Database level
   */
  private SessionData save(SessionData object) throws BaseServiceException {
    SessionData result = object;
    try {
      result = repository.save(object);
      repository.flush();
    } catch (Exception ex) {
      CommonUtil.InternalServerException(ex.getMessage());
    }
    return result;
  }

  /**
   * Find SessionData with given ID.
   *
   * @param id Long
   * @return SessionData instance or null
   * @throws BaseServiceException If Error at Database level
   */
  private SessionData find(long id) throws BaseServiceException {
    return repository.locateById(id).orElseThrow(() -> CommonUtil.generateSessionDataNotFound(id));
  }

  /**
   * Find SessionData with given Name.
   *
   * @param name String
   * @return SessionData instance or null
   * @throws BaseServiceException If Error at Database level
   */
  private SessionData find(final String name) throws BaseServiceException {
    CommonUtil.checkNullOrEmptyParameter(name, "name");
    return repository
        .locateByName(name)
        .orElseThrow(() -> CommonUtil.generateSessionDataNotFound(name));
  }

  /**
   * Save SessionData.
   *
   * @param session SessionData
   * @param logger LogMessage
   */
  public SessionData saveSessionData(SessionData session, LogMessage logger) {
    SessionData clz = session;
    try {
      clz = save(session);
    } catch (BaseServiceException ex) {
      if (null != logger) logger.debug("Error in saveSessionData: %s", ex.getMessage());
    }
    return clz;
  }

  /**
   * Create SessionData instance.
   *
   * @param id Long
   * @return ItemId instance
   */
  public SessionData createSessionDataById(final long id) {
    SessionData clazz = new SessionData();
    clazz.setId(id);
    return clazz;
  }

  /**
   * Create a SessionData.
   *
   * @param entry SessionDataEntry
   * @return SessionData
   * @throws BaseServiceException If Error at Database level
   */
  public SessionData createSessionData(final SessionDataEntry entry) throws BaseServiceException {
    SessionData clazz = createSessionDataById(0L);
    CommonUtil.translateSessionData(clazz, entry);
    return save(clazz);
  }

  /**
   * Read a SessionData by name.
   *
   * @param name String
   * @return SessionData
   * @throws BaseServiceException If Error at Database level
   */
  public SessionData getSessionDataByName(final String name) throws BaseServiceException {
    return find(name);
  }

  /**
   * Read a SessionData by ID.
   *
   * @param id Long
   * @return SessionData
   * @throws BaseServiceException If Error at Database level
   */
  public SessionData getSessionDataById(final long id) throws BaseServiceException {
    return find(id);
  }

  /**
   * Update a SessionData.
   *
   * @param update SessionDataUpdate
   * @param name String
   * @return ItemId
   * @throws BaseServiceException If Error at Database level
   */
  public ItemId updateSessionData(final SessionDataUpdate update, final String name)
      throws BaseServiceException {
    SessionData clazz = find(name);
    CommonUtil.updateSessionData(clazz, update);
    return new ItemId(clazz.getId());
  }

  /**
   * Delete a SessionData by ID.
   *
   * @param id Long
   * @return ItemId
   * @throws BaseServiceException If Error at Database level
   */
  public ItemId deleteSessionData(final long id) throws BaseServiceException {
    SessionData clazz = find(id);
    ItemId item = new ItemId(0L);
    try {
      repository.delete(clazz);
      item.setId(id);
    } catch (Exception ex) {
      CommonUtil.BadRequestException(ex.getMessage());
      /** NOTREACH */
    }
    return item;
  }
}
