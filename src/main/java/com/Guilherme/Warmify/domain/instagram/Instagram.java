package com.Guilherme.Warmify.domain.instagram;

import com.Guilherme.Warmify.utils.StatusAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "instagram")
@Table(name = "instagram")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Instagram {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String username;

    private String email;

    private String password;

    @Column(name = "google_authenticator_email")
    private String googleAuthenticatorEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_account")
    private StatusAccount statusAccount;

    public Instagram(String username, String email, String password, String googleAuthenticatorEmail, StatusAccount statusAccount) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.googleAuthenticatorEmail = googleAuthenticatorEmail;
        this.statusAccount = statusAccount;
    }

}
