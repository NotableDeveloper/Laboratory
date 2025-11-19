# Laboratory Project

This project serves as a collection of various Java utilities, examples, and experimental code. It is structured into several modules, each addressing a specific area or demonstrating a particular concept.

## Project Structure Overview

The `src/main/java` directory contains the core source code, organized into the following main packages:

-   **`config`**: Handles application configuration loading and management from YAML files, utilizing environment variables for file paths.
-   **`crypto`**: Contains utilities related to cryptographic operations.
-   **`external_request`**: Provides components for making and handling external HTTP requests.
-   **`logback_filtering`**: Demonstrates custom Logback filters for advanced logging control.
-   **`threads`**: Includes examples and implementations related to multi-threading and concurrent programming.

## Building the Project

This project uses Gradle as its build automation tool.

To build the project, navigate to the root directory of the project and execute the following command:

```bash
./gradlew build
```

This command will compile the source code, run tests (if any), and package the application.

## Running Tests

To run the tests, use the following Gradle command:

```bash

./gradlew test
```

## Further Information

Refer to the `readme.md` files within individual sub-packages (e.g., `src/main/java/config/readme.md`) for more detailed information on specific modules.
