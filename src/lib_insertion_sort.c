/**
 * A native JNI module for sorting an int list using insertion sort.
 */

#include "NativeInsertionSortOperation.h"
#include <jni.h>
#include <stdbool.h>
#include <stdint.h>
#include <time.h>
#include <stdlib.h>

void insertion_sort(jint *data, int length);
bool check_failure();
int get(jint *data, int index);
void set(jint *data, int index, jint value);
void swap(jint *data, int i, int j);
jint throwMemoryAccessFailureException(const char *message);


// Need to keep these globals for simulating the random memory failures.
// Another option would be to pass these values around to every single function call, but this is better.
// 'static' keyword and 'global' suffix should be good enough for safety in this small program.
static JNIEnv *env_global = NULL;
static uint64_t mem_access_count_global = 0;
static double failure_probability_global = 0.0;

// The public JNI insertion sort API.
JNIEXPORT void JNICALL Java_NativeInsertionSortOperation_insertionSort
  (JNIEnv *env, jobject object, jintArray data, jdouble failure_probability) {

    jsize length = (*env)->GetArrayLength(env, data);
    jint *data_c = (*env)->GetIntArrayElements(env, data, 0);

    env_global = env;
    failure_probability_global = failure_probability;

    srand(time(NULL));
    insertion_sort(data_c, length);

    (*env)->ReleaseIntArrayElements(env, data, data_c, 0);
}

void insertion_sort(jint *data, int length) {
    for (int end = length; end >= 0; --end) {
        for (int i = 0; i < end - 1; ++i) {
            int index = i;
            for (int j = index + 1; j > 0 && (get(data, j) < get(data, index)); --j) {
                swap(data, j, index--);
            }

        }
    }
}

// Checks if a random memory access failure should be produced now.
bool check_failure() {
    double hazard = mem_access_count_global * failure_probability_global;
    double random_event = (double)rand()/(double)RAND_MAX;
    return random_event >= 0.5 && random_event <= 0.5 + hazard;
}

jint get(jint *data, int index) {
    ++mem_access_count_global;
    if (check_failure()) {
        throwMemoryAccessFailureException("Random simulated failure event.");
    }

    return data[index];
}

void set(jint *data, int index, jint value) {
    ++mem_access_count_global;
    if (check_failure()) {
        throwMemoryAccessFailureException("Random simulated failure event.");
    }

    data[index] = value;
}

void swap(jint *data, int i, int j) {
    jint temp = get(data, j);
    set(data, j, get(data, i));
    set(data, i, temp);
}

jint throwMemoryAccessFailureException(const char *message) {
    static const char *className = "MemoryAccessFailureException";
    jclass exceptionClass = (*env_global)->FindClass(env_global, className);

    if (exceptionClass == NULL) {
        printf("DEVELOPER NOTE: MemoryAccessFailureException not found from JNI. Fix it!\n");
        exit(-1);
    }

    return (*env_global)->ThrowNew(env_global, exceptionClass, message);
}