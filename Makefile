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

clean:
	cd app/src/main/cpp && ./clean_patch
	-rm gradle-6.8.2-bin.zip
	-rm -fR gradle-6.8.2
	-rm -fR build
	-rm -fR app/build
	-rm -fR app/.cxx
	-rm -fR .gradle
