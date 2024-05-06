### Albon auth0 sdk 1.0.1

[Guide to installing 3rd party JARs](https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html)

#### authsdk-1.0.1.jar

1. the auth0 package

```xml

		<auth0-sdk.version>1.0.1</auth0-sdk.version>

		<dependency>
			<groupId>com.albon.auth</groupId>
			<artifactId>authsdk</artifactId>
			<version>${auth0-sdk.version}</version>
		</dependency>
```

2. install it as follow:

```bash
mvn install:install-file -Dfile=authsdk-1.0.1.jar -DpomFile=authsdk-1.0.1.pom

```

3. or run `install-authsdk.sh`

```bash
bash ./install-authsdk.sh

# [INFO] Scanning for projects...
# [INFO] 
# [INFO] ------------------< org.apache.maven:standalone-pom >-------------------
# [INFO] Building Maven Stub Project (No POM) 1
# [INFO] --------------------------------[ pom ]---------------------------------
# [INFO] 
# [INFO] --- maven-install-plugin:2.4:install-file (default-cli) @ standalone-pom ---
# [INFO] Installing /project/tuneurl-webrtc/tuneurl-webrtc/lib/authsdk-1.0.1.jar to /home/sid/.m2/repository/com/albon/auth/authsdk/1.0.1/authsdk-1.0.1.jar
# [INFO] Installing /project/tuneurl-webrtc/tuneurl-webrtc/lib/authsdk-1.0.1.pom to /home/sid/.m2/repository/com/albon/auth/authsdk/1.0.1/authsdk-1.0.1.pom
# [INFO] ------------------------------------------------------------------------
# [INFO] BUILD SUCCESS
# [INFO] ------------------------------------------------------------------------
# [INFO] Total time:  0.197 s
# [INFO] Finished at: 2024-02-22T10:03:45+08:00
# [INFO] ------------------------------------------------------------------------

```
