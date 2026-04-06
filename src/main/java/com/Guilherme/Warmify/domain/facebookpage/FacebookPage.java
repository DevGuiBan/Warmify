package com.Guilherme.Warmify.domain.facebookpage;

import com.Guilherme.Warmify.domain.businessportfolio.BusinessPortfolio;
import com.Guilherme.Warmify.domain.facebook.Facebook;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "pgfacebook")
@Table(name = "pgfacebook")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacebookPage {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "page_name")
	private String pageName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "facebook_account_id", nullable = false)
	private Facebook facebookAccount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "business_portfolio_id", nullable = false)
	private BusinessPortfolio businessPortfolio;

	public FacebookPage(String pageName, Facebook facebookAccount, BusinessPortfolio businessPortfolio) {
		this.pageName = pageName;
		this.facebookAccount = facebookAccount;
		this.businessPortfolio = businessPortfolio;
	}
}

