#!/bin/sh

# Author:Li Yanpeng

package()
{
   echo "----> start packaging $SERVICE-$PKG_ENV..."
   
   PKG_SERVICE=$1
   PKG_ENV=$2
   
   if [ ! -d "$GID_HOME" ]; then
	mkdir -p $GIT_HOME	
   fi
   cd $GIT_HOME
   rm -fr $PKG_SERVICE
   git clone $GIT_URL$PKG_SERVICE.git
   cd $GIT_HOME/$PKG_SERVICE
   
   
   #echo "$PKG_SERVICE" | tr "-" "\n" > tmpPrefix
	
   #while read word; do
   #	prefix=$prefix${word:0:1}
   #done < tmpPrefix
	
   #rm -fr tmpPrefix

   #echo "use branch ${prefix}_develop..."
   #git checkout ${prefix}_develop
   
   git checkout develop
   
   
   ls $GIT_HOME/$PKG_SERVICE/ |  while read file_name; do
	 if [ -d $file_name ]
	 then
	   if [ -d $GIT_HOME/$PKG_SERVICE/$file_name/src/main/filters/ ]
	   then
		   echo "copy filters to $GIT_HOME/$PKG_SERVICE/$file_name/src/main/filters/"
		   cp $APP_PATH/properties/$PKG_SERVICE/filter/filter-*.properties $GIT_HOME/$PKG_SERVICE/$file_name/src/main/filters/		   
	   fi
	 fi
   done
   
   
   cd $GIT_HOME/$PKG_SERVICE/
   mvn clean package -Dmaven.test.skip=true -DskipTests=true -P$PKG_ENV

   mkdir -p $GIT_HOME/$PKG_SERVICE/distribution/$name-$version
   tar -xzvf $GIT_HOME/$PKG_SERVICE/distribution/$package-$version.tar.gz -C $GIT_HOME/$PKG_SERVICE/distribution/$name-$version
   
   cp -r $APP_PATH/dcmd-bin $GIT_HOME/$PKG_SERVICE/distribution/$name-$version/bin
   
   mkdir $GIT_HOME/$PKG_SERVICE/distribution/$name-$version/conf
   cp $APP_PATH/properties/$PKG_SERVICE/server.properties $GIT_HOME/$PKG_SERVICE/distribution/$name-$version/conf/server.properties
   
   timestamp=`date +%Y%m%d%H%M%S`
   export pkgDcmdName=$name'_'$timestamp
   
   cd $GIT_HOME/$PKG_SERVICE/distribution/$name-$version
   tar -czvf "$GIT_HOME/$PKG_SERVICE/distribution/$pkgDcmdName.tar.gz" .
   
   export pkgDcmdPath=$GIT_HOME/$PKG_SERVICE/distribution/$pkgDcmdName.tar.gz
   
   echo "----> end packaging $SERVICE-$PKG_ENV..."
}


deploy()
{
   echo "----> start sending $pkgDcmdPath to dcmd..."
  
   rsync -avr $pkgDcmdPath root@10.77.130.21::release/$pkgDcmdName.tar.gz
   echo "----> done!"
}

PrintUsage()
{
cat << EndOfUsageMessage

		Usage: $0 -pd -s SERVICE -e ENV
	
		Descriptions:
		-p : package the service.
		-d : deploy binary jar to DCMD.
		-s SERVICE : specify which service to build.
		-e ENV : specify which env to build.

		Example: $0 -pd -s athena-example -e test
	
EndOfUsageMessage
}

InvalidCommandSyntaxExit()
{
        echo "Invalid command\n`PrintUsage`"
        exit;
}

if [ $# -eq 0 ]
then
	echo "`PrintUsage`"
	exit 1
fi



BASE_PATH=`cd "$(dirname "$0")"; pwd`
APP_PATH=`cd "$(dirname "$BASE_PATH")"; pwd`
ENV="test"
SERVICE="athena-example"
ENV=
SERVICE=

while getopts "pde:s:" arg
do
        case $arg in
             p)
                ISPACKAGE='ON' 
                ;;
             d)
             	ISDEPLOY='ON'
                ;;
             e)
                ENV=$OPTARG
                ;;
             s)
                SERVICE=$OPTARG
                ;;
             ?) 
            	echo "`InvalidCommandSyntaxExit`"
        		exit 1
        		;;
        esac
done

source $APP_PATH/conf/deploy.properties
source $APP_PATH/properties/$SERVICE/server.properties

if [[ $ISPACKAGE = 'ON' ]]; then
  package $SERVICE $ENV
fi

if [[ $ISDEPLOY = 'ON' ]]; then
  deploy
fi
