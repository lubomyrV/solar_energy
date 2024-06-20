# Solar energy monitor

Tracker for an MPPT solar charge controller with RS232 interface. Recives data via RS232/Serial-to-USB converter or via REST API and saves it to a database.

## Running Locally

1. Update config 
```bash
Rename `copy_application.properties` to `application.properties` and update variables
```
2. Build the project

```bash
mvn package
```

3. Run jar package

```bash
java -jar solarenergy.jar 
```

## Software requirements

Java 11+, Apache Maven 3.8.7+

## Tech Stack

**Server:** Java, SQlite, Spring Boot

**Client:** JavaScript, Google Chart

![example](https://github.com/lubomyrV/solar_energy/blob/master/solar_example.png)