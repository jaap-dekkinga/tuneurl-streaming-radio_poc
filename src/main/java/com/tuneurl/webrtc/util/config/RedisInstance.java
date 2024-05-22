package com.tuneurl.webrtc.util.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuneurl.webrtc.util.controller.dto.EvaluateAudioStreamResponse;
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
    try (JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379)) {
      jedis = jedisPool.getResource();
    }
  }

  public static RedisInstance getInstance() {
    if (redisConfig == null) {
      redisConfig = new RedisInstance();
    }
    return redisConfig;
  }

  private String formatKey(String offset, String url, Long dataSize, byte[] dataFingerprint) {
    String sha256hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex(dataFingerprint);
    return url + "--" + dataSize + "--" + offset + "--" + sha256hex;
  }

  public EvaluateAudioStreamResponse getOneSecondAudioStreamCache(
      String offset, String url, Long dataSize, byte[] dataFingerprint) {
    if (jedis == null) {
      return null;
    }

    String key = formatKey(offset, url, dataSize, dataFingerprint);

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
      Long dataSize,
      byte[] dataFingerprint,
      EvaluateAudioStreamResponse audioStreamResponse) {
    if (jedis == null) {
      return;
    }

    String key = formatKey(offset, url, dataSize, dataFingerprint);

    jedis.set(key + "+count", "" + audioStreamResponse.getLiveTags().size());
    if (!audioStreamResponse.getLiveTags().isEmpty()) {
      String toCache = audioStreamResponse.getLiveTags().get(0).toJsonWithoutDescription();
      jedis.set(key + "+liveTags", toCache);

      String description = audioStreamResponse.getLiveTags().get(0).getDescription();
      jedis.set(key + "+liveTagsDescription", description);
    }

    // Expires in 24 hours = 24 * 60 * 60 = 86400
    jedis.expire(key + "+liveTags", 86400);
    jedis.expire(key + "+liveTagsDescription", 86400);
    jedis.expire(key + "+count", 86400);
  }
}
