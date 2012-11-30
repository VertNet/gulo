#!/bin/sh

# Generates Java code from the vn.thrift DSL. Depends on the Apache Thrift compiler.

rm -rf ../src/jvm/gen-java
rm -rf ../src/jvm/gulo/schema/*
thrift -o "../src/jvm" -r --gen java:hashcode gulo.thrift
mv ../src/jvm/gen-java/gulo/schema ../src/jvm/gulo
rm -rf ../src/jvm/gen-java
