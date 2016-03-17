#include <jni.h>

JNIEXPORT jstring JNICALL Java_com_holo_encrypt_MailAccount_getMailAccount
        (JNIEnv *env, jclass obj){
    return (*env)->NewStringUTF(env, "1690512717@qq.com");
}

JNIEXPORT jstring JNICALL Java_com_holo_encrypt_MailAccount_getMailPassword
        (JNIEnv *env, jclass obj){
    return (*env)->NewStringUTF(env, "cgslovecmj");//**********
}
