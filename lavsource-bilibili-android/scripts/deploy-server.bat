chcp 65001
cd /d %~dp0..\lavender-reader-server

set TARGET_DIR=..\app\src\main\assets\inner-data-server

rmdir /s /q .\build\libs
del /s /q %TARGET_DIR%\inner-data-server.jar
call gradlew bootJar
copy .\build\libs\lavender-reader-server.jar %TARGET_DIR%\inner-data-server.jar