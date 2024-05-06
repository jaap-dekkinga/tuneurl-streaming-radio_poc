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

package com.tuneurl.webrtc.util;

import static java.util.Collections.singletonList;
import static java.util.TimeZone.getTimeZone;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.firewall.HttpStatusRequestRejectedHandler;
import org.springframework.security.web.firewall.RequestRejectedHandler;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.InitBinderDataBinderFactory;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * The Tune URL SDK Spring Boot API.
 *
 * <ul>
 *   <li>v1.1, CVE-2022-22965 added mvcRegistrations(): WebMvcRegistrations
 *   <li>v1.1, CVE-2022-22965 added class ExtendedRequestMappingHandlerAdapter
 *   <li>v1.1, CVE-2022-22965 added createDataBinderFactory(List<InvocableHandlerMethod>):
 *       InitBinderDataBinderFactory
 * </ul>
 *
 * @author albonteddy@gmail.com
 * @version 1.12
 * @see https://spring.io/blog/2022/03/31/spring-framework-rce-early-announcement
 * @see https://tanzu.vmware.com/security/cve-2022-22965
 */
@SpringBootApplication(scanBasePackages = "com.tuneurl.webrtc.util")
public class WebrtcUtilApplication extends SpringBootServletInitializer {

  @Value("${language.default:en}")
  private String languageSetting;

  /** Pre-configuration. */
  static {
    System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
    System.setProperty(
        "com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
    System.setProperty("com.sun.xml.ws.util.pipe.StandaloneTubeAssembler.dump", "true");
    System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
    System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
  }

  /**
   * Setup where to look for .JSP files.
   *
   * @return ViewResolver
   */
  @Bean
  public ViewResolver getViewResolver() {
    InternalResourceViewResolver resolver = new InternalResourceViewResolver();
    resolver.setPrefix("/WEB-INF/views/");
    resolver.setSuffix(".jsp");
    return resolver;
  }

  /**
   * Setup Language template.
   *
   * @return RestTemplate
   */
  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setInterceptors(
        singletonList(
            (request, body, clientHttpRequestExecution) -> {
              String language = LocaleContextHolder.getLocale().getLanguage();
              HttpHeaders headers = request.getHeaders();
              headers.add("Accept-Language", language);
              return clientHttpRequestExecution.execute(request, body);
            }));
    return restTemplate;
  }

  /**
   * Setup Locale setting.
   *
   * @return LocaleResolver
   */
  @Bean
  public LocaleResolver localeResolver() {
    AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
    String en = "en";
    if (languageSetting.length() > 1) en = languageSetting;
    localeResolver.setDefaultLocale(new Locale(en));
    return localeResolver;
  }

  /**
   * Setup LocalDateTime default JSON output format.
   *
   * @return ObjectMapper
   */
  @Bean
  public ObjectMapper objectMapper() {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    dateFormat.setTimeZone(getTimeZone("UTC"));
    ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    objectMapper.setDateFormat(dateFormat);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return objectMapper;
  }

  /**
   * Tell Springboot the main class for this API.
   *
   * @param application
   * @return
   */
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(WebrtcUtilApplication.class);
  }

  /**
   * Setup which class to handle reject cases.
   *
   * @return RequestRejectedHandler
   */
  @Bean
  public RequestRejectedHandler requestRejectedHandler() {
    return new HttpStatusRequestRejectedHandler();
  }

  @Bean
  public WebMvcRegistrations mvcRegistrations() {
    return new WebMvcRegistrations() {
      @Override
      public RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
        return new ExtendedRequestMappingHandlerAdapter();
      }
    };
  }

  private static class ExtendedRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {

    @Override
    protected InitBinderDataBinderFactory createDataBinderFactory(
        List<InvocableHandlerMethod> methods) {

      return new ServletRequestDataBinderFactory(methods, getWebBindingInitializer()) {

        @Override
        protected ServletRequestDataBinder createBinderInstance(
            Object target, String name, NativeWebRequest request) throws Exception {

          ServletRequestDataBinder binder = super.createBinderInstance(target, name, request);
          String[] fields = binder.getDisallowedFields();
          List<String> fieldList =
              new ArrayList<>(fields != null ? Arrays.asList(fields) : Collections.emptyList());
          fieldList.addAll(Arrays.asList("class.*", "Class.*", "*.class.*", "*.Class.*"));
          binder.setDisallowedFields(fieldList.toArray(new String[] {}));
          return binder;
        }
      };
    }
  }

  /* private class MyCustomContentNegotiationStrategy implements ContentNegotiationStrategy {

    // SEE https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
    private final String[] FILE_EXTENTION_TO_MEDIA_TYPE = {
      ".html", "text/html",
      ".js", "application/javascript",
      ".wasm", "application/wasm",
      ".css", "text/css",
      ".json", "application/json",
      ".jsp", "text/html",
      ".txt", "text/plain"
    };

    private int getContentType(final String fileName) {
      for (int index = 0; index < FILE_EXTENTION_TO_MEDIA_TYPE.length; index += 2) {
        if (fileName.endsWith(FILE_EXTENTION_TO_MEDIA_TYPE[index])) {
          return index;
        }
      }
      return -1;
      // return "application/octet-stream";
    }

    @Override
    public List<MediaType> resolveMediaTypes(final NativeWebRequest request)
        throws HttpMediaTypeNotAcceptableException {
      final List<MediaType> mediaTypes = new ArrayList<>();
      final String url = ((ServletWebRequest) request).getRequest().getRequestURI().toString();
      String ext;
      String type;
      int index = getContentType(url);
      if (index >= 0) {
        ext = FILE_EXTENTION_TO_MEDIA_TYPE[index];
        ext = ext.substring(1);
        type = FILE_EXTENTION_TO_MEDIA_TYPE[index + 1];
        mediaTypes.add(new MediaType(type, ext));
      }
      return mediaTypes;
    }
  }

  static final MediaType MEDIA_WASM = new MediaType("application", "wasm");
  static final MediaType MEDIA_JAVASCRIPT = new MediaType("application", "javascript");

  @Bean
  public void configureContentNegotiation(
      org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer configurer) {
    configurer
        .favorParameter(true)
        .parameterName("mediaType")
        .ignoreAcceptHeader(true)
        .useRegisteredExtensionsOnly(false)
        .defaultContentType(MediaType.APPLICATION_JSON)
        .mediaType("xml", MediaType.APPLICATION_XML)
        .mediaType("json", MediaType.APPLICATION_JSON)
        .mediaType("html", MediaType.TEXT_HTML)
        .mediaType("jsp", MediaType.TEXT_HTML)
        .mediaType("wasm", MEDIA_WASM)
        .mediaType("js", MEDIA_JAVASCRIPT);
  } */

  /**
   * API start here.
   *
   * @param args Array of String
   */
  public static void main(String[] args) {
    SpringApplication.run(WebrtcUtilApplication.class, args);
  }
}
