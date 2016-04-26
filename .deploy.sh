#!/bin/bash

set -e

if [[ "$TRAVIS_PULL_REQUEST" = "true" ]]; then
    echo "Skipping deployment for pull request"
    exit
fi

if [[ -n ${TRAVIS_TAG} ]]; then
    echo "on a tag -> set pom.xml <version> to $TRAVIS_TAG"
    mvn --settings maven_deploy_settings.xml org.codehaus.mojo:versions-maven-plugin:2.2:set -DnewVersion=${TRAVIS_TAG} 1>/dev/null 2>/dev/null
else 
		if [[ ${TRAVIS_BRANCH} != 'master' ]]; then
		    echo "Skipping deployment for branch \"${TRAVIS_BRANCH}\""
		    exit
		fi
fi

mvn -B verify deploy --settings maven_deploy_settings.xml -DskipTests=true -Dfindbugs.skip=true -Djacoco.skip=true