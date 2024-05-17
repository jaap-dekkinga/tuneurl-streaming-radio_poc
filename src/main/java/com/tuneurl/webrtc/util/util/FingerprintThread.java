package com.tuneurl.webrtc.util.util;

import com.tuneurl.webrtc.util.controller.dto.FingerprintCollection;
import com.tuneurl.webrtc.util.controller.dto.FingerprintCompareResponse;
import com.tuneurl.webrtc.util.controller.dto.FingerprintResponse;
import com.tuneurl.webrtc.util.value.Constants;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Random;

public class FingerprintThread extends FingerprintUtility implements Runnable {

    final private MessageLogger logger;
    public FingerprintThread() {
        this.logger = MessageLogger.getMessageLoggerInstance();

    }

    @Getter
    @Setter
    private FingerprintCollection fingerprintCollectionResult;

    String rootDir;
    short[] data;
    Long elapse;
    Random random;
    Long fingerprintRate;
    StringBuffer dataFingerprintBuffer;
    int dataFingerprintBufferSize;


    public FingerprintThread(
            final String rootDir,
            final short[] data,
            Long elapse,
            Random random,
            final Long fingerprintRate,
            StringBuffer dataFingerprintBuffer,
            int dataFingerprintBufferSize) {
        this.logger = MessageLogger.getMessageLoggerInstance();

        this.rootDir = rootDir;
        this.data = data;
        this.elapse = elapse;
        this.random = random;
        this.fingerprintRate = fingerprintRate;
        this.dataFingerprintBuffer = dataFingerprintBuffer;
        this.dataFingerprintBufferSize = dataFingerprintBufferSize;
    }

    /**
     * Helper to process fingerprint
     *
     * @param rootDir String
     * @param data Array of short
     * @param elapse Long
     * @param random Random
     * @param fingerprintRate Long
     * @param dataFingerprintBuffer StringBuffer
     * @param dataFingerprintBufferSize int
     * @return FingerprintCollection
     */
    public FingerprintCollection collectFingerprint(
            final String rootDir,
            final short[] data,
            Long elapse,
            Random random,
            final Long fingerprintRate,
            StringBuffer dataFingerprintBuffer,
            int dataFingerprintBufferSize) {

        int incrementDelta = Constants.FINGERPRINT_INCREMENT_DELTA;

        final String signature = "collectFingerprint";
        ArrayList<FingerprintResponse> frSelection = new ArrayList<>();
        ArrayList<FingerprintCompareResponse> selection = new ArrayList<>();
        int increment;
        long timeOffset;
        long iStart, iEnd;
        short[] dData;
        int dSize;
        FingerprintResponse fr = null;
        FingerprintCompareResponse fcr = null;
        boolean isDebugOn = Constants.DEBUG_FINGERPRINTING;

        for (increment = 0; increment < 100; increment += incrementDelta) {
            timeOffset = elapse + increment;

            iStart = Converter.muldiv(timeOffset, fingerprintRate, 1000L);
            iEnd = Converter.muldiv(timeOffset + 1000L, fingerprintRate, 1000L);

            dSize = (int) (iEnd - iStart);
            if (dSize < 16) continue;
            dData = Converter.convertListShortEx(data, (int) iStart, dSize);
            if (dData == null) break;

            fr = fingerprintExternals.runExternalFingerprinting(
                    random, rootDir, dData, dData.length);

            fcr =
                    compareFingerprint(
                            fr,
                            timeOffset,
                            rootDir,
                            random,
                            dataFingerprintBuffer,
                            dataFingerprintBufferSize);

            if (fcr != null) {
                selection.add(fcr);
                frSelection.add(fr);
                if (isDebugOn) {
                    this.logger.logEntry(
                            signature,
                            new Object[] {
                                    "pos=", fcr.getOffset(),
                                    "score=", fcr.getScore(),
                                    "similarity=", fcr.getSimilarity(),
                                    "Frame=", fcr.getMostSimilarFramePosition(),
                                    "StartTime=", fcr.getMostSimilarStartTime()
                            });
                }
            }
        }
        FingerprintCollection result = new FingerprintCollection();
        result.setFrCollection(frSelection);
        result.setFcrCollection(selection);

        setFingerprintCollectionResult(result);
        return result;
    }

    @Override
    public void run() {
        collectFingerprint(rootDir, data, elapse, random, fingerprintRate, dataFingerprintBuffer, dataFingerprintBufferSize);
    }
}
