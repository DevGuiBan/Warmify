package com.Guilherme.Warmify.domain.businessportfolio;

import com.Guilherme.Warmify.domain.domain.Domain;
import com.Guilherme.Warmify.domain.facebook.Facebook;
import com.Guilherme.Warmify.domain.instagram.Instagram;
import com.Guilherme.Warmify.utils.StatusBM;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "business_portfolio")
@Table(name = "business_portfolio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessPortfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "bm_name")
    private String bmName;

    @Column(name = "cnjp")
    private String cnpj;

    @Column(name = "cnpj_pdf")
    private String cnpjPdf;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "facebook_pages", columnDefinition = "jsonb", nullable = false)
    private List<UUID> facebookPages = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private StatusBM status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dom_id")
    private Domain domain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facebook_account_id", nullable = false)
    private Facebook facebookAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instagram_account_id")
    private Instagram instagramAccount;

    public BusinessPortfolio(String bmName, String cnpj, String cnpjPdf, StatusBM status, Domain domain, Facebook facebookAccount, Instagram instagramAccount) {
        this.bmName = bmName;
        this.cnpj = cnpj;
        this.cnpjPdf = cnpjPdf;
        this.status = status;
        this.domain = domain;
        this.facebookAccount = facebookAccount;
        this.instagramAccount = instagramAccount;
    }
}

