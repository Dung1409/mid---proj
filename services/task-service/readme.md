# task-service

task-service là service điều phối saga checkout. Service đọc dữ liệu giỏ hàng, **validate các món ăn qua menu-service**, tạo đơn, gọi thanh toán, cập nhật trạng thái đơn và trả về trạng thái xử lý theo `requestId`.

## Yêu cầu

- Java 21
- Maven
- Chạy trong Docker Compose

## Cấu hình môi trường

Service đọc cấu hình từ biến môi trường:

- `SERVER_PORT` - mặc định `5000`
- `ORDER_SERVICE_URL` - mặc định `http://order-service:5000`
- `PAYMENT_SERVICE_URL` - mặc định `http://payment-service:5000`
- `CART_SERVICE_URL` - mặc định `http://cart-service:5000`
- `MENU_SERVICE_URL` - mặc định `http://menu-service:5000`

Trong [docker-compose.yml](../../docker-compose.yml), service này được map ra host ở port `5005`.

## Chạy local

Từ thư mục gốc project:

```bash
docker compose up --build task-service cart-service order-service payment-service menu-service
```

Nếu muốn build bằng Maven:

```bash
cd services/task-service
mvn spring-boot:run
```

## API

### Health check

`GET /health`

Trả về:

```json
{ "status": "ok" }
```

### Khởi tạo checkout

`POST /task/checkout`

Request mẫu:

```json
{
  "phone": "0900000000",
  "address": "123 Nguyen Trai"
}
```

Response thành công là `202 Accepted`:

```json
{
  "requestId": "uuid",
  "status": "ORDER_SUBMITTED",
  "message": "Order is being processed"
}
```

Nếu thanh toán thất bại, status có thể là `PAYMENT_FAILED`.

### Tra cứu trạng thái checkout

`GET /task/status/{requestId}`

Response trả về trạng thái hiện tại của request trong saga.

## Quy trình checkout (Saga Orchestration)

1. **Nhận request** - Kiểm tra request hợp lệ
2. **Lấy dữ liệu giỏ hàng** - Gọi cart-service để lấy danh sách các món
3. **Validate với menu** - Gọi menu-service để xác nhấn những món còn có sẵn cho phép thêm vào đơn không
4. **Tạo đơn hàng** - Gọi order-service tạo đơn với các món đã validate
5. **Xử lý thanh toán** - Gọi payment-service để xử lý thanh toán với tổng giá từ giỏ hàng
6. **Cập nhật trạng thái đơn**:
   - Nếu thanh toán thành công → trạng thái `PAID`
   - Nếu thanh toán thất bại → trạng thái `PAYMENT_FAILED`
7. **Clear giỏ hàng** - Xóa giỏ hàng sau khi thanh toán thành công
8. **Trả về kết quả** - Trả về `requestId` và trạng thái final cho client

## Ghi chú

- API spec: [docs/api-specs/task-service.yaml](../../docs/api-specs/task-service.yaml)
- Service này không dùng database riêng; trạng thái request được lưu trong bộ nhớ RAM.
- Service phụ thuộc vào `cart-service`, `order-service`, và `payment-service` qua Docker Compose DNS.
