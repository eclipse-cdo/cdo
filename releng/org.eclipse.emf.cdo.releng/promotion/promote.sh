#!/usr/bin/env bash

workDir=~/promotion/jobs
mkdir "$workDir"

for job in `ls jobs`
do
	jobDir=$workDir/$job
	mkdir "$jobDir"
	
	file=$jobDir/nextBuildNumber
  if [ -f "$file" ]
  then
    lastBuildNumber=`cat "$file"`
  else
    lastBuildNumber=0
  fi

  nextBuildNumber=`cat "/shared/jobs/$job/nextBuildNumber"`
  if [ "$nextBuildNumber" != "$lastBuildNumber" ]
  then
    echo "Checking whether $job is promotable..."
  else
    echo "Nothing to promote for $job"
  fi
  
  echo "$nextBuildNumber" > "$file"
done
