#ifndef __unix_64
#define __unix_64

// for memcpy()
#include <cstring>

// for qsort()
#include <cstdlib>

#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

#ifdef __for_emcc

typedef unsigned char   uint8_t;
typedef unsigned short  uint16_t;
typedef short  int16_t;

#else

typedef __uint8_t uint8_t;

typedef __int16_t int16_t;

#endif

#ifdef __cplusplus
}
#endif // __cplusplus

#endif
