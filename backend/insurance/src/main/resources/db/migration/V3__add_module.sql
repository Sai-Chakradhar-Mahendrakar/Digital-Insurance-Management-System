CREATE TABLE public.claim (
    id BIGSERIAL PRIMARY KEY,
    user_policy_id BIGINT NOT NULL,
    claim_date DATE NOT NULL,
    claim_amount NUMERIC(10,2) NOT NULL,
    reason VARCHAR(500) NOT NULL,
    status VARCHAR(255) NOT NULL,
    reviewer_comment VARCHAR(1000),
    resolved_date DATE,
    CONSTRAINT fk_claim_userpolicy FOREIGN KEY (user_policy_id) REFERENCES public.user_policy(id)
);
