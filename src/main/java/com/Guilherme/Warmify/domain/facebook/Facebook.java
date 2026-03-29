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

    @NonNull
    @Column(name = "profile_name")
    private String profileName;

    @NonNull
    private String email;

    @NonNull
    private String password;

    @NonNull
    private String url;

    @NonNull
    @Enumerated(EnumType.STRING)
    private StatusAccount statusAccount;


}
