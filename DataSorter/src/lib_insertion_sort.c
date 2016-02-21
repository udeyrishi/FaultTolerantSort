#include "NativeInsertionSortOperation.h"
#include <jni.h>
#include <stdbool.h>
#include <stdint.h>
#include <time.h>
#include <stdlib.h>

void insertion_sort(jint *data, int length, double failure_probability);
bool check_failure(uint64_t count, double failure_probability);
int get(jint *data, int index, double failure_probability);
void swap(jint *data, int i, int j);
void fail();

JNIEXPORT void JNICALL Java_NativeInsertionSortOperation_insertionSort
  (JNIEnv *env, jobject object, jintArray data, jdouble failure_probability) {

    jsize length = (*env)->GetArrayLength(env, data);
    jint *data_c = (*env)->GetIntArrayElements(env, data, 0);
    srand(time(NULL));
    insertion_sort(data_c, length, failure_probability);
    (*env)->ReleaseIntArrayElements(env, data, data_c, 0);
}

void insertion_sort(jint *data, int length, double failure_probability) {
    for (int end = length; end >= 0; --end) {
        for (int i = 0; i < end - 1; ++i) {

            int index = i;
            for (int j = index + 1;
                 j > 0 && (get(data, j, failure_probability) < get(data, index, failure_probability));
                 --j) {
                swap(data, j, index--);
            }

        }
    }
}

bool check_failure(uint64_t count, double failure_probability) {
    double hazard = count * failure_probability;
    double random_event = (double)rand()/(double)RAND_MAX;
    return random_event >= 0.5 && random_event <= 0.5 + hazard;
}

int get(jint *data, int index, double failure_probability) {
    static uint64_t count = 0;

    if (check_failure(++count, failure_probability)) {
        fail();
    }

    return data[index];
}

void swap(jint *data, int i, int j) {
    jint temp = data[j];
    data[j] = data[i];
    data[i] = temp;
}

void fail() {
    exit(-1);
}