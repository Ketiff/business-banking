# Resumen del Proyecto: business-banking

Este documento resume la arquitectura y componentes del proyecto "business-banking".

## 1. Arquitectura General

El proyecto sigue una **arquitectura de microservicios** con dos servicios principales:

*   **`customer-service`**: Gestiona la información y el ciclo de vida de los clientes.
*   **`account-service`**: Gestiona las cuentas bancarias, movimientos y reportes.

Ambos servicios están orquestados a través de **Docker Compose**.

## 2. Stack Tecnológico Común

Ambos microservicios comparten una base tecnológica moderna y homogénea:

*   **Lenguaje**: Java 21
*   **Framework**: Spring Boot 3.2.0
*   **Programación**: Reactiva (Spring WebFlux)
*   **Base de Datos**: MySQL (cada servicio con su propia instancia)
*   **Persistencia**: Spring Data JPA
*   **Diseño de API**: "API-first" con OpenAPI (Swagger)
*   **Documentación de API**: Springdoc
*   **Herramientas de Código**: Lombok

## 3. Orquestación y Despliegue (docker-compose.yml)

*   **Bases de Datos Aisladas**:
    *   `customer-service` usa la base de datos `customer_db`.
    *   `account-service` usa la base de datos `account_db`.
*   **Inicialización de Datos**: Cada servicio inicializa su base de datos con archivos `schema.sql` y `data.sql`.
*   **Orden de Arranque**:
    1.  Bases de datos.
    2.  `customer-service` (depende de su BD).
    3.  `account-service` (depende de su BD y de que `customer-service` haya iniciado).
*   **Red**: Todos los servicios se comunican en una red interna de Docker llamada `bank-network`.

## 4. `customer-service`

*   **Responsabilidad**: CRUD completo para los clientes.
*   **Endpoints Principales**:
    *   `POST /api/v1/customers`: Crear un cliente.
    *   `GET /api/v1/customers/{id}`: Obtener cliente por ID.
    *   `GET /api/v1/customers/identification/{identification}`: Obtener cliente por número de identificación.
    *   `DELETE /api/v1/customers/{id}`: Borrado lógico (desactivación).
    *   `DELETE /api/v1/customers/{id}/hard`: Borrado físico.
*   **Modelos**: `CreateCustomerRequest`, `UpdateCustomerRequest`, `CustomerResponse`.

## 5. `account-service`

*   **Responsabilidad**: Gestionar cuentas, movimientos (transacciones) y generar reportes.
*   **Endpoints Principales**:
    *   **Cuentas**: CRUD para cuentas (`/api/v1/accounts`). Requiere un `customerId` para su creación.
    *   **Movimientos**: Registro y consulta de débitos/créditos (`/api/v1/movements`).
    *   **Reportes**: `GET /api/v1/reports/{customerId}` para generar un estado de cuenta detallado en un rango de fechas.
*   **Dependencia Funcional**: Depende de `customer-service`, ya que una cuenta no puede existir sin un cliente.

## Conclusión Clave

El sistema está bien estructurado, con responsabilidades claras y desacoplamiento a nivel de datos. La dependencia de `account-service` hacia `customer-service` es un punto central del flujo de negocio. El stack tecnológico es moderno y consistente en todo el proyecto.

---

## 6. Endpoints Detallados

### 👥 Customer Service (`http://localhost:8081`)
-   **GET All Customers**: `GET /api/v1/customers`
-   **GET Customer by ID**: `GET /api/v1/customers/{id}`
-   **GET Customer by Identification**: `GET /api/v1/customers/identification/{identification}`
-   **POST Create Customer**: `POST /api/v1/customers`
-   **PUT Update Customer**: `PUT /api/v1/customers/{id}`
-   **PATCH Activate Customer**: `PATCH /api/v1/customers/{id}/activate`
-   **DELETE Soft Delete Customer**: `DELETE /api/v1/customers/{id}`
-   **DELETE Hard Delete Customer**: `DELETE /api/v1/customers/{id}/hard`

