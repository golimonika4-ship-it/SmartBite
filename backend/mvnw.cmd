@REM ----------------------------------------------------------------------------
@REM SmartBite Maven Execution Wrapper
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set "MAVEN_CMD=C:\Users\Durgarao\apache-maven-3.9.9\bin\mvn.cmd"
if exist "%MAVEN_CMD%" (
    call "%MAVEN_CMD%" %*
) else (
    call mvn %*
)
endlocal
