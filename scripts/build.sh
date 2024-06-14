#!/bin/bash

set -e

cd $(dirname "$0")/..
PROJECT_PATH="$(pwd)"

# 打包android/web模块
chmod +x ./android/scripts/*.sh
./android/scripts/deploy-web.sh

# Gradle打包
chmod +x gradlew
./gradlew build

# 重命名APK
cd ./android/build/outputs/apk/release
mv app-release-unsigned.apk "lavsource-bilibili-$1.apk"