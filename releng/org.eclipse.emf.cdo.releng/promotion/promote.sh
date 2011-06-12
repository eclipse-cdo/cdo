#!/usr/bin/env bash
set -e

jobsDir=~/promotion/jobs

for job in `ls "$jobsDir"`
do
	jobDir=$jobsDir/$job
	file=$jobDir/nextBuildNumber
	
  if [ -f "$file" ]
  then
    lastBuildNumber=`cat "$file"`
  else
    lastBuildNumber=1
  fi

  nextBuildNumber=`cat "/shared/jobs/$job/nextBuildNumber"`
  if [ "$nextBuildNumber" != "$lastBuildNumber" ]
  then
    echo "Checking whether $job is promotable..."
  	echo "$nextBuildNumber" > "$file"
  else
    echo "Nothing to promote for $job"
  fi
  
done
