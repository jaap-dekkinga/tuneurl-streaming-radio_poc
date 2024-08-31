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

package com.tuneurl.webrtc.util.service.impl;

import com.albon.auth.util.LogMessage;
import com.tuneurl.webrtc.util.controller.dto.*;
import com.tuneurl.webrtc.util.controller.dto.ItemId;
import com.tuneurl.webrtc.util.exception.BaseServiceException;
import com.tuneurl.webrtc.util.model.AudioStreamDatabase;
import com.tuneurl.webrtc.util.repository.AudioStreamDatabaseRepository;
import com.tuneurl.webrtc.util.service.AudioStreamDatabaseService;
import com.tuneurl.webrtc.util.util.CommonUtil;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implement AudioStreamDatabaseService interface.
 *
 * @author albonteddy@gmail.com
 * @version 1.1
 */
@Service
public class AudioStreamDatabaseServiceImpl implements AudioStreamDatabaseService {

  @Autowired private AudioStreamDatabaseRepository repository;

  /**
   * Save the AudioStreamDatabase data.
   *
   * @param object AudioStreamDatabase
   * @return AudioStreamDatabase instance
   * @throws BaseServiceException If Error at Database level
   */
  private AudioStreamDatabase save(AudioStreamDatabase object) throws BaseServiceException {
    AudioStreamDatabase result = object;
    try {
      result = repository.save(object);
      repository.flush();
    } catch (Exception ex) {
      CommonUtil.InternalServerException(ex.getMessage());
    }
    return result;
  }

  /**
   * Find AudioStreamDatabase with given ID.
   *
   * @param id Long
   * @return AudioStreamDatabase instance or null
   * @throws BaseServiceException If Error at Database level
   */
  private AudioStreamDatabase find(long id) throws BaseServiceException {
    return repository
        .locateById(id)
        .orElseThrow(() -> CommonUtil.generateAudioStreamDatabaseNotFound(id));
  }

  /**
   * Find AudioStreamDatabase with given Name.
   *
   * @param name String
   * @return AudioStreamDatabase instance or null
   * @throws BaseServiceException If Error at Database level
   */
  private AudioStreamDatabase find(final String name) throws BaseServiceException {
    CommonUtil.checkNullOrEmptyParameter(name, "name");
    return repository
        .locateByName(name)
        .orElseThrow(() -> CommonUtil.generateAudioStreamDatabaseNotFound(name));
  }

  /**
   * Save AudioStreamDatabase instance.
   *
   * @param clazz AudioStreamDatabase
   * @return AudioStreamDatabase instance
   */
  public AudioStreamDatabase saveAudioStreamDatabase(AudioStreamDatabase clazz, LogMessage logger) {
    AudioStreamDatabase clz = clazz;
    try {
      clz = save(clazz);
    } catch (BaseServiceException ex) {
      if (null != logger) logger.debug("Error in saveAudioStreamDatabase: %s", ex.getMessage());
    }
    return clz;
  }

  /**
   * Create AudioStreamDatabase instance.
   *
   * @return ItemId instance
   */
  public AudioStreamDatabase createAudioStreamDatabase() {
    return new AudioStreamDatabase();
  }

  /**
   * Read a AudioStreamDatabase by name.
   *
   * @param name String
   * @return AudioStreamDatabase
   * @throws BaseServiceException If Error at Database level
   */
  public AudioStreamDatabase getAudioStreamDatabaseByName(final String name)
      throws BaseServiceException {
    return find(name);
  }

  /**
   * Read a AudioStreamDatabase by name.
   *
   * @param name String
   * @param duration Long
   * @return AudioStreamDatabase
   * @throws BaseServiceException If Error at Database level
   */
  public AudioStreamDatabase getAudioStreamDatabaseByNameByDuration(
      final String name, final Long duration) throws BaseServiceException {
    CommonUtil.checkNullOrEmptyParameter(name, "name");
    return repository
        .locateByNameByDuration(name, duration)
        .orElseThrow(() -> CommonUtil.generateAudioStreamDatabaseNotFound(name));
  }

  /**
   * Read a AudioStreamDatabase by ID.
   *
   * @param id Long
   * @return AudioStreamDatabase
   * @throws BaseServiceException If Error at Database level
   */
  public AudioStreamDatabase getAudioStreamDatabaseById(final long id) throws BaseServiceException {
    return find(id);
  }

  /**
   * Delete a AudioStreamDatabase by ID.
   *
   * @param id Long
   * @return ItemId
   * @throws BaseServiceException If Error at Database level
   */
  public ItemId deleteAudioStreamDatabase(final long id) throws BaseServiceException {
    AudioStreamDatabase clazz = find(id);
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
   * Retrieves all AudioStreamDatabase.
   *
   * @param offset long Starting offset
   * @param page long Page size
   * @return Array of AudioStreamDatabase
   */
  public List<AudioStreamDatabase> findAll(final long offset, final long page) {
    long limit, count = repository.count();
    if (page > 0L) {
      limit = count / page;
      if (offset <= limit) {
        return repository.listAudioStreamDatabase(offset, page);
      }
    }
    return new ArrayList<AudioStreamDatabase>();
  }
}
