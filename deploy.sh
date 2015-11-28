command=`git log -n 1 | grep Author`
if [ "$command" != "Author: Travis-CI <noreply@travis-ci.org>" ] 
then 
	mvn -B clean release:clean release:prepare release:perform --settings travis-settings.xml 
fi

