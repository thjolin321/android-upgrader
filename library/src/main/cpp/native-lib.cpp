//
// Created by Administrator on 2020/9/2.
//
#include <jni.h>
#include <android/log.h>

extern "C" {
extern int executePatch(int argc, char *argv[]);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_enjoy_dexdiff_BsPatchUtils_patch(JNIEnv *env, jclass clazz, jstring old_apk,
                                          jstring new_apk, jstring patch_file) {
    int args = 4;
    char *argv[args];
    argv[0] = "bspatch";

    argv[1] = (char *) (env->GetStringUTFChars(old_apk, 0));
    argv[2] = (char *) (env->GetStringUTFChars(new_apk, 0));
    argv[3] = (char *) (env->GetStringUTFChars(patch_file, 0));

    //此处executePathch()就是上面我们修改出的
    int result = executePatch(args, argv);

    env->ReleaseStringUTFChars(old_apk, argv[1]);
    env->ReleaseStringUTFChars(new_apk, argv[2]);
    env->ReleaseStringUTFChars(patch_file, argv[3]);
    __android_log_print(ANDROID_LOG_ERROR,"diff","==%s==%s==%s==%d",argv[1] ,argv[2] ,argv[3],result );
    return result;
}