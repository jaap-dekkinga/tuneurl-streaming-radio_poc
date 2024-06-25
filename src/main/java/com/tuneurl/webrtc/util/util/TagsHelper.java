package com.tuneurl.webrtc.util.util;

import com.tuneurl.webrtc.util.controller.dto.FingerprintCompareResponse;
import com.tuneurl.webrtc.util.controller.dto.FingerprintResponse;
import com.tuneurl.webrtc.util.controller.dto.TuneUrlTag;
import com.tuneurl.webrtc.util.util.fingerprint.FingerprintUtility;
import com.tuneurl.webrtc.util.value.Constants;
import java.util.ArrayList;
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
      FingerprintResponse fr,
      FingerprintCompareResponse fcr) {
    TuneUrlTag tag = new TuneUrlTag();
    long offset = fcr.getOffset();
    offset += dataOffset;
    if (updateOffset) fcr.setOffset(offset);
    tag.setDataPosition(offset);
    offset = Converter.muldiv(1L, offset, 100L);
    tag.setIndex(offset);
    tag.setFingerprintCompareResponseData(fcr, false);

    final String payload = FingerprintUtility.convertFingerprintToString(fr.getData());
    tag.setTuneUrlEmptyEntryData(payload);
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
   * Helper method to create Array of tags.
   *
   * @param dataOffset Long data offset
   * @param frSelection List&lt;FingerprintResponse>
   * @param fcrSelection List&lt;FingerprintCompareResponse>
   * @return List&lt;TuneUrlTag>
   */
  public final List<TuneUrlTag> createTags(
      Long dataOffset,
      List<FingerprintResponse> frSelection,
      List<FingerprintCompareResponse> fcrSelection) {
    ArrayList<TuneUrlTag> tags = new ArrayList<>();
    TuneUrlTag tag;
    FingerprintResponse fx;
    FingerprintResponse fr;
    FingerprintCompareResponse fcx;
    FingerprintCompareResponse fcr;
    int index, limit = fcrSelection.size();
    if (limit > 2) {
      for (index = 0; index < limit; index++) {
        fr = frSelection.get(index);
        fcr = fcrSelection.get(index);
        tag = newTag(false, dataOffset, fr, fcr);
        tags.add(tag);
      }
      // Try to locate the X-Y-X or X-Y-Z-Y-X pattern
      List<TuneUrlTag> newTag = pruneTags(tags);
      if (!newTag.isEmpty()) return newTag;
      tags.clear();
    }
    if (limit == 1) {
      tag = newTag(true, dataOffset, frSelection.get(0), fcrSelection.get(0));
      tags.add(tag);
    } else if (limit > 1) {
      fx = frSelection.get(0);
      fcx = fcrSelection.get(0);
      // for (index = 1; index < limit; index++) {
      //   fr = frSelection.get(index);
      //   fcr = fcrSelection.get(index);
      //   if (CommonUtil.compareDouble(fcr.getScore(), fcx.getScore()) > 0) {
      //     fcx = fcr;
      //     fx = fr;
      //   }
      // }
      tag = newTag(true, dataOffset, fx, fcx);
      tags.add(tag);
    }
    return tags;
  }

  /**
   * Helper to prune List of TuneUrlTag
   *
   * @param tags List&lt;TuneUrlTag>
   * @return List&lt;TuneUrlTag>
   */
  public final List<TuneUrlTag> pruneTags(List<TuneUrlTag> tags) {
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
