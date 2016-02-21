#include <jni.h>
#include "NativeInsertionSortOperation.h"
#include <stdio.h>

void insertion_sort(jint *data, int length, double failure_probability);

JNIEXPORT void JNICALL Java_NativeInsertionSortOperation_insertionSort
  (JNIEnv *env, jobject object, jintArray data, jdouble failure_probability) {

    jsize length = (*env)->GetArrayLength(env, data);
    jint *data_c = (*env)->GetIntArrayElements(env, data, 0);
    insertion_sort(data_c, length, failure_probability);
    (*env)->ReleaseIntArrayElements(env, data, data_c, 0);
}

void insertion_sort(jint *data, int length, double failure_probability) {
//    for (int i = 0; i < length; ++i) {
//        printf("%d ", data[i]);
//    }
//
//    printf("\nEnd C\n");
}