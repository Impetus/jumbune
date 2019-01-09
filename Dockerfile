#

# Dockerfile - Jumbune
#
FROM     ubuntu:12.04
MAINTAINER Jumbune-Dev <dev@collaborate.jumbune.org>

RUN rm /bin/sh && ln -s /bin/bash /bin/sh

# Upgradation and installation of required packages.
RUN apt-get update && apt-get install -y curl supervisor openssh-server net-tools iputils-ping nano zip maven git

# Installing JDK and adding JAVA HOME
ENV JDK_URL http://download.oracle.com/otn-pub/java/jdk
ENV JDK_VER 7u79-b15
ENV JDK_VER2 jdk-7u79
ENV JAVA_HOME /usr/local/jdk
ENV PATH $PATH:$JAVA_HOME/bin
RUN cd $SRC_DIR && curl -LO "$JDK_URL/$JDK_VER/$JDK_VER2-linux-x64.tar.gz" -H 'Cookie: oraclelicense=accept-securebackup-cookie' \
 && tar xzf $JDK_VER2-linux-x64.tar.gz && mv jdk1* $JAVA_HOME && rm -f $JDK_VER2-linux-x64.tar.gz \
 && echo '' >> /etc/profile \
 && echo '# JDK' >> /etc/profile \
 && echo "export JAVA_HOME=$JAVA_HOME" >> /etc/profile \
 && echo 'export PATH="$PATH:$JAVA_HOME/bin"' >> /etc/profile \
 && echo '' >> /etc/profile \
 && update-alternatives --install "/usr/bin/java" "java" "/usr/local/jdk/bin/java" 5000


#Fetch Apache Hadoop and untar
ENV SRC_DIR /opt
ENV HADOOP_URL https://archive.apache.org/dist/hadoop/core/
ENV HADOOP_VERSION hadoop-2.4.1
RUN cd $SRC_DIR &&  wget --no-check-certificate "$HADOOP_URL/$HADOOP_VERSION/$HADOOP_VERSION.tar.gz" \
 && tar xzf $HADOOP_VERSION.tar.gz ; rm -f $HADOOP_VERSION.tar.gz

# Adding the required env variables to /etc/profile
ENV HADOOP_PREFIX $SRC_DIR/$HADOOP_VERSION
ENV HADOOP_HOME $HADOOP_PREFIX
ENV PATH $PATH:$HADOOP_PREFIX/bin:$HADOOP_PREFIX/sbin
ENV HADOOP_MAPRED_HOME $HADOOP_PREFIX
ENV HADOOP_COMMON_HOME $HADOOP_PREFIX
ENV HADOOP_HDFS_HOME $HADOOP_PREFIX
ENV YARN_HOME $HADOOP_PREFIX
RUN echo '# Hadoop' >> /etc/profile \
 && echo "export HADOOP_PREFIX=$HADOOP_PREFIX" >> /etc/profile \
 && echo 'export HADOOP_HOME=$HADOOP_PREFIX' >> /etc/profile \
 && echo 'export PATH=$PATH:$HADOOP_PREFIX/bin:$HADOOP_PREFIX/sbin' >> /etc/profile \
 && echo 'export HADOOP_MAPRED_HOME=$HADOOP_PREFIX' >> /etc/profile \
 && echo 'export HADOOP_COMMON_HOME=$HADOOP_PREFIX' >> /etc/profile \
 && echo 'export HADOOP_HDFS_HOME=$HADOOP_PREFIX' >> /etc/profile \
 && echo 'export YARN_HOME=$HADOOP_PREFIX' >> /etc/profile 

# Adding Hadoop configurations 
ADD docker-conf/core-site.xml $HADOOP_PREFIX/etc/hadoop/core-site.xml
ADD docker-conf/hdfs-site.xml $HADOOP_PREFIX/etc/hadoop/hdfs-site.xml
ADD docker-conf/yarn-site.xml $HADOOP_PREFIX/etc/hadoop/yarn-site.xml
ADD docker-conf/mapred-site.xml $HADOOP_PREFIX/etc/hadoop/mapred-site.xml

#House keeping
RUN rm /opt/$HADOOP_VERSION/bin/hadoop.cmd

