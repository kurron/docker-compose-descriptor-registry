# start with a Ubuntu base box with Java 8 pre-installed
FROM kurron/docker-oracle-jdk-8:1.8.0_77

# set the author/maintainer
MAINTAINER Ron Kurr "kurr@kurron.org"

LABEL org.kurron.built-on="${generated}" org.kurron.branch="${branch}" org.kurron.version="${version}"

COPY ${projectName}.jar /opt/example/${projectName}.jar

# HTTP port
EXPOSE ${port}

# JMX port
EXPOSE 1999

# launch scripts that watchdog process expects
RUN mkdir /etc/service/hid-server
ADD service.sh /etc/service/hid-server/run
RUN chmod a+x /etc/service/hid-server/run

# start the init service
ENTRYPOINT ["/sbin/my_init"]