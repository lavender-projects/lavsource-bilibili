APP_DATA_DIR=$(dirname "$0")/..

export PREFIX=$APP_DATA_DIR/termux
export LD_LIBRARY_PATH=$PREFIX/lib

cd $APP_DATA_DIR/lavsource-server
linker64 $PREFIX/opt/openjdk/bin/java -jar \
  -Djava.io.tmpdir=../cache/javaTemp \
  -Dserver.port=$1 \
  -Dspring.profiles.active=prod \
  ./lavsource-server.jar