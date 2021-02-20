#!/usr/bin/env bash

if ! git diff-index --quiet HEAD --; then
    echo "There are some local changes"
    exit 1;
fi


echo "What the release version? (ex: 0.0.6)"
read RELEASE_VERSION
echo "Releasing [$RELEASE_VERSION]"


git checkout develop
git pull --all


mvn versions:set -DnewVersion="$RELEASE_VERSION"
git commit -a -m "Set release version $RELEASE_VERSION"
git push


git checkout master
git pull
git merge --no-ff develop -m "Release $RELEASE_VERSION"
git tag "$RELEASE_VERSION"
git push --tags


git checkout develop
git pull
git merge --no-ff master -m "Final merge $RELEASE_VERSION -> develop"
git push


echo "What the next version? (ex: 0.0.7-SNAPSHOT)"
read NEXT_VERSION
echo "Next version is [$NEXT_VERSION]"

mvn versions:set -DnewVersion="$NEXT_VERSION"
git commit -a -m "Set next version $NEXT_VERSION"
git push
