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

import org.springframework.http.HttpStatus;

/**
 * The package base exception.
 *
 * <p>BaseServiceException and its subclasses are unchecked exceptions.
 *
 * <p>Unchecked exceptions do not need to be declared in a method or constructor's throws clause if
 * they can be thrown by the execution of the method or constructor and propagate outside the method
 * or constructor boundary.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public class BaseServiceException extends RuntimeException {
  /** The Error message. */
  private final String message;

  /** The Error HTTP status. */
  private final HttpStatus httpStatus;

  /**
   * Retrieve the Error message.
   *
   * @return String
   */
  @Override
  public String getMessage() {
    return this.message;
  }

  /**
   * Retrieve the Error HTTP status.
   *
   * @return HttpStatus
   */
  public HttpStatus getHttpStatus() {
    return this.httpStatus;
  }

  /**
   * Constructor with two input.
   *
   * @param message The Error message to set.
   * @param httpStatus The HTTP status to set.
   */
  public BaseServiceException(final String message, final HttpStatus httpStatus) {
    super(message);
    this.message = message;
    this.httpStatus = httpStatus;
  }

  /**
   * Constructor with single input.
   *
   * <p>The httpStatus is set to INTERAL_SERVER_ERROR (500).
   *
   * @param message The Error message to set.
   */
  public BaseServiceException(final String message) {
    this(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
