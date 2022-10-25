# Roach Trading

A standalone spring boot app simulating an online trading
system using Spring Boot and CockroachDB.

# Project Setup

## Prerequisites

- JDK8+ with 1.8 language level (OpenJDK compatible)
- Maven 3.1+ (optional)

## Database Setup

Create the databases:

    cockroach sql --url postgresql://localhost:26257?sslmode=disable -e "CREATE database trading; CREATE database product"

## Building and running from codebase

### Building

The application is built with [Maven 3.1+](https://maven.apache.org/download.cgi).
Tanuki's Maven wrapper is included (mvnw). All 3rd party dependencies are available 
in public Maven repos.

Clone the project:

    git clone git@github.com:kai-niemi/roach-trading.git

To build and deploy to your local Maven repo, execute:

    cd roach-trading
    ./mvnw clean install

### Run Trading Server

Run the trading server:

    java -jar trading-server/target/trading-server.jar

Alternatively, to start in the background:

    nohup java -jar trading-server/target/trading-server.jar > trading-server-stdout.log 2>&1 &

Open API index using default port:

    open localhost:8090/api

To use custom parameters (see application.yml for settings):

    java -jar trading-server/target/trading-server.jar \ 
    --spring.profiles.active=verbose \ 
    --spring.datasource.url=jdbc:postgresql://192.168.1.2:26257/trading?sslmode=disable    

### Run Product Server

Run the product server:

    java -jar product-server/target/product-server.jar

Alternatively, to start in the background:

    nohup java -jar product-server/target/product-server.jar > product-server-stdout.log 2>&1 &

Open API index using default port:

    open localhost:8089/api

To use custom parameters (see application.yml for settings):

    java -jar product-server/target/product-server.jar \ 
    --spring.profiles.active=verbose \ 
    --spring.datasource.url=jdbc:postgresql://192.168.1.2:26257/product?sslmode=disable    

### Run Product Client

Run the product shell client:

    java -jar trading-client/target/trading-client.jar

Then to create orders:
    
    connect
    place-orders