### 💰 Account Service (`http://localhost:8082`)
#### 📊 Cuentas
-   **GET All Accounts**: `GET /api/v1/accounts`
-   **GET Account by ID**: `GET /api/v1/accounts/{id}`
-   **GET Account by Number**: `GET /api/v1/accounts/number/{accountNumber}`
-   **GET Accounts by Customer ID**: `GET /api/v1/accounts/customer/{customerId}`
-   **POST Create Account**: `POST /api/v1/accounts`
-   **PUT Update Account**: `PUT /api/v1/accounts/{id}`
-   **DELETE Delete Account**: `DELETE /api/v1/accounts/{id}`
#### 💸 Movimientos
-   **GET All Movements**: `GET /api/v1/movements`
-   **GET Movement by ID**: `GET /api/v1/movements/{id}`
-   **GET Movements by Account ID**: `GET /api/v1/movements/account/{accountId}`
-   **POST Create Movement**: `POST /api/v1/movements` (Cubre créditos y débitos)
-   **DELETE Delete Movement**: `DELETE /api/v1/movements/{id}`
#### 📈 Reportes
-   **GET Account Statement**: `GET /api/v1/reports/{customerId}?startDate={fecha}&endDate={fecha}`

---

## 7. Análisis Detallado del Flujo (Ejemplo: Crear Cliente)

Este es el recorrido de una petición `POST /api/v1/customers` a través de las capas del `customer-service`.

1.  **`CustomerController` (`createCustomer`)**:
    *   **Rol**: Puerta de entrada HTTP.
    *   **Acción**: Recibe la petición. Usa `CustomerRestMapper` para convertir el JSON `CreateCustomerRequest` en un objeto de dominio `Customer`. Delega el trabajo llamando a `createCustomerUseCase.create(customer)`.

2.  **`CustomerService` (`create`)**:
    *   **Rol**: Cerebro de la operación (implementa `CreateCustomerUseCase`).
    *   **Acción**:
        *   **Validación de Negocio**: Verifica que no exista otro cliente con la misma identificación (`repositoryPort.existsByIdentification(...)`).
        *   **Enriquecimiento**: Establece valores por defecto como `status = true` y las fechas de creación.
        *   **Delegación a Persistencia**: Llama a `repositoryPort.save(customer)` para que los datos se guarden.

3.  **`CustomerPersistenceAdapter` (`save`)**:
    *   **Rol**: Adaptador a la tecnología de base de datos (implementa `CustomerRepositoryPort`).
    *   **Acción**:
        *   **Mapeo a Entidad**: Usa `CustomerPersistenceMapper` para convertir el objeto de dominio `Customer` en una `CustomerEntity` (la clase que representa la tabla de la base de datos con anotaciones `@Entity`).
        *   **Ejecución en BD**: Llama a `jpaRepository.save(entity)`. En este punto, **Spring Data JPA** genera y ejecuta el comando `INSERT` en la base de datos MySQL.
        *   **Mapeo de Vuelta a Dominio**: El `mapper` convierte la `CustomerEntity` (ya con su ID asignado por la BD) de nuevo a un objeto `Customer`.

4.  **El Viaje de Retorno**:
    *   El objeto `Customer` guardado vuelve del `Adapter` al `Service`, y del `Service` al `Controller`.
    *   En el `CustomerController`, `CustomerRestMapper` convierte el `Customer` en un `CustomerResponse` (un JSON seguro para enviar al exterior, sin datos sensibles).
    *   Finalmente, se envía la respuesta HTTP `201 Created` al cliente.

---

## 8. Análisis Detallado del Flujo (Ejemplo: Crear Cuenta)

Este es el recorrido de una petición `POST /api/v1/accounts` a través de las capas del `account-service`, destacando la comunicación entre microservicios.

