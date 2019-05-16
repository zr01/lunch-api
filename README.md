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
curl 'http://localhost:8080/lunch?use-by=true&date=<yyyy-MM-dd>'
```
Where in you replace the <yyyy-MM-dd> with a date of your choice (e.g. 2019-05-25), this will return a response with a list of recipes that can be made by the use by date of ingredients.

```
curl 'http://localhost:8080/lunch?use-by=true&best-before=true&date=<yyyy-MM-dd>'
```
Where in you replace the <yyyy-MM-dd> with a date of your choice (e.g. 2019-05-25), this will return a response with a list of recipes that can be made by the use by date of ingredients and sorted by what may expire first.

# Using Docker
Prior to running docker, make sure that you package the project first by running the command below:
```
mvn package
```
This will test the project and create the jar, if there are any errors then it needs to be fixed prior to packaging.

Installing docker can be found on its official website (), and will not be covered here.

Run the command below to run the container:
```
docker-compose up
```

You can terminate the service by 'ctrl + c'.

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)

