package com.example.fakemaleru.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder


public class User {
    private String username;
    private String password;
    private String email;
}
