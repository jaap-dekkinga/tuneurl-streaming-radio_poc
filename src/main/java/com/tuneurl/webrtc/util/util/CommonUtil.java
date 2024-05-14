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
import com.tuneurl.webrtc.util.controller.dto.*;
import com.tuneurl.webrtc.util.exception.BaseServiceException;
import com.tuneurl.webrtc.util.model.*;
import com.tuneurl.webrtc.util.value.Constants;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

/**
 * CommonUtil class for the package.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public final class CommonUtil {

  /** Hidden constructor. */
  private CommonUtil() {
    // Does nothing.
  }

  /**
   * Helper to compare two double number.
   *
   * @param a double
   * @param b double
   * @return int 0 a==b, 1 a>b, otherwise -1 a<b
   */
  public static int compareDouble(double a, double b) {
    BigDecimal aa = BigDecimal.valueOf(a);
    BigDecimal bb = BigDecimal.valueOf(b);
    return aa.compareTo(bb);
  }

  /**
   * Helper to compare double for a given range (inclusive).
   *
   * @param a double
   * @param b double
   * @param c double
   * @return true (a >= b && a <= c), otherwise false
   */
  public static boolean compareDoubleRange(double a, double b, double c) {
    // a >= b
    if (compareDouble(a, b) >= 0) {
      // a <= c
      return compareDouble(a, c) <= 0;
    }
    return false;
  }

  /**
   * Select single or plural word.
   *
   * @param word String
   * @param words String
   * @param counts int
   * @return String
   */
  public static String pluralOf(final String word, final String words, int counts) {
    if (counts > 0) return words;
    return word;
  }

  /**
   * Helper method to check for <code>null</code> param.
   *
   * @param param the Object to check
   * @param name the Name of the Object.
   * @throws BaseServiceException if param is null.
   */
  public static void checkNullParameter(final Object param, final String name)
      throws BaseServiceException {
    if (null == param) {
      throw new BaseServiceException("'" + name + "' cannot be null.", HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Helper method to check for <code>null</code> param or have zero value.
   *
   * @param param the Long to check
   * @param name the Name of the Long.
   * @throws BaseServiceException if param is null or zero.
   */
  public static void checkNullOrZero(final Long param, final String name)
      throws BaseServiceException {
    if (null == param || param.longValue() == 0L) {
      throw new BaseServiceException(
          "'" + name + "' cannot be null or zero.", HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Helper method to check for <code>null</code> or <code>empty</> param.
   *
   * @param param the Object to check
   * @param name the Name of the Object.
   * @throws BaseServiceException if param is null or trim empty.
   */
  public static void checkNullOrEmptyParameter(final String param, final String name)
      throws BaseServiceException {
    checkNullParameter(param, name);
    if (param.trim().isEmpty()) {
      throw new BaseServiceException("'" + name + "' cannot be empty.", HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Helper method to check if the given integer is between the minimum and maximum values
   * inclusive. If the given integer is null, use the default value.
   *
   * @param value Integer
   * @param name String
   * @param iMinimum int Minimum value
   * @param iMaximum int Maximum range if greater than 0
   * @param iDefault int Default on invalid value
   * @return Integer
   * @throws BaseServiceException if value beyond the given range.
   */
  public static Integer checkIntegerRangeValue(
      final Integer value,
      final String name,
      final int iMinimum,
      final int iMaximum,
      final int iDefault)
      throws BaseServiceException {
    Integer result = iDefault;
    if (value != null) {
      if (value < iMinimum) {
        throw new BaseServiceException(
            "'" + name + "' requires a minimum value of " + iMinimum, HttpStatus.BAD_REQUEST);
      }
      if (value > iMaximum && iMaximum > 0) {
        throw new BaseServiceException(
            "'" + name + "' requires a maximum value of " + iMaximum, HttpStatus.BAD_REQUEST);
      }
      result = value;
    }
    return result;
  }

  /**
   * Helper method to check if the given integer is between the minimum and maximum values
   * inclusive. If the given integer is null, use the default value.
   *
   * @param value Integer
   * @param name String
   * @param iMinimum long Minimum value
   * @param iMaximum long Maximum range if greater than 0
   * @throws BaseServiceException if value beyond the given range.
   */
  public static void checkLongRangeValue(
      final Long value, final String name, final long iMinimum, final long iMaximum)
      throws BaseServiceException {
    if (value != null) {
      if (value < iMinimum) {
        throw new BaseServiceException(
            "'" + name + "' requires a minimum value of " + iMinimum, HttpStatus.BAD_REQUEST);
      }
      if (value > iMaximum && iMaximum > 0) {
        throw new BaseServiceException(
            "'" + name + "' requires a maximum value of " + iMaximum, HttpStatus.BAD_REQUEST);
      }
    }
  }

  /**
   * Check if a given String is a valud UUID.
   *
   * @param uuid String
   * @param name String
   */
  public static void checkUUID(final String uuid, final String name) throws BaseServiceException {
    checkNullOrEmptyParameter(uuid, name);
    // 0123456789012345678901234567890123456789
    // a659bbfb-a584-4abf-b8f8-05a8dca4fc4c
    if (uuid.length() == Constants.UUID_SIZE
        && uuid.charAt(8) == '-'
        && uuid.charAt(13) == '-'
        && uuid.charAt(18) == '-'
        && uuid.charAt(23) == '-') {
      return;
    }
    throw new BaseServiceException("'" + name + "' is not a valid UUID", HttpStatus.BAD_REQUEST);
  }

  /**
   * Convert java.util.Date into LocalDateTime.
   *
   * @param date java.util.Date
   * @return LocalDateTime
   * @see {@link com.albon.auth.util.Helper#asLocalDateTime(java.time.LocalDateTime)}.
   */
  public static LocalDateTime asLocalDateTime(java.util.Date date) {
    return Helper.asLocalDateTime(date);
  }

  /**
   * Convert String to Long.
   *
   * @param value String
   * @param lDefault long
   * @return Long
   */
  public static Long parseLong(final String value, final long lDefault) {
    Long number;
    try {
      number = Long.parseLong(value);
    } catch (NumberFormatException ignore) {
      number = lDefault;
    }
    return number;
  }

  /**
   * Parse String to Long.
   *
   * @param value String
   * @param name String
   * @return Long
   */
  public static Long parseLongEx(final String value, final String name) {
    Long number = 0L;
    try {
      number = Long.parseLong(value);
    } catch (NumberFormatException ignore) {
      CommonUtil.BadRequestException("" + name + " '" + value + "' must be an Integer");
    }
    return number;
  }

  /**
   * Helper method to limit the length of String from HTTP parameter call.
   *
   * @param value String
   * @param limit int
   * @return String not null but can be empty.
   */
  public static String getString(final String value, final int limit) {
    String duplicate;
    if (value == null) {
      duplicate = "";
    } else if (value.length() > limit) {
      duplicate = new String(value.substring(0, limit));
    } else {
      duplicate = new String(value);
    }
    return duplicate;
  }

  /**
   * Helper method to limit the length of String to 2048.
   *
   * @param value String
   * @return String not null but can be empty.
   * @see {@link com.tuneurl.webrtc.util.value.Constants#ERROR_MESSAGES_LENGTH}.
   */
  public static String getStringEx(final String value) {
    return CommonUtil.getString(value, Constants.ERROR_MESSAGES_LENGTH);
  }

  /**
   * Convert to Url encoded String.
   *
   * @param data String
   * @param name String
   * @param size int
   * @return String
   * @throws BaseServiceException If null or invalid encoding.
   */
  public static String urlDecode(final String data, final String name, final int size)
      throws BaseServiceException {
    checkNullParameter(data, name);
    String raw = getString(data, size);
    String clean = null;
    try {
      clean = java.net.URLDecoder.decode(raw, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      BadRequestException("The Character Encoding is not supported for '" + name + "'");
    }
    return clean;
  }

  /**
   * Validate given String and limit it size.
   *
   * @param name String variable name
   * @param value String value to check
   * @param size Integere value size limit
   * @return String
   * @throws BaseServiceException The exception if value is <code>null</code>, or trim <code>empty
   *     </code>
   */
  public static String getValidString(final String name, final String value, final int size)
      throws BaseServiceException {
    String data = null;
    if (value == null) {
      CommonUtil.BadRequestException("The '" + name + "' cannot be null.");
      // NOTREACH
    } else {
      data = value.trim();
      if (data.isEmpty()) {
        CommonUtil.BadRequestException("The '" + name + "' cannot be empty.");
        // NOTREACH
      } else if (data.length() > size) {
        data = data.substring(0, size);
      }
    }
    return new String(data);
  }

  /**
   * Generate an HTTP 400 Bad Request exception.
   *
   * @param msg The Error message
   * @throws BaseServiceException The exception.
   */
  public static void BadRequestException(final String msg) throws BaseServiceException {
    throw new BaseServiceException(msg, HttpStatus.BAD_REQUEST);
  }

  /**
   * Generate an HTTP 401 Unauthorized exception.
   *
   * @param msg The Error message
   * @throws BaseServiceException The exception.
   */
  public static void NotAuthorizedException(final String msg) throws BaseServiceException {
    throw new BaseServiceException(msg, HttpStatus.UNAUTHORIZED);
  }

  /**
   * Generate an HTTP 403 Forbidden exception.
   *
   * @param msg The Error message
   * @throws BaseServiceException The exception.
   */
  public static void ForbiddenException(final String msg) throws BaseServiceException {
    throw new BaseServiceException(msg, HttpStatus.FORBIDDEN);
  }

  /**
   * Generate an HTTP 404 Not Found exception.
   *
   * @param msg The Error message
   * @throws BaseServiceException The exception.
   */
  public static void NotFoundException(final String msg) throws BaseServiceException {
    throw new BaseServiceException(msg, HttpStatus.NOT_FOUND);
  }

  /**
   * Generate an HTTP 429 Too Many Request exception.
   *
   * @param msg The Error message
   * @throws BaseServiceException The exception.
   */
  public static void TooManyRequestException(final String msg) throws BaseServiceException {
    throw new BaseServiceException(msg, HttpStatus.TOO_MANY_REQUESTS);
  }

  /**
   * Generate an HTTP 500 Internal Server exception.
   *
   * @param msg The Error message
   * @throws BaseServiceException The exception.
   */
  public static void InternalServerException(final String msg) throws BaseServiceException {
    throw new BaseServiceException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Generate an HTTP 503 - Retry Service not available.
   *
   * @param msg The Error message
   * @throws BaseServiceException The exception.
   */
  public static void RetryServiceNotAvailableException(final String msg)
      throws BaseServiceException {
    throw new BaseServiceException(msg, HttpStatus.SERVICE_UNAVAILABLE);
  }

  /**
   * Helper method to update SdkUser from SdkUserEntry.
   *
   * @param user SdkUser
   * @param entry SdkUserEntry
   */
  public static void translateSdkUser(SdkUser user, SdkUserEntry entry) {
    user.setEmail(entry.getEmail());
    user.setFullname(entry.getFullname());
    user.setRoles(entry.getRoles());
    user.setLdap(0L);
  }

  /**
   * Helper method to update SdkUser from SdkUserUpdate.
   *
   * @param user SdkUser
   * @param update SdkUserUpdate
   */
  public static void updateSdkUser(SdkUser user, SdkUserUpdate update) {
    String name = update.getName();
    if (!Helper.isStringNullOrEmpty(name)) {
      user.setFullname(name);
    }
    String roles = update.getRoles();
    if (!Helper.isStringNullOrEmpty(roles)) {
      user.setRoles(roles);
    }
    Long ldap = update.getLdap();
    if (ldap != null) {
      user.setLdap(ldap);
    }
  }

  /**
   * Helper method to update SessionData from SessionDataEntry.
   *
   * @param sesdata SessionData
   * @param entry SessionDataEntry
   */
  public static void translateSessionData(SessionData sesdata, SessionDataEntry entry) {
    sesdata.setLdap(entry.getLdap());
    sesdata.setIpaddr(entry.getIpaddr());
    sesdata.setSessionStart(entry.getSessionStart());
    sesdata.setSessionEnd(entry.getSessionEnd());
  }

  /**
   * Helper method to update SessionData from SessionDataUpdate.
   *
   * @param sesdata SessionData
   * @param update SessionDataUpdate
   */
  public static void updateSessionData(SessionData sesdata, SessionDataUpdate update) {
    sesdata.setSessionEnd(update.getSessionEnd());
  }

  /**
   * Helper method to update LdapInfo from LdapInfoEntry.
   *
   * @param info LdapInfo
   * @param entry LdapInfoEntry
   */
  public static void translateLdapInfo(LdapInfo info, LdapInfoEntry entry) {
    info.setPassword(entry.getPassword());
    info.setUuid(entry.getUuid());
    info.setStatus(entry.getStatus());
  }

  /**
   * Helper method to update LdapInfo from LdapInfoUpdate.
   *
   * @param info LdapInfo
   * @param update LdapInfoUpdate
   */
  public static void updateLdapInfo(LdapInfo info, LdapInfoUpdate update) {
    info.setPassword(update.getPassword());
    info.setStatus(update.getStatus());
  }

  /**
   * Helper method to generate BaseServiceException if Email not on sdk_user table.
   *
   * @param email String
   * @return BaseServiceException
   * @see {@link com.tuneurl.webrtc.util.service.impl.SdkUserServiceImpl#find(java.lang.String)}.
   */
  public static BaseServiceException generateSdkUserNotFound(final String email) {
    return new BaseServiceException("Email '" + email + "' not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Helper method to generate BaseServiceException if User ID not on sdk_user table.
   *
   * @param id long
   * @return BaseServiceException
   * @see {@link com.tuneurl.webrtc.util.service.impl.SdkUserServiceImpl#find(java.lang.long)}.
   */
  public static BaseServiceException generateSdkUserNotFound(final long id) {
    return new BaseServiceException("User ID '" + id + "' not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Helper method to generate BaseServiceException if Alias not on sdk_user table.
   *
   * @param alias String
   * @return BaseServiceException
   * @see {@link
   *     com.tuneurl.webrtc.util.service.impl.SdkUserServiceImpl#findByAlias(java.lang.String)}.
   */
  public static BaseServiceException generateSdkUserAliasNotFound(final String alias) {
    return new BaseServiceException("Alias '" + alias + "' not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Helper method to generate BaseServiceException if IP Addr not on session_data table.
   *
   * @param ipaddr String
   * @return BaseServiceException
   * @see {@link
   *     com.tuneurl.webrtc.util.service.impl.SessionDataServiceImpl#find(java.lang.String)}.
   */
  public static BaseServiceException generateSessionDataNotFound(final String ipaddr) {
    return new BaseServiceException("IP Address '" + ipaddr + "' not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Helper method to generate BaseServiceException if LDAP ID not on session_data table.
   *
   * @param ldap long
   * @return BaseServiceException
   * @see {@link com.tuneurl.webrtc.util.service.impl.SessionDataServiceImpl#find(java.lang.long)}.
   */
  public static BaseServiceException generateSessionDataNotFound(final long ldap) {
    return new BaseServiceException("LDAP ID '" + ldap + "' not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Helper method to generate BaseServiceException if UUID not on ldap_info table.
   *
   * @param uuid String
   * @return BaseServiceException
   * @see {@link com.tuneurl.webrtc.util.service.impl.LdapInfoServiceImpl#find(java.lang.String)}.
   */
  public static BaseServiceException generateLdapInfoNotFound(final String uuid) {
    return new BaseServiceException("LDAP UUID '" + uuid + "' not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Helper method to generate BaseServiceException if LDAP ID not on ldap_info table.
   *
   * @param id long
   * @return BaseServiceException
   * @see {@link com.tuneurl.webrtc.util.service.impl.LdapInfoServiceImpl#find(java.lang.long)}.
   */
  public static BaseServiceException generateLdapInfoNotFound(final long id) {
    return new BaseServiceException("LDAP ID '" + id + "' not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Helper method to generate BaseServiceException if UUID not on customer_access table.
   *
   * @param uuid String Customer access UUID
   * @return BaseServiceException
   * @see {@link
   *     com.tuneurl.webrtc.util.service.impl.CustomerAccessDatabaseServiceImpl#find(java.lang.String)}.
   */
  public static BaseServiceException generateCustomerAccessDatabaseNotFound(final String uuid) {
    return new BaseServiceException(
        "Customer access UUID '" + uuid + "' not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Helper method to generate BaseServiceException if tuneUrlID not on customer_access table.
   *
   * @param tuneUrlID long TuneURL ID
   * @return BaseServiceException
   * @see {@link
   *     com.tuneurl.webrtc.util.service.impl.CustomerAccessDatabaseServiceImpl#find(java.lang.long)}.
   */
  public static BaseServiceException generateCustomerAccessDatabaseNotFound(final long tuneUrlID) {
    return new BaseServiceException(
        "TuneURL ID '" + tuneUrlID + "' not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Helper method to generate BaseServiceException if TuneURL Type not on records_type table.
   *
   * @param type String TuneURL type
   * @return BaseServiceException
   * @see {@link
   *     com.tuneurl.webrtc.util.service.impl.RecordsTypeDatabaseServiceImpl#find(java.lang.String)}.
   */
  public static BaseServiceException generateRecordsTypeDatabaseNotFound(final String type) {
    return new BaseServiceException("TuneURL type '" + type + "' not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Helper method to generate BaseServiceException if Type ID not on records_type table.
   *
   * @param typeID long Type ID
   * @return BaseServiceException
   * @see {@link
   *     com.tuneurl.webrtc.util.service.impl.RecordsTypeDatabaseServiceImpl#find(java.lang.long)}.
   */
  public static BaseServiceException generateRecordsTypeDatabaseNotFound(final long typeID) {
    return new BaseServiceException("Type ID '" + typeID + "' not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Helper method to generate BaseServiceException if TuneURL Type not on records_type table.
   *
   * @param userId String TuneURL type
   * @return BaseServiceException
   * @see {@link
   *     com.tuneurl.webrtc.util.service.impl.InterestsDatabaseServiceImpl#find(java.lang.String)}.
   */
  public static BaseServiceException generateInterestsDatabaseNotFound(final String userId) {
    return new BaseServiceException("UserID '" + userId + "' not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Helper method to generate BaseServiceException if Type ID not on records_type table.
   *
   * @param tuneUrlID long Type ID
   * @return BaseServiceException
   * @see {@link
   *     com.tuneurl.webrtc.util.service.impl.InterestsDatabaseServiceImpl#find(java.lang.long)}.
   */
  public static BaseServiceException generateInterestsDatabaseNotFound(final long tuneUrlID) {
    return new BaseServiceException(
        "TuneURL_ID '" + tuneUrlID + "' not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Helper method to generate BaseServiceException if Type ID not on records_type table.
   *
   * @param userID String UserID
   * @param tuneUrlID long TuneURL ID
   * @return BaseServiceException
   * @see {@link
   *     com.tuneurl.webrtc.util.service.impl.InterestsDatabaseServiceImpl#findByUserIDByTuneUrlID(
   *     com.tuneurl.webrtc.util.model.SdkUser, java.lang.String, java.lang.long)}.
   */
  public static BaseServiceException generatefindByUserIDByTuneUrlIDNotFound(
      final String userID, final Long tuneUrlID) {
    return new BaseServiceException(
        "UserID '" + userID + "' with TuneURL_ID '" + tuneUrlID + "' not found.",
        HttpStatus.NOT_FOUND);
  }

  /**
   * Helper method to generate exception for missing conversionId.
   *
   * @param conversionId long Conversion ID
   * @return BaseServiceException
   * @see {@link
   *     com.tuneurl.webrtc.util.service.impl.AudioStreamDatabaseServiceImpl#find(java.lang.long)}
   */
  public static BaseServiceException generateAudioStreamDatabaseNotFound(final long conversionId) {
    return new BaseServiceException(
        "Audio Stream Conversion ID '" + conversionId + "' not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Helper method to generate exception for missing Audio Stream fileName.
   *
   * @param fileName String File name
   * @return BaseServiceException
   * @see {@link
   *     com.tuneurl.webrtc.util.service.impl.AudioStreamDatabaseServiceImpl#find(java.lang.String)}
   */
  public static BaseServiceException generateAudioStreamDatabaseNotFound(final String fileName) {
    return new BaseServiceException(
        "Audio Stream fileName '" + fileName + "' not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Helper method to generate exception for missing Audio Stream ID.
   *
   * @param asId long Audio Stream ID
   * @return BaseServiceException
   * @see {@link
   *     com.tuneurl.webrtc.util.service.impl.AudioStreamTrainingChannelServiceImpl#find(java.lang.long)}
   */
  public static BaseServiceException generateAudioStreamTrainingChannelNotFound(final long asId) {
    return new BaseServiceException(
        "Audio Stream ID '" + asId + "' not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Helper method to generate exception for missing Audio Stream fileName.
   *
   * @param fileName String File name
   * @return BaseServiceException
   * @see {@link
   *     com.tuneurl.webrtc.util.service.impl.AudioStreamTrainingChannelServiceImpl#find(java.lang.String)}
   */
  public static BaseServiceException generateAudioStreamTrainingChannelNotFound(
      final String fileName) {
    return new BaseServiceException(
        "Audio Stream fileName '" + fileName + "' not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Helper method to generate exception for missing Channel ID.
   *
   * @param channel long Channel ID
   * @return BaseServiceException
   * @see {@link
   *     com.tuneurl.webrtc.util.service.impl.AudioStreamTrainingChannelServiceImpl#findChannelById(java.lang.long)}
   */
  public static BaseServiceException generateAudioStreamTrainingChannelChannelNotFound(
      final long channel) {
    return new BaseServiceException(
        "Channel ID '" + channel + "' not found.", HttpStatus.NOT_FOUND);
  }
}
