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
#include <sstream>
#include <iomanip>
#include <iostream>
#include <fstream>
#include <algorithm>

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
#define THRESHOLD 4


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
int16_t correctValue(int16_t value, float amplify = 1.0) {
  value = abs(value);
  value *= amplify;

  if (value < THRESHOLD) {
    value = 0;
  }

  return value;
}

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

    // ***************************************************************************************
    // Rates: 0, 0.24, 0.43, 0.63, 0.8, 1
    std::vector<double> rates = {0, 0.24, 0.43, 0.63, 0.8, 1.0};
    std::vector<int> blockSizes(5);

    // Calculate block sizes
    for (size_t i = 0; i < 5; ++i) {
        blockSizes[i] = static_cast<int>(std::round(waveLength * (rates[i + 1] - rates[i])));
    }

    // Prepare the JSON result
    std::ostringstream jsonResult;
    jsonResult << "{";
    jsonResult << "\"blockCount\":" << blockSizes.size() << ",";
    jsonResult << "\"blockInfo\":[";

    // Sum up each block
    int startIndex = 0;
    int max = 0;

    for (size_t i = 0; i < 5; ++i) {
        int endIndex = startIndex + blockSizes[i];
        int32_t sum = 0;
        int16_t value;

        for (int j = startIndex; j < endIndex; ++j) {
          value = correctValue(data1[j]);
          // max value
          if (max < value) {
            max = value;
          }

          sum += value;
        }

        double average = blockSizes[i] > 0 ? static_cast<double>(sum) / blockSizes[i] : 0.0;

        // Add the block information to the JSON result
        jsonResult << "{";
        jsonResult << "\"blockSize\":" << blockSizes[i] << ",";
        jsonResult << "\"sum\":" << sum << ",";
        jsonResult << "\"average\":" << average << ",";
        jsonResult << "\"startRate\":" << rates[i] << ",";
        jsonResult << "\"endRate\":" << rates[i + 1];
        jsonResult << "}";

        if (i < 4) {
            jsonResult << ",";
        }

        startIndex = endIndex;
    }

    jsonResult << "],";
    jsonResult << "\"max\":" << max << "}";

    // Output the JSON result
    std::cout << jsonResult.str() << std::endl;

    // ***************************************************************************************

    // Fingerprint *ptr = ExtractFingerprint((const int16_t *)data1.data(), waveLength);
    // if (ptr != NULL_FINGERPRINT) {
    //     data = ptr->data;
    //     dataSize = ptr->dataSize;
    //     std::cout << "{\"size\":" ;
    //     std::cout << dataSize << ",\"data\":[" ;
    //     for (index = 0; index < dataSize; index++, data++) {
    //         if (index > 0) std::cout << ',';
    //         std::cout << (short)*data;
    //     }
    //     std::cout << "]}" << std::endl ;
    //     FingerprintFree(ptr);
    // }
    // else {
    //     std::cout << "{\"size\": 0,\"data\":[]}" << std::endl;
    // }

    return 0;
} /*doLocalFingerprinting*/

extern "C"
int doStreamFingerprinting(void) {
    // local fingerprint
    int index;
    int waveLength;
    int16_t value;
    const int16_t * data;
    int			 dataSize;

    std::cin >> waveLength;

    vector<int16_t> data1(waveLength);
    for (index = 0; index < waveLength; index++) {
        std::cin >> value;
        data1[index] = value;
    }
    while (index < waveLength) data1[index++] = (int16_t) 0;

    // Fingerprint *ptr = ExtractFingerprint((const int16_t *)data1.data(), waveLength);
    data = (const int16_t *)data1.data();
    dataSize = waveLength;
    std::cout << "{\"size\":" ;
    std::cout << dataSize << ",\"data\":[" ;
    for (index = 0; index < dataSize; index++, data++) {
        if (index > 0) std::cout << ',';
        std::cout << (short)*data;
    }
    std::cout << "]}" << std::endl ;

  return 0;
} /*doStreamFingerprinting*/

extern "C"

struct BlockInfo {
    int blockSize;
    int sum;
    float average;    // New field
    float startRate;  // New field
    float endRate;    // New field
};

struct DataFingerPrint {
    int blockCount;
    std::vector<BlockInfo> blockInfo;
    int max;
};

