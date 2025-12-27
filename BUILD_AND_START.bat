@echo off
REM Complete system build and startup script
REM Run this from the project root directory

setlocal enabledelayedexpansion
cd /d "%~dp0"

cls
echo.
echo ============================================================
echo   MICROSERVICE PLATFORM - COMPLETE BUILD AND STARTUP
echo ============================================================
echo.

REM Check prerequisites
echo [CHECK] Verifying prerequisites...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed. Download from https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed. Download from https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Node.js is not installed. Download from https://nodejs.org/
    pause
    exit /b 1
)

docker ps >nul 2>&1
if %errorlevel% neq 0 (
    echo WARNING: Docker is not running!
    echo Please ensure Docker Desktop is running before continuing.
    echo.
    pause
)

echo ✓ All prerequisites verified
echo.

REM Start builds
echo ============================================================
echo PHASE 1: Building Backend Services
echo ============================================================
echo.

set /a total_services=6
set /a current_service=0

REM Eureka Server
set /a current_service+=1
echo [!current_service!/!total_services!] Building Eureka Server...
cd eureka-server
call mvn clean package -DskipTests -q
if %errorlevel% neq 0 (
    echo ERROR: Eureka Server build failed!
    pause
    exit /b 1
)
echo ✓ Eureka Server built successfully
cd ..
echo.

REM API Gateway
set /a current_service+=1
echo [!current_service!/!total_services!] Building API Gateway...
cd api-gateway
call mvn clean package -DskipTests -q
if %errorlevel% neq 0 (
    echo ERROR: API Gateway build failed!
    pause
    exit /b 1
)
echo ✓ API Gateway built successfully
cd ..
echo.

REM Order Service
set /a current_service+=1
echo [!current_service!/!total_services!] Building Order Service...
cd order-service
call mvn clean package -DskipTests -q
if %errorlevel% neq 0 (
    echo ERROR: Order Service build failed!
    pause
    exit /b 1
)
echo ✓ Order Service built successfully
cd ..
echo.

REM Inventory Service
set /a current_service+=1
echo [!current_service!/!total_services!] Building Inventory Service...
cd inventory-service
call mvn clean package -DskipTests -q
if %errorlevel% neq 0 (
    echo ERROR: Inventory Service build failed!
    pause
    exit /b 1
)
echo ✓ Inventory Service built successfully
cd ..
echo.

REM Notification Service
set /a current_service+=1
echo [!current_service!/!total_services!] Building Notification Service...
cd notification-service
call mvn clean package -DskipTests -q
if %errorlevel% neq 0 (
    echo ERROR: Notification Service build failed!
    pause
    exit /b 1
)
echo ✓ Notification Service built successfully
cd ..
echo.

REM Accounting Service
set /a current_service+=1
echo [!current_service!/!total_services!] Building Accounting Service...
cd account-service
call mvn clean package -DskipTests -q
if %errorlevel% neq 0 (
    echo ERROR: Accounting Service build failed!
    pause
    exit /b 1
)
echo ✓ Accounting Service built successfully
cd ..
echo.

echo ============================================================
echo PHASE 2: Setting up Frontend
echo ============================================================
echo.

echo Installing npm dependencies...
cd frontend
call npm install -q --legacy-peer-deps
if %errorlevel% neq 0 (
    echo ERROR: npm install failed!
    pause
    exit /b 1
)
echo ✓ Frontend dependencies installed
cd ..
echo.

echo ============================================================
echo PHASE 3: Starting Docker Infrastructure
echo ============================================================
echo.

echo Starting Docker containers...
echo - Zookeeper
echo - Kafka
echo - Eureka Server
echo - MySQL (Order, Inventory, Accounting)
echo.

docker compose up -d
echo.
echo ✓ Docker containers started
echo   Note: Services may take 15-20 seconds to fully initialize...
echo.
timeout /t 5 /nobreak

echo ============================================================
echo PHASE 4: Starting Backend Services
echo ============================================================
echo.
echo Services will start in separate windows.
echo Start order matters - Eureka first, then Gateway, then others.
echo.

echo [1/7] Starting Eureka Server (port 8761)...
cd eureka-server
start "Eureka Server - port 8761" cmd /k "java -jar target/eureka-server-0.0.1-SNAPSHOT.jar & pause"
timeout /t 5 /nobreak
cd ..

echo [2/7] Starting API Gateway (port 8080)...
cd api-gateway
start "API Gateway - port 8080" cmd /k "java -jar target/api-gateway-0.0.1-SNAPSHOT.jar & pause"
timeout /t 3 /nobreak
cd ..

echo [3/7] Starting Order Service (port 8081)...
cd order-service
start "Order Service - port 8081" cmd /k "java -jar target/order-service-0.0.1-SNAPSHOT.jar & pause"
timeout /t 2 /nobreak
cd ..

echo [4/7] Starting Inventory Service (port 8082)...
cd inventory-service
start "Inventory Service - port 8082" cmd /k "java -jar target/inventory-service-0.0.1-SNAPSHOT.jar & pause"
timeout /t 2 /nobreak
cd ..

echo [5/7] Starting Notification Service (port 8083)...
cd notification-service
start "Notification Service - port 8083" cmd /k "java -jar target/notification-service-0.0.1-SNAPSHOT.jar & pause"
timeout /t 2 /nobreak
cd ..

echo [6/7] Starting Accounting Service (port 8084)...
cd account-service
start "Accounting Service - port 8084" cmd /k "java -jar target/account-service-0.0.1-SNAPSHOT.jar & pause"
timeout /t 2 /nobreak
cd ..

echo [7/7] Starting Frontend (port 5173)...
cd frontend
start "Frontend React - port 5173" cmd /k "npm run dev & pause"
timeout /t 2 /nobreak
cd ..

echo.
echo ============================================================
echo    ✓ ALL SERVICES STARTED SUCCESSFULLY!
echo ============================================================
echo.
echo.
echo ACCESS YOUR SYSTEM:
echo.
echo   Frontend:      http://localhost:5173
echo   API Gateway:   http://localhost:8080
echo   Eureka Server: http://localhost:8761
echo.
echo NEXT STEPS:
echo   1. Open http://localhost:5173 in your browser
echo   2. Check Eureka at http://localhost:8761 to verify all services
echo   3. Create orders and test the complete system
echo   4. Check service logs in the individual terminal windows
echo.
echo TROUBLESHOOTING:
echo   - If services don't appear in Eureka, wait a few seconds
echo   - Check individual service terminals for error messages
echo   - Ensure Docker Desktop is running (docker ps)
echo.

timeout /t 10

REM Open frontend in browser
start http://localhost:5173

echo.
echo System ready! Happy coding! 🚀
echo.
pause
