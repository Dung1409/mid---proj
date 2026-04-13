# API Gateway

## Overview

The API Gateway serves as the single entry point for all client requests. It routes incoming requests to the appropriate backend microservice.

## Responsibilities

- **Request routing**: Forward requests to the correct service
- **Load balancing**: Distribute traffic (if applicable)
- **Authentication**: Validate tokens/credentials (optional)
- **Rate limiting**: Protect services from overload (optional)
- **CORS handling**: Allow frontend cross-origin requests
- **Request/Response transformation**: Modify headers, paths as needed

## Tech Stack

| Component | Choice |
| --------- | ------ |
| Approach  | Nginx  |

## Routing Table

| External Path    | Target Service  | Internal URL                    |
| ---------------- | --------------- | ------------------------------- |
| `/api/menu/*`    | Menu Service    | `http://menu-service:5000/*`    |
| `/api/cart/*`    | Cart Service    | `http://cart-service:5000/*`    |
| `/api/order/*`   | Order Service   | `http://order-service:5000/*`   |
| `/api/payment/*` | Payment Service | `http://payment-service:5000/*` |
| `/api/task/*`    | Task Service    | `http://task-service:5000/*`    |

## Running

```bash
# From project root
docker compose up gateway --build
```

## Health Check

```bash
curl http://localhost:8080/health
```

Expected response:

```json
{ "status": "ok" }
```

## Route Test

```bash
curl http://localhost:8080/api/menu/health
curl http://localhost:8080/api/cart/health
curl http://localhost:8080/api/order/health
curl http://localhost:8080/api/payment/health
curl http://localhost:8080/api/task/health
```

## Configuration

The gateway uses Docker Compose networking. Services are accessible by their
service names defined in `docker-compose.yml`.

## Notes

- Use service names (not `localhost`) for upstream URLs inside Docker
- The gateway exposes port 8080 to the host
