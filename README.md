# Getting Started
You can run the project in its development mode by using the following commands:

```
mvn spring-boot:run
```

This will expose the service to http://localhost:8080

# Basic Usage (curl)

```
curl 'http://localhost:8080/lunch'
```
This will return a response with a list of recipes that can be made with all valid ingredients.

```
curl 'http://localhost:8080/lunch?date=<yyyy-MM-dd>'
```
Where in you replace the <yyyy-MM-dd> with a date of your choice (e.g. 2019-05-25), this will return a response with a list of recipes that can be made by the use by date of ingredients and sorted by what may expire first.

# Testing the application and coverage reporting
Simply run the following command to test the application, this will run both unit and integration tests.
This also makes use of jacoco to present a testing report. The report is going to be found in target/site/jacoco/index.html.
```
mvn test jacoco:report
```
# Using Docker
Prior to running docker, make sure that you package the project first by running the command below:
```
mvn package
```
This will test the project and create the jar, if there are any errors then it needs to be fixed prior to packaging.

Installing docker can be found on its official website (https://www.docker.com/get-started), and will not be covered here.

Run the command below to run the container:
```
docker-compose up
```

You can terminate the service by 'ctrl + c'.

# Accessing the API when running through Docker
The Docker container exposes port 8080, however we have configured docker-compose.yml to bind that port to port 80. This will make the container reachable through http://localhost instead of localhost:8080. Refer to the docker-compose.yml to see more configurations. This yml file makes it easy to re-build and re-run Docker containers without typing long commands.

As an example, you should be able access the service through curl by doing this instead of the one above:
```
curl 'http://localhost/lunch'
```

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)

