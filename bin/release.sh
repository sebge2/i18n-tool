#!/usr/bin/env bash

set -e

echo "What the release version? (ex: 0.0.6)"
#read RELEASE_VERSION
RELEASE_VERSION="0.0.10"
echo "Releasing [$RELEASE_VERSION]"

echo "What the next version? (ex: 0.0.7)"
#read NEXT_VERSION
NEXT_VERSION="0.0.11"
echo "Next version is [$NEXT_VERSION]"

mvn -B release:clean release:prepare -DreleaseVersion="$RELEASE_VERSION" -DdevelopmentVersion="$NEXT_VERSION"


#git checkout develop
#git pull --all
#
#
#mvn versions:set -DnewVersion="$RELEASE_VERSION"
#cd front/src/main/web/
#npm version "$RELEASE_VERSION" --allow-same-version
#cd ../../../../
#git commit -a -m "Set release version $RELEASE_VERSION"
#git push
#
#
#git checkout master
#git pull
#git merge --no-ff develop -m "Release $RELEASE_VERSION"
#git tag "$RELEASE_VERSION"
#git push --tags
#
#
#git checkout develop
#git pull
#git merge --no-ff master -m "Final merge $RELEASE_VERSION -> develop"
#git push
#
#
#echo "What the next version? (ex: 0.0.7)"
#read NEXT_VERSION
#echo "Next version is [$NEXT_VERSION]"
#
#mvn versions:set -DnewVersion="$NEXT_VERSION-SNAPSHOT"
#cd front/src/main/web/
#npm version "$NEXT_VERSION" --allow-same-version
#cd ../../../../
#git commit -a -m "Set next version $NEXT_VERSION"
#git push
