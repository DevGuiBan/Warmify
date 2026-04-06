package com.Guilherme.Warmify.domain.numberportfolio;

import com.Guilherme.Warmify.domain.businessportfolio.BusinessPortfolio;
import com.Guilherme.Warmify.utils.StatusAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "number_portfolio")
@Table(name = "number_portfolio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NumberPortfolio {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	private String name;

	private String number;

	@Enumerated(EnumType.STRING)
	private StatusAccount status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "business_portfolio_id", nullable = false)
	private BusinessPortfolio businessPortfolio;

	public NumberPortfolio(String name, String number, StatusAccount status, BusinessPortfolio businessPortfolio) {
		this.name = name;
		this.number = number;
		this.status = status;
		this.businessPortfolio = businessPortfolio;
	}
}

