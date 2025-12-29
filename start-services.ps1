# PowerShell script to start all microservices in order
# Make sure Eureka Server is running first at localhost:8761

$baseDir = "d:\Project_spring\microservice_project-02"

# Define services in startup order
$services = @(
    @{name="auth-service"; port=8086},
    @{name="order-service"; port=8081},
    @{name="inventory-service"; port=8085},
    @{name="account-service"; port=8084},
    @{name="notification-service"; port=8083},
    @{name="api-gateway"; port=8080}
)

Write-Host "Starting microservices..." -ForegroundColor Green
Write-Host "Make sure Eureka Server is running at localhost:8761" -ForegroundColor Yellow
Write-Host ""

foreach ($service in $services) {
    $serviceName = $service.name
    $servicePort = $service.port
    $jarPath = "$baseDir\$serviceName\target\$serviceName-0.0.1-SNAPSHOT.jar"
    
    if (Test-Path $jarPath) {
        Write-Host "Starting $serviceName on port $servicePort..." -ForegroundColor Green
        Start-Process -WindowStyle Normal -FilePath "java" -ArgumentList "-jar `"$jarPath`"" -NoNewWindow
        Start-Sleep -Seconds 8
        Write-Host "$serviceName started. Waiting for registration..." -ForegroundColor Cyan
    } else {
        Write-Host "JAR not found for $serviceName at $jarPath" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "All services started! Check Eureka dashboard at http://localhost:8761" -ForegroundColor Green
