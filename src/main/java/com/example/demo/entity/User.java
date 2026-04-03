package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;

    // WRITE_ONLY: Cho phép nhận password từ request (login/register)
    // nhưng KHÔNG trả password ra response (bảo mật)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String role;
}

