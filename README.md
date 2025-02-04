```markdown
# Mail Sender Demo

This is a demo project for sending emails using Spring Boot. The project includes user authentication and email verification functionalities.

## Technologies Used

- Java
- Spring Boot
- Maven
- Hibernate
- MySQL
- Spring Security
- JWT (JSON Web Tokens)
- Jakarta Persistence API (JPA)
- Lombok
- JavaMailSender

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven
- MySQL

### Configuration

Create a `.env` file in the root directory of the project and add the following properties:

```properties
SPRING_DATASOURCE_URL=your_db_url
SPRING_DATASOURCE_USERNAME=your_db_username
SPRING_DATASOURCE_PASSWORD=your_db_password
SUPPORT_EMAIL=your_email@gmail.com
APP_PASSWORD=your_email_password
JWT_SECRET_KEY=your_jwt_secret_key
```

### Application Properties

The `application.yml` file is configured to read properties from the `.env` file:

```yaml
spring:
  application:
    name: mail-sender-demo

  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}

  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect

  mail:
    host: smtp.gmail.com
    port: 587
    username: ${SUPPORT_EMAIL}
    password: ${APP_PASSWORD}
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true

security:
  jwt:
    secret-key: ${JWT_SECRET_KEY}
    expiration-time: 3600000

spring.config.import: optional:file:.env[.properties]
```

### Running the Application

1. Clone the repository:
   ```sh
   git clone https://github.com/your_username/mail-sender-demo.git
   ```
2. Navigate to the project directory:
   ```sh
   cd mail-sender-demo
   ```
3. Build the project using Maven:
   ```sh
   mvn clean install
   ```
4. Run the application:
   ```sh
   mvn spring-boot:run
   ```

## API Endpoints

### User Authentication

- `POST /login` - Login a user
    - **Request Body:**
      ```json
      {
        "email": "ergule18@gmail.com",
        "password": "SecurePassword123!"
      }
      ```
    - **Response Body:**
      ```json
      {
        "token": "your_jwt_token"
      }
      ```

- `POST /logout` - Logout a user
    - **Request Header:**
      ```http
      Authorization: Bearer your_jwt_token
      ```

- `POST /validateToken` - Validate JWT token
    - **Request Header:**
      ```http
      Authorization: Bearer your_jwt_token
      ```

### User Management

- `POST /users` - Add a new user
    - **Request Body:**
      ```json
      {
        "email": "test_email",
        "password": "SecurePassword123!",
        "username": "testuser1"
      }
      ```
    - **Response Body:**
      ```json
      {
        "email": "test_email",
        "password": "$2a$10$Qd9Q09HDDeW1SNN8gvPYhO4KkamCrTA9Mp0jXsRvgxWGdcmc4JRSW",
        "username": "testuser1",
        "role": [
          "ROLE_USER"
        ],
        "verificationCode": "177740"
      }
      ```

- `GET /users` - Get all users
    - **Response Body:**
      ```json
      [
        {
          "id": 1,
          "email": "test_email",
          "username": "testuser1",
          "roles": ["ROLE_USER"],
          "enabled": true
        }
      ]
      ```

- `POST /users/verify` - Verify a user
    - **Request Body:**
      ```json
      {
        "email": "test_email",
        "verificationCode": "177740"
      }
      ```

- `POST /users/resendVerification` - Resend verification email
    - **Request Body:**
      ```json
      {
        "email": "test_email"
      }
      ```

## License

This project is licensed under the MIT License.
```

This README provides a detailed overview of the project, setup instructions, configuration details, and API endpoints with example request and response bodies.