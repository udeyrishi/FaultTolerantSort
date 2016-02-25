ECE 422 Project 1: Fault Tolerant DataSorter based on RcB
--------------------------
Submitted by: Udey Rishi

Please see the ```src``` directory for the source code for both the DataGenerator and the DataSorter. They're in the same directory as they share some code, so it was convenient. Please see the ```docs``` directory for the design docs.

* Shared files: ```src/FileIOUtils.java```
* DataGenerator files: ```src/DataGenerator.java```
* DataSorter files: Everything else

See ```src/Makefile``` for details.

###To Build:

All the following commands build and put the targets in ```src/bin```. No need to call ```$ mkdir -p src/bin``` first, it is done automatically.

```sh
$ cd src

# To build everything
$ make

# To build just the DataGenerator
$ make datagen

# To build just the DataSorter
$ make sorter

# To delete the bin/*.class and bin/*.so
# (or *.dylib on OS X) files:
$ make clean
```

###To Run:
```sh
$ cd src/bin

# DataGenerator:
$ java DataGenerator path/to/output.txt <number of random ints>

# DataSorter:
$ java DataSorter path/to/randomints.txt path/to/sortedoutput.txt <primary failure probability> <backup failure probability> <time limit in ms>
```

If DataSorter throws ```java.lang.UnsatisfiedLinkError``` exception, then run ```export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:.```, and try again.

