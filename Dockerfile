#
# Dockerfile - Jumbune
#
FROM     ubuntu:16.04
MAINTAINER Jumbune-Dev <dev@collaborate.jumbune.org>
RUN echo "nameserver 8.8.8.8" | tee /etc/resolv.conf > /dev/null
RUN rm /bin/sh && ln -s /bin/bash /bin/sh

# Upgradation and installation of required packages.
RUN apt-get update && apt-get install -y curl supervisor openssh-server net-tools iputils-ping nano zip maven git

# Installing JDK and adding JAVA HOME
RUN apt-get update
RUN apt-get install -y software-properties-common
RUN add-apt-repository ppa:openjdk-r/ppa
RUN apt-get update
RUN apt-get install --fix-missing -y -f openjdk-8-jdk
#RUN update-java-alternatives -s java-1.7.0-openjdk-amd64
#ENV JAVA_HOME /usr/lib/jvm/java-7-openjdk-amd64




# Setup JAVA_HOME, this is useful for docker commandline
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
RUN export JAVA_HOME



RUN echo "export JAVA_HOME=$JAVA_HOME" >> /etc/profile
RUN echo export PATH="$PATH:$JAVA_HOME/bin" >> /etc/profile
RUN echo $JAVA_HOME
#Fetch Apache Hadoop and untar
ENV SRC_DIR /opt
ENV HADOOP_URL https://archive.apache.org/dist/hadoop/core/
ENV HADOOP_VERSION hadoop-2.7.1
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
RUN sed -i '/^export JAVA_HOME/ s:.*:export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/:' $HADOOP_PREFIX/etc/hadoop/hadoop-env.sh
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
RUN java -version
# Name node foramt
RUN $HADOOP_PREFIX/bin/hdfs namenode -format

# Supervisor
RUN mkdir -p /var/log/supervisor
ADD docker-conf/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# SSH
RUN mkdir /var/run/sshd
RUN sed -i 's/without-password/yes/g' /etc/ssh/sshd_config
RUN sed -i 's/UsePAM yes/UsePAM no/g' /etc/ssh/sshd_config
RUN sed -i 's/prohibit-password/yes/' /etc/ssh/sshd_config
RUN echo 'SSHD: ALL' >> /etc/hosts.allow
RUN echo "NoHostAuthenticationForLocalhost yes" >>~/.ssh/config
RUN echo "StrictHostKeyChecking no" >>~/.ssh/config

#Adding the Jumbune specific ENV variables to /etc/profile
ENV JUMBUNE_HOME /root/jumbune
ENV AGENT_HOME /root/agent
RUN mkdir $JUMBUNE_HOME
RUN mkdir $AGENT_HOME
RUN echo '#jumbune' >> /etc/profile \
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
#ENV JUMBUNE_VERSION 1.6
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
 && mvn -Dhttps.protocols=TLSv1.2 install -P yarn
###
# Uncomment Above lines (if you wish to build from the latest snapshot codebase and not the latest Jumbune release)
###
ADD docker-conf/deploynRun.sh /root/deploynRun.sh
ADD docker-conf/sampleJson.json /root/sampleJson.json
RUN chmod +x /root/deploynRun.sh
ADD docker-conf/cluster-configuration.properties /root/agent/cluster-configuration.properties

#Setting the username and password
RUN echo 'root:hadoop' |chpasswd
EXPOSE 22 9080 50070 8088
RUN /usr/sbin/sshd

# Daemon
CMD ["/usr/bin/supervisord"]
