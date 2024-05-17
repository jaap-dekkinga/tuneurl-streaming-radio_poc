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

package com.tuneurl.webrtc.util.controller;

import com.albon.auth.dto.UserEntry;
import com.albon.auth.jwt.JwtTool;
import com.albon.auth.util.Helper;
import com.tuneurl.webrtc.util.controller.dto.Error;
import com.tuneurl.webrtc.util.model.*;
import com.tuneurl.webrtc.util.value.Constants;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * The SwaggerController class.
 *
 * @author albonteddy@gmail.com
 * @version 1.12
 */
@RestController
@RequestMapping("/")
public class SwaggerController extends BaseController {

  /** Swagger view page at webapp/WEB-INF/views/swagger_page.jsp */
  private static final String swaggerPage = "swagger_page";

  /** Swagger Route end-point. */
  private static final String swaggerRoute = "/v3/swagger";

  /** Access denied to applicationContext.xml */
  private static final String applicationContext = "/applicationContext.xml";

  @RequestMapping(
      value = "/sdk-test-page.html",
      produces = {"text/html"},
      method = {RequestMethod.GET})
  public ModelAndView getSdkTestPage(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    return new ModelAndView("sdk_test_page");
  }

  @RequestMapping(
      value = "/new-sdk-page.html",
      produces = {"text/html"},
      method = {RequestMethod.GET})
  public ModelAndView getNewSdkPage(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    return new ModelAndView("new_sdk_page");
  }

  @RequestMapping(
      value = applicationContext,
      produces = {"text/html"},
      method = {
        RequestMethod.OPTIONS,
        RequestMethod.HEAD,
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PATCH,
        RequestMethod.PUT,
        RequestMethod.DELETE
      })
  public ModelAndView getApplicationContextXml(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    return super.getModelViewErrorPage("403", applicationContext, "error-403");
  }

  /** Allow OPTIONS, GET. */
  @RequestMapping(
      value = swaggerRoute,
      produces = {"application/json"},
      method = {RequestMethod.OPTIONS, RequestMethod.HEAD})
  public ResponseEntity<Error> optionSwagger(HttpServletResponse httpResponse) {
    Error response = new Error("OK");
    super.allowGetOptionsHead(httpResponse);
    return new ResponseEntity<Error>(response, HttpStatus.OK);
  }

  /** Deny POST, PATCH, PUT, DELETE. */
  @RequestMapping(
      value = swaggerRoute,
      produces = {"application/json"},
      method = {RequestMethod.POST, RequestMethod.PATCH, RequestMethod.PUT, RequestMethod.DELETE})
  public ResponseEntity<Error> postSwagger(HttpServletResponse httpResponse) {
    Error response = new Error("Forbidden");
    super.allowGetOptionsHead(httpResponse);
    return new ResponseEntity<Error>(response, HttpStatus.FORBIDDEN);
  }

  /** Display Main page. */
  @RequestMapping(
      value = "/",
      produces = {"text/html"},
      method = {RequestMethod.GET})
  public ModelAndView getMainPage(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    SdkUser sdk = userService.getSdkUserByName("user@example.com");
    UserEntry userEntry = super.convertSdkUser(sdk);
    JwtTool jwtTool = super.setupJwtTool(Constants.LOGIN_AUTHORIZATION_BEARER);
    String token = jwtTool.generateToken(logger, userEntry);
    Map<String, Object> maps = new HashMap<String, Object>();
    maps.put("token", token);
    return new ModelAndView("audio_streams_test", maps);
  }

  /** Display Swagger UI. */
  @RequestMapping(
      value = swaggerRoute,
      produces = {"text/html"},
      method = {RequestMethod.GET})
  public ModelAndView getSwaggerAtV2Swagger(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    return new ModelAndView(swaggerPage);
  }

  @RequestMapping(
      value = "/error",
      produces = {"text/html"},
      method = {RequestMethod.GET})
  public ModelAndView handleAnyError(HttpServletRequest httpRequest) {
    String path = httpRequest.getRequestURI();
    if (Helper.isStringNullOrEmpty(path)) path = "/";
    if (!path.contains("/v1") && !path.contains("/dev")) {
      return super.getModelViewErrorPage("404", path, "error-404");
    }
    return super.getModelViewErrorPage("500", path, "error-500");
  }
}
