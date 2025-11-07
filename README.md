
# Demo Project for Spring Boot with Google Generative AI

This project is a demonstration of a Spring Boot application that integrates with Google's Generative AI services through the Gemini API. It includes a complete set of CRUD APIs for managing users, clients, shops, products, carts, and orders.

## Project Structure

The project follows a standard layered architecture pattern:

- **`controller`**: Contains the REST controllers that expose the application's API endpoints.
- **`model`**: Defines the data models (entities) that represent the application's domain.
- **`repository`**: Includes the data access layer, using Spring Data JPA to interact with the database.
- **`service`**: Implements the business logic of the application.

## Getting Started

### Prerequisites

- Java 17
- Maven 3.2+
- PostgreSQL
- A Google Cloud project with the Vertex AI API enabled

### Setup

1. **Clone the repository:**

   ```bash
   git clone <repository-url>
   ```

2. **Configure the database:**

   - Open `src/main/resources/application.properties` and update the database connection properties:

     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/your-database
     spring.datasource.username=your-username
     spring.datasource.password=your-password
     ```

3. **Set up Google Generative AI:**

   - Open `src/main/java/com/example/demo/service/GeminiService.java` and replace the placeholder values with your Google Cloud project ID and location:

     ```java
     String projectId = "your-google-cloud-project-id";
     String location = "us-central1";
     ```

   - **API Key Management:** For security reasons, it is highly recommended to use environment variables to manage your API key. Avoid hardcoding it in the source code.

### Running the Application

1. **Build the project:**

   ```bash
   mvn clean install
   ```

2. **Run the application:**

   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`.

## API Endpoints

The following API endpoints are available:

- **`GET /api/users`**: Get all users
- **`POST /api/users`**: Create a new user
- **`GET /api/clients`**: Get all clients
- **`POST /api/clients`**: Create a new client
- **`GET /api/shops`**: Get all shops
- **`POST /api/shops`**: Create a new shop
- **`GET /api/products`**: Get all products
- **`POST /api/products`**: Create a new product
- **`GET /api/carts/{id}`**: Get a cart by ID
- **`POST /api/carts`**: Create a new cart
- **`GET /api/orders`**: Get all orders
- **`POST /api/orders`**: Create a new order
- **`POST /api/gemini/generate`**: Generate text using the Gemini API

To use the Gemini endpoint, send a POST request with a plain text prompt in the request body.