1.  **`AccountController` (`createAccount`)**:
    *   **Rol**: Puerta de entrada HTTP para `account-service`.
    *   **Acción**: Recibe la petición. Usa `AccountRestMapper` para convertir el JSON `CreateAccountRequest` en un objeto de dominio `Account`. Delega el trabajo llamando a `accountUseCase.createAccount(account)`.

2.  **`AccountService` (`createAccount`)**:
    *   **Rol**: Orquestador de la lógica de negocio.
    *   **Acción**:
        *   **Comunicación Inter-Servicio**: Llama a `customerClient.existsCustomer(customerId)` para validar que el cliente existe antes de crear la cuenta.
        *   **Validación de Negocio**: Si el cliente existe, verifica que el número de cuenta no esté duplicado (`accountPersistence.existsByAccountNumber(...)`).
        *   **Enriquecimiento**: Establece el saldo inicial, estado y fechas.
        *   **Delegación a Persistencia**: Llama a `accountPersistence.save(account)`.

3.  **`CustomerClientAdapter` (`existsCustomer`)**:
    *   **Rol**: Adaptador de cliente HTTP para hablar con `customer-service`.
    *   **Acción**:
        *   Utiliza **`WebClient`** (el cliente HTTP reactivo de Spring) para hacer una petición `GET` a `http://customer-service:8081/api/v1/customers/{id}`.
        *   Usa `.toBodilessEntity()` para eficiencia, ya que solo le importa el código de estado (200 OK o no).
        *   Implementa resiliencia con `.onErrorResume`, tratando un servicio caído como un cliente no existente (devuelve `false`).

4.  **`AccountPersistenceAdapter` (`save`)**:
    *   **Rol**: Adaptador a la tecnología de base de datos.
    *   **Acción**:
        *   **Manejo de Bloqueo Reactivo**: Envuelve la llamada bloqueante de JPA (`accountRepository.save()`) en `Mono.fromCallable(...).subscribeOn(Schedulers.boundedElastic())`. Esto protege los hilos de WebFlux y asegura el rendimiento.
        *   **Mapeo y Persistencia**: Convierte entre el `Account` (dominio) y la `AccountEntity` (persistencia) y guarda en la base de datos `account_db`.

5.  **El Viaje de Retorno**:
    *   El `Account` guardado vuelve a través de las capas hasta el `AccountController`, donde se convierte en un `AccountResponse` y se envía la respuesta HTTP `201 Created`.

---

## 9. Análisis Detallado del Flujo (Ejemplo: Crear Movimiento)

Este es el recorrido de una petición `POST /api/v1/movements`, el corazón transaccional del sistema. Aunque se usa un solo endpoint, la lógica de negocio se bifurca dependiendo del `movementType`.

1.  **Flujo Común Inicial**:
    *   **`MovementController`**: Recibe la petición, usa `MovementRestMapper` para convertir el JSON a un objeto de dominio `Movement` y llama a `movementUseCase.registerMovement(movement)`.
    *   **`MovementService`**:
        *   Valida que el monto sea positivo.
        *   Busca la `Account` por su ID. Si no existe o está inactiva, lanza un error.

2.  **Bifurcación de Lógica en `MovementService` (`processMovement`)**:

    *   **Si es `CREDIT` (Depósito)**:
        *   **Cálculo**: La lógica de dominio en `Movement.calculateNewBalance` **suma** el monto al saldo actual (`currentBalance.add(amount)`).
        *   **Validación**: Generalmente no hay validaciones adicionales. Se asume que siempre se puede recibir dinero.

    *   **Si es `DEBIT` (Retiro)**:
        *   **Cálculo**: La lógica de dominio en `Movement.calculateNewBalance` **resta** el monto del saldo actual (`currentBalance.subtract(amount)`).
        *   **Validación Crítica (F3)**: Antes de cualquier cálculo, el servicio invoca al método de dominio `account.hasSufficientBalance(amount)`.
            *   Este método encapsula la regla de negocio `this.currentBalance.compareTo(amount) >= 0`.
            *   Si devuelve `false`, el servicio lanza `InsufficientBalanceException`, el flujo se corta y se devuelve un error HTTP 400 ("Saldo no disponible").

