***
# HVH - Hanoi Volunteer Hub - Backend


## Tech stack
* Build tool: maven >= 3.9.0
* Java: 21
* Framework: Spring boot 3.5.9
* DBMS: PostgreSql
* Authentication Server: Supabase

## Start application
To run the application, first you have to provide following environment variables:

| Environment Variable | What is it used for                                                                                                 |
|----------------------|---------------------------------------------------------------------------------------------------------------------|
| DB_URL               | The url of the datasource                                                                                           |
| EMAIL_PASSWORD       | The password of the application's email, this is used in Spring mail                                                |
| EMAIL_USERNAME       | The username of the application's email                                                                             |
| SP_API_SECRET_KEY    | The API secrete key of your Supabase project                                                                        |
| SP_BUCKET_NAME       | The name of your Supabase project bucket                                                                            |
| SP_JWT_ISSUER_URI    | The URI represent your Supabase Authentication Server                                                               |
| SP_URL               | The URL of your Supabase project                                                                                    | 


After having necessary environment variable, you could open terminal and run `mvn spring-boot:run`
or if you want to run the application with a specific profile `-Dspring-boot.run.profiles=prod`

## APIDocument
This application has already implemented Spring doc, Swagger API, to get file .yml of this application, please do following steps:
1. Run the application in your device
2. Open browser and access http://localhost:8080/swagger-ui.html
3. Access http://localhost:8080/v3/api-docs to see the JSON format
4. Access http://localhost:8080/v3/api-docs.yaml to download swagger yaml file
5. Save the file to your device

## Build application
`mvn clean package `
Or you want to build the project but skip testing
`mvn clean package -DskipTests `

If your computer doesn't have Maven, you can replace `mvn` with `./mvnw`, which reside in the source root.