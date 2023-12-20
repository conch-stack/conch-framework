#!/bin/bash
shopt -s expand_aliases
currentVersion=`sed -n '/<packaging>/,/<properties>/p' pom.xml | grep version | cut -d '>' -f2 | cut -d '<' -f1`
echo "Current version is $currentVersion"

echo "Please enter a new version"
read newVersion
echo "The updated version is $newVersion !"

if [ `uname` == "Darwin" ] ;then
 	echo "This is OS X"
 	alias sed='sed -i ""'
else
 	echo "This is Linux"
 	alias sed='sed -i'
fi

echo "Change version in root pom.xml ===>"
sed "/<packaging>/,/<properties>/ s/<version>$currentVersion<\/version>/<version>$newVersion<\/version>/" pom.xml
sed "/<properties>/,/<\/properties>/ s/<common-framework.version>$currentVersion<\/common-framework.version>/<common-framework.version>$newVersion<\/common-framework.version>/" pom.xml

echo "Change version in subproject pom ===>"
for  filename in `find . -name "pom.xml" -mindepth 2`;
do
  if [ $filename == './common-framework-model/pom.xml' ];
  then
    echo "skip common-framework-model/pom.xml"
  elif [ $filename == './common-framework-biz/pom.xml' ];
  then
    echo "skip common-framework-biz/pom.xml"
  else
    echo "Deal with $filename"
    sed "/<parent>/,/<\/parent>/ s/<version>$currentVersion<\/version>/<version>$newVersion<\/version>/" $filename
  fi
done

echo ${java.home}