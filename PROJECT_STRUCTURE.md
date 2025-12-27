# Project Structure Summary

## 📁 Thư mục dự án

```
microservice_project-02/
│
├── 📂 eureka-server/                    # Service Discovery Server
│   ├── pom.xml
│   ├── src/main/java/com/example/eurekaserver/
│   │   └── EurekaServerApplication.java
│   └── src/main/resources/
│       └── application.properties
│
├── 📂 api-gateway/                      # API Gateway (Spring Cloud Gateway)
│   ├── pom.xml
│   ├── src/main/java/com/example/apigateway/
│   │   └── ApiGatewayApplication.java
│   └── src/main/resources/
│       └── application.properties
│
├── 📂 order-service/                    # Order Microservice
│   ├── pom.xml
│   ├── src/main/java/com/example/orderservice/
│   │   ├── OrderServiceApplication.java
│   │   ├── controller/
│   │   │   └── OrderController.java
│   │   ├── service/
│   │   │   └── OrderService.java
│   │   ├── repository/
│   │   │   └── OrderRepository.java
│   │   ├── model/
│   │   │   └── Order.java
│   │   └── dto/
│   │       └── OrderDTO.java
│   └── src/main/resources/
│       └── application.properties
│
├── 📂 inventory-service/                # Inventory Microservice
│   ├── pom.xml
│   ├── src/main/java/com/example/inventoryservice/
│   │   ├── InventoryServiceApplication.java
│   │   ├── controller/
│   │   │   └── InventoryController.java
│   │   ├── service/
│   │   │   └── InventoryService.java
│   │   ├── repository/
│   │   │   └── InventoryRepository.java
│   │   ├── model/
│   │   │   └── Inventory.java
│   │   └── dto/
│   │       └── InventoryDTO.java
│   └── src/main/resources/
│       └── application.properties
│
├── 📂 account-service/                  # Accounting Microservice
│   ├── pom.xml
│   ├── src/main/java/com/example/accountservice/
│   │   ├── AccountServiceApplication.java
│   │   ├── controller/
│   │   │   └── TransactionController.java
│   │   ├── service/
│   │   │   └── TransactionService.java
│   │   ├── repository/
│   │   │   └── TransactionRepository.java
│   │   ├── model/
│   │   │   └── Transaction.java
│   │   └── dto/
│   │       └── TransactionDTO.java
│   └── src/main/resources/
│       └── application.properties
│
├── 📂 notification-service/             # Notification Microservice
│   ├── pom.xml
│   ├── src/main/java/com/example/notificationservice/
│   │   ├── NotificationServiceApplication.java
│   │   ├── controller/
│   │   │   └── NotificationController.java
│   │   ├── service/
│   │   │   └── NotificationService.java
│   │   ├── model/
│   │   │   └── Notification.java
│   │   └── event/
│   │       └── OrderEvent.java
│   └── src/main/resources/
│       └── application.properties
│
├── 📂 user-service/                     # User Service (existing)
│   ├── pom.xml
│   ├── docker-compose.yml
│   ├── Dockerfile
│   ├── k8s/
│   │   └── deployment.yaml
│   └── ...
│
├── 📂 auth-service/                     # Auth Service (existing)
│   ├── pom.xml
│   └── ...
│
├── 📂 transaction-service/              # Transaction Service (existing)
│   ├── pom.xml
│   └── ...
│
├── 📄 pom.xml                           # Parent POM
├── 📄 docker-compose.yml                # Docker Compose (Kafka, Zookeeper, MySQL, Eureka)
│
├── 📄 README.md                         # Hướng dẫn chính
├── 📄 ARCHITECTURE.md                   # Chi tiết kiến trúc
├── 📄 QUICK_START.md                    # Quick start guide
├── 📄 HELP.md                           # Tài liệu ban đầu
│
├── 📄 start.sh                          # Bash script để startup (Linux/macOS)
├── 📄 start.bat                         # Batch script để startup (Windows)
│
├── 📄 Microservice-API.postman_collection.json  # Postman Collection
│
├── 📂 .git/                             # Git repository
├── 📂 .github/                          # GitHub configuration
├── 📂 .mvn/                             # Maven wrapper
├── 📂 target/                           # Build output
│
├── .gitignore
└── .gitattributes
```

## 🔧 Mô tả từng Service

### 1. **Eureka Server** 🔍
- **Port:** 8761
- **Chức năng:** Service Discovery & Registration
- **Main Class:** `EurekaServerApplication`
- **Key Config:** 
  - `eureka.client.register-with-eureka=false`
  - `eureka.client.fetch-registry=false`

### 2. **API Gateway** 🚪
- **Port:** 8080
- **Chức năng:** Entry point cho tất cả client requests
- **Main Class:** `ApiGatewayApplication`
- **Routes:**
  - `/api/orders/**` → Order Service (8081)
  - `/api/inventory/**` → Inventory Service (8082)
  - `/api/accounting/**` → Accounting Service (8084)
  - `/api/notifications/**` → Notification Service (8083)

