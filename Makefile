JAVA_HOME=/opt/android-studio/jre/

build:
	JAVA_HOME=$(JAVA_HOME) ./gradlew :app:build

sign: arkanosis.keystore
	cp app/build/outputs/apk/release/app-release-unsigned.apk app/build/outputs/apk/release/app-release-signed.apk
	$(JAVA_HOME)/bin/jarsigner -verbose -sigalg SHA1withRSA -keystore arkanosis.keystore app/build/outputs/apk/release/app-release-signed.apk arkanosis

install:
	JAVA_HOME=$(JAVA_HOME) ./gradlew :app:uninstallDebug :app:installDebug

run:
	adb shell am start -n net.arkanosis.wmrc/net.arkanosis.wmrc.activities.MainActivity

arkanosis.keystore:
	$(JAVA_HOME)/bin/keytool -genkey -v -keystore arkanosis.keystore -alias arkanosis -keyalg RSA -keysize 2048 -validity 100

experiment:
	JAVA_HOME=$(JAVA_HOME) ./gradlew :lib:run

clean:
	JAVA_HOME=$(JAVA_HOME) ./gradlew clean

.PHONY: build sign install run experiment clean
.NOTPARALLEL: run experiment
