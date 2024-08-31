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
 * DTO to hold Audio Stream Channel record.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.1
 */
@Entity
@Table(name = "audio_stream_channel")
public class AudioStreamTrainingChannel {

  /** Table FK. */
  @Id
  @Column(name = "ac_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long acId;

  /** The Channel ID. */
  @Column(name = "ac_channel", nullable = false)
  private Long acChannel;

  /** The Offset. */
  @Column(name = "ac_offset", nullable = false)
  private Long acOffset;

  /** The Capture Duration. */
  @Column(name = "ac_duration", nullable = false)
  private Long acDuration;

  /** The Category. */
  @Column(name = "ac_category", nullable = false, length = Constants.AUDIOSTREAM_CATEGORY_SIZE)
  private String acCategory;

  /** The Title. */
  @Column(name = "ac_title", nullable = false, length = Constants.AUDIOSTREAM_TITLE_SIZE)
  private String acTitle;

  /** The Audio Stream URL. */
  @Column(name = "ac_url", nullable = false, length = Constants.AUDIOSTREAM_URL_SIZE)
  private String acUrl;

  /** The Unique filename. */
  @Column(name = "ac_filename", nullable = false, length = Constants.AUDIOSTREAM_FILENAME_EX_SIZE)
  private String acFilename;

  /** The Popup messages. */
  @Column(name = "ac_popup", nullable = false, length = Constants.AUDIOSTREAM_POPUP_SIZE)
  private String acPopup;

  /** The Status. */
  @Column(name = "ac_status", nullable = false)
  private Integer acStatus;

  /** The Date creation. */
  @Column(name = "ac_created", nullable = false)
  private LocalDateTime acCreated;

  /** The Date modification. */
  @Column(name = "ac_modified", nullable = false)
  private LocalDateTime acModified;

  /**
   * Retrieves the FK.
   *
   * @return Long
   */
  public Long getAcId() {
    return this.acId;
  }

  /**
   * Set the FK.
   *
   * @param acId Long the FK to set
   */
  public void setAcId(Long acId) {
    this.acId = acId;
  }

  /**
   * Retrieves the Channel ID.
   *
   * @return Long
   */
  public Long getAcChannel() {
    return this.acChannel;
  }

  /**
   * Set the Channel ID.
   *
   * @param acChannel Long the Channel ID to set
   */
  public void setAcChannel(Long acChannel) {
    this.acChannel = acChannel;
  }

  /**
   * Retrieves the Offset.
   *
   * @return Long
   */
  public Long getAcOffset() {
    return this.acOffset;
  }

  /**
   * Set the Offset.
   *
   * @param acOffset Long the Offset to set
   */
  public void setAcOffset(Long acOffset) {
    this.acOffset = acOffset;
  }

  /**
   * Retrieves the Capture Duration.
   *
   * @return Long
   */
  public Long getAcDuration() {
    return this.acDuration;
  }

  /**
   * Set the Capture Duration.
   *
   * @param acDuration Long the Capture Duration to set
   */
  public void setAcDuration(Long acDuration) {
    this.acDuration = acDuration;
  }

  /**
   * Retrieves the Category.
   *
   * @return String
   */
  public String getAcCategory() {
    return this.acCategory;
  }

  /**
   * Set the Category.
   *
   * @param acCategory String the Category to set
   */
  public void setAcCategory(String acCategory) {
    this.acCategory = acCategory;
  }

  /**
   * Retrieves the Title.
   *
   * @return String
   */
  public String getAcTitle() {
    return this.acTitle;
  }

  /**
   * Set the Title.
   *
   * @param acTitle String the Title to set
   */
  public void setAcTitle(String acTitle) {
    this.acTitle = acTitle;
  }

  /**
   * Retrieves the Audio Stream URL.
   *
   * @return String
   */
  public String getAcUrl() {
    return this.acUrl;
  }

  /**
   * Set the Audio Stream URL.
   *
   * @param acUrl String the Audio Stream URL to set
   */
  public void setAcUrl(String acUrl) {
    this.acUrl = acUrl;
  }

  /**
   * Retrieves the Unique filename.
   *
   * @return String
   */
  public String getAcFilename() {
    return this.acFilename;
  }

  /**
   * Set the Unique filename.
   *
   * @param acFilename String the Unique filename to set
   */
  public void setAcFilename(String acFilename) {
    this.acFilename = acFilename;
  }

  /**
   * Retrieves the Popup messages.
   *
   * @return String
   */
  public String getAcPopup() {
    return this.acPopup;
  }

  /**
   * Set the Popup messages.
   *
   * @param acPopup String the Popup messages to set
   */
  public void setAcPopup(String acPopup) {
    this.acPopup = acPopup;
  }

  /**
   * Retrieves the Status.
   *
   * @return Integer
   */
  public Integer getAcStatus() {
    return this.acStatus;
  }

  /**
   * Set the Status.
   *
   * @param acStatus Integer the Status to set
   */
  public void setAcStatus(Integer acStatus) {
    this.acStatus = acStatus;
  }

  /**
   * Retrieves the Date creation.
   *
   * @return LocalDateTime
   */
  public LocalDateTime getAcCreated() {
    return this.acCreated;
  }

  /**
   * Set the Date creation.
   *
   * @param acCreated LocalDateTime the Date creation to set
   */
  public void setAcCreated(LocalDateTime acCreated) {
    this.acCreated = acCreated;
  }

  /**
   * Retrieves the Date modification.
   *
   * @return LocalDateTime
   */
  public LocalDateTime getAcModified() {
    return this.acModified;
  }

  /**
   * Set the Date modification.
   *
   * @param acModified LocalDateTime the Date modification to set
   */
  public void setAcModified(LocalDateTime acModified) {
    this.acModified = acModified;
  }

  /** Default constructor. */
  public AudioStreamTrainingChannel() {
    // Does nothing.
  }
}
