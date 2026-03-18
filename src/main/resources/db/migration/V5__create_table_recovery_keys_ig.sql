CREATE TABLE recovery_keys_ig(
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    instagram_account_id UUID NOT NULL,
    recovery_key VARCHAR(50) NOT NULL,

    CONSTRAINT fk_instagram_account FOREIGN KEY (instagram_account_id) REFERENCES instagram(id),

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);