3.  **Convergencia y Persistencia**:
    *   **Actualización en Memoria**: Si todas las validaciones pasan, se actualiza el saldo en el objeto `Account` (`account.updateBalance(newBalance)`) y se completa la información del objeto `Movement`.
    *   **Persistencia Secuencial**:
        1.  Se llama a `accountPersistence.save(account)` para ejecutar el `UPDATE` en la tabla de cuentas.
        2.  Usando `.then()`, solo si el paso anterior tiene éxito, se llama a `movementPersistence.save(movement)` para ejecutar el `INSERT` en la tabla de movimientos.

4.  **El Viaje de Retorno**: El `Movement` recién creado vuelve al controlador, se mapea a un `MovementResponse` y se envía la respuesta HTTP `201 Created`.

---

## 10. Patrones de Diseño en `customer-service`

1.  **Arquitectura Hexagonal (Puertos y Adaptadores)**
    *   **Qué es**: Un patrón que aísla el núcleo de la lógica de negocio de los detalles externos (UI, BD, etc.) a través de "Puertos" (interfaces) y "Adaptadores" (implementaciones).
    *   **Cómo lo usas**:
        *   **Puerto de Entrada**: Interfaces `CreateCustomerUseCase`, `GetCustomerUseCase`, etc.
        *   **Adaptador de Entrada**: `CustomerController` adapta HTTP a llamadas a los casos de uso.
        *   **Puerto de Salida**: `CustomerRepositoryPort` declara la necesidad de persistencia.
        *   **Adaptador de Salida**: `CustomerPersistenceAdapter` implementa el puerto usando Spring Data JPA.

2.  **Repository**
    *   **Qué es**: Una abstracción sobre la capa de persistencia, exponiendo una interfaz similar a una colección para acceder a los objetos de dominio.
    *   **Cómo lo usas**: `CustomerRepositoryPort` es la interfaz del repositorio, y `CustomerPersistenceAdapter` (usando `CustomerJpaRepository`) es su implementación.

3.  **Data Transfer Object (DTO) y Mapper**
    *   **Qué es**: DTOs son objetos para transferir datos entre capas. Mappers los convierten.
    *   **Cómo lo usas**: `CreateCustomerRequest` y `CustomerResponse` son DTOs de la API. `CustomerEntity` es el DTO de la BD. `CustomerRestMapper` y `CustomerPersistenceMapper` (usando MapStruct) hacen las conversiones.

4.  **Inyección de Dependencias (DI)**
    *   **Qué es**: Un objeto recibe sus dependencias desde una fuente externa (el contenedor de Spring) en lugar de crearlas él mismo.
    *   **Cómo lo usas**: Usando `@RequiredArgsConstructor` de Lombok, Spring inyecta las dependencias (declaradas como `final`) a través del constructor. Ejemplo: `private final CustomerRepositoryPort repositoryPort;` en `CustomerService`.

5.  **Service Layer**
    *   **Qué es**: Una capa que orquesta la lógica de negocio y las transacciones.
    *   **Cómo lo usas**: `CustomerService` actúa como la capa de servicio, implementando los casos de uso y conteniendo la lógica de negocio.

---

## 11. Patrones de Diseño en `account-service`

Este servicio utiliza todos los patrones de `customer-service` y añade los siguientes:

1.  **Client / Adapter (Comunicación entre Microservicios)**
    *   **Qué es**: Un patrón que encapsula la lógica de comunicación con un servicio externo.
    *   **Cómo lo usas**: `CustomerClientPort` es el puerto de salida que declara la necesidad de hablar con el `customer-service`. `CustomerClientAdapter` es el adaptador que implementa el puerto usando `WebClient` para hacer las llamadas HTTP reales, aislando al `AccountService` de la tecnología de red.

