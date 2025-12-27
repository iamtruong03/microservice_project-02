@echo off
REM Unified startup script for all services
setlocal enabledelayedexpansion

cd /d "%~dp0"
set PROJECT_ROOT=%cd%

echo.
echo ============================================
echo    Microservice Platform - Full Startup
echo ============================================
echo.

REM Check if Docker is running
echo Checking Docker...
docker ps >nul 2>&1
if %errorlevel% neq 0 (
    echo WARNING: Docker is not running!
    echo Please start Docker Desktop first.
    echo.
    pause
)

REM Build and run everything
echo.
echo Starting all services...
echo.

REM Eureka Server (wait for it to start)
echo.
echo [1/7] Starting Eureka Server (port 8761)...
cd "%PROJECT_ROOT%\eureka-server"
start "Eureka Server" cmd /k "java -jar target/eureka-server-0.0.1-SNAPSHOT.jar"
timeout /t 3 /nobreak

REM API Gateway
echo [2/7] Starting API Gateway (port 8080)...
cd "%PROJECT_ROOT%\api-gateway"
start "API Gateway" cmd /k "java -jar target/api-gateway-0.0.1-SNAPSHOT.jar"
timeout /t 2 /nobreak

REM Order Service
echo [3/7] Starting Order Service (port 8081)...
cd "%PROJECT_ROOT%\order-service"
start "Order Service" cmd /k "java -jar target/order-service-0.0.1-SNAPSHOT.jar"
timeout /t 2 /nobreak

REM Inventory Service
echo [4/7] Starting Inventory Service (port 8082)...
cd "%PROJECT_ROOT%\inventory-service"
start "Inventory Service" cmd /k "java -jar target/inventory-service-0.0.1-SNAPSHOT.jar"
timeout /t 2 /nobreak

REM Notification Service
echo [5/7] Starting Notification Service (port 8083)...
cd "%PROJECT_ROOT%\notification-service"
start "Notification Service" cmd /k "java -jar target/notification-service-0.0.1-SNAPSHOT.jar"
timeout /t 2 /nobreak

REM Accounting Service
echo [6/7] Starting Accounting Service (port 8084)...
cd "%PROJECT_ROOT%\account-service"
start "Accounting Service" cmd /k "java -jar target/account-service-0.0.1-SNAPSHOT.jar"
timeout /t 2 /nobreak

REM Frontend
echo [7/7] Starting Frontend (port 5173)...
cd "%PROJECT_ROOT%\frontend"
start "Frontend React" cmd /k "npm run dev"
timeout /t 2 /nobreak

echo.
echo ============================================
echo    All Services Started!
echo ============================================
echo.
echo Frontend:      http://localhost:5173
echo API Gateway:   http://localhost:8080
echo Eureka:        http://localhost:8761
echo.
echo Waiting for services to initialize...
timeout /t 5 /nobreak

REM Try to open frontend in browser
start http://localhost:5173

echo.
echo Services are starting in separate windows.
echo You can monitor each service's logs in its terminal.
echo.
pause
