# Blogging Platform

A full-stack blogging platform built with Java Servlets and React.js.

## Project Structure

```
blogging-platform/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/blog/
│   │   │       ├── controllers/  # Servlet controllers
│   │   │       ├── models/       # Data models
│   │   │       └── services/     # Business logic
│   │   └── webapp/
│   │       ├── WEB-INF/          # Web configuration
│   │       └── static/           # Frontend assets
└── pom.xml                       # Maven configuration
```

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher
- Apache Tomcat 9.0 or higher

## Setup Instructions

1. Clone the repository
2. Install dependencies:
   ```bash
   mvn clean install
   ```
3. Configure database:
   - Create a MySQL database named 'blogging_platform'
   - Update database configuration in src/main/resources/application.properties
4. Run the application:
   ```bash
   mvn tomcat7:run
   ```
5. Access the application at: http://localhost:8080

## Features

- User authentication (login/register)
- Create, edit, and delete blogs
- View blog posts
- Comment system
- User profile management

## Technologies Used

- Backend: Java Servlets, Spring Framework
- Frontend: React.js
- Database: MySQL
- Build Tool: Maven
- Web Server: Apache Tomcat
