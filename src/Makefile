UNAME_S := $(shell uname -s)
ifeq ($(UNAME_S),Linux)
    OS_NAME=linux
    NATIVE_TARGET=libinsertion_sort.so
endif
ifeq ($(UNAME_S),Darwin)
    OS_NAME=darwin
    NATIVE_TARGET=libinsertion_sort.dylib
endif

export LD_LIBRARY_PATH := $(LD_LIBRARY_PATH):.

MKDIR_P = mkdir -p
OUT_DIR=bin

RM_DIR=$(RM) -r

CC=gcc
JAVA_COMPILER=javac
JAVA_FLAGS=-Xlint:unchecked -d $(OUT_DIR)

SORTER_SRC_JAVA=DataSorter.java AcceptanceTest.java HeapSortOperation.java \
				NativeInsertionSortOperation.java RecoveryBlocksExecutor.java \
				Operation.java Variant.java MemoryAccessFailureException.java \
				OperationThread.java Watchdog.java FileIOUtils.java \
				RandomlyFailingList.java
SORTER_SRC_NATIVE=lib_insertion_sort.c
SORTER_NATIVE_FLAGS=-std=c99 -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/$(OS_NAME) -shared -fpic

DATAGEN_SRC=DataGenerator.java FileIOUtils.java

.PHONY: directories

default: all

all: directories datagen sorter

directories: $(OUT_DIR)

$(OUT_DIR):
	$(MKDIR_P) $(OUT_DIR)

datagen: $(DATAGEN_SRC)
	$(JAVA_COMPILER) $(JAVA_FLAGS) $(DATAGEN_SRC)

sorter: sorter_native sorter_java

sorter_java: $(SORTER_SRC_JAVA)
	$(JAVA_COMPILER) $(JAVA_FLAGS) $(SORTER_SRC_JAVA)

sorter_native: $(SORTER_SRC_NATIVE)
	$(CC) $(SORTER_NATIVE_FLAGS) -o $(OUT_DIR)/$(NATIVE_TARGET) $(SORTER_SRC_NATIVE)

clean:
	$(RM_DIR) $(OUT_DIR)