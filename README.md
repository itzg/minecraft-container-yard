# Minecraft Container Yard

[![Join the chat at https://gitter.im/itzg/minecraft-container-yard](https://badges.gitter.im/itzg/minecraft-container-yard.svg)](https://gitter.im/itzg/minecraft-container-yard?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Provides a web based "Minecraft Server as a Service" (MCaaS?) to deploy Minecraft server containers on any 
Docker Swarm cluster or standalone Engine instance.

## Building

This is a Spring Boot based application, so it can be run at development time using:

    ./mvnw -Drun.arguments=...see below... spring-boot:run

or packaged using

    ./mvnw package
    
On Windows use the `mvnw.bat` instead or on any platform bring your own copy of Maven 3.x.

## Building with Docker

If using a TLS authenticated Swarm/Daemon, copy the certificates directory into this project's space as a directory called `certs`. If using an insecure connection, just create an empty `certs` directory.

With that run

    docker build -t mccy .

## Accessing

By default, the application serves up at port 8080, such as

http://localhost:8080

and the user is "mccy". The password is output in the startup logs or you can pin it down by passing `--security.user.password`.

## Running

There are several options available to configure, but the only required one is `mccy.docker-host-uri`. When running the packaged jar,
it is passed as

    java -jar target/mccy-swarm-*.jar --mccy.docker-host-uri=...
    
where you provide a `http:` for insecure Docker access or `https:` for secure/authenticated.

In the case of authenticated Docker connections, you will need to point to a directory with the appropriate 
certificates using `mccy.docker-cert-path`. For example, the directory you download from your [Carina Cluster](https://getcarina.com/)
is exactly what you'll need.

## Running with Docker

After following the steps above in **Building with Docker**, start a MCCY container using

    docker run -d --name mccy -p 8080:8080 mccy --mccy.docker-host-uri=...
    
where the last part is the same command-line options described above.
