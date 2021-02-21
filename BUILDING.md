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

Command line tools: unzip, patch, make, gcc libncurses-dev, subversion

On Debian 10:

```apt-get install patch unzip build-essential libncurses-dev subversion```

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


