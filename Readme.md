Fault Tolerant Data Sorter based on RcB
--------------------------
A fault-tolerant system design exercise for implementing a data sorter that uses the [Recovery Blocks (RcB)](http://srel.ee.duke.edu/sw_ft/node6.html) technique for recovery. The application is written in Java, with the backup variant using a runtime linked C [JNI](http://docs.oracle.com/javase/7/docs/technotes/guides/jni/) library. The primary variant uses heap sort, while the backup uses insertion sort.

For simulating faults, both the variants encounter random memory access failures based on a probability. This is done via ```RandomlyFailingList``` in the primary variant, and by C wrappers in the backup variant. Additionally, failure to complete the computation in the specified time is also considered as a failure, and is regulated by the ```Watchdog```.

A utility integer ```DataGenerator``` application has been included. The design docs (UML class and sequence diagrams) are present in the ```docs``` directory.

This program was built as a project for [ECE 422 (Reliable & Secure Systems Design)](http://www.ece.ualberta.ca/communications/courses/CMPE420.html) at the University of Alberta.

###Compiling:

All the following commands build and put the targets in ```src/bin```. Tested on OS X and Linux. Requires Java/Javac 1.7, JNI, and gcc (with C99 support).

```sh
$ cd src

# To build everything
$ make

# To build just the DataGenerator
$ make datagen

# To build just the DataSorter
$ make sorter

# To delete the bin directory
$ make clean
```

###Running:
```sh
$ cd src/bin

# For loading the native backup variant library at runtime
$ export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:.

# DataGenerator:
$ java DataGenerator path/to/randomints.txt <number of random ints>

# DataSorter:
$ java DataSorter path/to/randomints.txt path/to/sortedoutput.txt \
  <primary failure probability> <backup failure probability> <time limit in ms>
```