DataFingerPrint parseDataFingerPrintFromJson(const std::string& jsonString) {
    DataFingerPrint dataFingerprint;

    // Step 1: Parse blockInfo array
    size_t startPos = jsonString.find("[");
    size_t endPos = jsonString.find("]");
    std::string blockInfoArrayStr = jsonString.substr(startPos + 1, endPos - startPos - 1);

    // Parse each BlockInfo object in the array
    std::istringstream iss(blockInfoArrayStr);
    std::string blockInfoStr;
    while (std::getline(iss, blockInfoStr, '{')) {
        if (blockInfoStr.empty()) continue;

        BlockInfo blockInfo;
        std::istringstream iss2(blockInfoStr);

        std::string token;
        while (std::getline(iss2, token, ',')) {
            size_t colonPos = token.find(":");
            std::string key = token.substr(1, colonPos - 2); // Extract key (remove quotes)
            std::string value = token.substr(colonPos + 1);

            if (key == "blockSize")
                blockInfo.blockSize = std::stoi(value);
            else if (key == "sum")
                blockInfo.sum = std::stoi(value);
            else if (key == "average")
                blockInfo.average = std::stof(value);
            else if (key == "startRate")
                blockInfo.startRate = std::stof(value);
            else if (key == "endRate")
                blockInfo.endRate = std::stof(value);
        }

        dataFingerprint.blockInfo.push_back(blockInfo);
    }

    // Step 2: Parse blockCount and max values
    size_t blockCountPos = jsonString.find("blockCount");
    size_t maxPos = jsonString.find("max");

    std::string blockCountStr = jsonString.substr(blockCountPos);
    std::string maxStr = jsonString.substr(maxPos);

    dataFingerprint.blockCount = std::stoi(blockCountStr.substr(blockCountStr.find(":") + 1));
    dataFingerprint.max = std::stoi(maxStr.substr(maxStr.find(":") + 1));

    return dataFingerprint;
}

// ***************************************************************************************

struct SearchResult {
    float similarity;  // similarity value between 0 and 1
    int offset;        // offset value
    std::vector<BlockInfo> blockInfo;
    float amplify;
    int  triggerMax;
    int  dataMax;

};

float calculateVectorSimilarity(const std::vector<BlockInfo>& vec1, const std::vector<BlockInfo>& vec2) {
  // Weights for average and sum similarity
  const float weightAverage = 0.7;
  const float weightSum = 0.3;

  const int BLOCK_THRESHOLD = 2;

  // Ensure both vectors have the same size
  if (vec1.size() != vec2.size()) {
      std::cerr << "Vectors must have the same size for comparison." << std::endl;
      return 0.0; // or handle this error case appropriately
  }

  if (vec1[1].average > BLOCK_THRESHOLD || vec1[3].average > BLOCK_THRESHOLD) {
    return 0;
  }
  if (vec1[0].average < 8 || vec1[2].average < 10 || vec1[4].average < 9) {
    return 0;
  }

  // float totalSimilarity = 0.0;

  // // Calculate similarity for each corresponding pair of BlockInfo objects
  // for (size_t i = 0; i < vec1.size(); ++i) {
  //     const BlockInfo& info1 = vec1[i];
  //     const BlockInfo& info2 = vec2[i];

  //     // Calculate differences
  //     float diffAverage = std::fabs(info1.average - info2.average); // Absolute difference
  //     float diffSum = std::fabs(info1.sum - info2.sum);             // Absolute difference

  //     // Calculate similarity score for this pair
  //     float similarity = weightAverage * (1.0 - diffAverage) + weightSum * (1.0 - diffSum);

  //     // Aggregate similarity scores
  //     totalSimilarity += similarity;
  // }

  // // Normalize total similarity if needed (optional)
  // // For example, divide by vec1.size() to get an average similarity per BlockInfo pair.

  return 1;
}

void saveIntoFile(int value) {
  // **************************************************************************
  // Write data1 to data1.txt
  std::ofstream outFile("text1.txt");
  if (outFile.is_open()) {
      // Write the string to the file
      outFile << value;

      // Close the file
      outFile.close();
      std::cout << "Data successfully saved to text1.txt" << std::endl;
  } else {
      std::cerr << "Unable to open file for writing" << std::endl;
  }
  // **************************************************************************
}

float standardizeOffset(int offset) {
  return (float) offset * 1000 / 11025;
}

SearchResult getSearchResult(vector<int16_t> data, const DataFingerPrint& df) {

  SearchResult result;
  result.similarity = 0;
  result.amplify = 0;
  result.offset = 0;
  result.dataMax = 0;
  result.triggerMax = 0;
  
  int length = data.size();

  int triggerMax = df.max;
  int segmentSize = 11025;
  int interval = 220;

  const int START_THRESHOLD = 50;
  int start_offset = -1;
  // Max ABS value of data
  int16_t dataMax = 0;
  int segment_size = 0;
  for (size_t i = 0; i < length; i++) {

    if (i > interval && dataMax < START_THRESHOLD)
      break;

    if (segment_size == segmentSize + start_offset)
      break;
      
    int16_t abs_value = std::abs(data[i]); 
    if (abs_value > dataMax) {
        dataMax = abs_value;
    }
    segment_size++;
    if (start_offset < 0 && dataMax > START_THRESHOLD)
      start_offset = i;
  }

  // if (start_offset < 20)
  //     return result;

  int start, end;
  float similarity = 0;
  float amplify = 1;
  bool  is_find = false;
  const int BLOCK_THRESHOLD = 2;
  BlockInfo info;
  if (dataMax) amplify = (float) triggerMax / dataMax;

  // saveIntoFile(amplify);

  int start_pos = start_offset - 20;
  if (start_pos < 0) start_pos = 0;
  for (int offset = start_pos; offset < interval; offset++) {

    is_find = false;
    std::vector<BlockInfo> segmentsBlockInfo;
    for (int index = 0; index < df.blockCount; index++) {
      start = df.blockInfo[index].startRate * segmentSize + offset;
      end = df.blockInfo[index].endRate * segmentSize + offset;

      info.blockSize = end - start;
      info.sum = 0;
      for (int i = start; i < end; i++)
          info.sum += correctValue(data[i], amplify);
      
      info.average = static_cast<float>(info.sum) / info.blockSize;

      if (index == 0 && info.average < 8)
        break;
      if (index == 2 && info.average < 10)
        break;
      if ((index == 1 || index == 3) && info.average > BLOCK_THRESHOLD)
        break;
      if (index == 4 && info.average < 9)
        break;

      if (index == 4)
        is_find = true;

      segmentsBlockInfo.push_back(info); // Add this BlockInfo to your vector
    }
    if (is_find) {
      result.blockInfo = segmentsBlockInfo;
      result.similarity = 1;
      result.amplify = amplify;
      result.triggerMax = triggerMax;
      result.dataMax = dataMax;
      result.offset = start_offset;      
      result.offset = offset;
      return result;
    }
    // adjust amplify
    if (length > segmentSize + offset + 1 && dataMax < abs(data[segmentSize + offset + 1]))
    {
      dataMax = abs(data[segmentSize + offset + 1]);
      if (dataMax == 0)
        amplify = 1;
      else
        amplify = (float) triggerMax / dataMax;
    }
  }
  return result;
}

