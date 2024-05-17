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

import com.albon.auth.util.LogMessage;
import org.apache.logging.log4j.LogManager;

/**
 * Class LogMessage implementation.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe.
 *
 * @author <albonteddy@gmail.com>
 * @version 1.1
 */
public class MessageLogger extends LogMessage {

  /** Default constructor. */
  public MessageLogger() {
    super();
  }

  static MessageLogger logger;
  /**
   * Get MessageLogger.
   *
   * @return MessageLogger
   */
  public static MessageLogger getMessageLoggerInstance() {
    if (logger == null) {
      logger = new MessageLogger();
      logger.setLogger(LogManager.getLogger(com.tuneurl.webrtc.util.util.MessageLogger.class));
    }
    return logger;
  }

  /**
   * Helper to write the Logs
   *
   * @param prefix String
   * @param signature String
   * @param parameters Objects
   */
  private void printLogs(final String prefix, final String signature, final Object... parameters) {
    if (super.getLogger() == null) return;
    final String sk = "{\"";
    final String bk = "\"";
    StringBuilder sb = new StringBuilder(sk + prefix + bk);
    int counts = 0;
    sb.append(":").append(sk).append(signature).append(bk);
    if (parameters != null) {
      sb.append(":[");
      for (Object obj : parameters) {
        String str = ((obj == null) ? "null" : obj.toString());
        if (++counts > 1) sb.append(",");
        sb.append(bk).append(str).append(bk);
      }
      sb.append("]");
    }
    sb.append("}}");
    super.getLogger().debug(sb.toString());
  }

  public void logEntry(final String signature, final Object... parameters) {
    printLogs("Entering", signature, parameters);
  }

  public void logExit(final String signature, final Object... parameters) {
    printLogs("Leaving", signature, parameters);
  }
}