### 3. **Order Service** 📦
- **Port:** 8081
- **Database:** MySQL (order_db:3306)
- **Main Class:** `OrderServiceApplication`
- **Key Components:**
  - `OrderController` - REST endpoints
  - `OrderService` - Business logic + Kafka producer
  - `OrderRepository` - Data access
  - `Order` model - JPA entity
  - `OrderDTO` - Data transfer object
- **Kafka Topics:** `order-events` (produce)

### 4. **Inventory Service** 📊
- **Port:** 8082
- **Database:** MySQL (inventory_db:3307)
- **Main Class:** `InventoryServiceApplication`
- **Key Components:**
  - `InventoryController` - REST endpoints + reservation logic
  - `InventoryService` - Business logic + Kafka producer/consumer
  - `InventoryRepository` - Data access
  - `Inventory` model - JPA entity
  - `InventoryDTO` - Data transfer object
- **Kafka Topics:** `inventory-events` (produce), `order-events` (consume)

### 5. **Accounting Service** 💰
- **Port:** 8084
- **Database:** MySQL (accounting_db:3308)
- **Main Class:** `AccountServiceApplication`
- **Key Components:**
  - `TransactionController` - REST endpoints
  - `TransactionService` - Business logic + Kafka producer/consumer
  - `TransactionRepository` - Data access
  - `Transaction` model - JPA entity
  - `TransactionDTO` - Data transfer object
- **Kafka Topics:** `accounting-events` (produce), `order-events` (consume)

### 6. **Notification Service** 📧
- **Port:** 8083
- **Database:** None (in-memory)
- **Main Class:** `NotificationServiceApplication`
- **Key Components:**
  - `NotificationController` - REST endpoints
  - `NotificationService` - Business logic + Kafka consumer
  - `Notification` model - Data class
  - `OrderEvent` - Event model
- **Kafka Topics:** `order-events`, `inventory-events`, `accounting-events` (all consume)

## 📦 Key Dependencies

### All Services:
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.kafka</groupId>
  <artifactId>spring-kafka</artifactId>
</dependency>
```

### Data-Driven Services (Order, Inventory, Accounting):
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
</dependency>
```

### API Gateway:
```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

### Eureka Server:
```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

## 🐳 Docker Compose Services

```yaml
- zookeeper:2181
- kafka:9092
- eureka-server:8761
- mysql-order:3306 (database: order_db)
- mysql-inventory:3307 (database: inventory_db)
- mysql-accounting:3308 (database: accounting_db)
```

## 📋 Database Schema

### Order Service (order_db)
```sql
CREATE TABLE orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_id BIGINT NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  status VARCHAR(50) NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
```

### Inventory Service (inventory_db)
```sql
CREATE TABLE inventory (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id BIGINT UNIQUE NOT NULL,
  quantity INT NOT NULL,
  reserved_quantity INT DEFAULT 0,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
```

### Accounting Service (accounting_db)
```sql
CREATE TABLE transactions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  customer_id BIGINT NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  transaction_type VARCHAR(50) NOT NULL,
  status VARCHAR(50) NOT NULL,
  description VARCHAR(255),
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
```

## 🔄 Event Flow

```
Order Service           Inventory Service         Accounting Service       Notification Service
     |                       |                           |                         |
     +-- (order created) -------> Kafka: order-events ---+                         |
     |                       |                           |                         |
     |                  process event            (listen) |                         |
     |                       |                     create transaction              |
     |                       +-- (inventory reserved) --> Kafka: accounting-events-|
     |                                                    |                         |
     |                                                    |                    (listen all topics)
     |                                                    |                         |
     +-- (inventory updated) --> Kafka: inventory-events-|--------send notification-|
     |                                                    |                         |
```

## 🔐 Security Considerations

- ✅ Database isolation per service
- ✅ Async communication via Kafka
- ⚠️ TODO: Add authentication (Spring Security)
- ⚠️ TODO: Add API rate limiting
- ⚠️ TODO: Add encryption for Kafka messages

## 📈 Performance Tuning

- CPU: 4+ cores recommended
- RAM: 8+ GB recommended
- Network: High bandwidth for Kafka
- Disk: SSDs for databases

## 🚀 Future Enhancements

- [ ] Spring Cloud Config Server
- [ ] Distributed Tracing (Sleuth + Zipkin)
- [ ] Circuit Breaker (Resilience4j)
- [ ] API Documentation (Swagger/OpenAPI)
- [ ] Monitoring (Prometheus + Grafana)
- [ ] Logging (ELK Stack)
- [ ] Container Orchestration (Kubernetes)
- [ ] Multi-region deployment
- [ ] Caching (Redis)
- [ ] Message Queue Backup

---

**Last Updated:** December 2024
