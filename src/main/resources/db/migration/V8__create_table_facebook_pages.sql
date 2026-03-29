CREATE TABLE pgfacebook(
    id UUID PRIMARY KEY UNIQUE DEFAULT GEN_RANDOM_UUID(),
    page_name VARCHAR(255) NOT NULL,

    facebook_account_id UUID NOT NULL,
    business_portfolio_id UUID NOT NULL,

    CONSTRAINT fk_facebook_account FOREIGN KEY (facebook_account_id) REFERENCES facebook(id),
    CONSTRAINT fk_business_portfolio FOREIGN KEY (business_portfolio_id) REFERENCES business_portfolio(id),

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);