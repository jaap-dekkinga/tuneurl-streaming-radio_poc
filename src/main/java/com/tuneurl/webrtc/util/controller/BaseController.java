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
import com.albon.auth.jwt.JwtResponse;
import com.albon.auth.jwt.JwtTool;
import com.albon.auth.util.Helper;
import com.albon.auth.util.LogMessage;
import com.albon.auth.value.Constant;
import com.tuneurl.webrtc.util.controller.dto.*;
import com.tuneurl.webrtc.util.exception.BaseServiceException;
import com.tuneurl.webrtc.util.model.*;
import com.tuneurl.webrtc.util.service.AudioStreamService;
import com.tuneurl.webrtc.util.service.LdapInfoService;
import com.tuneurl.webrtc.util.service.SdkUserService;
import com.tuneurl.webrtc.util.service.SessionDataService;
import com.tuneurl.webrtc.util.util.CommonUtil;
import com.tuneurl.webrtc.util.util.MessageLogger;
import com.tuneurl.webrtc.util.value.Constants;
import com.tuneurl.webrtc.util.value.UserType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.ModelAndView;

// import org.apache.logging.log4j.Logger;

/**
 * BaseController class.
 *
 * <p><strong>Thread Safety: </strong>This class is mutable and not thread safe but use in thread
 * safe manner.
 *
 * @author albonteddy@gmail.com
 * @version 1.0
 */
public abstract class BaseController {

  @Autowired protected AudioStreamService audioStreamBaseService;

  @Autowired protected SessionDataService sessionService;
  @Autowired protected LdapInfoService ldapService;
  @Autowired protected SdkUserService userService;

  @Value("${audio.access.without.login:false}")
  protected boolean isAllowAccessToWaveWithoutLogin;

  @Value("${gather.sdk.analytics:false}")
  private boolean isSaveAnalytic;

  @Value("${jwt.signing.key.secret:6bZrVzB4@5hYt370S12f15Tc}")
  private String loginJwtSecret;

  @Value("${jwt.signing.key.issuer:6cZwVu1b@4Gq91cOyHzB8OiT}")
  private String loginJwtIssuer;

  @Value("${jwt.signing.key.salt:35ZwVuB1@WpA867EsIxB8fTo}")
  private String loginJwtSalts;

  // JWT token will expires by default in 10 minutes.
  @Value("${jwt.token.expiration.in.seconds:600}")
  private Long tokenExpirationTime;

  @Value("${server.domain.url}")
  private String serverDomainUrl;

  /**
   * Authentication override. Only available during development.
   *
   * @return Boolean
   */
  protected boolean canAccessAudioWithoutLogin() {
    return isAllowAccessToWaveWithoutLogin;
  }

  /**
   * Helper method to convert SdkUser into UserEntry.
   *
   * @param sdk SdkUser
   * @return UserEntry
   */
  protected UserEntry convertSdkUser(SdkUser sdk) {
    UserEntry userEntry = new UserEntry();
    userEntry.setId(sdk.getId());
    userEntry.setFullName(sdk.getFullname());
    userEntry.setEmail(sdk.getEmail());
    userEntry.setRoles(sdk.getRoles());
    return userEntry;
  }

  /** URL for redirection. */
  public String getServerDomainUrl() {
    return serverDomainUrl;
  }

  /** Check if Issuer match with this server value. */
  protected boolean isValidIssuer(final String iss) {
    return iss != null && loginJwtIssuer.equals(iss);
  }

  /** Salt ptr. */
  private byte[] jwtSalt = null;
  private MessageLogger logger;

  /**
   * Get Salt.
   *
   * @return byte[]
   */
  protected byte[] getSalt() {
    if (jwtSalt == null) {
      jwtSalt = loginJwtSalts.getBytes();
    }
    return jwtSalt;
  }

  /**
   * Get MessageLogger.
   *
   * @return MessageLogger
   */
  protected MessageLogger getMessageLogger() {
    if (this.logger == null) {
      this.logger = new MessageLogger();
      this.logger.setLogger(
          LogManager.getLogger(com.tuneurl.webrtc.util.util.MessageLogger.class));
    }
    return this.logger;
  }

  protected ClientCredential clientCredential = null;

  /** Default constructor. */
  public BaseController() {
    // Does nothing.
  }

  /**
   * Get Caller's IP Address.
   *
   * @param httpRequest HttpServletRequest
   * @return String (or 127.0.0.1
   */
  protected String getCallersIPAddress(HttpServletRequest httpRequest) {
    String ipAddr = httpRequest.getRemoteAddr();
    if (Helper.isStringNullOrEmpty(ipAddr)) {
      ipAddr = "127.0.0.1";
    }
    return ipAddr;
  }

