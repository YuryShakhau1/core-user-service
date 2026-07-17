CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(50)  NOT NULL,
    surname    VARCHAR(50)  NOT NULL,
    birth_date DATE         NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    active     BOOLEAN      NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);

CREATE INDEX idx_users_name ON users(name);
CREATE INDEX idx_users_surname ON users(surname);