2.  **Patrones de Programación Reactiva (Project Reactor)**
    *   **Qué es**: Un paradigma centrado en flujos de datos asíncronos (`Mono` y `Flux`).
    *   **Cómo lo usas**:
        *   **Composición**: Encadenas operaciones con operadores como `.map` y `.flatMap`.
        *   **Bridge a JPA**: Usas `Mono.fromCallable(...).subscribeOn(Schedulers.boundedElastic())` para ejecutar código bloqueante (JPA) en hilos seguros, sin bloquear los hilos reactivos principales.
        *   **Manejo de Errores**: Usas `.switchIfEmpty(Mono.error(...))` para convertir resultados vacíos en errores de negocio y `.onErrorResume(...)` para manejar fallos de red con resiliencia.

3.  **Domain-Driven Design (DDD) - Modelo de Dominio Enriquecido**
    *   **Qué es**: Los objetos de dominio (`Account`, `Movement`) contienen no solo datos, sino también la lógica de negocio que les pertenece.
    *   **Cómo lo usas**:
        *   `Account.hasSufficientBalance(amount)`: La lógica para verificar el saldo está en la propia cuenta.
        *   `Movement.calculateNewBalance(...)`: La fórmula matemática está en el propio movimiento.
        *   Esto hace que el `MovementService` sea más legible y la lógica esté mejor encapsulada.

---

## 12. Preguntas Frecuentes y Conceptos Clave

1.  **¿Cómo se evita interferir en los flujos reactivos y no reactivos?**
    *   **Respuesta**: Usando el patrón "Bridge". Se envuelve el código bloqueante (JPA) en `Mono.fromCallable(...)` y se ejecuta en un pool de hilos separado y seguro para tareas bloqueantes con `.subscribeOn(Schedulers.boundedElastic())`. Esto libera los hilos reactivos principales.

2.  **¿Por qué usar `var`?**
    *   **Respuesta**: Para mejorar la legibilidad y reducir la verbosidad. `var` permite que el compilador infiera el tipo de una variable local. El código sigue siendo 100% fuertemente tipado. Es azúcar sintáctico.

3.  **¿Diferencia entre `.map` y `.flatMap`?**
    *   **`.map(T -> U)`**: Transforma el valor *dentro* del contenedor reactivo (`Mono<T>` -> `Mono<U>`). Se usa para transformaciones síncronas.
    *   **`.flatMap(T -> Mono<U>)`**: Transforma el valor en un *nuevo* contenedor reactivo. Se usa cuando la transformación es en sí misma una operación asíncrona que devuelve un `Mono` o `Flux`. Evita resultados anidados como `Mono<Mono<U>>`.

4.  **¿Qué es el Lenguaje Ubicuo?**
    *   **Respuesta**: Es un concepto de DDD. Consiste en crear un lenguaje común entre desarrolladores y expertos de negocio que se usa en las conversaciones y, crucialmente, en el código.
    *   **Ejemplo**: Si el negocio habla de "Saldo Insuficiente", el código tiene una excepción `InsufficientBalanceException` y un método `hasSufficientBalance()`.

5.  **¿Qué es `Mono.empty()` y `Mono.just()`?**
    *   **`Mono.just(item)`**: Crea un `Mono` que emite un único elemento y se completa. Es la forma reactiva de decir "aquí tienes el resultado".
    *   **`Mono.empty()`**: Crea un `Mono` que se completa exitosamente sin emitir nada. Es la forma reactiva de decir "la operación terminó y no se encontró nada", lo cual es un resultado válido (no un error).

