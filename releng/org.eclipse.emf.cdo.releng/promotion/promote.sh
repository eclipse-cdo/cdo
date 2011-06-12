#!/usr/bin/env bash

jobsDir=~/promotion/jobs
mkdir "$jobsDir"

for job in `ls "$jobsDir"`
do
	jobDir=$jobsDir/$job
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
