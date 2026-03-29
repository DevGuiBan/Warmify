CREATE TABLE number_portfolio(
    id UUID PRIMARY KEY UNIQUE DEFAULT GEN_RANDOM_UUID(),
    name VARCHAR(255) NOT NULL,
    number VARCHAR(11) NOT NULL,
    status VARCHAR(50) NOT NULL,

    business_portfolio_id UUID NOT NULL,

    CONSTRAINT fk_business_portfolio FOREIGN KEY (business_portfolio_id) REFERENCES business_portfolio(id),

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);