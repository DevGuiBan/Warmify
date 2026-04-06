package com.Guilherme.Warmify.domain.recoveryKeys;

import com.Guilherme.Warmify.domain.facebook.Facebook;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "recovery_keys_fb")
@Table(name = "recovery_keys_fb")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacebookRecovery {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "recovery_key")
	private String recoveryKey;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "facebook_account_id", nullable = false)
	private Facebook facebookAccount;

	public FacebookRecovery(String recoveryKey, Facebook facebookAccount) {
		this.recoveryKey = recoveryKey;
		this.facebookAccount = facebookAccount;
	}
}
