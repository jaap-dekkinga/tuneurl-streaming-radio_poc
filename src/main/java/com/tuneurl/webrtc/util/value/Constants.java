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

package com.tuneurl.webrtc.util.value;

/**
 * This Constant class will hold common definitions for the package.
 *
 * <p><strong>Thread Safety: </strong>This class is not mutable thus thread safe.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public final class Constants {

  /** Error messages limit. */
  public static final int ERROR_MESSAGES_LENGTH = 2048;

  /** Fingerprinting Song Name size. */
  public static final int SONG_NAME_SIZE = 50;

  /** Fingerprinting File SHA1 size. */
  public static final int FILE_SHA1_SIZE = 20;

  /** Fingerprints binary size. */
  public static final int FINGERPRINTS_BIN_SIZE = 10;

  /**
   * The maximum length the Email address can have as per RFC 2821.
   *
   * @see
   *     https://www.rfc-editor.org/errata/eid1690#:~:text=It%20should%20say%3A-,In%20addition%20to%20restrictions%20on%20syntax%2C%20there%20is%20a%20length,total%20length%20of%20320%20characters.
   */
  @SuppressWarnings("JavadocReference")
  public static final int EMAIL_LENGTH = 254;

  /** User login name length. see EMAIL_LENGTH. */
  public static final int USER_NAME_SIZE = 254;

  /** User password length. */
  public static final int USER_PASSWORD_SIZE = 32;

  /** User password length in hEx + (32x8)/6 */
  public static final int USER_PASSWORD_SIZE_IN_HEX = 47;

  /** Hex encoding prefix */
  public static final String HEX_PREFIX = "hEx";

  /** Hash User password length. */
  public static final int HASHED_PASSWORD_SIZE = 63;

  /** Limit parameter as digit with this size. */
  public static final int DIGIT_SIZE = 32;

  /** User roles 3x16. */
  public static final int ROLES_SIZE = 48;

  /** UUID size. */
  public static final int UUID_SIZE = 36;

  /** IP version 6 is 128bits and its size in char is 45. */
  public static final int IPV6_ADDRESS_SIZE = 45;

  /** Access control allow methods String. */
  public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
  /** Session expired? */
  public static final String SESSION_EXPIRED_LOGIN_AGAIN =
      "Login session expired, Please login again";
  /** Not allowed to execute the request. */
  public static final String NOT_ALLOWED_TO_EXECUTE_THE_REQUEST =
      "Not Allowed to Execute the Request";
  /** Either invalid Email or Password. */
  public static final String INVALID_USER_NAME_OR_PASSWORD = "Invalid User name and/or password";

  public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server error";

  /** Token prefix. */
  public static final String TOKEN_PREFIX = "t0K3n";
  /** Token prefix size. */
  public static final int TOKEN_PREFIX_SIZE = 5;
  /** Token header name. */
  public static final String X_JWT_HEADER_NAME = "x-jwt-token";

  /** Zero ID value. */
  public static final Long ZERO_ID = 0L;

  /** Use token from Authorization: Bearer. */
  public static final boolean LOGIN_AUTHORIZATION_BEARER = true;

  /** 250 user x 10M x 8 per day := 20GB space per day. */
  public static final long FINGERPRINTING_DAILY_LIMIT = 8;

  /** 24 x 60 x 60 = 86400 div 250 := 345.6 seconds */
  public static final long FINGERPRINTING_RATE_LIMIT = 352;

  /** Reached 8 counts finger printing limit in a day. */
  public static final String REACH_DAY_REQUEST_LIMIT =
      "You have reached the 8 counts finger printing limit in a day,";

  public static final String X_SDK_CLIENT_ID_HEADER_NAME = "x-sdk-client-id";
  public static final String X_SDK_CLIENT_ID_HEADER_NAME_ERROR_STRING =
      "Must have a valid SDK Client ID to use the service";
  public static final String X_SDK_CLIENT_ID_HEADER_NAME_MISMATCH_ERROR_STRING =
      "Must use correct SDK Client ID to use the service";

  public static final String X_AUDIO_TITLE_HEADER_NAME = "x-audio-title";
  public static final String X_AUDIO_TITLE_HEADER_NAME_ERROR_STRING = "Missing Audio title.";

  public static final String INVALID_AUDIO_FILE_ERROR_STRING = "Invalid Audio file.";

  public static final String ACCESS_DENIED_ERROR_STRING = "Access denied";

  /** page minimum value. */
  public static final int PAGE_VALUE_MINIMUM = 1;
  /** page maximum value. */
  public static final int PAGE_VALUE_MAXIMUM = 100;
  /** page default value. */
  public static final int PAGE_VALUE_DEFAULT = 100;

  public static final String CUSTOMER_ACCESS_RECORD_SHOULD_BE_AN_ARRAY =
      "Request body should be an array of the objects where Customer_ID(string), TuneURL_ID(INT as string)";

  /** Record Type name size. */
  public static final int RECORD_TYPE_SIZE = 32;

  /** Default UUID. */
  public static final String ZERO_UUID = "00000000-1111-2222-33333333333333333";

  /** Grant type for Refreshing JWT token. */
  public static final String GRANT_TYPE_REFRESH_TOKEN_NAME = "refresh_token";

  /** Hint for grant type */
  public static final String INVALID_GRANT_TYPE_REFRESH_TOKEN_ERROR =
      "Specify grant_type=refresh_token";

  /** Hit for grant type */
  public static final String FORBIDDEN_INVALID_TOKEN_ERROR =
      "Grant access denied - JWT token mismatch.";

  /** Interests DB action size */
  public static final int INTEREST_ACTION_SIZE = 16;

  /** Interests DB users size */
  public static final int INTEREST_USER_SIZE = 16;

  /** AudioStreamDatabase ID */
  public static final long AUDIOSTREAM_MIN_ID = 19003001L;

  /** AudioStreamDatabase DB filename size */
  public static final int AUDIOSTREAM_FILENAME_SIZE = 48;

  /** AudioStreamDatabase DB filename size */
  public static final int AUDIOSTREAM_FILENAME_EX_SIZE = 384;

  /** AudioStreamDatabase DB URL size */
  public static final int AUDIOSTREAM_URL_SIZE = 4096;

  /** AudioStreamDatabase DB Category. */
  public static final int AUDIOSTREAM_CATEGORY_SIZE = 64;

  /** AudioStreamDatabase DB Title. */
  public static final int AUDIOSTREAM_TITLE_SIZE = 512;

  /** AudioStreamDatabase Popup messages. */
  public static final int AUDIOSTREAM_POPUP_SIZE = 8192;

  /** AudioStreamData minimum duration value */
  public static final long AUDIOSTEAM_MIN_DURATION = 10L;

  /** AudioStreamData maximum duration value */
  public static final long AUDIOSTEAM_MAX_DURATION = 800L;

  /** AudioStreamData 5 seconds duration value */
  public static final long AUDIOSTEAM_FIVE_SECOND_DURATION = 5L;

  /** AudioStreamData filename size */
  public static final int AUDIOSTEAM_MAXIMUM_FILE_SIZE = 16;

  public static final int AUDIOSTEAM_FILENAME_MIN_DOTPOS = 8;
  public static final int AUDIOSTEAM_FILENAME_MAX_DOTPOS = 12;

  /** Bash script to run WebRTC process */
  public static final String RUN_WEBRTC_SCRIPT = "run_webrtc_script.sh";

  public static final int AUDIOSTREAM_STATUS_INIT = 0;
  public static final int AUDIOSTREAM_STATUS_FINAL = 1;

  /* Channel minimum counts. */
  public static final long CHANNEL_MIN_COUNT = 1L;
  /* Channel maximum counts. */
  public static final long CHANNEL_MAX_COUNT = 12L;

  /* Fingerprint size minimum limit in second. */
  public static final long FINGERPRINT_MIN_DURATION = 1L; // in seconds

  /* Fingerprint size minimum limit in second. */
  public static final long FINGERPRINT_MIN_SEVEN_DURATION = 7L; // in seconds

  /* Fingerprint size maximum limit in second. */
  public static final long FINGERPRINT_MAX_DURATION = 480L; // in seconds

  /* Fingerprint score threshold. */
  public static final double FINGERPRINT_SCORE_MIN_THRESHOLD = 0.0; // 1.5100E-4;
  public static final double FINGERPRINT_SCORE_MAX_THRESHOLD = 0.0021;

  /* Fingerprint score threshold. */
  public static final double FINGERPRINT_SIMILAR_MIN_THRESHOLD = -0.25;
  public static final double FINGERPRINT_SIMILAR_MAX_THRESHOLD = 0.25;

  /* Search API URL */
  public static final String TUNEURL_SEARCH_API_URL =
      "https://pnz3vadc52.execute-api.us-east-2.amazonaws.com/dev/search-fingerprint";

  public static final boolean DEBUG_FINGERPRINTING = false;

  /* 100 millisecond elapse time. 20 gives 5 tags. */
  public static final int FINGERPRINT_INCREMENT_DELTA = 20;

  public static final int FRAME_LOWEST_VALUE = -214748364;

  /** Default constructor. */
  private Constants() {
    // Hidden
  }
}