  /**
   * Method for gathering SDK Analytics.
   *
   * @param signature String
   * @param httpRequest HttpServletRequest
   */
  protected void saveAnalytics(final String signature, HttpServletRequest httpRequest) {
    if (!isSaveAnalytic) return;
    final String bk = "\"";
    String value = httpRequest.getRequestURL().toString();
    String name = httpRequest.getQueryString();
    if (!Helper.isStringNullOrEmpty(name)) {
      value = value + "?" + name;
    }
    StringBuffer sb = new StringBuffer();
    name = "url";
    String nValue = value.replace(":", "\\:");
    sb.append(bk + name + bk).append(":").append(bk + nValue + bk);

    java.util.Enumeration<java.lang.String> headers = httpRequest.getHeaderNames();
    while (headers.hasMoreElements()) {
      name = headers.nextElement();
      value = httpRequest.getHeader(name);
      nValue = value.replace(":", "\\:");
      value = nValue.replace("\"", "\\\"");
      sb.append(",").append(bk + name + bk).append(":").append(bk + value + bk);
    }
    value = sb.toString();
    getMessageLogger().logEntry(signature, new Object[] {"{" + value + "}"});
  }

  /**
   * https://docs.oracle.com/javase/8/docs/api/java/util/NoSuchElementException.html Find SdkUser
   * from a given JwtTool using User Email or User ID.
   *
   * @param jwt JwtTool
   * @return SdkUser
   * @throws BaseServiceException If Error at Database level
   */
  protected SdkUser findUser(final JwtTool jwt) throws BaseServiceException {
    CommonUtil.checkNullParameter(jwt, "JwtTool");
    UserEntry entry = jwt.getUser();
    CommonUtil.checkNullParameter(entry, "UserEntry");
    SdkUser user;
    try {
      user = userService.getSdkUserByName(entry.getEmail());
    } catch (BaseServiceException be) {
      user = userService.getSdkUserById(entry.getId());
    }
    return user;
  }

  /**
   * Find LdapInfo by ID.
   *
   * @param sdk SdkUser
   * @return LdapInfo
   * @throws BaseServiceException If Error at Database level
   */
  protected LdapInfo findLdapInfo(final SdkUser sdk) throws BaseServiceException {
    CommonUtil.checkNullParameter(sdk, "SdkUser");
    // If sdk exist, so as the LdapInfo.
    return ldapService.getLdapInfoById(sdk.getLdap());
  }

  /**
   * Find session associated with the ldap ID. Create one if does not exist.
   *
   * @param info LdapInfo
   * @param httpRequest HttpServletRequest
   * @return SessionData
   * @throws BaseServiceException If Error at Database level
   */
  protected SessionData findSdkSession(final LdapInfo info, final HttpServletRequest httpRequest)
      throws BaseServiceException {
    CommonUtil.checkNullParameter(info, "LdapInfo");
    Long ldapId = info.getId();
    try {
      return sessionService.getSessionDataById(ldapId);
    } catch (BaseServiceException be) {
      // drop bellow
    }
    LocalDateTime oras = CommonUtil.asLocalDateTime(new Date());
    SessionDataEntry entry = new SessionDataEntry();
    entry.setLdap(ldapId);
    String ipaddr = httpRequest.getRemoteAddr();
    if (null == ipaddr) {
      ipaddr = httpRequest.getLocalAddr().toString();
      if (ipaddr == null) ipaddr = "";
    }
    entry.setIpaddr(ipaddr);
    entry.setSessionStart(oras);
    entry.setSessionEnd(oras);
    return sessionService.createSessionData(entry);
  }

  /**
   * Helper method to extract value from HTTP header, and if not exist, optionally generate HTTP
   * 400.
   *
   * @param headerName String
   * @param httpRequest HttpServletRequest
   * @param errorMsgs String
   * @return String
   * @throws BaseServiceException if missing header parameter
   */
  protected String getHeaderValues(
      final String headerName, final HttpServletRequest httpRequest, final String errorMsgs)
      throws BaseServiceException {
    String value = httpRequest.getHeader(headerName);
    if (Helper.isStringNullOrEmpty(value) && !Helper.isStringNullOrEmpty(errorMsgs)) {
      CommonUtil.BadRequestException(errorMsgs);
      /** NOTREACH */
    }
    return value;
  }

