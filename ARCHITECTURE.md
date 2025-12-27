# Microservice Architecture

## Cấu Trúc Kiến Trúc
```
           [ API Gateway ]
                  |
      ____________________________
      |        |        |        |
   Order    Inventory  Accounting Notification
   Service  Service    Service    Service
      |        |        |        |
      |________|________|________|
              |
           Kafka
      (Message Broker)
              |
     [Eureka Server]
   (Service Discovery)
```

## Các Services

### 1. **Eureka Server** (Service Discovery)
- **Port:** 8761
- **Chức năng:** Quản lý service registration và discovery
- **Location:** http://localhost:8761

### 2. **API Gateway**
- **Port:** 8080
- **Chức năng:** Điểm vào duy nhất cho tất cả yêu cầu từ client
- **Routes:**
  - `/api/orders/**` → Order Service
  - `/api/inventory/**` → Inventory Service
  - `/api/accounting/**` → Accounting Service
  - `/api/notifications/**` → Notification Service

### 3. **Order Service**
- **Port:** 8081
- **Database:** order_db (MySQL, port 3306)
- **Chức năng:** Quản lý đơn hàng
- **Main Endpoints:**
  - `POST /api/orders` - Tạo đơn hàng
  - `GET /api/orders/{id}` - Lấy thông tin đơn hàng
  - `GET /api/orders` - Lấy tất cả đơn hàng
  - `GET /api/orders/customer/{customerId}` - Lấy đơn hàng của khách hàng
  - `PUT /api/orders/{id}` - Cập nhật đơn hàng
  - `DELETE /api/orders/{id}` - Xóa đơn hàng
- **Kafka Topics:** 
  - `order-events` (produce)

### 4. **Inventory Service**
- **Port:** 8082
- **Database:** inventory_db (MySQL, port 3307)
- **Chức năng:** Quản lý kho hàng
- **Main Endpoints:**
  - `POST /api/inventory` - Tạo kho hàng
  - `GET /api/inventory/{id}` - Lấy thông tin kho hàng
  - `GET /api/inventory/product/{productId}` - Lấy kho hàng theo sản phẩm
  - `GET /api/inventory` - Lấy tất cả kho hàng
  - `PUT /api/inventory/{id}` - Cập nhật kho hàng
  - `POST /api/inventory/reserve` - Đặt trữ hàng
  - `DELETE /api/inventory/{id}` - Xóa kho hàng
- **Kafka Topics:** 
  - `inventory-events` (produce)
  - `order-events` (consume)

### 5. **Accounting Service**
- **Port:** 8084
- **Database:** accounting_db (MySQL, port 3308)
- **Chức năng:** Quản lý giao dịch và tài chính
- **Main Endpoints:**
  - `POST /api/accounting/transactions` - Tạo giao dịch
  - `GET /api/accounting/transactions/{id}` - Lấy giao dịch
  - `GET /api/accounting/transactions` - Lấy tất cả giao dịch
  - `GET /api/accounting/orders/{orderId}/transactions` - Lấy giao dịch theo đơn hàng
  - `GET /api/accounting/customers/{customerId}/transactions` - Lấy giao dịch khách hàng
  - `PUT /api/accounting/transactions/{id}` - Cập nhật giao dịch
  - `DELETE /api/accounting/transactions/{id}` - Xóa giao dịch
- **Kafka Topics:**
  - `accounting-events` (produce)
  - `order-events` (consume)

### 6. **Notification Service**
- **Port:** 8083
- **Chức năng:** Gửi thông báo (email, SMS, etc.)
- **Main Endpoints:**
  - `POST /api/notifications` - Gửi thông báo
  - `GET /api/notifications/health` - Kiểm tra trạng thái
- **Kafka Topics:**
  - `order-events` (consume)
  - `inventory-events` (consume)
  - `accounting-events` (consume)

## Công Nghệ Sử Dụng

- **Spring Boot 3.3.2**
- **Spring Cloud 2023.0.1**
  - Spring Cloud Gateway
  - Spring Cloud Eureka (Service Discovery)
  - Spring Cloud LoadBalancer
- **Apache Kafka** (Message Broker)
- **MySQL 8.0** (Database)
- **Zookeeper** (Kafka coordination)
- **Java 17**
- **Maven**

## Hướng Dẫn Chạy

### Điều kiện tiên quyết
- Java 17+
- Maven 3.6+
- Docker & Docker Compose
- MySQL 8.0 (hoặc chạy qua Docker)

### 1. Khởi động Kafka, Zookeeper và Databases

```bash
docker-compose up -d
```

Điều này sẽ khởi động:
- Zookeeper (port 2181)
- Kafka (port 9092)
- Eureka Server (port 8761)
- MySQL cho Order Service (port 3306)
- MySQL cho Inventory Service (port 3307)
- MySQL cho Accounting Service (port 3308)

