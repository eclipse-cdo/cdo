#!/usr/bin/env bash
set -e

promotionWorkDir=~/promotion

HUDSON_JOBS_DIR=/shared/jobs
JAVA_HOME=/shared/common/jdk-1.6.0_10
ANT=/shared/common/apache-ant-1.7.1/bin/ant

CriticalSection ()
{
	localJobsDir=$promotionWorkDir/jobs
	for jobName in `ls "$localJobsDir"`
	do
		jobDir=$localJobsDir/$jobName
		file=$jobDir/nextBuildNumber
		
	  if [ -f "$file" ]
	  then
	    lastBuildNumber=`cat "$file"`
	  else
	    lastBuildNumber=1
	  fi
	
	  nextBuildNumber=`cat "$HUDSON_JOBS_DIR/$jobName/nextBuildNumber"`
	  if [ "$nextBuildNumber" != "$lastBuildNumber" ]
	  then
	    echo "Checking $jobName for builds that need promotion..."
	    "$ANT" -f "$promotionWorkDir/bootstrap.ant" \
	    	"-DhudsonJobsDir=$HUDSON_JOBS_DIR" \
	    	"-DpromotionWorkDir=$promotionWorkDir" \
	    	"-DjobName=$jobName" \
	    	"-DlastBuildNumber=$lastBuildNumber" \
	    	"-DnextBuildNumber=$nextBuildNumber"
	  else
	    echo "Nothing to promote for $jobName"
	  fi
	done
}

lockFile=$promotionWorkDir/promote.lock
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

