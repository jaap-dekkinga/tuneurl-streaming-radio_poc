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
import com.tuneurl.webrtc.util.model.AudioStreamTrainingChannel;
import com.tuneurl.webrtc.util.repository.AudioStreamTrainingChannelRepository;
import com.tuneurl.webrtc.util.service.AudioStreamTrainingChannelService;
import com.tuneurl.webrtc.util.util.CommonUtil;
import com.tuneurl.webrtc.util.value.Constants;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implement AudioStreamTrainingChannelService interface.
 *
 * @author albonteddy@gmail.com
 * @version 1.1
 */
@Service
public class AudioStreamTrainingChannelServiceImpl implements AudioStreamTrainingChannelService {

  @Autowired private AudioStreamTrainingChannelRepository repository;

  /**
   * Save the AudioStreamTrainingChannel data.
   *
   * @param object AudioStreamTrainingChannel
   * @return AudioStreamTrainingChannel instance
   * @throws BaseServiceException If Error at Database level
   */
  private AudioStreamTrainingChannel save(AudioStreamTrainingChannel object)
      throws BaseServiceException {
    AudioStreamTrainingChannel result = object;
    try {
      result = repository.save(object);
      repository.flush();
    } catch (Exception ex) {
      CommonUtil.InternalServerException(ex.getMessage());
    }
    return result;
  }

  /**
   * Find AudioStreamTrainingChannel with given ID.
   *
   * @param id Long
   * @return AudioStreamTrainingChannel instance or null
   * @throws BaseServiceException If Error at Database level
   */
  private AudioStreamTrainingChannel find(long id) throws BaseServiceException {
    return repository
        .locateById(id)
        .orElseThrow(() -> CommonUtil.generateAudioStreamTrainingChannelNotFound(id));
  }

  /**
   * Find AudioStreamTrainingChannel with given Name.
   *
   * @param name String
   * @return AudioStreamTrainingChannel instance or null
   * @throws BaseServiceException If Error at Database level
   */
  private AudioStreamTrainingChannel find(final String name) throws BaseServiceException {
    CommonUtil.checkNullOrEmptyParameter(name, "name");
    return repository
        .locateByName(name)
        .orElseThrow(() -> CommonUtil.generateAudioStreamTrainingChannelNotFound(name));
  }

  /**
   * Save AudioStreamTrainingChannel instance.
   *
   * @param clazz AudioStreamTrainingChannel
   * @param logger LogMessage instance
   * @return AudioStreamTrainingChannel instance
   */
  public AudioStreamTrainingChannel saveAudioStreamTrainingChannel(
      AudioStreamTrainingChannel clazz, LogMessage logger) {
    AudioStreamTrainingChannel clz = clazz;
    try {
      clz = save(clazz);
    } catch (BaseServiceException ex) {
      if (null != logger)
        logger.debug("Error in saveAudioStreamTrainingChannel: %s", ex.getMessage());
    }
    return clz;
  }

  /**
   * Create AudioStreamTrainingChannel instance.
   *
   * @return ItemId instance
   */
  public AudioStreamTrainingChannel createAudioStreamTrainingChannel() {
    return new AudioStreamTrainingChannel();
  }

  /**
   * Read a AudioStreamTrainingChannel by name.
   *
   * @param name String
   * @return AudioStreamTrainingChannel
   * @throws BaseServiceException If Error at Database level
   */
  public AudioStreamTrainingChannel getAudioStreamTrainingChannelByName(final String name)
      throws BaseServiceException {
    return find(name);
  }

  /**
   * Read a AudioStreamTrainingChannel by ID.
   *
   * @param id Long
   * @return AudioStreamTrainingChannel
   * @throws BaseServiceException If Error at Database level
   */
  public AudioStreamTrainingChannel getAudioStreamTrainingChannelById(final long id)
      throws BaseServiceException {
    return find(id);
  }

  /**
   * Delete a AudioStreamTrainingChannel by ID.
   *
   * @param id Long
   * @return ItemId
   * @throws BaseServiceException If Error at Database level
   */
  public ItemId deleteAudioStreamTrainingChannel(final long id) throws BaseServiceException {
    AudioStreamTrainingChannel clazz = find(id);
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
   * Retrieves all AudioStreamTrainingChannel.
   *
   * @param offset long Starting offset
   * @param page long Page size
   * @return Array of AudioStreamTrainingChannel
   */
  public List<AudioStreamTrainingChannel> findAll(final long offset, final long page) {
    long limit, count = repository.count();
    if (page > 0L) {
      limit = count / page;
      if (offset <= limit) {
        return repository.listAudioStreamTrainingChannel(offset, page);
      }
    }
    return new ArrayList<AudioStreamTrainingChannel>();
  }

  /**
   * Retrieves all AudioStreamTrainingChannel by Active Channel.
   *
   * @param counts long Limit Number of Channel to list
   * @param logger LogMessage instance
   * @return Array of AudioStreamTrainingChannel
   */
  public List<AudioStreamTrainingChannel> listActiveChannel(final long counts, LogMessage logger) {
    List<AudioStreamTrainingChannel> instances;
    long count =
        (counts < Constants.CHANNEL_MIN_COUNT)
            ? Constants.CHANNEL_MIN_COUNT
            : (counts > Constants.CHANNEL_MAX_COUNT) ? Constants.CHANNEL_MAX_COUNT : counts;
    try {
      instances = repository.listActiveChannel(count);
    } catch (Exception ex) {
      if (null != logger) {
        logger.debug("Error in listActiveChannel: %s", ex.getMessage());
      }
      instances = new ArrayList<AudioStreamTrainingChannel>();
    }
    return instances;
  }

  /**
   * Retrieves AudioStreamTrainingChannel by given Active Channel ID.
   *
   * @param channel Long
   * @return AudioStreamTrainingChannel instance or null.
   * @throws BaseServiceException If Error at Database level
   */
  public AudioStreamTrainingChannel findChannelById(final Long channel)
      throws BaseServiceException {
    return repository
        .findChannelById(channel)
        .orElseThrow(() -> CommonUtil.generateAudioStreamTrainingChannelChannelNotFound(channel));
  }
}
