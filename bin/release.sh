#!/usr/bin/env bash

set -e

echo "What the release version? (ex: 0.0.6)"
read RELEASE_VERSION
echo "Releasing [$RELEASE_VERSION]"

echo "What the next version? (ex: 0.0.7)"
read NEXT_VERSION
echo "Next version is [$NEXT_VERSION]"

git checkout develop
git pull --all

git checkout master
git pull
git merge --no-ff develop -m "Merge before release $RELEASE_VERSION"

mvn -B release:clean release:prepare release:perform -DreleaseVersion="$RELEASE_VERSION" -DdevelopmentVersion="$NEXT_VERSION"

git checkout develop
git pull
git merge --no-ff master -m "Final merge $RELEASE_VERSION -> develop"
git push