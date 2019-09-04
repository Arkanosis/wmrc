JAVA_HOME=/opt/android-studio/jre/
ANDROID_HOME=/home/arkanosis/Android/Sdk

build: app/build/outputs/apk/release/app-release-unsigned.apk

app/build/outputs/apk/release/app-release-unsigned.apk:
	JAVA_HOME=$(JAVA_HOME) ./gradlew :app:build

app/build/outputs/apk/release/app-release-signed.apk: arkanosis.keystore app/build/outputs/apk/release/app-release-unsigned.apk
	cp app/build/outputs/apk/release/app-release-unsigned.apk $@
	$(JAVA_HOME)/bin/jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore arkanosis.keystore $@ arkanosis

install:
	JAVA_HOME=$(JAVA_HOME) ./gradlew :app:uninstallDebug :app:installDebug

run:
	adb shell am start -n net.arkanosis.wmrc/net.arkanosis.wmrc.activities.MainActivity

arkanosis.keystore:
	$(JAVA_HOME)/bin/keytool -genkey -v -keystore arkanosis.keystore -alias arkanosis -keyalg RSA -keysize 4096 -validity 100

fdroid:
	mkdir $@
	cd $@ && ANDROID_HOME=$(ANDROID_HOME) fdroid init

package: app/build/outputs/apk/release/app-release-signed.apk fdroid
	cp app/build/outputs/apk/release/app-release-signed.apk fdroid/repo/wmrc.apk
	cd fdroid && ANDROID_HOME=$(ANDROID_HOME) fdroid update && ANDROID_HOME=$(ANDROID_HOME) fdroid gpgsign && ANDROID_HOME=$(ANDROID_HOME) fdroid server update

experiment:
	JAVA_HOME=$(JAVA_HOME) ./gradlew :lib:run

clean:
	JAVA_HOME=$(JAVA_HOME) ./gradlew clean

.PHONY: build install run package experiment clean
.NOTPARALLEL: run experiment
