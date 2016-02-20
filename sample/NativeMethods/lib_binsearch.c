#include <stdio.h>
#include <jni.h>
#include "MyBinarySearch.h"

/* C source for the native binary search mehtod */
/* Scott Dick, Summer 2004 */

int bs(jint *, jint, int, int, int);
/* Recursive binary search function */

JNIEXPORT jint JNICALL Java_MyBinarySearch_binsearch
(JNIEnv *env, jobject object, jintArray buf, jint target){
  jsize len;
  jint *myCopy;
  int right, left, idx;
  jint result;
  jboolean *is_copy = 0;

  len = (*env)->GetArrayLength(env, buf);
  myCopy = (jint *) (*env)->GetIntArrayElements(env, buf, is_copy);
  if (myCopy == NULL){
    printf("Cannot obtain array from JVM\n");
    exit(0);
  }

  right = (int) len - 1;
  left = 0;
  idx = left / 2;

  result = bs(myCopy, target, left, idx, right);

  return result;

}

/*---------------------------------------------------------------*/

int bs(jint *buf, jint target, int left , int idx, int right){
/* Recursive binary search function */

/* Parent: JNI entry point */

/* Subroutines: bs() */

  if (right == left){
    /* Single-element list */
    return right;
  }

  if (buf[idx] == target){
    /* Right on the split point */
    return idx;
  }

  if (buf[idx] > target){
    /* Left sublist */
    right = idx;
    idx = left + ((right - left) / 2);
    return bs(buf, target, left, idx, right);
  }

  if (buf[idx] < target){
    /* Right sublist */
    left = idx;
    idx = idx + ((right-idx)/ 2);
    return bs(buf, target, left, idx, right);
  }
}



  

