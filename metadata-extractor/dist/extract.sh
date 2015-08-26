#!/bin/ksh
############################################################################
# Startup Script for the Metadata Extraction Tool v3.0 - Command Line Tool
############################################################################

# If the METAHOME directory is not yet set, try to guess it.
if [ -z "$METAHOME" ] ; then
  METAHOME=$(cd -P -- "$(dirname -- "$0")" && pwd -P)
fi

# A check to make sure we guessed okay.
if [ ! -f "$METAHOME/config.xml" ] ; then
  echo Failed to guess home directory.
  exit
fi



# Start the tool.
. $METAHOME/setenv.sh
$JAVA_HOME/bin/java -Xmx128m -Dmetahome=$METAHOME -Djava.system.class.loader=nz.govt.natlib.meta.config.Loader -cp ${META_CLASSPATH} nz.govt.natlib.meta.ui.CmdLine $*