CREATE TABLE claim (
    id BIGSERIAL PRIMARY KEY,
    user_policy_id BIGINT NOT NULL,
    claim_date DATE NOT NULL,
    claim_amount NUMERIC(10,2) NOT NULL,
    reason VARCHAR(500) NOT NULL,
    status VARCHAR(50) NOT NULL,
    reviewer_comment VARCHAR(1000),
    resolved_date DATE,
    CONSTRAINT fk_user_policy
        FOREIGN KEY (user_policy_id)
        REFERENCES user_policy(id)
);