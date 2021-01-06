# homework-service project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `homework-service-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/homework-service-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/homework-service-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

# RESTEasy JAX-RS

<p>A Hello World RESTEasy resource</p>

Guide: https://quarkus.io/guides/rest-json

# Metrics

### Prometheus
```shell script
docker run --rm --name prometheus -p 9090:9090 --network host \
-v /path/to/prometheus.yml:/etc/prometheus/prometheus.yml:Z prom/prometheus:v2.14.0 \
--config.file=/etc/prometheus/prometheus.yml
```

### Grafana
```shell script
docker run --rm -p 3000:3000 --network host grafana/grafana:6.4.4
```
Import dashboard from `src/main/resources/grafana-dashboard.json`

# Health check

In You can manually check health in dev mode by  
`http :8083/health/liveness`  
`http :8083/health/readiness`