6.  **¿Por qué nombrar variables con `final`?**
    *   **Respuesta**: Para garantizar la inmutabilidad y la seguridad. Una variable `final` solo puede ser asignada una vez. Cuando se usa en campos de una clase, obliga a su inicialización en el constructor, lo cual es perfecto para la inyección de dependencias y asegura que un objeto siempre se cree en un estado válido.

7.  **¿Para qué sirve `instanceof`?**
    *   **Respuesta**: Es un operador que comprueba si un objeto es de un tipo específico en tiempo de ejecución. Se usa para tratar un objeto de manera diferente según su tipo real, a menudo antes de un "casting" para evitar errores. Desde Java 16, soporta "Pattern Matching", que permite comprobar y declarar una variable del tipo específico en un solo paso, haciendo el código más limpio.

---

## 13. Guía de Estudio: Conceptos Clave y Preguntas Frecuentes

### **Conceptos Fundamentales de Java**

#### 1. Programación Orientada a Objetos (POO)
Los cuatro pilares son **Encapsulación, Abstracción, Herencia y Polimorfismo**.
*   **Encapsulación**: Ocultar el estado interno de un objeto y exponer su funcionalidad solo a través de métodos públicos.
    *   **En tu proyecto**: La clase `Account` tiene campos privados como `currentBalance`. No puedes modificar el saldo directamente. En su lugar, usas un método como `account.updateBalance(newBalance)`. El método `account.hasSufficientBalance(amount)` es un ejemplo perfecto: oculta la lógica de comparación y expone una pregunta de negocio simple.
*   **Abstracción**: Mostrar solo las características esenciales de un objeto, ocultando los detalles de implementación.
    *   **En tu proyecto**: Las interfaces de los puertos como `AccountUseCase` son una abstracción que define *qué* se puede hacer con las cuentas, pero no dice *cómo*. La implementación real está en `AccountService`.
*   **Herencia**: Permite que una clase (subclase) herede campos y métodos de otra (superclase).
    *   **En tu proyecto**: No usas herencia de forma explícita en tus modelos de dominio principales, lo cual es una decisión de diseño válida para mantener los modelos simples.
*   **Polimorfismo**: Permite que un objeto pueda tomar diferentes formas.
    *   **En tu proyecto**: Lo usas masivamente. El `AccountService` depende de `AccountPersistencePort` (una interfaz). En tiempo de ejecución, Spring le inyecta una instancia de `AccountPersistenceAdapter` (la implementación). El servicio no sabe ni le importa que la implementación usa JPA.

#### 2. Inmutabilidad vs. Mutabilidad
*   **¿Por qué String es inmutable?**: Por seguridad (evita cambios inesperados en contraseñas, rutas, etc.), seguridad en hilos (pueden ser compartidos sin riesgo) y optimización (permite el "String Pool" para reutilizar instancias).
*   **Inmutabilidad en tu código**: Usas `final` para todas tus dependencias inyectadas (ej. `private final AccountUseCase accountUseCase;` en `AccountController`). Esto es inmutabilidad a nivel de referencia.
    *   **Importancia**: Garantiza que la referencia a la dependencia no puede ser reasignada después de que el objeto es construido, haciendo tus clases más robustas y predecibles.

#### 3. Colecciones (Collections)
*   **ArrayList vs. LinkedList**: `ArrayList` es rápido para acceso por índice (`get(i)`). `LinkedList` es rápido para añadir/eliminar en medio de la lista.
*   **HashMap vs. Hashtable**: `HashMap` es más moderno, más rápido y no es sincronizado (no thread-safe). `Hashtable` es antiguo, más lento y sí es sincronizado (thread-safe). Generalmente se prefiere `ConcurrentHashMap` para concurrencia.

