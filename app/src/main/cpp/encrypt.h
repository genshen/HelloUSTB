//
// Created by gensh on 2017/9/23.
//

#ifndef HELLOUSTB_ENCRYPT_H
#define HELLOUSTB_ENCRYPT_H

#include <jni.h>

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_me_gensh_natives_Encrypt_nativeEncrypt(JNIEnv *env, jclass type, jbyteArray plainTextData_,
                                            jbyteArray iv_);

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_me_gensh_natives_Encrypt_nativeDecrypt(JNIEnv *env, jclass type, jbyteArray cipherTextData_,
                                            jbyteArray iv_);

#endif //HELLOUSTB_ENCRYPT_H
