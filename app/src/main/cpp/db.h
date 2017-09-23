//
// Created by gensh on 2017/9/19.
//

#ifndef HELLOUSTB_DB_H
#define HELLOUSTB_DB_H

#include <jni.h>
#include <string>

//implements your own cpp file yourself,just return password of string format.
extern "C"
JNIEXPORT jstring JNICALL
Java_me_gensh_natives_DatabaseEncrypted_getDBEncryptedPassword(JNIEnv *env, jobject /* this */);

#endif //HELLOUSTB_DB_H
