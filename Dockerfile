FROM hseeberger/scala-sbt:latest

COPY target/scala-2.11/basic-project-assembly-0.1.0-SNAPSHOT.jar /root/basic-project-0.1.0.jar
COPY pingpong.sh /root

ENTRYPOINT ["/root/pingpong.sh"]
CMD ["--help"]

WORKDIR "/root"