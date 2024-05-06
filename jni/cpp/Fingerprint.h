//
//  Fingerprint.h
//  TuneURL
//
//  Created by Gerrit on 5/4/21.
//  Copyright © 2021 TuneURL Inc. All rights reserved.
//

#ifndef FINGERPRINT_H
#define FINGERPRINT_H

#include <stdlib.h>

#if defined(__for_unix) || defined(__for_emcc)
#include "forunix.h"
#endif

#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

// the audio sample rate required for fingerprinting
#define FINGERPRINT_SAMPLE_RATE		10240.0

struct Fingerprint {

	uint8_t		*data;
	int			dataSize __attribute__((aligned(4)));

} __attribute__((packed));
typedef struct Fingerprint Fingerprint;

struct FingerprintSimilarity {

	int mostSimilarFramePosition __attribute__((aligned(8)));
																// the frame number that was most similar
	float mostSimilarStartTime;		// the start time of the most similar section
	float score;									// the number of features matched per frame
	float similarity;							// similarity ranked in range (0.0 - 1.0)
																// 1.0 means that on average there is at least one match every frame.
} __attribute__((packed));
typedef struct FingerprintSimilarity FingerprintSimilarity;

extern FingerprintSimilarity CompareFingerprints(const Fingerprint *fingerprint1, const Fingerprint *fingerprint2);
extern Fingerprint *ExtractFingerprint(const int16_t *wave, int waveLength);
extern Fingerprint *ExtractFingerprintFromRawFile(const char *filePath);
extern void FingerprintFree(Fingerprint *fingerprint);

#ifdef __cplusplus
}
#endif // __cplusplus

#endif /* FINGERPRINT_H */
