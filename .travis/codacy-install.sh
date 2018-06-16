#!/usr/bin/env bash
# get latest version of codacy reporter from sonatype
latest=$(curl "https://oss.sonatype.org/service/local/repositories/releases/content/com/codacy/codacy-coverage-reporter/maven-metadata.xml" | xpath -e "/metadata/versioning/release/text()")

echo Downloading latest version $latest of codacy reporter from sonatype
# download laterst assembly jar
mvn dependency:get dependency:copy \
   -DoutputDirectory=$HOME \
   -DoutputAbsoluteArtifactFilename=true \
   -Dmdep.stripVersion=true \
   -DrepoUrl=https://oss.sonatype.org/service/local/repositories/releases/content/ \
   -Dartifact=com.codacy:codacy-coverage-reporter:$latest:jar:assembly
