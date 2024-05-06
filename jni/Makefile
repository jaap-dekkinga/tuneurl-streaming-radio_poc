# Makefile

EMPTY=

JAVA_HOME=/opt/java/jdk1.8.0_231

DEPS=-g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC

INCS= -I . -I ./cpp -I $(JAVA_HOME)/include -I $(JAVA_HOME)/include/linux

LDD_DEFS= -L /usr/lib -lm -lstdc++

OBJECTS=main.o \
    ArrayCoord.o \
    ArrayRankFloat.o \
    FastFourierTransform.o \
    Fingerprint.o \
    FingerprintManager.o \
    FingerprintProperties.o \
    FingerprintSimilarityComputer.o \
    MapRankInteger.o \
    PairManager.o \
    QuickSortInteger.o \
    RobustIntensityProcessor.o \
    Spectrogram.o \
    WindowFunction.o \
    $(EMPTY)

all: fingerprintexec

do-test: fingerprintexec  fingerprint-comparison-test.txt
		@echo "Test for jsExtractFingerprint"
		fingerprintexec fingerprint < fingerprint-test.txt > fingerprint-result.txt
		@echo "Test for jsCompareFingerprint"
		fingerprintexec < fingerprint-comparison-test.txt

fingerprintexec: ../script/runExternalFingerprintModule.sh  $(OBJECTS)
		g++ $(LDD_DEFS) -o fingerprintexec $(OBJECTS)
		@mkdir -p /home/ubuntu/audio/ 
		@rm -f /home/ubuntu/audio/fingerprintexec
		@cp -f fingerprintexec /home/ubuntu/audio/fingerprintexec
		chmod +x /home/ubuntu/audio/fingerprintexec
		@rm -f /home/ubuntu/audio/runExternalFingerprintModule.sh
		@cp -f ../script/runExternalFingerprintModule.sh  /home/ubuntu/audio/runExternalFingerprintModule.sh
		chmod +x /home/ubuntu/audio/runExternalFingerprintModule.sh

main.o: cpp/main.cpp Makefile
	  g++ $(DEPS) $(INCS) -c cpp/main.cpp -o main.o

ArrayCoord.o:  cpp/ArrayCoord.cpp  cpp/ArrayCoord.h
	  g++ $(DEPS) $(INCS) -c cpp/ArrayCoord.cpp -o ArrayCoord.o

ArrayRankFloat.o:  cpp/ArrayRankFloat.cpp  cpp/ArrayRankFloat.h
	  g++ $(DEPS) $(INCS) -c cpp/ArrayRankFloat.cpp -o ArrayRankFloat.o

FastFourierTransform.o:  cpp/FastFourierTransform.cpp  cpp/FastFourierTransform.h
	  g++ $(DEPS) $(INCS) -c cpp/FastFourierTransform.cpp -o FastFourierTransform.o

Fingerprint.o:  cpp/Fingerprint.cpp  cpp/Fingerprint.h
	  g++ $(DEPS) $(INCS) -c cpp/Fingerprint.cpp -o Fingerprint.o

FingerprintManager.o:  cpp/FingerprintManager.cpp  cpp/FingerprintManager.h
	  g++ $(DEPS) $(INCS) -c cpp/FingerprintManager.cpp -o FingerprintManager.o

FingerprintProperties.o:  cpp/FingerprintProperties.cpp  cpp/FingerprintProperties.h
	  g++ $(DEPS) $(INCS) -c cpp/FingerprintProperties.cpp -o FingerprintProperties.o

FingerprintSimilarityComputer.o:  cpp/FingerprintSimilarityComputer.cpp  cpp/FingerprintSimilarityComputer.h
	  g++ $(DEPS) $(INCS) -c cpp/FingerprintSimilarityComputer.cpp -o FingerprintSimilarityComputer.o

MapRankInteger.o:  cpp/MapRankInteger.cpp  cpp/MapRankInteger.h
	  g++ $(DEPS) $(INCS) -c cpp/MapRankInteger.cpp -o MapRankInteger.o

PairManager.o:  cpp/PairManager.cpp  cpp/PairManager.h
	  g++ $(DEPS) $(INCS) -c cpp/PairManager.cpp -o PairManager.o

QuickSortInteger.o:  cpp/QuickSortInteger.cpp  cpp/QuickSortInteger.h
	  g++ $(DEPS) $(INCS) -c cpp/QuickSortInteger.cpp -o QuickSortInteger.o

RobustIntensityProcessor.o:  cpp/RobustIntensityProcessor.cpp  cpp/RobustIntensityProcessor.h
	  g++ $(DEPS) $(INCS) -c cpp/RobustIntensityProcessor.cpp -o RobustIntensityProcessor.o

Spectrogram.o:  cpp/Spectrogram.cpp  cpp/Spectrogram.h
	  g++ $(DEPS) $(INCS) -c cpp/Spectrogram.cpp -o Spectrogram.o

WindowFunction.o:  cpp/WindowFunction.cpp  cpp/WindowFunction.h
	  g++ $(DEPS) $(INCS) -c cpp/WindowFunction.cpp -o WindowFunction.o

clean: force
	rm -f *.o

zap: clean
	rm -f fingerprintexec

force:


# eof: Makefile
