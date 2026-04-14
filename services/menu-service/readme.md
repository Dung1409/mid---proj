# menu-service

menu-service là service cung cấp danh sách món ăn cho hệ thống. Service chạy bằng Spring Boot, sử dụng MySQL để lưu dữ liệu menu và expose API qua API Gateway.

## Yêu cầu

- Java 21
- Maven
- MySQL 8+
- Chạy trong Docker Compose

## Cấu hình môi trường

Service đọc cấu hình từ biến môi trường:

- `SERVER_PORT` - mặc định `5000`
- `DB_HOST` - mặc định `menu-db`
- `DB_PORT` - mặc định `3306`
- `DB_NAME` - mặc định `menu_db`
- `DB_USER` - mặc định `admin`
- `DB_PASSWORD` - mặc định `changeme`

Trong [docker-compose.yml](../../docker-compose.yml), service này được map ra host ở port `5001`.

## Chạy local

Từ thư mục gốc project:

```bash
docker compose up --build menu-service menu-db
```

Nếu muốn build bằng Maven:

```bash
cd services/menu-service
mvn spring-boot:run
```

## API

### Health check

`GET /health`

Trả về:

```json
{ "status": "ok" }
```

### Lấy danh sách món ăn

`GET /menu/items`

Response mẫu:

```json
{
  "items": [
    { "id": 1, "name": "Pho Bo", "price": 45000 },
    { "id": 2, "name": "Com Ga", "price": 40000 }
  ]
}
```

Nếu không có dữ liệu, service trả về `400` với thông báo lỗi.

### Xác thực các item trong menu

`POST /menu/validate`

Request mẫu:

```json
{
  "itemIds": [1, 2, 3]
}
```

Response nếu hợp lệ (200):

```json
{
  "valid": true,
  "items": [
    { "id": 1, "name": "Pho Bo", "price": 45000 },
    { "id": 2, "name": "Com Ga", "price": 40000 },
    { "id": 3, "name": "Banh Mi", "price": 25000 }
  ]
}
```

Response nếu có item không hợp lệ (400):

```json
{
  "error": "Invalid items",
  "invalidIds": [99, 100]
}
```

Endpoint này được task-service gọi trong quá trình checkout saga để xác nhận các món trong giỏ hàng còn hợp lệ.

## Ghi chú

- Khi bảng rỗng, service tự seed sẵn một vài món mặc định khi khởi động.
- API spec: [docs/api-specs/menu-service.yaml](../../docs/api-specs/menu-service.yaml)
