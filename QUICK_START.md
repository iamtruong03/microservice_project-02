# Quick Start Guide

## 🚀 Bắt đầu nhanh trong 5 bước

### 1️⃣ Khởi động Infrastructure

```bash
docker-compose up -d
```

Điều này sẽ khởi động:
- ✅ Zookeeper
- ✅ Kafka
- ✅ Eureka Server (http://localhost:8761)
- ✅ 3x MySQL Databases

**Kiểm tra:**
```bash
docker ps
```

### 2️⃣ Build tất cả services

**Option A: Build song song (nhanh hơn)**
```bash
mvn clean package -DskipTests -T 1C
```

**Option B: Build tuần tự**
```bash
cd eureka-server && mvn clean package -DskipTests && cd ..
cd api-gateway && mvn clean package -DskipTests && cd ..
cd order-service && mvn clean package -DskipTests && cd ..
cd inventory-service && mvn clean package -DskipTests && cd ..
cd account-service && mvn clean package -DskipTests && cd ..
cd notification-service && mvn clean package -DskipTests && cd ..
```

### 3️⃣ Chạy các services

**Mở 6 terminal (hoặc 6 tabs) và chạy:**

```bash
# Terminal 1
cd eureka-server && mvn spring-boot:run

# Terminal 2
cd api-gateway && mvn spring-boot:run

# Terminal 3
cd order-service && mvn spring-boot:run

# Terminal 4
cd inventory-service && mvn spring-boot:run

# Terminal 5
cd account-service && mvn spring-boot:run

# Terminal 6
cd notification-service && mvn spring-boot:run
```

### 4️⃣ Kiểm tra services

```bash
# Eureka Dashboard - Xem tất cả registered services
open http://localhost:8761

# API Gateway Health
curl http://localhost:8080/actuator/health
```

### 5️⃣ Test API

```bash
# 1. Tạo Inventory
curl -X POST http://localhost:8080/api/inventory \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 100}'

# 2. Tạo Order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId": 1, "totalAmount": 150.00}'

# 3. Đặt trữ hàng
curl -X POST "http://localhost:8080/api/inventory/reserve?productId=1&quantity=10"

# 4. Tạo giao dịch
curl -X POST http://localhost:8080/api/accounting/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "customerId": 1,
    "amount": 150.00,
    "transactionType": "DEBIT",
    "description": "Payment"
  }'

# 5. Gửi thông báo
curl -X POST "http://localhost:8080/api/notifications?recipientEmail=test@example.com&subject=Test&message=Hello"
```

## 📊 Monitoring

### Eureka Dashboard
```
http://localhost:8761
```

### Kafka Monitoring

```bash
# List topics
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092

# Monitor order-events
docker exec kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic order-events \
  --from-beginning
```

### Database Access

```bash
# Connect to Order Database
docker exec -it mysql-order mysql -u root -proot order_db

# SQL: Select all orders
SELECT * FROM orders;
```

## 🛑 Dừng tất cả

```bash
# Dừng Docker containers
docker-compose down

# Hoặc dừng từng service (Ctrl+C trong mỗi terminal)
```

## 🔧 Troubleshooting

### Services không khởi động
```bash
# Check logs
docker logs kafka
docker logs eureka-server
docker logs mysql-order

# Restart
docker-compose restart
```

### Port đang được sử dụng
```bash
# Windows: Find process using port 8080
netstat -ano | findstr :8080

# macOS/Linux: Find process using port 8080
lsof -i :8080
```

### Kafka connection error
```bash
# Restart Kafka
docker-compose down
docker-compose up -d kafka zookeeper

# Wait 30 seconds
sleep 30

# Check if Kafka is running
docker logs kafka | tail -20
```

## 📚 Tài liệu

- **README.md** - Tổng quan dự án
- **ARCHITECTURE.md** - Chi tiết kiến trúc
- **Microservice-API.postman_collection.json** - Postman Collection

## 🎯 Endpoints Quick Reference

| Service | Port | Base URL |
|---------|------|----------|
| API Gateway | 8080 | http://localhost:8080 |
| Order Service | 8081 | http://localhost:8081 |
| Inventory Service | 8082 | http://localhost:8082 |
| Notification Service | 8083 | http://localhost:8083 |
| Accounting Service | 8084 | http://localhost:8084 |
| Eureka Server | 8761 | http://localhost:8761 |

## 💡 Tips

1. **Use Postman** - Import `Microservice-API.postman_collection.json` để test dễ dàng hơn
2. **Monitor logs** - Dùng `docker logs -f <container>` để theo dõi real-time
3. **Scale services** - Dễ dàng scale horizontal bằng Docker
4. **Database backups** - Docker volumes tự động lưu data

## ❓ FAQ

**Q: Tại sao Eureka không show services?**
A: Services cần 1-2 phút để đăng ký. Refresh page http://localhost:8761

**Q: Tôi thay đổi code, cần restart không?**
A: Có, cần rebuild (`mvn clean package`) và restart service

**Q: Kafka có mất data khi restart?**
A: Có (development mode). Dùng Docker volume hoặc external storage cho production

**Q: Có thể chạy trên 1 machine không?**
A: Có, nhưng cần 6+ GB RAM. Hoặc dùng `docker-compose scale`

---

**Chúc bạn thành công! 🎉**
