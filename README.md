# Core User microservice

## About The Project

The microservice application processes operations with users and user payment cards

## Environment variables

| Variable                   | Type    | Desription                                                         |
|----------------------------|---------|--------------------------------------------------------------------| 
| `USER_SERVICE_DB_NAME`     | String  | Database schema name                                               |
| `USER_SERVICE_DB_PORT`     | String  | Database port                                                      |
| `USER_SERVICE_DB_USERNAME` | String  | Database user name                                                 |
| `USER_SERVICE_DB_PASSWORD` | String  | Database user password                                             |
| `SHOW_SQL`                 | Boolean | Not mandatory parameter to allow show sql queries in debug only    |
| `CARD_SECRET_KEY`          | Boolean | 32 symblos secret password to encrypt/dercypt payment card numbers |
| `REDIS_SECRET_PASSWORD`    | String  | Redis password                                                     |
| `KAFKA_HOST_POST`          | String  | Kafka host:port                                                    |


## Tables

### Table `users`

| Column       | Type         | Constraints |
|--------------|--------------|-------------| 
| `id`         | UUID         | NOT NULL    |
| `name`       | VARCHAR(50)  | NOT NULL    |
| `surname`    | VARCHAR(50)  | NOT NULL    |
| `birth_date` | DATE         | NOT NULL    |
| `email`      | VARCHAR(100) | NOT NULL    |
| `active`     | BOOLEAN      | NOT NULL    |
| `created_at` | TIMESTAMP    | NOT NULL    |
| `updated_at` | TIMESTAMP    | NOT NULL    |


### Table `payment_cards`

| Column            | Type         | Constraints |
|-------------------|--------------|-------------| 
| `id`              | UUID         | NOT NULL    |
| `user_id`         | UUID         | NOT NULL    |
| `number`          | VARCHAR(255) | NOT NULL    |
| `expiration_date` | DATE         | NOT NULL    |
| `active`          | BOOLEAN      | NOT NULL    |
| `created_at`      | TIMESTAMP    | NOT NULL    |
| `updated_at`      | TIMESTAMP    | NOT NULL    |

Indexes

* idx_payment_cards_user_id payment_cards(user_id)

## Local debug

Before debugging create .env file with project properties.

The property file example:

USER_SERVICE_PORT=8080

USER_SERVICE_DB_NAME=user_db
USER_SERVICE_DB_USERNAME=Username
USER_SERVICE_DB_PASSWORD=Password
USER_SERVICE_DB_PORT=5432

KAFKA_HOST_POST=localhost:9092

SHOW_SQL=true

CARD_SECRET_KEY=gsvhnjkblunbgfjvbcgvnhrfxcvmbjhn  
REDIS_SECRET_PASSWORD=redis_password

## Application docker-compose

docker-compose example

```yaml
services:
  postgres:
    image: postgres:18-alpine
    container_name: postgres_container
    environment:
      - POSTGRES_DB=${USER_SERVICE_DB_NAME}
      - POSTGRES_USER=${USER_SERVICE_DB_USERNAME}
      - POSTGRES_PASSWORD=${USER_SERVICE_DB_PASSWORD}
      - PGDATA=/var/lib/postgresql/data/pgdata
    ports:
      - "${USER_SERVICE_DB_PORT}:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U $$POSTGRES_USER -d $$POSTGRES_DB"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: always

  redis:
    image: redis:8.8-alpine
    container_name: microservice-redis
    ports:
      - "6379:6379"
    command: redis-server --requirepass ${REDIS_SECRET_PASSWORD}
    volumes:
      - redis_data:/data
    restart: always

  core-user-service:
    image: ghcr.io/yuryshakhau1/core-user-service:latest
    ports:
      - "8080:8080"
    environment:
      - USER_SERVICE_PORT=${USER_SERVICE_PORT}
      - USER_SERVICE_DB_NAME=${USER_SERVICE_DB_NAME}
      - USER_SERVICE_DB_PORT=${USER_SERVICE_DB_PORT}
      - USER_SERVICE_DB_USERNAME=${USER_SERVICE_DB_USERNAME}
      - USER_SERVICE_DB_PASSWORD=${USER_SERVICE_DB_PASSWORD}
      - REDIS_SECRET_PASSWORD=${REDIS_SECRET_PASSWORD}
      - CARD_SECRET_KEY=${CARD_SECRET_KEY}
      - KAFKA_HOST_POST=${KAFKA_HOST_POST}

volumes:
  postgres_data:
  redis_data:
```

