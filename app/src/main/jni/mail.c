#include <com_holo_encrypt_MailAccount.h>
#include <jni.h>

JNIEXPORT jstring JNICALL Java_com_holo_encrypt_MailAccount_getMailAccount
        (JNIEnv *env, jclass obj){
    return (*env)->NewStringUTF(env, "********");
}

JNIEXPORT jstring JNICALL Java_com_holo_encrypt_MailAccount_getMailPassword
        (JNIEnv *env, jclass obj){
    return (*env)->NewStringUTF(env, "**********");
}
