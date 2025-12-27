# Microservice Project - README

## Tổng Quan

Đây là một dự án **Banking/E-commerce Microservice Architecture** được xây dựng với Spring Boot, Spring Cloud và Apache Kafka.

## Kiến Trúc Hệ Thống

```
           [ API Gateway - Port 8080 ]
                  |
      ____________________________
      |        |        |        |
   Order    Inventory  Accounting Notification
   Service  Service    Service    Service
  (8081)    (8082)     (8084)     (8083)
      |        |        |        |
      |________|________|________|
              |
           Kafka
      (Message Broker)
      Port: 9092
```

## Cấu Trúc Thư Mục

```
microservice_project-02/
├── api-gateway/              # API Gateway (Spring Cloud Gateway)
├── eureka-server/            # Eureka Server (Service Discovery)
├── order-service/            # Order Service (Đơn hàng)
├── inventory-service/        # Inventory Service (Kho hàng)
├── account-service/          # Accounting Service (Kế toán)
├── notification-service/     # Notification Service (Thông báo)
├── docker-compose.yml        # Docker Compose file
├── ARCHITECTURE.md           # Chi tiết kiến trúc
└── Microservice-API.postman_collection.json  # Postman Collection
```

## Các Services

| Service | Port | Database | Chức Năng |
|---------|------|----------|---------|
| Eureka Server | 8761 | - | Service Discovery & Registration |
| API Gateway | 8080 | - | Điểm vào duy nhất |
| Order Service | 8081 | order_db | Quản lý đơn hàng |
| Inventory Service | 8082 | inventory_db | Quản lý kho hàng |
| Accounting Service | 8084 | accounting_db | Quản lý giao dịch |
| Notification Service | 8083 | - | Gửi thông báo |

## Công Nghệ

- **Java 17**
- **Spring Boot 3.3.2**
- **Spring Cloud 2023.0.1**
  - Spring Cloud Gateway
  - Spring Cloud Eureka
  - Spring Cloud LoadBalancer
- **Apache Kafka 7.5.0** (Message Broker)
- **MySQL 8.0** (Database)
- **Zookeeper** (Kafka coordination)
- **Docker & Docker Compose**

## Yêu Cầu

- Java 17 trở lên
- Maven 3.6+
- Docker & Docker Compose
- cURL hoặc Postman (để test API)

## Hướng Dẫn Chạy

### 1. Khởi động Infrastructure (Kafka, Databases, Eureka)

```bash
docker-compose up -d
```

### 2. Build tất cả services

```bash
mvn clean package -DskipTests -T 1C
```

Hoặc build từng service:

```bash
# Eureka Server
cd eureka-server && mvn clean package -DskipTests && cd ..

# API Gateway
cd api-gateway && mvn clean package -DskipTests && cd ..

# Order Service
cd order-service && mvn clean package -DskipTests && cd ..

# Inventory Service
cd inventory-service && mvn clean package -DskipTests && cd ..

# Accounting Service
cd account-service && mvn clean package -DskipTests && cd ..

# Notification Service
cd notification-service && mvn clean package -DskipTests && cd ..
```

### 3. Chạy các services

Mở 6 terminal riêng biệt và chạy lệnh:

#### Terminal 1: Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
```

#### Terminal 2: API Gateway
```bash
cd api-gateway
mvn spring-boot:run
```

#### Terminal 3: Order Service
```bash
cd order-service
mvn spring-boot:run
```

#### Terminal 4: Inventory Service
```bash
cd inventory-service
mvn spring-boot:run
```

#### Terminal 5: Accounting Service
```bash
cd account-service
mvn spring-boot:run
```

#### Terminal 6: Notification Service
```bash
cd notification-service
mvn spring-boot:run
```

### 4. Kiểm tra Services

#### Eureka Dashboard
```
http://localhost:8761
```

#### Health Check
```bash
curl http://localhost:8080/actuator/health
```

## API Examples

### Tạo Đơn Hàng
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "totalAmount": 150.00
  }'
```

### Tạo Kho Hàng
```bash
curl -X POST http://localhost:8080/api/inventory \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 100
  }'
```

### Lấy Tất Cả Đơn Hàng
```bash
curl http://localhost:8080/api/orders
```

### Tạo Giao Dịch
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

### Gửi Thông Báo
```bash
curl -X POST "http://localhost:8080/api/notifications?recipientEmail=test@example.com&subject=Test&message=Hello"
```

## Kafka Monitoring

### Liệt kê Topics
```bash
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092
```

### Xem Messages từ Topic
```bash
docker exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic order-events --from-beginning
```

## Dừng Services

### Dừng Docker containers
```bash
docker-compose down
```

### Dừng các services
Nhấn `Ctrl+C` trong mỗi terminal

## Postman Collection

Import file `Microservice-API.postman_collection.json` vào Postman để dễ dàng test tất cả API endpoints.

## Event Flow

1. **Order Created** → Order Service gửi event qua Kafka
2. **Inventory Updated** → Inventory Service nhận event từ Order, gửi event qua Kafka
3. **Transaction Created** → Accounting Service nhận event từ Order, tạo transaction
4. **Notification Sent** → Notification Service nhận tất cả events và gửi thông báo

## Troubleshooting

### Lỗi: Connection refused
- Kiểm tra Docker containers: `docker ps`
- Kiểm tra logs: `docker logs <container_name>`

### Lỗi: Service not found
- Kiểm tra Eureka: http://localhost:8761
- Đảm bảo services đã đăng ký với Eureka

### Lỗi: Kafka connection error
- Kiểm tra Kafka container: `docker logs kafka`
- Restart Kafka: `docker restart kafka`

## Tài Liệu Chi Tiết

Xem file `ARCHITECTURE.md` để biết thêm chi tiết về cấu trúc, endpoints, và cách phát triển tiếp theo.

## Liên Hệ & Hỗ Trợ

- Spring Boot: https://spring.io/projects/spring-boot
- Spring Cloud: https://spring.io/projects/spring-cloud
- Apache Kafka: https://kafka.apache.org/
- Docker: https://www.docker.com/

---

**Happy Coding! 🚀**
