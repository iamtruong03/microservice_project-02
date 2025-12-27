@echo off
REM Windows batch script to start Docker containers

echo ========================================
echo  Microservice Project Startup Script
echo ========================================
echo.

REM Check if Docker is running
docker ps >nul 2>&1
if errorlevel 1 (
    echo Error: Docker is not running
    pause
    exit /b 1
)

REM Start Docker containers
echo Starting Docker containers...
docker-compose up -d

REM Wait for containers to start
echo Waiting for containers to start...
timeout /t 10

REM Check if Kafka is running
docker ps | find "kafka" >nul
if errorlevel 1 (
    echo Error: Kafka failed to start
    pause
    exit /b 1
) else (
    echo Kafka is running
)

REM Build all services
echo.
echo Building all services...
call mvn clean package -DskipTests -T 1C

if errorlevel 1 (
    echo Build failed
    pause
    exit /b 1
)

echo.
echo ========================================
echo All services built successfully!
echo ========================================
echo.
echo Please open separate Command Prompt/PowerShell windows and run:
echo.
echo Terminal 1: cd eureka-server ^&^& mvn spring-boot:run
echo Terminal 2: cd api-gateway ^&^& mvn spring-boot:run
echo Terminal 3: cd order-service ^&^& mvn spring-boot:run
echo Terminal 4: cd inventory-service ^&^& mvn spring-boot:run
echo Terminal 5: cd account-service ^&^& mvn spring-boot:run
echo Terminal 6: cd notification-service ^&^& mvn spring-boot:run
echo.
echo Eureka Dashboard: http://localhost:8761
echo API Gateway: http://localhost:8080
echo.
pause
