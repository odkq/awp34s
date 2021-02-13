/*
 * AWP34S WP34-S Scientific Calculator Port to Android
 *
 * wp34s_android.cpp: C++ JNI wrapper interacting with the WP34S C code
 * (Bridge between Java and C++)
 *
 * Copyright (C) 2020 Pablo Martin Medrano <pablo@odkq.com>
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
}

extern "C" {
int android_logString(char *text) {
    jstring jTextParam = global_env->NewStringUTF(text);

    jclass activityClass = global_env->FindClass("com/odkq/wp34s/MainActivity");
    jmethodID logStringID = global_env->GetStaticMethodID(activityClass, "logString",
                                                          "(Ljava/lang/String;)V");
    if (!logStringID) {
        return -1;
    }
    global_env->CallStaticVoidMethod(activityClass, logStringID, jTextParam);
    return 0;
}

/* This is called as MainActivity.wp34sInit() from Java */
extern "C" JNIEXPORT jint JNICALL
Java_com_odkq_wp34s_MainActivity_wp34sInit(
        JNIEnv *env,
        jobject /* this */) {
    global_env = env;
    jint ret = 0;

    init_calculator();
    return ret;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_odkq_wp34s_CalcView_wp34sGetLcdData(
        JNIEnv *env,
        jobject /* this */) {
    //display();
    /*
     * See lcd.h
     * (DISPLAY_DIGITS + SEGS_PER_DIGIT) + (3 * SEGS_PER_EXP_DIGIT) = 128
     * From 129 to 141 -> annunciators
     * 142 + (BITMAP_WIDTH * 6) = 400
     * 400 = 50 * 8 bits
     */
    jbyteArray retVal = env->NewByteArray(400);
    jbyte *buf = env->GetByteArrayElements(retVal, NULL);
    // memcpy((void *) buf, (void *) data, 50);
    memcpy((void *) buf, (void *) dots, 400);

    env->ReleaseByteArrayElements(retVal, buf, 0);

    return retVal;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_odkq_wp34s_CalcView_wp34sIsDot(
        JNIEnv *env,
        jobject /* this */,
        jint n) {
    return is_dot_wrapper(n);
}

/* This is called as MainActivity.wp34sPutKey() from Java */
extern "C" JNIEXPORT jint JNICALL
Java_com_odkq_wp34s_CalcView_wp34sPutKey(
        JNIEnv *env,
        jobject /* this */,
        jint key) {
    /* Translate row/column calculated key to the constants
     * in keys.h */
    jint translated;
#define K_RELEASE 98   // From xeq.h
    jint jtockey[] =
            {-1, 0, 1, 2, 3, 4, 5,
             6, 7, 8, 9, 10, 11,
             12, -1, 13, 14, 15, 16,
             18, 19, 20, 21, 22,
             24, 25, 26, 27, 28,
             30, 31, 32, 33, 34,
             36, 37, 38, 39, 40};
    if (key == -1) {
        translated = K_RELEASE;
    } else if ((key < 0) || (key > 38)) {
        return -1;
    } else {
        translated = jtockey[key];
    }
    char logline[256];
    sprintf(logline, "Translated key %d", translated);
    android_logString(logline);
    xprocess_keycode(translated);
    return 0;
}
}