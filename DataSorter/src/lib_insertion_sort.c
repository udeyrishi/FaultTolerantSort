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

void swap(jint *data, int i, int j) {
    jint temp = data[j];
    data[j] = data[i];
    data[i] = temp;
}

void insertion_sort(jint *data, int length, double failure_probability) {
    for (int end = length; end >= 0; --end) {
        for (int i = 0; i < end - 1; ++i) {
            int index = i;
            for (int j = index + 1; j > 0 && (data[j] < data[index]); --j) {
                swap(data, j, index--);
            }
        }
    }
}