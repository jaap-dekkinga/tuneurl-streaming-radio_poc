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

package com.tuneurl.webrtc.util.util;

import com.albon.auth.util.Helper;
import com.tuneurl.webrtc.util.exception.BaseServiceException;
import com.tuneurl.webrtc.util.value.Constants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.zip.CRC32;
import org.springframework.http.HttpStatus;

/**
 * Process helper.
 *
 * <ul>
 *   <li>added counter in readTextFile().
 * </ul>
 *
 * @author albonteddy@gmail.com
 * @version 1.1
 */
public final class ProcessHelper {

  /** Hidden constructor. */
  private ProcessHelper() {
    // Hidden
  }

  public static String byte2short(byte c) {
    String s = "";
    short n = (short) c;
    if (n < 0) n += 256;
    s = s + n;
    return s;
  }

  public static double stod(final String value) {
    Double data = 0.0;
    try {
      if (value != null) {
        data = Double.parseDouble(value);
      }
    } catch (NumberFormatException ignore) {
      data = 0.0;
    }
    return data.doubleValue();
  }

  /**
   * Check for <code>null</code> object.
   *
   * @param param Object to check
   * @param name String the name of the object
   * @throws BaseServiceException
   *     eclipse-javadoc:%E2%98%82=webrtc.util/%5C/opt%5C/java%5C/jdk1.8.0_231%5C/jre%5C/lib%5C/rt.jar=/javadoc_location=/https:%5C/%5C/docs.oracle.com%5C/javase%5C/8%5C/docs%5C/api%5C/=/=/maven.pomderived=/true=/%3Cjava.lang(Double.class%E2%98%83Double~parseDouble~Ljava.lang.String;%E2%98%82NumberFormatException
   *     If param is <code>null</code>
   */
  public static void checkNull(Object param, String name) throws BaseServiceException {
    if (param == null) {
      throw new IllegalArgumentException("The '" + name + "' cannot be null!");
    }
  }

  /**
   * Check for <code>null</code> or trimmed <code>empty</code> String.
   *
   * @param param String to check
   * @param name String the name of the object
   * @throws BaseServiceException If param is <code>null</code> or trimmed <code>empty</code>
   */
  public static void checkNullOrEmptyString(String param, String name) throws BaseServiceException {
    checkNull(param, name);
    if (param.trim().isEmpty()) {
      throw new IllegalArgumentException("The '" + name + "' cannot be empty!");
    }
  }

  /**
   * Helper to convert String to Long integer.
   *
   * @param value String
   * @param pDefault long
   * @return long
   */
  public static long parseLong(final String value, long pDefault) {
    String val;
    long number;
    if (value != null && value.length() > 0) {
      val = value.trim();
      if (val.length() > 0) {
        try {
          number = Long.parseLong(val);
        } catch (NumberFormatException ex) {
          number = 0L;
        }
        if (number < 1L) number = pDefault;
        return number;
      }
    }
    return pDefault;
  }

  /**
   * Helper to convert String to integer.
   *
   * @param value String
   * @param pDefault long
   * @return int
   */
  public static int parseInt(final String value, int pDefault) {
    String val;
    int number;
    if (value != null && value.length() > 0) {
      val = value.trim();
      if (val.length() > 0) {
        try {
          number = Integer.parseInt(val);
        } catch (NumberFormatException ex) {
          number = 0;
        }
        if (number < 1) number = pDefault;
        return number;
      }
    }
    return pDefault;
  }

  /**
   * Helper method to create a unique String suitable for file names.
   *
   * @return String
   */
  public static String createUniqueFilename() {
    Date oras = new Date();
    long time = oras.getTime();
    @SuppressWarnings("deprecation")
    int micro = oras.getSeconds();
    return String.format("%x%d", time, micro);
  }

