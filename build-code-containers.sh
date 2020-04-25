#!/bin/sh

echo " Building the offline spark app app"
cd "code/winnow-spark"

./gradlew clean build

cd "../winnow"
./gradlew clean build
FILE_WAR='winnow-war/build/libs/winnow-war-0.0.1-SNAPSHOT.war'
FILE_JAR='winnow-ftp/build/libs/winnow-ftp-0.0.1-SNAPSHOT.jar'
INJESTION_JAR='winnow-ingest/build/libs/winnow-ingest-0.0.1-SNAPSHOT.jar'
ANALYZER_JAR='winnow-analyzer/build/libs/winnow-analyzer-0.0.1-SNAPSHOT.jar'

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

if test -f "$INJESTION_JAR"; then
    echo "$INJESTION_JAR exist"
else
    echo "Code Failed to build injestion jar file"
    exit 1
fi

if test -f "$ANALYZER_JAR"; then
    echo "$ANALYZER_JAR exist"
else
    echo "Code Failed to build analyzer jar file"
    exit 1
fi

cd "../.."
docker-compose build

echo "Building the winnow-ftp docker image "

docker build -f ./deploy/local/ftpapp/Dockerfile --label winnow-ftpapp -t gfn_ftpapp.cscie99.com code/winnow/winnow-ftp

echo "Building the winnow-ingester docker image "

docker build -f ./deploy/local/ingestionapp/Dockerfile --label winnow-ftpapp -t gfn_ingesterapp.cscie99.com code/winnow/winnow-ingest

echo "Building the winnow-analyzer docker image "

docker build -f ./deploy/local/analyzerapp/Dockerfile --label winnow-analyzerapp -t gfn_analyzerapp.cscie99.com code/winnow/winnow-analyzer