#### 4. Excepciones
*   **Checked vs. Unchecked**: `Checked Exceptions` deben ser manejadas o declaradas (`throws`), forzado por el compilador. `Unchecked Exceptions` (o `RuntimeExceptions`) no requieren manejo obligatorio.
*   **¿Por qué usas solo Runtime Exceptions?**: Es una práctica moderna para arquitecturas en capas. Evita acoplar las capas superiores a los detalles de error de las inferiores. Permite que la excepción "burbujee" hasta un manejador global (`GlobalExceptionHandler`), manteniendo el código intermedio limpio.

#### 5. Generics
*   **¿Qué problema resuelven?**: Proporcionan seguridad de tipos en tiempo de compilación, eliminando la necesidad de "casting" manual y previniendo `ClassCastException` en tiempo de ejecución.
*   **Wildcards (?) vs. Bounded Types (<T extends SomeClass>)**: `Wildcard (?)` significa "un tipo desconocido", útil para operaciones de solo lectura. `Bounded Types` restringen los tipos permitidos (`<T extends SomeClass>`), lo que te permite llamar a métodos de la clase base.

---

### **Conceptos de Spring y Arquitectura**

#### 6. Inversión de Control (IoC) y Dependency Injection (DI)
*   **IoC**: Es un principio donde el control sobre la creación y gestión de objetos se cede a un agente externo (el Contenedor de IoC).
*   **Contenedor de IoC de Spring**: Es el `ApplicationContext`. Escanea, crea y conecta tus Beans (`@Component`, `@Service`, etc.).
*   **Inyección por Constructor**: Es la mejor práctica porque garantiza que un objeto nunca puede ser creado sin sus dependencias, asegurando que siempre esté en un estado válido. La usas con `@RequiredArgsConstructor`.

#### 7. Beans y Componentes
Son estereotipos de `@Component` con propósitos semánticos:
*   `@Service`: Para la lógica de negocio.
*   `@Repository`: Para la persistencia de datos. Activa la traducción de excepciones.
*   `@RestController`: Para controladores de API REST.

#### 8. Spring WebFlux vs. WebMVC
*   **WebMVC (Bloqueante)**: Un hilo por petición. Si el hilo espera, se bloquea.
*   **WebFlux (No Bloqueante)**: Usa un "Event Loop" con pocos hilos. Un hilo inicia una operación y se libera. Cuando la operación termina, un hilo disponible continúa el trabajo. Es más eficiente en recursos.

#### 9. Mono y Flux
Son los publicadores de Project Reactor.
*   **`Mono`**: Flujo asíncrono de **0 o 1** elemento. Perfecto para operaciones que devuelven un único resultado (o ninguno). Ejemplo: `accountUseCase.createAccount(...)` devuelve `Mono<Account>`.
*   **`Flux`**: Flujo asíncrono de **0 a N** elementos. Ideal para operaciones que devuelven múltiples resultados. Ejemplo: `accountUseCase.findAll()` devuelve `Flux<Account>`.

#### 10. map vs. flatMap (Reactivo)
*   **`map`**: Transformación **síncrona** del elemento *dentro* del `Mono`/`Flux`.
*   **`flatMap`**: Transformación **asíncrona** que devuelve un *nuevo* `Mono`/`Flux`. Se usa para encadenar operaciones reactivas.

#### 11. Manejo de Operaciones Bloqueantes en WebFlux
*   **Problema**: JPA es bloqueante, WebFlux no. Mezclarlos directamente bloquea el Event Loop.
*   **Solución**: Usas el patrón "Bridge". En `AccountPersistenceAdapter`, envuelves la llamada a JPA en `Mono.fromCallable(...)` y la ejecutas en un hilo seguro con `.subscribeOn(Schedulers.boundedElastic())`.

#### 12. Arquitectura Limpia (Clean Architecture) / DDD
*   **Estructura**: Tu proyecto sigue la Regla de la Dependencia (hacia adentro).
    1.  **`domain`**: El núcleo. Modelos de negocio. No depende de nada.
    2.  **`application`**: Lógica y Puertos (`UseCase`, `Port`). Depende solo del `domain`.
    3.  **`infrastructure`**: Adaptadores (`Controller`, `PersistenceAdapter`). Implementa los puertos. Depende de `application`.
