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
import com.albon.auth.exception.AuthSdkException;
import com.albon.auth.jwt.JwtResponse;
import com.albon.auth.jwt.JwtTool;
import com.auth0.jwt.interfaces.Claim;
import com.tuneurl.webrtc.util.controller.dto.*;
import com.tuneurl.webrtc.util.controller.dto.Error;
import com.tuneurl.webrtc.util.exception.BaseServiceException;
import com.tuneurl.webrtc.util.model.*;
import com.tuneurl.webrtc.util.util.CommonUtil;
import com.tuneurl.webrtc.util.util.MessageLogger;
import com.tuneurl.webrtc.util.util.ProcessHelper;
import com.tuneurl.webrtc.util.value.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * The UserController class.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
@RestController
@RequestMapping("/")
public class UserController extends BaseController {

  /** Default constructor. */
  public UserController() {
    super();
  }

  /**
   * moved to BaseController. Helper method to convert SdkUser into UserEntry.
   *
   * @param sdk SdkUser
   * @return UserEntry / private UserEntry convertSdkUser(SdkUser sdk) { UserEntry userEntry = new
   *     UserEntry(); userEntry.setId(sdk.getId()); userEntry.setFullName(sdk.getFullname());
   *     userEntry.setEmail(sdk.getEmail()); userEntry.setRoles(sdk.getRoles()); return userEntry; }
   */

  /**
   * Find UserEntry that matched the given user name and password.
   *
   * @param username String
   * @param password String
   * @param ipAddr String
   * @return UserEntry
   */
  private UserEntry findUserEntry(final String username, final String password, final String ipAddr)
      throws BaseServiceException {
    SdkUser sdk = userService.getSdkUserByName(username);
    LdapInfo ldap = ldapService.getLdapInfoById(sdk.getId());
    /* MessageLogger logger = super.getMessageLogger();
    final String signature = "findUserEntry";
    logger.logEntry(
        signature,
        new Object[] {"usr=", sdk.getEmail(), "pwd=", password, "ldap=", ldap.getPassword()}); */
    if (!password.equals(ldap.getPassword())) {
      CommonUtil.BadRequestException("Invalid Email / password");
    }
    UserEntry user = super.convertSdkUser(sdk);
    return user;
  }

  /** Display Forbidden */
  @RequestMapping(
      value = "/v1/user/login",
      produces = {"text/html"},
      method = {RequestMethod.GET})
  public ModelAndView getV1UserLoginr(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    return super.getModelViewErrorPage("403", "/v1/user/login", "error-403");
  }