# Adding JAVA_HOME to hadoop-env.sh and exposing JMX ports for monitoring
RUN sed -i '/^export JAVA_HOME/ s:.*:export JAVA_HOME=/usr/local/jdk:' $HADOOP_PREFIX/etc/hadoop/hadoop-env.sh
RUN echo 'export HADOOP_NAMENODE_OPTS="-Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=5677"' >> $HADOOP_PREFIX/etc/hadoop/hadoop-env.sh
RUN echo 'export HADOOP_DATANODE_OPTS="-Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=5679"' >> $HADOOP_PREFIX/etc/hadoop/hadoop-env.sh
RUN echo 'export YARN_NODEMANAGER_OPTS="-Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=5678"' >> $HADOOP_PREFIX/etc/hadoop/yarn-env.sh
RUN echo 'export YARN_RESOURCEMANAGER_OPTS="-Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=5680"'>> $HADOOP_PREFIX/etc/hadoop/yarn-env.sh

# Native
# https://gist.github.com/ruo91/7154697#comment-936487
RUN echo 'export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_PREFIX/lib/native' >> $HADOOP_PREFIX/etc/hadoop/hadoop-env.sh \
 && echo 'export HADOOP_OPTS=-Djava.library.path=$HADOOP_PREFIX/lib' >> $HADOOP_PREFIX/etc/hadoop/hadoop-env.sh \
 && echo 'export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_PREFIX/lib/native' >> $HADOOP_PREFIX/etc/hadoop/yarn-env.sh \
 && echo 'export HADOOP_OPTS=-Djava.library.path=$HADOOP_PREFIX/lib' >> $HADOOP_PREFIX/etc/hadoop/yarn-env.sh

# SSH keygen
RUN cd /root && ssh-keygen -t dsa -P '' -f "/root/.ssh/id_dsa" \
 && cat /root/.ssh/id_dsa.pub >> /root/.ssh/authorized_keys && chmod 644 /root/.ssh/authorized_keys

# Name node foramt
RUN $HADOOP_PREFIX/bin/hdfs namenode -format

# Supervisor
RUN mkdir -p /var/log/supervisor
ADD docker-conf/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# SSH
RUN mkdir /var/run/sshd
RUN sed -i 's/without-password/yes/g' /etc/ssh/sshd_config
RUN sed -i 's/UsePAM yes/UsePAM no/g' /etc/ssh/sshd_config
RUN echo 'SSHD: ALL' >> /etc/hosts.allow
RUN echo "NoHostAuthenticationForLocalhost yes" >>~/.ssh/config
RUN echo "StrictHostKeyChecking no" >>~/.ssh/config

#Adding the Jumbune specific ENV variables to /etc/profile
ENV JUMBUNE_HOME /root/jumbune
ENV AGENT_HOME /root/agent
RUN mkdir $JUMBUNE_HOME
RUN mkdir $AGENT_HOME
RUN echo '#Jumbune' >> /etc/profile \
 && echo "export JUMBUNE_HOME=$JUMBUNE_HOME" >> /etc/profile \
 && echo "export AGENT_HOME=$AGENT_HOME" >> /etc/profile

#########################################################
#							#
# Fetching latest Jumbune release (stable) build	#
#							#
#########################################################

###
# UnComment below lines (if you wish to fetch latest Jumbune release rather than building from latest snapshot codebase)
###

#ENV JUMBUNE_VERSION 1.5.1
#RUN mkdir -p /jumbune_code/distribution/target/ \
# && wget -O /jumbune_code/distribution/target/jumbune-dist-$JUMBUNE_VERSION-bin.jar  http://www.jumbune.org/jar/current/yarn/jumbune-dist-$JUMBUNE_VERSION-bin.jar

###
# Uncomment Above lines  (if you wish to fetch latest Jumbune release rather than building from latest snapshot codebase)
###

#########################################################
#                                                       #
# Fetching latest Jumbune SNAPSHOT codebase             #
#                                                       #
#########################################################

###
# UnComment below lines (if you wish to build from the latest snapshot codebase and not the latest Jumbune release)
###

RUN git clone https://github.com/Impetus/jumbune.git jumbune_code/ -b master \
 && cd jumbune_code/ \
 && export MAVEN_OPTS="-Xmx512m -XX:MaxPermSize=350m" \
 && mvn clean install -P yarn 

###
# Uncomment Above lines (if you wish to build from the latest snapshot codebase and not the latest Jumbune release)
###


ADD docker-conf/deploynRun.sh /root/deploynRun.sh
ADD docker-conf/sampleJson.json /root/sampleJson.json
RUN chmod +x /root/deploynRun.sh
ADD docker-conf/cluster-configuration.properties /root/agent/cluster-configuration.properties

#Setting the username and password
RUN echo 'root:hadoop' |chpasswd
EXPOSE 22 8080:8080 50070 8088

# Daemon
CMD ["/usr/bin/supervisord"]

