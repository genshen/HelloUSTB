//
// Created by gensh on 2017/9/19.
//
#include "db.h"

JNIEXPORT jstring JNICALL
Java_me_gensh_natives_DatabaseEncrypted_getDBEncryptedPassword(JNIEnv *env, jobject /* this */) {
    std::string str = "3awd534aaab22a0";
    return env->NewStringUTF(str.c_str());
}