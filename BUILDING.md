Building awp34s from source
===========================

There is a Makefile that:

  - Downloads gradle
  - Download the original WP 34s code from SVN
  - Patches the C code (see app/src/main/cpp/PATCH)+
  - Prepares the cpp/ directory for compilation
  - Call gradle to build the .apk

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

Using Android Studio
--------------------

Android Studio needs 'gradlew' wrapper in place to use latest gradle and find
all the files for the project. From a clean repo, you can Make:

```
make
make gradlew
```

To build the apk (that will pull the .c files from SVN) and the gradlew
wrapper, then you can start studio with

```
studio.sh .
```

And it will find all the dependencies

Making back the patch file
--------------------------

Any modifications made to the original .c files are not stored on this
repository, only a diff with the differences between them and the selected
revision in SVN_REVISION.

TODO: fix make_patch