  /**
   * Helper method to create a unique String suitable for file names.
   *
   * @return String
   */
  public static String createUniqueFilenameEx(Random random) {
    Date oras = new Date();
    long time = oras.getTime();
    @SuppressWarnings("deprecation")
    int micro = oras.getSeconds();
    int y = (int) Math.abs(random.nextInt());
    return String.format("%x%d%d", time, micro, y);
  }

  /**
   * Helper method to calculate String CRC32 value.
   *
   * @param data String
   * @return String in Hex format
   */
  public static String genCrc32(final String data) {
    CRC32 crc32 = new CRC32();
    byte[] dataBytes = data.getBytes();
    crc32.update(dataBytes, 0, dataBytes.length);
    long value = crc32.getValue();
    return String.format("%x", value);
  }

  /**
   * Helper method to delete a file.
   *
   * @param filename String
   */
  public static boolean isFileExist(final String filename) {
    File file = new File(filename);
    boolean isExist;
    try {
      isExist = file.exists();
    } catch (SecurityException ex) {
      isExist = false;
    }
    return isExist;
  }

  /**
   * Check if the conversion time meet the desired duration.
   *
   * @param creationTime LocalDateTime
   * @param duration Long
   * @return boolean is True if current time is beyond conversion time, otherwise False.
   */
  public static boolean isConversionExpired(final LocalDateTime creationTime, final Long duration) {
    // Creation time is 5 seconds ago, so adjust expiration date
    long nanos = (duration - Constants.AUDIOSTEAM_FIVE_SECOND_DURATION) * 1000_1000_000L;
    LocalDateTime expiration = creationTime.plusNanos(nanos);
    LocalDateTime today = CommonUtil.asLocalDateTime(new Date());
    return expiration.isAfter(today);
  }

  /**
   * Check for <code>null</code> or trimmed <code>empty</code> String.
   *
   * @param param String to check
   * @param limit integer size limit of String
   * @param name String the name of the object
   * @return String trimmed upto the size of LIMIT
   * @throws BaseServiceException If param is <code>null</code> or trimmed <code>empty</code>
   */
  public static String getStringEx(String param, int limit, String name)
      throws BaseServiceException {
    checkNull(param, name);
    String result = param.trim();
    if (result.length() == 0) {
      throw new IllegalArgumentException("The '" + name + "' cannot be empty!");
    }
    if (result.length() > limit) {
      return result.substring(0, limit);
    }
    return result;
  }

  /**
   * Helper method to delete a file.
   *
   * @param filename String
   */
  public static void deleteFile(final String filename) {
    File file;
    try {
      if (!Helper.isStringNullOrEmpty(filename)) {
        file = new File(filename);
        file.delete();
      }
    } catch (SecurityException ex) {
      // Ignore
    }
  }

  /**
   * Helper method to create a directory.
   *
   * @param dirName String
   */
  public static void makeDir(final String dirName) {
    File file;
    try {
      if (!Helper.isStringNullOrEmpty(dirName)) {
        file = new File(dirName);
        if (!file.isDirectory()) file.mkdir();
      }
    } catch (SecurityException ex) {
      // Ignore
    }
  }

