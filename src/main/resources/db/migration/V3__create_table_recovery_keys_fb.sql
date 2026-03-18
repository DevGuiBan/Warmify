CREATE TABLE recovery_keys_fb (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    facebook_account_id UUID NOT NULL,
    recovery_key VARCHAR(255) NOT NULL,

    CONSTRAINT fk_facebook_account FOREIGN KEY (facebook_account_id) REFERENCES facebook(id),

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);