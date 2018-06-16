#!/usr/bin/env bash
latest="4.0.1"
echo Downloading latest version $latest of codacy reporter from sonatype
# download laterst assembly jar
mvn dependency:get dependency:copy \
   -DoutputDirectory=$HOME \
   -DoutputAbsoluteArtifactFilename=true \
   -Dmdep.stripVersion=true \
   -DrepoUrl=https://oss.sonatype.org/service/local/repositories/releases/content/ \
   -Dartifact=com.codacy:codacy-coverage-reporter:$latest:jar:assembly
