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

CC=gcc
JAVA_COMPILER=javac
JAVA_SRC=*.java
NATIVE_SRC=*.c
NATIVE_FLAGS=-I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/$(OS_NAME) -shared -fpic

default: $(SRC)
	$(JAVA_COMPILER) $(JAVA_SRC)
	$(CC) $(NATIVE_FLAGS) -o $(NATIVE_TARGET) $(NATIVE_SRC)

clean:
	$(RM) *.class $(NATIVE_TARGET)