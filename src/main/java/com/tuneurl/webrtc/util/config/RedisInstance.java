package com.tuneurl.webrtc.util.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuneurl.webrtc.util.controller.dto.AudioDataEntry;
import com.tuneurl.webrtc.util.controller.dto.EvaluateAudioStreamResponse;
import com.tuneurl.webrtc.util.controller.dto.FingerprintEntry;
import com.tuneurl.webrtc.util.controller.dto.TuneUrlTag;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.LinkedList;

public class RedisInstance {

  @Getter
  Jedis jedis;

  static RedisInstance redisConfig;

  public RedisInstance() {
    JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);
    jedis = jedisPool.getResource();
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

  public EvaluateAudioStreamResponse getOneSecondAudioStreamCache(String offset, String url, byte[] dataFingerprint) {
    String key = formatKey(offset, url, dataFingerprint);

    EvaluateAudioStreamResponse result = null;

    boolean exists = jedis.exists(key+"+count");
    if (exists) {
      String count = jedis.get(key+"+count");

      result = new EvaluateAudioStreamResponse();
      if (!count.equals("0")) {
        return null;
        /*
        result.setTuneUrlCounts(1L);
        result.setTagCounts(1L);

        try {
          String cached = jedis.get(key+"+liveTags");

          cached = cached.replaceAll("class TuneUrlTag ", "");
          cached = cached.replaceAll("\"", "\\\"");
          cached = cached.replaceAll("\n", "");
          System.out.println(cached);

          ArrayList tags = new ArrayList<>();
          ObjectMapper mapper = new ObjectMapper();

          tags.add(mapper.readValue(cached, Object.class));

          result.setLiveTags(tags);
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
         */
      } else {
        result = new EvaluateAudioStreamResponse();
        result.setTuneUrlCounts(0L);
        result.setTagCounts(0L);
        result.setLiveTags(new LinkedList<TuneUrlTag>());
      }
    }

    return result;
  }

  public void setOneSecondAudioStreamCache(String offset, String url, byte[] dataFingerprint, EvaluateAudioStreamResponse audioStreamResponse) {
    String key = formatKey(offset, url, dataFingerprint);

    String toCache;
    if (!audioStreamResponse.getLiveTags().isEmpty()) {
      toCache = audioStreamResponse.getLiveTags().get(0).toString();
      jedis.set(key+"+liveTags", toCache);
    }
    jedis.set(key+"+count", ""+audioStreamResponse.getLiveTags().size());

    // Expires in 24 hours = 24 * 60 * 60 = 86400
    jedis.expire(key+"+liveTags", 86400);
    jedis.expire(key+"+count", 86400);
  }

  private String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2 * hash.length);
      for (byte b : hash) {
          String hex = Integer.toHexString(0xff & b);
          if (hex.length() == 1) {
              hexString.append('0');
          }
          hexString.append(hex);
      }
    return hexString.toString();
  }


}
