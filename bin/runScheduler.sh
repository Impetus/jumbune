now=$(date +"%T")

##Fetch jumbune home directory (JUMBUNE_HOME)
currentJobDir=`dirname $0`

## Location of all desired runtimes
JUMBUNE_HOME=<JUMBUNE.HOME>
JAVA_HOME=<JAVA.HOME>

## Where to find Java Command
JAVA=$JAVA_HOME/bin/java

# add resources to CLASSPATH
resources="${JUMBUNE_HOME}/resources"
CLASSPATH=${CLASSPATH}:$resources;

# add jumbune libs to CLASSPATH
for f in $JUMBUNE_HOME/lib/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done

#add dataValidation module to classpath
for f in $JUMBUNE_HOME/modules/jumbune-tuning*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done
for f in $JUMBUNE_HOME/modules/jumbune-datavalidation*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done

# add debugger module  to classpath
for f in $JUMBUNE_HOME/modules/jumbune-debug*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done

# add execution jar to classpath
for f in $JUMBUNE_HOME/modules/jumbune-exec*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done


#SchedulerExecutorService is class which is to be executed for scheduling
CLASS='org.jumbune.execution.service.DataQualityTimelineShellExecutor'

#Set log4j properties
CONFIGURATION=-Dlog4j.configuration=log4j2.xml


##The class takes in input two parameters 1) current Job directory 2)user's jumbune home
COMMAND="$JAVA $CONFIGURATION -cp $CLASSPATH $CLASS $currentJobDir $JUMBUNE_HOME"

exec $COMMAND
