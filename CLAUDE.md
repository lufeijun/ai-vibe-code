# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a simple Spring Boot "Hello World" demo application that demonstrates a basic REST API endpoint. The application returns "Hello World!" when accessed at `/hello`.

## Technology Stack

- **Spring Boot**: 4.0.5
- **Java**: 24 (Note: There may be compatibility issues with Java 24 and the current Maven compiler configuration)
- **Build Tool**: Maven 3.6.3
- **Web Framework**: Spring Web (for REST API)
- **Server**: Embedded Tomcat (via Spring Boot starter)

## Project Structure

```
src/main/java/com/example/demo/
├── SpringBootHelloWorldApplication.java  # Main Spring Boot application class
└── HelloWorldController.java            # REST controller with /hello endpoint

src/main/resources/
└── application.properties               # Configuration (server.port=8080)

pom.xml                                  # Maven configuration
```

## Key Components

1. **SpringBootHelloWorldApplication**: The entry point with `@SpringBootApplication` annotation. Uses `SpringApplication.run()` to bootstrap the application.

2. **HelloWorldController**: A `@RestController` with a single `@GetMapping("/hello")` endpoint that returns "Hello World!".

3. **application.properties**: Configures the application to run on port 8080 with name `springboot-hello-world`.

## Development Commands

### Build and Run
```bash
# Clean and compile the project
mvn clean compile

# Run the application
mvn spring-boot:run

# Package as executable JAR
mvn clean package
```

### Testing
```bash
# Run tests (if any exist)
mvn test

# Test the running application
curl http://localhost:8080/hello
```

### Development
```bash
# Check for dependency updates
mvn versions:display-dependency-updates

# Verify project structure
mvn validate
```

## Important Notes

1. **Java Version Compatibility**: The project is configured for Java 24, but there may be compilation issues with the current Maven compiler plugin configuration. The pom.xml includes custom compiler arguments (`--release 24`) to support Java 24.

2. **Spring Boot 4.0.5**: This is a recent version of Spring Boot. Ensure compatibility with Java 24 features if modifying the codebase.

3. **No Test Structure**: The project currently has no test files in the `src/test` directory. When adding new functionality, consider adding appropriate unit and integration tests.

4. **Simple Architecture**: This is a minimal Spring Boot application. For production use, consider adding error handling, logging, health checks, and additional configuration.

5. **Dependencies**: The project depends on `spring-boot-starter-web` for web functionality and `spring-boot-starter-test` for testing (though not currently used).

## Common Issues

- **Java 24 compilation errors**: If encountering "不支持发行版本 24" (unsupported release version 24), ensure Maven is running with Java 24. The project includes a `mvn24` script that sets `JAVA_HOME=/usr/lib/jvm/jdk-24.0.1-oracle-x64`. Use `./mvn24` instead of `mvn` for all Maven commands.

- **Maven using wrong Java version**: The system may have multiple Java installations. The project includes Maven toolchain configuration to select Java 24. If compilation fails, verify `~/.m2/toolchains.xml` contains the correct JDK path.

- **Port conflicts**: The application runs on port 8080 by default. Change `server.port` in `application.properties` if port 8080 is occupied.

- **Dependency resolution**: If Maven dependencies fail to download, check network connectivity or configure appropriate Maven repositories.

## Extension Points

When extending this application:
- Add new controllers in `com.example.demo` package or sub-packages
- Add service classes for business logic
- Add repository classes for data access
- Update `application.properties` for additional configuration
- Consider adding `@ComponentScan` if organizing code into multiple packages