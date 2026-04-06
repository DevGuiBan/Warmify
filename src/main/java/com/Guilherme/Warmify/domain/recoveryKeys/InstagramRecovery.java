package com.Guilherme.Warmify.domain.recoveryKeys;

import com.Guilherme.Warmify.domain.instagram.Instagram;
import com.Guilherme.Warmify.utils.RecoveryKeyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "recovery_keys_ig")
@Table(name = "recovery_keys_ig")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InstagramRecovery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "recovery_key")
    private String recoveryKey;

    @Enumerated(EnumType.STRING)
    private RecoveryKeyStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instagram_account_id", nullable = false)
    private Instagram instagramAccount;

    public InstagramRecovery(String recoveryKey, RecoveryKeyStatus status, Instagram instagramAccount) {
        this.recoveryKey = recoveryKey;
        this.status = status;
        this.instagramAccount = instagramAccount;
    }
}
