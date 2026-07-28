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


## Tables

### Table `users`

| Column       | Type          | Constraints             |
|--------------|---------------|-------------------------| 
| `id`         | BIGSERIAL(20) | NOT NULL AUTO_INCREMENT |
| `name`       | VARCHAR(50)   | NOT NULL                |
| `surname`    | VARCHAR(50)   | NOT NULL                |
| `birth_date` | DATE          | NOT NULL                |
| `email`      | VARCHAR(100)  | NOT NULL                |
| `active`     | BOOLEAN       | NOT NULL                |
| `created_at` | TIMESTAMP     | NOT NULL                |
| `updated_at` | TIMESTAMP     | NOT NULL                |


### Table `payment_cards`

| Column            | Type          | Constraints             |
|-------------------|---------------|-------------------------| 
| `id`              | BIGSERIAL(20) | NOT NULL AUTO_INCREMENT |
| `user_id`         | BIGINT        | NOT NULL                |
| `number`          | VARCHAR(255)  | NOT NULL                |
| `expiration_date` | DATE          | NOT NULL                |
| `active`          | BOOLEAN       | NOT NULL                |
| `created_at`      | TIMESTAMP     | NOT NULL                |
| `updated_at`      | TIMESTAMP     | NOT NULL                |

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
      - DB_USERNAME=${DB_USERNAME}
      - DB_PASSWORD=${DB_PASSWORD}
      - REDIS_SECRET_PASSWORD=${REDIS_SECRET_PASSWORD}
      - CARD_SECRET_KEY=${CARD_SECRET_KEY}

volumes:
  postgres_data:
  redis_data:
```

Don't forget create .env file in the same place

DB_NAME=user_db  
DB_USERNAME=db_username  
DB_PASSWORD=db_password

CARD_SECRET_KEY=32encryption_symbolds_secret_key
REDIS_SECRET_PASSWORD=redis_password
