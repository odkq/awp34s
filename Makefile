# Makefile for Android WP 34S (apw34s)
app/build/outputs/apk/debug/app-debug.apk: gradle-6.8.2/bin/gradle app/src/main/cpp/keys.c
	gradle-6.8.2/bin/gradle build

app/src/main/cpp/keys.c: app/src/main/cpp/PATCH
	cd app/src/main/cpp && ./apply_patch

gradle-6.8.2/bin/gradle: gradle-6.8.2-bin.zip
	unzip gradle-6.8.2-bin.zip
	touch gradle-6.8.2/bin/gradle

gradle-6.8.2-bin.zip:
	wget https://downloads.gradle-dn.com/distributions/gradle-6.8.2-bin.zip

gradlew: gradle-6.8.2/bin/gradle
	gradle-6.8.2/bin/gradle wrapper

app/build/outputs/bundle/release/app-release.aab: gradlew app/build/outputs/apk/debug/app-debug.apk
	./gradlew  bundleRelease

app/build/outputs/bundle/release/.signed: app/build/outputs/bundle/release/app-release.aab
	jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore ../android-key-store/odkq-signing-key ./app/build/outputs/bundle/release/app-release.aab key0
	touch app/build/outputs/bundle/release/.signed

# Generate app-relase.aab
release: app/build/outputs/bundle/release/.signed

clean:
	cd app/src/main/cpp && ./clean_patch
	-rm gradle-6.8.2-bin.zip
	-rm -fR gradle-6.8.2
	-rm -fR build
	-rm -fR app/build
	-rm -fR app/.cxx
	-rm -fR .gradle
	-rm -fR gradle/ gradlew gradlew.bat

.PHONY: clean release
