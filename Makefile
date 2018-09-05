JAVA_HOME=/opt/android-studio/jre/

build:
	JAVA_HOME=$(JAVA_HOME) ./gradlew :app:build

install:
	JAVA_HOME=$(JAVA_HOME) ./gradlew :app:uninstallDebug :app:installDebug

run:
	adb shell am start -n net.arkanosis.wmrc/net.arkanosis.wmrc.activities.MainActivity

experiment:
	JAVA_HOME=$(JAVA_HOME) ./gradlew :lib:run

.PHONY: build install run experiment
.NOTPARALLEL: run experiment
