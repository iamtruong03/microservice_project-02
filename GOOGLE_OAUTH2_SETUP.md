# Google OAuth2 Login Setup Guide

## Giới Thiệu
Hệ thống đã được cấu hình để hỗ trợ đăng nhập bằng Google OAuth2. Hướng dẫn này sẽ giúp bạn thiết lập Google OAuth2 credentials.

## Bước 1: Tạo Google Cloud Project

1. Truy cập [Google Cloud Console](https://console.cloud.google.com/)
2. Tạo một project mới:
   - Click "Select a Project" → "NEW PROJECT"
   - Nhập tên: `Banking Microservice`
   - Click "CREATE"

3. Chọn project vừa tạo từ dropdown

## Bước 2: Enable Google+ API

1. Vào "APIs & Services" → "Library"
2. Tìm "Google+ API"
3. Click vào nó → "ENABLE"

## Bước 3: Tạo OAuth2 Credentials

1. Vào "APIs & Services" → "Credentials"
2. Click "Create Credentials" → "OAuth client ID"
3. Nếu được hỏi "Configure the OAuth consent screen":
   - Chọn "External"
   - Click "CREATE"
   - Điền thông tin:
     - **App name**: Banking Microservice
     - **User support email**: your-email@gmail.com
     - **Developer contact**: your-email@gmail.com
   - Click "SAVE AND CONTINUE" → "SAVE AND CONTINUE" → "BACK TO DASHBOARD"

4. Click "Create Credentials" → "OAuth client ID" lần nữa
5. Chọn Application Type: **Web application**
6. Điền Authorized redirect URIs:
   ```
   http://localhost:8086/login/oauth2/code/google
   http://localhost:8086/oauth2/callback
   ```
7. Click "CREATE"
8. Copy **Client ID** và **Client Secret**

## Bước 4: Cấu Hình Auth Service

1. Mở file: `auth-service/src/main/resources/application.properties`
2. Tìm phần `# OAuth2 Google Configuration`
3. Thay thế:
   ```properties
   spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
   spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
   ```
   Với Client ID và Secret bạn vừa copy

4. Lưu file

## Bước 5: Thêm CORS Configuration (nếu frontend khác domain)

Nếu frontend chạy trên domain khác, cần thêm CORS:

```java
// Trong SecurityConfig.java
@Bean
public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:3001")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
        }
    };
}
```

## Bước 6: Khởi Chạy Ứng Dụng

1. Build auth-service:
   ```bash
   cd auth-service
   mvn clean package -DskipTests
   ```

2. Khởi động auth-service:
   ```bash
   java -jar target/auth-service-0.0.1-SNAPSHOT.jar
   ```

3. Khởi động frontend:
   ```bash
   cd frontend
   npm start
   ```

## Bước 7: Kiểm Tra Chức Năng

1. Truy cập `http://localhost:3000`
2. Click vào nút "Đăng Nhập Với Google"
3. Bạn sẽ được chuyển đến trang đăng nhập Google
4. Sau khi đăng nhập thành công, bạn sẽ được chuyển hướng về ứng dụng với JWT token

## Các File Đã Thay Đổi

### Backend (Auth Service)
- **pom.xml**: Thêm `spring-boot-starter-oauth2-client` dependency
- **SecurityConfig.java**: Cấu hình OAuth2 login
- **OAuth2SuccessHandler.java**: Handler xử lý đăng nhập thành công
- **OAuth2Controller.java**: REST endpoints cho OAuth2
- **application.properties**: Google OAuth2 credentials

### Frontend
- **LoginPage.jsx**: 
  - Thêm Google login button
  - Xử lý OAuth2 callback từ backend
  - Lưu token vào localStorage

## Workflow Đăng Nhập Bằng Google

```
User clicks "Đăng Nhập Với Google"
    ↓
Frontend redirects to: http://localhost:8086/oauth2/authorization/google
    ↓
User authenticates with Google (nếu chưa login)
    ↓
Google redirects to callback: http://localhost:8086/login/oauth2/code/google
    ↓
Backend OAuth2SuccessHandler:
  1. Kiểm tra user có tồn tại trong DB không
  2. Nếu chưa có → tạo user mới + publish Kafka event
  3. Generate JWT token
  4. Redirect to: http://localhost:3000/login?token=xxx&email=xxx
    ↓
Frontend nhận token từ URL params
    ↓
Lưu token vào localStorage
    ↓
Redirect to home page (/)
    ↓
Login successful ✅
```

## Troubleshooting

### Lỗi: "redirect_uri_mismatch"
- **Nguyên nhân**: Redirect URI không khớp với cấu hình Google
- **Giải pháp**: Kiểm tra lại `Authorized redirect URIs` trong Google Cloud Console

### Lỗi: "invalid_client"
- **Nguyên nhân**: Client ID hoặc Client Secret sai
- **Giải pháp**: Copy lại chính xác từ Google Cloud Console

### Người dùng không thể login
- **Kiểm tra**:
  1. Auth service có chạy trên cổng 8086 không?
  2. Google credentials có được cấu hình đúng không?
  3. Database có kết nối được không?

## Tài Liệu Tham Khảo

- [Spring Security OAuth2 Documentation](https://spring.io/projects/spring-security-oauth)
- [Google OAuth2 Setup Guide](https://developers.google.com/identity/protocols/oauth2)
- [Spring Boot OAuth2 Example](https://spring.io/guides/tutorials/spring-boot-oauth2/)
