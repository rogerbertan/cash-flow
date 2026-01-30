<a id="readme-top"></a>

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <h3 align="center">Cash Flow</h3>

  <p align="center">
    A AI-powered Spring Boot application implementing Clean Architecture principles for daily income and expense transaction tracker
    <br />
    <a href="#about-the-project">Explore the docs</a>
    &middot;
    <a href="https://github.com/rogerbertan/cash-flow/issues/new?labels=bug">Report Bug</a>
    &middot;
    <a href="https://github.com/rogerbertan/cash-flow/issues/new?labels=enhancement">Request Feature</a>
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
        <li><a href="#architecture">Architecture</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#api-endpoints">API Endpoints</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->

## About The Project

Cash Flow is an AI-powered RESTful API application designed to help users manage their personal finances by tracking income and expenses. The project serves as a practical implementation of Clean Architecture principles using modern Java and Spring Boot.

Key Features:
- Category management for organizing transactions (INCOME/EXPENSE)
- Transaction tracking with full CRUD operations
- AI-powered category suggestions based on transaction descriptions
- Paginated transaction history
- Business rule validation (type matching, positive amounts, referential integrity)
- Database migrations with Flyway
- Comprehensive test coverage

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Built With

- [![Java][Java-badge]][Java-url]
- [![Spring Boot][SpringBoot-badge]][SpringBoot-url]
- [![PostgreSQL][PostgreSQL-badge]][PostgreSQL-url]
- [![Maven][Maven-badge]][Maven-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Architecture

This application follows **Clean Architecture** principles with clear separation of concerns:

**Domain Layer** (`domain/`):
- Immutable entities (Category, Transaction)
- Use case classes with single responsibility
- Gateway interfaces for persistence abstraction
- Zero external dependencies

**Infrastructure Layer** (`infra/`):
- REST controllers for HTTP endpoints
- JPA entities and Spring Data repositories
- Gateway implementations with business validation
- DTOs and mappers for data transformation
- Global exception handling

**Key Patterns:**
- Use Cases: Simple classes with `execute()` method
- Gateway Pattern: Domain interfaces, infrastructure implementations
- Mapper Pattern: Separate mappers for create, update, and response operations
- DTO Pattern: Immutable Java records

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->

## Getting Started

Follow these steps to get a local copy up and running.

### Prerequisites

- Java 21 or higher
  ```sh
  java -version
  ```
- PostgreSQL 12 or higher
  ```sh
  psql --version
  ```
- Maven (Maven Wrapper included)

### Installation

1. Clone the repository
   ```sh
   git clone https://github.com/rogerbertan/cash-flow.git
   ```

2. Navigate to the project directory
   ```sh
   cd cash-flow
   ```

3. Create a PostgreSQL database
   ```sh
   createdb cash_flow
   ```

4. Configure database connection (optional - defaults to localhost)
   ```sh
   export DB_HOST=localhost
   export DB_NAME=cash_flow
   export DB_USER=postgres
   export DB_PASSWORD=postgres
   ```

5. Configure Gemini API key (required for AI category suggestions)
   ```sh
   export GEMINI_API_KEY=your_gemini_api_key_here
   ```
   Get your API key from [Google AI Studio](https://aistudio.google.com/app/apikey)

6. Build the project
   ```sh
   ./mvnw clean install
   ```

7. Run the application
   ```sh
   ./mvnw spring-boot:run
   ```

   Or with custom database configuration:
   ```sh
   DB_HOST=localhost DB_NAME=cash_flow DB_USER=postgres DB_PASSWORD=postgres ./mvnw spring-boot:run
   ```

The application will start on `http://localhost:8080`

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- USAGE EXAMPLES -->

## Usage

### Running Tests

Run all tests:
```sh
./mvnw test
```

Run specific test class:
```sh
./mvnw test -Dtest=CategoryControllerTest
```

Run specific test method:
```sh
./mvnw test -Dtest=CategoryControllerTest#testCreateCategory
```

### Building

Build without tests:
```sh
./mvnw clean package -DskipTests
```

Build with tests:
```sh
./mvnw clean install
```

### Database Migrations

Flyway migrations run automatically on startup. Migration files are located in `src/main/resources/db/migration/`

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- API ENDPOINTS -->

## API Endpoints

### Health Check
- `GET /api/health` - Application health status

### Categories
- `POST /api/categories` - Create a new category
- `GET /api/categories` - List all categories
- `GET /api/categories/{id}` - Get category by ID
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

### Transactions
- `POST /api/transactions` - Create a new transaction
- `GET /api/transactions` - List all transactions (paginated, default 20 per page)
- `GET /api/transactions/{id}` - Get transaction by ID
- `PUT /api/transactions/{id}` - Update transaction
- `DELETE /api/transactions/{id}` - Delete transaction
- `POST /api/transactions/suggest-category` - Get AI-powered category suggestion

### Example Request

Create a category:
`POST /api/categories`
```json
{
  "name": "Salary",
  "type": "INCOME"
}
```

Create a transaction:
`POST /api/transactions`
```json
{
  "description": "Monthly salary",
  "amount": 5000.00,
  "date": "2026-01-28",
  "categoryId": 1,
  "type": "INCOME"
}
```

Get AI category suggestion:
`POST /api/transactions/suggest-category`
```json
{
  "description": "Coffee at Starbucks",
  "type": "EXPENSE"
}
```

Response:
```json
{
  "suggestedCategory": {
    "id": 3,
    "name": "Food & Dining",
    "type": "EXPENSE"
  },
  "confidence": "HIGH",
  "message": "Based on the description, this appears to be a food/dining expense."
}
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTRIBUTING -->

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are greatly appreciated.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- MARKDOWN LINKS & IMAGES -->
[Java-badge]: https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://www.oracle.com/java/
[SpringBoot-badge]: https://img.shields.io/badge/Spring%20Boot-4.0.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white
[SpringBoot-url]: https://spring.io/projects/spring-boot
[PostgreSQL-badge]: https://img.shields.io/badge/PostgreSQL-12+-336791?style=for-the-badge&logo=postgresql&logoColor=white
[PostgreSQL-url]: https://www.postgresql.org/
[Maven-badge]: https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white
[Maven-url]: https://maven.apache.org/