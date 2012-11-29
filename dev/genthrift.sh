#!/bin/sh

# Generates Java code from the vn.thrift DSL. Depends on the Apache Thrift compiler.

rm -rf ../src/jvm/gen-java
rm -rf ../src/jvm/forma/schema
rm -rf ../src/jvm/vn/schema/*
thrift -o "../src/jvm" -r --gen java:hashcode vn.thrift
mv ../src/jvm/gen-java/vn/schema ../src/jvm/vn
rm -rf ../src/jvm/gen-java
