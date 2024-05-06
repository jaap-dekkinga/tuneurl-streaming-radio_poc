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
 * DTO to hold Audio Stream files record.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.1
 */
@Entity
@Table(name = "audio_stream_data")
public class AudioStreamDatabase {

  /** Table FK. */
  @Id
  @Column(name = "as_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long asId;

  /** The Capture Duration. */
  @Column(name = "as_duration", nullable = false)
  private Long asDuration;

  /** The Unique filename. */
  @Column(name = "as_filename", nullable = false, length = Constants.AUDIOSTREAM_FILENAME_SIZE)
  private String asFilename;

  /** The Status. */
  @Column(name = "as_status", nullable = false)
  private Integer asStatus;

  /** The Audio Stream URL. */
  @Column(name = "as_url", nullable = false, length = Constants.AUDIOSTREAM_URL_SIZE)
  private String asUrl;

  /** The Date creation. */
  @Column(name = "as_created", nullable = false)
  private LocalDateTime asCreated;

  /** The Date modification. */
  @Column(name = "as_modified", nullable = false)
  private LocalDateTime asModified;

  /**
   * Retrieves the FK.
   *
   * @return Long
   */
  public Long getAsId() {
    return this.asId;
  }

  /**
   * Set the FK.
   *
   * @param asId Long the FK to set
   */
  public void setAsId(Long asId) {
    this.asId = asId;
  }

  /**
   * Retrieves the Capture Duration.
   *
   * @return Long
   */
  public Long getAsDuration() {
    return this.asDuration;
  }

  /**
   * Set the Capture Duration.
   *
   * @param asDuration Long the Capture Duration to set
   */
  public void setAsDuration(Long asDuration) {
    this.asDuration = asDuration;
  }

  /**
   * Retrieves the Unique filename.
   *
   * @return String
   */
  public String getAsFilename() {
    return this.asFilename;
  }

  /**
   * Set the Unique filename.
   *
   * @param asFilename String the Unique filename to set
   */
  public void setAsFilename(String asFilename) {
    this.asFilename = asFilename;
  }

  /**
   * Retrieves the Status.
   *
   * @return Integer
   */
  public Integer getAsStatus() {
    return this.asStatus;
  }

  /**
   * Set the Status.
   *
   * @param asStatus Integer the Status to set
   */
  public void setAsStatus(Integer asStatus) {
    this.asStatus = asStatus;
  }

  /**
   * Retrieves the Audio Stream URL.
   *
   * @return String
   */
  public String getAsUrl() {
    return this.asUrl;
  }

  /**
   * Set the Audio Stream URL.
   *
   * @param asUrl String the Audio Stream URL to set
   */
  public void setAsUrl(String asUrl) {
    this.asUrl = asUrl;
  }

  /**
   * Retrieves the Date creation.
   *
   * @return LocalDateTime
   */
  public LocalDateTime getAsCreated() {
    return this.asCreated;
  }

  /**
   * Set the Date creation.
   *
   * @param asCreated LocalDateTime the Date creation to set
   */
  public void setAsCreated(LocalDateTime asCreated) {
    this.asCreated = asCreated;
  }

  /**
   * Retrieves the Date modification.
   *
   * @return LocalDateTime
   */
  public LocalDateTime getAsModified() {
    return this.asModified;
  }

  /**
   * Set the Date modification.
   *
   * @param asModified LocalDateTime the Date modification to set
   */
  public void setAsModified(LocalDateTime asModified) {
    this.asModified = asModified;
  }

  /** Default constructor. */
  public AudioStreamDatabase() {
    // Does nothing.
  }
}
