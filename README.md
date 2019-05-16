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

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)

