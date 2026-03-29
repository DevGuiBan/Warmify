CREATE TABLE business_portfolio(
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    bm_name VARCHAR(255) NOT NULL,
    cnjp VARCHAR(255) NOT NULL,
    cnpj_pdf VARCHAR(255),
    status VARCHAR(50) NOT NULL,

    dom_id UUID,
    facebook_account_id UUID NOT NULL,
    instagram_account_id UUID,

    CONSTRAINT fk_dom FOREIGN KEY (dom_id) REFERENCES domains(id),
    CONSTRAINT fk_facebook_account FOREIGN KEY (facebook_account_id) REFERENCES facebook(id),
    CONSTRAINT fk_instagram_account FOREIGN KEY (instagram_account_id) REFERENCES instagram(id),

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);