  /**
   * Generate an Error page for a given resource path.
   *
   * @param errCode String Error code 400-500
   * @param resourcePath String path to resource
   * @param errorPage String JSP page
   */
  protected ModelAndView getModelViewErrorPage(
      final String errCode, final String resourcePath, final String errorPage) {
    Map<String, Object> maps = new HashMap<String, Object>();
    maps.put("status", errCode);
    maps.put("path", resourcePath);
    return new ModelAndView(errorPage, maps);
  }

  /**
   * Allow GET OPTIONS HEAD.
   *
   * @param httpResponse HttpServletResponse
   */
  protected void allowGetOptionsHead(HttpServletResponse httpResponse) {
    httpResponse.addHeader(Constants.ACCESS_CONTROL_ALLOW_METHODS, "GET, OPTIONS, HEAD");
  }

  /**
   * Clear JWT token on HTTP headers.
   *
   * @param location String where to redirect.
   * @return HttpHeaders
   */
  protected HttpHeaders clearHeaderTokens(final String location) {
    URI uri;
    String token = ""; // Clear previous JSON Web Token.
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    try {
      uri = new URI(location);
      headers.setLocation(uri);
    } catch (java.net.URISyntaxException ex) {
      CommonUtil.InternalServerException(ex.getMessage());
      /** NOTREACH */
    }
    headers.set(Constants.X_JWT_HEADER_NAME, token);
    return headers;
  }

  /**
   * Removed leading Token Prefix from token.
   *
   * @param token
   * @return String
   */
  protected String removeTokenPrefix(final String token) {
    int index;
    int size;
    if (token != null) {
      size = token.length();
      index = token.indexOf("Bearer ");
      if (index >= 0) {
        return token.substring(index + 7, size);
      }
      index = token.indexOf("Basic ");
      if (index >= 0) {
        return token.substring(index + 6, size);
      }
      return token;
    }
    return "";
  }

  /**
   * Helper method to access the token from HttpServletRequest.
   *
   * @param logger LogMessage
   * @param httpRequest HttpServletRequest
   * @param tokenName String
   */
  protected String getToken(
      LogMessage logger, HttpServletRequest httpRequest, final String tokenName) {
    java.util.Enumeration<String> tokenEnum = httpRequest.getHeaderNames();
    if (tokenEnum != null) {
      while (tokenEnum.hasMoreElements()) {
        String key = tokenEnum.nextElement();
        String token = httpRequest.getHeader(key);
        // logger.debug("getToken("+key+") -> ["+token+"]");
        if (tokenName.equalsIgnoreCase(key)) {
          return removeTokenPrefix(token);
        }
      }
    }
    return null;
  }

  /**
   * Setup JwtTool instance for login.
   *
   * @param useAuthBearer boolean
   * @return JwtTool instance
   */
  protected JwtTool setupJwtTool(boolean useAuthBearer) {
    return new JwtTool(
        loginJwtSecret, loginJwtIssuer, getSalt(), useAuthBearer, tokenExpirationTime);
  }

  /**
   * Setup JwtTool instance for login.
   *
   * @param expirationTime Long
   * @return JwtTool instance
   */
  protected JwtTool setupJwtTool(Long expirationTime) {
    return new JwtTool(
        loginJwtSecret,
        loginJwtIssuer,
        getSalt(),
        Constants.LOGIN_AUTHORIZATION_BEARER,
        expirationTime);
  }