### 2. Kiểm tra Kafka đã chạy
```bash
docker ps
```

### 3. Xây dựng tất cả services
```bash
# Build Eureka Server
cd eureka-server
mvn clean package -DskipTests
cd ..

# Build API Gateway
cd api-gateway
mvn clean package -DskipTests
cd ..

# Build Order Service
cd order-service
mvn clean package -DskipTests
cd ..

# Build Inventory Service
cd inventory-service
mvn clean package -DskipTests
cd ..

# Build Accounting Service
cd account-service
mvn clean package -DskipTests
cd ..

# Build Notification Service
cd notification-service
mvn clean package -DskipTests
cd ..
```

### 4. Chạy các services (mở terminal riêng cho mỗi service)

#### Terminal 1 - Eureka Server:
```bash
cd eureka-server
mvn spring-boot:run
```

#### Terminal 2 - API Gateway:
```bash
cd api-gateway
mvn spring-boot:run
```

#### Terminal 3 - Order Service:
```bash
cd order-service
mvn spring-boot:run
```

#### Terminal 4 - Inventory Service:
```bash
cd inventory-service
mvn spring-boot:run
```

#### Terminal 5 - Accounting Service:
```bash
cd account-service
mvn spring-boot:run
```

#### Terminal 6 - Notification Service:
```bash
cd notification-service
mvn spring-boot:run
```

## API Examples

### 1. Tạo đơn hàng
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "totalAmount": 150.00
  }'
```

### 2. Tạo kho hàng
```bash
curl -X POST http://localhost:8080/api/inventory \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 100
  }'
```

### 3. Đặt trữ hàng
```bash
curl -X POST "http://localhost:8080/api/inventory/reserve?productId=1&quantity=10"
```

### 4. Tạo giao dịch
```bash
curl -X POST http://localhost:8080/api/accounting/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "customerId": 1,
    "amount": 150.00,
    "transactionType": "DEBIT",
    "description": "Payment for order"
  }'
```

### 5. Gửi thông báo
```bash
curl -X POST "http://localhost:8080/api/notifications?recipientEmail=test@example.com&subject=Test&message=Hello"
```

## Theo dõi các Services

### Eureka Dashboard
http://localhost:8761

### Kafka Topics
```bash
# Liệt kê các topics
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092

# Theo dõi messages từ topic
docker exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic order-events
```

## Event Flow

1. **Order Created**: Order Service → Kafka (order-events) → Inventory Service, Accounting Service, Notification Service
2. **Inventory Reserved**: Inventory Service → Kafka (inventory-events) → Notification Service
3. **Transaction Created**: Accounting Service → Kafka (accounting-events) → Notification Service
4. **Notification Sent**: Notification Service nhận các events từ các services khác

## Dừng Services

### Dừng Docker containers
```bash
docker-compose down
```

### Dừng các services (Ctrl+C trong terminal)
- API Gateway
- Order Service
- Inventory Service
- Accounting Service
- Notification Service
- Eureka Server

## Troubleshooting

### 1. Kafka không kết nối được
```bash
# Kiểm tra Kafka đã chạy
docker ps | grep kafka

# Kiểm tra logs
docker logs kafka
```

### 2. MySQL connection error
```bash
# Kiểm tra MySQL containers
docker ps | grep mysql

# Check MySQL logs
docker logs mysql-order
docker logs mysql-inventory
docker logs mysql-accounting
```

### 3. Services không đăng ký với Eureka
- Kiểm tra Eureka Server đã chạy trên port 8761
- Kiểm tra `eureka.client.service-url.defaultZone` trong `application.properties`
- Kiểm tra network connectivity

## Monitoring

### Health Check Endpoints
```bash
# API Gateway
curl http://localhost:8080/actuator

# Order Service
curl http://localhost:8081/actuator

# Inventory Service
curl http://localhost:8082/actuator

# Accounting Service
curl http://localhost:8084/actuator

# Notification Service
curl http://localhost:8083/api/notifications/health
```

## Ghi chú

- Mỗi service có database riêng biệt (Database per Service pattern)
- Kafka được sử dụng cho async communication giữa các services
- Eureka Server quản lý service registration và discovery
- API Gateway là điểm vào duy nhất cho tất cả client requests
- Notification Service là một event listener cho các sự kiện từ các services khác

## Phát triển tiếp theo

1. **Authentication & Authorization**: Thêm Spring Security
2. **Distributed Tracing**: Thêm Spring Cloud Sleuth + Zipkin
3. **Circuit Breaker**: Thêm Resilience4j
4. **API Documentation**: Thêm Swagger/OpenAPI
5. **Monitoring**: Thêm Prometheus + Grafana
6. **Logging**: Thêm ELK Stack (Elasticsearch, Logstash, Kibana)
7. **Configuration Management**: Thêm Spring Cloud Config Server
