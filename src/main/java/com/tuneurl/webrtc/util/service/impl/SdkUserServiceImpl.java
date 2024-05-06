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

import com.albon.auth.util.Helper;
import com.albon.auth.util.LogMessage;
import com.tuneurl.webrtc.util.controller.dto.*;
import com.tuneurl.webrtc.util.controller.dto.ItemId;
import com.tuneurl.webrtc.util.exception.BaseServiceException;
import com.tuneurl.webrtc.util.model.SdkUser;
import com.tuneurl.webrtc.util.repository.SdkUserRepository;
import com.tuneurl.webrtc.util.service.SdkUserService;
import com.tuneurl.webrtc.util.util.CommonUtil;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implement SdkUserService interface.
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
 * @author albonteddy@gmail.com
 * @version 1.1
 */
@Service
public class SdkUserServiceImpl implements SdkUserService {

  @Autowired private SdkUserRepository repository;

  /**
   * Save the SdkUser data.
   *
   * @param object SdkUser
   * @return SdkUser instance
   * @throws BaseServiceException If Error at Database level
   */
  private SdkUser save(SdkUser object) throws BaseServiceException {
    SdkUser result = object;
    try {
      // At least su_alias == LDAP ID (v1.1)
      if (Helper.isStringNullOrEmpty(object.getAlias())) {
        object.setAlias(object.getLdap().toString());
      }
      result = repository.save(object);
      repository.flush();
    } catch (Exception ex) {
      CommonUtil.InternalServerException(ex.getMessage());
    }
    return result;
  }

  /**
   * Find SdkUser with given ID.
   *
   * @param id Long
   * @return SdkUser instance or null
   * @throws BaseServiceException If Error at Database level
   */
  private SdkUser find(long id) throws BaseServiceException {
    return repository.locateById(id).orElseThrow(() -> CommonUtil.generateSdkUserNotFound(id));
  }

  /**
   * Find SdkUser with given Name.
   *
   * @param name String
   * @return SdkUser instance or null
   * @throws BaseServiceException If Error at Database level
   */
  private SdkUser find(final String name) throws BaseServiceException {
    CommonUtil.checkNullOrEmptyParameter(name, "name");
    return repository
        .locateByName(name)
        .orElseThrow(() -> CommonUtil.generateSdkUserNotFound(name));
  }

  /**
   * Save SdkUser.
   *
   * @param sdk SdkUser
   * @param logger LogMessage
   */
  public SdkUser saveSdkUser(SdkUser sdk, LogMessage logger) {
    SdkUser clz = sdk;
    try {
      clz = save(sdk);
    } catch (BaseServiceException ex) {
      if (null != logger) logger.debug("Error in saveSdkUser: %s", ex.getMessage());
    }
    return clz;
  }

  /**
   * Create SdkUser instance.
   *
   * @param id Long
   * @return ItemId instance
   */
  public SdkUser createSdkUserById(final long id) {
    SdkUser clazz = new SdkUser();
    clazz.setId(id);
    return clazz;
  }

  /**
   * Create a SdkUser.
   *
   * @param entry SdkUserEntry
   * @param ldap Long LDAP ID
   * @return SdkUser
   * @throws BaseServiceException If Error at Database level
   */
  public SdkUser createSdkUser(final SdkUserEntry entry, final Long ldap)
      throws BaseServiceException {
    SdkUser clazz = createSdkUserById(0L);
    CommonUtil.translateSdkUser(clazz, entry);
    clazz.setAlias(ldap.toString()); // v1.1
    clazz.setLdap(ldap);
    clazz.setCreatedAt(CommonUtil.asLocalDateTime(new java.util.Date()));
    return save(clazz);
  }

  /**
   * Read a SdkUser by name.
   *
   * @param name String
   * @return SdkUser
   * @throws BaseServiceException If Error at Database level
   */
  public SdkUser getSdkUserByName(final String name) throws BaseServiceException {
    return find(name);
  }

  /**
   * Read a SdkUser by ID.
   *
   * @param id Long
   * @return SdkUser
   * @throws BaseServiceException If Error at Database level
   */
  public SdkUser getSdkUserById(final long id) throws BaseServiceException {
    return find(id);
  }

  /**
   * Update a SdkUser.
   *
   * @param update SdkUserUpdate
   * @param name String
   * @return ItemId
   * @throws BaseServiceException If Error at Database level
   */
  public ItemId updateSdkUser(final SdkUserUpdate update, final String name)
      throws BaseServiceException {
    SdkUser clazz = find(name);
    CommonUtil.updateSdkUser(clazz, update);
    return new ItemId(clazz.getId());
  }

  /**
   * Delete a SdkUser by ID.
   *
   * @param id Long
   * @return ItemId
   * @throws BaseServiceException If Error at Database level
   */
  public ItemId deleteSdkUser(final long id) throws BaseServiceException {
    SdkUser clazz = find(id);
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

  /**
   * Retrieves all SdkUser.
   *
   * @param offset long Starting offset
   * @param page long Page size
   * @return Array of SdkUser
   */
  public List<SdkUser> findAll(final long offset, final long page) {
    long limit, count = repository.count();
    if (page > 0L) {
      limit = count / page;
      if (offset <= limit) {
        return repository.listSdkUser(offset, page);
      }
    }
    return new ArrayList<SdkUser>();
  }

  /**
   * Find a SdkUser by alias.
   *
   * @param alias String
   * @return SdkUser
   * @throws BaseServiceException If Error at Database level
   */
  public SdkUser findByAlias(final String alias) throws BaseServiceException {
    CommonUtil.checkNullOrEmptyParameter(alias, "alias");
    return repository
        .locateByAlias(alias)
        .orElseThrow(() -> CommonUtil.generateSdkUserAliasNotFound(alias));
  }
}
