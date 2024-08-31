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

package com.tuneurl.webrtc.util.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.albon.auth.util.Helper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.tuneurl.webrtc.util.controller.dto.Error;
import com.tuneurl.webrtc.util.util.CommonUtil;
import com.tuneurl.webrtc.util.value.Constants;
import java.time.temporal.UnsupportedTemporalTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

/**
 * AdvisorErrorController class tells How to display exceptions.
 * <li>1.1 Added findUnrecognizedField and handleUnrecognizedPropertyException to trap caller's
 *     invalid field on input DTOs.
 * <li>1.2 Added handleUnsupportedTemporalTypeException
 * <li>1.13 Added call to CommonUtil.getStringEx() to limit error messages to 2048 {@link
 *     com.tuneurl.webrtc.util.value.Constants#ERROR_MESSAGES_LENGTH}.
 *
 * @author albonteddy@gmail.com
 * @version 1.13
 */
@ControllerAdvice
public class AdvisorErrorController {

  /**
   * Helper method to limit error message to Unrecognized field.
   *
   * @param msg String message to translate
   * @return String translated message or null
   */
  private String findUnrecognizedField(final String msg) {
    String comment;
    int index;
    if (msg == null) {
      comment = null;
    } else {
      index = msg.indexOf("reference chain: ");
      if (index > 0) {
        comment = "Unrecognized field at " + msg.substring(index + 17);
        index = comment.indexOf("])");
        if (index > 0) {
          comment = comment.substring(0, index + 1);
        }
      } else {
        index = msg.indexOf("Unrecognized field");
        if (index > 0) {
          comment = msg.substring(index);
          index = comment.indexOf(")");
          if (index > 0) {
            comment = comment.substring(0, index);
          }
        } else {
          // revert to original message.
          comment = null;
        }
      }
    }
    return comment;
  }

  @ExceptionHandler({org.apache.catalina.connector.ClientAbortException.class})
  public ResponseEntity<Error> handleCatalinaClientAbortException(
      org.apache.catalina.connector.ClientAbortException ex) {
    String msg = CommonUtil.getStringEx(ex.getMessage());
    HttpStatus status;
    if (msg.contains("Broken pipe")) {
      status = HttpStatus.OK;
    } else {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return new ResponseEntity<>(new Error(status, status.name()), status);
  }

  /**
   * Handle UnsupportedTemporalTypeException.
   *
   * @param ex UnsupportedTemporalTypeException
   * @return ResponseEntity &lt;Object>
   */
  @ExceptionHandler({UnsupportedTemporalTypeException.class})
  public ResponseEntity<Error> handleUnsupportedTemporalTypeException(
      UnsupportedTemporalTypeException ex) {
    Error error = new Error(INTERNAL_SERVER_ERROR, CommonUtil.getStringEx(ex.getMessage()));
    return new ResponseEntity<>(error, INTERNAL_SERVER_ERROR);
  }

  /**
   * Handle ThrowableError.
   *
   * @param ex ThrowableError
   * @return ResponseEntity &lt;Object>
   */
  @ExceptionHandler({Throwable.class})
  public ResponseEntity<Error> handleThrowableError(Throwable ex) {
    String msg = CommonUtil.getStringEx(ex.getMessage());
    String comment = findUnrecognizedField(msg);
    HttpStatus status = BAD_REQUEST;
    if (Helper.isStringNullOrEmpty(comment)) {
      comment = Helper.isStringNullOrEmpty(msg) ? Constants.INTERNAL_SERVER_ERROR_MESSAGE : msg;
      status = INTERNAL_SERVER_ERROR;
    }
    ex.printStackTrace();
    Error error = new Error(status, comment);
    return new ResponseEntity<>(error, status);
  }

  /**
   * Handle BaseServiceException.
   *
   * @param ex BaseServiceException
   * @return ResponseEntity &lt;Object>
   */
  @ExceptionHandler({BaseServiceException.class})
  public ResponseEntity<Error> handleBaseServiceException(BaseServiceException ex) {
    HttpStatus status = ex.getHttpStatus();
    if (status == null) status = BAD_REQUEST;
    Error error = new Error(status, CommonUtil.getStringEx(ex.getMessage()));
    return new ResponseEntity<>(error, status);
  }

  /**
   * Handle UnrecognizedPropertyException.
   *
   * @param ex UnrecognizedPropertyException
   * @return ResponseEntity &lt;Object>
   */
  @ExceptionHandler({UnrecognizedPropertyException.class})
  public ResponseEntity<Error> handleUnrecognizedPropertyException(
      UnrecognizedPropertyException ex) {
    // This will handle vulnerability issue when caller's input DTOs have invalid field.
    String msg = CommonUtil.getStringEx(ex.getMessage());
    String comment = findUnrecognizedField(msg);
    HttpStatus status = BAD_REQUEST;
    if (Helper.isStringNullOrEmpty(comment)) {
      if (Helper.isStringNullOrEmpty(msg)) {
        comment = Constants.INTERNAL_SERVER_ERROR_MESSAGE;
        status = HttpStatus.INTERNAL_SERVER_ERROR;
      } else {
        comment = msg;
      }
    }
    Error error = new Error(status, comment);
    return new ResponseEntity<>(error, status);
  }

  /**
   * Handle IllegalArgumentException.
   *
   * @param ex IllegalArgumentException
   * @return ResponseEntity &lt;Object>
   */
  @ExceptionHandler({IllegalArgumentException.class})
  public ResponseEntity<Error> handleIllegalArgumentException(IllegalArgumentException ex) {
    HttpStatus status = BAD_REQUEST;
    Error error = new Error(status, CommonUtil.getStringEx(ex.getMessage()));
    return new ResponseEntity<>(error, status);
  }

  /**
   * Handle AccessDeniedException.
   *
   * @param ex AccessDeniedException
   * @return ResponseEntity &lt;Object>
   */
  @ExceptionHandler({AccessDeniedException.class})
  public ResponseEntity<Error> handleAccessDeniedException(AccessDeniedException ex) {
    HttpStatus status = HttpStatus.FORBIDDEN;
    Error error = new Error(status, CommonUtil.getStringEx(ex.getMessage()));
    return new ResponseEntity<>(error, status);
  }

  /**
   * Handle MultipartException.
   *
   * @param ex MultipartException
   * @return ResponseEntity &lt;Object>
   */
  @ExceptionHandler(MultipartException.class)
  public ResponseEntity<Error> handleMultipartException(MultipartException ex) {
    HttpStatus status = BAD_REQUEST;
    Error error = new Error(status, CommonUtil.getStringEx(ex.getMessage()));
    return new ResponseEntity<>(error, status);
  }

  /**
   * Handle RequestRejectedException.
   *
   * @param ex RequestRejectedException
   * @return ResponseEntity &lt;Object>
   */
  @ExceptionHandler(RequestRejectedException.class)
  public ResponseEntity<Error> handleRequestRejectedException(RequestRejectedException ex) {
    HttpStatus status = BAD_REQUEST;
    Error error = new Error(status, CommonUtil.getStringEx(ex.getMessage()));
    return new ResponseEntity<>(error, status);
  }
}
