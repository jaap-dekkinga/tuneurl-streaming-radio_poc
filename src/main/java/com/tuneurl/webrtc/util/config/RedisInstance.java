package com.tuneurl.webrtc.util.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuneurl.webrtc.util.controller.dto.EvaluateAudioStreamResponse;
import com.tuneurl.webrtc.util.controller.dto.FingerprintResponse;
import com.tuneurl.webrtc.util.controller.dto.FingerprintResponseNew;
import com.tuneurl.webrtc.util.controller.dto.TuneUrlTag;
import java.util.ArrayList;
import java.util.LinkedList;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Getter
public class RedisInstance {

  Jedis jedis;

  static RedisInstance redisConfig;

  public RedisInstance() {
    try {
      JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);
      jedis = jedisPool.getResource();
    } catch (Exception e) {
      jedis = null;
    }
  }

  public static RedisInstance getInstance() {
    if (redisConfig == null) {
      redisConfig = new RedisInstance();
    }
    return redisConfig;
  }

  private String formatKey(String offset, String url, byte[] dataFingerprint) {
    String sha256hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex(dataFingerprint);
    return url + "--" + offset + "--" + sha256hex;
  }

  public EvaluateAudioStreamResponse getOneSecondAudioStreamCache(
      String offset, String url, String dataFingerprintString) {
    if (jedis == null) {
      return null;
    }

    byte[] dataFingerprint = dataFingerprintString.getBytes();

    String key = formatKey(offset, url, dataFingerprint);

    EvaluateAudioStreamResponse result = null;

    boolean exists = jedis.exists(key + "+count");
    if (exists) {
      String count = jedis.get(key + "+count");

      result = new EvaluateAudioStreamResponse();
      if (!count.equals("0")) {
        result.setTuneUrlCounts(1L);
        result.setTagCounts(1L);

        try {
          String cached = jedis.get(key + "+liveTags");

          cached = cached.replaceAll("\"", "\\\"");
          System.out.println(cached);

          ArrayList<TuneUrlTag> tags = new ArrayList<>();
          ObjectMapper mapper = new ObjectMapper();

          TuneUrlTag tag = mapper.readValue(cached, TuneUrlTag.class);
          tag.setDescription(jedis.get(key + "+liveTagsDescription"));

          tags.add(tag);
          result.setLiveTags(tags);
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      } else {
        result = new EvaluateAudioStreamResponse();
        result.setTuneUrlCounts(0L);
        result.setTagCounts(0L);
        result.setLiveTags(new LinkedList<TuneUrlTag>());
      }
    }

    return result;
  }

  public void setOneSecondAudioStreamCache(
      String offset,
      String url,
      String dataFingerprintString,
      EvaluateAudioStreamResponse audioStreamResponse) {
    if (jedis == null) {
      return;
    }

    byte[] dataFingerprint = dataFingerprintString.getBytes();

    String key = formatKey(offset, url, dataFingerprint);

    jedis.set(key + "+count", "" + audioStreamResponse.getLiveTags().size());
    if (!audioStreamResponse.getLiveTags().isEmpty()) {
      String toCache = audioStreamResponse.getLiveTags().get(0).toJsonWithoutDescription();
      jedis.set(key + "+liveTags", toCache);

      String description = audioStreamResponse.getLiveTags().get(0).getDescription();
      jedis.set(key + "+liveTagsDescription", description);
    }

    // Expires in 10 days = 10 * 24 * 60 * 60 = 864000
    jedis.expire(key + "+liveTags", 86400000);
    jedis.expire(key + "+liveTagsDescription", 8640000);
    jedis.expire(key + "+count", 8640000);
  }

  public FingerprintResponse getFingerprintCache(String url) {
    if (jedis == null) {
      return null;
    }

    FingerprintResponse result = null;
    String cached = jedis.get("fingerprint--" + url);
    if (cached != null) {
      try {
        cached = cached.replaceAll("\"", "\\\"");
        System.out.println(cached);

        ObjectMapper mapper = new ObjectMapper();
        result = mapper.readValue(cached, FingerprintResponse.class);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }

    return result;
  }

  public FingerprintResponseNew getFingerprintCacheNew(String url) {
    if (jedis == null) {
      return null;
    }

    FingerprintResponseNew result = null;
    String cached = jedis.get("fingerprintNew--" + url);
    if (cached != null) {
      try {
        cached = cached.replaceAll("\"", "\\\"");
        System.out.println(cached);

        ObjectMapper mapper = new ObjectMapper();
        result = mapper.readValue(cached, FingerprintResponseNew.class);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }

    return result;
  }

  public void setFingerprintCache(String url, FingerprintResponse fingerprintResponse) {
    if (jedis == null) {
      return;
    }
    jedis.set("fingerprint--" + url, fingerprintResponse.toJson());
  }

  public void setFingerprintCacheNew(String url, FingerprintResponseNew fingerprintResponse) {
    if (jedis == null) {
      return;
    }
    jedis.set("fingerprintNew--" + url, fingerprintResponse.toJson());
  }
}
