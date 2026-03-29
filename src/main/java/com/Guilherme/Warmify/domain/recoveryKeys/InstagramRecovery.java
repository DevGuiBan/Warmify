package com.Guilherme.Warmify.domain.recoveryKeys;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "recovery_keys_ig")
@Table(name = "recovery_keys_ig")
@Getter
@Setter
public class InstagramRecovery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


}
