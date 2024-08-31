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

package com.tuneurl.webrtc.util.repository;

import com.tuneurl.webrtc.util.model.AudioStreamDatabase;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA interface that defines CRUD operation on DB for AudioStreamDatabase class.
 *
 * @author albonteddy@gmail.com
 * @version 1.1
 */
@Repository
@Transactional
public interface AudioStreamDatabaseRepository extends JpaRepository<AudioStreamDatabase, Long> {

  @Query(
      value = "SELECT * FROM audio_stream_data AS t WHERE t.as_id= :pId LIMIT 1",
      nativeQuery = true)
  Optional<AudioStreamDatabase> locateById(@Param("pId") final Long pId);

  List<AudioStreamDatabase> findAll();

  void delete(AudioStreamDatabase object);

  void flush();

  @Query(
      value = "SELECT * FROM audio_stream_data AS t WHERE t.as_filename= :pName LIMIT 1",
      nativeQuery = true)
  Optional<AudioStreamDatabase> locateByName(@Param("pName") final String pName);

  @Query(
      value =
          "SELECT * FROM audio_stream_data AS t WHERE t.as_filename= :pName AND t.as_duration= :pDuration LIMIT 1",
      nativeQuery = true)
  Optional<AudioStreamDatabase> locateByNameByDuration(
      @Param("pName") final String pName, @Param("pDuration") final Long pDuration);

  @Query(
      value =
          "SELECT * FROM audio_stream_data AS t WHERE t.as_id > 0 ORDER BY as_id DESC LIMIT :pag OFFSET :ofs",
      nativeQuery = true)
  List<AudioStreamDatabase> listAudioStreamDatabase(
      @Param("ofs") final long ofs, @Param("pag") final long pag);
}
