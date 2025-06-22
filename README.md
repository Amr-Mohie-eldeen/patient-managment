# Patient Management System

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A comprehensive microservices-based patient management system built with Spring Boot, designed for healthcare organizations to manage patient data, authentication, billing, and analytics in a secure and scalable manner.

## 🏗️ Architecture Overview

The system follows a microservices architecture pattern with the following components:

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Client Apps   │────│   API Gateway    │────│  Load Balancer  │
│                 │    │   (Port 4004)    │    │                 │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                │
                ┌───────────────┼───────────────┐
                │               │               │
        ┌───────▼──────┐ ┌──────▼──────┐ ┌─────▼──────┐
        │ Auth Service │ │Patient Svc  │ │Billing Svc │
        │ (Port 4005)  │ │(Port 4000)  │ │(Port 4001) │
        └──────────────┘ └─────────────┘ └────────────┘
                │               │               │
                │        ┌──────▼──────┐       │
                │        │Analytics Svc│       │
                │        │             │       │
                │        └─────────────┘       │
                │               │               │
        ┌───────▼──────┐ ┌──────▼──────┐ ┌─────▼──────┐
        │ PostgreSQL   │ │   Kafka     │ │    gRPC    │
        │   Database   │ │  Message    │ │Communication│
        └──────────────┘ └─────────────┘ └────────────┘
```

## 🚀 Microservices

### 1. API Gateway (`api-gateway`)
- **Port**: 4004
- **Technology**: Spring Cloud Gateway WebFlux
- **Purpose**: Single entry point for all client requests, handles routing, authentication, and load balancing
- **Features**:
  - JWT token validation
  - Request routing to appropriate microservices
  - API documentation aggregation
  - Rate limiting and security policies

### 2. Authentication Service (`auth-service`)
- **Port**: 4005
- **Technology**: Spring Boot + Spring Security + JWT
- **Database**: PostgreSQL
- **Purpose**: Centralized authentication and authorization
- **Features**:
  - User registration and login
  - JWT token generation and validation
  - Role-based access control (RBAC)
  - Password encryption and security

### 3. Patient Service (`patient-service`)
- **Port**: 4000
- **Technology**: Spring Boot + JPA + Kafka Producer
- **Database**: PostgreSQL
- **Purpose**: Core patient data management
- **Features**:
  - CRUD operations for patient records
  - Patient data validation
  - Event publishing to Kafka for analytics
  - RESTful API with OpenAPI documentation

### 4. Billing Service (`billing-service`)
- **Port**: 4001 (HTTP), 9001 (gRPC)
- **Technology**: Spring Boot + gRPC
- **Purpose**: Financial and billing operations
- **Features**:
  - Billing account management
  - gRPC-based high-performance communication
  - Integration with patient service
  - Financial transaction processing

### 5. Analytics Service (`analytics-service`)
- **Technology**: Spring Boot + Kafka Consumer
- **Purpose**: Real-time data analytics and reporting
- **Features**:
  - Event-driven data processing
  - Real-time analytics dashboard
  - Patient data insights
  - Reporting and metrics generation

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| **Runtime** | Java | 21 |
| **Framework** | Spring Boot | 3.5.0 |
| **Build Tool** | Maven | 3.6+ |
| **Database** | PostgreSQL | Latest |
| **Message Broker** | Apache Kafka | 3.3.0 |
| **API Gateway** | Spring Cloud Gateway | 2025.0.0 |
| **Security** | Spring Security + JWT | Latest |
| **Communication** | gRPC | 1.69.0 |
| **Documentation** | OpenAPI 3 | 2.8.9 |
| **Containerization** | Docker | Latest |

## 📋 Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **Docker & Docker Compose**
- **PostgreSQL 13+**
- **Apache Kafka 2.8+**

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd patient-management
```

### 2. Build All Services
```bash
# Build all microservices
mvn clean install -f patient-service/pom.xml
mvn clean install -f auth-service/pom.xml
mvn clean install -f billing-service/pom.xml
mvn clean install -f analytics-service/pom.xml
mvn clean install -f api-gateway/pom.xml
```

### 3. Start Infrastructure Services
```bash
# Start PostgreSQL and Kafka using Docker
docker run -d --name postgres -e POSTGRES_DB=patientdb -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=password -p 5432:5432 postgres:13
docker run -d --name kafka -p 9092:9092 -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 confluentinc/cp-kafka:latest
```

