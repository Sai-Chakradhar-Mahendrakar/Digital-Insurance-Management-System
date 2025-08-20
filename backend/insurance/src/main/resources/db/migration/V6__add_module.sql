CREATE TABLE notification (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    message VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(8) NOT NULL CHECK (status IN ('UNREAD', 'READ')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES "user"(id)
);

