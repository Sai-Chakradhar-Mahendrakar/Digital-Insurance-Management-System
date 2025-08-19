CREATE TABLE public.support_ticket (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    policy_id BIGINT,
    claim_id BIGINT,
    subject VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(8) NOT NULL CHECK (status IN ('OPEN', 'RESOLVED')),
    response TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (policy_id) REFERENCES policy(id),
    FOREIGN KEY (claim_id) REFERENCES claim(id)
);