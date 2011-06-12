#!/usr/bin/env bash
set -e

promotionDir=~/promotion
jobsDir=$promotionDir/jobs

CriticalSection ()
{
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
	    echo "Checking whether $job builds need promotion..."
			#echo "$nextBuildNumber" > "$file"
	  else
	    echo "Nothing to promote for $job"
	  fi
	done
	
	sleep 30
}

lockFile=$promotionDir/promote.lock
if ( set -o noclobber; echo "$$" > "$lockFile" ) 2> /dev/null; 
then
  trap 'rm -f "$lockFile"; exit $?' INT TERM EXIT

	###############
	CriticalSection
	###############
		
  rm -f "$lockFile"
  trap - INT TERM EXIT
else
	echo "Promotion already being executed by process $(cat $lockFile)."
fi 

