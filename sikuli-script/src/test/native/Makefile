
NATIVE_TEST_PATH=../../target/test
NATIVE_TEST_SUITE=TestFinder.h
NATIVE_TEST_RUNNER=TestFinderRunner.cpp
NATIVE_TEST_RUNNER_BIN=$(NATIVE_TEST_PATH)/$(NATIVE_TEST_RUNNER:%.cpp=%)

SRC_PATH=../../main/native
OBJS:=$(shell ls $(SRC_PATH)/*.o)
OBJS:=$(OBJS:%screenmatch.o=)

include $(SRC_PATH)/Makefile.common

CXXFLAGS+=-I/opt/cxxtest -I$(SRC_PATH) -lcv -lcxcore -lcvaux -lhighgui
LDFLAGS:=$(LDFLAGS:-dynamiclib=) -L/usr/local/libx

default: run-test

$(NATIVE_TEST_PATH):

$(NATIVE_TEST_RUNNER): $(NATIVE_TEST_SUITE)
	cxxtestgen.pl --error-printer -o $@ $^

$(NATIVE_TEST_RUNNER_BIN): $(NATIVE_TEST_RUNNER) $(OBJS)
	mkdir -p $(NATIVE_TEST_PATH)
	$(CXX) $(CXXFLAGS) $(LDFLAGS) -o $@ $^

run-test: $(NATIVE_TEST_RUNNER_BIN)
	time $(NATIVE_TEST_RUNNER_BIN)

screenmatch: $(OBJS) screenmatch.o
	$(CXX) $(CXXFLAGS) $(LDFLAGS) -o $@ $^
