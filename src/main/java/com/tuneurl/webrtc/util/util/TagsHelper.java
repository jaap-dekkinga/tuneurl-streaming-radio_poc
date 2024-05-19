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
          "score=", tag.getScore(),
          "similarity=", tag.getSimilarity(),
          "Frame=", tag.getMostSimilarFramePosition(),
          "StartTime=", tag.getMostSimilarStartTime(),
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
      for (index = 1; index < limit; index++) {
        fr = frSelection.get(index);
        fcr = fcrSelection.get(index);
        if (CommonUtil.compareDouble(fcr.getScore(), fcx.getScore()) > 0) {
          fcx = fcr;
          fx = fr;
        }
      }
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
    ArrayList<TuneUrlTag> newTags = new ArrayList<>();
    int index, limit = tags.size() - 4;
    long distance;
    TuneUrlTag a;
    TuneUrlTag b;
    TuneUrlTag c;
    TuneUrlTag d;

    for (index = 0; index <= limit; index++) {
      a = tags.get(index);
      b = tags.get(index + 1);
      c = tags.get(index + 2);
      d = tags.get(index + 3);
      distance = b.getDataPosition() - a.getDataPosition();
      if (distance > 800L) continue;
      distance = c.getDataPosition() - b.getDataPosition();
      if (distance > 800L) continue;
      distance = d.getDataPosition() - c.getDataPosition();
      if (distance > 800L) continue;
      // 509 509 -214748364 -50
      if (c.getMostSimilarFramePosition() == Constants.FRAME_LOWEST_VALUE) {
        if (FingerprintUtility.hasPositiveSimilarFrameStartTime(a)
            && FingerprintUtility.hasSimilarFrameStartTime(a, b)
            && FingerprintUtility.hasNegativeSimilarFrameStartTimeEx(d)) {
          newTags.add(c);
          index += 3;
          continue;
        }
      }
      // 509 -214748364 -50 -50
      if (b.getMostSimilarFramePosition() == Constants.FRAME_LOWEST_VALUE) {
        if (FingerprintUtility.hasPositiveSimilarFrameStartTime(a)
            && FingerprintUtility.hasNegativeSimilarFrameStartTimeEx(c)
            && FingerprintUtility.hasSimilarFrameStartTime(c, d)) {
          newTags.add(d);
          index += 3;
          continue;
        }
      }
      // -214748364 -50 -50 -50
      if (a.getMostSimilarFramePosition() == Constants.FRAME_LOWEST_VALUE) {
        if (FingerprintUtility.hasNegativeSimilarFrameStartTimeEx(b)
            && FingerprintUtility.hasSimilarFrameStartTime(b, c)
            && FingerprintUtility.hasSimilarFrameStartTime(c, d)) {

          distance = d.getDataPosition() - a.getDataPosition();
          if (distance < 1100L) continue;
          if (distance > 1100L) {
            newTags.add(b);
          } else {
            newTags.add(d);
          }
          index += 3;
        }
      }
    }
    return newTags;
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

    ArrayList<TuneUrlTag> newTags = new ArrayList<>();
    int index, limit = tags.size() - 4;
    long d1, d2, d3, distance;
    TuneUrlTag a;
    TuneUrlTag b;
    TuneUrlTag c;
    TuneUrlTag d;

    for (index = 0; index <= limit; index++) {
      a = tags.get(index);
      b = tags.get(index + 1);
      c = tags.get(index + 2);
      d = tags.get(index + 3);
      d1 = b.getDataPosition() - a.getDataPosition();
      if (d1 > 800L) continue;
      d2 = c.getDataPosition() - b.getDataPosition();
      if (d2 > 800L) continue;
      d3 = d.getDataPosition() - c.getDataPosition();
      if (d3 > 800L) continue;
      distance = d.getDataPosition() - a.getDataPosition();
      if (isDebugOn) {
        logger.debug(
            "{\"Leaving\":{pruneTagsEx(%d %d %d %d %d, diff=%d %d %d %d)}}",
            a.getDataPosition(),
            a.getMostSimilarFramePosition(),
            b.getMostSimilarFramePosition(),
            c.getMostSimilarFramePosition(),
            d.getMostSimilarFramePosition(),
            d1,
            d2,
            d3,
            distance);
      }
      // 509 509 -214748364 -50
      // 509 509          N -50 SELECT C 2880
      if (c.getMostSimilarFramePosition() == Constants.FRAME_LOWEST_VALUE) {
        if (FingerprintUtility.hasPositiveSimilarFrameStartTime(a)
            && FingerprintUtility.hasSimilarFrameStartTime(a, b)
            && FingerprintUtility.hasNegativeSimilarFrameStartTimeEx(d)) {

          if (distance > 1840L) {
            newTags.add(c);
            index += 3;
          }
          continue;
        }
      }
      // 509 -214748364 -50 -50
      // 509          N -50 -50 SELECT C 13200
      if (b.getMostSimilarFramePosition() == Constants.FRAME_LOWEST_VALUE) {
        if (FingerprintUtility.hasPositiveSimilarFrameStartTime(a)
            && FingerprintUtility.hasNegativeSimilarFrameStartTimeEx(c)
            && FingerprintUtility.hasSimilarFrameStartTime(c, d)) {

          if (distance > 1280L) {
            newTags.add(c);
            index += 3;
          }
          continue;
        }
      }
      // -214748364 -50 -50 -50
      //          N -50 -50 -50 SELECT B 33900
      if (a.getMostSimilarFramePosition() == Constants.FRAME_LOWEST_VALUE) {
        if (FingerprintUtility.hasNegativeSimilarFrameStartTimeEx(b)
            && FingerprintUtility.hasSimilarFrameStartTime(b, c)
            && FingerprintUtility.hasSimilarFrameStartTime(c, d)) {

          if (distance > 1096L) {
            newTags.add(b);
            index += 3;
          }
        }
      }
    }
    return newTags;
  }
}
