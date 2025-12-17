#! /bin/sh

docker run --rm -it --security-opt seccomp=unconfined \
-v ~/.m2:/root/.m2 \
-v ${PWD}:/home/builder \
magnoabreu/java-builder:java24 mvn clean package

docker rmi magnoabreu/hotwheels:1.0
docker build --tag=magnoabreu/hotwheels:1.0 --rm=true .

docker stop hotwheels && docker rm hotwheels

docker run --name hotwheels --network=interna --hostname=hotwheels \
--restart=always \
-d -p 36099:8080 \
-e URL_CONNECTION=***********
-e USER_NAME=********
-e PASSWORD=*********
-e ADMIN_PASSWORD=******** \
-v /hotwheels:/carrinhos \
magnoabreu/hotwheels:1.0

