# PowerShell script to start all microservices

# Colors for output
function Write-Success { Write-Host $args -ForegroundColor Green }
function Write-Error { Write-Host $args -ForegroundColor Red }
function Write-Info { Write-Host $args -ForegroundColor Cyan }
function Write-Warning { Write-Host $args -ForegroundColor Yellow }

Write-Host ""
Write-Info "============================================"
Write-Info "  Microservice Platform - Complete Startup"
Write-Info "============================================"
Write-Host ""

# Get project root
$projectRoot = Get-Location
Write-Info "Project Root: $projectRoot"

# Check Java
Write-Info "Checking Java..."
java -version 2>&1 | Select-Object -First 1
if ($LASTEXITCODE -ne 0) {
    Write-Error "ERROR: Java is not installed"
    exit 1
}
Write-Success "✓ Java is installed"
Write-Host ""

# Check Maven
Write-Info "Checking Maven..."
mvn -v 2>&1 | Select-Object -First 1
if ($LASTEXITCODE -ne 0) {
    Write-Error "ERROR: Maven is not installed"
    exit 1
}
Write-Success "✓ Maven is installed"
Write-Host ""

# Check Node.js
Write-Info "Checking Node.js..."
node -v
if ($LASTEXITCODE -ne 0) {
    Write-Error "ERROR: Node.js is not installed"
    exit 1
}
Write-Success "✓ Node.js is installed"
Write-Host ""

# Check Docker
Write-Warning "NOTE: Docker Desktop must be running separately!"
Write-Info "Please ensure Docker is installed and running."
Write-Info "You can check with: docker ps"
Write-Host ""

# Build step
Write-Info "============================================"
Write-Info "STEP 1: Building Backend Services"
Write-Info "============================================"
Write-Host ""

Write-Warning "Building (this may take 2-3 minutes)..."
$startTime = Get-Date
mvn clean package -DskipTests -T 1C
if ($LASTEXITCODE -ne 0) {
    Write-Error "ERROR: Build failed!"
    exit 1
}
$endTime = Get-Date
$duration = ($endTime - $startTime).TotalSeconds
Write-Success "✓ Build completed in $([Math]::Round($duration, 2)) seconds"
Write-Host ""

# Frontend setup
Write-Info "============================================"
Write-Info "STEP 2: Setting up Frontend"
Write-Info "============================================"
Write-Host ""

Set-Location "$projectRoot\frontend"
Write-Info "Installing npm dependencies..."
npm install 2>&1 | Select-Object -Last 3
if ($LASTEXITCODE -ne 0) {
    Write-Error "ERROR: npm install failed!"
    Set-Location $projectRoot
    exit 1
}
Write-Success "✓ Frontend dependencies installed"
Set-Location $projectRoot
Write-Host ""

# Docker containers
Write-Info "============================================"
Write-Info "STEP 3: Docker Containers"
Write-Info "============================================"
Write-Host ""

Write-Warning "Make sure Docker Desktop is running!"
Write-Info "Starting Docker containers..."
Write-Info "This will start: Zookeeper, Kafka, MySQL (3x), Eureka"
Write-Host ""
Write-Warning "Press any key to continue (or Ctrl+C to cancel)..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

docker compose up -d
Write-Host ""
Write-Success "✓ Docker containers started"
Write-Info "Containers may take 15-20 seconds to fully initialize..."
Write-Host ""

# Display instructions
Write-Host ""
Write-Info "============================================"
Write-Info "  READY TO START SERVICES"
Write-Info "============================================"
Write-Host ""
Write-Success "All backend services are built!"
Write-Success "Frontend dependencies are installed!"
Write-Success "Docker containers are running!"
Write-Host ""

Write-Warning "To start the complete system, open 7 separate PowerShell windows:"
Write-Host ""

Write-Host "Terminal 1 - Eureka Server:" -ForegroundColor Yellow
Write-Host "  cd $projectRoot\eureka-server"
Write-Host "  java -jar target/eureka-server-0.0.1-SNAPSHOT.jar"
Write-Host ""

Write-Host "Terminal 2 - API Gateway:" -ForegroundColor Yellow
Write-Host "  cd $projectRoot\api-gateway"
Write-Host "  java -jar target/api-gateway-0.0.1-SNAPSHOT.jar"
Write-Host ""

Write-Host "Terminal 3 - Order Service:" -ForegroundColor Yellow
Write-Host "  cd $projectRoot\order-service"
Write-Host "  java -jar target/order-service-0.0.1-SNAPSHOT.jar"
Write-Host ""

Write-Host "Terminal 4 - Inventory Service:" -ForegroundColor Yellow
Write-Host "  cd $projectRoot\inventory-service"
Write-Host "  java -jar target/inventory-service-0.0.1-SNAPSHOT.jar"
Write-Host ""

Write-Host "Terminal 5 - Accounting Service:" -ForegroundColor Yellow
Write-Host "  cd $projectRoot\account-service"
Write-Host "  java -jar target/account-service-0.0.1-SNAPSHOT.jar"
Write-Host ""

Write-Host "Terminal 6 - Notification Service:" -ForegroundColor Yellow
Write-Host "  cd $projectRoot\notification-service"
Write-Host "  java -jar target/notification-service-0.0.1-SNAPSHOT.jar"
Write-Host ""

Write-Host "Terminal 7 - Frontend:" -ForegroundColor Yellow
Write-Host "  cd $projectRoot\frontend"
Write-Host "  npm run dev"
Write-Host ""

Write-Info "============================================"
Write-Info "  ACCESS POINTS"
Write-Info "============================================"
Write-Success "Frontend:      http://localhost:5173"
Write-Success "API Gateway:   http://localhost:8080"
Write-Success "Eureka:        http://localhost:8761"
Write-Host ""

Write-Warning "After all services are started, you can:"
Write-Info "  1. View Eureka Dashboard: http://localhost:8761"
Write-Info "  2. Access Frontend: http://localhost:5173"
Write-Info "  3. Test APIs through the UI"
Write-Host ""

Write-Success "Setup complete! Happy coding! 🚀"
Write-Host ""
