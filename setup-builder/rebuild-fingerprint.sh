cd jni

make zap && make && make do-test && make zap

rm -f *.o
rm -f fingerprintexec
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/main.cpp -o main.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/ArrayCoord.cpp -o ArrayCoord.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/ArrayRankFloat.cpp -o ArrayRankFloat.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/FastFourierTransform.cpp -o FastFourierTransform.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/Fingerprint.cpp -o Fingerprint.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/FingerprintManager.cpp -o FingerprintManager.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/FingerprintProperties.cpp -o FingerprintProperties.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/FingerprintSimilarityComputer.cpp -o FingerprintSimilarityComputer.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/MapRankInteger.cpp -o MapRankInteger.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/PairManager.cpp -o PairManager.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/QuickSortInteger.cpp -o QuickSortInteger.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/RobustIntensityProcessor.cpp -o RobustIntensityProcessor.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/Spectrogram.cpp -o Spectrogram.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/WindowFunction.cpp -o WindowFunction.o
g++ -L /usr/lib -lm -lstdc++ -o fingerprintexec main.o ArrayCoord.o ArrayRankFloat.o FastFourierTransform.o Fingerprint.o FingerprintManager.o FingerprintProperties.o FingerprintSimilarityComputer.o MapRankInteger.o PairManager.o QuickSortInteger.o RobustIntensityProcessor.o Spectrogram.o WindowFunction.o
chmod +x fingerprintexec
cp fingerprintexec /home/ubuntu/audio/fingerprintexec
chmod +x /home/ubuntu/audio/runExternalFingerprintModule.sh

Test for jsExtractFingerprint
./fingerprintexec fingerprint < fingerprint-test.txt > fingerprint-result.txt
Test for jsCompareFingerprint
./fingerprintexec < fingerprint-comparison-test.txt
{"mostSimilarFramePosition":"-25","mostSimilarStartTime":"-0.775","score":"0.000158755","similarity":"0.000158755"}

rm -f *.o
rm -f fingerprintexec