  /**
   * Log In into the Java TuneUrl API server. <br>
   * <br>
   * <b>Implementation Notes</b>: <br>
   * <b>A. Input is <code>LoginEntry</code>.</b>
   *
   * <ul>
   *   <li><code>LoginEntry.username</code>: user email address in plain text.
   *   <li><code>LoginEntry.password</code>: user password in plain text.
   * </ul>
   *
   * <br>
   * <b>B. How the Log in works.</b>
   *
   * <ul>
   *   <li><b>Note:</b>No user table for this.
   *   <li>Login as <code>admin@example.com</code> and password is <code>admin-1234</code> to have
   *       an <code>ADMIN role</code>.
   *   <li>Login as <code>user@example.com</code> and password is <code>user-1234</code> to have an
   *       <code>USER role</code>.
   *   <li>If given username or password is not valid, response with HTTP 400 - Invalid User name
   *       and/or password.
   * </ul>
   *
   * <br>
   * <b>C. Output is LoginResponse.</b>
   *
   * <ul>
   *   <li><code>LoginResponse.userId</code>: User ID
   *   <li>header <code>x-jwt-token</code> will hold the simulated token to be use in Authorization
   *       header
   * </ul>
   *
   * @param loginEntry LoginEntry,
   * @param httpRequest HttpServletRequest HTTP Request
   * @param httpResponse HttpServletResponse HTTP Response
   * @return ResponseEntity &lt;LoginResponse>
   */
  @PostMapping(
      path = "/v1/user/login",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  @ApiOperation(value = "Log In into the Java TuneUrl API server", response = LoginResponse.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "LoginResponse"),
        @ApiResponse(code = 400, message = "BadRequest"),
        @ApiResponse(code = 500, message = "InternalServerError"),
      })
  @CrossOrigin("*")
  public ResponseEntity<LoginResponse> userLogin(
      @Valid @RequestBody LoginEntry loginEntry,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {

    final String signature = "userLogin";
    super.saveAnalytics(signature, httpRequest);
    String ipAddress = super.getCallersIPAddress(httpRequest);

    logger.logEntry(
        signature,
        new Object[] {"usr=", loginEntry.getUsername(), "pwd=", "***", "ip=", ipAddress});
    JwtTool jwtTool = super.setupJwtTool(Constants.LOGIN_AUTHORIZATION_BEARER);
    JwtResponse decodedToken = jwtTool.getJwtResponse();

    @SuppressWarnings("unused")
    Long currentUserId = decodedToken.getUserId();

    String username =
        CommonUtil.getValidString("User Name", loginEntry.getUsername(), Constants.USER_NAME_SIZE);
    String password =
        CommonUtil.getValidString(
            "User Password", loginEntry.getPassword(), Constants.HASHED_PASSWORD_SIZE);
    password =
        CommonUtil.getString(
            ProcessHelper.encryptPassword(password, logger), Constants.HASHED_PASSWORD_SIZE);

    UserEntry userEntry = findUserEntry(username, password, ipAddress);
    String token = jwtTool.generateToken(logger, userEntry);
    LoginResponse response = new LoginResponse();
    response.setUserId(userEntry.getId());
    response.setToken(token);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.set(Constants.X_JWT_HEADER_NAME, token);

    return new ResponseEntity<LoginResponse>(response, headers, HttpStatus.OK);
  }

  /**
   * Log out from the Java TuneUrl API server. <br>
   * Log out from the Java TuneUrl API server
   *
   * @param httpRequest HttpServletRequest HTTP Request
   * @param httpResponse HttpServletResponse HTTP Response
   * @return ResponseEntity &lt;Error>
   */
  @GetMapping(
      path = "/v1/user/logout",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  @ApiOperation(value = "Log out from the Java TuneUrl API server", response = Error.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 301, message = "Location"),
      })
  @CrossOrigin("*")
  public ResponseEntity<Error> userLogout(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

    String location = super.getServerDomainUrl();
    String message = "Redirect to \"" + location + "\"";
    HttpStatus status = HttpStatus.MOVED_PERMANENTLY;
    Error response = new Error(status, message);
    HttpHeaders headers = super.clearHeaderTokens(location);

    return new ResponseEntity<Error>(response, headers, status);
  }

  /**
   * Refresh token. <br>
   * Refresh token
   *
   * @param oauthTokenEntry OauthTokenEntry,
   * @param httpRequest HttpServletRequest HTTP Request
   * @param httpResponse HttpServletResponse HTTP Response
   * @return ResponseEntity &lt;LoginResponse>
   */
  @PostMapping(
      path = "/v1/oauth/token",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  @ApiOperation(value = "Refresh token", response = LoginResponse.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "LoginResponse"),
        @ApiResponse(code = 400, message = "BadRequest"),
        @ApiResponse(code = 500, message = "InternalServerError"),
      })
  @CrossOrigin("*")
  public ResponseEntity<LoginResponse> refreshToken(
      @Valid @RequestBody OauthTokenEntry oauthTokenEntry,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {
    final String signature = "refreshToken";
    super.saveAnalytics(signature, httpRequest);
    // logger.logEntry(signature, new Object[] {"token=", oauthTokenEntry.toString()});

    // always refresh_token
    CommonUtil.checkNullOrEmptyParameter(oauthTokenEntry.getGrantType(), "Grant Type");
    if (!Constants.GRANT_TYPE_REFRESH_TOKEN_NAME.equals(oauthTokenEntry.getGrantType())) {
      CommonUtil.BadRequestException(Constants.INVALID_GRANT_TYPE_REFRESH_TOKEN_ERROR);
    }
    CommonUtil.checkNullOrEmptyParameter(oauthTokenEntry.getRefreshToken(), "Previous JWT");

    CommonUtil.checkNullParameter(oauthTokenEntry.getTokenExpiration(), "Token expiration");
    CommonUtil.checkLongRangeValue(
        oauthTokenEntry.getTokenExpiration(), "Token expiration", 60L, 86400L);

    JwtTool jwtTool = super.setupJwtTool(oauthTokenEntry.getTokenExpiration());

    String token = oauthTokenEntry.getRefreshToken();

    // Detect JWT expiration here
    jwtTool.setUser(null);
    try {
      jwtTool.parse(token, "");
    } catch (AuthSdkException ex) {
      throw new BaseServiceException("Token ERROR:" + ex.getMessage(), HttpStatus.FORBIDDEN);
    }
    // logger.debug("Payload:[%s]", jwtTool.getPayload());
    // logger.debug("Header:[%s]", jwtTool.getHeader());
    Map<String, Claim> claims = jwtTool.getJwt().getClaims();
    // Make sure that this server did issue the token - beat crafted JWT tokens.
    Claim ISS = claims.get(com.albon.auth.value.Constant.TOKEN_STRING_ISSUER);
    String iss = null;
    if (ISS != null) iss = ISS.asString();
    if (!super.isValidIssuer(iss)) {
      CommonUtil.ForbiddenException(Constants.FORBIDDEN_INVALID_TOKEN_ERROR);
    }
    // The Email should be here.
    Claim Email = claims.get(com.albon.auth.value.Constant.TOKEN_STRING_EMAIL);
    // String status;
    UserEntry userEntry = null;
    if (Email != null) {
      try {
        userEntry = convertSdkUser(userService.getSdkUserByName(Email.asString()));
        // logger.debug("userEntry(Email): [%s]", userEntry);
      } catch (BaseServiceException ex) {
        // The only explanation was we Nuke the database - missing User entries.
        // status = ex.getMessage();
        // logger.debug(status);
        CommonUtil.ForbiddenException(Constants.FORBIDDEN_INVALID_TOKEN_ERROR);
      }
    } else {
      CommonUtil.ForbiddenException(Constants.FORBIDDEN_INVALID_TOKEN_ERROR);
    }

    token = jwtTool.generateToken(logger, userEntry);
    LoginResponse response = new LoginResponse();
    response.setUserId(userEntry.getId());
    response.setToken(token);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.set(Constants.X_JWT_HEADER_NAME, token);

    return new ResponseEntity<LoginResponse>(response, headers, HttpStatus.OK);
  }
}
