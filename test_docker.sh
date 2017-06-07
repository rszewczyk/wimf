#!/bin/bash

# script for running a build isolated from my workstation

set -ex

docker run -it --rm -e "CI=true" -w /project -v $(pwd):/project -v /var/run/docker.sock:/var/run/docker.sock java:8 ./gradlew build dockerBuildImage
