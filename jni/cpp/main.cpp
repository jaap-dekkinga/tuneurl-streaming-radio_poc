//
//  main.cpp
//  TuneURL
//
//  Created by Teodoro Albon <albonteddy@gmail.com> on 10/23/23
//  Copyright (c) 2023-2024 TuneURL Inc. All rights reserved.
//

#ifdef __for_emcc
#include <emscripten/bind.h>
#else
#define EMSCRIPTEN_KEEPALIVE
#endif

#include <algorithm>
#include <vector>
#include <string.h>
#include <cmath>

#include "Fingerprint.h"
#include "FingerprintManager.h"
#include "FingerprintSimilarityComputer.h"

#include "stringiobuffer.h"

#ifdef __for_emcc
    using namespace emscripten;
#endif

using std::vector;

#define NULL_FINGERPRINT (Fingerprint *)0
#define NULL_INT16_T (int16_t *)0


/* struct FingerPrinterOutput {
  std::vector <int8_t>   fingerprintData;
                     int fingerprinSize;
}; */


/* struct FingerPrinterInput {
  std::vector <int16_t>   wave;
                      int waveLength;
}; */


extern "C" {

  EMSCRIPTEN_KEEPALIVE
  int jsGetSizeOf_int8_t() {
      return (int) sizeof(int8_t);
  }

  EMSCRIPTEN_KEEPALIVE
  int jsGetSizeOf_int16_t() {
      return (int) sizeof(int16_t);
  }

  EMSCRIPTEN_KEEPALIVE
  // get Version
  double jsTuneUrlSDK(float pa, float pb, float pt) {
      float v = (1 - pt) * pa + pt * pb;
      return v;
  }

  EMSCRIPTEN_KEEPALIVE
  // get Fingerprint Sample Rate
  double jsTuneUrlFingerprintSampleRate() {
      float v = FINGERPRINT_SAMPLE_RATE;
      return v;
  }

  EMSCRIPTEN_KEEPALIVE
  int jsFixInteger(int value) {
    double v = (double) value;
    if (0 > v) {
        if (abs (v) > 1.0) {
            v = v / 10.0;
            v = v + 0.5;
            return (int) v;
        }
    }
    return value;
  }

  EMSCRIPTEN_KEEPALIVE
  float jsFixFloat(float value) {
    double v = (double) value;
    if (0 > v) {
        if (abs (v) > 1.0) {
            v = v / 10.0;
            v = v + 0.5;
        }
    }
    return (float) v;
  }

}

extern "C" {
  EMSCRIPTEN_KEEPALIVE
  char *toStringInt16(int16_t* data, int dataSize)
  {
    std::string s;
    std::existing_string_buf b(s);
    std::ostream o(&b);
    int index;
      o << "{\"size\":" ;
      o << dataSize << ",\"data\":[" ;
      for (index = 0; index < dataSize; index++, data++) {
        o << *data;
        if (index + 1 < dataSize) o << ',';
      }
      o << "]}" ;
    size_t n = 1 + s.length();
    char *mem = (char *) malloc(n);
    memcpy (mem, s.c_str(), n);
    mem[n] = '\0';
    return mem;
  }
}


extern "C" {
  EMSCRIPTEN_KEEPALIVE
  char * jsExtractFingerprint(int16_t* pWave, int waveLength)
  {
    std::string s;
    std::existing_string_buf b(s);
    std::ostream o(&b);
    uint8_t* data;
    int			 dataSize;
    Fingerprint *ptr;
    int     index;
    ptr = ExtractFingerprint((const int16_t *)pWave, waveLength);
    if (ptr != NULL_FINGERPRINT) {
      data = ptr->data;
      dataSize = ptr->dataSize;
      o << "{\"size\":" ;
      o << dataSize << ",\"data\":[" ;
      for (index = 0; index < dataSize; index++, data++) {
        o << (short)*data;
        if (index + 1 < dataSize) o << ',';
      }
      o << "]}" ;
      FingerprintFree(ptr);
    }
    else {
      o << "{\"size\": 0,\"data\":[]}" ;
    }

    size_t n = 1 + s.length();
    char *mem = (char *) malloc(n);
    memcpy (mem, s.c_str(), n);
    mem[n] = '\0';
    return mem;
  }
}

extern "C" {
  EMSCRIPTEN_KEEPALIVE
  char * jsCompareFingerprint(int8_t* aWave, int aLength, int8_t* bWave, int bLength)
  {
    std::string s;
    std::existing_string_buf b(s);
    std::ostream o(&b);

    size_t dataSize = aLength > bLength ? bLength : aLength;

    // copy the fingerprint data
    vector<uint8_t> data1(dataSize);
    memcpy(data1.data(), aWave, dataSize);

    vector<uint8_t> data2(dataSize);
    memcpy(data2.data(), bWave, dataSize);

    FingerprintSimilarityComputer computer(data1, data2);

    /* typedef struct FingerprintSimilarity {
      int mostSimilarFramePosition;  // the frame number that was most similar
      float mostSimilarStartTime;    // the start time of the most similar section
      float score;                   // the number of features matched per frame
      float similarity;              // similarity ranked in range (0.0 - 1.0)
                                     // 1.0 means that on average there is at least one match every frame.
    } FingerprintSimilarity; */

    FingerprintSimilarity result;
    try {
        computer.getMatchResultsEx(&result);
    } catch (...) {
        result.score = 0.0f;
        result.similarity = 0.0f;
        result.mostSimilarFramePosition = 1;
        result.mostSimilarStartTime = 1.0f;
    }

    o << "{\"mostSimilarFramePosition\":\""
      << jsFixInteger(result.mostSimilarFramePosition) ;

    o << "\",\"mostSimilarStartTime\":\""
      << jsFixFloat(result.mostSimilarStartTime) ;

    o << "\",\"score\":\""
      << result.score ;

    o << "\",\"similarity\":\""
      << result.similarity ;

    o << "\"}" ;

    size_t n = 1 + s.length();
    char *mem = (char *) malloc(n);
    memcpy (mem, s.c_str(), n);
    mem[n] = '\0';
    return mem;
  }
}

