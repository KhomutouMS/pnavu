package com.example.fakemaleru.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;

    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL, CascadeType.MERGE},
            fetch = FetchType.EAGER)

    private List<Question> questions;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL, CascadeType.MERGE},
            fetch = FetchType.LAZY)

    private List<Answer> answers;
}
