#!/usr/bin/env bash
set -e

promotionDir=~/promotion
jobsDir=$promotionDir/jobs
lockFile=$promotionDir/promote.lock

if ( set -o noclobber; echo "$$" > "$lockFile" ) 2> /dev/null; 
then
  trap 'rm -f "$lockFile"; exit $?' INT TERM EXIT

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
			#echo "$nextBuildNumber" > "$file"
	  else
	    echo "Nothing to promote for $job"
	  fi
	done
   
  sleep 20000
  
  rm -f "$lockFile"
  trap - INT TERM EXIT
fi 

