# MAVILLE Application

This project was originally developed as part of a university Software Engineering course.
This repository is a public showcase version of my work.

## About the Application

MAVILLE was developed for the STPM to make city maintenance faster and more transparent.

## Features

- Residents can report or check the enumStatus of urban problems.
- Service providers can view, propose and select projects to resolve issues.
- Centralised dashboard for all users.
- Real-time notifications and updates.

## Requirements

- Java 21 or higher.
- Maven.

## Install & Run

These are the instructions to compile and run the app in command line.
You can use whatever is available from your favorite IDE.

### Using the pre-built JAR

```sh
java -jar out/artifacts/maVille_jar/maVille.jar
```

### Building from source

1. Clone the repository using git (see Code button above), move to the created folder
2. Build an executable: `mvn package`
3. Run: `java -jar target/maVille-1.0-SNAPSHOT.jar`

You can also run tests: `mvn test`

## Project structure

- `README.md` -> This file ;)
- `pom.xml` -> Maven project information
- `.gitignore` -> Specifies which files are ignored by git 
- `src/main/java` -> Code for the javalin backend
- `src/main/resources` -> Code for the html frontend
- `src/test`  -> Test suites
- `rapport/` -> Project documentation


