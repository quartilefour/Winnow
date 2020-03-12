#!/bin/sh
cd "code/winnow"
./gradlew clean build
FILE=build/libs/winnow-0.0.1-SNAPSHOT.war

if test -f "$FILE"; then
    echo "$FILE exist"
else
    echo "Code Failed to build"
    exit 1
fi

cd "../.."
docker-compose build