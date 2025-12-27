#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  Microservice Project Startup Script${NC}"
echo -e "${BLUE}========================================${NC}"

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}Error: Docker is not running${NC}"
    exit 1
fi

# Start Docker containers
echo -e "${YELLOW}Starting Docker containers...${NC}"
docker-compose up -d

# Wait for containers to be ready
echo -e "${YELLOW}Waiting for containers to start...${NC}"
sleep 10

# Check if Kafka is running
if docker ps | grep -q kafka; then
    echo -e "${GREEN}✓ Kafka is running${NC}"
else
    echo -e "${RED}✗ Kafka failed to start${NC}"
    exit 1
fi

# Check if Eureka is running
if docker ps | grep -q eureka; then
    echo -e "${GREEN}✓ Eureka Server is running${NC}"
else
    echo -e "${RED}✗ Eureka Server failed to start${NC}"
    exit 1
fi

# Build all services
echo -e "${YELLOW}Building all services...${NC}"
mvn clean package -DskipTests -T 1C

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ All services built successfully${NC}"
else
    echo -e "${RED}✗ Build failed${NC}"
    exit 1
fi

# Start services
echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}Starting services...${NC}"
echo -e "${YELLOW}Please open separate terminals to run:${NC}"
echo -e "${YELLOW}========================================${NC}"

echo ""
echo -e "${BLUE}Terminal 1:${NC} cd eureka-server && mvn spring-boot:run"
echo -e "${BLUE}Terminal 2:${NC} cd api-gateway && mvn spring-boot:run"
echo -e "${BLUE}Terminal 3:${NC} cd order-service && mvn spring-boot:run"
echo -e "${BLUE}Terminal 4:${NC} cd inventory-service && mvn spring-boot:run"
echo -e "${BLUE}Terminal 5:${NC} cd account-service && mvn spring-boot:run"
echo -e "${BLUE}Terminal 6:${NC} cd notification-service && mvn spring-boot:run"

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Docker containers started!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "Eureka Dashboard: ${BLUE}http://localhost:8761${NC}"
echo -e "API Gateway: ${BLUE}http://localhost:8080${NC}"
echo -e "Kafka: ${BLUE}localhost:9092${NC}"
echo ""
