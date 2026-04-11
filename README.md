# PlaceIQ – College Placement Management System (Backend)

PlaceIQ is a robust Spring Boot application designed to manage college placements, connecting students with company opportunities through a centralized platform.

## 🚀 Technologies Used
- **Java 17**: Core programming language.
- **Spring Boot 3.2.4**: Application framework.
- **Spring Security**: Authentication and Authorization.
- **JSON Web Token (JWT)**: Secure stateless authentication.
- **Spring Data MongoDB**: Database integration with NoSQL.
- **MongoDB**: Primary data storage.
- **Maven**: Build and dependency management.
- **Lombok**: Reduced boilerplate code.
- **Swagger/OpenAPI**: API documentation and testing UI.

## 🛠️ Key Features
- **Authentication**: Secure Login/Registration for Students and Admins using JWT.
- **Student Management**: Profile updates, CGPA tracking, and skill management.
- **Company Management**: CRUD operations for placement companies.
- **Application Tracking**: Applying for jobs, tracking status (Pending, Shortlisted, Selected, Rejected).
- **Data Seeding**: Automatic creation of default Admin and Student accounts for testing.
- **CORS Support**: Configured for seamless communication with the React frontend.

## ⚙️ Getting Started

### Prerequisites
- JDK 17 or higher
- Maven 3.6+
- MongoDB instance (Local or Atlas)

### Configuration
Update `src/main/resources/application.properties` with your MongoDB URI:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/placeiq
```

### Running the Application
```bash
mvn clean install
mvn spring-boot:run
```

## 📖 API Documentation
Once the server is running, you can access the Swagger UI at:
`http://localhost:8080/swagger-ui/index.html`

## 📂 Project Structure
- `com.placeiq.api`: REST Controllers
- `com.placeiq.model`: MongoDB Entity classes
- `com.placeiq.service`: Business logic layer
- `com.placeiq.repository`: Data access layer
- `com.placeiq.security`: JWT and Security filters
- `com.placeiq.config`: Application configurations (CORS, Security, Swagger)
- `com.placeiq.dto`: Data Transfer Objects for requests/responses
