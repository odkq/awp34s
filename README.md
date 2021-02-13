# awp34s

WP 34s Calculator Port to Android

<img src="doc/awp34s-a2.jpg" alt="Screenshot" width="200"/>

Port of the the WP 34S calculator (open source firmware for the HP 30b/20b
calculators) to Android.

More information about WP 34S:

  - [Wikipedia Entry](https://en.wikipedia.org/wiki/HP_30b#WP_31S,_WP_34S_and_WP_34C)
  - [SourceForge Project](https://sourceforge.net/projects/wp34s/)
  - [Online Seller](https://commerce.hpcalc.org/34s.php)
  - [Original Wiki](http://www.wiki4hp.com/doku.php?id=34s:repurposing_project)

The WP 34S project has an IPhone app, but no Android app. Wanting to learn
about Android and NDK development, I did this.

I labeled this as a port rather than as an emulator as it is not
emulating the CPU of the physical device. The firmware itself originally
written in portable C is compiled along with the
Android Native Development Kit (NDK).

The code for the interface and modifications to the WP 34s firmware are
Free Software and released under the GNU Public License v3, as the WP
34S code itself.

Thanks to Walter Bonin, Paul Dale, Marcus von Cube and the rest of the WP 34S
contributors for the excelent calculator.

Building awp34s from source
===========================

There is a Makefile that download the dependencies:

  - gradle
  - the original WP 34s code from SVN

Patches the C code (see app/src/main/cpp/PATCH) and prepares the cpp/ directory
for compilation, then call gradle to build the .apk

Linux distribution dependencies
-------------------------------

Java:

On Debian 10:

```apt-get install openjdk-11-jre```

Command line tools: unzip, patch, make, gcc libncurses-dev

On Debian 10:

```apt-get install patch unzip build-essential libncurses-dev```

Installing Android SDK, Android 29 API, Platform Tools and NDK using commandline tools
--------------------------------------------------------------------------------------

The android SDK and dependencies will take around 10 GB of hard drive space

```
# Create a directory writable by the user for the android sdk
sudo mkdir /opt/android-sdk
sudo chown $(whoami) /opt/android-sdk
cd /opt/android-sdk
# Install the commandlintools
wget https://dl.google.com/android/repository/commandlinetools-linux-6858069_latest.zip
unzip commandlinetools*.zip
rm *.zip
# Set ANDROID_HOME for this and future sessions
export ANDROID_HOME=/opt/android-sdk
echo 'export ANDROID_HOME=/opt/android-sdk/' >> ~/.bashrc
# Somehow cmdline-tools need to be in a subdirectory called 'latest' to work
mkdir cmdline-tools/latest
mv cmdline-tools/* cmdline-tools/latest/
cd cmdline-tools/latest/bin
# Install API layer, platform tools and ndk
./sdkmanager 'platforms;android-29' platform-tools ndk-bundle
```

Compiling everything
--------------------

```
$ make
```

This will download gradle, and build the .apk into
app/build/outputs/apk/release/app-release-unsigned.apk