### 4. Start Microservices
```bash
# Start services in order
java -jar auth-service/target/auth-service-0.0.1-SNAPSHOT.jar &
java -jar patient-service/target/patient-service-0.0.1-SNAPSHOT.jar &
java -jar billing-service/target/billing-service-0.0.1-SNAPSHOT.jar &
java -jar analytics-service/target/analytics-service-0.0.1-SNAPSHOT.jar &
java -jar api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar &
```

## 🔧 Configuration

### Environment Variables
```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=patientdb
DB_USER=admin
DB_PASSWORD=password

# JWT Configuration
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=86400000

# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

### Service Ports
| Service | HTTP Port | gRPC Port |
|---------|-----------|-----------|
| API Gateway | 4004 | - |
| Auth Service | 4005 | - |
| Patient Service | 4000 | - |
| Billing Service | 4001 | 9001 |
| Analytics Service | Dynamic | - |

## 📚 API Documentation

### Authentication Endpoints
```http
POST /auth/login
POST /auth/register
GET  /auth/validate
```

### Patient Management Endpoints
```http
GET    /api/patients
POST   /api/patients
GET    /api/patients/{id}
PUT    /api/patients/{id}
DELETE /api/patients/{id}
```

### Billing gRPC Services
```protobuf
service BillingService {
  rpc CreateBillingAccount(CreateBillingAccountRequest) returns (BillingAccountResponse);
  rpc GetBillingAccount(GetBillingAccountRequest) returns (BillingAccountResponse);
}
```

### API Documentation URLs
- **Swagger UI**: http://localhost:4004/swagger-ui.html
- **Patient Service Docs**: http://localhost:4004/api-docs/patients
- **Auth Service Docs**: http://localhost:4004/api-docs/auth

## 🔒 Security

### Authentication Flow
1. Client authenticates via `/auth/login`
2. Server returns JWT token
3. Client includes token in `Authorization: Bearer <token>` header
4. API Gateway validates token before routing requests

### Security Features
- JWT-based stateless authentication
- Password encryption using BCrypt
- Role-based access control
- API rate limiting
- CORS configuration
- Input validation and sanitization

## 📊 Monitoring & Observability

### Health Checks
```http
GET /actuator/health
GET /actuator/info
GET /actuator/metrics
```

### Logging
- Centralized logging with structured JSON format
- Log levels: INFO, WARN, ERROR
- Request/response logging in API Gateway

## 🧪 Testing

### Run Unit Tests
```bash
mvn test -f patient-service/pom.xml
mvn test -f auth-service/pom.xml
mvn test -f billing-service/pom.xml
mvn test -f analytics-service/pom.xml
```

### API Testing
Use the provided HTTP request files in `api-requests/` directory:
- Patient Service: `api-requests/patient-service/`
- Auth Service: `api-requests/auth-service/`
- Billing Service: `grpc-requests/billing-service/`

## 🐳 Docker Deployment

### Build Docker Images
```bash
docker build -t patient-service ./patient-service
docker build -t auth-service ./auth-service
docker build -t billing-service ./billing-service
docker build -t analytics-service ./analytics-service
docker build -t api-gateway ./api-gateway
```

### Docker Compose (Recommended)
```bash
docker-compose up -d
```

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow
```yaml
name: CI/CD Pipeline
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 21
        uses: actions/setup-java@v2
        with:
          java-version: '21'
      - name: Run tests
        run: mvn test
```

## 📈 Performance Considerations

- **Database Connection Pooling**: HikariCP for optimal database performance
- **Caching**: Redis integration for frequently accessed data
- **Load Balancing**: Multiple instances behind load balancer
- **Async Processing**: Kafka for event-driven architecture
- **gRPC**: High-performance communication for billing service

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Standards
- Follow Java coding conventions
- Write unit tests for new features
- Update documentation for API changes
- Use meaningful commit messages

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For support and questions:
- Create an issue in the GitHub repository
- Contact the development team
- Check the documentation and FAQ

## 🗺️ Roadmap

- [ ] Kubernetes deployment manifests
- [ ] Distributed tracing with Jaeger
- [ ] Metrics collection with Prometheus
- [ ] GraphQL API support
- [ ] Mobile app integration
- [ ] Advanced analytics dashboard
- [ ] Multi-tenant architecture
- [ ] FHIR compliance for healthcare standards

---

**Built with ❤️ for Healthcare Innovation**