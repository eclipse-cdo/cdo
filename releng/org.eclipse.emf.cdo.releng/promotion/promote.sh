#!/usr/bin/env bash
set -e

promotionDir=~/promotion
jobsDir=$promotionDir/jobs

ant=/shared/common/apache-ant-1.7.1/bin/ant
JAVA_HOME=/shared/common/jdk-1.6.0_10

CriticalSection ()
{
	for jobName in `ls "$jobsDir"`
	do
		jobDir=$jobsDir/$jobName
		file=$jobDir/nextBuildNumber
		
	  if [ -f "$file" ]
	  then
	    lastBuildNumber=`cat "$file"`
	  else
	    lastBuildNumber=1
	  fi
	
	  nextBuildNumber=`cat "/shared/jobs/$jobName/nextBuildNumber"`
	  if [ "$nextBuildNumber" != "$lastBuildNumber" ]
	  then
	    echo "Checking $jobName for builds that need promotion..."
	    "$ant" -f "$promotionDir/bootstrap.ant" \
	    	"-DjobDir=$jobDir" \
	    	"-DjobName=$jobName" \
	    	"-DlastBuildNumber=$lastBuildNumber" \
	    	"-DnextBuildNumber=$nextBuildNumber"
	  else
	    echo "Nothing to promote for $jobName"
	  fi
	done
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

