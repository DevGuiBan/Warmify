package com.Guilherme.Warmify.domain.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "domains")
@Table(name = "domains")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Domain {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NonNull
    @Column(name = "dom_url")
    private String domUrl;

    public Domain(String domUrl) {
        this.domUrl = domUrl;
    }
}