int doLocalFingerprinting_prev(void) {
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
} /*doLocalFingerprinting_prev*/


int main(int argc, char **argv) {

    if (argc > 1 && 0==strncmp(argv[1], "fingerprintprev", 15)) {
        return doLocalFingerprinting_prev();
    }

    if (argc > 1 && 0==strncmp(argv[1], "fingerprint", 11)) {
        return doLocalFingerprinting();
    }

    if (argc > 1 && 0==strncmp(argv[1], "stream", 6)) {
        return doStreamFingerprinting();
    }

  // read fingerPrint
    std::string dataFingerPrint;
    std::getline(std::cin, dataFingerPrint); 

  // read stream data
    int index;
    int16_t value;
    int aLength;
    std::cin >> aLength;

    vector<int16_t> data(aLength);
    for (index = 0; index < aLength; index++) {
        std::cin >> value;
        data[index] = value;
    }
    while (index < aLength) data[index++] = (int16_t) 0;

    DataFingerPrint df = parseDataFingerPrintFromJson(dataFingerPrint);
    SearchResult result = getSearchResult(data, df);

    // saveIntoFile(df.blockInfo[1].endRate);

    std::cout << "{\"similarity\":\""
      << result.similarity ;

    std::cout << "\",\"offset\":\""
      << standardizeOffset(result.offset);
      // std::cout << "\",\"amplify\":\""
      // << result.amplify ;
      // std::cout << "\",\"triggerMax\":\""
      // << result.triggerMax ;
      // std::cout << "\",\"dataMax\":\""
      // << result.dataMax << "\",";

      // for (size_t i = 0; i < result.blockInfo.size(); ++i) {
      //   // Add the block information to the JSON result
      //   std::cout << "{"
      //   << result.blockInfo[i].blockSize << ", "
      //   << result.blockInfo[i].sum << ", "
      //   << result.blockInfo[i].average 
      //   << "}";
      // }


    std::cout << "\"}" << std::endl ;


    // **************************************************************************


    // save into text of data1 and data2

    // char *mem = jsCompareFingerprint((int8_t*) data1.data(), aLength, (int8_t*) data2.data(), bLength);

    // std::cout << mem << std::endl;

    // free (mem);

    // FingerprintSimilarityComputer computer(data1, data2);

    // /* typedef struct FingerprintSimilarity {
    //   int mostSimilarFramePosition;  // the frame number that was most similar
    //   float mostSimilarStartTime;    // the start time of the most similar section
    //   float score;                   // the number of features matched per frame
    //   float similarity;              // similarity ranked in range (0.0 - 1.0)
    //                                  // 1.0 means that on average there is at least one match every frame.
    // } FingerprintSimilarity; */

    // FingerprintSimilarity result;
    // try {
    //     result = computer.getMatchResults();
    // } catch (...) {
    //     result.score = 0.0f;
    //     result.similarity = 0.0f;
    //     result.mostSimilarFramePosition = 1;
    //     result.mostSimilarStartTime = 1.0f;
    // }
    // // result.score = 0.0f;
    // // result.similarity = 0.0f;
    // // result.mostSimilarFramePosition = 1;
    // // result.mostSimilarStartTime = 1.0f;
    
    // std::cout << "{\"mostSimilarFramePosition\":\""
    //   << jsFixInteger(result.mostSimilarFramePosition) ;

    // std::cout << "\",\"mostSimilarStartTime\":\""
    //   << jsFixFloat(result.mostSimilarStartTime) ;

    // std::cout << "\",\"score\":\""
    //   << result.score ;

    // std::cout << "\",\"similarity\":\""
    //   << result.similarity ;

    // std::cout << "\"}" << std::endl ;

    // std::cerr << "aLength: " << aLength << std::endl;
    // std::cerr << "bLength: " << bLength << std::endl;

    return 0;
}

#endif
