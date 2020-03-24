#!/bin/sh
cd "code/winnow"
./gradlew clean build
FILE_WAR='winnow-war/build/libs/winnow-war-0.0.1-SNAPSHOT.war'
FILE_JAR='winnow-ftp/build/libs/winnow-ftp-0.0.1-SNAPSHOT.jar'

if test -f "$FILE_WAR"; then
    echo "$FILE_WAR exist"
else
    echo "Code Failed to build war file"
    exit 1
fi

if test -f "$FILE_JAR"; then
    echo "$FILE_JAR exist"
else
    echo "Code Failed to build jar file"
    exit 1
fi

cd "../.."
docker-compose build

echo "Building the winnow-ftp docker image "

docker build -f ./deploy/local/ftpapp/Dockerfile --label winnow-ftpapp -t gfn_ftpapp.cscie99.com .
