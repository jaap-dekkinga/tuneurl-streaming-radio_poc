package com.tuneurl.webrtc.util.util.fingerprint;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.apache.commons.io.IOUtils;

public class FingerprintFileLoader {
  private final int AMPLITUDE_SIZE = 512;
  private final int MAXIMUM_DURATION = 1020;
  private final int SAMPLE_RATE = 44100;
  private final int FINGERPRINT_SAMPLE_RATE = 10240;

  public short[] loadFileByUrl(String pUrl) throws Exception {
    URL url = new URL(pUrl);
    System.out.println(pUrl + " " + url);
    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);

    byte[] bytes = IOUtils.toByteArray(audioInputStream);
    System.out.println("BYTES: " + bytes.length);

    ByteBuffer bufferedAudio = ByteBuffer.wrap(bytes);

    return getAudioBufferChannelZeroData(bufferedAudio);
  }

  private short[] getAudioBufferChannelZeroData(ByteBuffer audioBuffer) {
    System.out.println("MARK 1");
    if (audioBuffer == null) {
      return null;
    }
    System.out.println("MARK 2");
    int capacity = audioBuffer.capacity() / 4;
    short[] data = new short[capacity];
    for (int i = 0; i < data.length; i++) {
      data[i] = audioBuffer.getShort();
    }

    int duration = MAXIMUM_DURATION;
    /*
    int duration = (int) (0.5f + audioBuffer.getFloat(capacity - 4));
    if (duration > MAXIMUM_DURATION) {
        duration = MAXIMUM_DURATION;
    }
    */
    System.out.println("MARK 3");
    int sampleRate = SAMPLE_RATE;
    int max_size = sampleRate * duration;
    int size = Math.min(data.length, max_size);

    short minFloat = 0;
    short maxFloat = 0;
    short value;
    for (int index = 0; index < size; index++) {
      value = data[index];
      if (value < minFloat) {
        minFloat = value;
      }
      if (value > maxFloat) {
        maxFloat = value;
      }
    }
    System.out.println("MARK 3");
    double divider = 0.01f + maxFloat - minFloat;
    short iMin = 0;
    short iMax = 0;
    // short iSum = 0;
    int skip = sampleRate / FINGERPRINT_SAMPLE_RATE;
    if (skip < 1) {
      skip = 1;
    }
    int limit = size / skip;
    short[] results = new short[limit];
    System.out.println("MARK 4");
    for (int index = 0, offset = 0; offset < limit && index < size; index += skip, offset++) {
      value = data[index];
      value = (short) (value / divider);
      value = (short) (value * AMPLITUDE_SIZE);
      if (value < iMin) {
        iMin = value;
      }
      if (value > iMax) {
        iMax = value;
      }
      results[offset] = value;
      // iSum += Integer.parseInt(String.valueOf(value));
    }
    for (short item : results) {
      System.out.print(item + " ");
    }
    System.out.println("MARK 5");
    Arrays.fill(results, limit, results.length, (short) 0);

    return results;
  }
}
