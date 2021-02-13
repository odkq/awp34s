#
# AWP34S WP34-S Scientific Calculator Port to Android
#
# CMakeLists.txt CMake configuration to build the wp34s library with NDK
#

# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

set (CMAKE_CXX_FLAGS "-DANDROID -DINCLUDE_USER_MODE -I./ -std=c++11")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        wp34s

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        wp34s_android.cpp
        wp34s_wrapper.c
        alpha.c
        charmap.c
        commands.c
        complex.c
        consts.c
        data.c
        date.c
        decn.c
        display.c
        font.c
        int.c
        keys.c
        lcd.c
        matrix.c
        printer.c
        pretty.c
        prt.c
        serial.c
        stats.c
        stopwatch.c
        storage.c
        string.c
        xeq.c
        xrom.c
        decNumber/decNumber.c
        decNumber/decContext.c
        decNumber/decimal64.c
        decNumber/decimal128.c
        consts/const_0_0001.c
        consts/const_0_001.c
        consts/const_0_0195.c
        consts/const_0_04.c
        consts/const_0_05.c
        consts/const_0_1.c
        consts/const_0_2214.c
        consts/const_0_25.c
        consts/const_0_2.c
        consts/const_0_4.c
        consts/const__0_5.c
        consts/const_0_5.c
        consts/const_0_665.c
        consts/const_0_6.c
        consts/const_0_75.c
        consts/const_0_85.c
        consts/const_0_97.c
        consts/const_0_995.c
        consts/const_0_9.c
        consts/const__0.c
        consts/const_0.c
        consts/const_100000.c
        consts/const_100.c
        consts/const_10.c
        consts/const_1_3.c
        consts/const_150.c
        consts/const_1_5.c
        consts/const_15.c
        consts/const_16.c
        consts/const_1_7.c
        consts/const_180.c
        consts/const__1.c
        consts/const_1.c
        consts/const_1e_10000.c
        consts/const_1e_14.c
        consts/const_1e_24.c
        consts/const_1e_32.c
        consts/const_1e_37.c
        consts/const_1on60.c
        consts/const__1onPI.c
        consts/const_200.c
        consts/const_20.c
        consts/const_21.c
        consts/const_2_326.c
        consts/const_256.c
        consts/const__2.c
        consts/const_2.c
        consts/const_2on3.c
        consts/const_2PI.c
        consts/const_2pow64.c
        consts/const_300.c
        consts/const_32.c
        consts/const_36000.c
        consts/const_360.c
        consts/const__3.c
        consts/const_3.c
        consts/const_400.c
        consts/const_45.c
        consts/const_4.c
        consts/const_500.c
        consts/const_50.c
        consts/const_5.c
        consts/const_5on6.c
        consts/const_60.c
        consts/const_6.c
        consts/const_7.c
        consts/const_8.c
        consts/const_9000.c
        consts/const_90.c
        consts/const_9.c
        consts/const_9on5.c
        consts/const_e.c
        consts/const_egamma.c
        consts/const_gammaC00.c
        consts/const_gammaC01.c
        consts/const_gammaC02.c
        consts/const_gammaC03.c
        consts/const_gammaC04.c
        consts/const_gammaC05.c
        consts/const_gammaC06.c
        consts/const_gammaC07.c
        consts/const_gammaC08.c
        consts/const_gammaC09.c
        consts/const_gammaC10.c
        consts/const_gammaC11.c
        consts/const_gammaC12.c
        consts/const_gammaC13.c
        consts/const_gammaC14.c
        consts/const_gammaC15.c
        consts/const_gammaC16.c
        consts/const_gammaC17.c
        consts/const_gammaC18.c
        consts/const_gammaC19.c
        consts/const_gammaC20.c
        consts/const_gammaC21.c
        consts/const_gammaR.c
        consts/const_hms_threshold.c
        consts/const__inf.c
        consts/const_inf.c
        consts/const_ln10.c
        consts/const_ln2.c
        consts/const_NaN.c
        consts/const_phi.c
        consts/const_PI.c
        consts/const_PIon180.c
        consts/const_PIon200.c
        consts/const_PIon2.c
        consts/const_randfac.c
        consts/const_recipsqrt2PI.c
        consts/const_root2on2.c
        consts/const_sqrt2PI.c)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        wp34s

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})
