# 🏦 Business Banking - Microservices Architecture

![Java](https://img.shields.io/badge/Java-21-orange?style=flat&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?style=flat&logo=spring)
![WebFlux](https://img.shields.io/badge/Spring-WebFlux-green?style=flat&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat&logo=mysql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat&logo=docker)

Sistema bancario construido con arquitectura de microservicios utilizando Spring Boot, programación reactiva con WebFlux, y despliegue completo en contenedores Docker.

## 📋 Tabla de Contenidos

- [Características](#-características)
- [Arquitectura](#-arquitectura)
- [Stack Tecnológico](#-stack-tecnológico)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Ejecución](#-instalación-y-ejecución)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [API Endpoints](#-api-endpoints)
- [Documentación de la API](#-documentación-de-la-api)
- [Casos de Uso](#-casos-de-uso)
- [Patrones de Diseño](#-patrones-de-diseño)
- [Testing](#-testing)
- [Autor](#-autor)

## 🚀 Características

- **Arquitectura de Microservicios**: Separación de responsabilidades en servicios independientes
- **Programación Reactiva**: Implementación no bloqueante con Spring WebFlux
- **API First Development**: Diseño contract-first usando OpenAPI Specification
- **Arquitectura Limpia**: Separación en capas (Domain, Application, Infrastructure)
- **Containerización**: Despliegue completo con Docker y Docker Compose
- **Bases de Datos Aisladas**: Cada microservicio con su propia instancia MySQL
- **Comunicación HTTP Reactiva**: WebClient para comunicación inter-servicios
- **Validación Robusta**: Bean Validation en todas las capas
- **Manejo Global de Excepciones**: @RestControllerAdvice para respuestas consistentes
- **Documentación Interactiva**: Swagger UI integrado

## 🏗️ Arquitectura

El sistema está compuesto por dos microservicios principales:

```
┌─────────────────────┐         ┌─────────────────────┐
│  Customer Service   │◄────────│  Account Service    │
│     (Port 8081)     │  HTTP   │    (Port 8082)      │
└──────────┬──────────┘         └──────────┬──────────┘
           │                               │
           ▼                               ▼
    ┌─────────────┐                ┌─────────────┐
    │ customer_db │                │ account_db  │
    │   (MySQL)   │                │   (MySQL)   │
    └─────────────┘                └─────────────┘
```

### Microservicios

#### 1. Customer Service
**Responsabilidad**: Gestión completa del ciclo de vida de clientes

**Funcionalidades**:
- CRUD completo de clientes
- Herencia de entidad Persona
- Validación de identificación única
- Borrado lógico (soft delete) y físico (hard delete)

**Endpoints**: `/api/v1/customers`

#### 2. Account Service
**Responsabilidad**: Gestión de cuentas bancarias y movimientos

**Funcionalidades**:
- CRUD de cuentas bancarias
- Registro de movimientos (débitos/créditos)
- Validación de saldo disponible
- Comunicación con Customer Service para validaciones

**Endpoints**: 
- `/api/v1/accounts`
- `/api/v1/movements`

## 🛠️ Stack Tecnológico

### Backend
- **Java**: 21 (LTS)
- **Spring Boot**: 3.2.0
- **Spring WebFlux**: Programación reactiva
- **Spring Data JPA**: Capa de persistencia
- **Project Reactor**: Mono y Flux para flujos reactivos

### Base de Datos
- **MySQL**: 8.0
- **Hibernate**: ORM
- **Flyway**: Gestión de migraciones (via SQL scripts)

### Herramientas y Librerías
- **Lombok**: Reducción de código boilerplate
- **MapStruct**: Mapeo automático entre DTOs y entidades
- **OpenAPI Generator**: Generación de código desde especificación OpenAPI
- **SpringDoc OpenAPI**: Documentación automática de la API
- **Jakarta Validation**: Validaciones declarativas

### DevOps
- **Docker**: Containerización de aplicaciones
- **Docker Compose**: Orquestación de contenedores
- **Maven**: Gestión de dependencias y build

### Testing
- **JUnit 5**: Framework de testing
- **Mockito**: Mocking para pruebas unitarias
- **Reactor Test**: StepVerifier para testing reactivo

## 📦 Requisitos Previos

- **Java JDK**: 21 o superior
- **Maven**: 3.6+ (opcional, se incluye Maven Wrapper)
- **Docker**: 20.10+
- **Docker Compose**: 2.0+
- **Postman** (opcional): Para pruebas de endpoints

## 🚀 Instalación y Ejecución

### Opción 1: Docker Compose (Recomendado)

Esta es la forma más sencilla de ejecutar todo el sistema:

```bash
# Clonar el repositorio
git clone https://github.com/Ketiff/business-banking.git
cd business-banking

# Levantar todos los servicios
docker-compose up --build

# Para ejecutar en segundo plano
docker-compose up -d --build
```

Esto iniciará:
- Base de datos MySQL para Customer Service (puerto 3307)
- Base de datos MySQL para Account Service (puerto 3308)
- Customer Service (puerto 8081)
- Account Service (puerto 8082)

### Opción 2: Ejecución Local (Desarrollo)

#### 1. Iniciar Bases de Datos

```bash
# Solo las bases de datos
docker-compose up mysql-customer mysql-account -d
```

#### 2. Ejecutar Customer Service

```bash
cd customer-service

# Con Maven Wrapper
./mvnw spring-boot:run

# O con Maven instalado
mvn spring-boot:run
```

#### 3. Ejecutar Account Service

```bash
cd account-service

# Con Maven Wrapper
./mvnw spring-boot:run

# O con Maven instalado
mvn spring-boot:run
```

### Verificar la Instalación

```bash
# Customer Service Health Check
curl http://localhost:8081/api/v1/customers

# Account Service Health Check
curl http://localhost:8082/api/v1/accounts
```

## 📁 Estructura del Proyecto

El proyecto sigue una arquitectura hexagonal (puertos y adaptadores) con separación en capas:

```
business-banking/
├── customer-service/
│   └── src/main/java/com/bank/customer/
│       ├── domain/                      # Entidades de negocio
│       │   ├── model/
│       │   └── exceptions/
│       ├── application/                 # Lógica de negocio
│       │   ├── ports/
│       │   │   ├── input/              # Casos de uso
│       │   │   └── output/             # Puertos de salida
│       │   └── services/
│       └── infrastructure/              # Adaptadores
│           ├── input/rest/             # Controladores REST
│           └── output/persistence/     # Repositorios JPA
│
├── account-service/
│   └── src/main/java/com/bank/account_service/
│       ├── domain/                      # Entidades de negocio
│       │   ├── model/
│       │   └── exceptions/
│       ├── application/                 # Lógica de negocio
│       │   ├── ports/
│       │   └── services/
│       └── infrastructure/
│           ├── input/rest/             # Controladores REST
│           ├── output/
│           │   ├── persistence/        # Repositorios JPA
│           │   └── client/             # Cliente HTTP (WebClient)
│           └── config/
│
└── docker-compose.yml
```

### Capas de la Arquitectura

#### 1. Domain Layer (Dominio)
- **Propósito**: Lógica de negocio pura, sin dependencias externas
- **Componentes**: Entidades, Value Objects, Excepciones de dominio
- **Ejemplo**: `Account.java`, `Movement.java`, `MovementType.java`

#### 2. Application Layer (Aplicación)
- **Propósito**: Casos de uso y orquestación de la lógica
- **Componentes**: Interfaces de casos de uso (puertos), servicios
- **Ejemplo**: `AccountUseCase`, `AccountService`

#### 3. Infrastructure Layer (Infraestructura)
- **Propósito**: Detalles técnicos e implementaciones
- **Componentes**: Controladores REST, Adaptadores de persistencia, Clientes HTTP
- **Ejemplo**: `AccountController`, `AccountPersistenceAdapter`, `CustomerClientAdapter`

## 🌐 API Endpoints

### Customer Service (http://localhost:8081)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/customers` | Listar todos los clientes |
| GET | `/api/v1/customers/{id}` | Obtener cliente por ID |
| GET | `/api/v1/customers/identification/{identification}` | Buscar por identificación |
| POST | `/api/v1/customers` | Crear nuevo cliente |
| PUT | `/api/v1/customers/{id}` | Actualizar cliente |
| PATCH | `/api/v1/customers/{id}/activate` | Activar cliente (revertir soft delete) |
| DELETE | `/api/v1/customers/{id}` | Borrado lógico (soft delete) |
| DELETE | `/api/v1/customers/{id}/hard` | Borrado físico (permanente) |

### Account Service (http://localhost:8082)

#### Cuentas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/accounts` | Listar todas las cuentas |
| GET | `/api/v1/accounts/{id}` | Obtener cuenta por ID |
| GET | `/api/v1/accounts/number/{accountNumber}` | Buscar por número de cuenta |
| GET | `/api/v1/accounts/customer/{customerId}` | Listar cuentas de un cliente |
| POST | `/api/v1/accounts` | Crear nueva cuenta |
| PUT | `/api/v1/accounts/{id}` | Actualizar cuenta |
| DELETE | `/api/v1/accounts/{id}` | Eliminar cuenta |

#### Movimientos
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/movements` | Listar todos los movimientos |
| GET | `/api/v1/movements/{id}` | Obtener movimiento por ID |
| GET | `/api/v1/movements/account/{accountId}` | Listar movimientos de una cuenta |
| POST | `/api/v1/movements` | Registrar movimiento (débito/crédito) |
| DELETE | `/api/v1/movements/{id}` | Eliminar movimiento |

## 📚 Documentación de la API

### Swagger UI (Interfaz Interactiva)

Una vez el sistema esté ejecutándose, accede a:

- **Customer Service**: http://localhost:8081/swagger-ui.html
- **Account Service**: http://localhost:8082/swagger-ui.html

### OpenAPI Specification (JSON/YAML)

- **Customer Service**: http://localhost:8081/api-docs
- **Account Service**: http://localhost:8082/api-docs

### Especificaciones YAML

Los contratos OpenAPI están disponibles en:
- `customer-service/src/main/resources/openapi.yaml`
- `account-service/src/main/resources/openapi/account-api.yaml`

## 💡 Casos de Uso

### Ejemplo 1: Crear un Cliente

**Request**:
```bash
curl -X POST http://localhost:8081/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jose Lema",
    "gender": "MALE",
    "identification": "1234567890",
    "address": "Otavalo sn y principal",
    "phone": "098254785",
    "password": "1234"
  }'
```

**Response** (201 Created):
```json
{
  "id": 1,
  "name": "Jose Lema",
  "gender": "MALE",
  "identification": "1234567890",
  "address": "Otavalo sn y principal",
  "phone": "098254785",
  "status": true,
  "createdAt": "2024-01-22T10:30:00",
  "updatedAt": "2024-01-22T10:30:00"
}
```

### Ejemplo 2: Crear una Cuenta

**Request**:
```bash
curl -X POST http://localhost:8082/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "478758",
    "accountType": "SAVINGS",
    "initialBalance": 2000.00,
    "customerId": 1
  }'
```

**Response** (201 Created):
```json
{
  "id": 1,
  "accountNumber": "478758",
  "accountType": "SAVINGS",
  "initialBalance": 2000.00,
  "currentBalance": 2000.00,
  "status": true,
  "customerId": 1,
  "createdAt": "2024-01-22T10:35:00",
  "updatedAt": "2024-01-22T10:35:00"
}
```

### Ejemplo 3: Registrar un Retiro (Débito)

**Request**:
```bash
curl -X POST http://localhost:8082/api/v1/movements \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 1,
    "movementType": "DEBIT",
    "amount": 575.00
  }'
```

**Response** (201 Created):
```json
{
  "id": 1,
  "date": "2024-01-22T10:40:00",
  "movementType": "DEBIT",
  "amount": 575.00,
  "balance": 1425.00,
  "accountId": 1
}
```

### Ejemplo 4: Registrar un Depósito (Crédito)

**Request**:
```bash
curl -X POST http://localhost:8082/api/v1/movements \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 1,
    "movementType": "CREDIT",
    "amount": 1000.00
  }'
```

**Response** (201 Created):
```json
{
  "id": 2,
  "date": "2024-01-22T10:45:00",
  "movementType": "CREDIT",
  "amount": 1000.00,
  "balance": 2425.00,
  "accountId": 1
}
```

### Ejemplo 5: Generar Estado de Cuenta

**Request**:
```bash
curl "http://localhost:8082/api/v1/reports/1?startDate=2024-01-01&endDate=2024-01-31"
```

**Response** (200 OK):
```json
{
  "clientId": 1,
  "clientName": "Jose Lema",
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "accounts": [
    {
      "accountNumber": "478758",
      "accountType": "SAVINGS",
      "initialBalance": 2000.00,
      "currentBalance": 2425.00,
      "status": true,
      "movements": [
        {
          "date": "2024-01-22T10:40:00",
          "movementType": "DEBIT",
          "amount": 575.00,
          "balance": 1425.00
        },
        {
          "date": "2024-01-22T10:45:00",
          "movementType": "CREDIT",
          "amount": 1000.00,
          "balance": 2425.00
        }
      ]
    }
  ]
}
```

### Ejemplo 6: Error - Saldo Insuficiente

**Request**:
```bash
curl -X POST http://localhost:8082/api/v1/movements \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 1,
    "movementType": "DEBIT",
    "amount": 5000.00
  }'
```

**Response** (400 Bad Request):
```json
{
  "timestamp": "2024-01-22T10:50:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Saldo no disponible",
  "path": "/api/v1/movements"
}
```

## 🎯 Patrones de Diseño

### Arquitectura Hexagonal (Ports & Adapters)
- **Puertos de Entrada**: Interfaces de casos de uso (`AccountUseCase`, `CustomerUseCase`)
- **Puertos de Salida**: Interfaces de repositorios y clientes (`AccountPersistencePort`, `CustomerClientPort`)
- **Adaptadores de Entrada**: Controladores REST (`AccountController`)
- **Adaptadores de Salida**: Implementaciones de persistencia y clientes HTTP

### Repository Pattern
- Abstracción sobre la capa de persistencia
- Interfaces de puertos que declaran operaciones
- Implementaciones en adaptadores de persistencia

### DTO Pattern & Mappers
- DTOs para transferencia de datos entre capas
- MapStruct para conversiones automáticas
- Desacoplamiento entre representaciones

### Dependency Injection
- Inyección por constructor usando `@RequiredArgsConstructor`
- Inversión de control gestionada por Spring

### Service Layer
- Orquestación de lógica de negocio
- Implementación de casos de uso
- Manejo de transacciones

### Domain-Driven Design (DDD)
- Modelo de dominio enriquecido con comportamiento
- Lenguaje ubicuo en el código
- Excepciones de dominio específicas

### Programación Reactiva
- Composición de flujos con operadores (`map`, `flatMap`)
- Bridge pattern para código bloqueante (JPA)
- Manejo de errores reactivo

## 🧪 Testing

### Ejecutar Pruebas Unitarias

#### Customer Service
```bash
cd customer-service
./mvnw test
```

#### Account Service
```bash
cd account-service
./mvnw test
```

### Pruebas Implementadas

**MovementTypeTest.java**:
```java
✅ debitShouldHaveNegativeMultiplier()
✅ creditShouldHavePositiveMultiplier()
✅ debitShouldRequireBalanceValidation()
✅ creditShouldNotRequireBalanceValidation()
```

### Cobertura de Testing

- **Pruebas Unitarias**: ✅ Implementadas para lógica de negocio crítica
- **Pruebas de Integración**: ⚠️ Pendientes (próxima versión)

## 🗄️ Base de Datos

### Configuración

**Customer DB**:
- **Puerto**: 3307 (host) → 3306 (contenedor)
- **Base de datos**: `customer_db`
- **Usuario**: `bank_user`
- **Contraseña**: `bank_password`

**Account DB**:
- **Puerto**: 3308 (host) → 3306 (contenedor)
- **Base de datos**: `account_db`
- **Usuario**: `bank_user`
- **Contraseña**: `bank_password`

### Scripts SQL

Los scripts de inicialización están en:
- `customer-service/src/main/resources/schema.sql`
- `customer-service/src/main/resources/data.sql`
- `account-service/src/main/resources/schema.sql`
- `account-service/src/main/resources/data.sql`

### Conectar a las Bases de Datos

```bash
# Customer DB
mysql -h 127.0.0.1 -P 3307 -u bank_user -pbank_password customer_db

# Account DB
mysql -h 127.0.0.1 -P 3308 -u bank_user -pbank_password account_db
```

## 🔧 Configuración Avanzada

### Perfiles de Spring

El proyecto soporta múltiples perfiles:

- **local**: Para desarrollo local (application.yml)
- **docker**: Para ejecución en contenedores (application-docker.yml)

Cambiar perfil:
```bash
# En application.yml
spring:
  profiles:
    active: local  # o 'docker'
```

### Variables de Entorno en Docker

Editar `docker-compose.yml` para configurar:
```yaml
environment:
  - SPRING_PROFILES_ACTIVE=docker
  - SPRING_DATASOURCE_URL=jdbc:mysql://...
```

### Configuración de Logging

Niveles de log en `application.yml`:
```yaml
logging:
  level:
    com.bank.account_service: DEBUG  # DEBUG, INFO, WARN, ERROR
    org.hibernate.SQL: DEBUG
```

## 🚦 Estado del Proyecto

**Versión**: 1.0.0  
**Estado**: ✅ Finalizado (Funcionalidades Core)

### Completado
- ✅ Arquitectura de microservicios
- ✅ CRUD completo (Clientes, Cuentas, Movimientos)
- ✅ Validaciones de negocio
- ✅ Comunicación inter-servicios
- ✅ Manejo global de excepciones
- ✅ Documentación OpenAPI/Swagger
- ✅ Despliegue con Docker
- ✅ Pruebas unitarias básicas

### Pendiente (Versiones Futuras)
- ⚠️ Reportería (F4)
- ⚠️ Pruebas de integración (F6)
- ⚠️ Métricas y monitoreo
- ⚠️ Circuit breakers para resiliencia
- ⚠️ Comunicación asíncrona (Kafka/RabbitMQ)

## 👤 Autor

**Kevin Revelo**

## 📄 Notas de Desarrollo
### Decisiones de Diseño

- **WebFlux sobre WebMVC**: Para aprovechar programación reactiva
- **JPA bloqueante**: Bridge pattern con `Schedulers.boundedElastic()`
- **Bases de datos separadas**: Aislamiento por microservicio
- **HTTP síncrono**: WebClient reactivo (comunicación simple)
- **Excepciones Runtime**: Para arquitectura en capas limpia

