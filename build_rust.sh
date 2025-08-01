#!/bin/bash
cd basketball_ml
cargo ndk -t arm64-v8a -t armeabi-v7a -t x86_64 -t x86 -o ../app/src/main/jniLibs build --release