##  REST endpoints

The base URL for all API endpoints.

### Authentication
All requests must include a Bearer token in the HTTP Authorization header:
`Authorization: Bearer <your_access_token>`

## User REST endpoints

---

### 1. Create new user.

POST /users  
`Authorization: Bearer <your_access_token>` with ADMIN role  
`Content-Type: application/json`  

```json
{ 
  "firstName": "<user_first_name>", 
  "lastName": "<user_last_name>",
  "birthDate": "<user_birth_date> in yyyy-MM-dd format",
  "email": "<user_email>",
  "active": "<true_or_false_user_status>"
}
```

* **Success (201 Created):**
```json
{
  "id": "<user_id_uuid>",
  "firstName": "<user_first_name>",
  "lastName": "<user_last_name>",
  "birthDate": "<user_birth_date> in yyyy-MM-dd format",
  "email": "<user_email>",
  "active": "<true_or_false_user_status>",
  "tempPassword": "<user_temp_password> user must change in to have capability of receiving access_token"
}
```

---

### 2. Get current user info.

GET /users/me  
`Authorization: Bearer <your_access_token>`  

* **Success (200):**
```json
{
  "id": "<user_id_uuid>",
  "firstName": "<user_first_name>",
  "lastName": "<user_last_name>",
  "birthDate": "<user_birth_date> in yyyy-MM-dd format",
  "email": "<user_email>",
  "active": "<true_or_false_user_status>"
}
```

---

### 3. Get user info by id.

GET /users/{id}  
`Authorization: Bearer <your_access_token>` with ADMIN role  

`id` - user id.  

* **Success (200):**
```json
{
  "id": "<user_id_uuid>",
  "firstName": "<user_first_name>",
  "lastName": "<user_last_name>",
  "birthDate": "<user_birth_date> in yyyy-MM-dd format",
  "email": "<user_email>",
  "active": "<true_or_false_user_status>"
}
```

---

### 4. Get user info list by first and last name.

GET /users?firstName=<first_user_name>&lastName=<last_user_name>
`Authorization: Bearer <your_access_token>` with ADMIN role

`firstName` - first username prefix. Not mandatory.  
`laststName` - last username prefix. Not mandatory.  

* **Success (200):**
```json
{
  "content": [
    {
      "id": "<user_id_uuid>",
      "firstName": "<user_first_name>",
      "lastName": "<user_last_name>",
      "birthDate": "<user_birth_date> in yyyy-MM-dd format",
      "email": "<user_email>",
      "active": "<true_or_false_user_status>"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": { "empty": false, "sorted": true, "unsorted": false },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalPages": 1,
  "totalElements": 1,
  "size": 20,
  "number": 0,
  "sort": { "empty": false, "sorted": true, "unsorted": false },
  "first": true,
  "numberOfElements": 1,
  "empty": false
}
```

---

### 5. Update new user.

PUT /users/{id}  
`Authorization: Bearer <your_access_token>` with ADMIN role  
`Content-Type: application/json`  

`id` - user id.  

```json
{ 
  "firstName": "<user_first_name>", 
  "lastName": "<user_last_name>",
  "birthDate": "<user_birth_date> in yyyy-MM-dd format",
  "email": "<user_email>",
  "active": "<true_or_false_user_status>"
}
```

* **Success (200):**
```json
{
  "id": "<user_id_uuid>",
  "firstName": "<user_first_name>",
  "lastName": "<user_last_name>",
  "birthDate": "<user_birth_date> in yyyy-MM-dd format",
  "email": "<user_email>",
  "active": "<true_or_false_user_status>"
}
```

---

### 6. Update user active status.

PATCH /users/{id}?active=<true_or_false_user_status>  
`Authorization: Bearer <your_access_token>` with ADMIN role  

`active` - user active status to change.  

* **Success (200):**

---

## Payment card REST endpoints

