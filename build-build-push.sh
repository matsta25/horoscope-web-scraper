#!/bin/sh

./gradlew build -Dorg.gradle.java.home=/c/Program\ Files/Java/jdk-11.0.1/
docker build -t matsta25.tk:5000/efairy-backend .
docker push matsta25.tk:5000/efairy-backend
