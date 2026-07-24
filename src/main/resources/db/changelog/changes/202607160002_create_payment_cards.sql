CREATE TABLE payment_cards
(
    id              UUID PRIMARY KEY,
    user_id         BIGINT    NOT NULL,
    number          VARCHAR(255),
    holder          VARCHAR(100),
    expiration_date DATE      NOT NULL,
    active          BOOLEAN   NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL,

    CONSTRAINT fk_payment_cards_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_payment_cards_user_id ON payment_cards(user_id);
