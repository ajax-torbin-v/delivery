#!/bin/bash

declare -A flag_map

while [[ $# -gt 0 ]]; do
    key="$1"
    shift

    if [[ $key == -* ]]; then
        value_list=()
        while [[ $# -gt 0 && $1 != -* ]]; do
            value_list+=("$1")
            shift
        done
        flag_map["$key"]="${value_list[*]}"
    fi
done

help () {
  cat << EOF
  usage: init-docker [-h | --help][-ge | --gen-env]
  Global options
  -ge, --gen--env : Generates .env_sample file based on .env
  -h,  --help : Print help message
  Report bugs to: https://github.com/ajax-torbin-v
  You can find more about this script https://tinyurl.com/5br5nhmy
EOF
}

for key in "${!flag_map[@]}"
do
  case $key in
  -h | --help)
    help
    exit 0
    ;;
  -ge | --gen-env)

    awk -F'=' '/^[^#]/ { print $1"=#" }' .env > .env_sample
    exit 0
    ;;
  *)
    echo "Unknown argument ${flag_map[key]}"
    exit 1
    ;;
  esac
done

./gradlew clean build

docker-compose up -d --build --force-recreate --remove-orphans