  /**
   * Get Login information fro Authorization header values.
   *
   * @param useAuthBearer boolean
   * @param userType UserType
   * @param httpRequest HttpServletRequest
   * @param httpResponse HttpServletResponse
   * @param sessionExpirationMessage String
   * @return JwtTool instance
   * @throws BaseServiceException If Authorization payload does not match the access credential
   *     setup for UserType.
   */
  protected JwtTool getLoginInformation(
      boolean useAuthBearer,
      UserType userType,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse,
      final String sessionExpirationMessage)
      throws BaseServiceException {
    JwtTool jwt = setupJwtTool(useAuthBearer);
    MessageLogger logger = getMessageLogger();

    // Is Auth / x-jwt-token exist?
    String token = getToken(logger, httpRequest, Constant.HEADER_AUTHORIZATION_LOWERCASE);
    // logger.debug("Token 1: ["+ token + "]");
    if (Helper.isStringNullOrEmpty(token)) {
      token = getToken(logger, httpRequest, Constants.X_JWT_HEADER_NAME);
      // logger.debug("Token 2: ["+ token + "]");
      if (Helper.isStringNullOrEmpty(token)) {
        CommonUtil.ForbiddenException(Constants.NOT_ALLOWED_TO_EXECUTE_THE_REQUEST);
        /** NOTREACH */
      }
    }
    // Detect JWT expiration here
    jwt.setUser(null);

    // Extract JWT payload
    @SuppressWarnings("unused")
    JwtResponse jwtResponse = jwt.parseToken(logger, token);
    UserEntry user = jwt.getUser();
    String roles = "";
    @SuppressWarnings("unused")
    HttpHeaders headers;
    try {
      // If jwt.user is null, jwt.parseToken() fail.
      CommonUtil.checkNullParameter(user, "UserEntry");
      roles = user.getRoles();

      // The best log about the current user is here.
      logger.debug("UserEntry [%s]", user.toString());
    } catch (BaseServiceException ex) {
      // JWT expired
      headers = clearHeaderTokens(getServerDomainUrl());
      CommonUtil.ForbiddenException(sessionExpirationMessage);
      /** NOTREACH */
    }
    switch (userType) {
      case LOGIN_FOR_ADMIN:
        // Check if current user have ADMIN role.
        if (roles.indexOf(Constant.ROLES_ADMIN) < 0) {
          CommonUtil.ForbiddenException(Constant.REQUIRES_ADMIN_ROLE_TO_ACCESS_THE_ENDPOINT);
          /** NOTREACH */
        }
        break;

      default:
        // Check if current user have USER role.
        if (roles.indexOf(Constant.ROLES_ADMIN) < 0 && roles.indexOf(Constant.ROLES_USER) < 0) {
          CommonUtil.ForbiddenException(
              Constant.REQUIRES_USER_OR_ADMIN_ROLE_TO_ACCESS_THE_ENDPOINT);
          /** NOTREACH */
        }
        break;
    }
    // Check expiration
    if (jwt.isJWTExpired(user)) {
      // Clear JWT
      headers = clearHeaderTokens(getServerDomainUrl());
      CommonUtil.ForbiddenException(sessionExpirationMessage);
      /** NOTREACH */
    }

    return jwt;
  }

  /**
   * Get the SDK Client credentials.
   *
   * @param signature String
   * @param userType UserType
   * @param httpRequest HttpServletRequest
   * @param httpResponse HttpServletResponse
   * @throws BaseServiceException If parameter(s) does not exist or error at DB level
   */
  public void getSdkClientCredentials(
      final String signature,
      UserType userType,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse)
      throws BaseServiceException {

    this.clientCredential = new ClientCredential();

    MessageLogger logger = getMessageLogger();

    // 1.1 Gather callers information for SDK analytic usage.
    String clientId = httpRequest.getHeader(Constants.X_SDK_CLIENT_ID_HEADER_NAME);
    if (clientId == null) clientId = "";
    logger.logEntry(
        signature,
        new Object[] {
          "IP=",
          httpRequest.getRemoteAddr(),
          "p=",
          httpRequest.getRemotePort(),
          "q=",
          httpRequest.getRequestURI(),
          "h=",
          clientId
        });

    // 1.2 Caller must have a valid
    // x-sdk-client-id="Must have a valid SDK Client ID to use the service"
    LdapInfo clientInfo = null;
    clientCredential.setSdkUuid(clientId);
    try {
      CommonUtil.checkUUID(clientId, Constants.X_SDK_CLIENT_ID_HEADER_NAME);

      // 1.4 Will check the UUID after we get the User who is using the SDK ID.
      clientInfo = ldapService.getLdapInfoByName(clientId);
    } catch (Exception ignore) {
      clientInfo = null;
    }
    // 1.3 Is the SDK ID valid?
    clientCredential.setLdapInfo(clientInfo);

    // 2. Check for ADMIN or USER role.
    JwtTool jwtTool =
        this.getLoginInformation(
            Constants.LOGIN_AUTHORIZATION_BEARER,
            userType,
            httpRequest,
            httpResponse,
            Constants.SESSION_EXPIRED_LOGIN_AGAIN);
    clientCredential.setJwtTool(jwtTool);
    JwtResponse decodedToken = jwtTool.getJwtResponse();

    @SuppressWarnings("unused")
    Long currentUserId = decodedToken.getUserId();

    // 3.1 Access user records
    SdkUser user = this.findUser(jwtTool);
    clientCredential.setSdkUser(user);

    // 3.2 Find LdapInfo - it hold the counter to limit access of the service.
    LdapInfo info = this.findLdapInfo(user);
    clientCredential.setLdapInfo(info);
    String uuuid = info.getUuid();

    // Override for a while
    clientCredential.setSdkUuid(uuuid);
    clientCredential.setUserLdap(info);

    /* 3.3 Is this two the same - Must use correct SDK Client ID to use the service
    if (!uuuid.equals(uuid)) {
      CommonUtil.BadRequestException(Constants.X_SDK_CLIENT_ID_HEADER_NAME_MISMATCH_ERROR_STRING);
      / ** NOTREACH * /
    } */
    // 3.4 Load or create a SessionData for this user - it hold
    SessionData session = this.findSdkSession(info, httpRequest);
    clientCredential.setSession(session);

    // 3.5 get the UserEntry (login user from JWT)
    clientCredential.setUser(jwtTool.getUser());

    // 3.6 Status
    clientCredential.setStatus("OK");
  }

