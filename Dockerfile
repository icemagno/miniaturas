FROM openjdk:22-jdk
LABEL maintainer="Carlos M. Abreu <magno.mabreu@gmail.com>"

COPY ./target/hw-0.1.jar /opt/lib/
ENTRYPOINT ["java"]
ENV LANG=pt_BR.utf8 
CMD ["-jar", "/opt/lib/hw-0.1.jar"]