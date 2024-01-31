#!/bin/sh

set -e

cd $(dirname "$0")/../../..
PROJECT_PATH="$(pwd)"

TERMUX_ENV_PATH="$PROJECT_PATH/src/main/assets/termux"
LAVSOURCE_SERVER_PATH="$PROJECT_PATH/src/main/assets/lavsource-server"

TERMUX_ENV_VERSION='jdk17-1.0.0'
TERMUX_ENV_FILES=(
  "termux-env-openjdk17-arm64.zip"
  "termux-env-openjdk17-x64.zip"
)

for TERMUX_ENV_FILE in "${TERMUX_ENV_FILES[@]}"
do
  if [ ! -f "$TERMUX_ENV_PATH/$TERMUX_ENV_FILE" ]; then
    echo "Download $TERMUX_ENV_FILE ..."
    cd "$TERMUX_ENV_PATH"
    curl -L -O "https://github.com/kosaka-bun/termux-environments/releases/download/$TERMUX_ENV_VERSION/$TERMUX_ENV_FILE"
  fi
done

rm -f "$LAVSOURCE_SERVER_PATH/lavsource-server.jar"

cd "$PROJECT_PATH/../server"
rm -f ./build/libs/*.jar
chmod +x ../gradlew
../gradlew :server:build

cd ./build/libs
mv lavsource-bilibili-server.jar lavsource-server.jar
cp -f lavsource-server.jar "$LAVSOURCE_SERVER_PATH"
mv lavsource-server.jar lavsource-bilibili-server.jar