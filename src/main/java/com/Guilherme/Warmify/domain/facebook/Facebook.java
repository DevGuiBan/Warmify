package com.Guilherme.Warmify.domain.facebook;

import com.Guilherme.Warmify.utils.StatusAccount;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "facebook")
@Table(name = "facebook")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Facebook {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "profile_name")
    private String profileName;

    private String email;

    private String password;

    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_account")
    private StatusAccount statusAccount;

    public Facebook (String profileName, String email, String password, String url, StatusAccount statusAccount) {
        this.profileName = profileName;
        this.email = email;
        this.password = password;
        this.url = url;
        this.statusAccount = statusAccount;
    }
}
