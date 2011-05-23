#!/bin/sh
# Fast run specific python tests (without building dependencies)
# 
# Run this in the build/ directory.
# Sample: ./run-py-test.sh test_basic,test_env

cmake .. -DNOSE_ARGS:String="--tests=$@" && make python-test/fast