  /**
   * Helper method to read text file created by
   *
   * <p>/home/ubuntu/audio/run_webrtc_script.sh and the
   *
   * <p>content have KEY equal VALUE pairs in it.
   *
   * @param filename String
   * @param autoDelete boolean
   * @return Map&lt;String, String&gt;
   * @throws BaseServiceException If error at File level
   */
  public static Map<String, String> readTextFileAsArray(final String filename, boolean autoDelete)
      throws BaseServiceException {
    Map<String, String> maps = new HashMap<String, String>();
    BufferedReader reader = null;
    String line;
    String datus[];
    String key;
    String value;
    try {
      reader = new BufferedReader(new FileReader(filename));
      while ((line = reader.readLine()) != null) {
        datus = line.split("\\=");
        if (datus != null && datus.length == 2) {
          key = datus[0];
          value = datus[1];
          if (!Helper.isStringNullOrEmpty(key) && !Helper.isStringNullOrEmpty(value)) {
            maps.put(key, value);
          }
        }
      }
    } catch (IOException ex) {
      throw new BaseServiceException("ERROR: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ex) {
          // Ignore
        }
      }
      if (autoDelete) deleteFile(filename);
    }
    return maps;
  }

  /**
   * Helper method to read a text file.
   *
   * @param filename String
   * @return String
   * @throws BaseServiceException If error at File level
   */
  public static String readTextFile(final String filename) throws BaseServiceException {
    BufferedReader reader = null;
    StringBuilder content = new StringBuilder();
    String line;
    Long counter = 0L;
    try {
      reader = new BufferedReader(new FileReader(filename));
      while ((line = reader.readLine()) != null) {
        content.append(line);
        content.append(System.lineSeparator());
        counter++;
      }
    } catch (IOException ex) {
      throw new BaseServiceException("ERROR: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ex) {
          // Ignore
        }
      }
    }
    if (counter == 0L) return "";
    return content.toString();
  }

  /**
   * Helper method to create a text file.
   *
   * @param filename String
   * @param data String
   * @throws BaseServiceException If error at File level
   */
  public static void writeTextFile(final String filename, final String data)
      throws BaseServiceException {
    FileWriter fw = null;
    BaseServiceException error = null;
    ProcessHelper.deleteFile(filename);
    try {
      try {
        fw = new FileWriter(filename);
      } catch (IOException ex) {
        fw = null;
        error = new BaseServiceException("ERROR: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
      }
      if (error == null) {
        fw.write(data);
      }
    } catch (IOException ex) {
      error = new BaseServiceException("ERROR: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    } finally {
      if (fw != null) {
        try {
          fw.close();
        } catch (IOException ex) {
          // Ignore
        }
      }
    }
    if (error != null) throw error;
  }

  /**
   * Helper method to encode String into hEx format.
   *
   * @param value String
   * @return String
   */
  public static String hexEncoder(final String value) {
    String data = value;
    byte[] encoded;
    if (Helper.isStringNullOrEmpty(data)) data = "";
    if (!data.startsWith(Constants.HEX_PREFIX)) {
      encoded = Base64.getEncoder().encode(data.getBytes());
      data = "hEx" + encoded.toString();
    }
    return data;
  }

  /**
   * This version does not encrypt the password. It just check the hEx prefix.
   *
   * @param password String
   * @param logger MessageLogger
   * @return String
   * @throws BaseServiceException
   */
  public static String encryptPassword(final String password, MessageLogger logger)
      throws BaseServiceException {
    if (Helper.isStringNullOrEmpty(password) || !password.startsWith(Constants.HEX_PREFIX)) {
      // Don't tell the correct error. Should tell either the user name or password is not valid.
      CommonUtil.BadRequestException(Constants.INVALID_USER_NAME_OR_PASSWORD);
      /*NOTREACH*/
    }
    // Add encryption here.
    return hexEncoder(password);
  }

  /**
   * Helper method to extract filename from given URL.
   *
   * @param pUrl String
   * @return String
   */
  public static String extractFilenameFromUrl(final String pUrl) {
    String url = pUrl;
    int offset;
    char cc;
    if (url != null && url.length() > 0) {
      for (offset = url.length() - 1; offset > 0; offset--) {
        cc = url.charAt(offset);
        if (cc == '/') {
          return url.substring(offset + 1);
        }
      }
    }
    return url;
  }

  /**
   * Helper to extract file extension from a fileName.
   *
   * @param fileName String
   * @return String
   */
  public static String getFileExtentionFromFilename(final String fileName) {
    for (int length = fileName.length() - 1; length > 0; length--) {
      if ('.' == fileName.charAt(length)) {
        return fileName.substring(length);
      }
    }
    return "";
  }
}
