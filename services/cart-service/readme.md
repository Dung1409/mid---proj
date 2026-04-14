# cart-service

cart-service quản lý giỏ hàng. Service lưu trạng thái giỏ hàng trong MySQL để task-service sử dụng trong quy trình checkout saga.

## Yêu cầu

- Java 21
- Maven
- MySQL 8+
- Chạy trong Docker Compose

## Cấu hình môi trường

Service đọc cấu hình từ biến môi trường:

- `SERVER_PORT` - mặc định `5000`
- `DB_HOST` - mặc định `cart-db`
- `DB_PORT` - mặc định `3306`
- `DB_NAME` - mặc định `cart_db`
- `DB_USER` - mặc định `admin`
- `DB_PASSWORD` - mặc định `changeme`

Trong [docker-compose.yml](../../docker-compose.yml), service này được map ra host ở port `5002`.

## Chạy local

Từ thư mục gốc project:

```bash
docker compose up --build cart-service cart-db task-service
```

Nếu muốn build bằng Maven:

```bash
cd services/cart-service
mvn spring-boot:run
```

## API

### Health check

`GET /health`

Trả về:

```json
{ "status": "ok" }
```

### Giỏ hàng

- `POST /cart/items` - thêm item vào giỏ
- `GET /cart` - xem giỏ hàng và tổng tiền
- `PUT /cart/items/{itemId}` - cập nhật số lượng item
- `DELETE /cart/items/{itemId}` - xóa item khỏi giỏ
- `DELETE /cart/clear` - xóa toàn bộ giỏ hàng

## Ghi chú

- API spec: [docs/api-specs/cart-service.yaml](../../docs/api-specs/cart-service.yaml)
- Checkout entrypoint cho client nằm ở task-service: `POST /task/checkout`
