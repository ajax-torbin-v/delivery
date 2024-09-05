#!/bin/bash

while [[ "$#" -gt 0 ]]; do
    case "$1" in
        --gen-env | -ge)
            if [ -f ".env" ]; then
                awk -F'=' '/^[^#]/ { print $1"=#" }' .env > .env_sample
            fi
            ;;

        --help | -h)
            echo "Pls refer to https://tinyurl.com/5br5nhmy"
            exit 1
            ;;
        *)
            echo "Unknown flag: $1"
            exit 1
            ;;
    esac
    shift
done

./gradlew -q check > output.log 2>&1

if grep -q "BUILD FAILED" output.log; then
    echo "Check task failed!"
    cat output.log
    rm output.log
    exit
fi

./gradlew assemble

docker-compose up
