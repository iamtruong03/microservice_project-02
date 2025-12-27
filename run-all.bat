@echo off
REM Main startup script for both Backend and Frontend

echo.
echo ============================================
echo  Microservice Platform - Complete Startup
echo ============================================
echo.

REM Check if Docker is installed
docker --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Docker is not installed or not in PATH
    pause
    exit /b 1
)

echo [1/4] Starting Docker containers (Kafka, Zookeeper, MySQL, Eureka)...
docker-compose up -d

REM Wait for services to start
echo Waiting for services to initialize...
timeout /t 15

REM Check if Kafka is running
docker ps | find "kafka" >nul
if errorlevel 1 (
    echo ERROR: Kafka failed to start
    pause
    exit /b 1
)
echo ✓ Docker containers are running

echo.
echo [2/4] Building backend services...
echo This may take 2-3 minutes...
call mvn clean package -DskipTests -T 1C

if errorlevel 1 (
    echo ERROR: Build failed
    pause
    exit /b 1
)
echo ✓ Backend services built successfully

echo.
echo [3/4] Frontend setup...
cd frontend
echo Installing npm dependencies...
call npm install

if errorlevel 1 (
    echo ERROR: npm install failed
    cd ..
    pause
    exit /b 1
)
echo ✓ Frontend dependencies installed

cd ..

echo.
echo ============================================
echo  READY TO START SERVICES
echo ============================================
echo.
echo To run the complete system, open 7 separate terminal windows and run:
echo.
echo Terminal 1 - Eureka Server:
echo   cd eureka-server ^&^& mvn spring-boot:run
echo.
echo Terminal 2 - API Gateway:
echo   cd api-gateway ^&^& mvn spring-boot:run
echo.
echo Terminal 3 - Order Service:
echo   cd order-service ^&^& mvn spring-boot:run
echo.
echo Terminal 4 - Inventory Service:
echo   cd inventory-service ^&^& mvn spring-boot:run
echo.
echo Terminal 5 - Accounting Service:
echo   cd account-service ^&^& mvn spring-boot:run
echo.
echo Terminal 6 - Notification Service:
echo   cd notification-service ^&^& mvn spring-boot:run
echo.
echo Terminal 7 - Frontend:
echo   cd frontend ^&^& npm run dev
echo.
echo ============================================
echo  ACCESS POINTS
echo ============================================
echo Frontend:        http://localhost:5173
echo API Gateway:     http://localhost:8080
echo Eureka:          http://localhost:8761
echo.
echo Press any key to continue...
pause
