#!/bin/sh

./gradlew :spotlessApply -Dorg.gradle.java.home=/c/Program\ Files/Java/jdk-11.0.1/ &&
./gradlew build -Dorg.gradle.java.home=/c/Program\ Files/Java/jdk-11.0.1/ &&
docker build -t 34.118.120.110:5000/horoscope-web-scraper . &&
docker push 34.118.120.110:5000/horoscope-web-scraper
