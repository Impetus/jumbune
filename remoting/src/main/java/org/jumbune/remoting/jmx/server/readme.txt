The parent directory contains jumbune-jmx-agent.jar which is sent to all the nodes in the cluster.
Besides, it also contains the codebase of jumbune-jmx-agent.jar. If something in jar needs to be changed,
the java files(org.jumbune.remoting.jmx.server.JumbuneJMXServer.java, org.jumbune.remoting.jmx.common.JMXStats.java, org.jumbune.remoting.jmx.common.JMXStatsUtil.java)
should be modified and should be packaged into jar named  jumbune-jmx-agent.jar and older one should be replaced.


Command to run jar(JMX Agent): java -cp $JAVA_HOME/lib/tools.jar:jumbune-jmx-agent.jar org.jumbune.remoting.jmx.server.JumbuneJMXServer