@echo off

set DIR=%~dp0

if exist "%DIR%\gradle\bin\gradle" (
  set GRADLE_CMD="%DIR%\gradle\bin\gradle" %*
) else (
  set GRADLE_CMD="%DIR%\gradle-wrapper.jar" %*
)

java -jar %GRADLE_CMD%