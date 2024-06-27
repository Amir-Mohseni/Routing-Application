@echo off
REM Check if Maven is installed
mvn -version >nul 2>&1
if errorlevel 1 (
    echo Maven is not installed. Installing Maven...
    REM Download Maven binary
    bitsadmin.exe /transfer "Maven" https://dlcdn.apache.org/maven/maven-3/3.8.4/binaries/apache-maven-3.8.4-bin.zip "%cd%\apache-maven-3.8.4-bin.zip"
    REM Extract Maven
    powershell -command "Expand-Archive -Path .\apache-maven-3.8.4-bin.zip -DestinationPath ."
    set "MAVEN_HOME=%cd%\apache-maven-3.8.4"
    set "PATH=%MAVEN_HOME%\bin;%PATH%"
    echo Maven installed successfully.
)

REM Clean the project
echo Cleaning project...
mvn clean

REM Install dependencies
echo Installing dependencies...
mvn install

REM Build the project
echo Building project...
mvn package

echo Done.
