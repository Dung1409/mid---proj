# order-service

order-service quản lý đơn hàng. Service tạo đơn mới, cập nhật trạng thái đơn, và lưu dữ liệu trong MySQL.

## Yêu cầu

- Java 21
- Maven
- MySQL 8+
- Chạy trong Docker Compose

## Cấu hình môi trường

Service đọc cấu hình từ biến môi trường:

- `SERVER_PORT` - mặc định `5000`
- `DB_HOST` - mặc định `order-db`
- `DB_PORT` - mặc định `3306`
- `DB_NAME` - mặc định `order_db`
- `DB_USER` - mặc định `admin`
- `DB_PASSWORD` - mặc định `changeme`

Trong [docker-compose.yml](../../docker-compose.yml), service này được map ra host ở port `5003`.

## Chạy local

Từ thư mục gốc project:

```bash
docker compose up --build order-service order-db
```

Nếu muốn build bằng Maven:

```bash
cd services/order-service
mvn spring-boot:run
```

## API

### Health check

`GET /health`

Trả về:

```json
{ "status": "ok" }
```

### Tạo đơn hàng

`POST /orders`

Request mẫu:

```json
{
  "items": [{ "id": 1, "name": "Pho Bo", "price": 45000 }],
  "total": 45000
}
```

Response thành công trả về `201 Created`:

```json
{
  "orderId": "uuid",
  "status": "CREATED"
}
```

### Cập nhật trạng thái đơn

`PUT /orders/{orderId}/status`

Request mẫu:

```json
{ "status": "PAID" }
```

## Ghi chú

- API spec: [docs/api-specs/order-service.yaml](../../docs/api-specs/order-service.yaml)
- task-service gọi API này để tạo đơn và cập nhật trạng thái trong saga checkout
