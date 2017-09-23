//
// Created by gensh on 2017/9/19.
//
#ifndef HELLOUSTB_NATIVE_LIB_H
#define HELLOUSTB_NATIVE_LIB_H

#include <jni.h>
#include <string>

// implements your own cpp file yourself,just returns your email account or password string

extern "C"
JNIEXPORT jstring JNICALL
Java_me_gensh_natives_MailAccount_getMailAccount(JNIEnv *env, jobject /* this */);

extern "C"
JNIEXPORT jstring JNICALL
Java_me_gensh_natives_MailAccount_getMailPassword(JNIEnv *env, jobject /* this */);

#endif   //HELLOUSTB_NATIVE_LIB_H
