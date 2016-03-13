# Copyright 2016 Udey Rishi
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

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

.PHONY: directories clean

default: all

all: directories datagen sorter

directories:
	$(MKDIR_P) $(OUT_DIR)

datagen: directories $(DATAGEN_SRC)
	$(JAVA_COMPILER) $(JAVA_FLAGS) $(DATAGEN_SRC)

sorter: directories sorter_native sorter_java

sorter_java: $(SORTER_SRC_JAVA)
	$(JAVA_COMPILER) $(JAVA_FLAGS) $(SORTER_SRC_JAVA)

sorter_native: $(SORTER_SRC_NATIVE)
	$(CC) $(SORTER_NATIVE_FLAGS) -o $(OUT_DIR)/$(NATIVE_TARGET) $(SORTER_SRC_NATIVE)

clean:
	$(RM_DIR) $(OUT_DIR)