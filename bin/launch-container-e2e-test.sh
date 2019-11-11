#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

export ACTIVE_SPRING_PROFILE=e2e-test
export SERVER_PORT=8080
export DOCKER_IMAGE_VERSION=$(cd $DIR/../ && mvn -Dexec.executable='echo' -Dexec.args='${project.version}' --non-recursive exec:exec -q)

docker-compose up -d