*   **Rol de los Puertos**: Invierten las dependencias, permitiendo que el núcleo sea independiente de la tecnología.

#### 13. API First (Contract First)
*   **Enfoque**: Diseñar el contrato de la API (`openapi.yaml`) *antes* de escribir la implementación.
*   **Cómo lo usas**: El `openapi-generator-maven-plugin` lee tu `.yaml` y genera las interfaces de la API (`AccountsApi`) y los DTOs. Tu controlador implementa la interfaz, garantizando el cumplimiento del contrato.

#### 14. Comunicación Inter-Servicios
*   **¿Por qué WebClient?**: Porque es el cliente HTTP **nativo, no bloqueante y reactivo** de Spring. Usar el antiguo `RestTemplate` (bloqueante) habría sido un anti-patrón en una aplicación WebFlux.

#### 15. Mappers
*   **Propósito**: Desacoplar las representaciones de datos entre capas (`API DTO` <-> `Domain Model` <-> `Persistence Entity`).
*   **Ventaja de MapStruct**: Genera la implementación en tiempo de compilación, eliminando código manual, repetitivo y propenso a errores.

#### 16. JPA y Hibernate
*   **Relación**: **JPA** es la especificación (las interfaces, como `@Entity`). **Hibernate** es la implementación que hace el trabajo.
*   **`ddl-auto: none`**: Configuración de seguridad crucial que le dice a Hibernate que **no** modifique el esquema de la base de datos. La gestión del esquema es tu responsabilidad (ej. con `schema.sql`).

#### 17. Transacciones
*   **Atomicidad**: Para asegurar que la actualización de `Account` y la inserción de `Movement` sean atómicas, la operación completa debe estar en una única transacción.
*   **Implementación**: En un entorno reactivo, la forma correcta es usar `TransactionalOperator` de Spring para envolver programáticamente la cadena reactiva. Tu uso actual de `.then()` asegura el orden, pero no la atomicidad transaccional entre las dos operaciones `save`.

#### 18. Logging
*   **Librerías**: Usas **SLF4J** como fachada (API) y **Logback** como implementación por defecto, gracias a Spring Boot.
*   **Propósito de SLF4J**: Te permite escribir código de logging sin acoplarte a una implementación específica.
*   **`@Slf4j`**: Anotación de Lombok que genera el campo `Logger` por ti.

#### 19. F3 (Validación de Saldo)
*   **Logro**: Creaste una excepción personalizada `InsufficientBalanceException`. En `MovementService`, cuando la validación falla, lanzas esta excepción (`Mono.error(new ...)`). El `GlobalExceptionHandler` la atrapa y genera la respuesta HTTP con el mensaje "Saldo no disponible".

#### 20. Manejo Global de Excepciones
*   **Intercepción**: Una clase anotada con `@RestControllerAdvice` intercepta excepciones de todos tus controladores.
*   **Traducción**: Métodos anotados con `@ExceptionHandler(ExceptionType.class)` atrapan tipos específicos de excepción y los traducen a una `ResponseEntity` con el DTO de error y el código de estado HTTP correcto (ej. 404 NOT FOUND para `AccountNotFoundException`).

#### 21. Pruebas Unitarias
*   **Propósito**: Verificar una única "unidad" de código (una clase) **en aislamiento**. Son rápidas.
*   **Diferencia con Pruebas de Integración**: Las pruebas de integración verifican que múltiples componentes funcionan juntos (ej. controlador -> servicio -> BD). Son más lentas.
*   **Mocking para `MovementService`**: Para probar `MovementService` unitariamente, usarías **Mockito** para crear "mocks" (objetos falsos) de sus dependencias (`AccountPersistencePort`, `MovementPersistencePort`) y así probar su lógica de orquestación sin una base de datos real.
