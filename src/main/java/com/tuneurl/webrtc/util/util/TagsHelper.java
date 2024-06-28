package com.tuneurl.webrtc.util.util;

import com.tuneurl.webrtc.util.controller.dto.FingerprintCompareResponse;
import com.tuneurl.webrtc.util.controller.dto.FingerprintResponseNew;
import com.tuneurl.webrtc.util.controller.dto.TuneUrlTag;
import java.util.List;

public class TagsHelper {

  /**
   * Helper method to create TuneUrlTag from FingerprintResponse and FingerprintCompareResponse.
   *
   * @param updateOffset boolean
   * @param dataOffset Long
   * @param fr FingerprintResponse
   * @param fcr FingerprintCompareResponse
   * @return TuneUrlTag
   */
  public final TuneUrlTag newTag(
      boolean updateOffset,
      Long dataOffset,
      FingerprintResponseNew fr,
      FingerprintCompareResponse fcr) {
    TuneUrlTag tag = new TuneUrlTag();
    long offset = fcr.getOffset();
    offset += dataOffset;
    if (updateOffset) fcr.setOffset(offset);
    tag.setDataPosition(offset);
    offset = Converter.muldiv(1L, offset, 100L);
    tag.setIndex(offset);
    tag.setFingerprintCompareResponseData(fcr, false);

    if (null != fr) {
      final String payload = fr.toJson();
      tag.setTuneUrlEmptyEntryData(payload);
    }

    return tag;
  }

  /**
   * Display live tags for debugging.
   *
   * @param signature String
   * @param logger MessageLogger
   * @param tag TuneUrlTag
   */
  public final void displayLiveTagsEx(
      final String signature, MessageLogger logger, TuneUrlTag tag) {
    logger.logExit(
        signature,
        new Object[] {
          "pos=", tag.getDataPosition(),
          "similarity=", tag.getSimilarity(),
          "index=", tag.getIndex()
        });
  }

  /**
   * Display live tags for debugging.
   *
   * @param signature String
   * @param logger MessageLogger
   * @param liveTags List&lt;TuneUrlTag>
   */
  public final void displayLiveTags(
      final String signature, MessageLogger logger, List<TuneUrlTag> liveTags) {
    for (TuneUrlTag liveTag : liveTags) {
      displayLiveTagsEx(signature, logger, liveTag);
    }
  }

  /**
   * Helper to prune List of TuneUrlTag
   *
   * @param tags List&lt;TuneUrlTag>
   * @return List&lt;TuneUrlTag>
   */
  public final List<TuneUrlTag> pruneTags(List<TuneUrlTag> tags) {
    int size = tags.size();

    while (tags.size() > 1) {
      Long dataPostion1 = tags.get(0).getDataPosition();
      Long dataPostion2 = tags.get(1).getDataPosition();
      if (Math.abs(dataPostion1 - dataPostion2) < 1000) tags.remove(0);
    }
    return tags;
  }

  /**
   * Helper to prune List of TuneUrlTag for /dev/v3/evaluateOneSecondAudioStream .
   *
   * @param isDebugOn boolean
   * @param logger MessageLogger
   * @param tags List&lt;TuneUrlTag>
   * @return List&lt;TuneUrlTag>
   */
  public final List<TuneUrlTag> pruneTagsEx(
      boolean isDebugOn, MessageLogger logger, List<TuneUrlTag> tags) {
    return tags;
  }
}
