# payment-service

payment-service mô phỏng xử lý thanh toán và lưu kết quả thanh toán vào MySQL. Kết quả thanh toán hiện tại là ngẫu nhiên `SUCCESS` hoặc `FAILED`.

## Yêu cầu

- Java 21
- Maven
- MySQL 8+
- Chạy trong Docker Compose

## Cấu hình môi trường

Service đọc cấu hình từ biến môi trường:

- `SERVER_PORT` - mặc định `5000`
- `DB_HOST` - mặc định `payment-db`
- `DB_PORT` - mặc định `3306`
- `DB_NAME` - mặc định `payment_db`
- `DB_USER` - mặc định `admin`
- `DB_PASSWORD` - mặc định `changeme`

Trong [docker-compose.yml](../../docker-compose.yml), service này được map ra host ở port `5004`.

## Chạy local

Từ thư mục gốc project:

```bash
docker compose up --build payment-service payment-db
```

Nếu muốn build bằng Maven:

```bash
cd services/payment-service
mvn spring-boot:run
```

## API

### Health check

`GET /health`

Trả về:

```json
{ "status": "ok" }
```

### Xử lý thanh toán

`POST /payments`

Request mẫu:

```json
{
  "orderId": "order-uuid",
  "amount": 45000
}
```

Response thành công:

```json
{ "status": "SUCCESS" }
```

Hoặc:

```json
{ "status": "FAILED" }
```

## Ghi chú

- API spec: [docs/api-specs/payment-service.yaml](../../docs/api-specs/payment-service.yaml)
- task-service sử dụng service này trong flow checkout
