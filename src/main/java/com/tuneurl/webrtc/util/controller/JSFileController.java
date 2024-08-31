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

package com.tuneurl.webrtc.util.controller;

import com.tuneurl.webrtc.util.util.CommonUtil;
import java.io.File;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * The JSFileController class use to monitor who is accessing the WASM files.
 *
 * @author albonteddy@gmail.com
 * @version 1.14
 */
@RestController
@RequestMapping("/")
public class JSFileController extends BaseController {

  @Value("${server.webapp.folder}")
  private String absoluteWebappPath;

  // Default Constructor.
  public JSFileController() {
    super();
  }

  // Just monitor who is accessing this resources
  private static final String[] JS_WASM_FILES = {
    "js-sdk-1.0.5.wasm",
    "jqbs64.js",
    "js-audio-streams-radio.js",
    "js-sdk-1.0.5.min.js",
    "js-sdk-audio-data-1.0.5.js",
    "js-sdk-1.0.5.js",
    "swagger-initializer.js",
    "js-sdk-canvas-1.0.5.js",
    "audio-data-class.js",
    "canvas-tool-class.js"
  };

  // SEE https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
  private static final String contentTypeWasm = "application/wasm";
  private static final String contentTypeJs = "text/javascript";

  private boolean isFileToMonitor(final String fileName) {
    for (String jsWasmFile : JS_WASM_FILES) {
      if (fileName.endsWith(jsWasmFile)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Helper to get files from webapp/js folder.
   *
   * @param fileName String
   * @param httpRequest HttpServletRequest
   * @param httpResponse HttpServletResponse
   * @param outputStream OutputStream
   */
  private void getFile(
      final String fileName,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse,
      OutputStream outputStream,
      final String contentType) {

    final String signature = "getFile";

    // Directly read from file
    final String endPoint = "/js/" + fileName;
    final String theFile = String.format("%s%s", absoluteWebappPath, endPoint);

    // Save analytics for read access on these files.
    if (isFileToMonitor(endPoint)) {
      super.saveAnalytics(signature, httpRequest);
    }
    String fileNameOnDisposition = fileName;
    if (contentType.indexOf("wasm") > 0) {
      fileNameOnDisposition = null;
    }
    // Set the appropriate content type
    super.writeResponseStreamResult(
        endPoint, outputStream, contentType, httpResponse, theFile, fileNameOnDisposition);
  }

  // access the mp3 or wav file
  @RequestMapping(value = "/dev/v4/audio/{audioFile}", method = RequestMethod.GET)
  @CrossOrigin("*")
  public ResponseEntity<byte[]> getAudioFileV4(
      @PathVariable(value = "audioFile", required = true) String audioFile,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {
    String contentType = audioFile.endsWith(".mp3") ? "audio/mpeg" : "audio/x-wav";
    String endPoint = "/audio/" + audioFile;
    final String accessDeniedMessages = "Access denied to " + endPoint;
    final String theFile = String.format("%s%s", absoluteWebappPath, endPoint);
    byte[] data = null;
    File file = null;
    try {
      file = new File(theFile);
    } catch (Exception ignore) {
      CommonUtil.NotFoundException("File not found " + endPoint);
      /*NOTREACH*/
    }
    data = readFileToStream(file, accessDeniedMessages);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Length", String.valueOf(data.length));
    headers.set("Content-Type", contentType);
    return new ResponseEntity<>(data, headers, HttpStatus.OK);
  }

  // protect access to js-sdk-1.0.5.wasm
  @RequestMapping(
      value = "/js/js-sdk-1.0.5.wasm",
      method = RequestMethod.GET,
      produces = "application/wasm")
  @CrossOrigin("*")
  public void getJsSdk105Wasm(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse, OutputStream outputStream) {
    getFile("js-sdk-1.0.5.wasm", httpRequest, httpResponse, outputStream, contentTypeWasm);
  }

  // protect access to jqbs64.js
  @RequestMapping(value = "/js/jqbs64.js", method = RequestMethod.GET, produces = "text/javascript")
  @CrossOrigin("*")
  public void getJqbs64Js(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse, OutputStream outputStream) {
    getFile("jqbs64.js", httpRequest, httpResponse, outputStream, contentTypeJs);
  }

  // protect access to js-audio-streams-radio.js
  @RequestMapping(
      value = "/js/js-audio-streams-radio.js",
      method = RequestMethod.GET,
      produces = "text/javascript")
  @CrossOrigin("*")
  public void getJsAudioStreamsRadioJs(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse, OutputStream outputStream) {
    getFile("js-audio-streams-radio.js", httpRequest, httpResponse, outputStream, contentTypeJs);
  }

  // protect access to js-sdk-1.0.5.min.js
  @RequestMapping(
      value = "/js/js-sdk-1.0.5.min.js",
      method = RequestMethod.GET,
      produces = "text/javascript")
  @CrossOrigin("*")
  public void getJsSdk105minJs(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse, OutputStream outputStream) {
    getFile("js-sdk-1.0.5.min.js", httpRequest, httpResponse, outputStream, contentTypeJs);
  }

  // protect access to js-sdk-audio-data-1.0.5.js
  @RequestMapping(
      value = "/js/js-sdk-audio-data-1.0.5.js",
      method = RequestMethod.GET,
      produces = "text/javascript")
  @CrossOrigin("*")
  public void getJsSdkAudioData105Js(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse, OutputStream outputStream) {
    getFile("js-sdk-audio-data-1.0.5.js", httpRequest, httpResponse, outputStream, contentTypeJs);
  }

  // protect access to js-sdk-canvas-1.0.5.js
  @RequestMapping(
      value = "/js/js-sdk-canvas-1.0.5.js",
      method = RequestMethod.GET,
      produces = "text/javascript")
  @CrossOrigin("*")
  public void getJsSdkCanvas105Js(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse, OutputStream outputStream) {
    getFile("js-sdk-canvas-1.0.5.js", httpRequest, httpResponse, outputStream, contentTypeJs);
  }

  // protect access to js-sdk-1.0.5.js
  @RequestMapping(
      value = "/js/js-sdk-1.0.5.js",
      method = RequestMethod.GET,
      produces = "text/javascript")
  @CrossOrigin("*")
  public void getJsSdk105Js(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse, OutputStream outputStream) {
    getFile("js-sdk-1.0.5.js", httpRequest, httpResponse, outputStream, contentTypeJs);
  }

  // protect access to audio-data-class.js
  @RequestMapping(
      value = "/js/audio-data-class.js",
      method = RequestMethod.GET,
      produces = "text/javascript")
  @CrossOrigin("*")
  public void getAudioDataClassJs(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse, OutputStream outputStream) {
    getFile("audio-data-class.js", httpRequest, httpResponse, outputStream, contentTypeJs);
  }

  // protect access to canvas-tool-class.js
  @RequestMapping(
      value = "/js/canvas-tool-class.js",
      method = RequestMethod.GET,
      produces = "text/javascript")
  @CrossOrigin("*")
  public void getCanvasToolClassJs(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse, OutputStream outputStream) {
    getFile("canvas-tool-class.js", httpRequest, httpResponse, outputStream, contentTypeJs);
  }

  // protect access to swagger-initializer.js
  @RequestMapping(
      value = "/js/swagger-initializer.js",
      method = RequestMethod.GET,
      produces = "text/javascript")
  @CrossOrigin("*")
  public void getSwaggerInitializerJs(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse, OutputStream outputStream) {
    getFile("swagger-initializer.js", httpRequest, httpResponse, outputStream, contentTypeJs);
  }

  // protect access to swagger-ui-bundle.js
  @RequestMapping(
      value = "/js/swagger-ui-bundle.js",
      method = RequestMethod.GET,
      produces = "text/javascript")
  @CrossOrigin("*")
  public void getSwaggerUiBundleJs(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse, OutputStream outputStream) {
    getFile("swagger-ui-bundle.js", httpRequest, httpResponse, outputStream, contentTypeJs);
  }

  // protect access to swagger-ui-es-bundle-core.js
  @RequestMapping(
      value = "/js/swagger-ui-es-bundle-core.js",
      method = RequestMethod.GET,
      produces = "text/javascript")
  @CrossOrigin("*")
  public void getSwaggerUiEsBundleCoreJs(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse, OutputStream outputStream) {
    getFile("swagger-ui-es-bundle-core.js", httpRequest, httpResponse, outputStream, contentTypeJs);
  }

  // protect access to swagger-ui-es-bundle.js
  @RequestMapping(
      value = "/js/swagger-ui-es-bundle.js",
      method = RequestMethod.GET,
      produces = "text/javascript")
  @CrossOrigin("*")
  public void getSwaggerUiEsBundleJs(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse, OutputStream outputStream) {
    getFile("swagger-ui-es-bundle.js", httpRequest, httpResponse, outputStream, contentTypeJs);
  }

  // protect access to swagger-ui.js
  @RequestMapping(
      value = "/js/swagger-ui.js",
      method = RequestMethod.GET,
      produces = "text/javascript")
  @CrossOrigin("*")
  public void getSwaggerUiJs(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse, OutputStream outputStream) {
    getFile("swagger-ui.js", httpRequest, httpResponse, outputStream, contentTypeJs);
  }

  // protect access to swagger-ui-standalone-preset.js
  @RequestMapping(
      value = "/js/swagger-ui-standalone-preset.js",
      method = RequestMethod.GET,
      produces = "text/javascript")
  @CrossOrigin("*")
  public void getSwaggerUiStandalonePresetJs(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse, OutputStream outputStream) {
    getFile(
        "swagger-ui-standalone-preset.js", httpRequest, httpResponse, outputStream, contentTypeJs);
  }
}
