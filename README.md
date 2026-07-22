# Core User microservice

## About The Project

The microservice application processes operations with users and user payment cards

## Environment variables

| Variable                | Type    | Desription                                                         |
|-------------------------|---------|--------------------------------------------------------------------| 
| `DB_NAME`               | String  | Database schema name                                               |
| `DB_USERNAME`           | String  | Database user name                                                 |
| `DB_PASSWORD`           | String  | Database user password                                             |
| `SHOW_SQL`              | Boolean | Not mandatory parameter to allow show sql queries in debug only    |
| `CARD_SECRET_KEY`       | Boolean | 32 symblos secret password to encrypt/dercypt payment card numbers |
| `REDIS_SECRET_PASSWORD` | String  | Redis password                                                     |

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

DB_NAME=user_db  
DB_USERNAME=Username  
DB_PASSWORD=Password

SHOW_SQL=true

CARD_SECRET_KEY=gsvhnjkblunbgfjvbcgvnhrfxcvmbjhn  
REDIS_SECRET_PASSWORD=redis_password

## Application docker-compose example

```yaml
version: '3.8'
services:
  core-user-service:
    image: ghcr.io/yuryshakhau1/core-user-service:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DB_NAME=user_db
      - DB_USERNAME=user_name
      - DB_PASSWORD=user_password
      - REDIS_SECRET_PASSWORD=redis_password
      - CARD_SECRET_KEY=32symbols_payment_card_secretkey
```
