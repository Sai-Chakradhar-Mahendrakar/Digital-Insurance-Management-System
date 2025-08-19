CREATE TABLE public.user_policy (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    policy_id BIGINT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE', 'EXPIRED', 'CANCELLED')),
    premium_paid DECIMAL(10,2),
    CONSTRAINT fk_user_policy_user FOREIGN KEY (user_id) REFERENCES public.users(id),
    CONSTRAINT fk_user_policy_policy FOREIGN KEY (policy_id) REFERENCES public.policy(id)
);
