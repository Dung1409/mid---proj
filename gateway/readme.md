# API Gateway

API Gateway là điểm vào duy nhất cho toàn bộ request từ frontend hoặc client bên ngoài. Gateway dùng Nginx để định tuyến request đến đúng microservice phía sau.

## Vai trò

- Định tuyến request đến đúng service
- Xử lý CORS cho frontend chạy local
- Chuyển tiếp path `/api/*` sang các backend service tương ứng
- Cung cấp endpoint health riêng cho gateway

## Công nghệ

| Thành phần | Lựa chọn |
| ---------- | -------- |
| Gateway    | Nginx    |

## Bảng định tuyến

| External path    | Service đích    | Internal URL                    |
| ---------------- | --------------- | ------------------------------- |
| `/api/menu/*`    | Menu Service    | `http://menu-service:5000/*`    |
| `/api/cart/*`    | Cart Service    | `http://cart-service:5000/*`    |
| `/api/order/*`   | Order Service   | `http://order-service:5000/*`   |
| `/api/payment/*` | Payment Service | `http://payment-service:5000/*` |
| `/api/task/*`    | Task Service    | `http://task-service:5000/*`    |

Các path không có dấu `/` cuối như `/api/menu` sẽ được chuyển hướng sang `/api/menu/`.

## Cấu hình chạy

Gateway lắng nghe bên trong container ở port `8000`. Trong [docker-compose.yml](../docker-compose.yml), port này được map ra host là `8080`.

## Chạy local

Từ thư mục gốc project:

```bash
docker compose up --build gateway
```

Nếu muốn chạy cùng toàn bộ hệ thống:

```bash
docker compose up --build
```

## Health check

```bash
curl http://localhost:8080/health
```

Response:

```json
{ "status": "ok" }
```

## Kiểm tra route

```bash
curl http://localhost:8080/api/menu/health
curl http://localhost:8080/api/cart/health
curl http://localhost:8080/api/order/health
curl http://localhost:8080/api/payment/health
curl http://localhost:8080/api/task/health
```

## CORS

Gateway bật CORS cơ bản để phục vụ frontend local development:

- `Access-Control-Allow-Origin: *`
- `Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS`
- `Access-Control-Allow-Headers: Authorization, Content-Type, X-Requested-With`

Request `OPTIONS` sẽ được trả về `204` trực tiếp.

## Ghi chú

- Gateway chỉ dùng Docker Compose DNS để gọi service nội bộ, không dùng `localhost`.
- API health của gateway được expose tại `GET /health`.