  /**
   * Helper to read file.
   *
   * @param file File
   * @param accessDeniedMessages String
   * @return Array of byte
   * @throws BaseServiceException Any error opening and/or reading the file.
   */
  protected byte[] readFileToStream(final File file, final String accessDeniedMessages)
      throws BaseServiceException {
    FileInputStream fis = null;
    byte[] data = null;
    int fileSize = (int) file.length();
    boolean onError = false;
    String errMsg = "";
    try {
      fis = new FileInputStream(file);
      data = new byte[fileSize];
      fis.read(data, 0, fileSize);
    } catch (FileNotFoundException e) {
      CommonUtil.NotFoundException("File not found");
      /*NOTREACH*/
    } catch (IOException e) {
      onError = true;
    } finally {
      try {
        if (fis != null) fis.close();
      } catch (IOException ignore) {
        // Ignore
      }
    }
    if (onError || data == null) {
      CommonUtil.ForbiddenException(accessDeniedMessages + errMsg);
      /*NOTREACH*/
    }
    return data;
  }

  /**
   * Nuke OutputStream.
   *
   * @param outputStream OutputStream
   * @return null
   */
  private OutputStream closeOutputStream(OutputStream outputStream) {
    try {
      if (outputStream != null) {
        outputStream.close();
      }
    } catch (IOException e) {
      // Ignore
    }
    return null;
  }

  /**
   * Helper to write bytes to HttpServletResponse via OutputStream.
   *
   * @param endPoint String
   * @param pOutputStream OutputStream
   * @param style String
   * @param response HttpServletResponse
   * @param fileName String
   * @param isClose Boolean
   * @param fileNameOnDisposition String
   * @return long the size
   * @throws BaseServiceException If any file operation fails
   */
  protected long writeResponseStreamResult(
      final String endPoint,
      OutputStream pOutputStream,
      final String style,
      HttpServletResponse response,
      final String fileName,
      final boolean isClose,
      final String fileNameOnDisposition)
      throws BaseServiceException {
    OutputStream outputStream = pOutputStream;
    final String accessDeniedMessages = "Access denied to " + endPoint;
    File file = null;
    try {
      file = new File(fileName);
    } catch (NullPointerException npe) {
      npe.printStackTrace();
      CommonUtil.ForbiddenException(accessDeniedMessages);
      /*NOTREACH*/
    }
    //    final long fileSize = file.length();
    //    if (fileSize < 1L || !file.canRead()) {
    //      CommonUtil.ForbiddenException(accessDeniedMessages);
    //      /*NOTREACH*/
    //    }
    byte[] b = readFileToStream(file, accessDeniedMessages);
    if (b == null) {
      CommonUtil.ForbiddenException(accessDeniedMessages);
      /*NOTREACH*/
    }
    long size = b.length < 1 ? file.length() : b.length;
    final String sSize = "" + size;
    final String contentLength = "Content-Length";
    //    MessageLogger logger = getMessageLogger();
    //    logger.logEntry(
    //        "writeResponseStreamResult",
    //        new Object[] {
    //          "f=", fileName,
    //          "n=", size,
    //          "t=", style,
    //          "d=", fileNameOnDisposition
    //        });
    try {
      response.setStatus(200);
      response.setContentLength((int) size);
      response.addHeader(contentLength, sSize);
      response.setHeader(contentLength, sSize);
      if (style != null) {
        response.setContentType(style);
      }
      if (fileNameOnDisposition != null) {
        response.setHeader("Content-Disposition", "attachment; filename=" + fileNameOnDisposition);
      }
      outputStream.write(b);
      outputStream.flush();
    } catch (IOException ex) {
      ex.printStackTrace();
      outputStream = closeOutputStream(outputStream);
      CommonUtil.ForbiddenException(accessDeniedMessages);
      /*NOTREACH*/
    } finally {
      closeOutputStream(outputStream);
    }

    return size;
  }
}
