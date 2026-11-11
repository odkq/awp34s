/*
 * AWP34S WP34-S Scientific Calculator Port to Android
 *
 * wp34s_android.cpp: C++ JNI wrapper interacting with the WP34S C code
 * (Bridge between Java and C++)
 *
 * Copyright (C) 2020 Pablo Martin Medrano <pablo@odkq.com>
 * Copyright (C) 2026 Ding Zhaojie <zhaojie_ding@msn.com>
 *
 * AWP34S is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AWP34S is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with 34S.  If not, see <http://www.gnu.org/licenses/>.
 */
#include <jni.h>
#include <string>
#include "wps34_wrapper.h"

JNIEnv *global_env;

extern "C" {
extern void display(void);

int android_logString(char *text) {
    jstring jTextParam = global_env->NewStringUTF(text);

    jclass activityClass = global_env->FindClass("com/megatops/wp34s/MainActivity");
    jmethodID logStringID = global_env->GetStaticMethodID(activityClass, "logString",
                                                          "(Ljava/lang/String;)V");
    if (!logStringID) {
        return -1;
    }
    global_env->CallStaticVoidMethod(activityClass, logStringID, jTextParam);
    return 0;
}
}

/* This is called as MainActivity.wp34sInit() from Java */
extern "C" JNIEXPORT jint JNICALL
Java_com_megatops_wp34s_MainActivity_wp34sInit(
        JNIEnv *env,
        jobject /* this */) {
    global_env = env;
    jint ret = 0;

    init_calculator();
    return ret;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_megatops_wp34s_CalcView_wp34sGetLcdData(
        JNIEnv *env,
        jobject /* this */) {
    /*
     * See lcd.h
     * (DISPLAY_DIGITS + SEGS_PER_DIGIT) + (3 * SEGS_PER_EXP_DIGIT) = 128
     * From 129 to 141 -> annunciators
     * 142 + (BITMAP_WIDTH * 6) = 400
     * 400 = 50 * 8 bits
     */
    jbyteArray retVal = env->NewByteArray(400);
    jbyte *buf = env->GetByteArrayElements(retVal, nullptr);
    memcpy((void *) buf, (void *) dots, 400);

    env->ReleaseByteArrayElements(retVal, buf, 0);

    return retVal;
}

/* This is called as MainActivity.wp34sPutKey() from Java */
extern "C" JNIEXPORT jint JNICALL
Java_com_megatops_wp34s_CalcView_wp34sPutKey(
        JNIEnv *env,
        jobject /* this */,
        jint key) {
    /* Translate row/column calculated key to the constants
     * in keys.h */
    jint translated;
    static const jint J2C_KEY[] =
            {-1, 0, 1, 2, 3, 4, 5,
             6, 7, 8, 9, 10, 11,
             12, -1, 13, 14, 15, 16,
             18, 19, 20, 21, 22,
             24, 25, 26, 27, 28,
             30, 31, 32, 33, 34,
             36, 37, 38, 39, 40};

#define K_RELEASE   98 // From xeq.h
#define K_HEARTBEAT 99 // From xeq.h
#define ARRAY_SZ(arr) (sizeof(arr) / sizeof(arr[0]))

    if (key == -2) { // in most case (100ms)
        translated = K_HEARTBEAT;
    } else if (key == -1) {
        translated = K_RELEASE;
    } else if ((key >= 0) && (key < ARRAY_SZ(J2C_KEY))) {
        translated = J2C_KEY[key];
    } else {
        return -1;
    }

    if (translated != K_HEARTBEAT) {
        // avoid heartbeat log flooding
        char logline[256];
        sprintf(logline, "Translated key %d", translated);
        android_logString(logline);
    }
    return xprocess_keycode(translated);
}
