#!/bin/bash

# Gradle wrapper script

if [ -f gradlew.bat ]; then
    cmd /c gradlew.bat "$@"
else
    echo "Gradle wrapper not found. Please regenerate using 'gradle wrapper' locally."
    exit 1
fi