### 1. Create new payment cart.

POST /payment-cards/users/{userId}  
`Authorization: Bearer <your_access_token>` with ADMIN role  
`Content-Type: application/json`  

```json
{ 
  "number": "<payment_card_number>", 
  "holder": "<user_holder>",
  "expirationDate": "<payment_card_expiration_date> in yyyy-MM-dd format",
  "active": "<true_or_false_payment_card_status>"
}
```

* **Success (201 Created):**
```json
{
  "id": "<payment_card_id_uuid>",
  "number": "<payment_card_number>",
  "holder": "<user_holder>",
  "expirationDate": "<payment_card_expiration_date> in yyyy-MM-dd format",
  "active": "<true_or_false_payment_card_status>"
}
```

---

### 2. Get payment cards by user id.

GET /payment-cards/users/{userId}?active=<true_or_false_user_status>  
`Authorization: Bearer <your_access_token>` with ADMIN role  

`active` - not mandatory payment card status. If absent it returns payment cards with any status

* **Success (200):**
```json
[
  {
    "id": "<payment_card_id_uuid>",
    "number": "<payment_card_number>",
    "holder": "<user_holder>",
    "expirationDate": "<payment_card_expiration_date> in yyyy-MM-dd format",
    "active": "<true_or_false_payment_card_status>"
  }
]
```

---

### 3. Get current user payment cards.

GET /payment-cards/users/me  
`Authorization: Bearer <your_access_token>`  

* **Success (200):**
```json
[
  {
    "id": "<payment_card_id_uuid>",
    "number": "<payment_card_number>",
    "holder": "<user_holder>",
    "expirationDate": "<payment_card_expiration_date> in yyyy-MM-dd format",
    "active": "<true_or_false_payment_card_status>"
  }
]  
```

---

### 4. Get payment card by id.

GET /payment-cards/{id}  
`Authorization: Bearer <your_access_token>`  

`id` - payment card id

* **Success (200):**
```json
[
  {
    "id": "<payment_card_id_uuid>",
    "number": "<payment_card_number>",
    "holder": "<user_holder>",
    "expirationDate": "<payment_card_expiration_date> in yyyy-MM-dd format",
    "active": "<true_or_false_payment_card_status>"
  }
]  
```

---

### 5. Get payment cards by user first and last name.

GET /payment-cards?firstName=<first_user_name>&lastName=<last_user_name>  
`Authorization: Bearer <your_access_token>` with ADMIN role  

`firstName` - first username prefix. Not mandatory.  
`laststName` - last username prefix. Not mandatory.  

* **Success (200):**
```json
{
  "content": [
    {
      "id": "<payment_card_id_uuid>",
      "number": "<payment_card_number>",
      "holder": "<user_holder>",
      "expirationDate": "<payment_card_expiration_date> in yyyy-MM-dd format",
      "active": "<true_or_false_payment_card_status>"
    }
  ],
    "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": { "empty": false, "sorted": true, "unsorted": false },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalPages": 1,
  "totalElements": 1,
  "size": 20,
  "number": 0,
  "sort": { "empty": false, "sorted": true, "unsorted": false },
  "first": true,
  "numberOfElements": 1,
  "empty": false
}
```

---

### 5. Update payment card.

PUT /payment-cards/{id}/users/{userId}  
`Authorization: Bearer <your_access_token>` with ADMIN role  
`Content-Type: application/json`  

`id` - payment card id.  

```json
{
  "number": "<payment_card_number>",
  "holder": "<user_holder>",
  "expirationDate": "<payment_card_expiration_date> in yyyy-MM-dd format",
  "active": "<true_or_false_payment_card_status>"
}
```

* **Success (200):**
```json
{
  "id": "<payment_card_id_uuid>",
  "number": "<payment_card_number>",
  "holder": "<user_holder>",
  "expirationDate": "<payment_card_expiration_date> in yyyy-MM-dd format",
  "active": "<true_or_false_payment_card_status>"
}
```

---

### 6. Update payment card active status.

PATCH /payment-cards/{id}?active=<true_or_false_user_status>  
`Authorization: Bearer <your_access_token>` with ADMIN role  

`active` - user active status to change.

* **Success (200):**

---