#ifdef __for_emcc

/* */
EMSCRIPTEN_BINDINGS(my_jsExtractFingerprint_module) {

    // value_object<FingerPrinterInput>("FingerPrinterInput")
    //   .field("wave", &FingerPrinterInput::wave)
    //   .field("waveLength", &FingerPrinterInput::waveLength);

    // value_object<FingerPrinterOutput>("FingerPrinterOutput")
    //   .field("fingerprintData", &FingerPrinterOutput::fingerprintData)
    //   .field("fingerprinSize", &FingerPrinterOutput::fingerprinSize);

    function("jsGetSizeOf_int8_t", &jsGetSizeOf_int8_t);
    function("jsGetSizeOf_int16_t", &jsGetSizeOf_int16_t);

    function("jsTuneUrlSDK", &jsTuneUrlSDK);

    function("jsTuneUrlFingerprintSampleRate", &jsTuneUrlFingerprintSampleRate);

    function("toStringInt16", &toStringInt16, allow_raw_pointers()); 

    function("jsExtractFingerprint", &jsExtractFingerprint, allow_raw_pointers());

    function("jsFixInteger", &jsFixInteger);

    function("jsFixFloat", &jsFixFloat);

    function("jsCompareFingerprint", &jsCompareFingerprint, allow_raw_pointers());
}
/* */

#else

extern "C"
int doLocalFingerprinting(void) {
    // local fingerprint
    int index;
    int waveLength;
    int16_t value;
    uint8_t* data;
    int			 dataSize;

    std::cin >> waveLength;

    vector<int16_t> data1(waveLength);
    for (index = 0; index < waveLength; index++) {
        std::cin >> value;
        data1[index] = value;
    }
    while (index < waveLength) data1[index++] = (int16_t) 0;

    Fingerprint *ptr = ExtractFingerprint((const int16_t *)data1.data(), waveLength);
    if (ptr != NULL_FINGERPRINT) {
        data = ptr->data;
        dataSize = ptr->dataSize;
        std::cout << "{\"size\":" ;
        std::cout << dataSize << ",\"data\":[" ;
        for (index = 0; index < dataSize; index++, data++) {
            if (index > 0) std::cout << ',';
            std::cout << (short)*data;
        }
        std::cout << "]}" << std::endl ;
        FingerprintFree(ptr);
    }
    else {
        std::cout << "{\"size\": 0,\"data\":[]}" << std::endl;
    }

    return 0;
} /*doLocalFingerprinting*/

extern "C"
int main(int argc, char **argv) {

    int index;
    uint8_t value;
    int aLength;
    int bLength;

    if (argc > 1 && 0==strncmp(argv[1], "fingerprint", 11)) {
        return doLocalFingerprinting();
    }

    std::cin >> aLength;
    std::cin >> bLength;

    vector<uint8_t> data1(aLength);
    for (index = 0; index < aLength; index++) {
        std::cin >> value;
        data1[index] = value;
    }
    while (index < aLength) data1[index++] = (uint8_t) 0;

    vector<uint8_t> data2(bLength);
    for (index = 0; index < bLength; index++) {
        std::cin >> value;
        data2[index] = value;
    }
    while (index < bLength) data2[index++] = (uint8_t) 0;

    // char *mem = jsCompareFingerprint((int8_t*) data1.data(), aLength, (int8_t*) data2.data(), bLength);

    // std::cout << mem << std::endl;

    // free (mem);

    FingerprintSimilarityComputer computer(data1, data2);

    /* typedef struct FingerprintSimilarity {
      int mostSimilarFramePosition;  // the frame number that was most similar
      float mostSimilarStartTime;    // the start time of the most similar section
      float score;                   // the number of features matched per frame
      float similarity;              // similarity ranked in range (0.0 - 1.0)
                                     // 1.0 means that on average there is at least one match every frame.
    } FingerprintSimilarity; */

    FingerprintSimilarity result;
    try {
        result = computer.getMatchResults();
    } catch (...) {
        result.score = 0.0f;
        result.similarity = 0.0f;
        result.mostSimilarFramePosition = 1;
        result.mostSimilarStartTime = 1.0f;
    }

    std::cout << "{\"mostSimilarFramePosition\":\""
      << jsFixInteger(result.mostSimilarFramePosition) ;

    std::cout << "\",\"mostSimilarStartTime\":\""
      << jsFixFloat(result.mostSimilarStartTime) ;

    std::cout << "\",\"score\":\""
      << result.score ;

    std::cout << "\",\"similarity\":\""
      << result.similarity ;

    std::cout << "\"}" << std::endl ;

    // std::cerr << "aLength: " << aLength << std::endl;
    // std::cerr << "bLength: " << bLength << std::endl;

    return 0;
}

#endif
