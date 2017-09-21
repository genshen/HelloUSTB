//
// Created by gensh on 2017/9/19.
//
#ifndef HELLO_USTB_NATIVE_LIB_H
#define HELLO_USTB_NATIVE_LIB_H

#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_me_gensh_natives_MailAccount_getMailAccount(JNIEnv *env, jobject /* this */);

extern "C"
JNIEXPORT jstring JNICALL
Java_me_gensh_natives_MailAccount_getMailPassword(JNIEnv *env, jobject /* this */);

#endif   //HELLO_USTB_NATIVE_LIB_H
