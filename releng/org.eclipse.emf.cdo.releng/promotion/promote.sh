#!/bin/bash

for job in `ls jobs`
do
	if [ -f "jobs/$job/nextBuildNumber" ]
	then
		lastBuildNumber=`cat "jobs/$job/nextBuildNumber"`
	else
		lastBuildNumber=0
	fi
	
	nextBuildNumber=`cat "/shared/jobs/$job/nextBuildNumber"`
	if [ "$nextBuildNumber" != "$lastBuildNumber" ]
	then
		echo "Promoting a build: $nextBuildNumber"
	else
		echo "Nothing to promote."
	fi
done
